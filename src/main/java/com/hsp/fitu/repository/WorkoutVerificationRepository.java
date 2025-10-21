package com.hsp.fitu.repository;

import com.hsp.fitu.dto.RankingItem;
import com.hsp.fitu.entity.Total500Info;
import com.hsp.fitu.entity.WorkoutVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutVerificationRepository extends JpaRepository<WorkoutVerificationEntity, Long> {
    @Query(value = """
            SELECT 
                ROW_NUMBER() OVER (ORDER BY SUM(wv.weight) DESC) AS ranking, 
                u.name AS userName, 
                SUM(wv.weight) AS amount, 
                COALESCE(
                        CASE WHEN u.profile_visibility = FALSE THEN NULL ELSE m.url END,
                        'https://fitu-bucket.s3.ap-northeast-2.amazonaws.com/fitu_default_image.png'
                    ) AS profileImageUrl
            FROM workout_verifications wv 
            JOIN users u ON wv.user_id = u.id 
            LEFT JOIN media_files m ON u.profile_img_id = m.id 
            WHERE u.university_id = (SELECT university_id FROM users WHERE id = :userId) 
              AND wv.request_date BETWEEN DATE_FORMAT(CURDATE(), '%Y-%m-01') AND LAST_DAY(CURDATE()) 
              AND wv.status = 'ACCEPTED' 
            GROUP BY u.id, u.name, u.profile_img_id, m.url 
            ORDER BY ranking 
            LIMIT 6
            """, nativeQuery = true)
    List<RankingItem> findTotalRankingByUserId(Long userId);

    //todo: 함수명 findRankingByUserId로 수정
    @Query(value = """
            WITH ranked_users AS (
                SELECT
                    ROW_NUMBER() OVER (ORDER BY SUM(wv.weight) DESC) AS ranking,
                    u.id AS userId,
                    u.name AS userName,
                    SUM(wv.weight) AS amount,
                    COALESCE(
                        CASE WHEN u.profile_visibility = FALSE THEN NULL ELSE m.url END,
                        'https://fitu-bucket.s3.ap-northeast-2.amazonaws.com/fitu_default_image.png'
                    ) AS profileImageUrl
                FROM workout_verifications wv
                JOIN users u ON wv.user_id = u.id
                LEFT JOIN media_files m ON u.profile_img_id = m.id
                WHERE u.university_id = (SELECT university_id FROM users WHERE id = :userId)
                  AND wv.request_date BETWEEN DATE_FORMAT(CURDATE(), '%Y-%m-01') AND LAST_DAY(CURDATE())
                  AND wv.status = 'ACCEPTED'
                GROUP BY u.id, u.name, u.profile_visibility, u.profile_img_id, m.url
            )
            SELECT *
            FROM ranked_users
            WHERE userId = :userId
            """, nativeQuery = true)
    RankingItem getMyRanking(Long userId);

    @Query(value = """
        SELECT 
            IFNULL(MAX(CASE WHEN workout_type = 'SQUAT' THEN weight END), 0) AS squat,
            IFNULL(MAX(CASE WHEN workout_type = 'DEADLIFT' THEN weight END), 0) AS deadLift,
            IFNULL(MAX(CASE WHEN workout_type = 'BENCH_PRESS' THEN weight END), 0) AS benchPress
        FROM workout_verifications
        WHERE user_id = :userId
          AND status = 'ACCEPTED'
          AND request_date BETWEEN DATE_FORMAT(CURDATE(), '%Y-%m-01') AND LAST_DAY(CURDATE())
        """, nativeQuery = true)
    Total500Info findTotal500InfoByUserId(Long userId);
}
