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
@Table(name = "tmp_ndms_daily")
public class EmTmpNdmsDaily {
	
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
	
	public EmTmpNdmsDaily() {
		super();
	}
	
	@Builder
	public EmTmpNdmsDaily(String name,
					   Integer age,
					   String phoneNumber) {
		this.name = name;
		this.age = age;
		this.phoneNumber = phoneNumber;		
	}

}
