package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.mock.model.DocAuthResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Slf4j
@Component("ognlTransformer")
public class OGNLResponseTransformer implements ResponseTransformer{

  static {
    AcuantResponseTransformerFactory.register("ognl", OGNLResponseTransformer.class);
  }

  private Map<Object, Object> ognlContext = new HashMap<>();

  @Override
  public DocAuthResponse transform(DocAuthResponse response, Map<String, Object> context) throws TransformException {
    String ognlExpression = context.get("ognlExpression").toString();
    LOGGER.debug("Transform with OGNL expression: {}", ognlExpression);
    if(StringUtils.isBlank(ognlExpression)) {
      return response;
    }
    Object expr = null;
    try {
      expr = Ognl.parseExpression(ognlExpression);
      OgnlContext ctx = Ognl.createDefaultContext(response, ognlContext);
      Ognl.getValue(expr, ctx, response);
      return response;
    } catch (OgnlException e) {
      throw new TransformException(e.getMessage(), e);
    }
  }

}
