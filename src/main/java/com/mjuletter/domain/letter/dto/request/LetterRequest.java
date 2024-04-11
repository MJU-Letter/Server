package com.mjuletter.domain.letter.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LetterRequest {
    private String content;
    private Long recipientId;
    private boolean anonymous; // 익명 여부
}