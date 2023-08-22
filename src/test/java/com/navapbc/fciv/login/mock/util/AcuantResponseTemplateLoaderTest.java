package com.navapbc.fciv.login.mock.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wiremock.org.checkerframework.checker.units.qual.A;

import static org.junit.jupiter.api.Assertions.*;


class AcuantResponseTemplateLoaderTest {

  private AcuantResponseTemplateLoader loader =
      new AcuantResponseTemplateLoader(new ObjectMapper());

  @Test
  void template() {
    AcuantResponse result = loader.getTemplate();
    Assertions.assertNotNull(result);
  }
}
