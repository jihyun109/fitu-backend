package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class BodyPhotoEntity {
    @Id
    private long id;
    private long userId;
    private String imageUrl;
    private LocalDate recordedAt;
}
