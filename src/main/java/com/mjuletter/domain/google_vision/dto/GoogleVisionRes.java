package com.mjuletter.domain.google_vision.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GoogleVisionRes {

    private int classOf;
    private String major;

    private String name;

    @Builder
    public GoogleVisionRes(String name, String major, int classOf) {
        this.name = name;
        this.major = major;
        this.classOf = classOf;
    }
}
