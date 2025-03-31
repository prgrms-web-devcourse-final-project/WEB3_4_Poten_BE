package com.beanSpot.WEB3_4_Poten_BE.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
