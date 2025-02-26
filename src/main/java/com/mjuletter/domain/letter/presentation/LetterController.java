package com.mjuletter.domain.letter.presentation;

import com.mjuletter.domain.letter.application.LetterService;
import com.mjuletter.domain.letter.dto.request.LetterRequest;
import com.mjuletter.domain.letter.dto.response.ReceivedLetterResponse;
import com.mjuletter.domain.letter.dto.response.SentLetterResponse;
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
    public ResponseEntity<List<ReceivedLetterResponse>> getReceivedLetters(@CurrentUser UserPrincipal userPrincipal) {
        return letterService.getReceivedLetters(userPrincipal.getId());
    }

    @Operation(summary = "사용자가 보낸 편지 리스트 조회", description = "사용자가 보낸 편지 리스트를 조회합니다.")
    @GetMapping("/sent")
    public ResponseEntity<List<SentLetterResponse>> getSentLetters(@CurrentUser UserPrincipal userPrincipal) {
        // 현재 인증된 사용자의 ID를 가져와서 사용자가 보낸 편지 리스트를 조회합니다.
        return letterService.getSentLetters(userPrincipal.getId());
    }
    @Operation(summary = "내가 쓴 롤링페이퍼 삭제", description = "내가 쓴 롤링페이퍼를 삭제하는 API")
    @DeleteMapping("/sent/{letterId}")
    public ResponseEntity<ApiResponse> deleteSentLetter(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long letterId) {
        letterService.deleteSentLetter(userPrincipal.getId(), letterId);
        return ResponseEntity.ok(new ApiResponse(true, "내가 쓴 롤링페이퍼가 삭제되었습니다."));
    }

    @Operation(summary = "내가 받은 롤링페이퍼 삭제", description = "내가 받은 롤링페이퍼를 삭제하는 API")
    @DeleteMapping("/received/{letterId}")
    public ResponseEntity<ApiResponse> deleteReceivedLetter(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long letterId) {
        letterService.deleteReceivedLetter(userPrincipal.getId(), letterId);
        return ResponseEntity.ok(new ApiResponse(true, "내가 받은 롤링페이퍼가 삭제되었습니다."));
    }

    @Operation(summary = "다른 유저의 편지 조회", description = "다른 유저의 편지를 조회하는 API")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getOtherUserLetters(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long userId) {
        return letterService.getOtherReceivedLetters(userPrincipal, userId);
    }
}
