package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import ognl.OgnlException;

import java.util.Map;

public interface ResponseTransformer {
  public AcuantResponse transform(AcuantResponse response, Map<String, Object> context) throws Exception;
}
