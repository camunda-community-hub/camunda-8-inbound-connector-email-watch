package io.camunda.connector.inbound.subscription;

import jakarta.mail.*;
import jakarta.mail.search.FlagTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;

public class CheckEmailFolder {

  private final static Logger LOG = LoggerFactory.getLogger(CheckEmailFolder.class);

  public static void searchForUnreadEmails(Folder imapFolder, Consumer<EmailWatchServiceSubscriptionEvent> callback) {
    try {
      Message[] messages = imapFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
      // Process each unseen email
      for (Message message : messages) {
        LOG.info("New unread email on connector startup: {} from {}", message.getSubject(), message.getFrom()[0].toString());
        EmailMessageHandler.parseEmailAndSendtoCamunda(message, callback);

      }

      //mark emails as seen so it will not be picked up twice
      imapFolder.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
      imapFolder.close();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
