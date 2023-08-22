package com.navapbc.fciv.login.mock.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AcuantResponseTemplateLoader implements InitializingBean {

  private ObjectMapper mapper;
  private String template;

  @Autowired
  public AcuantResponseTemplateLoader(ObjectMapper mapper) {
    this.mapper = mapper;
  }



  protected String load() {
    String fileName="/files/acuant/state_id/get_results_response_success.json";
    LOGGER.debug("Using template file path: {}", fileName);
    try {
      File input =
          new ClassPathResource(
                  fileName, this.getClass())
              .getFile();
      template = FileUtils.readFileToString(input);

    } catch (IOException e) {
      LOGGER.debug("Cannot read response template file {}: {}", fileName, e.getMessage());
    }
    return template;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    LOGGER.debug("Loading template");
    if(this.template==null) {
      this.template = load();
    }
  }

  public AcuantResponse getTemplate() {
    AcuantResponse result = null;
    try {
      result = mapper.readValue(this.template, AcuantResponse.class);
    } catch (JsonProcessingException e) {
      LOGGER.debug("Cannot parse template {}", e.getMessage());
    }
    return result;
  }
}
