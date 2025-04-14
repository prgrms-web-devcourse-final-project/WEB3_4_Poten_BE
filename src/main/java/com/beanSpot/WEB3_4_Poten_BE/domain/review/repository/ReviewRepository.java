package com.beanSpot.WEB3_4_Poten_BE.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findByCafe(Cafe cafe, Pageable pageable);

	boolean existsByCafeAndMember(Cafe cafe, Member member);

	List<Review> findByCafe(Cafe cafe);

	List<Review> findByMemberId(Long memberId);

	List<Review> findByMember(Member member);

	void deleteByIdIn(List<Long> reviewIds);

	@Query("SELECT r FROM Review r WHERE r.cafe.owner.id = :ownerId")
	List<Review> findByOwnerId(@Param("ownerId") Long ownerId);
}
