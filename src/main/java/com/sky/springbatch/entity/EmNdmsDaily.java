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
@Table(name = "tb_ndms_daily")
public class EmNdmsDaily {
	
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
	
	public EmNdmsDaily() {
		super();
	}
	
	@Builder
	public EmNdmsDaily(/* String id, */
					   String name,
					   Integer age,
					   String phoneNumber) {
//		this.id = Long.parseLong(id);
		this.name = name;
		this.age = age;
		this.phoneNumber = phoneNumber;		
	}
}
