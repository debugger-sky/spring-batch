package com.sky.springbatch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sky.springbatch.entity.TbTmpUser;

public interface TbTmpUserRepository extends JpaRepository<TbTmpUser, Long>{
    Page<TbTmpUser> findByAge(int age, Pageable pageable);
}
