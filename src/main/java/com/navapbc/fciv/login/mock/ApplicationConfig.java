package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.servlet.WireMockHandlerDispatchingServlet;
import com.github.tomakehurst.wiremock.servlet.WireMockWebContextListener;
import com.navapbc.fciv.login.mock.util.EnableWireMockConfiguration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties
@EnableWireMockConfiguration
@ComponentScan(
    basePackages = {"com.navapbc.fciv.login.mock.services", "com.navapbc.fciv.login.mock.stubs","com.navapbc.fciv.login.mock.util" })

public class ApplicationConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

//  @Bean
//  public WireMockConfigurationCustomizer optionsCustomizer(
//      PostBodyRequestFilter postBodyRequestFilter,
//      ResultRequestMatcher resultRequestMatcher,
//      GetResultRequestRequestFilter getResultRequestRequestFilter,
//      GetResultResponseDefinitionTransformer getResultResponseDefinitionTransformer
//  ) {
//    return (config) -> {
//      LOGGER.debug("Customizing configuration");
//      config.extensions(new ResponseTemplateTransformer(false),
//          postBodyRequestFilter, resultRequestMatcher, getResultRequestRequestFilter, getResultResponseDefinitionTransformer);
//      config.notifier(new Slf4jNotifier(true));
//    };
//
//  }
  @Bean
  public ServletListenerRegistrationBean<WireMockWebContextListener> wireMockContextListener() {
    ServletListenerRegistrationBean<WireMockWebContextListener> bean =
        new ServletListenerRegistrationBean<WireMockWebContextListener>();
    bean.setListener(new WireMockWebContextListener());
    bean.setOrder(1);
    return bean;
  }

  @Bean
  public ServletContextInitializer initializer() {
    return new ServletContextInitializer() {

      @Override
      public void onStartup(ServletContext servletContext) throws ServletException {
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
    properties.setProperty("RequestHandlerClass", "com.github.tomakehurst.wiremock.http.StubRequestHandler");
    controller.setInitParameters(properties);
    return controller;
  }

  @Bean
  SimpleUrlHandlerMapping wireMockControllerMapping() {
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
    properties.setProperty("RequestHandlerClass", "com.github.tomakehurst.wiremock.http.AdminRequestHandler");
    controller.setInitParameters(properties);
    return controller;
  }

  @Bean
  SimpleUrlHandlerMapping wireMockAdminControllerMapping() {
    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    Properties urlProperties = new Properties();
    urlProperties.put("/__admin/*", "wireMockAdminController");
    mapping.setMappings(urlProperties);
    mapping.setOrder(Integer.MAX_VALUE - 2);
    return mapping;
  }

}
