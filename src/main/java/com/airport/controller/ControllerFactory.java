package com.airport.controller;

import com.airport.service.FlightManager;
import com.airport.service.PassengerManager;
import javafx.util.Callback;

public class ControllerFactory implements Callback<Class<?>, Object> {
  
  private final FlightManager flightManager;
  private final PassengerManager passengerManager;
  
  public ControllerFactory(FlightManager flightManager, PassengerManager passengerManager) {
    this.flightManager = flightManager;
    this.passengerManager = passengerManager;
  }
  
  @Override
  public Object call(Class<?> type) {
    if (type == MainWindowController.class) {
      return new MainWindowController(flightManager, passengerManager);
    } else if (type == FlightFormController.class) {
      return new FlightFormController(flightManager);
    } else if (type == PassengerFormController.class) {
      return new PassengerFormController(passengerManager, flightManager);
    }
    try {
      return type.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Cannot create controller: " + type, e);
    }
  }
}
