package com.airport.exception;

public class ValidationException extends RuntimeException implements ApplicationException {
  public ValidationException(String message) {
    super(message);
  }
}
