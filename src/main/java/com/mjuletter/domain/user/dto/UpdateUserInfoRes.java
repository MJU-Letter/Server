package com.mjuletter.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateUserInfoRes {

    private Long id;

    private String email;

    private String name;

    private String picture;

    private String major;

    private int classOf;

    private String instagram;

    @Builder
    public UpdateUserInfoRes(Long id, String email, String name, String picture, String major, int classOf, String instagram) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.major = major;
        this.classOf = classOf;
        this.instagram = instagram;
    }
}
