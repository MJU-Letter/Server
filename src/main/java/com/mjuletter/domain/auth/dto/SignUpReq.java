package com.mjuletter.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class SignUpReq {

    private String providerId;

    @Email
    private String email;

    private String picture;

    private String instagram;

}
