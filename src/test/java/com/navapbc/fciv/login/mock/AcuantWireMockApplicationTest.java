package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = "app.baseUrl=http://localhost:6063", webEnvironment = WebEnvironment.NONE)
@AutoConfigureWireMock
public class AcuantWireMockApplicationTest {

  @Autowired
  private RestTemplate restTemplate;


  @Autowired
  WireMockServer wireMockServer;
  @Test
  public void contextLoads() throws Exception {

    System.out.println(restTemplate.toString());
    System.out.println(wireMockServer==null ? "########null": wireMockServer.toString());
  }
}
