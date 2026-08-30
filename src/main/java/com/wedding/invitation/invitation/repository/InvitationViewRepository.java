package com.wedding.invitation.invitation.repository;

import com.wedding.invitation.domain.AccessType;
import com.wedding.invitation.domain.InvitationView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationViewRepository extends JpaRepository<InvitationView, Long> {

    long countByAccessType(AccessType accessType);

    List<InvitationView> findTop10ByOrderByViewedAtDesc();
}