package com.airport.repository;

import com.airport.model.Passenger;
import com.airport.model.PassengerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PassengerRepository {
  
  private static final String FILE = "passengers.json";
  private PassengerList data;
  
  public PassengerRepository() {
    try {
      data = JsonStorage.load(FILE, PassengerList.class);
    } catch (Exception e) {
      data = new PassengerList();
    }
  }
  
  public List<Passenger> getPassengers() {
    return new ArrayList<>(data.getPassengers());
  }
  
  public List<Passenger> getByFlightId(int flightId) {
    return data.getPassengers().stream()
               .filter(p -> p.getLinkedFlightId() == flightId)
               .collect(Collectors.toList());
  }
  
  public Optional<Passenger> findById(int id) {
    return data.getPassengers().stream().filter(p -> p.getPassengerId() == id).findFirst();
  }
  
  public Passenger add(Passenger passenger) {
    passenger.setPassengerId(data.getNextId());
    data.setNextId(data.getNextId() + 1);
    data.getPassengers().add(passenger);
    save();
    return passenger;
  }
  
  public void update(Passenger existing, Passenger updated) {
    existing.setFullName(updated.getFullName());
    existing.setTicketNumber(updated.getTicketNumber());
    existing.setPassportNumber(updated.getPassportNumber());
    existing.setLinkedFlightId(updated.getLinkedFlightId());
    existing.setSeat(updated.getSeat());
    save();
  }
  
  public void remove(Passenger passenger) {
    data.getPassengers().removeIf(p -> p.getPassengerId() == passenger.getPassengerId());
    save();
  }
  
  public void removeByFlightId(int flightId) {
    data.getPassengers().removeIf(p -> p.getLinkedFlightId() == flightId);
    save();
  }
  
  private void save() {
    JsonStorage.save(FILE, data);
  }
}
