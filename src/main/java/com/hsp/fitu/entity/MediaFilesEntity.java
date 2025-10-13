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
@Table(name = "media_files")
public class MediaFilesEntity {
    @Id
    private long id;

    @Column(name = "uploader_id")
    private long uploaderId;

    @Column(name = "url")
    private String url;

    @CreationTimestamp
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}
