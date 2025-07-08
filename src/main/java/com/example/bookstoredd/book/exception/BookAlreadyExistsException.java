package com.example.bookstoredd.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {

  public BookAlreadyExistsException(Long id) {
    super(String.format("Book already exists with ID: %d", id));
  }

  public BookAlreadyExistsException(String message) {
    super(message);
  }
}
