package com.programmers.heycake.domain.image.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.programmers.heycake.domain.BaseEntity;
import com.programmers.heycake.domain.image.model.vo.ImageType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "image")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE image SET deleted_at = NOW() WHERE id = ?")
public class Image extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "reference_id", nullable = false)
	private Long referenceId;

	@Column(name = "image_type", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private ImageType imageType;

	@Column(name = "image_url", length = 2000, nullable = false)
	private String imageUrl;

	public Image(Long referenceId, ImageType imageType, String imageUrl) {
		this.referenceId = referenceId;
		this.imageType = imageType;
		this.imageUrl = imageUrl;
	}

	public String getFilename() {
		int beforeFilenameIndex = imageUrl.lastIndexOf("/");
		return imageUrl.substring(beforeFilenameIndex + 1);
	}
}
