package com.navapbc.fciv.login.mock.model.trueid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Document {
  @JsonProperty("Front")
  private String front;

  @JsonProperty("Back")
  private String back;
}
