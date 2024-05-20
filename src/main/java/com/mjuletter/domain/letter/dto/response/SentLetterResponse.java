package com.mjuletter.domain.letter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SentLetterResponse {
    private Long id;
    private String content;
    private boolean anonymous; // 익명 여부
    private String recipientName; // 받은 사람 이름
    private String recipientProfileImage; // 보낸 사람의 프로필 이미지

}
