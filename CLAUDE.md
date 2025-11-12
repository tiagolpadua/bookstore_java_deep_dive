# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.3 application demonstrating a simple REST API for managing books. The application uses:
- **Spring Boot Starter Web** for REST API endpoints
- **Spring Data JDBC** with direct JdbcTemplate usage (no Spring Data repositories)
- **H2 in-memory database** for data persistence during development
- **Java 17** as the target runtime

## Architecture

The application follows a simple architecture without traditional layers (no service layer, no repository layer):

- **BookController** (`src/main/java/com/example/bookstoredd/BookController.java`): REST controller that directly uses JdbcTemplate for database operations. Contains all CRUD logic including save, update, delete, and search operations.
- **Book** (`src/main/java/com/example/bookstoredd/Book.java`): Plain POJO entity with id, title, and author fields. No JPA annotations.
- **Database Schema**: Managed via SQL scripts (`schema.sql` and `data.sql`) executed at startup, not by Hibernate/JPA.

### Key Architectural Notes

- The controller uses raw JDBC with JdbcTemplate directly (no abstraction layer)
- The `save()` method in BookController handles both insert and update based on whether the book exists
- SQL queries are written inline within the controller methods
- A custom RowMapper is defined as a static field for mapping ResultSet to Book objects
- **SQL Injection Vulnerability**: The search endpoint at line 111-112 in BookController concatenates the title parameter directly into SQL, creating a security vulnerability. The author parameter correctly uses parameterized queries.

## Common Commands

### Running the Application
```bash
./mvnw spring-boot:run
```

### Running with a Specific Profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Running Tests
```bash
./mvnw test
```

### Building the Project
```bash
./mvnw clean install
```

### Packaging (creates JAR)
```bash
./mvnw package
```

## Development Resources

When the application is running:
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:bytecarddb`
  - Username: `sa`
  - Password: (empty)
- **API Base URL**: http://localhost:8080/api/books

## Database Configuration

- The application uses H2 in-memory database that resets on every restart
- Schema is created via `src/main/resources/schema.sql`
- Sample data is loaded via `src/main/resources/data.sql`
- Both SQL files run automatically on startup (`spring.sql.init.mode=always`)
- JPA/Hibernate DDL auto is disabled (`spring.jpa.hibernate.ddl-auto=none`)

## API Endpoints

The REST API is exposed under `/api/books`:
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create a new book
- `PUT /api/books/{id}` - Update a book
- `POST /api/books/excluir/{id}` - Delete a book by ID
- `DELETE /api/books` - Delete all books
- `GET /api/books/search?title={title}` or `?author={author}` - Search books
