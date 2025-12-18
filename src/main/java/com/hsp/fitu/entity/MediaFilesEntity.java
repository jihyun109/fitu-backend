package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.MediaCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "media_files")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MediaFilesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uploader_id")
    private Long uploaderId;

    @Column(name = "url")
    private String url;

    @CreationTimestamp
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Enumerated(EnumType.STRING)
    private MediaCategory category;
}
