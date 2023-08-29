package io.camunda.connector.inbound;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;
import io.camunda.connector.inbound.subscription.EmailWatchServiceSubscription;
import io.camunda.connector.inbound.subscription.EmailWatchServiceSubscriptionEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@InboundConnector(name = "EMAILWATCHINBOUNDCONNECTOR", type = "io.camunda:emailwatchserviceinbound:1")
public class MyConnectorExecutable implements InboundConnectorExecutable {

  private EmailWatchServiceSubscription subscription;
  private InboundConnectorContext connectorContext;
  private ExecutorService executorService;
  public CompletableFuture<?> future;


  @Override
  public void activate(InboundConnectorContext connectorContext) {
    MyConnectorProperties props = connectorContext.bindProperties(MyConnectorProperties.class);
    this.connectorContext = connectorContext;
    this.executorService = Executors.newSingleThreadExecutor();

    this.future =
            CompletableFuture.runAsync(
                    () -> {
                      new EmailWatchServiceSubscription(props.getUsername(), props.getPassword(), props.getUrl(), props.getPort(), props.getTimeout(), props.getFolder(), props.getPollingInterval(), this::onEvent);
                    },
                    this.executorService);
  }

  @Override
  public void deactivate() {
    subscription.stop();
  }

  private void onEvent(EmailWatchServiceSubscriptionEvent rawEvent) {
    MyConnectorEvent connectorEvent = new MyConnectorEvent(rawEvent);
    connectorContext.correlate(connectorEvent);
  }
}
