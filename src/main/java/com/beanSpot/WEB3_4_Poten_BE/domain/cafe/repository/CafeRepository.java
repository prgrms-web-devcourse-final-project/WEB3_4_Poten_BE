package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

public interface CafeRepository extends JpaRepository<Cafe, Long> {

    @Query("SELECT c FROM Cafe c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Cafe> searchByKeyword(@Param("keyword") String keyword);

    // 특정 주소의 카페가 존재하는지 확인
    boolean existsByNameAndAddress(String name, String address);
}
