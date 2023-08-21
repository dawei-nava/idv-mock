package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class NoOpResponseTransformer implements ResponseTransformer{

  @Override
  public AcuantResponse transform(AcuantResponse response, Map<String, Object> context) {
    return response;
  }

}
