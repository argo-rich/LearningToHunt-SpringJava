package com.learningtohunt.web.server.service;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import com.postmarkapp.postmark.client.exception.PostmarkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${learningtohunt.email.from}")
    private String fromEmail;

    public MessageResponse sendEmail(String to, String subject, String body) throws PostmarkException, IOException {
        ApiClient client = Postmark.getApiClient(System.getenv("PSTMRK_TOKEN"));
        Message message = new Message(fromEmail, to, subject, body, body, System.getenv("PSTMRK_STREAM"));
        return client.deliverMessage(message);
    }

}
