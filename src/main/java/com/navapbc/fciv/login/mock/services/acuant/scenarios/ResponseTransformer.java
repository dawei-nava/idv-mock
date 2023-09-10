package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.mock.model.DocAuthResponse;
import java.util.Map;

public interface ResponseTransformer {
  public DocAuthResponse transform(DocAuthResponse response, Map<String, Object> context)
      throws TransformException;
}
