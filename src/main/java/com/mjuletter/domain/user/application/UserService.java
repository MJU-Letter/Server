package com.mjuletter.domain.user.application;

import com.mjuletter.domain.s3.application.S3Uploader;
import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.domain.user.dto.UpdateUserInfoReq;
import com.mjuletter.domain.user.dto.UpdateUserInfoRes;
import com.mjuletter.domain.user.dto.UserInfoRes;
import com.mjuletter.global.DefaultAssert;
import com.mjuletter.global.config.security.token.UserPrincipal;
import com.mjuletter.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    // 탈퇴하기
    @Transactional
    public ResponseEntity<?> deleteUser(UserPrincipal userPrincipal) {

        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        if (!findUser.getPicture().contains("amazonaws.com/")) {
            String originalFile = findUser.getPicture().split("amazonaws.com/")[1];
            s3Uploader.deleteFile(originalFile);
        }

        userRepository.delete(findUser);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("회원탈퇴가 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 마이페이지 정보 조회
    public ResponseEntity<?> getUserInfo(UserPrincipal userPrincipal) {
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        UserInfoRes userRes = UserInfoRes.builder()
                .id(findUser.getId())
                .name(findUser.getName())
                .picture(findUser.getPicture())
                .major(findUser.getMajor())
                .classOf(findUser.getClassOf())
                .instagram(findUser.getInstagram())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(userRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 프로필 수정
    @Transactional
    public ResponseEntity<?> updateUserInfo(UserPrincipal userPrincipal, UpdateUserInfoReq updateUserInfoReq, MultipartFile file) {
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        // 인스타그램 수정
        if (updateUserInfoReq.getInstagram() != null) {
            findUser.updateInstagram(updateUserInfoReq.getInstagram());
        }
        // 프로필 수정
        if (updateUserInfoReq.getIsDefault() != null) {
            updateUserProfile(findUser, updateUserInfoReq.getIsDefault(), file);
        }

        UpdateUserInfoRes updateUserInfoRes = UpdateUserInfoRes.builder()
                .id(findUser.getId())
                .picture(findUser.getPicture())
                .name(findUser.getName())
                .major(findUser.getMajor())
                .classOf(findUser.getClassOf())
                .instagram(findUser.getInstagram())
                .email(findUser.getEmail())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(updateUserInfoRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private void updateUserProfile(User user, Boolean isDefault, MultipartFile file) {
        if (!isDefault) {
            // 사용자가 직접 프로필을 업로드하는 경우
            if ((user.getPicture()).contains("amazonaws.com/")) {
                String originalFile = user.getPicture().split("amazonaws.com/")[1];
                s3Uploader.deleteFile(originalFile);
            }
            String picture = s3Uploader.uploadImage(file);
            user.updatePicture(picture);
        }
         else {
            // 사용자가 기본 프로필을 설정한 경우
            user.updatePicture("/img/default_image.png");
        }
    }

    // 이메일 알림 수신 허용
    @Transactional
    public ResponseEntity<?> updateReceivedEmail(UserPrincipal userPrincipal, boolean isReceivedEmail) {
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        findUser.updateReceivedEmail(isReceivedEmail);

        userRepository.save(findUser);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(findUser.isReceivedEmail())
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    public ResponseEntity<?> getReceivedEmail(UserPrincipal userPrincipal) {
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(findUser.isReceivedEmail())
                .build();

        return ResponseEntity.ok(apiResponse);
    }


}
