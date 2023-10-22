package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.mock.model.DocAuthResponse;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class NoOpResponseTransformer implements ResponseTransformer {

  @Override
  public DocAuthResponse transform(DocAuthResponse response, Map<String, Object> context) {
    return response;
  }
}
