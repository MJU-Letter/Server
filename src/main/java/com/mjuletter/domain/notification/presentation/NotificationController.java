package com.mjuletter.domain.notification.presentation;

import com.mjuletter.domain.notification.application.NotificationService;
import com.mjuletter.domain.notification.dto.response.NotificationResponse;
import com.mjuletter.global.config.security.token.CurrentUser;
import com.mjuletter.global.config.security.token.UserPrincipal;
import com.mjuletter.global.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "Notification API")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "현재 사용자의 알림 목록을 조회합니다.")
    @GetMapping("/")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@CurrentUser UserPrincipal userPrincipal) {
        return notificationService.getNotifications(userPrincipal.getId());
    }

    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    @PatchMapping("/{notificationId}")
    public ResponseEntity<ApiResponse> markNotificationAsRead(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long notificationId) {
        // 알림 ID를 사용하여 알림을 조회하고 읽음으로 표시
        notificationService.markNotificationAsRead(userPrincipal, notificationId);
        ApiResponse response = new ApiResponse(true, "알림이 읽음으로 표시되었습니다.");
        return ResponseEntity.ok(response);
    }
}
