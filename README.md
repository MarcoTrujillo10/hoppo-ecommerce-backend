# HOPPO Backend

Backend service for **HOPPO**, a full-stack e-commerce platform developed as part of a software engineering project.

The backend is built using **Java and Spring Boot**, exposing REST APIs that handle the core business logic of the platform.

---

## Features

- Product management
- User authentication and authorization
- Shopping cart functionality
- Order management
- Payment processing
- REST API architecture

---

## Tech Stack

- Java
- Spring Boot
- REST APIs
- SQL Database
- Maven
- Docker (optional)

---

## Project Structure
src/main/java/
│
├── controllers/ # API endpoints
├── service/ # Business logic
├── repository/ # Database access
├── entity/ # Domain models
├── config/ # Security and application configuration
└── auth/ # Authentication and authorization logic

---

## Getting Started

### Prerequisites

Before running the project, make sure you have installed:

- **Java 17**
- **Maven**
- **MySQL** (or the database configured in the project)

---

## Installation

Clone the repository:
- git clone https://github.com/MarcoTrujillo10/hoppo-ecommerce-backend.git

Make sure the database is running before starting the backend.

---

## Running the Application

### Option 1 — Using Maven Wrapper
- mvn spring-boot:run

### Option 3 — From your IDE

Open the project in **IntelliJ IDEA**, **Eclipse**, or **VS Code** and run the main class:
- HppoBackendApplication.java

