package com.mjuletter.domain.notification.domain;

import com.mjuletter.domain.common.BaseEntity;
import com.mjuletter.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Notification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;


    @ManyToOne(fetch = FetchType.LAZY) // ManyToOne 관계 설정
    @JoinColumn(name = "recipient_id", nullable = false) // 수신자 ID에 대한 외래 키
    private User recipient; // 수신자 필드 추가

    @Setter
    @Column(name = "is_read", nullable = false)
    private boolean read;

}