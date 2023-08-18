package com.navapbc.fciv.login.mock.stubs.matcher;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import wiremock.org.checkerframework.checker.units.qual.C;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResultRequestMatcher extends RequestMatcherExtension {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResultRequestMatcher.class);
  private final Pattern urlPattern = Pattern.compile("AssureIDService/Document/([^/]+)");

  @Override
  public String getName() {
    return "result-request-matcher";
  }

  @Override
  public MatchResult match(Request request, Parameters parameters) {
    String url = request.getUrl();
    String instanceId = instanceId(url);
    LOGGER.debug("Result request matched instance id {}", instanceId);
    return MatchResult.of(StringUtils.isNotBlank(instanceId));
  }

  private String instanceId(String url) {
    Matcher m = urlPattern.matcher(url);
    return m.group();
  }
}
