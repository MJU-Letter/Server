package com.mjuletter.domain.auth.application;

import com.mjuletter.domain.auth.domain.Token;
import com.mjuletter.domain.auth.domain.repository.TokenRepository;
import com.mjuletter.domain.auth.dto.*;
import com.mjuletter.domain.s3.application.S3Uploader;
import com.mjuletter.domain.user.domain.Role;
import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.global.DefaultAssert;
import com.mjuletter.global.config.security.token.UserPrincipal;
import com.mjuletter.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final CustomTokenProviderService customTokenProviderService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> signUp(SignUpReq signUpReq, MultipartFile picture) {

        User user = User.builder()
                .email(signUpReq.getEmail())
                .password(passwordEncoder.encode(signUpReq.getPassword()))
                .name(signUpReq.getName())
                .major(signUpReq.getMajor())
                .classOf(signUpReq.getClassOf())
                .picture(setPicture(signUpReq.getIsDefault(), picture))
                .instagram(signUpReq.getInstagram())
                .role(Role.USER)
                .isReceivedEmail(true)
                .build();

        userRepository.save(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("회원가입이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    private String setPicture(Boolean isDefault, MultipartFile picture) {
        if (isDefault) {
            return "/img/default_image.png";
        } else if (!isDefault && !picture.isEmpty()) {
            return s3Uploader.uploadImage(picture);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 입력입니다.");
        }
    }

    @Transactional
    public ResponseEntity<?> refresh(RefreshTokenReq tokenRefreshRequest){
        //1차 검증
        boolean checkValid = valid(tokenRefreshRequest.getRefreshToken());
        DefaultAssert.isAuthentication(checkValid);

        Token token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken())
                .orElseThrow(RuntimeException::new);
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.getUserEmail());

        //refresh token 정보 값을 업데이트 한다.
        //시간 유효성 확인
        TokenMapping tokenMapping;

        Long expirationTime = customTokenProviderService.getExpiration(tokenRefreshRequest.getRefreshToken());
        if(expirationTime > 0){
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.getRefreshToken());
        }else{
            tokenMapping = customTokenProviderService.createToken(authentication);
        }

        Token updateToken = token.updateRefreshToken(tokenMapping.getRefreshToken());

        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(updateToken.getRefreshToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> signOut(UserPrincipal userPrincipal){
        Token token = tokenRepository.findByUserEmail(userPrincipal.getEmail())
                .orElseThrow(RuntimeException::new);

        tokenRepository.delete(token);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("유저가 로그아웃 되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    private boolean valid(String refreshToken){

        //1. 토큰 형식 물리적 검증
        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);
        DefaultAssert.isTrue(validateCheck, "Token 검증에 실패하였습니다.");

        //2. refresh token 값을 불러온다.
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "탈퇴 처리된 회원입니다.");

        //3. email 값을 통해 인증값을 불러온다
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());
        DefaultAssert.isTrue(token.get().getUserEmail().equals(authentication.getName()), "사용자 인증에 실패하였습니다.");

        return true;
    }

    public ResponseEntity<?> signIn(SignInReq signInReq) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInReq.getEmail(),
                        signInReq.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .userEmail(tokenMapping.getUserEmail())
                .build();
        tokenRepository.save(token);

        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authResponse).build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> checkEmailDuplicate(String email) {
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(userRepository.existsByEmail(email)).build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> checkPassword(CheckPasswordReq checkPasswordReq) {
        boolean isEqual = Objects.equals(checkPasswordReq.getPassword(), checkPasswordReq.getCheckPassword());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(isEqual)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
