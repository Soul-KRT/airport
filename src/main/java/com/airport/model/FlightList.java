package com.airport.model;

import com.airport.util.Storeable;

import java.util.ArrayList;
import java.util.List;

public class FlightList implements Storeable {
  private List<Flight> flights = new ArrayList<>();
  private int nextId = 1;
  
  public List<Flight> getFlights() {
    return flights;
  }
  
  public void setFlights(List<Flight> flights) {
    this.flights = flights;
  }
  
  public int getNextId() {
    return nextId;
  }
  
  public void setNextId(int nextId) {
    this.nextId = nextId;
  }
}
