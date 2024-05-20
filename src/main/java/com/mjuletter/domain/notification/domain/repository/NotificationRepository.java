package com.mjuletter.domain.notification.domain.repository;

import com.mjuletter.domain.notification.domain.Notification;
import com.mjuletter.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(User recipient);
    Page<Notification> findTop50ByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
}
