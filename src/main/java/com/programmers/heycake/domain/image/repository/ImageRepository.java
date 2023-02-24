package com.programmers.heycake.domain.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;

public interface ImageRepository extends JpaRepository<Image, Long> {

	Optional<Image> findByReferenceIdAndImageType(Long referenceId, ImageType imageType);

	void deleteByReferenceIdAndImageType(Long referenceId, ImageType imageType);
}
