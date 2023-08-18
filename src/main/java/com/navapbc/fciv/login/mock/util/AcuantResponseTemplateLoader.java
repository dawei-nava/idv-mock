package com.navapbc.fciv.login.mock.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AcuantResponseTemplateLoader {

  public AcuantResponse load() {
    AcuantResponse template = null;
    String fileName="/files/acuant/state_id/get_results_response_success.json";
    try {
      ObjectMapper mapper = new ObjectMapper();
      File input =
          new ClassPathResource(
                  fileName, this.getClass())
              .getFile();
      template = mapper.readValue(input, AcuantResponse.class);

    } catch (IOException e) {
      LOGGER.debug("Cannot read response template file {}: {}", fileName, e.getMessage());
    }
    return template;
  }

}
