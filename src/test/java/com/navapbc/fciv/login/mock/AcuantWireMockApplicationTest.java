package com.navapbc.fciv.login.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "app.baseUrl=http://localhost:6063", webEnvironment = WebEnvironment.NONE)
@AutoConfigureWireMock(port = 6063)
public class AcuantWireMockApplicationTest {

  @Autowired
  private Service service;

  @Test
  public void contextLoads() throws Exception {
    stubFor(get(urlEqualTo("/resource")).willReturn(aResponse()
        .withHeader("Content-Type", "text/plain").withBody("Hello World!")));
    assertThat(this.service.go()).isEqualTo("Hello World!");
  }
}
