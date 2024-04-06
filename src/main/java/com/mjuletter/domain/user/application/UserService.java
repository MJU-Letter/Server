package com.mjuletter.domain.user.application;

import com.mjuletter.domain.s3.application.S3Uploader;
import com.mjuletter.domain.user.domain.PictureType;
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

import java.util.Objects;
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
    public ResponseEntity<?> updateUserInfo(UserPrincipal userPrincipal, UpdateUserInfoReq updateUserInfoReq, MultipartFile file) {
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");
        User findUser = user.get();

        String instagram = updateUserInfoReq.getInstagram();
        String updatePictureType = updateUserInfoReq.getPictureType();;

        if (!instagram.isEmpty()) {
            findUser.updateInstagram(instagram);
        }
        if (Objects.equals(updatePictureType, PictureType.CUSTOM.toString())) {
            // 사용자가 직접 프로필을 업로드하는 경우
            s3Uploader.deleteFile(findUser.getPicture());
            String picture = s3Uploader.uploadImage(file);

            findUser.updatePictureType("CUSTOM");
            findUser.updatePicture(picture);
        } else if (Objects.equals(updatePictureType, PictureType.DEFAULT.toString())) {
            // 사용자가 기본 프로필을 설정한 경우
            findUser.updatePictureType("DEFAULT");
            findUser.updatePicture("/img/default_image.png");
        }

        UpdateUserInfoRes updateUserInfoRes = UpdateUserInfoRes.builder()
                .id(findUser.getId())
                .picture(findUser.getPicture())
                .name(file.getName())
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
