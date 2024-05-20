package com.mjuletter.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoRes {

    private Long id;

    private String name;

    private String picture;

    private String major;

    private int classOf;

    private String instagram;

    @Builder
    public UserInfoRes(Long id, String name, String picture, String major, int classOf, String instagram) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.major = major;
        this.classOf = classOf;
        this.instagram = instagram;
    }
}
