package com.hsp.fitu.repository;

import com.hsp.fitu.dto.RankingItem;
import com.hsp.fitu.entity.SessionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionsEntity, Long> {
    List<SessionsEntity> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    Optional<SessionsEntity> findFirstByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value = """
            SELECT ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT s.id) DESC, SUM(s.end_time - s.start_time) DESC) AS ranking,
            		u.name AS userName,
            		COUNT(s.id) AS amount,
            		COALESCE(
            			CASE WHEN u.profile_visibility = FALSE THEN NULL ELSE m.url END,
            			'https://fitu-bucket.s3.ap-northeast-2.amazonaws.com/fitu_default_image.png'
            			) AS profileImageUrl
            FROM sessions s
            JOIN users u ON s.user_id = u.id
            LEFT JOIN media_files m ON u.profile_img_id = m.id
            WHERE u.university_id = (SELECT university_id FROM users u2 WHERE u2.id = :userId)
            	AND s.start_time BETWEEN DATE_FORMAT(CURDATE(), '%Y-%m-01') AND LAST_DAY(CURDATE())
            GROUP BY u.id
            ORDER BY ranking
            LIMIT 10
            """, nativeQuery = true)
    List<RankingItem> findAllRankingWorkoutCountByUserId(Long userId);

    @Query(value = """
            WITH ranked_users AS (
            	SELECT
            		ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT s.id) DESC, SUM(s.end_time - s.start_time) DESC) AS ranking,
            		u.id AS userId,
            		u.name AS userName,
            		COUNT(s.id) AS amount,
            		COALESCE(
            			CASE WHEN u.profile_visibility = FALSE THEN NULL ELSE m.url END,
            				'https://fitu-bucket.s3.ap-northeast-2.amazonaws.com/fitu_default_image.png'
            			) AS profileImageUrl
            	FROM sessions s
            	JOIN users u ON s.user_id = u.id
            	LEFT JOIN media_files m ON u.profile_img_id = m.id
            	WHERE u.university_id = (SELECT university_id FROM users u2 WHERE u2.id = :userId)
            		AND s.start_time BETWEEN DATE_FORMAT(CURDATE(), '%Y-%m-01') AND LAST_DAY(CURDATE())
            	GROUP BY u.id
            	ORDER BY ranking
            )
            SELECT *
            FROM ranked_users
            WHERE userId = :userId
            """, nativeQuery = true)
    RankingItem findRankingWorkoutCountByUserId(Long userId);
}
