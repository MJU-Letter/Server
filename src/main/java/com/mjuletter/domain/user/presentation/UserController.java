package com.mjuletter.domain.user.presentation;

import com.mjuletter.domain.user.application.UserService;

import com.mjuletter.domain.user.dto.response.RandomUserResponse;
import com.mjuletter.domain.user.dto.response.RelatedUserResponse;

import com.mjuletter.domain.user.dto.UpdateUserInfoReq;

import com.mjuletter.domain.user.dto.response.UserProfileResponse;
import com.mjuletter.global.config.security.token.CurrentUser;
import com.mjuletter.global.config.security.token.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 정보 수정", description = "사용자의 정보를 수정합니다.")
    @PatchMapping("/update")
    public ResponseEntity<?> updateUserInfo(@CurrentUser UserPrincipal userPrincipal,
                                            @RequestPart UpdateUserInfoReq updateUserInfoReq,
                                            @RequestPart MultipartFile picture) {
        return userService.updateUserInfo(userPrincipal, updateUserInfoReq, picture);
    }

    @Operation(summary = "사용자 정보 조회", description = "마이페이지에서 사용자의 정보를 조회합니다.")
    @GetMapping()
    public ResponseEntity<?> getUserInfo(@CurrentUser UserPrincipal userPrincipal) {
        return userService.getUserInfo(userPrincipal);
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 수행합니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@CurrentUser UserPrincipal userPrincipal) {
        return userService.deleteUser(userPrincipal);
    }

    @Operation(summary = "이메일 수신 여부 변경", description = "이메일 수신 여부를 변경합니다.")
    @PatchMapping("/received-email")
    public ResponseEntity<?> updateReceivedEmail(@CurrentUser UserPrincipal userPrincipal, @RequestParam boolean isReceivedEmail) {
        return userService.updateReceivedEmail(userPrincipal, isReceivedEmail);
    }

    @Operation(summary = "이메일 수신 여부 조회", description = "이메일 수신 여부를 조회합니다.")
    @GetMapping("/received-email")
    public ResponseEntity<?> getReceivedEmail(@CurrentUser UserPrincipal userPrincipal) {
        return userService.getReceivedEmail(userPrincipal);
    }

    @Operation(summary = "나와 관련된 유저 목록 출력", description = "최신 10명의 롤링페이퍼 작성자를 출력하는 API")
    @GetMapping("/related")
    public ResponseEntity<List<RelatedUserResponse>> getLatestRollingPaperWriters(@CurrentUser UserPrincipal userPrincipal) {
        List<RelatedUserResponse> latestUsers = userService.getLatestRollingPaperWriters(userPrincipal.getId(), 10);
        return new ResponseEntity<>(latestUsers, HttpStatus.OK);
    }

    // 일반 사용자들을 랜덤으로 모두 출력하는 API
    @Operation(summary = "랜덤 유저 목록 출력", description = "일반 사용자들을 랜덤으로 모두 출력하는 API")
    @GetMapping("/random")
    public ResponseEntity<List<RandomUserResponse>> getRandomUsers(@CurrentUser UserPrincipal userPrincipal) {
        List<RandomUserResponse> randomUsers = userService.getRandomUsers(userPrincipal.getId());
        return new ResponseEntity<>(randomUsers, HttpStatus.OK);
    }
    @Operation(summary = "유저 이름으로 검색", description = "일반 사용자들을 이름으로 검색하는 API")
    @GetMapping("/name")
    public ResponseEntity<List<UserProfileResponse>> searchUsersByName(@RequestParam String name) {
        List<UserProfileResponse> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    @Operation(summary = "유저 학과명으로 검색", description = "일반 사용자들을 학과명으로 검색하는 API")
    @GetMapping("/major")
    public ResponseEntity<List<UserProfileResponse>> searchUsersByMajor(@RequestParam String major) {
        List<UserProfileResponse> users = userService.searchUsersByMajor(major);
        return ResponseEntity.ok(users);
    }

}