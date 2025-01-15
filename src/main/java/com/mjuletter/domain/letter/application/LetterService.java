package com.mjuletter.domain.letter.application;

import com.mjuletter.domain.letter.domain.Letter;
import com.mjuletter.domain.letter.domain.repository.LetterRepository;
import com.mjuletter.domain.letter.dto.response.ReceivedLetterResponse;
import com.mjuletter.domain.letter.dto.response.SentLetterResponse;
import com.mjuletter.domain.notification.application.NotificationService;
import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.global.config.security.token.UserPrincipal;
import com.mjuletter.global.payload.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void writeLetter(String content, Long recipientId, Long senderId, boolean anonymous) {
        Optional<User> senderOptional = userRepository.findById(senderId);
        Optional<User> recipientOptional = userRepository.findById(recipientId);

        User sender = senderOptional.orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User recipient = recipientOptional.orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        Letter letter = Letter.builder()
                .content(content)
                .sender(sender)
                .recipient(recipient)
                .anonymous(anonymous) // 익명 여부 설정
                .build();

        letterRepository.save(letter); // 편지를 먼저 저장합니다.
        notificationService.createNotification(recipient,sender); // 알림을 생성합니다.

    }


    @Transactional(readOnly = true)
    public ResponseEntity<List<ReceivedLetterResponse>> getReceivedLetters(Long userId) {
        // 사용자 ID로 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자가 받은 편지 리스트 조회 (오래된 순으로 정렬)
        List<Letter> receivedLetters = letterRepository.findByRecipientOrderByCreatedAtAsc(user);

        // 받은 편지 리스트를 LetterResponse 리스트로 변환
        List<ReceivedLetterResponse> letterResponses = receivedLetters.stream()
                .map(letter -> new ReceivedLetterResponse(
                        letter.getId(),
                        letter.getContent(),
                        letter.isAnonymous(),
                        user.getName(),
                        letter.getSender().getPicture()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(letterResponses);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<SentLetterResponse>> getSentLetters(Long userId) {
        // 사용자 ID로 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자가 보낸 편지 리스트 조회 (오래된 순으로 정렬)
        List<Letter> sentLetters = letterRepository.findBySenderOrderByCreatedAtAsc(user);

        // 보낸 편지 리스트를 LetterResponse 리스트로 변환
        List<SentLetterResponse> letterResponses = sentLetters.stream()
                .map(letter -> new SentLetterResponse(
                        letter.getId(),
                        letter.getContent(),
                        false, // 익명 여부는 보낸 편지에서는 항상 false
                        letter.getRecipient().getName(),
                        letter.getRecipient().getPicture()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(letterResponses);
    }

    @Transactional
    public void deleteSentLetter(Long userId, Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found"));

        // Check if the logged-in user is the sender of the letter
        if (!userId.equals(letter.getSender().getId())) {
            throw new AccessDeniedException("You are not authorized to delete this letter");
        }
        // Delete the letter
        letterRepository.delete(letter);
    }

    @Transactional
    public void deleteReceivedLetter(Long userId, Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter not found"));

        if (!userId.equals(letter.getRecipient().getId())) {
            throw new AccessDeniedException("You are not authorized to delete this letter");
        }
        letterRepository.delete(letter);
    }


    public ResponseEntity<?> getOtherReceivedLetters(UserPrincipal userPrincipal, Long userId) {
        User user= userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // otherUser가 받은 편지 리스트 조회 (오래된 순으로 정렬)
        List<Letter> receivedLetters = letterRepository.findByRecipientOrderByCreatedAtAsc(otherUser);

        List<ReceivedLetterResponse> letterResponses = receivedLetters.stream()
                .map(letter -> new ReceivedLetterResponse(
                        letter.getId(),
                        letter.getContent(),
                        letter.isAnonymous(),
                        user.getName(),
                        letter.getSender().getPicture()
                ))
                .toList();

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(letterResponses)
                .build();

        return ResponseEntity.ok(response);

    }
}

