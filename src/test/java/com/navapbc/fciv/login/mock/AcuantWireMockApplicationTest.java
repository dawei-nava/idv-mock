package com.navapbc.fciv.login.mock;

import static org.junit.jupiter.api.Assertions.*;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.util.AcuantImageUploadUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
class AcuantWireMockApplicationTest {


  @Value("${wiremock.server.port:8080}")
  int serverPort;


  @Autowired
  private AcuantImageUploadUtil imageUploadUtil;


  @Test
  void contextLoads() {
    assertNotNull(imageUploadUtil);
  }

  @Test
  void testExpiredResponse() throws Exception{
    String ognlExpression = "#this.alerts.{? #this.key==\"2D Barcode Content\"}[0].result=5";

    AcuantResponse response = imageUploadUtil.uploadAndGetResponse("http://localhost:"+ serverPort, ognlExpression);

    assertNotNull(response, "Null object returned");
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
    AcuantResponse response = imageUploadUtil.uploadAndGetResponse("http://localhost:"+ serverPort, ognlExpression);
    Assertions.assertEquals(1,response.getAlerts().size());
    Assertions.assertEquals(2, response.getAlerts().get(0).getResult());
  }

  @Test
  void testExceptions() {
    HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
     imageUploadUtil.uploadWithException("http://localhost:"+ serverPort, 440)
    );
    assertEquals(440, exception.getStatusCode().value());
  }
}
