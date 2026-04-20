package com.airport.exception;

public class FlightConflictException extends RuntimeException implements ApplicationException {
  public FlightConflictException(String message) {
    super(message);
  }
}
