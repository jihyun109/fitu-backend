package com.hsp.fitu.repository;

import com.hsp.fitu.dto.PostListResponseDTO;
import com.hsp.fitu.dto.PostResponseDTO;
import com.hsp.fitu.entity.PostEntity;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.enums.PostCategory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager em;

    private static final long UNIVERSITY_A = 1L;
    private static final long UNIVERSITY_B = 2L;

    @BeforeEach
    void setUp() {
        // 대학 A 유저
        UserEntity writerA = UserEntity.builder()
                .id(1L)
                .name("유저A")
                .build();
        em.persist(writerA);

        // 대학 B 유저
        UserEntity writerB = UserEntity.builder()
                .id(2L)
                .name("유저B")
                .build();
        em.persist(writerB);

        // 대학 A - FREE_BOARD 게시글 2개
        em.persist(PostEntity.builder()
                .universityId(UNIVERSITY_A)
                .writerId(1L)
                .category(PostCategory.FREE_BOARD)
                .title("운동 자유게시글")
                .contents("내용1")
                .build());

        em.persist(PostEntity.builder()
                .universityId(UNIVERSITY_A)
                .writerId(1L)
                .category(PostCategory.FREE_BOARD)
                .title("스쿼트 팁 공유")
                .contents("스쿼트 자세 교정법")
                .build());

        // 대학 A - WORKOUT_INFO 게시글 1개
        em.persist(PostEntity.builder()
                .universityId(UNIVERSITY_A)
                .writerId(1L)
                .category(PostCategory.WORKOUT_INFO)
                .title("운동 정보 게시글")
                .contents("내용3")
                .build());

        // 대학 B - FREE_BOARD 게시글 1개 (격리 검증용)
        em.persist(PostEntity.builder()
                .universityId(UNIVERSITY_B)
                .writerId(2L)
                .category(PostCategory.FREE_BOARD)
                .title("대학B 게시글")
                .contents("내용4")
                .build());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("대학 A의 FREE_BOARD 조회 시 대학 B 게시글은 포함되지 않는다")
    void findPostsByUniversityAndCategory_excludesOtherUniversity() {
        Slice<PostListResponseDTO> result = postRepository.findPostsByUniversityAndCategory(
                UNIVERSITY_A, PostCategory.FREE_BOARD, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(2)
                .noneMatch(p -> p.writerName().equals("유저B"));
    }

    @Test
    @DisplayName("카테고리 필터링 - WORKOUT_INFO 조회 시 FREE_BOARD 게시글은 포함되지 않는다")
    void findPostsByUniversityAndCategory_filtersByCategory() {
        Slice<PostListResponseDTO> result = postRepository.findPostsByUniversityAndCategory(
                UNIVERSITY_A, PostCategory.WORKOUT_INFO, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).category()).isEqualTo(PostCategory.WORKOUT_INFO);
    }

    @Test
    @DisplayName("게시글 목록은 id 내림차순(최신순)으로 반환된다")
    void findPostsByUniversityAndCategory_orderedByIdDesc() {
        Slice<PostListResponseDTO> result = postRepository.findPostsByUniversityAndCategory(
                UNIVERSITY_A, PostCategory.FREE_BOARD, PageRequest.of(0, 10));

        var posts = result.getContent();
        assertThat(posts.get(0).id()).isGreaterThan(posts.get(1).id());
    }

    @Test
    @DisplayName("게시글 상세 조회 시 작성자 이름이 포함된다")
    void findPostWithWriter_includesWriterName() {
        long postId = postRepository.findPostsByUniversityAndCategory(
                        UNIVERSITY_A, PostCategory.FREE_BOARD, PageRequest.of(0, 1))
                .getContent().get(0).id();

        Optional<PostResponseDTO> result = postRepository.findPostWithWriter(postId);

        assertThat(result).isPresent();
        assertThat(result.get().writerName()).isEqualTo("유저A");
    }

    @Test
    @DisplayName("제목 키워드 검색 - '스쿼트'로 검색하면 해당 게시글만 반환된다")
    void searchPosts_byTitleKeyword_returnsMatchingPosts() {
        Slice<PostListResponseDTO> result = postRepository.searchPostsByUniversityAndKeyword(
                UNIVERSITY_A, PostCategory.FREE_BOARD, "스쿼트", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("스쿼트 팁 공유");
    }

    @Test
    @DisplayName("본문 키워드 검색 - contents에 포함된 키워드로도 검색된다")
    void searchPosts_byContentsKeyword_returnsMatchingPosts() {
        Slice<PostListResponseDTO> result = postRepository.searchPostsByUniversityAndKeyword(
                UNIVERSITY_A, PostCategory.FREE_BOARD, "자세 교정", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("스쿼트 팁 공유");
    }

    @Test
    @DisplayName("키워드 검색은 대학 범위 내에서만 수행된다")
    void searchPosts_limitedToUniversity() {
        // "대학B"라는 키워드가 대학B 게시글 제목에 있어도 대학A 범위 검색에는 안 나온다
        Slice<PostListResponseDTO> result = postRepository.searchPostsByUniversityAndKeyword(
                UNIVERSITY_A, PostCategory.FREE_BOARD, "대학B", PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }
}
