package com.example.bookstoredd.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {

  public BookNotFoundException(Long id) {
    super(String.format("Book not found with ID: %d", id));
  }

  public BookNotFoundException(String message) {
    super(message);
  }
}
