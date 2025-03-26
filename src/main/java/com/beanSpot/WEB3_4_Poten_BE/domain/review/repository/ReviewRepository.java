package com.beanSpot.WEB3_4_Poten_BE.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByCafeId(Long cafeId);
}
