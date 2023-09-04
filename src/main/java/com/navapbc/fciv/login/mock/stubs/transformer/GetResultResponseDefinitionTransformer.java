package com.navapbc.fciv.login.mock.stubs.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.model.acuant.ImagePayload;
import com.navapbc.fciv.login.mock.services.acuant.scenarios.ResponseTransformer;
import com.navapbc.fciv.login.mock.stubs.extensions.DefaultStateExtension;
import com.navapbc.fciv.login.mock.util.AcuantResponseTemplateLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.wiremock.extensions.state.internal.ContextManager;

@Slf4j
@Component
public class GetResultResponseDefinitionTransformer implements ResponseDefinitionTransformerV2 {

  @Autowired private AcuantResponseTemplateLoader loader;

  @Autowired private ObjectMapper mapper;

  @Autowired
  @Qualifier("ognl")
  private ResponseTransformer transformer;

  @Autowired private DefaultStateExtension defaultStateExtension;

  @Autowired private ContextManager contextManager;

  private Pattern pattern = Pattern.compile("/AssureIDService/Document/([^/]+)");

  @Override
  public ResponseDefinition transform(ServeEvent serveEvent) {
    Request request = serveEvent.getRequest();
    String frontImage =
        (String) this.contextManager.getState(getInstanceId(request.getUrl()), "front_image");
    LOGGER.debug("front Image: {}", frontImage);
    try {
      ImagePayload imagePayload = mapper.readValue(frontImage, ImagePayload.class);
      int fixedDelays = imagePayload.getFixedDelays();
      int status = imagePayload.getHttpStatus();
      HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(status ==0 ? 200 : status);
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
      AcuantResponse template = loader.getTemplate();
      try {
        Map<String, Object> transformerContext = new HashMap<>();
        transformerContext.put("ognlExpression", ognlExpression);
        AcuantResponse result = transformer.transform(template, transformerContext);
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
    }
    return new ResponseDefinitionBuilder().withStatus(500).build();
  }

  @Override
  public boolean applyGlobally() {
    return false;
  }

  @Override
  public String getName() {
    return "get-result-response-definition-transformer";
  }

  private String getInstanceId(String url) {
    LOGGER.debug("Url:### {}", url);
    Matcher m = pattern.matcher(url);
    m.find();
    return m.group(1);
  }
}
