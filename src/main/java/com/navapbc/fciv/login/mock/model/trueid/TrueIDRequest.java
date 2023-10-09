package com.navapbc.fciv.login.mock.model.trueid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TrueIDRequest {

  @JsonProperty("Type")
  private String type;

  @JsonProperty("Document")
  private Document document;

  @JsonProperty("DocumentType")
  private String documentType;
}
