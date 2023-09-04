package com.navapbc.fciv.login.mock.spring;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.*;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.extension.ExtensionDeclarations;
import com.github.tomakehurst.wiremock.extension.ExtensionFactory;
import com.github.tomakehurst.wiremock.servlet.WireMockWebContextListener;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WireMockWebContextListener.class)
@ConditionalOnMissingBean(WireMockWebContextListener.class)
@EnableConfigurationProperties(WireMockSpringProperties.class)
@Slf4j
public class WireMockWebApplicationConfiguration implements SmartLifecycle {

  static final String WIREMOCK_SERVER_BEAN_NAME = "wireMockServer";


  @Autowired WireMockSpringProperties wireMock;


  @Autowired(required = false)
  private Options options;

  @Autowired
  private DefaultListableBeanFactory beanFactory;


  @Autowired(required = false)
  private ObjectProvider<WireMockConfigurationCustomizer> customizers;

  @Autowired
  private ResourceLoader resourceLoader;


  private WireMockServer server;

  private volatile boolean running;

  @PostConstruct
  public void init() throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Running initialization of the WireMock configuration");
    }
    if (this.options == null) {
      com.github.tomakehurst.wiremock.core.WireMockConfiguration factory = WireMockSpring.options();
      factory.fileSource(new SingleRootFileSource("src/main/resources"));
      if (this.wireMock.getServer().getPort() != 8080) {
        factory.port(this.wireMock.getServer().getPort());
      }
      if (this.wireMock.getServer().getHttpsPort() != -1) {
        factory.httpsPort(this.wireMock.getServer().getHttpsPort());
      }
      registerFiles(factory);
      factory.notifier(new Slf4jNotifier(true));
      this.options = factory;
      if (this.customizers != null) {
        this.customizers.orderedStream().forEach(customizer -> customizer.customize(factory));
      }
    }
    reRegisterServerWithResetMappings();
    reRegisterBeans();
    updateCurrentServer();
  }

  private void reRegisterServer() {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Creating a new server at http port [" + this.wireMock.getServer().getPort() + "] and "
          + "https port [" + this.wireMock.getServer().getHttpsPort() + "]");
    }
    if (this.isRunning()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Stopping server [" + this.server + "] at port [" + port(this.server) + "]");
      }
      stop();
    }
    else if (this.server == null) {
      this.server = new WireMockServer(this.options);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created new server [" + this.server + "] at http port [" + httpPort() + "] and https port ["
            + httpsPort() + "]");
      }
    }
    start();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Started server [" + this.server + "] at http port [" + httpPort() + "] and https port ["
          + httpsPort() + "]");
    }
    logRegisteredMappings();
  }

  private void addExtensions() {
    ExtensionDeclarations declarations = this.options.getDeclaredExtensions();
    List<String> classNames = declarations.getClassNames();
    List<Class<? extends Extension>> classList = declarations.getClasses();
    String[] beanNames = beanFactory.getBeanNamesForType(Extension.class);
    for(String beanName: beanNames) {
      Object bean = beanFactory.getBean(beanName);
      if(classNames.contains(bean.getClass().getName()) || classList.contains(bean.getClass())) {
        continue;
      }
      LOGGER.debug("Adding extension : {}={}", ((Extension)bean).getName(), bean.getClass().getName());
      declarations.add((Extension) bean);
    }

    String[] extensionFactories = beanFactory.getBeanNamesForType(ExtensionFactory.class);
    List<ExtensionFactory> existingFactories = declarations.getFactories();
    Set<String> existingFactoryNames = existingFactories.stream().map((f)-> f.getClass().getName()).collect(Collectors.toSet());
    for(String factoryName: extensionFactories) {
      if(existingFactoryNames.contains(factoryName)) {
        continue;
      }
      ExtensionFactory bean =(ExtensionFactory) beanFactory.getBean(factoryName);
      LOGGER.debug("Adding extension factory : {}", bean.getClass().getName());

      declarations.getFactories().add(bean);
    }
  }
  void reRegisterServerWithResetMappings() {
    addExtensions();
    reRegisterServer();
    resetMappings();
    if (this.server.isRunning()) {
      updateCurrentServer();
    }
  }

  void resetMappings() {
    if (this.server.isRunning()) {
      this.server.resetAll();
      this.server.resetRequests();
      this.server.resetScenarios();
      this.server.resetToDefaultMappings();
      WireMock.reset();
      WireMock.resetAllRequests();
      WireMock.resetAllScenarios();
      //registerStubs();
      logRegisteredMappings();
    }
  }


  private void logRegisteredMappings() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("WireMock server has [" + this.server.getStubMappings().size() + "] stubs registered");
    }
  }
  private void registerFiles(com.github.tomakehurst.wiremock.core.WireMockConfiguration factory) throws IOException {
    List<Resource> resources = new ArrayList<>();
    for (String files : this.wireMock.getServer().getFiles()) {
      if (StringUtils.hasText(files)) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
            this.resourceLoader);
        for (Resource resource : resolver.getResources(files)) {
          if (resource.exists()) {
            resources.add(resource);
          }
        }
      }
    }
    if (!resources.isEmpty()) {
      ResourcesFileSource fileSource = new ResourcesFileSource(resources.toArray(new Resource[0]));
      factory.fileSource(fileSource);
    }
  }

  private void registerStubs() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Will register [" + this.wireMock.getServer().getStubs().length + "] stub locations");
    }
    for (String stubs : this.wireMock.getServer().getStubs()) {
      if (StringUtils.hasText(stubs)) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
            this.resourceLoader);
        StringBuilder pattern = new StringBuilder(stubs);
        if (!stubs.contains("*")) {
          if (!stubs.endsWith("/")) {
            pattern.append("/");
          }
          pattern.append("**/*.json");
        }
        try {
          for (Resource resource : resolver.getResources(pattern.toString())) {
            try (InputStream inputStream = resource.getInputStream()) {
              StubMapping stubMapping = StubMapping
                  .buildFrom(StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8));
              this.server.addStubMapping(stubMapping);
            }
          }
        }
        catch (IOException ex) {
          throw new IllegalStateException(ex);
        }
      }
    }
  }

  private void reRegisterBeans() {
    if (!this.beanFactory.containsBean(WIREMOCK_SERVER_BEAN_NAME)) {
      if (LOGGER.isDebugEnabled()) {
        printRegistrationLog();
      }
      this.beanFactory.registerSingleton(WIREMOCK_SERVER_BEAN_NAME, this.server);
    }
    else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Destroying WireMock [" + this.beanFactory.getBean(WIREMOCK_SERVER_BEAN_NAME) + "] instance");
      }
      this.beanFactory.destroySingleton(WIREMOCK_SERVER_BEAN_NAME);
      if (LOGGER.isDebugEnabled()) {
        printRegistrationLog();
      }
      this.beanFactory.registerSingleton(WIREMOCK_SERVER_BEAN_NAME, this.server);
    }
  }

  private void printRegistrationLog() {
    LOGGER.debug("Registering WireMock [" + this.server + "] at http port [" + httpPort() + "] and https port ["
        + httpsPort() + "]");
  }

  private void updateCurrentServer() {
    LOGGER.debug("Update current server");
    WireMock.configureFor(new WireMock(this.server));
    this.running = true;
    if (LOGGER.isDebugEnabled() && this.server.isRunning()) {
      LOGGER.debug("Server [" + this.server + "] is already running at http port [" + httpPort() + "] / https port ["
          + httpsPort() + "]. It has [" + this.server.getStubMappings().size() + "] mappings registered");
    }
  }

  private int httpsPort() {
    return this.server.isRunning() && this.server.getOptions().httpsSettings().enabled() ? this.server.httpsPort()
        : -1;
  }

  private int port(WireMockServer server) {
    return server.isRunning() ? (server.getOptions().httpsSettings().enabled() ? server.httpsPort() : server.port())
        : -1;
  }

  @Override
  public boolean isRunning() {
    return this.running;
  }
  @Override
  public void start() {
    if (isRunning()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Server [" + this.server + "] is already running at http port [" + httpPort()
            + "] / https port [" + httpsPort() + "]");
      }
      updateCurrentServer();
      return;
    }
    this.server.start();
    updateCurrentServer();
  }

  @Override
  public void stop() {
    if (this.running) {
      WireMockServer server = this.server;
      int port = port(server);
      this.server.stop();
      this.running = false;
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Stopped WireMock [" + server + "] instance port [" + port + "]");
      }
    }
    else if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Server already stopped");
    }

  }

  private int httpPort() {
    return this.server.isRunning() ? this.server.port() : -1;
  }

}

@ConfigurationProperties("wiremock")
class WireMockSpringProperties {

  private Server server = new Server();

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

  public Server getServer() {
    return this.server;
  }

  public void setServer(Server server) {
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

  public static class Server {

    private int port = 8080;

    private int httpsPort = -1;

    private String[] stubs = new String[0];

    private String[] files = new String[0];

    private boolean portDynamic = false;

    private boolean httpsPortDynamic = false;

    public int getPort() {
      return this.port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public int getHttpsPort() {
      return this.httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
      this.httpsPort = httpsPort;
    }

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

    public boolean isPortDynamic() {
      return this.portDynamic;
    }

    public void setPortDynamic(boolean portDynamic) {
      this.portDynamic = portDynamic;
    }

    public boolean isHttpsPortDynamic() {
      return this.httpsPortDynamic;
    }

    public void setHttpsPortDynamic(boolean httpsPortDynamic) {
      this.httpsPortDynamic = httpsPortDynamic;
    }

  }
}
