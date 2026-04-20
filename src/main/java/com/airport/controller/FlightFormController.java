package com.airport.controller;

import com.airport.model.Flight;
import com.airport.service.FlightManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.format.DateTimeFormatter;

public class FlightFormController {
  
  private final FlightManager flightManager;
  private Flight flight;
  private Runnable onSaved;
  
  @FXML
  private TextField flightNumberField;
  @FXML
  private TextField destinationField;
  @FXML
  private DatePicker departureDateField;
  @FXML
  private TextField departureTimeField;
  @FXML
  private TextField durationField;
  @FXML
  private TextField gateField;
  @FXML
  private ComboBox<String> statusCombo;
  @FXML
  private Button saveButton;
  
  private static final String[] STATUSES = {"Запланирован", "Посадка", "Вылетел", "Задержан", "Отменён"};
  
  public FlightFormController(FlightManager flightManager) {
    this.flightManager = flightManager;
  }
  
  @FXML
  public void initialize() {
    statusCombo.getItems().addAll(STATUSES);
  }
  
  public void setFlight(Flight flight) {
    this.flight = flight;
    flightNumberField.textProperty().bindBidirectional(flight.flightNumberProperty());
    destinationField.textProperty().bindBidirectional(flight.destinationProperty());
    departureDateField.valueProperty().bindBidirectional(flight.departureDateProperty());
    departureTimeField.textProperty().bindBidirectional(
       flight.departureTimeProperty(),
       new LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm"), null)
    );
    durationField.textProperty().bindBidirectional(
       flight.durationMinProperty().asObject(),
       new IntegerStringConverter()
    );
    gateField.textProperty().bindBidirectional(flight.gateProperty());
    
    if (flight.getStatus() != null && !flight.getStatus().isEmpty()) {
      statusCombo.setValue(flight.getStatus());
    } else {
      statusCombo.setValue("Запланирован");
    }
    statusCombo.valueProperty().addListener((obs, o, n) -> flight.setStatus(n));
  }
  
  public void setOnSaved(Runnable onSaved) {
    this.onSaved = onSaved;
  }
  
  @FXML
  private void handleSave() {
    try {
      flightManager.merge(flight);
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
