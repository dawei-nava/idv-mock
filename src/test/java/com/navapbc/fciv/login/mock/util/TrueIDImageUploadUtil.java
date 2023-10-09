package com.navapbc.fciv.login.mock.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.mock.model.acuant.ImagePayload;
import com.navapbc.fciv.login.mock.model.trueid.Document;
import com.navapbc.fciv.login.mock.model.trueid.TrueIDRequest;
import com.navapbc.fciv.login.trueid.TrueIDResponse;
import org.apache.commons.codec.binary.Base64;
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
    TrueIDRequest trueIDRequest = new TrueIDRequest();
    trueIDRequest.setType("Initiate");
    trueIDRequest.setDocumentType("DriversLicenses");
    ImagePayload payload = new ImagePayload();
    payload.setOgnlExpression(oglnExpression);
    String payloadStr = mapper.writeValueAsString(payload);
    String payloadBase64Str = Base64.encodeBase64String(payloadStr.getBytes());
    Document document = new Document();
    document.setFront(payloadBase64Str);
    document.setBack(payloadBase64Str);
    trueIDRequest.setDocument(document);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<String>(mapper.writeValueAsString(trueIDRequest), headers);
    response = restTemplate.postForObject(url, request, TrueIDResponse.class);
    return response;
  }
}
