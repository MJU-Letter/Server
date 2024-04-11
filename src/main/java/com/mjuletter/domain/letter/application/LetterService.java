package com.mjuletter.domain.letter.application;

import com.mjuletter.domain.letter.domain.Letter;
import com.mjuletter.domain.letter.domain.repository.LetterRepository;
import com.mjuletter.domain.letter.dto.response.LetterResponse;
import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;

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

        letterRepository.save(letter);
    }


    public ResponseEntity<List<LetterResponse>> getReceivedLetters(Long userId) {
        // 사용자 ID로 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자가 받은 편지 리스트 조회
        List<Letter> receivedLetters = letterRepository.findByRecipient(user);

        // 받은 편지 리스트를 LetterResponse 리스트로 변환
        List<LetterResponse> letterResponses = receivedLetters.stream()
                .map(letter -> new LetterResponse(
                        letter.getId(),
                        letter.getContent(),
                        letter.isAnonymous(),
                        user.getName(),
                        letter.getSender().getPicture()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(letterResponses);
    }

    public ResponseEntity<List<LetterResponse>> getSentLetters(Long userId) {
        // 사용자 ID로 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자가 보낸 편지 리스트 조회
        List<Letter> sentLetters = letterRepository.findBySender(user);

        // 보낸 편지 리스트를 LetterResponse 리스트로 변환
        List<LetterResponse> letterResponses = sentLetters.stream()
                .map(letter -> new LetterResponse(
                        letter.getId(),
                        letter.getContent(),
                        false, // 익명 여부는 보낸 편지에서는 항상 false
                        letter.getRecipient().getName(),
                        letter.getRecipient().getPicture()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(letterResponses);
    }

}

