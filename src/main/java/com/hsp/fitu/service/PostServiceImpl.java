package com.hsp.fitu.service;

import com.hsp.fitu.dto.*;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.entity.enums.PostCategory;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.repository.PostCommentRepository;
import com.hsp.fitu.repository.PostRepository;
import com.hsp.fitu.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UniversityRepository universityRepository;
    private final PostCommentService postCommentService;
    private final PostCommentRepository postCommentRepository;

    @Override
    @Transactional
    public PostResponseDTO createPost(long writerId, long universityId, PostCreateRequestDTO requestDTO) {

        PostEntity postEntity = PostEntity.builder()
                .category(requestDTO.category())
                .title(requestDTO.title())
                .contents(requestDTO.contents())
                .universityId(universityId)
                .writerId(writerId)
                .build();

        PostEntity saved = postRepository.save(postEntity);
        return postRepository.findPostWithWriter(saved.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PostSliceResponseDTO<PostListResponseDTO> getAllPosts(PostCategory category, Long universityId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        String universityName = universityRepository.findUniversityNameById(universityId);

        Slice<PostListResponseDTO> posts = postRepository.findPostsByUniversityAndCategory(universityId, category, pageRequest);

        return new PostSliceResponseDTO<>(universityName, posts.getContent(), posts.hasNext());
    }

    @Override
    @Transactional
    public PostDetailResponseDTO getPost(long postId, Long currentUserId) {
        PostResponseDTO postResponseDTO = postRepository.findPostWithWriter(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Long postWriterId = postRepository.findById(postId)
                .map(PostEntity::getWriterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_WRITER_NOT_FOUND));

        List<PostCommentResponseDTO> comments = postCommentService.getComments(postId, currentUserId, postWriterId);

        return new PostDetailResponseDTO(postResponseDTO, comments);
    }

    @Override
    @Transactional
    public PostSliceResponseDTO<PostListResponseDTO> searchPosts(Long universityId, PostCategory category, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        String universityName = universityRepository.findUniversityNameById(universityId);

        Slice<PostListResponseDTO> posts = postRepository.searchPostsByUniversityAndKeyword(universityId, category, keyword, pageRequest);

        return new PostSliceResponseDTO<>(universityName, posts.getContent(), posts.hasNext());
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(long postId, PostUpdateRequestDTO postUpdateRequestDTO) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        postEntity.update(
                postUpdateRequestDTO.title(),
                postUpdateRequestDTO.contents()
        );
        return postRepository.findPostWithWriter(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    @Override
    public void deletePost(long postId) {
        postCommentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

}