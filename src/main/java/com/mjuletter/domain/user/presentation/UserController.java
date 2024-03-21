package com.mjuletter.domain.user.presentation;

import com.mjuletter.domain.user.application.UserService;
import com.mjuletter.global.config.security.token.CurrentUser;
import com.mjuletter.global.config.security.token.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 수행합니다.")
    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@CurrentUser UserPrincipal userPrincipal) {
        return userService.deleteUser(userPrincipal);
    }

}