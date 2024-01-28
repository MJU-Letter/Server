package com.mjuletter.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

public class BaseEntity {

    @CreatedDate
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name="updaated_at")
    private LocalDateTime updatedAt;

    @Enumerated(value= EnumType.STRING)
    @Column(name="status")
    private Status status=Status.ACTIVE;

    public void updateStatus(Status status) {
        this.status = status;
    }
}
