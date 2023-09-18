package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.model.DocAuthResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NoOpResponseTransformer implements ResponseTransformer {

  @Override
  public DocAuthResponse transform(DocAuthResponse response, Map<String, Object> context) {
    return response;
  }
}
