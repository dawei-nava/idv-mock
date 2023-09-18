package com.navapbc.fciv.login.mock.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.mock.model.acuant.ImagePayload;
import com.navapbc.fciv.login.trueid.TrueIDResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TrueIDImageUploadUtil {
  private RestTemplate restTemplate;
  private ObjectMapper mapper;

  @Autowired
  public TrueIDImageUploadUtil(RestTemplate restTemplate, ObjectMapper mapper) {
    this.restTemplate = restTemplate;
    this.mapper = mapper;
  }

  public TrueIDResponse uploadAndGetResponse(String url, String oglnExpression)
      throws JsonProcessingException {
    TrueIDResponse response = null;
    ImagePayload payload = new ImagePayload();
    payload.setOgnlExpression(oglnExpression);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
    response = restTemplate.postForObject(url, request, TrueIDResponse.class);
    return response;
  }
}
