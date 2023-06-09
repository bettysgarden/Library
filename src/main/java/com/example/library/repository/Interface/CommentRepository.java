package com.example.library.repository.Interface;

import com.example.library.entity.Author;
import com.example.library.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}