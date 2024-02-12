package com.mjuletter.global.auth.application;

import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.global.auth.dto.SessionUser;
import com.mjuletter.global.auth.dto.OAuthAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2UserService를 이용하여 OAuth2User 정보를 가져옵니다.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 사용되는 필드 값(주로 Primary Key와 같은 의미)
        // 구글의 경우 기본적으로 'sub'를 사용
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // OAuthAttributes 클래스를 사용하여 OAuth2User의 속성(Attribute)을 가져옵니다.
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 사용자 정보를 저장 또는 업데이트합니다.
        User user = saveOrUpdate(attributes);

        // 세션에 사용자 정보를 저장합니다.
        httpSession.setAttribute("user", new SessionUser(user));

        // Spring Security의 DefaultOAuth2User를 반환합니다.
        // 여기서는 권한 정보, 사용자의 속성(Attribute), 이름을 포함합니다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }



    // 사용자 정보를 저장 또는 업데이트하는 메서드
    @Transactional
    protected User saveOrUpdate(OAuthAttributes attributes) {
        // 사용자 이메일을 기반으로 이미 가입된 사용자인지 확인하고 업데이트 또는 신규 가입 처리
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        log.info("Saving or updating user: {}", user);
        // UserRepository를 통해 사용자 정보를 저장하고 반환합니다.
        return userRepository.save(user);
    }
}
