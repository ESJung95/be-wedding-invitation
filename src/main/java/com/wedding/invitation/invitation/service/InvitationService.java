package com.wedding.invitation.invitation.service;

import com.wedding.invitation.common.exception.CustomException;
import com.wedding.invitation.common.exception.ErrorCode;
import com.wedding.invitation.domain.AccessType;
import com.wedding.invitation.domain.Guest;
import com.wedding.invitation.domain.InvitationView;
import com.wedding.invitation.invitation.dto.InvitationResponse;
import com.wedding.invitation.invitation.repository.GuestRepository;
import com.wedding.invitation.invitation.repository.InvitationViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitationService {

    private final GuestRepository guestRepository;
    private final InvitationViewRepository invitationViewRepository;

    public InvitationResponse getInvitation(String token, AccessType accessType, String ipAddress) {
        if (!StringUtils.hasText(token)) {
            return anonymousAccess(accessType, ipAddress);
        }
        return personalizedAccess(token, accessType, ipAddress);
    }

    private InvitationResponse personalizedAccess(String token, AccessType accessType, String ipAddress) {
        Guest guest = guestRepository.findByTokenAndIsActiveTrue(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITATION_TOKEN));

        logView(guest, accessType, ipAddress);

        return new InvitationResponse(true, guest.getName(), guest.getSide());
    }

    private InvitationResponse anonymousAccess(AccessType accessType, String ipAddress) {
        logView(null, accessType, ipAddress);

        return new InvitationResponse(false, null, null);
    }

    private void logView(Guest guest, AccessType accessType, String ipAddress) {
        InvitationView view = InvitationView.builder()
                .guest(guest)
                .accessType(accessType)
                .ipAddress(ipAddress)
                .build();
        invitationViewRepository.save(view);
    }
}