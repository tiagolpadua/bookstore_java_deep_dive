package com.example.bookstoredd.book;

import java.sql.ResultSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

  private final JdbcTemplate jdbcTemplate;

  private static final RowMapper<Book> bookRowMapper =
      (ResultSet rs, int rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        return book;
      };

  public List<Book> getAllBooks() {
    String sql = "SELECT id, title, author FROM book ORDER BY id";
    return jdbcTemplate.query(sql, bookRowMapper);
  }

  public Book getBookById(Long id) {
    String sql = "SELECT id, title, author FROM book WHERE id = ?";
    List<Book> books = jdbcTemplate.query(sql, bookRowMapper, id);
    return books.isEmpty() ? null : books.get(0);
  }

  public Book createBook(Book book) {
    return save(book);
  }

  public Book updateBook(Long id, Book book) {
    Book existingBook = getBookById(id);
    if (existingBook != null) {
      existingBook.setTitle(book.getTitle().trim());
      existingBook.setAuthor(book.getAuthor().trim());
      return save(existingBook);
    }
    return null;
  }

  public void deleteBook(Long id) {
    String sql = "DELETE FROM book WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  public void deleteAllBooks() {
    String sql = "DELETE FROM book";
    jdbcTemplate.update(sql);
  }

  public List<Book> searchBooksByTitle(String title) {
    String sql =
        "SELECT id, title, author FROM book WHERE LOWER(title) LIKE LOWER(\'%"
            + title
            + "%\') ORDER BY id";
    return jdbcTemplate.query(sql, bookRowMapper);
  }

  public List<Book> searchBooksByAuthor(String author) {
    String sql = "SELECT id, title, author FROM book WHERE LOWER(author) LIKE LOWER(?) ORDER BY id";
    return jdbcTemplate.query(sql, bookRowMapper, "%" + author + "%");
  }

  private Book save(Book book) {
    if (getBookById(book.getId()) == null) {
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
