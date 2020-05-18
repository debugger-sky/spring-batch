package com.sky.springbatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sky.springbatch.entity.EmTmpNdmsDaily;

public interface EmTmpNdmsDailyRepository extends JpaRepository<EmTmpNdmsDaily, Long> {
	
	@Query(value="truncate table tmp_ndms_daily", nativeQuery=true)
	public List<EmTmpNdmsDaily> truncateTmpNdmsDaily();

}
