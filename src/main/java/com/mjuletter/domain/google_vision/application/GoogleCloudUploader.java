package com.mjuletter.domain.google_vision.application;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GoogleCloudUploader {

    @Value("${spring.cloud.gcp.storage.bucket.name}")
    String bucketName;

    //get service by env var GOOGLE_APPLICATION_CREDENTIALS. Json file generated in API & Services -> Service account key
    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    public String upload(MultipartFile file) {
        try {
            BlobInfo blobInfo = storage.create(
                    //Todo: UUID 추가하기 (파일이름 중복)
                    BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).build(), //get original file name
                    file.getBytes() // the file
//                    BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ) // Set file permission
            );
            return blobInfo.getMediaLink(); // Return file url
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String gcsPath) {
        // gcsPath에서 버킷 이름과 객체 이름을 추출
        String[] parts = gcsPath.split("/", 4); // gs://bucketName/objectName
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid GCS path format");
        }
        String bucketName = parts[2];
        String objectName = parts[3];

        BlobId blobId = BlobId.of(bucketName, objectName);

        boolean deleted = storage.delete(blobId);
        if (deleted) {
            System.out.println("File deleted successfully: " + gcsPath);
        } else {
            System.out.println("Failed to delete file: " + gcsPath);
        }
    }

}

