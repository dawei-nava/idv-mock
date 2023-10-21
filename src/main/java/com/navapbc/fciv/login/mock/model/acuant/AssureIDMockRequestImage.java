package com.navapbc.fciv.login.mock.model.acuant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AssureIDMockRequestImage {
  @JsonProperty private ImagePayload front;
  @JsonProperty private ImagePayload back;
  @JsonProperty private ResultPayload result;
}
