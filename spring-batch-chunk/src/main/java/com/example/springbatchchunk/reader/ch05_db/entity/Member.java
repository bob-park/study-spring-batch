package com.example.springbatchchunk.reader.ch05_db.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer age;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Address address;

}
