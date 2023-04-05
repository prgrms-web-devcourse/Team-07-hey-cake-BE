package com.programmers.heycake.domain.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.model.vo.ImageType;

public interface ImageRepository extends JpaRepository<Image, Long> {

	List<Image> findAllByReferenceIdAndImageType(Long referenceId, ImageType imageType);

	@Query("SELECT i FROM Image i WHERE i.deletedAt IS NOT NULL")
	List<Image> findDeletedImages();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Image i SET i.deletedAt = NOW() "
			+ "WHERE i.deletedAt IS NULL AND i.referenceId = :referenceId AND i.imageType = :imageType")
	void softDeleteByReferenceIdAndImageType(
			@Param("referenceId") Long referenceId,
			@Param("imageType") ImageType imageType
	);
}
