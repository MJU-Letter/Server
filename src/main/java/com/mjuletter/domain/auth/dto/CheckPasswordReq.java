package com.mjuletter.domain.auth.dto;


import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPasswordReq {

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$")
    private String password;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$")
    private String checkPassword;
}
