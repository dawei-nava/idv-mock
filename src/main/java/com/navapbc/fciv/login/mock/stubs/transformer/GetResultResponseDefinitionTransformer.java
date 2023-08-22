package com.navapbc.fciv.login.mock.stubs.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.services.acuant.scenarios.AcuantResponseTransformerFactory;
import com.navapbc.fciv.login.mock.services.acuant.scenarios.ResponseTransformer;
import com.navapbc.fciv.login.mock.util.AcuantResponseTemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GetResultResponseDefinitionTransformer extends ResponseDefinitionTransformer {

  private AcuantResponseTemplateLoader loader;
  private ApplicationContext context;

  private ObjectMapper mapper;

  @Autowired
  public GetResultResponseDefinitionTransformer(
      AcuantResponseTemplateLoader loader, ApplicationContext context, ObjectMapper mapper) {
    this.loader = loader;
    this.context = context;
    this.mapper = mapper;
  }

  @Override
  public boolean applyGlobally() {
    return false;
  }

  @Override
  public ResponseDefinition transform(
      Request request,
      ResponseDefinition responseDefinition,
      FileSource fileSource,
      Parameters parameters) {
    String ognlExpression = request.getHeader("X-Ognl-Expression");
    LOGGER.debug("OGNL expression from request header : {}", ognlExpression);
    ResponseTransformer transformer = AcuantResponseTransformerFactory.getInstance(context, "ognl");
    if (transformer != null) {
      AcuantResponse template = loader.getTemplate();
      try {
        LOGGER.trace("Template: {}",mapper.writeValueAsString(template));
      } catch (JsonProcessingException e) {
        LOGGER.warn("Error to serialize template: {}", e.getMessage());
      }
      try {
        Map<String, Object> transformerContext = new HashMap<>();
        transformerContext.put("ognlExpression", ognlExpression);
        AcuantResponse result = transformer.transform(template, transformerContext);
        return new ResponseDefinitionBuilder()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(mapper.writeValueAsString(result))
            .build();
      } catch (Exception e) {
        LOGGER.debug("Exception wile transform template response: {}", e.getMessage());
        return new ResponseDefinitionBuilder().withStatus(500).build();
      }
    } else {
      return responseDefinition;
    }
  }

  @Override
  public String getName() {
    return "get-result-response-definition-transformer";
  }
}
