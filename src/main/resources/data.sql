-- Script SQL para popular a tabela Book com dados iniciais
-- Este arquivo é executado automaticamente pelo Spring Boot após o schema.sql

INSERT INTO book (title, author) VALUES 
('1984', 'George Orwell'),
('Dom Casmurro', 'Machado de Assis'),
('O Senhor dos Anéis: A Sociedade do Anel', 'J.R.R. Tolkien'),
('Cem Anos de Solidão', 'Gabriel García Márquez'),
('O Pequeno Príncipe', 'Antoine de Saint-Exupéry');
