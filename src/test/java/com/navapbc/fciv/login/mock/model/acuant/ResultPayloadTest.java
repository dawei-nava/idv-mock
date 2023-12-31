package com.navapbc.fciv.login.mock.model.acuant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class ResultPayloadTest {

  private ObjectMapper mapper = new ObjectMapper();

  @Test
  void testParsing() throws JsonProcessingException {
    String input =
        "{\n"
            + "  \"ognlExpression\" : \"#this.images[0].verticalResolution=10, #this.result=2\"\n"
            + "}";
    System.out.println(input);
    ResultPayload payload = mapper.readValue(input, ResultPayload.class);
  }
}
