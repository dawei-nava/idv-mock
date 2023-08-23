package com.navapbc.fciv.login.mock.util;

import java.lang.annotation.*;
import org.springframework.cloud.contract.wiremock.WireMockConfiguration;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WireMockConfiguration.class)
@Inherited
public @interface EnableWireMockConfiguration {
}
