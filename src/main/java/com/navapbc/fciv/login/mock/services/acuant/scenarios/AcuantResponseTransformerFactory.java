package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
public class AcuantResponseTransformerFactory {

  private AcuantResponseTransformerFactory() {}

  private static final Map<String, Class<? extends ResponseTransformer>> instances = new HashMap<>();

  public static void register(String transformerName, Class<? extends ResponseTransformer> instanceClass) {
    if(transformerName!=null && instanceClass!=null) {
      LOGGER.info("Register transformer {} = {}", transformerName, instanceClass);
      instances.put(transformerName, instanceClass);
    }
  }
  public static ResponseTransformer getInstance(ApplicationContext context, String transformerName) {
    if(instances.containsKey(transformerName)) {
      return context.getBean(instances.get(transformerName));
    }
    return null;
  }

}
