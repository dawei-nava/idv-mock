package com.navapbc.fciv.login.mock;

import com.github.tomakehurst.wiremock.extension.Extension;
import com.navapbc.fciv.login.mock.util.EnableWireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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
  public ServiceListFactoryBean serviceListFactoryBean() {
    LOGGER.debug("ServiceListFactory");
    ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
    serviceListFactoryBean.setServiceType(Extension.class);
    return serviceListFactoryBean;
  }

//  @Bean
//  public ServletListenerRegistrationBean<WireMockWebContextListener> wireMockContextListener() {
//    ServletListenerRegistrationBean<WireMockWebContextListener> bean =
//        new ServletListenerRegistrationBean<WireMockWebContextListener>();
//    bean.setListener(new WireMockWebContextListener());
//    bean.setOrder(100);
//    return bean;
//  }

}
