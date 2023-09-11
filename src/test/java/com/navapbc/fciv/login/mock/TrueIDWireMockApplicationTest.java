package com.navapbc.fciv.login.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.navapbc.fciv.login.mock.util.TrueIDImageUploadUtil;
import com.navapbc.fciv.login.trueid.TrueIDResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TrueIDWireMockApplicationTest {
  @Value("${wiremock.server.port:8080}")
  int serverPort;

  @Value("${trueid.workflow:GSA.V3.TrueID.Flow")
  String workflow;

  @Value("${trueid.account:123456")
  String account;

  @Autowired TrueIDImageUploadUtil trueIDImageUploadUtil;

  @Test
  void test() throws JsonProcessingException {
    TrueIDResponse trueIDResponse;
    String[] ognlExpressions =
        new String[] {
          "#detail=#this.products.{parameterDetails.{? group.value=='AUTHENTICATION_RESULT' && name.endsWith('AlertName') && values.{?value=='2D Barcode Content'}.size==1 }}[0][0]",
          "#target_name=#detail.name",
          "#name_seq=#target_name.split(\"_\")[1]",
          "#result_name=\"Alert_\"+#name_seq+ \"_AuthenticationResult\"",
          "#auth_result=#this.products.{parameterDetails.{? group.value=='AUTHENTICATION_RESULT' && name==#result_name }}[0][0]",
          "#auth_result.values[0].value='Failed'",
        };
    String ognlExpression = String.join(", ", ognlExpressions);
    trueIDResponse = trueIDImageUploadUtil.uploadAndGetResponse(getUrl(), ognlExpression);
  }

  String getUrl() {
    return new StringBuilder("http://localhost:")
        .append(serverPort)
        .append("/")
        .append("/restws/identity/v3/accounts/")
        .append(account)
        .append("/workflows/")
        .append(workflow)
        .append("/conversations")
        .toString();
  }
}
