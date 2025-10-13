package com.hsp.fitu.repository;

import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.entity.enums.PostCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Slice<PostEntity> findAllByCategoryOrderByIdDesc(PostCategory postCategory, Pageable pageable);
    Slice<PostEntity> findByCategoryAndTitleContainingIgnoreCaseOrCategoryAndContentsContainingIgnoreCase(PostCategory category1, String title, PostCategory category2, String contents, Pageable pageable);

}
