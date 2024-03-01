package com.mjuletter.domain.auth.dto;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class SignInReq {

    @Email
    private String email;

    private String providerId;
}
