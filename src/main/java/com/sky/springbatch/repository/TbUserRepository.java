package com.sky.springbatch.repository;

import com.sky.springbatch.entity.TbUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TbUserRepository extends JpaRepository<TbUser, Long> {
    List<TbUser> findByName(String name);
}
