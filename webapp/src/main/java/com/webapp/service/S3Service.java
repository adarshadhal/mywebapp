package com.webapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webapp.model.DataRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private static final String RECORDS_KEY = "data-records/records.json";

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private S3Client s3Client;
    private final ObjectMapper objectMapper;

    public S3Service() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(
            com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @PostConstruct
    public void initS3Client() {
        try {
            // No credentials needed — uses EC2 IAM Role automatically
            this.s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .build();
            logger.info("S3 client initialized via IAM Role. Region: {}, Bucket: {}", region, bucketName);
            ensureBucketExists();
        } catch (Exception e) {
            logger.error("Failed to initialize S3 client: {}", e.getMessage(), e);
        }
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            logger.info("S3 bucket '{}' confirmed.", bucketName);
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            logger.info("S3 bucket '{}' created.", bucketName);
        } catch (Exception e) {
            logger.error("Bucket check failed: {}", e.getMessage());
        }
    }

    public DataRecord saveRecord(DataRecord record) {
        List<DataRecord> records = getAllRecords();
        records.add(record);
        persistRecords(records);
        logger.info("Record saved: {}", record.getId());
        return record;
    }

    public List<DataRecord> getAllRecords() {
        if (s3Client == null) {
            logger.error("S3 client not initialized!");
            return new ArrayList<>();
        }
        try {
            byte[] content = s3Client.getObjectAsBytes(
                GetObjectRequest.builder().bucket(bucketName).key(RECORDS_KEY).build()
            ).asByteArray();
            return objectMapper.readValue(
                new String(content, StandardCharsets.UTF_8),
                new TypeReference<List<DataRecord>>() {}
            );
        } catch (NoSuchKeyException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            logger.error("Error reading records: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<DataRecord> getRecordsByUser(String username) {
        return getAllRecords().stream()
                .filter(r -> username.equals(r.getCreatedBy()))
                .toList();
    }

    public DataRecord updateRecord(String id, DataRecord updated) {
        List<DataRecord> records = getAllRecords();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(id)) {
                updated.setId(id);
                updated.setCreatedAt(records.get(i).getCreatedAt());
                updated.setUpdatedAt(java.time.LocalDateTime.now());
                records.set(i, updated);
                persistRecords(records);
                return updated;
            }
        }
        throw new RuntimeException("Record not found: " + id);
    }

    public void deleteRecord(String id) {
        List<DataRecord> records = getAllRecords();
        boolean removed = records.removeIf(r -> r.getId().equals(id));
        if (removed) {
            persistRecords(records);
        } else {
            throw new RuntimeException("Record not found: " + id);
        }
    }

    private void persistRecords(List<DataRecord> records) {
        try {
            String json = objectMapper.writeValueAsString(records);
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName).key(RECORDS_KEY)
                    .contentType("application/json").build(),
                RequestBody.fromString(json)
            );
            logger.info("Records persisted. Total: {}", records.size());
        } catch (Exception e) {
            logger.error("Error saving to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save records to S3", e);
        }
    }

    public long getRecordCount() {
        return getAllRecords().size();
    }
}
