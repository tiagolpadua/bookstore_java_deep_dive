-- Script SQL para criar a tabela Book
-- Este arquivo é executado automaticamente pelo Spring Boot na inicialização

CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);
