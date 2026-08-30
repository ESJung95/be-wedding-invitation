package com.wedding.invitation.admin.dto;

import com.wedding.invitation.domain.Guest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GuestCreateRequest {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotNull(message = "혼주 구분을 선택해주세요.")
    private Guest.Side side;

    protected GuestCreateRequest() {
    }

    public GuestCreateRequest(String name, Guest.Side side) {
        this.name = name;
        this.side = side;
    }

    public String getName() {
        return name;
    }

    public Guest.Side getSide() {
        return side;
    }
}