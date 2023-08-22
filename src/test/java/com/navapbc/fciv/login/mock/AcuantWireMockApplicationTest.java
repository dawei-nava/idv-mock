package com.navapbc.fciv.login.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.util.AcuantImageUploadUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(properties = "app.baseUrl=http://localhost:6363", webEnvironment = WebEnvironment.NONE)
public class AcuantWireMockApplicationTest {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper mapper;


  @Value("${app.baseUrl}")
  private String baseUrl;

  @Autowired
  private WireMockServer wireMockServer;

  @Autowired
  private AcuantImageUploadUtil imageUploadUtil;

  @BeforeEach
  void resetState() {
    String createInstanceUrl = baseUrl+"/__admin/scenarios/default/state";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<String>("{\"state\": \"Started\"}", headers);
    restTemplate.put(createInstanceUrl, request);
  }

  @Test
  void contextLoads() throws Exception {
    Assertions.assertNotNull(wireMockServer);
  }

  @Test
  void testExpiredResponse() throws Exception{
    String createInstanceUrl = baseUrl+"/AssureIDService/Document/Instance";
    String ognlExpression = "#this.alerts.{? #this.key==\"2D Barcode Content\"}[0].result=5";

    AcuantResponse response = imageUploadUtil.uploadAndGetResponse(baseUrl, ognlExpression);

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
  void testUnknowDocTypeResponse() throws Exception {
    int errResult = 2;
    String ognlExpression = "#action = #this.alerts.{? #this.key==\"Document Classification\"}[0], #this.alerts.clear, #action.result=2, #this.alerts.add(#action)";
    AcuantResponse response = imageUploadUtil.uploadAndGetResponse(baseUrl, ognlExpression);
    Assertions.assertEquals(1,response.getAlerts().size());
    Assertions.assertEquals(2, response.getAlerts().get(0).getResult());
  }

}
