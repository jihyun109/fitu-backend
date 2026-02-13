package com.hsp.fitu.repository;

import com.hsp.fitu.entity.SetsEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkoutBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void bulkInsertSets(List<SetsEntity> sets) {
        String sql = "INSERT INTO sets (session_exercise_id, set_index, weight, reps) VALUES (?, ?, ?, ?)";

        // batchUpdate: 드라이버가 지원하는 한 최대한 쿼리를 모아서(Batch) DB로 보냄
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            // 리스트(sets)의 i번째 데이터를 꺼내서 SQL의 ? 자리에 값을 채워
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SetsEntity set = sets.get(i);
                ps.setLong(1, set.getSessionExerciseId());
                ps.setInt(2, set.getSetIndex());
                ps.setInt(3, set.getWeight());
                ps.setInt(4, set.getReps());
            }

            // 총 몇 번 반복할지를 드라이버에게 알려줍
            @Override
            public int getBatchSize() {
                return sets.size();
            }
        });
    }
}
