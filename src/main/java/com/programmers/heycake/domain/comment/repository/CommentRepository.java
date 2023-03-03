package com.programmers.heycake.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.comment.model.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
