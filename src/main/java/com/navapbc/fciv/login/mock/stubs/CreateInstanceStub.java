package com.navapbc.fciv.login.mock.stubs;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CreateInstanceStub {

  public StubMapping stub() {
    return stubFor(post(urlEqualTo("AssureIDService/Document/Instance"))
        .inScenario("default")
        .whenScenarioStateIs(Scenario.STARTED)
        .willSetStateTo("Front")
        .willReturn(aResponse()));
  }
}
