package com.mjuletter.domain.letter.presentation;

import com.mjuletter.domain.letter.application.LetterService;
import com.mjuletter.domain.letter.dto.request.LetterRequest;
import com.mjuletter.domain.letter.dto.response.LetterResponse;
import com.mjuletter.global.config.security.token.CurrentUser;
import com.mjuletter.global.config.security.token.UserPrincipal;
import com.mjuletter.global.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/letters")
@Tag(name = "Letter", description = "Letter API")
@Slf4j
public class LetterController {

    private final LetterService letterService;

    @Operation(summary = "편지 작성", description = "사용자가 편지를 작성합니다.")
    @PostMapping("/")
    public ResponseEntity<ApiResponse> writeRollingPaper(@RequestBody LetterRequest letterRequest, @CurrentUser UserPrincipal userPrincipal) {
        letterService.writeLetter(letterRequest.getContent(), letterRequest.getRecipientId(), userPrincipal.getId(), letterRequest.isAnonymous());
        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information("롤링페이퍼 작성이 완료되었습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "사용자가 받은 편지 리스트 조회", description = "사용자가 받은 편지 리스트를 조회합니다.")
    @GetMapping("/")
    public ResponseEntity<List<LetterResponse>> getReceivedLetters(@CurrentUser UserPrincipal userPrincipal) {
        return letterService.getReceivedLetters(userPrincipal.getId());
    }

}
