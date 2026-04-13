# 📦 DataVault — Java Spring Boot Web Application

A full-stack Java web application with user authentication, dashboard, data record management, and **AWS S3 storage**.

---

## 🗂️ Project Structure

```
data-record-app/
├── pom.xml
└── src/main/
    ├── java/com/webapp/
    │   ├── DataRecordApplication.java        ← Main entry point
    │   ├── config/
    │   │   └── SecurityConfig.java           ← Spring Security setup
    │   ├── controller/
    │   │   ├── AuthController.java           ← Login / Register routes
    │   │   └── DashboardController.java      ← Dashboard, Records routes
    │   ├── model/
    │   │   ├── User.java                     ← User JPA entity
    │   │   ├── UserRepository.java           ← User JPA repo
    │   │   └── DataRecord.java               ← Record POJO (stored in S3)
    │   └── service/
    │       ├── UserService.java              ← User registration & auth
    │       └── S3Service.java                ← AWS S3 read/write
    └── resources/
        ├── application.properties            ← Config (set AWS keys here!)
        └── templates/
            ├── login.html                    ← Login page
            ├── register.html                 ← Registration page
            ├── dashboard.html                ← Main dashboard
            ├── create-record.html            ← New record form
            └── records.html                  ← All records table
```

---

## ⚙️ Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |
| AWS Account | — |

---

## 🚀 Setup & Run

### Step 1 — Clone / Extract the project
```bash
cd data-record-app
```

### Step 2 — Configure AWS S3

Edit `src/main/resources/application.properties`:

```properties
aws.accessKeyId=YOUR_AWS_ACCESS_KEY_ID
aws.secretKey=YOUR_AWS_SECRET_ACCESS_KEY
aws.region=us-east-1
aws.s3.bucketName=your-datavault-bucket
```

> ⚠️ **Never commit AWS credentials to Git.** Use environment variables in production:
> ```bash
> export AWS_ACCESS_KEY_ID=...
> export AWS_SECRET_ACCESS_KEY=...
> ```

### Step 3 — AWS IAM Permissions Required

Your AWS IAM user/role needs these S3 permissions:
```json
{
  "Effect": "Allow",
  "Action": [
    "s3:GetObject",
    "s3:PutObject",
    "s3:DeleteObject",
    "s3:ListBucket",
    "s3:CreateBucket",
    "s3:HeadBucket"
  ],
  "Resource": [
    "arn:aws:s3:::your-datavault-bucket",
    "arn:aws:s3:::your-datavault-bucket/*"
  ]
}
```

### Step 4 — Build & Run

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# OR run the JAR directly:
java -jar target/data-record-app-1.0.0.jar
```

### Step 5 — Open in Browser

```
http://localhost:8080
```

---

## 🧭 Application Flow

```
/ (root)
  └──→ /login             ← Login with username + password
         └──→ /register   ← New user registration
         └──→ /dashboard  ← (after login) Main dashboard with stats
               ├── /records/new     ← Create a new record
               ├── /records         ← View all records from S3
               └── /api/records     ← JSON REST endpoint
```

---

## 🔐 Security Features

- **BCrypt** password hashing
- **Spring Security** session management
- **CSRF** protection (disabled for API — enable in production)
- **Route protection** — unauthenticated users redirect to `/login`

---

## ☁️ How AWS S3 Storage Works

All data records are stored as a **single JSON file** in S3:

```
s3://your-bucket/data-records/records.json
```

Each record has this structure:
```json
{
  "id": "uuid-string",
  "title": "Q1 Report",
  "category": "Finance",
  "description": "...",
  "status": "Active",
  "createdBy": "username",
  "createdAt": "2025-01-01 10:00:00",
  "updatedAt": "2025-01-01 10:00:00"
}
```

---

## 🌐 REST API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/records` | Get all records as JSON |
| POST | `/api/records` | Create a record via JSON |
| GET | `/api/health` | Health check |

Example API call:
```bash
curl -u username:password http://localhost:8080/api/records
```

---

## 🗃️ User Database

Users are stored in an **H2 in-memory database** (resets on restart).

To use a persistent database, replace H2 with MySQL/PostgreSQL in `pom.xml` and `application.properties`:

```properties
# MySQL example
spring.datasource.url=jdbc:mysql://localhost:3306/datavault
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2 |
| Security | Spring Security 6 |
| Database | H2 (in-memory) / JPA |
| Templates | Thymeleaf |
| Cloud | AWS SDK v2 — S3 |
| Build | Maven |
| UI | Custom CSS, Google Fonts |

---

## 📜 License

MIT — Free to use and modify.
