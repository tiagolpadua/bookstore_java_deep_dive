package com.example.bookstoredd.book.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Error response object")
public record ErrorResponse(
    @Schema(description = "Timestamp when the error occurred", example = "2025-07-08T18:15:30")
        LocalDateTime timestamp,
    @Schema(description = "HTTP status code", example = "404") int status,
    @Schema(description = "HTTP status reason phrase", example = "Not Found") String error,
    @Schema(description = "Error message", example = "Book not found with ID: 999") String message,
    @Schema(description = "Request path that caused the error", example = "/api/books/999")
        String path,
    @Schema(description = "List of validation errors (if applicable)")
        List<String> validationErrors) {

  public ErrorResponse(int status, String error, String message, String path) {
    this(LocalDateTime.now(), status, error, message, path, null);
  }

  public ErrorResponse(
      int status, String error, String message, String path, List<String> validationErrors) {
    this(LocalDateTime.now(), status, error, message, path, validationErrors);
  }
}
