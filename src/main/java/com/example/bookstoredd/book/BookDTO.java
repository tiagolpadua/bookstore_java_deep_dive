package com.example.bookstoredd.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Book Data Transfer Object")
public record BookDTO(
    @Schema(description = "Book ID", example = "1") Long id,
    @Schema(description = "Book title", example = "The Great Gatsby")
        @NotBlank(message = "Title is mandatory")
        String title,
    @Schema(description = "Book author", example = "F. Scott Fitzgerald")
        @NotBlank(message = "Author is mandatory")
        String author) {

  /** Converts a Book entity to BookDTO */
  public static BookDTO fromBook(Book book) {
    return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
  }

  /** Converts this BookDTO to a Book entity */
  public Book toBook() {
    return new Book(id, title, author);
  }
}
