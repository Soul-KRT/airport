package com.airport.repository;

import com.airport.model.Flight;
import com.airport.model.FlightList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightRepository {
  
  private static final String FILE = "flights.json";
  private FlightList data;
  
  public FlightRepository() {
    try {
      data = JsonStorage.load(FILE, FlightList.class);
    } catch (Exception e) {
      data = new FlightList();
    }
  }
  
  public List<Flight> getFlights() {
    return new ArrayList<>(data.getFlights());
  }
  
  public Optional<Flight> findById(int id) {
    return data.getFlights().stream().filter(f -> f.getFlightId() == id).findFirst();
  }
  
  public Flight add(Flight flight) {
    flight.setFlightId(data.getNextId());
    data.setNextId(data.getNextId() + 1);
    data.getFlights().add(flight);
    save();
    return flight;
  }
  
  public void update(Flight existing, Flight updated) {
    existing.setFlightNumber(updated.getFlightNumber());
    existing.setDestination(updated.getDestination());
    existing.setDepartureDate(updated.getDepartureDate());
    existing.setDepartureTime(updated.getDepartureTime());
    existing.setDurationMin(updated.getDurationMin());
    existing.setGate(updated.getGate());
    existing.setStatus(updated.getStatus());
    save();
  }
  
  public void remove(Flight flight) {
    data.getFlights().removeIf(f -> f.getFlightId() == flight.getFlightId());
    save();
  }
  
  private void save() {
    JsonStorage.save(FILE, data);
  }
}
