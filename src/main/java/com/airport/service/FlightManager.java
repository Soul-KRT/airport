package com.airport.service;

import com.airport.exception.FlightConflictException;
import com.airport.exception.ValidationException;
import com.airport.model.Flight;
import com.airport.repository.FlightRepository;
import com.airport.repository.PassengerRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlightManager {
  
  private final FlightRepository flightRepository;
  private final PassengerRepository passengerRepository;
  
  public FlightManager(FlightRepository flightRepository, PassengerRepository passengerRepository) {
    this.flightRepository = flightRepository;
    this.passengerRepository = passengerRepository;
  }
  
  public List<Flight> getFlights() {
    return flightRepository.getFlights();
  }
  
  public Optional<Flight> getFlight(int id) {
    return flightRepository.findById(id);
  }
  
  public Flight createFlight(Flight flight) {
    validateFlight(flight);
    List<Flight> conflicts = getConflict(flight);
    if (!conflicts.isEmpty()) {
      throw new FlightConflictException(
         "Конфликт расписания: гейт " + flight.getGate() + " уже занят в указанное время. " +
         "Конфликтующий рейс: " + conflicts.get(0).getFlightNumber()
      );
    }
    return flightRepository.add(flight);
  }
  
  public void merge(Flight flight) {
    getFlight(flight.getFlightId()).ifPresentOrElse(
       existed -> {
         validateFlight(flight);
         List<Flight> conflicts = getConflict(flight);
         if (!conflicts.isEmpty()) {
           throw new FlightConflictException(
              "Конфликт расписания: гейт " + flight.getGate() + " уже занят. " +
              "Конфликтующий рейс: " + conflicts.get(0).getFlightNumber()
           );
         }
         flightRepository.update(existed, flight);
       },
       () -> createFlight(flight)
    );
  }
  
  public void removeFlight(Flight flight) {
    passengerRepository.removeByFlightId(flight.getFlightId());
    flightRepository.remove(flight);
  }
  
  public List<Flight> getConflict(Flight flight) {
    return flightRepository.getFlights().stream()
                           .filter(flight::isConflictingWith)
                           .filter(f -> f.getFlightId() != flight.getFlightId())
                           .collect(Collectors.toList());
  }
  
  public void validateFlight(Flight flight) {
    if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) {
      throw new ValidationException("Номер рейса не может быть пустым");
    }
    if (flight.getDestination() == null || flight.getDestination().trim().isEmpty()) {
      throw new ValidationException("Направление не может быть пустым");
    }
    if (flight.getDepartureDate() == null) {
      throw new ValidationException("Укажите дату вылета");
    }
    if (flight.getDepartureTime() == null) {
      throw new ValidationException("Укажите время вылета (формат ЧЧ:мм)");
    }
    if (flight.getDurationMin() <= 0) {
      throw new ValidationException("Длительность рейса должна быть положительной");
    }
    if (flight.getGate() == null || flight.getGate().trim().isEmpty()) {
      throw new ValidationException("Укажите гейт посадки");
    }
  }
}
