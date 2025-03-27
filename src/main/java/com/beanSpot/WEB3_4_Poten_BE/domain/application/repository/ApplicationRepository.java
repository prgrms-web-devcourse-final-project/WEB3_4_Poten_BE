package com.beanSpot.WEB3_4_Poten_BE.domain.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	List<Application> findByStatus(Status status);
}
