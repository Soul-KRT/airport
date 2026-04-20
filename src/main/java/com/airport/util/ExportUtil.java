package com.airport.util;

import com.airport.model.Flight;
import com.airport.model.Passenger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportUtil {
  
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
  
  public static File exportFlightReport(Flight flight, List<Passenger> passengers, File targetDir) throws IOException {
    targetDir.mkdirs();
    String filename = "flight_" + flight.getFlightNumber().replace("/", "-") + "_"
                      + flight.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
    File file = new File(targetDir, filename);
    
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      writer.write("=== ИНФОРМАЦИОННАЯ СИСТЕМА АЭРОПОРТА ===");
      writer.newLine();
      writer.write("ДАННЫЕ О РЕЙСЕ");
      writer.newLine();
      writer.write("-----------------------------------");
      writer.newLine();
      writer.write("Номер рейса:    " + flight.getFlightNumber());
      writer.newLine();
      writer.write("Направление:    " + flight.getDestination());
      writer.newLine();
      writer.write("Дата вылета:    " + flight.getDepartureDate().format(DATE_FMT));
      writer.newLine();
      writer.write("Время вылета:   " + flight.getDepartureTime().format(TIME_FMT));
      writer.newLine();
      writer.write("Длительность:   " + flight.getDurationMin() + " мин.");
      writer.newLine();
      writer.write("Гейт:           " + flight.getGate());
      writer.newLine();
      writer.write("Статус:         " + flight.getStatus());
      writer.newLine();
      writer.write("-----------------------------------");
      writer.newLine();
      writer.write("СПИСОК ПАССАЖИРОВ (" + passengers.size() + " чел.):");
      writer.newLine();
      writer.write("-----------------------------------");
      writer.newLine();
      
      if (passengers.isEmpty()) {
        writer.write("Пассажиры не зарегистрированы");
        writer.newLine();
      } else {
        for (int i = 0; i < passengers.size(); i++) {
          Passenger p = passengers.get(i);
          writer.write((i + 1) + ". " + p.getFullName()
                       + "  |  Билет: " + p.getTicketNumber()
                       + "  |  Место: " + p.getSeat()
                       + "  |  Паспорт: " + p.getPassportNumber());
          writer.newLine();
        }
      }
      writer.write("-----------------------------------");
      writer.newLine();
      writer.write("Экспорт выполнен системой ИС Аэропорта v1.0.0");
      writer.newLine();
    }
    
    return file;
  }
}
