package com.mjuletter.domain.letter.domain.repository;

import com.mjuletter.domain.letter.domain.Letter;
import com.mjuletter.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {
    List<Letter> findByRecipient(User recipient);
    List<Letter> findBySender(User sender);
}
