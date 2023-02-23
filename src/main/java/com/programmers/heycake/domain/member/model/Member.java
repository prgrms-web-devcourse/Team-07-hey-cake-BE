package com.programmers.heycake.domain.member.model;

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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nickname", length = 20, nullable = false)
	private String nickname;

	@Column(name = "email", length = 254, nullable = false, unique = true)
	private String email;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "birth", nullable = true)
	private String birth;

	@Column(name = "image_url", nullable = false, length = 100)
	private String imageUrl;

	@Builder
	public Member(String nickname, String email, Role role, String birth, String imageUrl) {
		this.nickname = nickname;
		this.email = email;
		this.role = role;
		this.birth = birth;
		this.imageUrl = imageUrl;
	}
}
