package com.example.bookstoredd.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Book entity")
public class Book {
  @Schema(description = "Book ID", example = "1")
  @Id
  private Long id;

  @Schema(description = "Book title", example = "The Great Gatsby")
  @NotBlank(message = "Title is mandatory")
  private String title;

  @Schema(description = "Book author", example = "F. Scott Fitzgerald")
  @NotBlank(message = "Author is mandatory")
  private String author;
}
