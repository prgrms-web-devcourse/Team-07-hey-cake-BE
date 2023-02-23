package com.programmers.heycake.domain.member.model.entity;

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
import com.programmers.heycake.domain.member.model.vo.MemberAuthority;

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
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nickname", length = 20, nullable = false)
	private String nickname;

	@Column(name = "email", length = 254, nullable = false, unique = true)
	private String email;

	@Column(name = "member_role", nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberAuthority memberAuthority;

	@Column(name = "birth", length = 4, nullable = true)
	private String birth;

	@Column(name = "image_url", length = 100, nullable = false)
	private String imageUrl;

	@Builder
	public Member(String nickname, String email, MemberAuthority memberAuthority, String birth, String imageUrl) {
		this.nickname = nickname;
		this.email = email;
		this.memberAuthority = memberAuthority;
		this.birth = birth;
		this.imageUrl = imageUrl;
	}
}
