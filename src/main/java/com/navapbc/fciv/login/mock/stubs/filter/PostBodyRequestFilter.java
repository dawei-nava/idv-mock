package com.navapbc.fciv.login.mock.stubs.filter;

import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.extension.requestfilter.StubRequestFilter;
import com.github.tomakehurst.wiremock.http.Request;
import com.navapbc.fciv.login.mock.services.StorageService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostBodyRequestFilter extends StubRequestFilter {
  private static final String NAME = "com.navapbc.fciv.login.mock.stubs.filter.PostBodyRequestFilter";

  private final Pattern instanceIdPattern = Pattern.compile("/AssureIDService/Document/([^/]+)/Image.+");

  private final StorageService storageService;

  @Autowired
  public PostBodyRequestFilter(StorageService storageService) {
    this.storageService = storageService;
  }

  @Override
  public RequestFilterAction filter(Request request) {
//    if(!request.getMethod().isOneOf(RequestMethod.POST)) {
//      return RequestFilterAction.continueWith(request);
//    }
//    if(!request.queryParameter("side").isPresent() || !request.getUrl().matches("/AssureIDService/Document/[^/]+/Image.+")) {
//      LOGGER.debug("request does not matching image post request");
//      return RequestFilterAction.continueWith(request);
//    }
//    LOGGER.debug("request is an image upload request");
//    try {
//      LOGGER.debug("Request is image upload");
//      String side = request.queryParameter("side").firstValue();
//      String instanceId = getInstanceId(request.getUrl());
//      byte[] body = request.getBody();
//      LOGGER.debug("Saving side {} of instance {}", side, instanceId);
//      String fileName = instanceId+ "-"+side+".data";
//      storageService.store(fileName,  body);
//      LOGGER.debug("Posted image {} saved", fileName);
//    } catch (Exception e) {
//      LOGGER.error("Failed to save image", e);
//    }
    return RequestFilterAction.continueWith(request);
  }

  @Override
  public String getName() {
    return NAME ;
  }


  protected String getInstanceId(String url) {
    LOGGER.debug("Getting instance id from url {}",url);
    Matcher m = instanceIdPattern.matcher(url);
    m.find();
    return m.group(1);
  }
}
