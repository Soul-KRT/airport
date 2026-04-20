module com.airport {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.airport to javafx.fxml;
    opens com.airport.controller to javafx.fxml;
    opens com.airport.model to com.fasterxml.jackson.databind, javafx.base;

    exports com.airport;
    exports com.airport.controller;
    exports com.airport.model;
    exports com.airport.service;
    exports com.airport.repository;
    exports com.airport.util;
    exports com.airport.exception;
}
