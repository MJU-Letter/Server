package com.mjuletter.domain.user.domain.repository;

import com.mjuletter.domain.user.domain.User;
import com.mjuletter.domain.user.dto.respnse.RandomUserResponse;
import com.mjuletter.domain.user.dto.respnse.RelatedUserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByName(String name);

    @Query(value = "SELECT u FROM User u WHERE u.id != ?1 ORDER BY u.createdAt DESC")
    List<RelatedUserResponse> findLatestRollingPaperWriters(Long userId, int count);

    @Query(value = "SELECT u FROM User u WHERE u.id != ?1 ORDER BY RAND()")
    List<RandomUserResponse> findRandomUsers(Long userId);
}
