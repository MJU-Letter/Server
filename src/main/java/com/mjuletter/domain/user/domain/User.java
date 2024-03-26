package com.mjuletter.domain.user.domain;

import com.mjuletter.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name="picture", nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING) // Enum 타입은 문자열 형태로 저장해야 함
    @Column(name="role")
    private Role role;

    @Column(name = "major")
    private String major;

    @Column(name = "class_of")
    private int classOf;

    @Column(name = "instagram")
    private String instagram;

    @Builder
    public User(Long id, String name, String email, String password, String picture, Role role, String major, int classOf, String instagram) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.role = role;
        this.major=major;
        this.classOf=classOf;
        this.instagram = instagram;
    }
    public String getRoleKey() {
        return this.role.getKey();
    }

}
