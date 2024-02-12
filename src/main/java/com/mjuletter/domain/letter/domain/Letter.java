package com.mjuletter.domain.letter.domain;

import com.mjuletter.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Letter")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Letter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="content")
    private String content;

    @Builder
    public Letter(String content){
        this.content=content;
    }
}
