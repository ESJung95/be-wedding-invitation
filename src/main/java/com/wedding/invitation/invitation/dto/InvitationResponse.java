package com.wedding.invitation.invitation.dto;

import com.wedding.invitation.domain.Guest;
import lombok.Getter;

@Getter
public class InvitationResponse {

    private final boolean personalized;
    private final String name;
    private final Guest.Side side;

    public InvitationResponse(boolean personalized, String name, Guest.Side side) {
        this.personalized = personalized;
        this.name = name;
        this.side = side;
    }
}