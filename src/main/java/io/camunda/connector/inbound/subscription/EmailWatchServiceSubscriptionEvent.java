package io.camunda.connector.inbound.subscription;

import java.util.Objects;

/**
 * Data model of an event consumed by inbound Connector (e.g. originating from an external system)
 */
public class EmailWatchServiceSubscriptionEvent {
  private final Object[] from;
  private final Object[] replyTo;
  private final String subject;
  private final Object[] body;

  public EmailWatchServiceSubscriptionEvent(Object[] from, Object[] replyTo, String subject, Object[] body) {
    this.from = from;
    this.replyTo = replyTo;
    this.subject = subject;
    this.body = body;
  }

  public Object[] getFrom() {
    return from;
  }
  public Object[] getReplyTo() {
    return replyTo;
  }
  public String getSubject() {
    return subject;
  }
  public Object[] getBody() {
    return body;
  }
    @Override
  public boolean equals(Object o) {
    System.out.println("checking...");
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmailWatchServiceSubscriptionEvent that = (EmailWatchServiceSubscriptionEvent) o;
    return Objects.equals(from, that.from);
  }

  @Override
  public String toString() {
    return "EmailWatchServiceSubscriptionEvent{" +
            "from='" + from + '\'' +
            "reply to='" + replyTo + '\'' +
            "subject='" + subject + '\'' +
            "body='" + body + '\'' +
        '}';
  }
}
