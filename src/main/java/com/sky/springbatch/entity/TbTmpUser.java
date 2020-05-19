package com.sky.springbatch.entity;


import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "tb_tmp_user")
public class TbTmpUser {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "age")
	private Integer age;
	
	@Column(name = "phoneNumber")
	private String phoneNumber;
	
	@Column(name = "createdDate")
	private LocalDateTime createdDate = LocalDateTime.now();
	
	@Column(name = "updatedDate")
	private LocalDateTime updatedDate;
	
	public TbTmpUser() {
		super();
	}
	
	@Builder
	public TbTmpUser(/* String id, */
					   String name,
					   Integer age,
					   String phoneNumber) {
//		this.id = Long.parseLong(id);
		this.name = name;
		this.age = age;
		this.phoneNumber = phoneNumber;		
	}
}
