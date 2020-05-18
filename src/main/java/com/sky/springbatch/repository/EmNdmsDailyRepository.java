package com.sky.springbatch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sky.springbatch.entity.EmNdmsDaily;

public interface EmNdmsDailyRepository extends JpaRepository<EmNdmsDaily, Long>{
    Page<EmNdmsDaily> findByAge(int age, Pageable pageable);
}
