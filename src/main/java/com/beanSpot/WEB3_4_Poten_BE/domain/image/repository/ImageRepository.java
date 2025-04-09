package com.beanSpot.WEB3_4_Poten_BE.domain.image.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
