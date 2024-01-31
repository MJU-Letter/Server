package com.mjuletter.domain.user.application;

import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public boolean checkEmailDuplicate(String email) {
        // 로그인 아이디가 이미 존재하는지 확인
        return userRepository.existsByEmail(email);
    }

    public boolean checkNameDuplicate(String name) {
        // 닉네임이 이미 존재하는지 확인
        return userRepository.existsByName(name);
    }

    public User getLoginUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

}
