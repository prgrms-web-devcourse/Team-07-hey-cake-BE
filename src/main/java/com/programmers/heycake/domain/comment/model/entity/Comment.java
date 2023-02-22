package com.programmers.heycake.domain.comment.model.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.programmers.heycake.domain.BaseEntity;
import com.programmers.heycake.domain.thread.model.entity.Thread;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "comment")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "content", length = 500, nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "thread_id", referencedColumnName = "id")
	private Thread thread;

	public Comment(Long memberId, String content) {
		this.memberId = memberId;
		this.content = content;
	}

	public void setThread(Thread thread) {
		if (Objects.nonNull(this.thread)) {
			this.thread.getComments().remove(this);
		}
		this.thread = thread;
		thread.getComments().add(this);
	}
}
