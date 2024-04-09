package com.mjuletter.domain.user.dto.respnse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RandomUserResponse {
    private Long id;
    private String image;
    private String name;

    private String major;
    private int classOf;

}
