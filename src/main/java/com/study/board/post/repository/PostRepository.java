package com.study.board.post.repository;

import com.study.board.post.entityy.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {


}
