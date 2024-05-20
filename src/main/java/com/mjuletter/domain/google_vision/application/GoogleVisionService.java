package com.mjuletter.domain.google_vision.application;

import com.google.cloud.vision.v1.*;
import com.mjuletter.domain.google_vision.dto.GoogleVisionRes;
import com.mjuletter.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GoogleVisionService {

    @Value("${spring.cloud.gcp.storage.bucket.filePath}")
    String bucketFilePath;

    private final GoogleCloudUploader googleCloudUploader;

    public ResponseEntity<?> detectTextGcs(MultipartFile file) throws IOException {
        googleCloudUploader.upload(file);

        // TODO(developer): Replace these variables before running the sample.
        String filePath = bucketFilePath + file.getOriginalFilename();

        return detectTextGcs(filePath);
    }

    // Detects text in the specified remote image on Google Cloud Storage.
    public ResponseEntity<?> detectTextGcs(String gcsPath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.

        // Google Cloud Vision API 클라이언트 초기화
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            // 이미지 감지 요청 전송
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            for (AnnotateImageResponse res : response.getResponsesList()) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return ResponseEntity.badRequest().body("Error processing image");
                }

                // 감지된 텍스트 추출
                String detectedText = res.getFullTextAnnotation().getText();

                // 필요한 정보 추출 (학번, 성명, 학부(과))
                String studentNumber = extractValue(detectedText, "학번");
                if (studentNumber != null) {
                    if (isEightDigitNumber(studentNumber)) {
                        // 3번째부터 4번째 자리 추출
                        studentNumber = studentNumber.substring(2, 4);
                    }
                }
                String name = extractValue(detectedText, "한글성명");
                String major = extractValue(detectedText, "학부(과)");

                GoogleVisionRes googleVisionRes = GoogleVisionRes.builder()
                        .name(name)
                        .major(major)
                        .classOf(Integer.parseInt(studentNumber)).build();

                // 추출 완료 후 파일 삭제
                googleCloudUploader.delete(gcsPath);

                ApiResponse apiResponse = ApiResponse.builder()
                        .check(true)
                        .information(googleVisionRes)
                        .build();
                return ResponseEntity.ok(apiResponse);
            }
        } catch (IOException e) {
            googleCloudUploader.delete(gcsPath);

            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
        return ResponseEntity.ok().build();
    }

    // 특정 키워드 다음의 값 추출
    private String extractValue(String text, String keyword) {
        int index = text.indexOf(keyword);
        if (index == -1) {
            return null; // 키워드가 없는 경우
        }
        // 키워드 다음의 값을 추출
        int startIndex = index + keyword.length() + 1;
        int endIndex = text.indexOf("\n", startIndex);
        if (endIndex == -1) {
            endIndex = text.length();
        }
        return text.substring(startIndex, endIndex).trim();
    }

    // 학번이 8자리 숫자인지 확인
    private boolean isEightDigitNumber(String number) {
        return number != null && number.length() == 8 && number.matches("\\d{8}");
    }
}