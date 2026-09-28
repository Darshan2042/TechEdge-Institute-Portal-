package com.techedge.portal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "qualification", length = 100)
    private String qualification;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "city", length = 80)
    private String city;

    @Column(name = "resume_url", length = 255)
    private String resumeUrl;
}