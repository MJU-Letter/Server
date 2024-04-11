package com.mjuletter.domain.notification.application;

import com.mjuletter.domain.letter.dto.response.LetterResponse;
import com.mjuletter.domain.notification.domain.Notification;
import com.mjuletter.domain.notification.domain.repository.NotificationRepository;
import com.mjuletter.domain.notification.dto.response.NotificationResponse;
import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    @Transactional
    public void createNotification(User recipient, User sender) {
        Notification notification = Notification.builder()
                .sender(sender)
                .recipient(recipient)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }


    @Transactional(readOnly = true)
    public ResponseEntity<List<NotificationResponse>> getNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 최신 50개의 알림을 가져오기
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationsPage = notificationRepository.findTop50ByRecipientOrderByCreatedAtDesc(user, pageable);

        // 페이지에서 알림 목록을 가져옴
        List<Notification> notifications = notificationsPage.getContent();

        // Notification 엔티티를 NotificationResponse로 매핑하여 반환
        List<NotificationResponse> responseList = notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getSender().getName(),
                        notification.isRead()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }


    public void markNotificationAsRead(UserPrincipal userPrincipal, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification); // 변경 사항을 데이터베이스에 저장
    }
}
