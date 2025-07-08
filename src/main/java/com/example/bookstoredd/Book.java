package com.example.bookstoredd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Book entity")
public class Book {
  @Schema(description = "Book ID", example = "1")
  private Long id;
  
  @Schema(description = "Book title", example = "The Great Gatsby")
  private String title;
  
  @Schema(description = "Book author", example = "F. Scott Fitzgerald")
  private String author;
}
