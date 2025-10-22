package com.hsp.fitu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "inquiries")
public class InquiriesEntity {
    @Id
    private long id;

    @Column(name = "university_id")
    private long universityId;

    @Column(name = "writer_id")
    private long writerId;

    @Column(name = "contents")
    private long contents;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_resolved")
    private Boolean isResolved;
}
