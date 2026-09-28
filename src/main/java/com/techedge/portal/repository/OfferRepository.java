package com.techedge.portal.repository;

import com.techedge.portal.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByActiveTrue();

    Optional<Offer> findByCouponCode(String couponCode);

    boolean existsByCouponCode(String couponCode);

    @Query("""
        SELECT o
        FROM Offer o
        WHERE o.active = true
          AND o.validFrom <= :today
          AND o.validTo >= :today
        ORDER BY o.validTo ASC
        """)
    List<Offer> findLiveOffers(
            @Param("today") LocalDate today
    );
}