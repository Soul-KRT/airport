package com.airport.service;

import com.airport.exception.ValidationException;
import com.airport.model.Flight;
import com.airport.model.Passenger;
import com.airport.repository.FlightRepository;
import com.airport.repository.PassengerRepository;

import java.util.List;
import java.util.Optional;

public class PassengerManager {
  
  private final PassengerRepository passengerRepository;
  private final FlightRepository flightRepository;
  
  public PassengerManager(PassengerRepository passengerRepository, FlightRepository flightRepository) {
    this.passengerRepository = passengerRepository;
    this.flightRepository = flightRepository;
  }
  
  public List<Passenger> getPassengers() {
    return passengerRepository.getPassengers();
  }
  
  public List<Passenger> getPassengersByFlight(Flight flight) {
    return passengerRepository.getByFlightId(flight.getFlightId());
  }
  
  public Passenger addPassenger(Passenger passenger) {
    validatePassenger(passenger);
    return passengerRepository.add(passenger);
  }
  
  public void merge(Passenger passenger) {
    passengerRepository.findById(passenger.getPassengerId()).ifPresentOrElse(
       existed -> {
         validatePassenger(passenger);
         passengerRepository.update(existed, passenger);
       },
       () -> addPassenger(passenger)
    );
  }
  
  public void removePassenger(Passenger passenger) {
    passengerRepository.remove(passenger);
  }
  
  public void validatePassenger(Passenger passenger) {
    if (passenger.getFullName() == null || passenger.getFullName().trim().isEmpty()) {
      throw new ValidationException("ФИО пассажира не может быть пустым");
    }
    if (passenger.getTicketNumber() == null || passenger.getTicketNumber().trim().isEmpty()) {
      throw new ValidationException("Номер билета не может быть пустым");
    }
    if (passenger.getLinkedFlightId() < 0) {
      throw new ValidationException("Выберите рейс для пассажира");
    }
    boolean flightExists = flightRepository.findById(passenger.getLinkedFlightId()).isPresent();
    if (!flightExists) {
      throw new ValidationException("Выбранный рейс не существует");
    }
  }
  
  public Optional<Passenger> findById(int id) {
    return passengerRepository.findById(id);
  }
}
