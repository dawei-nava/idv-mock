package com.navapbc.fciv.login.mock.model.acuant;

import java.util.HashMap;
import java.util.Map;

public enum ResultCode {
  SUCCESS(1),
  FAILURE(0),
  UNKNOWN(2);

  @SuppressWarnings("java:S116x")
  public final int Value;

  private ResultCode(int value) {
    Value = value;
  }

  // Mapping difficulty to difficulty id
  private static final Map<Integer, ResultCode> _map = new HashMap<>();

  static {
    for (ResultCode code : ResultCode.values())
      _map.put(code.Value, code);
  }

  public static ResultCode from(int value) {
    return _map.get(value);
  }
}
