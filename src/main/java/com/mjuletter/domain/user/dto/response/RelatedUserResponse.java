package com.mjuletter.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelatedUserResponse {
    private Long id;
    private String picture;
    private String name;
    private String major;
    private int classOf;

}
