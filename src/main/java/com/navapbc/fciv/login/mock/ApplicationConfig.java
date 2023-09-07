package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.store.Store;
import com.navapbc.fciv.login.mock.stubs.extensions.DefaultStateExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.wiremock.extensions.state.CaffeineStore;
import org.wiremock.extensions.state.internal.ContextManager;

@Slf4j
@Configuration
@EnableConfigurationProperties
@ComponentScan(
    basePackages = {
      "com.navapbc.fciv.login.mock.services",
      "com.navapbc.fciv.login.mock.stubs",
      "com.navapbc.fciv.login.mock.util",
      "com.navapbc.fciv.login.mock.spring"
    })
public class ApplicationConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  Store<String, Object> stateStore() {
    return new CaffeineStore();
  }

  @Bean
  ContextManager contextManager(Store<String, Object> stateStore) {
    return new ContextManager(stateStore);
  }

  @Bean
  DefaultStateExtension defaultStateExtension(ContextManager contextManager) {
    return new DefaultStateExtension(contextManager);
  }
}
