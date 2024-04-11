package com.mjuletter.domain.user.domain;

import com.mjuletter.domain.common.BaseEntity;
import com.mjuletter.domain.letter.domain.Letter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name="picture")
    private String picture;

    @Enumerated(EnumType.STRING) // Enum 타입은 문자열 형태로 저장해야 함
    @Column(name="role")
    private Role role;

    @Enumerated(EnumType.STRING) // Enum 타입은 문자열 형태로 저장해야 함
    @Column(name="picture_type")
    private PictureType pictureType;

    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "class_of", nullable = false)
    private int classOf;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "is_received_email", nullable = false)
    private boolean isReceivedEmail;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Letter> sentLetters = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Letter> receivedLetters = new ArrayList<>();

    @Builder
    public User(Long id, String name, String email, String password, String picture, Role role, PictureType pictureType, String major, int classOf, String instagram, boolean isReceivedEmail) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.role = role;

        this.pictureType = pictureType;
        this.major=major;
        this.classOf=classOf;

        this.instagram = instagram;
        this.isReceivedEmail = isReceivedEmail;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public void updateInstagram(String insta) { instagram = insta; }
    public void updatePicture(String file) { picture = file; }

    public void updatePictureType(String type) { pictureType = PictureType.valueOf(type); }
    public void updateReceivedEmail(boolean receivedEmail) {
        isReceivedEmail = receivedEmail;
    }
}
