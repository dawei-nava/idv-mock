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
  int port() default 8080;

  /**
   * If specified, configures WireMock instance to enable <em>HTTPS</em> on specified
   * port.
   * <p>
   * Set this value to 0 for WireMock to listen to a random port.
   * </p>
   * @return port to which WireMock instance should listen to
   */
  int httpsPort() default -1;

  /**
   * The resource locations to use for loading WireMock mappings.
   * <p>
   * When none specified, <em>src/test/resources/mappings</em> is used as default
   * location.
   * </p>
   * <p>
   * To customize the location, this attribute must be set to the directory where
   * mappings are stored.
   * </p>
   * @return locations to read WireMock mappings from
   */
  String[] stubs() default { "" };

  /**
   * The resource locations to use for loading WireMock response bodies.
   * <p>
   * When none specified, <em>src/test/resources/__files</em> is used as default.
   * </p>
   * <p>
   * To customize the location, this attribute must be set to the parent directory of
   * <strong>__files</strong> directory.
   * </p>
   * @return locations to read WireMock response bodies from
   */
  String[] files() default { "" };
}
