package com.wedding.invitation.invitation.repository;

import com.wedding.invitation.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    long countByIsActiveTrue();

    boolean existsByName(String name);
}