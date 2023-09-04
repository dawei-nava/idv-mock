package com.navapbc.fciv.login.mock.model.acuant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ImageUpload {
  @JsonProperty
  private String onglExpression;

  @JsonProperty
  private int httpStatus;

  @JsonProperty(defaultValue = "-1")
  private int fixedDelays = -1;

}
