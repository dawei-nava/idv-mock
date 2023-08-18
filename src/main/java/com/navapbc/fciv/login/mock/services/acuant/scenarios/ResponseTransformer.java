package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import ognl.OgnlException;

public interface ResponseTransformer {
  public AcuantResponse transform(AcuantResponse response) throws Exception;
}
