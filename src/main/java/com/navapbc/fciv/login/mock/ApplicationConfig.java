package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.http.AdminRequestHandler;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import com.github.tomakehurst.wiremock.servlet.WireMockHandlerDispatchingServlet;
import com.github.tomakehurst.wiremock.servlet.WireMockWebContextListener;
import com.navapbc.fciv.login.mock.util.EnableWireMockConfiguration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

@Slf4j
@Configuration
@EnableConfigurationProperties
@EnableWireMockConfiguration
@ComponentScan(
    basePackages = {"com.navapbc.fciv.login.mock.services", "com.navapbc.fciv.login.mock.stubs","com.navapbc.fciv.login.mock.util" })
public class ApplicationConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ServletListenerRegistrationBean<WireMockWebContextListener> wireMockContextListener() {
    ServletListenerRegistrationBean<WireMockWebContextListener> bean =
        new ServletListenerRegistrationBean<WireMockWebContextListener>();
    bean.setListener(new WireMockWebContextListener());
    bean.setOrder(100);
    return bean;
  }

  @Bean
  public ServletContextInitializer servletContextInitializer() {
    return new ServletContextInitializer() {

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
  SimpleUrlHandlerMapping wireMockAdminControllerMapping() {
    LOGGER.debug("Setting up admin controller mapping");
    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    Properties urlProperties = new Properties();
    urlProperties.put("/__admin/*", "wireMockAdminController");
    mapping.setMappings(urlProperties);
    mapping.setOrder(Integer.MAX_VALUE - 100);
    return mapping;
  }

}
