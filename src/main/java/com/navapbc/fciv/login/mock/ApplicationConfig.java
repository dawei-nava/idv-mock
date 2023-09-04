package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.store.Store;
import com.navapbc.fciv.login.mock.services.acuant.scenarios.AcuantResponseTransformerFactory;
import com.navapbc.fciv.login.mock.services.acuant.scenarios.ResponseTransformer;
import com.navapbc.fciv.login.mock.stubs.extensions.DefaultStateExtension;
import com.navapbc.fciv.login.mock.util.EnableWireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.wiremock.extensions.state.CaffeineStore;
import org.wiremock.extensions.state.internal.ContextManager;

@Slf4j
@Configuration
@EnableConfigurationProperties
@EnableWireMockConfiguration
@ComponentScan(
    basePackages = {"com.navapbc.fciv.login.mock.services", "com.navapbc.fciv.login.mock.stubs","com.navapbc.fciv.login.mock.util" })
public class ApplicationConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  ResponseTransformer ognl(ApplicationContext context) {
    return AcuantResponseTransformerFactory.getInstance(context, "ognl");
  }


  @Bean
  Store stateStore() {
    return new CaffeineStore();
  }

  @Bean
  ContextManager contextManager(Store stateStore) {
    return new ContextManager(stateStore);
  }
  @Bean
  DefaultStateExtension defaultStateExtension(ContextManager contextManager) {
    DefaultStateExtension extension = new DefaultStateExtension(contextManager);
    return extension;
  }

//  @Bean
//  public Options defaultOptions() {
//    WireMockConfiguration options = WireMockConfiguration.options();
//    //options.extensions()
//  }

}
