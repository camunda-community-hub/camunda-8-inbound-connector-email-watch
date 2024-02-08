package io.camunda.connector.inbound.subscription;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.function.Consumer;

public class EmailMessageHandler {
    private final static Logger LOG = LoggerFactory.getLogger(EmailMessageHandler.class);

    public static void parseEmailAndSendtoCamunda(Message message, Consumer<EmailWatchServiceSubscriptionEvent> callback) {
        try {
            LOG.info("From " + message.getFrom());
            LOG.info("Reply to " + message.getReplyTo());
            LOG.info("Subject " + message.getSubject());

            Object[] emailBody = null;

            Object content = message.getContent();
            if (content instanceof String) {
                LOG.info("Body " + content);
                emailBody = new Object[]{content};
            } else if (content instanceof Multipart multiPart) {
                ArrayList parts = new ArrayList<Object>();


                for (int i = 0; i < multiPart.getCount(); i++) {
                    BodyPart bodyPart = multiPart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        parts.add(bodyPart.getContent());
                        LOG.info("Body  - ", bodyPart.getContent());
                    }

                    if (bodyPart.isMimeType("multipart/alternative")) {
                        Multipart subMulti = (Multipart) bodyPart.getContent();
                        for (int z = 0; z < subMulti.getCount(); z++) {
                            BodyPart subMultiBodyPart = subMulti.getBodyPart(z);
                            if (subMultiBodyPart.isMimeType("text/plain")) {
                                parts.add(subMultiBodyPart.getContent());
                                LOG.info("Sub Body - ", subMultiBodyPart.getContent());
                            }
                        }
                    }
                }
                emailBody = parts.toArray(Object[]::new);
            }

            EmailWatchServiceSubscriptionEvent ewsse = new EmailWatchServiceSubscriptionEvent(message.getFrom(), message.getReplyTo(), message.getSubject(), emailBody);
            callback.accept(ewsse);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
