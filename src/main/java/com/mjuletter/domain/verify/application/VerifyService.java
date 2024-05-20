package com.mjuletter.domain.verify.application;

import com.mjuletter.domain.mail.MailService;
import com.mjuletter.domain.verify.domain.Verify;
import com.mjuletter.domain.verify.domain.repository.VerifyRepository;
import com.mjuletter.global.DefaultAssert;
import com.mjuletter.global.payload.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VerifyService {

    private final MailService mailService;
    private final VerifyRepository verifyRepository;


    /**
     * 인증 코드 전송 메서드
     * @param targetEmail 인증 코드를 전송할 이메일
     */
    @Transactional
    public ResponseEntity<?> sendVerifyCode(String targetEmail) throws UnsupportedEncodingException, MessagingException {

        // mailService를 이용해 해당 이메일로 인증코드를 보낸다.
        String randomCode = mailService.sendVerifyCode(targetEmail);
        // 인증 코드 전송 후 인증 확인을 하지 않은 경우, 새로운 인증을 위해 그 전에 만들어져 있는 인증 객체를 삭제한다.
        deleteVerify(targetEmail);
        // 인증 코드와 이메일로 Verify 객체를 만들어 추후 인증에 활용한다.
        Verify verify = createVerify(randomCode, targetEmail);

        verifyRepository.save(verify);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("인증코드가 발급되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    /**
     * 인증 코드 확인 메서드
     * @param code 확인할 인증 코드
     */
    @Transactional
    public ResponseEntity<?> checkVerify(String code) {

        // 코드로 Verify 객체를 가져와서 올바른 인증번호인지 체크
        Optional<Verify> verify = verifyRepository.findByCode(code);
        DefaultAssert.isTrue(verify.isPresent(), "인증번호를 확인해주세요.");
        Verify findVerify = verify.get();

        // 인증 시간이 초과된 경우 예외 처리
        LocalDateTime requestTime = LocalDateTime.now();
        DefaultAssert.isTrue(findVerify.checkExpiration(requestTime), "인증 시간이 초과되었습니다. 다시 인증해주세요.");

        // 인증에 활용한 verify 객체 삭제
        verifyRepository.delete(findVerify);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("인증에 성공했습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    /**
     * Verify 객체 생성 메서드
     * @param code 인증 코드
     * @param targetEmail 이메일
     * @return 생성된 Verify 객체
     */
    private static Verify createVerify(String code, String targetEmail) {
        return Verify.builder()
                .code(code)
                .email(targetEmail)
                .build();
    }


    /**
     * Verify 객체 삭제 메서드
     * @param email 이메일을 통해 사용된 verify 객체를 찾는다.
     */
    private void deleteVerify(String email) {
        Optional<Verify> verify = verifyRepository.findByEmail(email);
        verify.ifPresent(verifyRepository::delete);
    }
}