package com.techedge.portal.repository;

import com.techedge.portal.entity.Batch;
import com.techedge.portal.entity.enums.BatchMode;
import com.techedge.portal.entity.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByCourseId(Long courseId);

    List<Batch> findByCourseIdAndStatus(
            Long courseId,
            BatchStatus status
    );

    Optional<Batch> findByBatchCode(String batchCode);

    boolean existsByBatchCode(String batchCode);

    long countByCourseIdAndStatus(
            Long courseId,
            BatchStatus status
    );

    @Query("""
        SELECT b
        FROM Batch b
        WHERE (:status IS NULL OR b.status = :status)
          AND (:mode IS NULL OR b.mode = :mode)
        ORDER BY b.startDate ASC
        """)
    List<Batch> findAllFiltered(
            @Param("status") BatchStatus status,
            @Param("mode") BatchMode mode
    );
}