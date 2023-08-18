package com.navapbc.fciv.login.mock.util;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wiremock.org.checkerframework.checker.units.qual.A;

import static org.junit.jupiter.api.Assertions.*;

class AcuantResponseTemplateLoaderTest {
  private AcuantResponseTemplateLoader loader = new AcuantResponseTemplateLoader();
  @Test
  void load() {
    AcuantResponse result = loader.load();
    Assertions.assertNotNull(result);
  }
}
