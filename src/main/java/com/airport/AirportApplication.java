package com.airport;

import com.airport.controller.ControllerFactory;
import com.airport.repository.FlightRepository;
import com.airport.repository.PassengerRepository;
import com.airport.service.FlightManager;
import com.airport.service.PassengerManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.Arrays;

public class AirportApplication extends Application {
  
  private FlightManager flightManager;
  private PassengerManager passengerManager;
  
  @Override
  public void init() {
    FlightRepository flightRepository = new FlightRepository();
    PassengerRepository passengerRepository = new PassengerRepository();
    flightManager = new FlightManager(flightRepository, passengerRepository);
    passengerManager = new PassengerManager(passengerRepository, flightRepository);
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    setDefaultExceptionHandler();
    
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
    loader.setControllerFactory(new ControllerFactory(flightManager, passengerManager));
    Parent root = loader.load();
    
    Scene scene = new Scene(root, 1000, 650);
    scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
    
    primaryStage.setTitle("Информационная система аэропорта v1.0.0");
    primaryStage.setScene(scene);
    primaryStage.setMinWidth(800);
    primaryStage.setMinHeight(600);
    primaryStage.show();
  }
  
  private void setDefaultExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
                                                 Platform.runLater(() -> showErrorDialog(throwable)));
  }
  
  private void showErrorDialog(Throwable throwable) {
    Throwable root = getRootCause(throwable);
    boolean isAppException = root instanceof com.airport.exception.ApplicationException;
    Alert alert = new Alert(isAppException ? Alert.AlertType.WARNING : Alert.AlertType.ERROR);
    alert.setResizable(true);
    alert.setTitle(isAppException ? "Предупреждение" : throwable.getClass().getSimpleName());
    alert.setHeaderText(null);
    String stackTrace = isAppException ? "" : "\n\n" + Arrays.stream(root.getStackTrace())
                                                             .filter(x -> x.getClassName().startsWith("com.airport"))
                                                             .map(StackTraceElement::toString)
                                                             .reduce("", (a, b) -> a + "\n" + b);
    alert.setContentText(root.getMessage() + stackTrace);
    alert.showAndWait();
  }
  
  private Throwable getRootCause(Throwable throwable) {
    while (throwable.getCause() != null) throwable = throwable.getCause();
    return throwable;
  }
  
  public static void main(String[] args) {
    launch(args);
  }
}
