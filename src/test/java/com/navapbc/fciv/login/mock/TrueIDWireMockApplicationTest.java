package com.navapbc.fciv.login.mock;

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
  void test() {
    TrueIDResponse trueIDResponse;
    // trueIDImageUploadUtil.uploadAndGetResponse(getUrl(), "#details=#this.products
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
