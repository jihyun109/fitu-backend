package com.hsp.fitu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "universities")
public class UniversityEntity {
    @Id
    private Long id;
    private String name;
    private String domainName;
}
