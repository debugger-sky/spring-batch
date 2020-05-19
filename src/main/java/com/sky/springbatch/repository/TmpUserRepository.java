package com.sky.springbatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sky.springbatch.entity.TmpUser;

public interface TmpUserRepository extends JpaRepository<TmpUser, Long> {
	
	@Query(value="truncate table tmp_ndms_daily", nativeQuery=true)
	public List<TmpUser> truncateTmpNdmsDaily();

}
