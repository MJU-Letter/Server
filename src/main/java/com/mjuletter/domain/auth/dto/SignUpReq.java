package com.mjuletter.domain.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpReq {

    @Email
    private String email;

    private String password;

    private String name;

    private String major;

    private int classOf;

    private String picture;

    private String instagram;

}
