package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navapbc.fciv.login.acuant.AcuantResponse;
import com.navapbc.fciv.login.mock.util.AcuantResponseTemplateLoader;
import com.navapbc.fciv.login.mock.util.TrueIDResponseTemplateLoader;
import com.navapbc.fciv.login.trueid.ParameterDetail;
import com.navapbc.fciv.login.trueid.TrueIDResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ognl.Ognl;
import ognl.OgnlContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

class OGNLResponseTransformerTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  private ResourceLoader resourceLoader = new DefaultResourceLoader();
  private AcuantResponseTemplateLoader loader =
      new AcuantResponseTemplateLoader(resourceLoader, objectMapper);

  private TrueIDResponseTemplateLoader trueIDLoader =
      new TrueIDResponseTemplateLoader(resourceLoader, objectMapper);

  @BeforeEach
  public void setUp() {
    try {
      ReflectionTestUtils.setField(
          loader, "templateFile", "/__files/acuant/state_id/get_results_response_success.json");
      loader.afterPropertiesSet();
      ReflectionTestUtils.setField(
          trueIDLoader, "templateFile", "/__files/trueid/state_id/true_id_response_success.json");
      trueIDLoader.afterPropertiesSet();
      trueIDLoader.afterPropertiesSet();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void acuantTransform() throws Exception {
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
  void acuantTransformComplex() {
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

  @Test
  void trueIdTransform() throws Exception {
    TrueIDResponse response = trueIDLoader.getTemplate();
    ParameterDetail pd =
        response.getProducts().get(0).getParameterDetails().stream()
            .filter(
                (parameterDetail -> {
                  return parameterDetail.getGroup().value().equals("AUTHENTICATION_RESULT")
                      && parameterDetail.getName().endsWith("AlertName")
                      && parameterDetail.getValues().get(0).getValue().equals("2D Barcode Content");
                }))
            .findFirst()
            .get();
    String alertName = pd.getName();
    String alertIndex = alertName.split("_")[1];
    String resultName = "Alert_" + alertIndex + "_AuthenticationResult";
    ParameterDetail result =
        response.getProducts().get(0).getParameterDetails().stream()
            .filter(
                (parameterDetail -> {
                  return parameterDetail.getGroup().value().equals("AUTHENTICATION_RESULT")
                      && parameterDetail.getName().equals(resultName);
                }))
            .findFirst()
            .get();
    String status = result.getValues().get(0).getValue();
    Assertions.assertEquals("Passed", status);

    OGNLResponseTransformer transformer = new OGNLResponseTransformer();
    Map<String, Object> transformerContext = new HashMap<>();
    String[] ognlExpressions =
        new String[] {
          "#detail=#this.products.{parameterDetails.{? group.value=='AUTHENTICATION_RESULT' && name.endsWith('AlertName') && values.{?value=='2D Barcode Content'}.size==1 }}[0][0]",
          "@java.lang.System@out.println( #detail)",
          "#target_name=#detail.name",
          "@java.lang.System@out.println(#target_name)",
          "#name_seq=#target_name.split(\"_\")[1]",
          "#result_name=\"Alert_\"+#name_seq+ \"_AuthenticationResult\"",
          "@java.lang.System@out.println(#result_name)",
          "#auth_result=#this.products.{parameterDetails.{? group.value=='AUTHENTICATION_RESULT' && name==#result_name }}[0][0]",
          "@java.lang.System@out.println(#auth_result)",
          "#auth_result.values[0].value='Failed'",
          "@java.lang.System@out.println(#auth_result.values[0])"
        };
    String ognlExpression = String.join(", ", ognlExpressions);
    transformerContext.put("ognlExpression", ognlExpression);
    transformer.transform(response, transformerContext);
    String newStatus = result.getValues().get(0).getValue();
    Assertions.assertEquals("Failed", newStatus);
  }

  @Test
  void trueIDImageMetrics() throws TransformException {
    TrueIDResponse response = trueIDLoader.getTemplate();
    OGNLResponseTransformer transformer = new OGNLResponseTransformer();
    Map<String, Object> transformerContext = new HashMap<>();
    String[] ognlExpressions =
        new String[] {
          "#detail=#this.products.{parameterDetails.{? group.value=='IMAGE_METRICS_RESULT' && name=='GlareMetric' }}[0][0]",
          "#detail.values[0].value='10'"
        };
    String ognlExpression = String.join(", ", ognlExpressions);
    transformerContext.put("ognlExpression", ognlExpression);
    transformer.transform(response, transformerContext);
    ParameterDetail result =
        response.getProducts().get(0).getParameterDetails().stream()
            .filter(
                (parameterDetail -> {
                  return parameterDetail.getGroup().value().equals("IMAGE_METRICS_RESULT")
                      && parameterDetail.getName().equals("GlareMetric");
                }))
            .findFirst()
            .get();
    String newValue = result.getValues().get(0).getValue();
    Assertions.assertEquals("10", newValue);
  }
}
