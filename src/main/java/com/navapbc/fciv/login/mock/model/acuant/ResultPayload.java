package com.navapbc.fciv.login.mock.model.acuant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ResultPayload extends ImagePayload {
  @JsonProperty private String ognlExpression;

  @JsonProperty private String[] ognlExpressions;
}
