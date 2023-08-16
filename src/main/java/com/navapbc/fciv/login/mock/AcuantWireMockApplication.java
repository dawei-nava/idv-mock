package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@AutoConfigureWireMock()
public class AcuantWireMockApplication{
  public static void main(String[] args) {
    SpringApplication.run(AcuantWireMockApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate t =  new RestTemplate();
    System.out.println(t.toString());
    return t;
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


