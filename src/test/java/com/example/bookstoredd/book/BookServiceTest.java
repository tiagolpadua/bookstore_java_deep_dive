package com.example.bookstoredd.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.bookstoredd.book.exception.BookAlreadyExistsException;
import com.example.bookstoredd.book.exception.BookNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Service Tests")
class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookService bookService;

  private Book book1;
  private Book book2;

  @BeforeEach
  void setUp() {
    book1 = new Book();
    book1.setId(1L);
    book1.setTitle("Spring Boot in Action");
    book1.setAuthor("Craig Walls");

    book2 = new Book();
    book2.setId(2L);
    book2.setTitle("Clean Code");
    book2.setAuthor("Robert C. Martin");
  }

  @Test
  @DisplayName("Should return all books")
  void getAllBooks_ShouldReturnAllBooks() {
    // Given
    List<Book> expectedBooks = Arrays.asList(book1, book2);
    when(bookRepository.findAllOrderedById()).thenReturn(expectedBooks);

    // When
    List<Book> actualBooks = bookService.getAllBooks();

    // Then
    assertEquals(expectedBooks, actualBooks);
    verify(bookRepository).findAllOrderedById();
  }

  @Test
  @DisplayName("Should return book by id when exists")
  void getBookById_WhenBookExists_ShouldReturnBook() {
    // Given
    Long bookId = 1L;
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book1));

    // When
    Book actualBook = bookService.getBookById(bookId);

    // Then
    assertEquals(book1, actualBook);
    verify(bookRepository).findById(bookId);
  }

  @Test
  @DisplayName("Should throw BookNotFoundException when book does not exist")
  void getBookById_WhenBookDoesNotExist_ShouldThrowException() {
    // Given
    Long bookId = 999L;
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    // When & Then
    BookNotFoundException exception =
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(bookId));

    assertEquals("Book not found with ID: 999", exception.getMessage());
    verify(bookRepository).findById(bookId);
  }

  @Test
  @DisplayName("Should create new book successfully")
  void createBook_WhenBookIsValid_ShouldCreateBook() {
    // Given
    Book newBook = new Book();
    newBook.setTitle("New Book");
    newBook.setAuthor("New Author");

    Book savedBook = new Book();
    savedBook.setId(3L);
    savedBook.setTitle("New Book");
    savedBook.setAuthor("New Author");

    when(bookRepository.save(newBook)).thenReturn(savedBook);

    // When
    Book actualBook = bookService.createBook(newBook);

    // Then
    assertEquals(savedBook, actualBook);
    verify(bookRepository).save(newBook);
  }

  @Test
  @DisplayName("Should throw BookAlreadyExistsException when book with ID already exists")
  void createBook_WhenBookWithIdExists_ShouldThrowException() {
    // Given
    Book existingBook = new Book();
    existingBook.setId(1L);
    existingBook.setTitle("Existing Book");
    existingBook.setAuthor("Existing Author");

    when(bookRepository.existsById(1L)).thenReturn(true);

    // When & Then
    BookAlreadyExistsException exception =
        assertThrows(BookAlreadyExistsException.class, () -> bookService.createBook(existingBook));

    assertEquals("Book already exists with ID: 1", exception.getMessage());
    verify(bookRepository).existsById(1L);
    verify(bookRepository, never()).save(any(Book.class));
  }

  @Test
  @DisplayName("Should update existing book successfully")
  void updateBook_WhenBookExists_ShouldUpdateBook() {
    // Given
    Long bookId = 1L;
    Book updateData = new Book();
    updateData.setTitle("Updated Title");
    updateData.setAuthor("Updated Author");

    Book existingBook = new Book();
    existingBook.setId(bookId);
    existingBook.setTitle("Original Title");
    existingBook.setAuthor("Original Author");

    Book updatedBook = new Book();
    updatedBook.setId(bookId);
    updatedBook.setTitle("Updated Title");
    updatedBook.setAuthor("Updated Author");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(existingBook)).thenReturn(updatedBook);

    // When
    Book actualBook = bookService.updateBook(bookId, updateData);

    // Then
    assertEquals(updatedBook, actualBook);
    assertEquals("Updated Title", existingBook.getTitle());
    assertEquals("Updated Author", existingBook.getAuthor());
    verify(bookRepository).findById(bookId);
    verify(bookRepository).save(existingBook);
  }

  @Test
  @DisplayName("Should throw BookNotFoundException when updating non-existent book")
  void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
    // Given
    Long bookId = 999L;
    Book updateData = new Book();
    updateData.setTitle("Updated Title");
    updateData.setAuthor("Updated Author");

    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    // When & Then
    BookNotFoundException exception =
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(bookId, updateData));

    assertEquals("Book not found with ID: 999", exception.getMessage());
    verify(bookRepository).findById(bookId);
    verify(bookRepository, never()).save(any(Book.class));
  }

  @Test
  @DisplayName("Should delete book successfully when exists")
  void deleteBook_WhenBookExists_ShouldDeleteBook() {
    // Given
    Long bookId = 1L;
    when(bookRepository.existsById(bookId)).thenReturn(true);

    // When
    bookService.deleteBook(bookId);

    // Then
    verify(bookRepository).existsById(bookId);
    verify(bookRepository).deleteById(bookId);
  }

  @Test
  @DisplayName("Should throw BookNotFoundException when deleting non-existent book")
  void deleteBook_WhenBookDoesNotExist_ShouldThrowException() {
    // Given
    Long bookId = 999L;
    when(bookRepository.existsById(bookId)).thenReturn(false);

    // When & Then
    BookNotFoundException exception =
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));

    assertEquals("Book not found with ID: 999", exception.getMessage());
    verify(bookRepository).existsById(bookId);
    verify(bookRepository, never()).deleteById(bookId);
  }

  @Test
  @DisplayName("Should delete all books")
  void deleteAllBooks_ShouldDeleteAllBooks() {
    // Given
    when(bookRepository.count()).thenReturn(5L);

    // When
    bookService.deleteAllBooks();

    // Then
    verify(bookRepository).count();
    verify(bookRepository).deleteAll();
  }

  @Test
  @DisplayName("Should search books by title")
  void searchBooksByTitle_ShouldReturnMatchingBooks() {
    // Given
    String searchTitle = "Spring";
    List<Book> expectedBooks = Arrays.asList(book1);
    when(bookRepository.findByTitleContainingIgnoreCase(searchTitle)).thenReturn(expectedBooks);

    // When
    List<Book> actualBooks = bookService.searchBooksByTitle(searchTitle);

    // Then
    assertEquals(expectedBooks, actualBooks);
    verify(bookRepository).findByTitleContainingIgnoreCase(searchTitle);
  }

  @Test
  @DisplayName("Should search books by author")
  void searchBooksByAuthor_ShouldReturnMatchingBooks() {
    // Given
    String searchAuthor = "Craig";
    List<Book> expectedBooks = Arrays.asList(book1);
    when(bookRepository.findByAuthorContainingIgnoreCase(searchAuthor)).thenReturn(expectedBooks);

    // When
    List<Book> actualBooks = bookService.searchBooksByAuthor(searchAuthor);

    // Then
    assertEquals(expectedBooks, actualBooks);
    verify(bookRepository).findByAuthorContainingIgnoreCase(searchAuthor);
  }

  @Test
  @DisplayName("Should check if book exists by id")
  void existsById_ShouldReturnCorrectBoolean() {
    // Given
    Long bookId = 1L;
    when(bookRepository.existsById(bookId)).thenReturn(true);

    // When
    boolean exists = bookService.existsById(bookId);

    // Then
    assertTrue(exists);
    verify(bookRepository).existsById(bookId);
  }

  @Test
  @DisplayName("Should trim whitespace from title and author when creating book")
  void createBook_ShouldTrimWhitespaceFromTitleAndAuthor() {
    // Given
    Book bookWithWhitespace = new Book();
    bookWithWhitespace.setTitle("  Spring Boot  ");
    bookWithWhitespace.setAuthor("  Craig Walls  ");

    Book expectedBook = new Book();
    expectedBook.setId(1L);
    expectedBook.setTitle("Spring Boot");
    expectedBook.setAuthor("Craig Walls");

    when(bookRepository.save(any(Book.class))).thenReturn(expectedBook);

    // When
    bookService.createBook(bookWithWhitespace);

    // Then
    assertEquals("Spring Boot", bookWithWhitespace.getTitle());
    assertEquals("Craig Walls", bookWithWhitespace.getAuthor());
    verify(bookRepository).save(bookWithWhitespace);
  }

  @Test
  @DisplayName("Should trim whitespace from title and author when updating book")
  void updateBook_ShouldTrimWhitespaceFromTitleAndAuthor() {
    // Given
    Long bookId = 1L;
    Book updateData = new Book();
    updateData.setTitle("  Updated Title  ");
    updateData.setAuthor("  Updated Author  ");

    Book existingBook = new Book();
    existingBook.setId(bookId);
    existingBook.setTitle("Original Title");
    existingBook.setAuthor("Original Author");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(existingBook)).thenReturn(existingBook);

    // When
    bookService.updateBook(bookId, updateData);

    // Then
    assertEquals("Updated Title", existingBook.getTitle());
    assertEquals("Updated Author", existingBook.getAuthor());
    verify(bookRepository).findById(bookId);
    verify(bookRepository).save(existingBook);
  }
}
