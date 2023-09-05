package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import java.util.Map;

public interface ResponseTransformer {
  public AcuantResponse transform(AcuantResponse response, Map<String, Object> context) throws TransformException;
}
