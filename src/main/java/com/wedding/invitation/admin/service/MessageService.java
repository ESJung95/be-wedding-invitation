package com.wedding.invitation.admin.service;

import com.wedding.invitation.admin.dto.MessageResponse;
import com.wedding.invitation.domain.Message;
import com.wedding.invitation.invitation.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;

    public List<MessageResponse> getMessages() {
        return messageRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getGuestName(),
                message.getAccessType(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}