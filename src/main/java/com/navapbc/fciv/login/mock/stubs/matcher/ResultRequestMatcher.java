package com.navapbc.fciv.login.mock.stubs.matcher;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResultRequestMatcher extends RequestMatcherExtension {
  private final Pattern urlPattern = Pattern.compile("AssureIDService/Document/([^/]+)");

  @Override
  public String getName() {
    return "result-request-matcher";
  }

  @Override
  public MatchResult match(Request request, Parameters parameters) {
    String url = request.getUrl();
    String method = request.getMethod().toString();
    String instanceId = instanceId(url);
    boolean matched= StringUtils.isNotBlank(instanceId) && "Get".equalsIgnoreCase(method);
    LOGGER.debug("Request matcher matched  = {}", matched);
    LOGGER.debug("Result request matched instance id {}", instanceId);
    if(matched) {
      return MatchResult.exactMatch();
    }else {
      return MatchResult.noMatch();
    }
  }

  private String instanceId(String url) {
    Matcher m = urlPattern.matcher(url);
    m.find();
    return m.group(1);
  }
}
