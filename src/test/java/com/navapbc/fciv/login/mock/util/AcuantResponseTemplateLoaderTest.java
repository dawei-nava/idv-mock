package com.navapbc.fciv.login.mock.util;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

class AcuantResponseTemplateLoaderTest {

  private AcuantResponseTemplateLoader loader =
      new AcuantResponseTemplateLoader(new DefaultResourceLoader(), new ObjectMapper());

  @BeforeEach
  void setUp() throws Exception {
    ReflectionTestUtils.setField(loader, "templateFile", "/__files/acuant/state_id/get_results_response_success.json");
    loader.afterPropertiesSet();
  }

  @Test
  void template() {
    AcuantResponse result = loader.getTemplate();
    Assertions.assertNotNull(result);
  }
}
