package io.camunda.connector.inbound.subscription;


import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import org.eclipse.angus.mail.imap.IdleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.function.Consumer;

public class IdleManagerHandler implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(IdleManagerHandler.class);
    private IdleManager idleManager;
    private Folder imapFolder;

    private Consumer<EmailWatchServiceSubscriptionEvent> callback;

    public IdleManagerHandler(IdleManager idleManager, Folder imapFolder, Consumer<EmailWatchServiceSubscriptionEvent> callback) {
        this.idleManager = idleManager;
        this.imapFolder = imapFolder;
        this.callback = callback;
    }

    @Override
    public void run(){
        LOG.info("Start long poll cycle");
        try {
            imapFolder.addMessageCountListener(new MessageCountAdapter() {
                public void messagesAdded(MessageCountEvent ev) {
                Folder folder = (Folder)ev.getSource();
                Message[] msgs = ev.getMessages();
                LOG.info("Email(s) received in folder: " + folder + " with " + msgs.length + " new message(s)");

                for(Message message: msgs) {
                    EmailMessageHandler.parseEmailAndSendtoCamunda(message, callback);
                }

                try {
                    // set msgs to SEEN and process new messages
                    folder.setFlags(msgs,new Flags(Flags.Flag.SEEN), true);
                    idleManager.watch(folder); // keep watching for new messages
                } catch (MessagingException mex) {
                        LOG.error("Error on starting new watch "+mex);
                }
                }
            });
            idleManager.watch(imapFolder);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
