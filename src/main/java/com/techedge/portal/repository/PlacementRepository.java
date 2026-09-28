package com.techedge.portal.repository;

import com.techedge.portal.entity.Placement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface PlacementRepository
        extends JpaRepository<Placement, Long> {

    @Query("""
            SELECT p
            FROM Placement p
            WHERE (:companyId IS NULL
                   OR p.company.id = :companyId)
              AND (:courseId IS NULL
                   OR p.course.id = :courseId)
              AND (:year IS NULL
                   OR YEAR(p.placedOn) = :year)
              AND (:minPackage IS NULL
                   OR p.packageLpa >= :minPackage)
            ORDER BY p.placedOn DESC
            """)
    Page<Placement> findFiltered(
            @Param("companyId") Long companyId,
            @Param("courseId") Long courseId,
            @Param("year") Integer year,
            @Param("minPackage") BigDecimal minPackage,
            Pageable pageable
    );

    @Query("""
            SELECT
                COUNT(p.id) AS totalPlaced,
                COUNT(DISTINCT p.company.id) AS hiringPartners,
                MAX(p.packageLpa) AS highestPackageLpa,
                AVG(p.packageLpa) AS averagePackageLpa,
                SUM(
                    CASE
                        WHEN YEAR(p.placedOn) = YEAR(CURRENT_DATE)
                        THEN 1
                        ELSE 0
                    END
                ) AS placementsThisYear
            FROM Placement p
            """)
    PlacementStatsProjection getPlacementStats();

    Optional<Placement> findFirstByStudentIdOrderByPlacedOnDesc(Long studentId);
}