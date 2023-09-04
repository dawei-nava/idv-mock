package com.navapbc.fciv.login.mock.stubs.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.model.acuant.ImageUpload;
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
import org.springframework.stereotype.Component;
import org.wiremock.extensions.state.internal.ContextManager;

@Slf4j
@Component
public class GetResultResponseDefinitionTransformer extends ResponseDefinitionTransformer {


  @Autowired
  private AcuantResponseTemplateLoader loader;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  @Qualifier("ognl")
  private ResponseTransformer transformer;

  @Autowired
  private DefaultStateExtension defaultStateExtension;

  @Autowired
  private ContextManager contextManager;

  private Pattern pattern = Pattern.compile("/AssureIDService/Document/([^/]+)");

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

    String frontImage =
        (String)
            this.contextManager.getState(getInstanceId(request.getUrl()), "front_image");
    LOGGER.debug("front Image: {}", frontImage);
    try {
      ImageUpload imageUpload = mapper.readValue(frontImage, ImageUpload.class);
      String ognlExpression = imageUpload.getOnglExpression();
      LOGGER.debug("OGNL expression state: {}", ognlExpression);
        AcuantResponse template = loader.getTemplate();
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
    } catch (JsonMappingException e) {
      LOGGER.error("JSON mapping error: {}", e.getMessage());
    } catch (JsonProcessingException e) {
      LOGGER.error("JSON processing error: {}", e.getMessage());
    }
    return new ResponseDefinitionBuilder().withStatus(500).build();
  }

    @Override
  public String getName() {
    return "get-result-response-definition-transformer";
  }

  private String getInstanceId(String url) {
    LOGGER.debug("Url:### {}",url);
    Matcher m = pattern.matcher(url);
    m.find();
    return m.group(1);
  }

}
