package com.airport.util;

import java.util.List;

public class Utils {
  public static String toString(List<?> list, String prefix, String delimiter, boolean wrapLines) {
    if (list == null || list.isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      if (wrapLines) sb.append(prefix);
      sb.append(list.get(i).toString());
      if (i < list.size() - 1) sb.append(delimiter);
    }
    return sb.toString();
  }
}
