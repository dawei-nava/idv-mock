package com.navapbc.fciv.login.mock.stubs.extensions;

import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.extension.ExtensionFactory;
import com.github.tomakehurst.wiremock.extension.WireMockServices;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import com.github.tomakehurst.wiremock.store.Store;
import java.util.Collections;
import java.util.List;
import org.wiremock.extensions.state.extensions.DeleteStateEventListener;
import org.wiremock.extensions.state.extensions.RecordStateEventListener;
import org.wiremock.extensions.state.extensions.StateRequestMatcher;
import org.wiremock.extensions.state.extensions.StateTemplateHelperProviderExtension;
import org.wiremock.extensions.state.internal.ContextManager;

public class DefaultStateExtension implements ExtensionFactory {
  private final StateTemplateHelperProviderExtension stateTemplateHelperProviderExtension;
  private final RecordStateEventListener recordStateEventListener;
  private final DeleteStateEventListener deleteStateEventListener;
  private final StateRequestMatcher stateRequestMatcher;

  private final ContextManager contextManager;

  private TemplateEngine templateEngine;

  public DefaultStateExtension(Store<String, Object> store) {
    this.contextManager = new ContextManager(store);
    this.stateTemplateHelperProviderExtension = new StateTemplateHelperProviderExtension(contextManager);
    this.templateEngine = new TemplateEngine(stateTemplateHelperProviderExtension.provideTemplateHelpers(), null, Collections.emptySet(), false);

    this.recordStateEventListener = new RecordStateEventListener(contextManager, templateEngine);
    this.deleteStateEventListener = new DeleteStateEventListener(contextManager, templateEngine);
    this.stateRequestMatcher = new StateRequestMatcher(contextManager, templateEngine);
  }

  public DefaultStateExtension(ContextManager contextManager) {
    this.contextManager = contextManager;
    this.stateTemplateHelperProviderExtension = new StateTemplateHelperProviderExtension(contextManager);
    this.templateEngine = new TemplateEngine(stateTemplateHelperProviderExtension.provideTemplateHelpers(), null, Collections.emptySet(), false);

    this.recordStateEventListener = new RecordStateEventListener(contextManager, templateEngine);
    this.deleteStateEventListener = new DeleteStateEventListener(contextManager, templateEngine);
    this.stateRequestMatcher = new StateRequestMatcher(contextManager, templateEngine);
  }

  @Override
  public List<Extension> create(WireMockServices services) {
    return List.of(
        recordStateEventListener,
        deleteStateEventListener,
        stateRequestMatcher,
        stateTemplateHelperProviderExtension
    );  }

  public Object getState(String contextName, String stateName) {
    return this.contextManager.getState(contextName, stateName);
  }

  public ContextManager getContextManager() {
    return this.contextManager;
  }
  public TemplateEngine getTemplateEngine() {
    return this.templateEngine;
  }
}
