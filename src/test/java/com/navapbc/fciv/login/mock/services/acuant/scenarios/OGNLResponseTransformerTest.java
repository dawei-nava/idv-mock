package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.util.AcuantResponseTemplateLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ognl.Ognl;
import ognl.OgnlContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class OGNLResponseTransformerTest {

  private AcuantResponseTemplateLoader loader =
      new AcuantResponseTemplateLoader(new ObjectMapper());

  @BeforeEach
  public void setUp() {
    try {
      ReflectionTestUtils.setField(loader, "templateFile", "/files/acuant/state_id/get_results_response_success.json");
      loader.afterPropertiesSet();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  @Test
  void transform() throws Exception {
    AcuantResponse response = loader.getTemplate();

    int original =
        response.getAlerts().stream()
            .filter(a -> a.getKey().equals("2D Barcode Content"))
            .findFirst()
            .get()
            .getResult();
    Assertions.assertNotEquals(-10, original);

    OGNLResponseTransformer transformer = new OGNLResponseTransformer();
    Map<String, Object> transformerContext = new HashMap<>();
    transformerContext.put(
        "ognlExpression", "#this.alerts.{? #this.key==\"2D Barcode Content\"}[0].result=-10");
    transformer.transform(response, transformerContext);

    int result =
        response.getAlerts().stream()
            .filter(a -> a.getKey().equals("2D Barcode Content"))
            .findFirst()
            .get()
            .getResult();
    Assertions.assertEquals(-10, result);
  }

  @Test
  void transformComplex() {
    AcuantResponse response = loader.getTemplate();

    OGNLResponseTransformer transformer = new OGNLResponseTransformer();
    Map<String, Object> transformerContext = new HashMap<>();
    transformerContext.put(
        "ognlExpression", "#this.alerts.{? #this.key==\"2D Barcode Content\"}[0].result");
    Assertions.assertDoesNotThrow(() -> transformer.transform(response, transformerContext));
  }

  @Test
  /* Demonstrate usage of OGNL expression */
  void test() throws Exception {
    String expression =
        "#@java.util.LinkedHashMap@{ \"foo\" : \"foo value\", \"bar\" : \"bar value\" }";
    OgnlContext ctx = Ognl.createDefaultContext(null, new HashMap<>());
    Object expr = Ognl.parseExpression(expression);
    Object result = Ognl.getValue(expr, ctx);
    Assertions.assertEquals("java.util.LinkedHashMap", result.getClass().getName());

    List<String> testList = new LinkedList<>();
    testList.add("String1");
    expression = "#this.clear()";
    expr = Ognl.parseExpression(expression);
    Object val = Ognl.getValue(expr, testList);
    Assertions.assertNull(val);
    Assertions.assertTrue(testList.isEmpty());

    expression = "#this.clear(), #this.add(\"String2\")";
    expr = Ognl.parseExpression(expression);
    Ognl.getValue(expr, testList);
    Assertions.assertFalse(testList.isEmpty());
    Assertions.assertEquals("String2", testList.get(0));
  }
}
