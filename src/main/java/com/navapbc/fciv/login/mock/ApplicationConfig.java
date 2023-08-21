package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.navapbc.fciv.login.mock.stubs.filter.PostBodyRequestFilter;
import com.navapbc.fciv.login.mock.util.EnableWireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties
@EnableWireMockConfiguration
@ComponentScan(
    basePackages = {"com.navapbc.fciv.login.mock.services", "com.navapbc.fciv.login.mock.stubs","com.navapbc.fciv.login.mock.util" })
public class ApplicationConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

  @Autowired private PostBodyRequestFilter postBodyRequestFilter;


  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public WireMockConfigurationCustomizer optionsCustomizer() {
    return new WireMockConfigurationCustomizer() {
      @Override
      public void customize(WireMockConfiguration config) {
        LOGGER.debug("Customizing configuration");
        config.extensions(new ResponseTemplateTransformer(false), postBodyRequestFilter);
      }
    };
  }
}
