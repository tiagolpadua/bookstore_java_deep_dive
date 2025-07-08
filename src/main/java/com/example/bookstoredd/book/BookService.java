package com.example.bookstoredd.book;

import com.example.bookstoredd.book.exception.BookAlreadyExistsException;
import com.example.bookstoredd.book.exception.BookNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

  private final BookRepository bookRepository;

  // Constants for log messages
  private static final String BOOK_CREATED_MSG = "Book created successfully: {}";
  private static final String BOOK_UPDATED_MSG = "Book updated successfully: {}";
  private static final String BOOK_DELETED_MSG = "Book deleted successfully with ID: {}";
  private static final String BOOK_NOT_FOUND_MSG = "Book not found with ID: {}";
  private static final String BOOKS_RETRIEVED_MSG = "Retrieved {} books";
  private static final String SEARCH_BOOKS_BY_TITLE_MSG = "Searching books by title: {}";
  private static final String SEARCH_BOOKS_BY_AUTHOR_MSG = "Searching books by author: {}";

  @Transactional(readOnly = true)
  public List<Book> getAllBooks() {
    log.debug("Retrieving all books");
    List<Book> books = bookRepository.findAllOrderedById();
    log.info(BOOKS_RETRIEVED_MSG, books.size());
    return books;
  }

  @Transactional(readOnly = true)
  public Book getBookById(Long id) {
    log.debug("Retrieving book with ID: {}", id);
    return bookRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.warn(BOOK_NOT_FOUND_MSG, id);
              return new BookNotFoundException(id);
            });
  }

  public Book createBook(Book book) {
    log.debug("Creating new book: {}", book.getTitle());

    // Trim whitespace from title and author if they are not null
    if (book.getTitle() != null) {
      book.setTitle(book.getTitle().trim());
    }
    if (book.getAuthor() != null) {
      book.setAuthor(book.getAuthor().trim());
    }

    // Verificar se o livro já existe por ID
    if (book.getId() != null && bookRepository.existsById(book.getId())) {
      log.warn("Book with ID {} already exists", book.getId());
      throw new BookAlreadyExistsException(book.getId());
    }

    Book savedBook = bookRepository.save(book);
    log.info(BOOK_CREATED_MSG, savedBook.getTitle());
    return savedBook;
  }

  public Book updateBook(Long id, Book book) {
    log.debug("Updating book with ID: {}", id);
    Book existingBook =
        bookRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn(BOOK_NOT_FOUND_MSG, id);
                  return new BookNotFoundException(id);
                });

    existingBook.setTitle(book.getTitle() != null ? book.getTitle().trim() : null);
    existingBook.setAuthor(book.getAuthor() != null ? book.getAuthor().trim() : null);

    Book updatedBook = bookRepository.save(existingBook);
    log.info(BOOK_UPDATED_MSG, updatedBook.getTitle());
    return updatedBook;
  }

  public void deleteBook(Long id) {
    log.debug("Deleting book with ID: {}", id);
    if (!bookRepository.existsById(id)) {
      log.warn(BOOK_NOT_FOUND_MSG, id);
      throw new BookNotFoundException(id);
    }
    bookRepository.deleteById(id);
    log.info(BOOK_DELETED_MSG, id);
  }

  public void deleteAllBooks() {
    log.debug("Deleting all books");
    long count = bookRepository.count();
    bookRepository.deleteAll();
    log.info("Deleted {} books", count);
  }

  @Transactional(readOnly = true)
  public List<Book> searchBooksByTitle(String title) {
    log.debug(SEARCH_BOOKS_BY_TITLE_MSG, title);
    List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
    log.info("Found {} books with title containing: {}", books.size(), title);
    return books;
  }

  @Transactional(readOnly = true)
  public List<Book> searchBooksByAuthor(String author) {
    log.debug(SEARCH_BOOKS_BY_AUTHOR_MSG, author);
    List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
    log.info("Found {} books with author containing: {}", books.size(), author);
    return books;
  }

  @Transactional(readOnly = true)
  public boolean existsById(Long id) {
    return bookRepository.existsById(id);
  }
}
