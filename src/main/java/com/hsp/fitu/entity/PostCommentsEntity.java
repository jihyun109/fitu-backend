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
@Table(name = "post_comments")
public class PostCommentsEntity {
    @Id
    private long id;

    @Column(name = "post_id")
    private long postId;

    @Column(name = "writer_id")
    private long writerId;

    @Column(name = "root_id")
    private long rootId;

    @Column(name = "contents")
    private long contents;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
