package com.navapbc.fciv.login.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@SpringBootApplication
@EnableConfigurationProperties
@Slf4j
public class AcuantWireMockApplication implements ApplicationListener<ContextRefreshedEvent> {
  public static void main(String[] args) {
    SpringApplication.run(AcuantWireMockApplication.class, args);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    LOGGER.debug("Initialized context");
  }

}


