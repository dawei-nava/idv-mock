package com.navapbc.fciv.login.mock.util;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AcuantResponseTemplateLoaderTest {

  private AcuantResponseTemplateLoader loader =
      new AcuantResponseTemplateLoader(new ObjectMapper());

  @Test
  void template() {
    AcuantResponse result = loader.getTemplate();
    Assertions.assertNotNull(result);
  }
}
