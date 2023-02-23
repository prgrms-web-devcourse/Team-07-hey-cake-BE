package com.programmers.heycake.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.image.model.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
