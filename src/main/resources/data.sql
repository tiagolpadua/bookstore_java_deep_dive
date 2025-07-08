-- Script SQL para popular a tabela Book com dados iniciais
-- Este arquivo é executado automaticamente pelo Spring Boot após o schema.sql

INSERT INTO book (id, title, author) VALUES 
(1, '1984', 'George Orwell'),
(2, 'Dom Casmurro', 'Machado de Assis'),
(3, 'O Senhor dos Anéis: A Sociedade do Anel', 'J.R.R. Tolkien'),
(4, 'Cem Anos de Solidão', 'Gabriel García Márquez'),
(5, 'O Pequeno Príncipe', 'Antoine de Saint-Exupéry');
