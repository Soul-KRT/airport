package com.airport.model;

import com.airport.util.Storeable;

import java.util.ArrayList;
import java.util.List;

public class PassengerList implements Storeable {
  private List<Passenger> passengers = new ArrayList<>();
  private int nextId = 1;
  
  public List<Passenger> getPassengers() {
    return passengers;
  }
  
  public void setPassengers(List<Passenger> passengers) {
    this.passengers = passengers;
  }
  
  public int getNextId() {
    return nextId;
  }
  
  public void setNextId(int nextId) {
    this.nextId = nextId;
  }
}
