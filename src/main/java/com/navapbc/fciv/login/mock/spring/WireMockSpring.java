package com.navapbc.fciv.login.mock.spring;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.util.ClassUtils;

/**
 * Convenience factory class for a {@link WireMockConfiguration} that knows how to use
 * Spring Boot to create a stub server. Use, for example, in a JUnit rule:
 *
 * <pre>
 * &#64;ClassRule
 * public static WireMockClassRule wiremock = new WireMockClassRule(
 * 		WireMockSpring.options());
 * </pre>
 *
 * and then use {@link com.github.tomakehurst.wiremock.client.WireMock} as normal in your
 * test methods.
 *
 * @author Dave Syer
 *
 */
public abstract class WireMockSpring {
  private WireMockSpring() {}
  private static boolean initialized = false;

  public static WireMockConfiguration options() {
    if (!initialized) {
      if (ClassUtils.isPresent("org.apache.http.conn.ssl.NoopHostnameVerifier", null)) {
        HttpsURLConnection.setDefaultHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        try {
          HttpsURLConnection.setDefaultSSLSocketFactory(SSLContexts.custom()
              .loadTrustMaterial(null, TrustSelfSignedStrategy.INSTANCE).build().getSocketFactory());
        }
        catch (Exception e) {
          throw new AssertionError("Cannot install custom socket factory: [" + e.getMessage() + "]");
        }
      }
      initialized = true;
    }
    return new WireMockConfiguration();
  }

}
