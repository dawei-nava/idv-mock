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
public class DocAuthWireMockApplication implements ApplicationListener<ContextRefreshedEvent> {
  public static void main(String[] args) {
    System.setProperty("java.util.logging.config.file", "jul.properties");

    SpringApplication.run(DocAuthWireMockApplication.class, args);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    LOGGER.debug("Initialized context");
  }
}
