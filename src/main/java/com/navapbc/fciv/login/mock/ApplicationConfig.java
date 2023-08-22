package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.navapbc.fciv.login.mock.stubs.filter.GetResultRequestRequestFilter;
import com.navapbc.fciv.login.mock.stubs.filter.PostBodyRequestFilter;
import com.navapbc.fciv.login.mock.stubs.matcher.ResultRequestMatcher;
import com.navapbc.fciv.login.mock.stubs.transformer.GetResultResponseDefinitionTransformer;
import com.navapbc.fciv.login.mock.util.EnableWireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties
@EnableWireMockConfiguration
@ComponentScan(
    basePackages = {"com.navapbc.fciv.login.mock.services", "com.navapbc.fciv.login.mock.stubs","com.navapbc.fciv.login.mock.util" })
public class ApplicationConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public WireMockConfigurationCustomizer optionsCustomizer(
      PostBodyRequestFilter postBodyRequestFilter,
      ResultRequestMatcher resultRequestMatcher,
      GetResultRequestRequestFilter getResultRequestRequestFilter,
      GetResultResponseDefinitionTransformer getResultResponseDefinitionTransformer
  ) {
    return (config) -> {
      LOGGER.debug("Customizing configuration");
      config.extensions(new ResponseTemplateTransformer(false),
          postBodyRequestFilter, resultRequestMatcher, getResultRequestRequestFilter, getResultResponseDefinitionTransformer);
      config.notifier(new Slf4jNotifier(true));
    };

  }
}
