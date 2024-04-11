package com.mjuletter.domain.user.application;

import com.mjuletter.domain.s3.application.S3Uploader;
import com.mjuletter.domain.user.domain.PictureType;
import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.domain.user.dto.UpdateUserInfoReq;
import com.mjuletter.domain.user.dto.UpdateUserInfoRes;
import com.mjuletter.domain.user.dto.UserInfoRes;
import com.mjuletter.domain.user.dto.response.RandomUserResponse;
import com.mjuletter.domain.user.dto.response.RelatedUserResponse;
import com.mjuletter.global.DefaultAssert;
import com.mjuletter.global.config.security.token.UserPrincipal;
import com.mjuletter.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if (findUser.getPictureType() == PictureType.CUSTOM) {
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
        if (updateUserInfoReq.getPictureType() != null) {
            updateUserProfile(findUser, updateUserInfoReq.getPictureType(), file);
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

    private void updateUserProfile(User user, String type, MultipartFile file) {
        if (Objects.equals(type, PictureType.CUSTOM.toString())) {
            // 사용자가 직접 프로필을 업로드하는 경우
            if (user.getPictureType() == PictureType.CUSTOM) {
                String originalFile = user.getPicture().split("amazonaws.com/")[1];
                s3Uploader.deleteFile(originalFile);
            }
            String picture = s3Uploader.uploadImage(file);

            user.updatePictureType("CUSTOM");
            user.updatePicture(picture);
        }
         else if (Objects.equals(type, PictureType.DEFAULT.toString())) {
            // 사용자가 기본 프로필을 설정한 경우
            user.updatePictureType("DEFAULT");
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

    public List<RelatedUserResponse> getLatestRollingPaperWriters(Long userId, int count) {
        List<RelatedUserResponse> latestUsers = userRepository.findLatestRollingPaperWriters(userId, count);
        return latestUsers.stream()
                .map(user -> RelatedUserResponse.builder()
                        .id(user.getId())
                        .picture(user.getPicture())
                        .name(user.getName())
                        .major(user.getMajor())
                        .classOf(user.getClassOf())
                        .build())
                .collect(Collectors.toList());
    }

    public List<RandomUserResponse> getRandomUsers(Long userId) {
        List<RandomUserResponse> randomUsers = userRepository.findRandomUsers(userId);
        return randomUsers.stream()
                .map(user -> RandomUserResponse.builder()
                        .id(user.getId())
                        .picture(user.getPicture())
                        .name(user.getName())
                        .major(user.getMajor())
                        .classOf(user.getClassOf())
                        .build())
                .collect(Collectors.toList());
    }

    // 프로필 수정
    // S3 설정하고 추가 진행

}
