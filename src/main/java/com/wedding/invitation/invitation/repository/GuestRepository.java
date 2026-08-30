package com.wedding.invitation.invitation.repository;

import com.wedding.invitation.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    long countByIsActiveTrue();

    boolean existsByName(String name);

    List<Guest> findAllByOrderByNameAsc();

    List<Guest> findAllByIsActiveOrderByNameAsc(Boolean isActive);

    List<Guest> findAllBySideOrderByNameAsc(Guest.Side side);

    List<Guest> findAllByIsActiveAndSideOrderByNameAsc(Boolean isActive, Guest.Side side);

    Optional<Guest> findByTokenAndIsActiveTrue(String token);
}