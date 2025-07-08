package com.example.bookstoredd;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper para converter ResultSet em Book
    private static final RowMapper<Book> bookRowMapper = (ResultSet rs, int rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        return book;
    };

    // Injeção de dependência por construtor
    public BookController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GET /api/books - Listar todos os livros
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        String sql = "SELECT id, title, author FROM book ORDER BY id";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper);
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // GET /api/books/{id} - Buscar livro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
        String sql = "SELECT id, title, author FROM book WHERE id = ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, id);
        Optional<Book> bookData = books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));

        if (bookData.isPresent()) {
            return new ResponseEntity<>(bookData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST /api/books - Criar novo livro
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Book newBook = new Book(book.getTitle().trim(), book.getAuthor().trim());
        Book savedBook = save(newBook);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    // PUT /api/books/{id} - Atualizar livro existente
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
        String sql = "SELECT id, title, author FROM book WHERE id = ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, id);
        Optional<Book> bookData = books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));

        if (bookData.isPresent()) {
            if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            Book existingBook = bookData.get();
            existingBook.setTitle(book.getTitle().trim());
            existingBook.setAuthor(book.getAuthor().trim());

            Book updatedBook = save(existingBook);
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE /api/books/{id} - Deletar livro por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") Long id) {
        if (!existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String sql = "DELETE FROM book WHERE id = ?";
        jdbcTemplate.update(sql, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // DELETE /api/books - Deletar todos os livros
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllBooks() {
        String sql = "DELETE FROM book";
        jdbcTemplate.update(sql);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // GET /api/books/search?title={title} - Buscar livros por título
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author) {
        List<Book> books;

        if (title != null && !title.trim().isEmpty()) {
            String sql = "SELECT id, title, author FROM book WHERE LOWER(title) LIKE LOWER(\'%" + title + "%\') ORDER BY id";
        books =  jdbcTemplate.query(sql, bookRowMapper);
        } else if (author != null && !author.trim().isEmpty()) {
            String sql = "SELECT id, title, author FROM book WHERE LOWER(author) LIKE LOWER(?) ORDER BY id";
        books =  jdbcTemplate.query(sql, bookRowMapper, "%" + author + "%");
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // GET /api/books/count - Contar total de livros
    @GetMapping("/count")
    public ResponseEntity<Long> countBooks() {
        long count = count();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM book WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }


        public long count() {
        String sql = "SELECT COUNT(*) FROM book";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            // Inserir novo livro
            String sql = "INSERT INTO book (title, author) VALUES (?, ?)";
            jdbcTemplate.update(sql, book.getTitle(), book.getAuthor());

            // Buscar o último ID inserido
            String lastIdSql = "SELECT LAST_INSERT_ID()";
            Long id = jdbcTemplate.queryForObject(lastIdSql, Long.class);
            book.setId(id);
        } else {
            // Atualizar livro existente
            String sql = "UPDATE book SET title = ?, author = ? WHERE id = ?";
            jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getId());
        }
        return book;
    }
}
