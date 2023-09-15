package com.navapbc.fciv.login.mock.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.model.acuant.ImagePayload;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AcuantImageUploadUtil {
  private RestTemplate restTemplate;
  private ObjectMapper mapper;

  @Autowired
  public AcuantImageUploadUtil(RestTemplate restTemplate, ObjectMapper mapper) {
    this.restTemplate = restTemplate;
    this.mapper = mapper;
  }

  public AcuantResponse uploadAndGetResponse(String baseUrl, String oglnExpression)
      throws JsonProcessingException {
    String createInstanceUrl = baseUrl + "/AssureIDService/Document/Instance";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<String>("", headers);
    String instanceId =
        restTemplate.postForObject(createInstanceUrl, request, String.class).replaceAll("\"", "");
    Assertions.assertNotNull(instanceId);

    // post front side
    String postFrontImageUrl =
        baseUrl + "/AssureIDService/Document/" + instanceId + "/Image?side=front&light=0";
    ImagePayload payload = new ImagePayload();
    payload.setOgnlExpression(oglnExpression);
    request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
    restTemplate.postForObject(postFrontImageUrl, request, String.class);
    // post back side
    String postBackImageUrl =
        baseUrl + "/AssureIDService/Document/" + instanceId + "/Image?side=back&light=0";
    request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
    restTemplate.postForObject(postBackImageUrl, request, String.class);

    String getResultsUrl = baseUrl + "/AssureIDService/Document/" + instanceId;
    AcuantResponse response = restTemplate.getForObject(getResultsUrl, AcuantResponse.class);
    return response;
  }

  public void uploadWithException(String baseUrl, int status) {
    String createInstanceUrl = baseUrl + "/AssureIDService/Document/Instance";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<String>("", headers);
    String instanceId =
        restTemplate.postForObject(createInstanceUrl, request, String.class).replaceAll("\"", "");
    Assertions.assertNotNull(instanceId);

    // post front side
    String postFrontImageUrl =
        baseUrl + "/AssureIDService/Document/" + instanceId + "/Image?side=front&light=0";
    ImagePayload payload = new ImagePayload();
    payload.setHttpStatus(status);
    try {
      request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
      restTemplate.postForObject(postFrontImageUrl, request, String.class);
      // post back side
      String postBackImageUrl =
          baseUrl + "/AssureIDService/Document/" + instanceId + "/Image?side=back&light=0";
      request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
      restTemplate.postForObject(postBackImageUrl, request, String.class);
    } catch (JsonProcessingException e) {

    }

    String getResultsUrl = baseUrl + "/AssureIDService/Document/" + instanceId;
    restTemplate.getForObject(getResultsUrl, AcuantResponse.class);
  }
}
