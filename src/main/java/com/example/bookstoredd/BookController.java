package com.example.bookstoredd;

import java.sql.ResultSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class BookController {

  private final JdbcTemplate jdbcTemplate;

  private static final RowMapper<Book> bookRowMapper =
      (ResultSet rs, int rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        return book;
      };

  // http://localhost:8080/api/books
  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    String sql = "SELECT id, title, author FROM book ORDER BY id";
    List<Book> books = jdbcTemplate.query(sql, bookRowMapper);
    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  // http://localhost:8080/api/books/1
  @GetMapping("/{id}")
  public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
    Book book = getBook(id);

    if (book == null) {
      return new ResponseEntity<>(new Book(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(book, HttpStatus.OK);
    }
  }

  private Book getBook(Long id) {
    String sql = "SELECT id, title, author FROM book WHERE id = ?";
    List<Book> books = jdbcTemplate.query(sql, bookRowMapper, id);
    if (books.isEmpty()) {
      return null;
    } else {
      return books.get(0);
    }
  }

  @PostMapping
  public ResponseEntity<Book> createBook(@RequestBody Book book) {
    Book savedBook = save(book);
    return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
    Book existingBook = getBook(id);
    if (existingBook != null) {
      existingBook.setTitle(book.getTitle().trim());
      existingBook.setAuthor(book.getAuthor().trim());

      Book updatedBook = save(existingBook);
      return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    } else {
      return null;
    }
  }

  @PostMapping("/excluir/{id}")
  public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") Long id) {
    String sql = "DELETE FROM book WHERE id = ?";
    jdbcTemplate.update(sql, id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping
  public ResponseEntity<HttpStatus> deleteAllBooks() {
    String sql = "DELETE FROM book";
    jdbcTemplate.update(sql);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/search")
  public ResponseEntity<List<Book>> searchBooks(
      @RequestParam(value = "title", required = false) String title,
      @RequestParam(value = "author", required = false) String author) {
    List<Book> books;

    if (title != null && !title.trim().isEmpty()) {
      String sql =
          "SELECT id, title, author FROM book WHERE LOWER(title) LIKE LOWER(\'%"
              + title
              + "%\') ORDER BY id";
      books = jdbcTemplate.query(sql, bookRowMapper);
    } else if (author != null && !author.trim().isEmpty()) {
      String sql =
          "SELECT id, title, author FROM book WHERE LOWER(author) LIKE LOWER(?) ORDER BY id";
      books = jdbcTemplate.query(sql, bookRowMapper, "%" + author + "%");
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  public Book save(Book book) {
    if (getBook(book.getId()) == null) {
      log.info("inserindo novo livro: {}", book.getTitle());
      String sql = "INSERT INTO book (id, title, author) VALUES (?, ?, ?)";
      jdbcTemplate.update(sql, book.getId(), book.getTitle(), book.getAuthor());
    } else {
      log.info("atualizando livro: {}", book.getTitle());
      String sql = "UPDATE book SET title = ?, author = ? WHERE id = ?";
      jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getId());
    }
    return book;
  }
}
