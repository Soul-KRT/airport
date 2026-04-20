package com.airport.controller;

import com.airport.model.Flight;
import com.airport.model.Passenger;
import com.airport.service.FlightManager;
import com.airport.service.PassengerManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PassengerFormController {
  
  private final PassengerManager passengerManager;
  private final FlightManager flightManager;
  private Passenger passenger;
  private Runnable onSaved;
  
  @FXML
  private TextField fullNameField;
  @FXML
  private TextField ticketNumberField;
  @FXML
  private TextField passportNumberField;
  @FXML
  private TextField seatField;
  @FXML
  private ComboBox<Flight> flightCombo;
  @FXML
  private Button saveButton;
  
  public PassengerFormController(PassengerManager passengerManager, FlightManager flightManager) {
    this.passengerManager = passengerManager;
    this.flightManager = flightManager;
  }
  
  @FXML
  public void initialize() {
    flightCombo.setConverter(new StringConverter<>() {
      @Override
      public String toString(Flight flight) {
        return flight == null ? "" : flight.toString();
      }
      
      @Override
      public Flight fromString(String string) {
        return null;
      }
    });
    flightCombo.getItems().setAll(flightManager.getFlights());
  }
  
  public void setPassenger(Passenger passenger) {
    this.passenger = passenger;
    fullNameField.textProperty().bindBidirectional(passenger.fullNameProperty());
    ticketNumberField.textProperty().bindBidirectional(passenger.ticketNumberProperty());
    passportNumberField.textProperty().bindBidirectional(passenger.passportNumberProperty());
    seatField.textProperty().bindBidirectional(passenger.seatProperty());
    
    if (passenger.getLinkedFlightId() >= 0) {
      flightManager.getFlight(passenger.getLinkedFlightId())
                   .ifPresent(flight -> flightCombo.getSelectionModel().select(flight));
    }
  }
  
  public void setOnSaved(Runnable onSaved) {
    this.onSaved = onSaved;
  }
  
  @FXML
  private void handleSave() {
    try {
      Flight selectedFlight = flightCombo.getSelectionModel().getSelectedItem();
      if (selectedFlight == null) {
        MainWindowController.showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите рейс из списка.");
        return;
      }
      passenger.setLinkedFlightId(selectedFlight.getFlightId());
      passengerManager.merge(passenger);
      if (onSaved != null) onSaved.run();
      ((Stage) saveButton.getScene().getWindow()).close();
    } catch (Exception e) {
      MainWindowController.showAlert(Alert.AlertType.WARNING, "Ошибка", e.getMessage());
    }
  }
  
  @FXML
  private void handleCancel() {
    ((Stage) saveButton.getScene().getWindow()).close();
  }
}
