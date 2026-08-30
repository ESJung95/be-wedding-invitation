package com.wedding.invitation.admin.service;

import com.wedding.invitation.admin.dto.GuestCreateRequest;
import com.wedding.invitation.admin.dto.GuestCreateResponse;
import com.wedding.invitation.common.exception.CustomException;
import com.wedding.invitation.common.exception.ErrorCode;
import com.wedding.invitation.domain.Guest;
import com.wedding.invitation.invitation.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestService {

    private final GuestRepository guestRepository;

    @Transactional
    public List<GuestCreateResponse> create(List<GuestCreateRequest> requests) {
        return requests.stream()
                .map(this::createOne)
                .toList();
    }

    private GuestCreateResponse createOne(GuestCreateRequest request) {
        if (guestRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_GUEST_NAME);
        }

        String token = UUID.randomUUID().toString();

        Guest guest = Guest.builder()
                .name(request.getName())
                .side(request.getSide())
                .token(token)
                .build();

        Guest saved = guestRepository.save(guest);

        return new GuestCreateResponse(
                saved.getId(),
                saved.getName(),
                saved.getSide(),
                saved.getToken(),
                saved.getIsActive(),
                saved.getCreatedAt()
        );
    }
}