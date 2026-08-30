package com.wedding.invitation.invitation.repository;

import com.wedding.invitation.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findTop10ByOrderByCreatedAtDesc();
}