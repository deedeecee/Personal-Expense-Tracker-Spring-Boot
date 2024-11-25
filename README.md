# Personal Finance Tracker

A **Personal Finance Tracker** application built using Spring Boot. This project helps users manage their expenses effectively by offering features such as user registration, authentication, expense tracking, and administrative controls.

---

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Project Structure](#project-structure)

---

## Features

- **User Authentication**: JWT-based authentication for secure access.
- **Expense Management**: Add, update, delete, and view expenses.
- **Role-based Access**:
  - **Users**: Manage personal expenses.
  - **Admins**: Manage users and view all user data.
- **REST API**: Clean and modular API design for seamless integration.
- **Data Validation**: Validates user inputs using annotations.
- **Calculate Expenses**: Users can calulate their weekly as well as monthly expenses.

---

## Technologies Used

- **Backend**: Spring Boot
- **Database**: MongoDB (via MongoDB Atlas)
- **Authentication**: JWT (JSON Web Tokens)
- **Object Mapping**: Custom mapper classes for DTO-Entity mapping
- **Validation**: Hibernate Validator (`@Valid`)
- **Build Tool**: Maven

---

## Setup and Installation

### Prerequisites

- Java 17 or later
- Maven 3.8+ installed
- MongoDB instance or MongoDB Atlas account

### Steps to Run Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/Personal-Expense-Tracker-Spring-Boot.git
   cd personal-expense-tracker
   ```
2. Configure application settings:
      - Create a file inside the `src/main/resources` folder and name it `application.yml`
      - Update the MongoDB connection string, JWT secret key and other environment-specific properties.
        ```yaml
          spring:
            application:
              name: Personal Expense Tracker
            data:
              mongodb:
                uri: <your_mongodb_atlas_uri>
                database: expense_tracker
                auto-index-creation: true
          
          my:
            secret:
              key: <your_32_character_key>
        ```
3. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
4. Access the endpoints using Postman or its alternatives.

## Project Structure
```bash
src/main/java/com/debankar/personal_expense_tracker
├── controller       # REST Controllers
├── service          # Business logic
├── repository       # Data access layer
├── entity           # MongoDB entity definitions
├── dto              # Data Transfer Objects
├── config           # Configuration classes (JWT, MongoDB, etc.)
└── mapper           # Custom mappers
```
