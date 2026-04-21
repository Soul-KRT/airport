package com.airport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight implements com.airport.util.Storeable {
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
  
  private IntegerProperty flightId = new SimpleIntegerProperty();
  private StringProperty flightNumber = new SimpleStringProperty("");
  private StringProperty destination = new SimpleStringProperty("");
  private ObjectProperty<LocalDate> departureDate = new SimpleObjectProperty<>();
  private ObjectProperty<LocalTime> departureTime = new SimpleObjectProperty<>();
  private IntegerProperty durationMin = new SimpleIntegerProperty(60);
  private StringProperty gate = new SimpleStringProperty("");
  private StringProperty status = new SimpleStringProperty("Запланирован");
  
  public Flight() {
  }
  
  public Flight(int id, String number, String destination, LocalDate date, LocalTime time, int duration, String gate, String status) {
    this.flightId.set(id);
    this.flightNumber.set(number);
    this.destination.set(destination);
    this.departureDate.set(date);
    this.departureTime.set(time);
    this.durationMin.set(duration);
    this.gate.set(gate);
    this.status.set(status);
  }
  
  public boolean isConflictingWith(Flight another) {
    if (!this.gate.get().equals(another.gate.get())) return false;
    if (!this.departureDate.get().equals(another.departureDate.get())) return false;
    LocalTime thisEnd = this.departureTime.get().plus(Duration.ofMinutes(this.durationMin.get()));
    LocalTime anotherEnd = another.departureTime.get().plus(Duration.ofMinutes(another.durationMin.get()));
    return (this.departureTime.get().isBefore(anotherEnd) && thisEnd.isAfter(another.departureTime.get()));
  }
  
  public int getFlightId() {
    return flightId.get();
  }
  
  public void setFlightId(int v) {
    flightId.set(v);
  }
  
  public IntegerProperty flightIdProperty() {
    return flightId;
  }
  
  public String getFlightNumber() {
    return flightNumber.get();
  }
  
  public void setFlightNumber(String v) {
    flightNumber.set(v);
  }
  
  public StringProperty flightNumberProperty() {
    return flightNumber;
  }
  
  public String getDestination() {
    return destination.get();
  }
  
  public void setDestination(String v) {
    destination.set(v);
  }
  
  public StringProperty destinationProperty() {
    return destination;
  }
  
  public LocalDate getDepartureDate() {
    return departureDate.get();
  }
  
  public void setDepartureDate(LocalDate v) {
    departureDate.set(v);
  }
  
  public ObjectProperty<LocalDate> departureDateProperty() {
    return departureDate;
  }
  
  public LocalTime getDepartureTime() {
    return departureTime.get();
  }
  
  public void setDepartureTime(LocalTime v) {
    departureTime.set(v);
  }
  
  public ObjectProperty<LocalTime> departureTimeProperty() {
    return departureTime;
  }
  
  public int getDurationMin() {
    return durationMin.get();
  }
  
  public void setDurationMin(int v) {
    durationMin.set(v);
  }
  
  public IntegerProperty durationMinProperty() {
    return durationMin;
  }
  
  public String getGate() {
    return gate.get();
  }
  
  public void setGate(String v) {
    gate.set(v);
  }
  
  public StringProperty gateProperty() {
    return gate;
  }
  
  public String getStatus() {
    return status.get();
  }
  
  public void setStatus(String v) {
    status.set(v);
  }
  
  public StringProperty statusProperty() {
    return status;
  }
  
  @Override
  public String toString() {
    return getFlightNumber() + " | " + getDestination()
           + " | " + getDepartureDate().format(DATE_FMT)
           + " " + getDepartureTime().format(TIME_FMT)
           + " | Гейт: " + getGate();
  }
}
