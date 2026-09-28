package com.techedge.portal.entity;

import com.techedge.portal.entity.id.CourseOfferId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOffer {

    @EmbeddedId
    private CourseOfferId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("offerId")
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;
}