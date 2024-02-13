package com.mjuletter.global.auth.presentation;

import com.mjuletter.global.auth.dto.request.GoogleOAuthRequest;
import com.mjuletter.global.auth.dto.response.GoogleLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
@RestController
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUrl;

    @GetMapping("/api/v1/oauth2/google")
    public ResponseEntity<Object> loginUrlGoogle(@RequestHeader(value = "User-Agent") String userAgent) throws IOException {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUrl +
                "&response_type=code&scope=email%20profile&access_type=offline";

        log.info("myLog-ClientId : {}", googleClientId);
        log.info("myLog-RedirectUrl : {}", googleRedirectUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(reqUrl));

        log.info("headers : {}", headers);
        // 1.reqUrl 구글로그인 창을 띄우고, 로그인 후 /login/oauth2/code/google 으로 리다이렉션하게 한다.
//        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        return new ResponseEntity<>(headers,HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping(value = "/login/oauth2/code/google")
    public String oauth_google_check(@RequestParam(value = "code") String authCode){



            // 2.구글에 등록된 레드망고 설정정보를 보내어 약속된 토큰을 받위한 객체 생성
            GoogleOAuthRequest googleOAuthRequest = GoogleOAuthRequest
                    .builder()
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .code(authCode)
                    .redirectUri(googleRedirectUrl)
                    .grantType("authorization_code")
                    .build();

            RestTemplate restTemplate = new RestTemplate();

            // 3.토큰요청을 한다.
            ResponseEntity<GoogleLoginResponse> apiResponse = restTemplate.postForEntity("https://oauth2.googleapis.com/token", googleOAuthRequest, GoogleLoginResponse.class);
            // 4.받은 토큰을 토큰객체에 저장
            GoogleLoginResponse googleLoginResponse = apiResponse.getBody();

            log.info("responseBody {}", googleLoginResponse.toString());

            String googleToken = googleLoginResponse.getId_token();

            // 5.받은 토큰을 구글에 보내 유저정보를 얻는다.
            // 6.허가된 토큰의 유저정보를 결과로 받는다.
            String requestUrl = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/oauth2/v3/tokeninfo").queryParam("id_token", googleToken).toUriString();
            String userInfo = restTemplate.getForObject(requestUrl, String.class);

            return userInfo;
    }
}