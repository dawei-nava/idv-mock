package com.navapbc.fciv.login.mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class AcuantWireMockApplication {
  public static void main(String[] args) {
    SpringApplication.run(AcuantWireMockApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}

@RestController
class Controller {

  private final Service service;

  Controller(Service service) {
    this.service = service;
  }

  @RequestMapping("/")
  public String home() {
    return this.service.go();
  }

}

@Component
class Service {

  private final RestTemplate restTemplate;

  @Value("${app.baseUrl:https://example.org}")
  private String base;

  Service(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String go() {
    return this.restTemplate.getForEntity(this.base + "/resource", String.class)
        .getBody();
  }

}


