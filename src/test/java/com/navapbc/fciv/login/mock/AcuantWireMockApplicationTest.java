package com.navapbc.fciv.login.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AcuantWireMockApplication.class}, properties = "app.baseUrl=http://localhost:6063", webEnvironment = WebEnvironment.NONE)
@AutoConfigureWireMock()
public class AcuantWireMockApplicationTest {

  @Autowired
  private RestTemplate restTemplate;
  @Test
  public void contextLoads() throws Exception {
    stubFor(get(urlEqualTo("/resource")).willReturn(aResponse()
        .withHeader("Content-Type", "text/plain").withBody("Hello World!")));
    System.out.println(restTemplate.toString());
  }
}
