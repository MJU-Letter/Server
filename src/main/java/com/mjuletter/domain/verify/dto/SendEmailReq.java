package com.mjuletter.domain.verify.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class SendEmailReq {

    @Email(message = "이메일 형식이어야 합니다.")
    private String email;
}