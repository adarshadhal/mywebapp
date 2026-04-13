package com.webapp.model;

import java.time.LocalDateTime;

public class DataRecord {

    private String id;
    private String title;
    private String category;
    private String description;
    private String status;
    private String createdBy;          // ← username who created this record
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── No-arg constructor required by Jackson (S3 deserialization) ──────────
    public DataRecord() {}

    // ── Constructor used in DashboardController ───────────────────────────────
    // new DataRecord(id, title, category, description, status, auth.getName())
    public DataRecord(String id, String title, String category,
                      String description, String status, String createdBy) {
        this.id          = id;
        this.title       = title;
        this.category    = category;
        this.description = description;
        this.status      = status;
        this.createdBy   = createdBy;
        this.createdAt   = LocalDateTime.now();
        this.updatedAt   = LocalDateTime.now();
    }

    public String getId()                          { return id; }
    public void   setId(String id)                 { this.id = id; }

    public String getTitle()                       { return title; }
    public void   setTitle(String title)           { this.title = title; }

    public String getCategory()                    { return category; }
    public void   setCategory(String category)     { this.category = category; }

    public String getDescription()                 { return description; }
    public void   setDescription(String desc)      { this.description = desc; }

    public String getStatus()                      { return status; }
    public void   setStatus(String status)         { this.status = status; }

    public String getCreatedBy()                   { return createdBy; }
    public void   setCreatedBy(String createdBy)   { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt()                        { return createdAt; }
    public void          setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                        { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
