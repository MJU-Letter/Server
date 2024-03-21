package com.mjuletter.domain.user.application;

import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.domain.repository.UserRepository;
import com.mjuletter.global.DefaultAssert;
import com.mjuletter.global.config.security.token.UserPrincipal;
import com.mjuletter.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

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


}
