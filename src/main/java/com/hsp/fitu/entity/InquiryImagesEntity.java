package com.hsp.fitu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "inquiry_images")
public class InquiryImagesEntity {
    @Id
    private long id;

    @Column(name = "inquire_id")
    private long inquireId;

    @Column(name = "media_id")
    private long mediaId;

    @Column(name = "index")
    private int index;


}
