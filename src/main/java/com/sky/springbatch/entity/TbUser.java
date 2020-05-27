package com.sky.springbatch.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tb_user")
public class TbUser {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_uniq_id")
    private String userId;

    @Column(name = "passwd")
    private String passwd;

    @Column(name = "name")
    private String name;

    @Column(name = "int_value")
    private Integer intValue;

}
