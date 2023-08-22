package com.navapbc.fciv.login.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.stubs.filter.GetResultRequestRequestFilter;
import com.navapbc.fciv.login.mock.util.AcuantResponseTemplateLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = "app.baseUrl=http://localhost:6363", webEnvironment = WebEnvironment.NONE)
public class AcuantWireMockApplicationTest {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper mapper;


  @Value("${app.baseUrl}")
  private String baseUrl;

  @Autowired
  WireMockServer wireMockServer;
  @Test
  public void contextLoads() throws Exception {

    Assertions.assertNotNull(wireMockServer);
  }


  @Test
  public void testImageUpload() throws Exception{
    String createInstanceUrl = baseUrl+"/AssureIDService/Document/Instance";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<String>("", headers);
    String instanceId = restTemplate.postForObject(createInstanceUrl, request, String.class);
    Assertions.assertNotNull(instanceId);

    // post front side
    String postFrontImageUrl = baseUrl+"/AssureIDService/Document/"+instanceId+"/Image?side=front&light=0";
    GetResultRequestRequestFilter.ImagePayload payload = new GetResultRequestRequestFilter.ImagePayload();
    payload.setOnglExpression("#this.alerts.{? #this.key==\"2D Barcode Content\"}[0].result=-10");
    request =
          new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
    restTemplate.postForObject(postFrontImageUrl, request, String.class);
    // post back side
    String postBackImageUrl = baseUrl+"/AssureIDService/Document/"+instanceId+"/Image?side=back&light=0";
    request =
        new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
    restTemplate.postForObject(postBackImageUrl, request, String.class);

    String getResultsUrl = baseUrl+"/AssureIDService/Document/"+instanceId;
    AcuantResponse response = restTemplate.getForObject(getResultsUrl, AcuantResponse.class);

    Assertions.assertNotNull(response, "Null object returned");
    int result =
        response.getAlerts().stream()
            .filter(a -> a.getKey().equals("2D Barcode Content"))
            .findFirst()
            .get()
            .getResult();
    Assertions.assertEquals(-10, result);
  }
}
