package com.navapbc.fciv.login.mock.stubs.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestWrapper;
import com.github.tomakehurst.wiremock.extension.requestfilter.StubRequestFilter;
import com.github.tomakehurst.wiremock.http.Request;
import com.navapbc.fciv.login.mock.services.StorageService;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** populate required data from previous upload request so we can render response conditionally. */
@Component
@Slf4j
public class GetResultRequestRequestFilter extends StubRequestFilter {

  private StorageService storageService;
  private ObjectMapper mapper;

  private Pattern pattern = Pattern.compile("/AssureIDService/Document/([^/].+)");


  @Autowired
  public GetResultRequestRequestFilter(StorageService storageService, ObjectMapper mapper) {
    this.storageService = storageService;
    this.mapper = mapper;
  }

  @Override
  public RequestFilterAction filter(Request request) {
    try {
      String url = request.getUrl();
      String instanceId = getInstanceId(url);
      String ognlExpression = getImagePayload(instanceId).getOnglExpression();
      Request newRequest =
          RequestWrapper.create()
              .transformHeader(
                  "X-Ognl-Expression", exitingValue -> Collections.singletonList(ognlExpression))
              .wrap(request);

      return RequestFilterAction.continueWith(newRequest);
    } catch (Exception e) {
      LOGGER.debug("Error filtering request: {}", e.getMessage());
    }
    return RequestFilterAction.continueWith(request);
  }

  @Override
  public String getName() {
    return "get-result-request-header-appender";
  }


  private String getInstanceId(String url) {
    Matcher m = pattern.matcher(url);
    return m.group();
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


