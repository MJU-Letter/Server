package com.mjuletter.domain.auth.presentation;

import com.mjuletter.domain.auth.application.AuthService;
import com.mjuletter.domain.auth.dto.CheckPasswordReq;
import com.mjuletter.domain.auth.dto.RefreshTokenReq;
import com.mjuletter.domain.auth.dto.SignInReq;
import com.mjuletter.domain.auth.dto.SignUpReq;
import com.mjuletter.global.config.security.token.CurrentUser;
import com.mjuletter.global.config.security.token.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Authorization", description = "Authorization API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입을 수행합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestPart SignUpReq signUpReq,
                                    @RequestPart MultipartFile picture) {

        return authService.signUp(signUpReq, picture);
    }

    @Operation(summary = "로그인", description = "로그인을 수행합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInReq signInReq) {
        return authService.signIn(signInReq);
    }

    @Operation(summary = "토큰 갱신", description = "신규 토큰을 갱신합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenReq tokenRefreshRequest) {
        return authService.refresh(tokenRefreshRequest);
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 수행합니다.")
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(@CurrentUser UserPrincipal userPrincipal) {
        return authService.signOut(userPrincipal);
    }

    @Operation(summary = "이메일 중복체크", description = "회원가입시 중복된 이메일인지 체크합니다.")
    @GetMapping("/email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestBody String email) {
        return authService.checkEmailDuplicate(email);
    }

    @Operation(summary = "비밀번호 재확인", description = "회원가입시 비밀번호를 재확인합니다.")
    @GetMapping("/password")
    public ResponseEntity<?> checkPassword(@RequestBody CheckPasswordReq checkPasswordReq) {
        return authService.checkPassword(checkPasswordReq);
    }
}
