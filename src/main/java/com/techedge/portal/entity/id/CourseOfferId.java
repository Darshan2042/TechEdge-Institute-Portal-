package com.techedge.portal.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CourseOfferId implements Serializable {

    private Long courseId;

    private Long offerId;
}