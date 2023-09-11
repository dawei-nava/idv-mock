package com.navapbc.fciv.login.mock.stubs.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.store.files.FileSourceBlobStore;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.navapbc.fciv.login.mock.model.DocAuthResponse;
import com.navapbc.fciv.login.mock.model.acuant.ImagePayload;
import com.navapbc.fciv.login.mock.services.acuant.scenarios.ResponseTransformer;
import com.navapbc.fciv.login.mock.util.SpringContext;
import com.navapbc.fciv.login.trueid.TrueIDResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrueIDResponseDefinitionTransformer implements ResponseDefinitionTransformerV2 {

  @Autowired private ObjectMapper mapper;

  @Autowired
  @Qualifier("ognlTransformer")
  private ResponseTransformer transformer;

  @Override
  public ResponseDefinition transform(ServeEvent serveEvent) {
    Request request = serveEvent.getRequest();
    LOGGER.debug("Body file name: {}", serveEvent.getResponseDefinition().getBodyFileName());
    String bodyFile = serveEvent.getResponseDefinition().getBodyFileName();
    WireMockServer wireMockServer = SpringContext.getBean(WireMockServer.class);
    Optional<byte[]> templateData =
        ((FileSourceBlobStore) wireMockServer.getOptions().getStores().getFilesBlobStore())
            .get(bodyFile);
    if (!templateData.isPresent()) {
      return new ResponseDefinitionBuilder().withStatus(500).build();
    }
    try {
      String templateContent = new String(templateData.get());
      TrueIDResponse template = mapper.readValue(templateContent, TrueIDResponse.class);
      ImagePayload imagePayload = mapper.readValue(request.getBody(), ImagePayload.class);
      int fixedDelays = imagePayload.getFixedDelays();
      int status = imagePayload.getHttpStatus();
      HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(status == 0 ? 200 : status);
      if (httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()) {
        LOGGER.info("Return with status: {}", status);
        return new ResponseDefinitionBuilder()
            .withHeader("Content-Type", "application/json")
            .withFixedDelay(fixedDelays <= 0 ? null : Integer.valueOf(fixedDelays))
            .withStatus(status)
            .build();
      }
      String ognlExpression = imagePayload.getOnglExpression();
      LOGGER.debug("OGNL expression state: {}", ognlExpression);
      try {
        Map<String, Object> transformerContext = new HashMap<>();
        transformerContext.put("ognlExpression", ognlExpression);
        DocAuthResponse result = transformer.transform(template, transformerContext);
        return new ResponseDefinitionBuilder()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withFixedDelay(fixedDelays <= 0 ? null : Integer.valueOf(fixedDelays))
            .withBody(mapper.writeValueAsString(result))
            .build();
      } catch (Exception e) {
        LOGGER.debug("Exception wile transform template response: {}", e.getMessage());
        return new ResponseDefinitionBuilder().withStatus(500).build();
      }
    } catch (JsonMappingException e) {
      LOGGER.error("JSON mapping error: {}", e.getMessage());
    } catch (JsonProcessingException e) {
      LOGGER.error("JSON processing error: {}", e.getMessage());
    } catch (IOException e) {
      LOGGER.error("IO error: {}", e.getMessage());
    }
    return new ResponseDefinitionBuilder().withStatus(500).build();
  }

  @Override
  public String getName() {
    return "true-id-response-transformer";
  }

  @Override
  public boolean applyGlobally() {
    return false;
  }
}
