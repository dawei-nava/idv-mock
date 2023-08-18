package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlContext;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Slf4j
public class OGNLResponseTransformer implements ResponseTransformer{
  private String ognlExpression;

  private Map<Object, Object> context = new HashMap<>();

  @Override
  public AcuantResponse transform(AcuantResponse response) throws Exception {
    LOGGER.debug("Transform with OGNL expression: {}", ognlExpression);
    if(StringUtils.isBlank(ognlExpression)) {
      return response;
    }
    Object expr = Ognl.parseExpression(this.ognlExpression);
    OgnlContext ctx = Ognl.createDefaultContext(response, context);
    Ognl.getValue(expr, ctx, response);
    return response;
  }
}
