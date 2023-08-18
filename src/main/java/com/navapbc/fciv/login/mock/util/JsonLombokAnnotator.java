package com.navapbc.fciv.login.mock.util;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import lombok.Builder;
import org.jsonschema2pojo.AbstractAnnotator;
public class JsonLombokAnnotator extends AbstractAnnotator{
  @Override
  public void typeInfo(JDefinedClass clazz, JsonNode schema) {
    clazz.annotate(Builder.class);
  }
}
