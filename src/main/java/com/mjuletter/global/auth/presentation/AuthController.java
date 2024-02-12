package com.mjuletter.global.auth.presentation;

import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.global.auth.dto.request.GoogleRequest;
import com.mjuletter.global.auth.dto.response.GoogleInfoResponse;
import com.mjuletter.global.auth.dto.response.GoogleResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class AuthController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUrl;

    @GetMapping("/api/v1/oauth2/google")
    public void loginUrlGoogle(HttpServletResponse response) throws IOException {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUrl +
                "&response_type=code&scope=email%20profile%20openid&access_type=offline";

        // Redirect
        response.sendRedirect(reqUrl);
    }

    @PostMapping("/api/v1/oauth2/google")
    public String loginGoogle(@RequestParam("code") String authCode) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Google OAuth Token 발급 요청
            GoogleRequest googleOAuthRequestParam = GoogleRequest
                    .builder()
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .code(authCode)
                    .redirectUri(googleRedirectUrl)
                    .grantType("authorization_code").build();
            ResponseEntity<GoogleResponse> resultEntity = restTemplate.postForEntity(
                    "https://oauth2.googleapis.com/token",
                    googleOAuthRequestParam, GoogleResponse.class);

            String accessToken = resultEntity.getBody().getAccess_token();

            // 로그 추가: 획득한 Access Token을 출력
            System.out.println("Access Token: " + accessToken);

            // TODO: 여기서 획득한 Access Token을 이용하여 사용자 정보를 처리하는 로직 추가
            // 획득한 Access Token을 사용하여 필요한 작업을 수행하면 됩니다.

            // Google의 UserInfo 엔드포인트에 Access Token을 사용하여 사용자 정보를 요청
            Map<String, String> userInfoParams = new HashMap<>();
            userInfoParams.put("access_token", accessToken);
            ResponseEntity<GoogleInfoResponse> resultEntity2 = restTemplate.getForEntity(
                    "https://www.googleapis.com/oauth2/v2/userinfo?access_token={access_token}",
                    GoogleInfoResponse.class, userInfoParams);

            String email = resultEntity2.getBody().getEmail();

            // 로그 추가: 획득한 Email을 출력
            System.out.println("Email: " + email);

            // TODO: 이후에 email 등의 사용자 정보를 처리하는 로직 추가
            // 예시: DB에 해당 이메일을 가지는 사용자가 있는지 확인하고, 없다면 새로운 사용자로 등록
//        Optional<User> existingUser = userRepository.findByEmail(email);
//        if (existingUser.isPresent()) {
//            // 이미 등록된 사용자
//            // TODO: 사용자 정보에 따른 처리 추가
//        } else {
//            // 새로운 사용자 등록
//            User newUser = new User(); // 사용자 정보를 적절히 채워서
//            userRepository.save(newUser);
//            // TODO: 새로운 사용자 등록에 따른 처리 추가
//        }

            return email;
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }


}
