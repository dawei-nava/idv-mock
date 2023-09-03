package com.navapbc.fciv.login.mock.spring;

import com.github.tomakehurst.wiremock.common.*;
import com.github.tomakehurst.wiremock.common.filemaker.FilenameMaker;
import com.github.tomakehurst.wiremock.core.MappingsSaver;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockApp;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.extension.ExtensionDeclarations;
import com.github.tomakehurst.wiremock.http.*;
import com.github.tomakehurst.wiremock.http.trafficlistener.WiremockNetworkTrafficListener;
import com.github.tomakehurst.wiremock.security.Authenticator;
import com.github.tomakehurst.wiremock.servlet.NotImplementedContainer;
import com.github.tomakehurst.wiremock.servlet.WireMockHandlerDispatchingServlet;
import com.github.tomakehurst.wiremock.servlet.WireMockWebContextListener;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.store.DefaultStores;
import com.github.tomakehurst.wiremock.store.Stores;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WireMockWebContextListener.class)
@ConditionalOnMissingBean(WireMockWebContextListener.class)
@EnableConfigurationProperties(WireMockSpringProperties.class)
@Slf4j
public class WireMockWebApplicationConfiguration implements Options {

  @Autowired WireMockSpringProperties wireMockSpringProperties;

  @Autowired private ServletContext servletContext;

  @Autowired ServiceListFactoryBean serviceListFactoryBean;

  @PostConstruct
  public void init() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Running initialization of the WireMock Application");
    }

    try {
      List<Extension> extensionList = (List) serviceListFactoryBean.getObject();
      extensionList.forEach(
          (e) -> {
            LOGGER.debug(e.getName());
          });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    LOGGER.debug("Setting up file source root");
    // servletContext.setInitParameter("WireMockFileSourceRoot", "");

    startApp();
  }


  private void startApp() {
    WireMockApp wireMockApp = new WireMockApp(this, new NotImplementedContainer());

    servletContext.setAttribute("WireMockAPp", wireMockApp);
    servletContext.setAttribute(
        StubRequestHandler.class.getName(), wireMockApp.buildStubRequestHandler());
    servletContext.setAttribute(
        AdminRequestHandler.class.getName(), wireMockApp.buildAdminRequestHandler());
    servletContext.setAttribute(Notifier.KEY, new Slf4jNotifier(true));
  }
  @Bean
  public ServletContextInitializer servletContextInitializer() {
    return new ServletContextInitializer() {

      Options options;

      @Override
      public void onStartup(ServletContext servletContext) throws ServletException {
        LOGGER.debug("Setting up file source root");
        servletContext.setInitParameter("WireMockFileSourceRoot", "");
      }
    };
  }

  @Bean
  ServletWrappingController wireMockController() {
    ServletWrappingController controller = new ServletWrappingController();
    controller.setServletClass(WireMockHandlerDispatchingServlet.class);
    controller.setBeanName("wireMockController");
    Properties properties = new Properties();
    properties.setProperty("RequestHandlerClass", StubRequestHandler.class.getName());
    controller.setInitParameters(properties);
    return controller;
  }

  @Bean
  SimpleUrlHandlerMapping wireMockControllerMapping() {
    LOGGER.debug("Setting up wiremock  mapping");
    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    Properties urlProperties = new Properties();
    urlProperties.put("/*", "wireMockController");
    mapping.setMappings(urlProperties);
    mapping.setOrder(Integer.MAX_VALUE - 1);
    return mapping;
  }

  @Bean
  ServletWrappingController wireMockAdminController() {
    ServletWrappingController controller = new ServletWrappingController();
    controller.setServletClass(WireMockHandlerDispatchingServlet.class);
    controller.setBeanName("wireMockAdminController");
    Properties properties = new Properties();
    properties.setProperty("RequestHandlerClass", AdminRequestHandler.class.getName());
    controller.setInitParameters(properties);
    return controller;
  }

  @Bean
  @DependsOn("wireMockControllerMapping")
  SimpleUrlHandlerMapping wireMockAdminControllerMapping() {
    LOGGER.debug("Setting up admin controller mapping");
    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    Properties urlProperties = new Properties();
    urlProperties.put("/__admin/*", "wireMockAdminController");
    mapping.setMappings(urlProperties);
    mapping.setOrder(Integer.MAX_VALUE - 2);
    return mapping;
  }

  @Override
  public int portNumber() {
    return 0;
  }

  @Override
  public boolean getHttpDisabled() {
    return false;
  }

  @Override
  public HttpsSettings httpsSettings() {
    return null;
  }

  @Override
  public JettySettings jettySettings() {
    return null;
  }

  @Override
  public int containerThreads() {
    return 0;
  }

  @Override
  public boolean browserProxyingEnabled() {
    return false;
  }

  @Override
  public BrowserProxySettings browserProxySettings() {
    return null;
  }

  @Override
  public ProxySettings proxyVia() {
    return null;
  }

  @Override
  public Stores getStores() {
    return new DefaultStores(filesRoot());
  }

  @Override
  public FileSource filesRoot() {
    return null;
  }

  @Override
  public MappingsLoader mappingsLoader() {
    return null;
  }

  @Override
  public MappingsSaver mappingsSaver() {
    return null;
  }

  @Override
  public Notifier notifier() {
    return null;
  }

  @Override
  public boolean requestJournalDisabled() {
    return false;
  }

  @Override
  public Optional<Integer> maxRequestJournalEntries() {
    return Optional.empty();
  }

  @Override
  public String bindAddress() {
    return null;
  }

  @Override
  public FilenameMaker getFilenameMaker() {
    return null;
  }

  @Override
  public List<CaseInsensitiveKey> matchingHeaders() {
    return null;
  }

  @Override
  public boolean shouldPreserveHostHeader() {
    return false;
  }

  @Override
  public String proxyHostHeader() {
    return null;
  }

  @Override
  public HttpServerFactory httpServerFactory() {
    return null;
  }

  @Override
  public ThreadPoolFactory threadPoolFactory() {
    return null;
  }

  @Override
  public ExtensionDeclarations getDeclaredExtensions() {
    return null;
  }

  @Override
  public WiremockNetworkTrafficListener networkTrafficListener() {
    return null;
  }

  @Override
  public Authenticator getAdminAuthenticator() {
    return null;
  }

  @Override
  public boolean getHttpsRequiredForAdminApi() {
    return false;
  }

  @Override
  public AsynchronousResponseSettings getAsynchronousResponseSettings() {
    return null;
  }

  @Override
  public ChunkedEncodingPolicy getChunkedEncodingPolicy() {
    return null;
  }

  @Override
  public boolean getGzipDisabled() {
    return false;
  }

  @Override
  public boolean getStubRequestLoggingDisabled() {
    return false;
  }

  @Override
  public boolean getStubCorsEnabled() {
    return false;
  }

  @Override
  public long timeout() {
    return 0;
  }

  @Override
  public boolean getDisableOptimizeXmlFactoriesLoading() {
    return false;
  }

  @Override
  public boolean getDisableStrictHttpHeaders() {
    return false;
  }

  @Override
  public DataTruncationSettings getDataTruncationSettings() {
    return null;
  }

  @Override
  public NetworkAddressRules getProxyTargetRules() {
    return null;
  }

  @Override
  public int proxyTimeout() {
    return 0;
  }

  @Override
  public boolean getResponseTemplatingEnabled() {
    return false;
  }

  @Override
  public boolean getResponseTemplatingGlobal() {
    return false;
  }

  @Override
  public Long getMaxTemplateCacheEntries() {
    return null;
  }

  @Override
  public Set<String> getTemplatePermittedSystemKeys() {
    return null;
  }

  @Override
  public boolean getTemplateEscapingDisabled() {
    return false;
  }
}

@ConfigurationProperties("wiremock.spring")
class WireMockSpringProperties {

  private App server = new App();

  private Placeholders placeholders = new Placeholders();

  private boolean restTemplateSslEnabled;

  private boolean resetMappingsAfterEachTest;

  public boolean isRestTemplateSslEnabled() {
    return this.restTemplateSslEnabled;
  }

  public void setRestTemplateSslEnabled(boolean restTemplateSslEnabled) {
    this.restTemplateSslEnabled = restTemplateSslEnabled;
  }

  public boolean isResetMappingsAfterEachTest() {
    return this.resetMappingsAfterEachTest;
  }

  public void setResetMappingsAfterEachTest(boolean resetMappingsAfterEachTest) {
    this.resetMappingsAfterEachTest = resetMappingsAfterEachTest;
  }

  public App getApp() {
    return this.server;
  }

  public void setApp(App server) {
    this.server = server;
  }

  public Placeholders getPlaceholders() {
    return this.placeholders;
  }

  public void setPlaceholders(Placeholders placeholders) {
    this.placeholders = placeholders;
  }

  public class Placeholders {

    /**
     * Flag to indicate that http URLs in generated wiremock stubs should be filtered to add or
     * resolve a placeholder for a dynamic port.
     */
    private boolean enabled = true;

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class App {

    private String[] stubs = new String[0];

    private String[] files = new String[0];

    private boolean portDynamic = false;

    private boolean httpsPortDynamic = false;

    public String[] getStubs() {
      return this.stubs;
    }

    public void setStubs(String[] stubs) {
      this.stubs = stubs;
    }

    public String[] getFiles() {
      return this.files;
    }

    public void setFiles(String[] files) {
      this.files = files;
    }
  }
}
