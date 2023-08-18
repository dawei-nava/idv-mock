package com.navapbc.fciv.login.mock.services.acuant.scenarios;

import com.navapbc.fciv.login.acuant.AcuantResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class ChainingResponseTransformer implements ResponseTransformer{
  public List<ResponseTransformer> transformerList;

  public ChainingResponseTransformer(List<ResponseTransformer> transformerList) {
    this.transformerList = transformerList;
  }

  public ChainingResponseTransformer() {
    this.transformerList = new LinkedList<>();
  }

  public void addTransformer(ResponseTransformer transformer) {
    this.transformerList.add(transformer);
  }
  @Override
  public AcuantResponse transform(AcuantResponse response) throws Exception {
    if(CollectionUtils.isEmpty(this.transformerList)) {
      LOGGER.debug("No transformers found, return original result.");
      return response;
    }

    for (ResponseTransformer t : this.transformerList) {
      response = t.transform(response);
    }
    return response;
  }
}
