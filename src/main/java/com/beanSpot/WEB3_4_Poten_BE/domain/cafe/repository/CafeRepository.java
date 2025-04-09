package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {

	@Query("SELECT c FROM Cafe c WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND c.disabled = false")
	List<Cafe> searchByKeywordAndDisabledFalse(@Param("keyword") String keyword);

	List<Cafe> findAllByDisabledFalse();

	boolean existsByNameAndAddress(String name, String address);

	Optional<Cafe> findBycafeIdAndDisabledFalse(Long cafeId);
}

