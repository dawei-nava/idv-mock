package com.navapbc.fciv.login.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;


@SpringBootApplication
@EnableConfigurationProperties
public class AcuantWireMockApplication {
  public static void main(String[] args) {
    SpringApplication.run(AcuantWireMockApplication.class, args);
  }



//  @Bean
//  public Options wireMockOptions() throws IOException {
//
//    final WireMockConfiguration options = WireMockSpring.options();
//    options.port(6363);
//
//    return options;
//  }

}


