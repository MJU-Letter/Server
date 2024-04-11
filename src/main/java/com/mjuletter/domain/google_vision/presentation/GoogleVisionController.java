package com.mjuletter.domain.google_vision.presentation;

import com.mjuletter.domain.google_vision.application.GoogleVisionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vision")
public class GoogleVisionController {

    private final GoogleVisionService googleVisionService;

    @GetMapping("/extract-text")
    public ResponseEntity<?> extractText(@RequestPart MultipartFile image) throws IOException {
            // Google Vision Service를 사용하여 이미지에서 텍스트 추출
            return googleVisionService.detectTextGcs(image);
    }
}