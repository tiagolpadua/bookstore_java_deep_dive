package com.example.bookstoredd.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Books", description = "API for managing books")
public class BookController {

  private final BookService bookService;

  @GetMapping
  @Operation(summary = "Get all books", description = "Retrieve a list of all books")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<List<BookDTO>> getAllBooks() {
    log.info("Retrieving all books");
    List<Book> books = bookService.getAllBooks();
    List<BookDTO> bookDTOs = books.stream().map(BookDTO::fromBook).toList();
    log.info("Retrieved {} books", bookDTOs.size());
    return ResponseEntity.ok(bookDTOs);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get book by ID", description = "Retrieve a book by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<BookDTO> getBookById(
      @PathVariable("id") @Parameter(description = "Book ID", example = "1") Long id) {
    log.info("Retrieving book with ID: {}", id);

    Book book = bookService.getBookById(id);
    log.info("Successfully retrieved book with ID: {}", id);
    return ResponseEntity.ok(BookDTO.fromBook(book));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a new book", description = "Create a new book")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Book already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
    log.info("Creating new book with title: {}", bookDTO.title());

    Book book = bookDTO.toBook();
    Book savedBook = bookService.createBook(book);

    log.info("Successfully created book with ID: {}", savedBook.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(BookDTO.fromBook(savedBook));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update a book", description = "Update an existing book")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<BookDTO> updateBook(
      @PathVariable("id") @Parameter(description = "Book ID", example = "1") Long id,
      @Valid @RequestBody BookDTO bookDTO) {
    log.info("Updating book with ID: {}", id);

    Book book = bookDTO.toBook();
    Book updatedBook = bookService.updateBook(id, book);

    log.info("Successfully updated book with ID: {}", id);
    return ResponseEntity.ok(BookDTO.fromBook(updatedBook));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a book", description = "Delete a book by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<Void> deleteBookById(
      @PathVariable("id") @Parameter(description = "Book ID", example = "1") Long id) {
    log.info("Deleting book with ID: {}", id);

    bookService.deleteBook(id);
    log.info("Successfully deleted book with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  @Operation(summary = "Delete all books", description = "Delete all books")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "All books deleted successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<Void> deleteAllBooks() {
    log.info("Deleting all books");
    bookService.deleteAllBooks();
    log.info("Successfully deleted all books");
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  @Operation(summary = "Search books", description = "Search books by title or author")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<List<BookDTO>> searchBooks(
      @RequestParam(value = "title", required = false)
          @Parameter(description = "Search by book title")
          String title,
      @RequestParam(value = "author", required = false)
          @Parameter(description = "Search by book author")
          String author) {

    log.info("Searching books with title: '{}' and author: '{}'", title, author);

    if ((title == null || title.trim().isEmpty()) && (author == null || author.trim().isEmpty())) {
      log.warn("Search called without valid parameters");
      return ResponseEntity.badRequest().build();
    }

    List<Book> books;
    if (title != null && !title.trim().isEmpty()) {
      books = bookService.searchBooksByTitle(title.trim());
      log.info("Found {} books by title '{}'", books.size(), title);
    } else {
      books = bookService.searchBooksByAuthor(author.trim());
      log.info("Found {} books by author '{}'", books.size(), author);
    }

    List<BookDTO> bookDTOs = books.stream().map(BookDTO::fromBook).toList();
    return ResponseEntity.ok(bookDTOs);
  }
}
