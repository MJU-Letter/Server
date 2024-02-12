package com.mjuletter.domain.user.presentation;

import com.mjuletter.global.auth.dto.SessionUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // @AuthenticationPrincipal 어노테이션을 사용하여 Principal 객체를 직접 주입받을 수 있습니다.
    @GetMapping("/info")
    public SessionUser getUserInfo(@AuthenticationPrincipal SessionUser sessionUser) {
        // 세션에 저장된 사용자 정보인 SessionUser 객체를 반환합니다.
        return sessionUser;
    }
}