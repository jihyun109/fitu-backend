package com.hsp.fitu.entity;

import com.hsp.fitu.entity.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserEntity {
    @Id
    private long id;
    private int height;
    private int weight;
    private Gender gender;
}
