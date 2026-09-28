package com.techedge.portal.entity;

import com.techedge.portal.entity.enums.CourseCategory;
import com.techedge.portal.entity.enums.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CourseCategory category;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private CourseLevel level;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}