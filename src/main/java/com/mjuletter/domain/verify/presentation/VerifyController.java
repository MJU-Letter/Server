package com.mjuletter.domain.verify.presentation;

import com.mjuletter.domain.verify.application.VerifyService;
import com.mjuletter.domain.verify.dto.SendEmailReq;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verify")
public class VerifyController {

    private final VerifyService verifyService;

    // 인증 코드 발송
    @PostMapping("/send")
    public ResponseEntity<?> sendVerifyCode(@RequestBody SendEmailReq sendEmailReq) throws UnsupportedEncodingException, MessagingException {
        return verifyService.sendVerifyCode(sendEmailReq.getEmail());
    }

    // 인증 코드 확인
    @PostMapping("/check/{code}")
    public ResponseEntity<?> checkVerify(@PathVariable String code) {
        return verifyService.checkVerify(code);
    }
}