package com.airport.controller;

import com.airport.model.Flight;
import com.airport.model.Passenger;
import com.airport.service.FlightManager;
import com.airport.service.PassengerManager;
import com.airport.util.ExportUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainWindowController {
  
  private final FlightManager flightManager;
  private final PassengerManager passengerManager;
  
  private ObservableList<Flight> flightObservable;
  private FilteredList<Flight> flightFiltered;
  private ObservableList<Passenger> passengerObservable;
  private FilteredList<Passenger> passengerFiltered;
  
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
  
  @FXML
  private TableView<Flight> flightTable;
  @FXML
  private TableColumn<Flight, Integer> colFlightId;
  @FXML
  private TableColumn<Flight, String> colFlightNumber;
  @FXML
  private TableColumn<Flight, String> colDestination;
  @FXML
  private TableColumn<Flight, String> colDate;
  @FXML
  private TableColumn<Flight, String> colTime;
  @FXML
  private TableColumn<Flight, Integer> colDuration;
  @FXML
  private TableColumn<Flight, String> colGate;
  @FXML
  private TableColumn<Flight, String> colStatus;
  @FXML
  private TextField flightSearchField;
  @FXML
  private DatePicker flightDatePicker;
  
  @FXML
  private TableView<Passenger> passengerTable;
  @FXML
  private TableColumn<Passenger, Integer> colPassId;
  @FXML
  private TableColumn<Passenger, String> colFullName;
  @FXML
  private TableColumn<Passenger, String> colTicket;
  @FXML
  private TableColumn<Passenger, String> colPassport;
  @FXML
  private TableColumn<Passenger, String> colSeat;
  @FXML
  private TableColumn<Passenger, String> colLinkedFlight;
  @FXML
  private TextField passengerSearchField;
  
  public MainWindowController(FlightManager flightManager, PassengerManager passengerManager) {
    this.flightManager = flightManager;
    this.passengerManager = passengerManager;
  }
  
  @FXML
  public void initialize() {
    setupFlightTable();
    setupPassengerTable();
    loadFlights();
    loadPassengers();
  }
  
  private void setupFlightTable() {
    colFlightId.setCellValueFactory(d -> d.getValue().flightIdProperty().asObject());
    colFlightNumber.setCellValueFactory(d -> d.getValue().flightNumberProperty());
    colDestination.setCellValueFactory(d -> d.getValue().destinationProperty());
    colDate.setCellValueFactory(d -> {
      LocalDateStringConverter c = new LocalDateStringConverter(d.getValue().getDepartureDate(), DATE_FMT);
      return c.property;
    });
    colTime.setCellValueFactory(d -> {
      LocalTimeStringConverter c = new LocalTimeStringConverter(d.getValue().getDepartureTime(), TIME_FMT);
      return c.property;
    });
    colDuration.setCellValueFactory(d -> d.getValue().durationMinProperty().asObject());
    colGate.setCellValueFactory(d -> d.getValue().gateProperty());
    colStatus.setCellValueFactory(d -> d.getValue().statusProperty());
    
    ContextMenu cm = new ContextMenu();
    MenuItem exportItem = new MenuItem("Экспорт рейса в файл");
    exportItem.setOnAction(e -> handleExportFlight());
    cm.getItems().add(exportItem);
    flightTable.setContextMenu(cm);
  }
  
  private void setupPassengerTable() {
    colPassId.setCellValueFactory(d -> d.getValue().passengerIdProperty().asObject());
    colFullName.setCellValueFactory(d -> d.getValue().fullNameProperty());
    colTicket.setCellValueFactory(d -> d.getValue().ticketNumberProperty());
    colPassport.setCellValueFactory(d -> d.getValue().passportNumberProperty());
    colSeat.setCellValueFactory(d -> d.getValue().seatProperty());
    colLinkedFlight.setCellValueFactory(d -> {
      int fid = d.getValue().getLinkedFlightId();
      String label = flightManager.getFlight(fid)
                                  .map(f -> f.getFlightNumber() + " (" + f.getDestination() + ")")
                                  .orElse("—");
      return new SimpleStringProperty(label);
    });
  }
  
  private void loadFlights() {
    flightObservable = FXCollections.observableArrayList(flightManager.getFlights());
    flightFiltered = new FilteredList<>(flightObservable, f -> true);
    flightTable.setItems(flightFiltered);
    flightSearchField.textProperty().addListener((obs, o, n) -> updateFlightFilter());
    if (flightDatePicker != null) {
      flightDatePicker.valueProperty().addListener((obs, o, n) -> updateFlightFilter());
    }
  }
  
  private void loadPassengers() {
    passengerObservable = FXCollections.observableArrayList(passengerManager.getPassengers());
    passengerFiltered = new FilteredList<>(passengerObservable, p -> true);
    passengerTable.setItems(passengerFiltered);
    passengerSearchField.textProperty().addListener((obs, o, n) -> updatePassengerFilter());
  }
  
  private void updateFlightFilter() {
    flightFiltered.setPredicate(flight -> {
      if (flightDatePicker != null && flightDatePicker.getValue() != null) {
        if (!flightDatePicker.getValue().equals(flight.getDepartureDate())) return false;
      }
      String search = flightSearchField.getText();
      if (search == null || search.trim().isEmpty()) return true;
      search = search.toLowerCase().trim();
      return flight.getFlightNumber().toLowerCase().contains(search)
             || flight.getDestination().toLowerCase().contains(search)
             || flight.getGate().toLowerCase().contains(search)
             || String.valueOf(flight.getFlightId()).contains(search);
    });
  }
  
  private void updatePassengerFilter() {
    passengerFiltered.setPredicate(passenger -> {
      String search = passengerSearchField.getText();
      if (search == null || search.trim().isEmpty()) return true;
      search = search.toLowerCase().trim();
      return passenger.getFullName().toLowerCase().contains(search)
             || passenger.getTicketNumber().toLowerCase().contains(search)
             || String.valueOf(passenger.getPassengerId()).contains(search);
    });
  }
  
  @FXML
  private void handleAddFlight() {
    openFlightForm(new Flight());
  }
  
  @FXML
  private void handleEditFlight() {
    Flight selected = flightTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showAlert(Alert.AlertType.WARNING, "Выберите рейс", "Пожалуйста, выберите рейс для редактирования.");
      return;
    }
    Flight copy = new Flight(selected.getFlightId(), selected.getFlightNumber(), selected.getDestination(),
                             selected.getDepartureDate(), selected.getDepartureTime(), selected.getDurationMin(),
                             selected.getGate(), selected.getStatus());
    openFlightForm(copy);
  }
  
  @FXML
  private void handleDeleteFlight() {
    Flight selected = flightTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showAlert(Alert.AlertType.WARNING, "Выберите рейс", "Пожалуйста, выберите рейс для удаления.");
      return;
    }
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить рейс " + selected.getFlightNumber() + "?\nВсе пассажиры рейса также будут удалены.", ButtonType.YES, ButtonType.NO);
    confirm.setTitle("Подтверждение");
    confirm.showAndWait().ifPresent(btn -> {
      if (btn == ButtonType.YES) {
        flightManager.removeFlight(selected);
        refreshFlights();
        refreshPassengers();
      }
    });
  }
  
  @FXML
  private void handleClearDateFilter() {
    if (flightDatePicker != null) flightDatePicker.setValue(null);
  }
  
  private void openFlightForm(Flight flight) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/flight-form.fxml"));
      loader.setControllerFactory(type -> new FlightFormController(flightManager));
      Parent root = loader.load();
      FlightFormController ctrl = loader.getController();
      ctrl.setFlight(flight);
      ctrl.setOnSaved(() -> {
        refreshFlights();
        refreshPassengers();
      });
      Stage stage = new Stage();
      stage.setTitle(flight.getFlightId() == 0 ? "Создание рейса" : "Редактирование рейса");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(new Scene(root));
      stage.showAndWait();
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть форму: " + e.getMessage());
    }
  }
  
  @FXML
  private void handleAddPassenger() {
    openPassengerForm(new Passenger());
  }
  
  @FXML
  private void handleEditPassenger() {
    Passenger selected = passengerTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showAlert(Alert.AlertType.WARNING, "Выберите пассажира", "Пожалуйста, выберите пассажира для редактирования.");
      return;
    }
    Passenger copy = new Passenger(selected.getPassengerId(), selected.getFullName(),
                                   selected.getTicketNumber(), selected.getPassportNumber(),
                                   selected.getLinkedFlightId(), selected.getSeat());
    openPassengerForm(copy);
  }
  
  @FXML
  private void handleDeletePassenger() {
    Passenger selected = passengerTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showAlert(Alert.AlertType.WARNING, "Выберите пассажира", "Пожалуйста, выберите пассажира для удаления.");
      return;
    }
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить пассажира " + selected.getFullName() + "?", ButtonType.YES, ButtonType.NO);
    confirm.setTitle("Подтверждение");
    confirm.showAndWait().ifPresent(btn -> {
      if (btn == ButtonType.YES) {
        passengerManager.removePassenger(selected);
        refreshPassengers();
      }
    });
  }
  
  private void openPassengerForm(Passenger passenger) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/passenger-form.fxml"));
      loader.setControllerFactory(type -> new PassengerFormController(passengerManager, flightManager));
      Parent root = loader.load();
      PassengerFormController ctrl = loader.getController();
      ctrl.setPassenger(passenger);
      ctrl.setOnSaved(this::refreshPassengers);
      Stage stage = new Stage();
      stage.setTitle(passenger.getPassengerId() == 0 ? "Регистрация пассажира" : "Редактирование пассажира");
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(new Scene(root));
      stage.showAndWait();
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть форму: " + e.getMessage());
    }
  }
  
  @FXML
  private void handleExportFlight() {
    Flight selected = flightTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showAlert(Alert.AlertType.WARNING, "Выберите рейс", "Выберите рейс для экспорта.");
      return;
    }
    DirectoryChooser dc = new DirectoryChooser();
    dc.setTitle("Выберите папку для сохранения");
    File dir = dc.showDialog(flightTable.getScene().getWindow());
    if (dir == null) return;
    try {
      List<Passenger> passengers = passengerManager.getPassengersByFlight(selected);
      File exported = ExportUtil.exportFlightReport(selected, passengers, dir);
      showAlert(Alert.AlertType.INFORMATION, "Экспорт выполнен",
                "Файл сохранён:\n" + exported.getAbsolutePath());
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Ошибка экспорта", e.getMessage());
    }
  }
  
  @FXML
  private void handleHelp() {
    Alert help = new Alert(Alert.AlertType.INFORMATION);
    help.setTitle("Справка");
    help.setHeaderText("Информационная система аэропорта v1.0.0");
    help.setContentText(
       "Вкладка «Рейсы»:\n" +
       "  – Добавить/Изменить/Удалить рейс\n" +
       "  – Фильтрация по дате и ключевому слову\n" +
       "  – ПКМ → Экспорт рейса в текстовый файл\n\n" +
       "Вкладка «Пассажиры»:\n" +
       "  – Добавить/Изменить/Удалить пассажира\n" +
       "  – Поиск по ФИО или номеру билета\n\n" +
       "Данные хранятся в папке data/ рядом с приложением."
    );
    help.setResizable(true);
    help.showAndWait();
  }
  
  private void refreshFlights() {
    flightObservable.setAll(flightManager.getFlights());
  }
  
  private void refreshPassengers() {
    passengerObservable.setAll(passengerManager.getPassengers());
  }
  
  public static void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.setResizable(true);
    alert.showAndWait();
  }
  
  private static class LocalDateStringConverter {
    final SimpleStringProperty property;
    
    LocalDateStringConverter(java.time.LocalDate date, DateTimeFormatter fmt) {
      property = new SimpleStringProperty(date != null ? date.format(fmt) : "");
    }
  }
  
  private static class LocalTimeStringConverter {
    final SimpleStringProperty property;
    
    LocalTimeStringConverter(java.time.LocalTime time, DateTimeFormatter fmt) {
      property = new SimpleStringProperty(time != null ? time.format(fmt) : "");
    }
  }
}
