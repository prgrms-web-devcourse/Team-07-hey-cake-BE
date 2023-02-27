package com.programmers.heycake.domain.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;

public interface ImageRepository extends JpaRepository<Image, Long> {

	List<Image> findAllByReferenceIdAndImageType(Long referenceId, ImageType imageType);
}
