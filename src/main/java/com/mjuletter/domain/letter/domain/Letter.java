package com.mjuletter.domain.letter.domain;

import com.mjuletter.domain.common.BaseEntity;
import com.mjuletter.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Letter")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Letter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous; // 익명 여부

    @Builder
    public Letter(String content, User sender, User recipient, boolean anonymous) {
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
        this.anonymous = anonymous;
    }
}
