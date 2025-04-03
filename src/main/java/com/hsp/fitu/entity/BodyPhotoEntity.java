package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "body_photos")
public class BodyPhotoEntity {
    @Id
    private long id;
    private long userId;
    private String imageUrl;
    private LocalDate recordedAt;
}
