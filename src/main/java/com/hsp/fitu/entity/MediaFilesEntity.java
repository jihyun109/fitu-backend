package com.hsp.fitu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "media_files")
@NoArgsConstructor
@AllArgsConstructor
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
