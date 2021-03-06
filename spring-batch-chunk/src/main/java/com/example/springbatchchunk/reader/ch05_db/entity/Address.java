package com.example.springbatchchunk.reader.ch05_db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String location;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
