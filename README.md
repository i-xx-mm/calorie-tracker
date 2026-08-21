# Calorie Tracker: Full\-Stack Web Application

# 🎯 Calorie‑Tracker

A modern full‑stack web application for tracking daily calorie intake, including user authentication, BMI calculation, and monthly intake analytics\.


---

## 📋 Table of Contents

1. \[Technology Stack\]\(\#technology\-stack\)

2. \[Prerequisites\]\(\#prerequisites\)

3. \[Project Structure\]\(\#project\-structure\)

4. \[Quick Start\]\(\#quick\-start\)

5. \[Detailed Setup\]\(\#detailed\-setup\)

6. \[API Documentation\]\(\#api\-documentation\)

7. \[Features \& Usage\]\(\#features\-\-usage\)

8. \[Architecture Overview\]\(\#architecture\-overview\)

9. \[Troubleshooting\]\(\#troubleshooting\)

10. \[Common Commands Reference\]\(\#commoncommands\)

10. \[Future Improvements\]\(\#futureimprovements\)

---

## 🛠 Technology Stack

### Frontend

- **Angular 18**

- **TypeScript 5\.5**

- **RxJS 7\.8**

- **HTML5 / CSS3**

### Backend

- **Java 17\+**

- **Spring Boot 3\.x**

- **Spring Security \+ JWT**

- **Spring Data MongoDB**

- **Maven 3\.8\+**

### Database

- **MongoDB Community Server \(Local\)**

---

## 📦 Prerequisites

### Required Software

```bash
# Java 17+
java -version

# Node.js 18+, npm 9+
node --version
npm --version

# MongoDB Community (local instance)
# macOS: brew install mongodb‑community
# Windows: install MongoDB Community locally

# Maven 3.8+
mvn --version

# Angular CLI 18+
npm install -g @angular/cli@18
ng version
```

---

## 📁 Project Structure

> Repository root name: **`calorie‑tracker`**
> 
> 

```Plain Text
calorie-tracker/
├── backend/                     # Spring Boot Java backend
│   └── src/main/java/com/calorie/
│       ├── config/              # SecurityConfig etc.
│       ├── controller/         # REST Controllers
│       ├── dto/                 # Request / Response DTOs
│       ├── exception/           # Global exception handling
│       ├── model/               # MongoDB entity models
│       ├── repository/           # Spring Data repositories
│       ├── security/            # JWT filter & provider
│       ├── service/             # Business logic services
│       └── util/                # Helper utilities
│   ├── src/main/resources/
│   └── pom.xml

└── frontend/
    └── calorie‑tracker/        # Angular frontend project
        └── src/app/
            ├── pages/
            │   ├── auth/           # login / register components
            │   ├── dashboard/
            │   ├── food‑log/
            │   └── profile/
            ├── shared/
            │   ├── components/     # modal / header / notification
            │   ├── guards/         # AuthGuard
            │   ├── interceptors/   # JWT & Error interceptor
            │   ├── models/
            │   ├── pipes/
            │   └── services/
            ├── app‑routing.module.ts
            └── app.module.ts
```

---

## 🚀 Quick‑Start

### 1\. Start Local MongoDB Community

```bash
# macOS
brew services start mongodb‑community

# Verify MongoDB is running locally on 27017
mongosh --eval "db.adminCommand('ping')"
```

### 2\. Start Backend \(Terminal 1\)

```bash
# from repo root
cd calorie‑tracker/backend

# build & run spring boot
mvn spring‑boot:run
```

> Backend runs on `http://localhost:8080/api`
> 
> 

### 3\. Start Frontend \(Terminal 2\)

```bash
# from repo root
cd calorie‑tracker/frontend/calorie‑tracker

# install dependencies (first‑time only)
npm install

# start angular dev server
npm start
```

> Frontend available at `http://localhost:4200`
> 
> 

---

## 📖 Detailed Setup

### Database Setup \(Local MongoDB Community\)

Local connection string used in backend `application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/calorie_tracker
```

```bash
# stop mongodb when finished
brew services stop mongodb‑community
```

### Backend Setup \(Spring Boot\)

```bash
cd calorie‑tracker/backend

# install maven dependencies
mvn clean install
```

`backend/src/main/resources/application.yml`

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/calorie_tracker
  security:
    jwt:
      secret: ${JWT_SECRET:your‑secret‑key‑at‑least‑32‑chars‑long}
      expiration: ${JWT_EXPIRATION:86400000} # 24h ms

server:
  port: 8080
  servlet:
    context‑path: /api
```

Run backend:

```bash
cd calorie‑tracker/backend
mvn spring‑boot:run
```

### Frontend Setup \(Angular\)

```bash
cd calorie‑tracker/frontend/calorie‑tracker

npm install
```

`src/environments/environment.ts`

```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api',
  jwtTokenKey: 'calorie_jwt_token'
};
```

Run dev server:

```bash
npm start
# open http://localhost:4200
```

---

## 🔗 API Documentation

### Authentication

```bash
# Register
POST /api/auth/register
# { "username": "test", "password": "test1234", "height": 160, "weight": 52, "age": 26, "gender": "female" }

# Login
POST /api/auth/login
# { "username":"test", "password":"test1234" }

# Logout
POST /api/auth/logout
```

### User Profile

```bash
GET  /api/users/me
GET  /api/users/{username}
PUT  /api/users/{username}
GET  /api/users/{username}/bmi
```

### Food Library API

```bash
# Search foods (fuzzy search + result limit)
# Query Params: search (string), limit (number, default=10)
GET /api/foods?search=chicken&limit=10

# Get single food by ID
GET /api/foods/{id}

# Create custom food (name + calorie only)
POST /api/foods
{
  "name": "salmon",
  "calorie": 280
}

# Update existing food
PUT /api/foods/{id}
{
  "name": "salmon",
  "calorie": 285
}

# Delete food
DELETE /api/foods/{id}
```

### Food Log API

```bash
# Get daily food log (date optional, defaults to today EST)
GET /api/foodlogs?date=2026-08-21

# Add new food entry to daily log
POST /api/foodlogs
{
  "foodName": "salmon",
  "calorie": 280,
  "note": "steamed salmon",
  "date": "2026-08-21"
}

# Update specific food entry in log (index = array item index)
PUT /api/foodlogs/{foodLogId}?index=0
{
  "foodName": "salmon",
  "calorie": 285,
  "note": "seasoned salmon",
  "date": "2026-08-21"
}

# Delete specific food entry in log
DELETE /api/foodlogs/{foodLogId}?index=0
```

### Dashboard API

```bash
# Get today's calorie & BMI stats
GET /api/dashboard/today

# Get monthly calorie analytics
GET /api/dashboard/monthly-stats?month=8
```

#### API Key Notes

- All food \& log endpoints require JWT authentication

- Backend uses **EST timezone** for all date calculations

- Food library entries do not support notes; only food log entries support notes

- Food log CRUD operates via `foodLogId + array index` for precise item control

---

## ✨ Features \& Usage

- **JWT User Authentication**: Secure register, login, and session persistence

- **BMI \& TDEE Calculation**: Personal daily calorie goal based on user physical data

- **Daily Food Logging**: Add, edit, delete, and search food records

- **Analytics Dashboard**: Daily summary \+ monthly calorie trend statistics

- **User Profile Management**: Update personal physical metrics dynamically

### Calorie Calculation Formula

Mifflin\-St Jeor Equation for BMR \& TDEE:

```plaintext
Male:   BMR = 10×weight + 6.25×height − 5×age + 5
Female: BMR = 10×weight + 6.25×height − 5×age − 161
TDEE = BMR × 1.55 (moderate activity level)
```

---

## 🏗️ Architecture Overview

```Plain Text
Angular Frontend (localhost:4200)
        ↓ HTTP Requests
Spring Boot Backend (localhost:8080/api)
        ↓ Spring Data MongoDB
Local MongoDB Community (port 27017)
```

### Authentication Flow

1. User registers or logs in with valid credentials

2. Backend validates user and returns signed JWT token

3. Frontend stores token locally

4. Angular JWT interceptor injects token into every secured request

5. Spring Security filter chain validates token and processes requests

---

## 🔧 Troubleshooting

#### Port 8080 Already in Use

```bash
lsof -i :8080
kill -9 <PID>
```

#### MongoDB Connection Refused

```bash
brew services start mongodb-community
mongosh --eval "db.adminCommand('ping')"
```

#### Maven Build Failure

```bash
cd calorie-tracker/backend
mvn clean install
```

#### Frontend API Connection Error

- Confirm backend running on port 8080

- Verify `apiBaseUrl` in `environment.ts`

- Check backend CORS configuration

#### Port 4200 Already in Use

```bash
cd calorie-tracker/frontend/calorie-tracker
ng serve --port 4201
```

---

## 📝 Common Commands Reference

```bash
# Start local MongoDB
brew services start mongodb‑community

# Backend
cd calorie‑tracker/backend
mvn spring‑boot:run
mvn clean install
mvn test

# Frontend
cd calorie‑tracker/frontend/calorie‑tracker
npm start
ng lint
```

---

## 📋 Future Improvements / Todo
### Backend
- Add more unit test coverage for controller authenticated endpoints
- Expand integration tests for MongoDB repository layer

### Frontend
- Add more unit tests for Angular components & services
- Add e2e end‑to‑end test

### Demo & Documentation
- Add project demo screenshots / GIF of core workflow