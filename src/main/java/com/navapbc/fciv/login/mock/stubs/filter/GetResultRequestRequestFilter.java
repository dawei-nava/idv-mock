package com.navapbc.fciv.login.mock.stubs.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.extension.requestfilter.StubRequestFilter;
import com.github.tomakehurst.wiremock.http.Request;
import com.navapbc.fciv.login.mock.services.StorageService;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** populate required data from previous upload request so we can render response conditionally. */
@Component
@Slf4j
public class GetResultRequestRequestFilter extends StubRequestFilter {

  private StorageService storageService;
  private ObjectMapper mapper;

  private Pattern pattern = Pattern.compile("/AssureIDService/Document/([^/]+)");


  @Autowired
  public GetResultRequestRequestFilter(StorageService storageService, ObjectMapper mapper) {
    this.storageService = storageService;
    this.mapper = mapper;
  }

  @Override
  public RequestFilterAction filter(Request request) {
//    try {
//      String requestMethod = request.getMethod().toString();
//      LOGGER.debug("Request method = {}", requestMethod);
//      String url = request.getUrl();
//      LOGGER.debug("request : {}", url);
//      String instanceId = getInstanceId(url);
//      LOGGER.debug("Instance id: {}", instanceId);
//      if(!"Get".equalsIgnoreCase(requestMethod) ||! StringUtils.isNotBlank(instanceId))  {
//        return RequestFilterAction.continueWith(request);
//      }
//      String ognlExpression = getImagePayload(instanceId).getOnglExpression();
//      LOGGER.debug("Set X-Ognl-Expression: {}", ognlExpression);
//      Request newRequest =
//          RequestWrapper.create()
//              .addHeader("X-Ognl-Expression", ognlExpression)
//              .wrap(request);
//      LOGGER.debug("New request header: {}", newRequest.getAllHeaderKeys());
//      return RequestFilterAction.continueWith(newRequest);
//    } catch (Exception e) {
//      LOGGER.debug("Error filtering request: {}", e);
//    }
    return RequestFilterAction.continueWith(request);
  }

  @Override
  public String getName() {
    return "get-result-request-header-appender";
  }


  private String getInstanceId(String url) {
    LOGGER.debug("Url:### ",url);
    Matcher m = pattern.matcher(url);
    m.find();
    return m.group(1);
  }

  private ImagePayload getImagePayload(String instanceId) throws IOException {
    String responseTemplateFile = null;
    String fileName = new StringBuilder().append(instanceId).append("-front.data").toString();
    Path imageFile = storageService.load(fileName);
    ImagePayload payload = mapper.readValue(imageFile.toFile(), ImagePayload.class);
    return payload;
  }


  @Setter
  @Getter
  public static class ImagePayload implements Serializable {

    @JsonProperty
    private String onglExpression;
  }
}


