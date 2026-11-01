package com.thinkmart.service;

import com.thinkmart.dto.ContactRequest;
import com.thinkmart.entity.ContactMessage;
import com.thinkmart.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
    private final ContactMessageRepository messages;

    public ContactService(ContactMessageRepository messages) {
        this.messages = messages;
    }

    public void submit(ContactRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.name().trim());
        message.setEmail(request.email().trim().toLowerCase());
        message.setSubject(request.subject().trim());
        message.setMessage(request.message().trim());
        messages.save(message);
    }
}