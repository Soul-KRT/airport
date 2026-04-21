package com.airport.repository;

import com.airport.util.Storeable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonStorage {
  private static final Path dataDir = resolveDataDir();
  private static final ObjectMapper mapper;
  
  static {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    dataDir.toFile().mkdirs();
  }
  
  private static Path resolveDataDir() {
    String os = System.getProperty("os.name", "").toLowerCase();
    if (os.contains("win")) {
      String localAppData = System.getenv("LOCALAPPDATA");
      if (localAppData != null && !localAppData.isBlank()) {
        return Paths.get(localAppData, "AirportIS", "data");
      }
      String userHome = System.getProperty("user.home", ".");
      return Paths.get(userHome, "AppData", "Local", "AirportIS", "data");
    }
    return Paths.get("data").toAbsolutePath().normalize();
  }
  
  public static <T extends Storeable> T load(String fileName, Class<T> type)
     throws InvocationTargetException, InstantiationException, IllegalAccessException {
    File src = dataDir.resolve(fileName).toFile();
    if (!src.exists()) return createDefault(type);
    try {
      return mapper.readValue(src, type);
    } catch (Exception e) {
      return createDefault(type);
    }
  }
  
  public static <T extends Storeable> void save(String fileName, T data) {
    File dest = dataDir.resolve(fileName).toFile();
    try {
      Files.createDirectories(dest.toPath().getParent());
      mapper.writeValue(dest, data);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка сохранения данных: " + e.getMessage(), e);
    }
  }
  
  private static <T extends Storeable> T createDefault(Class<T> type)
     throws InstantiationException, IllegalAccessException, InvocationTargetException {
    Constructor<?> constructor = type.getConstructors()[0];
    constructor.setAccessible(true);
    return type.cast(constructor.newInstance());
  }
}
