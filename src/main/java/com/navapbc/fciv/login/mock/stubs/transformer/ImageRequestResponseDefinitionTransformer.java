package com.navapbc.fciv.login.mock.stubs.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.navapbc.fciv.login.mock.model.acuant.AssureIDMockRequestImage;
import com.navapbc.fciv.login.mock.model.acuant.ImagePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageRequestResponseDefinitionTransformer implements ResponseDefinitionTransformerV2 {

  public static String NAME = "acuant-image-upload-transformer";

  @Autowired ObjectMapper mapper;

  @Override
  public ResponseDefinition transform(ServeEvent serveEvent) {
    Parameters params = serveEvent.getTransformerParameters();
    String side = (String) params.get("side");
    LOGGER.debug("Processing side: {}", side);
    Request request = serveEvent.getRequest();
    String body = request.getBodyAsString();
    AssureIDMockRequestImage requestImage = null;
    try {
      requestImage = mapper.readValue(body, AssureIDMockRequestImage.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    ImagePayload imagePayload = null;
    if ("front".equalsIgnoreCase(side)) {
      imagePayload = requestImage.getFront();
    } else if ("back".equalsIgnoreCase(side)) {
      imagePayload = requestImage.getBack();
    }

    int status = imagePayload.getHttpStatus();
    HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(status == 0 ? 200 : status);
    int delay = imagePayload.getFixedDelays();
    LOGGER.info("{} image request status: {} with delay {}", side, status, delay);

    return new ResponseDefinitionBuilder()
        .withStatus(httpStatusCode.value())
        .withFixedDelay(delay < 0 ? 0 : delay)
        .build();
  }

  @Override
  public boolean applyGlobally() {
    return false;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
