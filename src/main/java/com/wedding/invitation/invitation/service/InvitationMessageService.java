package com.wedding.invitation.invitation.service;

import com.wedding.invitation.domain.Guest;
import com.wedding.invitation.domain.Message;
import com.wedding.invitation.invitation.dto.MessageCreateRequest;
import com.wedding.invitation.invitation.repository.GuestRepository;
import com.wedding.invitation.invitation.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitationMessageService {

    private final GuestRepository guestRepository;
    private final MessageRepository messageRepository;

    public void create(MessageCreateRequest request) {
        Guest guest = resolveGuest(request.getToken());

        Message message = Message.builder()
                .guest(guest)
                .guestName(request.getGuestName())
                .accessType(request.getAccessType())
                .content(request.getContent())
                .build();

        messageRepository.save(message);
    }

    private Guest resolveGuest(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return guestRepository.findByTokenAndIsActiveTrue(token).orElse(null);
    }
}