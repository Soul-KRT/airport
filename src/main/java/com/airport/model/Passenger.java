package com.airport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Passenger implements com.airport.util.Storeable {
  
  private IntegerProperty passengerId = new SimpleIntegerProperty();
  private StringProperty fullName = new SimpleStringProperty("");
  private StringProperty ticketNumber = new SimpleStringProperty("");
  private StringProperty passportNumber = new SimpleStringProperty("");
  private IntegerProperty linkedFlightId = new SimpleIntegerProperty(-1);
  private StringProperty seat = new SimpleStringProperty("");
  
  public Passenger() {
  }
  
  public Passenger(int id, String fullName, String ticketNumber, String passportNumber, int flightId, String seat) {
    this.passengerId.set(id);
    this.fullName.set(fullName);
    this.ticketNumber.set(ticketNumber);
    this.passportNumber.set(passportNumber);
    this.linkedFlightId.set(flightId);
    this.seat.set(seat);
  }
  
  public int getPassengerId() {
    return passengerId.get();
  }
  
  public void setPassengerId(int v) {
    passengerId.set(v);
  }
  
  public IntegerProperty passengerIdProperty() {
    return passengerId;
  }
  
  public String getFullName() {
    return fullName.get();
  }
  
  public void setFullName(String v) {
    fullName.set(v);
  }
  
  public StringProperty fullNameProperty() {
    return fullName;
  }
  
  public String getTicketNumber() {
    return ticketNumber.get();
  }
  
  public void setTicketNumber(String v) {
    ticketNumber.set(v);
  }
  
  public StringProperty ticketNumberProperty() {
    return ticketNumber;
  }
  
  public String getPassportNumber() {
    return passportNumber.get();
  }
  
  public void setPassportNumber(String v) {
    passportNumber.set(v);
  }
  
  public StringProperty passportNumberProperty() {
    return passportNumber;
  }
  
  public int getLinkedFlightId() {
    return linkedFlightId.get();
  }
  
  public void setLinkedFlightId(int v) {
    linkedFlightId.set(v);
  }
  
  public IntegerProperty linkedFlightIdProperty() {
    return linkedFlightId;
  }
  
  public String getSeat() {
    return seat.get();
  }
  
  public void setSeat(String v) {
    seat.set(v);
  }
  
  public StringProperty seatProperty() {
    return seat;
  }
}
