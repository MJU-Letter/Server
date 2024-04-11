package com.mjuletter.domain.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private static final int VERIFY_CODE_LENGTH = 6;

    // 이메일로 인증 번호 보내기
    public String sendVerifyCode(String email) throws MessagingException, UnsupportedEncodingException {

//        SimpleMailMessage message = new SimpleMailMessage();

        MimeMessage message = mailSender.createMimeMessage();
        String randomeCode = generateRandomCode();

        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setFrom(new InternetAddress("wltndh16so@gmail.com", "명지편지"));
        message.setSubject("'명지편지' 회원가입을 위한 인증번호가 발급되었습니다.");
        message.setText(setVerifyContext(randomeCode), "utf-8", "html");  // 내용설정

        mailSender.send(message);
        return randomeCode;
    }

    // 랜덤 인증코드 생성

    /**
     * 랜덤 인증 코드 생성 메서드
     * @return 생성된 인증 코드
     */
    public static String generateRandomCode() {

        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < VERIFY_CODE_LENGTH; i++) {
            int digit = random.nextInt(10); // Generate a random digit (0-9)
            codeBuilder.append(digit);
        }
        log.info(codeBuilder.toString());
        return codeBuilder.toString();
    }

    private String setVerifyContext(String code) { // 타임리프 설정하는 코드
        Context context = new Context();
        context.setVariable("code", code); // Template에 전달할 데이터 설정
        return templateEngine.process("email", context); // email.html
    }

}