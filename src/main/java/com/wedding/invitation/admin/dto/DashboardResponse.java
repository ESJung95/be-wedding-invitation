package com.wedding.invitation.admin.dto;

import lombok.Getter;

@Getter
public class DashboardResponse {

    private final long totalGuestCount;
    private final long activeGuestCount;
    private final long totalViewCount;
    private final long linkViewCount;
    private final long qrViewCount;
    private final long totalMessageCount;

    public DashboardResponse(
            long totalGuestCount,
            long activeGuestCount,
            long totalViewCount,
            long linkViewCount,
            long qrViewCount,
            long totalMessageCount
    ) {
        this.totalGuestCount = totalGuestCount;
        this.activeGuestCount = activeGuestCount;
        this.totalViewCount = totalViewCount;
        this.linkViewCount = linkViewCount;
        this.qrViewCount = qrViewCount;
        this.totalMessageCount = totalMessageCount;
    }
}