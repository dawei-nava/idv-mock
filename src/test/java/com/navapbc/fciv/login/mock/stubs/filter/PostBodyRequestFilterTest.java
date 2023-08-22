package com.navapbc.fciv.login.mock.stubs.filter;

import com.navapbc.fciv.login.mock.services.StorageService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostBodyRequestFilterTest {

  @MockBean private StorageService storageService;
  PostBodyRequestFilter filter = new PostBodyRequestFilter(storageService);

  @Test
  void getInstanceId() {
    String url =
        "/AssureIDService/Document/7db024b2-70a8-4044-baf6-f0247b260b4c/Image?side=front&light=0";

    String instanceId = filter.getInstanceId(url);
    Assertions.assertEquals("7db024b2-70a8-4044-baf6-f0247b260b4c", instanceId);
  }
}
