package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long> {

    // 특정 주소의 카페가 존재하는지 확인
    boolean existsByNameAndAddress(String name, String address);

    @Query("SELECT c FROM Cafe c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Cafe> searchByKeyword(@Param("keyword") String keyword);
}
