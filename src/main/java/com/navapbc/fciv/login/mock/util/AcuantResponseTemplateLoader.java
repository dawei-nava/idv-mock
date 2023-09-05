package com.navapbc.fciv.login.mock.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AcuantResponseTemplateLoader implements InitializingBean {

  @Value("${assureid.result.template:/__files/acuant/state_id/get_results_response_success.json}")
  String templateFile;

  private ObjectMapper mapper;
  private ResourceLoader resourceLoader;
  private String template;

  @Autowired
  public AcuantResponseTemplateLoader(ResourceLoader resourceLoader , ObjectMapper mapper) {
    this.mapper = mapper;
    this.resourceLoader = resourceLoader;
  }



  protected String load() {
    LOGGER.debug("Using template file path: {}", templateFile);
    InputStream inputStream = null;
    try {
      inputStream = resourceLoader.getResource(templateFile).getInputStream();
      List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
      template= StringUtils.join(lines, "\n");
      LOGGER.trace("Loaded template: {}", template);
      inputStream.close();
    } catch (IOException e) {
      LOGGER.debug("Cannot read response template file {}: {}", templateFile, e.getMessage());
    }finally{
      if(inputStream!=null) {
        try {
          inputStream.close();
        }catch (Exception e) {
          // blank
        }
      }
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
