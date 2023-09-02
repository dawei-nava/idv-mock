package com.navapbc.fciv.login.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockApp;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.util.AcuantImageUploadUtil;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = DocAuthWireMockApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class AcuantWireMockApplicationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper mapper;


  @LocalServerPort
  int randomServerPort;


  @Autowired
  private AcuantImageUploadUtil imageUploadUtil;

  @Autowired
  WebApplicationContext webApplicationContext;

  @BeforeEach
  void resetState() {
    ServletContext servletContext = webApplicationContext.getServletContext();
    WireMockApp wireMockApp = (WireMockApp) servletContext.getAttribute("WireMockApp");
    wireMockApp.resetScenario("default");
  }

  @Test
  void contextLoads() throws Exception {
  }

  @Test
  void testExpiredResponse() throws Exception{
    String ognlExpression = "#this.alerts.{? #this.key==\"2D Barcode Content\"}[0].result=5";

    AcuantResponse response = imageUploadUtil.uploadAndGetResponse("http://localhost:"+randomServerPort, ognlExpression);

    Assertions.assertNotNull(response, "Null object returned");
    int result =
        response.getAlerts().stream()
            .filter(a -> a.getKey().equals("2D Barcode Content"))
            .findFirst()
            .get()
            .getResult();
    Assertions.assertEquals(5, result);
  }

  @Test
  void testUnknownDocTypeResponse() throws Exception {
    String ognlExpression = "#action = #this.alerts.{? #this.key==\"Document Classification\"}[0], #this.alerts.clear, #action.result=2, #this.alerts.add(#action)";
    AcuantResponse response = imageUploadUtil.uploadAndGetResponse("http://localhost:"+randomServerPort, ognlExpression);
    Assertions.assertEquals(1,response.getAlerts().size());
    Assertions.assertEquals(2, response.getAlerts().get(0).getResult());
  }

}
