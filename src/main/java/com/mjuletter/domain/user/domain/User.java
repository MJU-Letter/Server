package com.mjuletter.domain.user.domain;

import com.mjuletter.domain.letter.domain.Letter;
import com.mjuletter.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Entity
@Table(name="User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="picture", nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING) // Enum 타입은 문자열 형태로 저장해야 함
    @Column(name="role")
    private Role role;

    @Column(name = "major")
    private String major;

    @Column(name = "class_of")
    private int classOf;

    @Column(name = "agree")
    private boolean agree;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="writer_id")
    private ArrayList<Letter> letters;

    @Builder
    public User(String name, String email, String picture, Role role, String major, int classOf, boolean agree) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.major=major;
        this.classOf=classOf;
        this.agree=agree;
    }

    public User updatePicture(String picture) {
        this.picture = picture;
        return this;
    }

    public User updateName(String name, String picture) {
        this.name = name;
        return this;
    }

    public User updateAgree(boolean isAgree) {
        this.agree = isAgree;
        return this;
    }

    public User update(String name, String picture) {
        this.name=name;
        this.picture=picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

}
