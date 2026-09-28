package com.techedge.portal.mapper;

import com.techedge.portal.dto.response.ExamResponse;
import com.techedge.portal.entity.Exam;

public class ExamMapper {

    private ExamMapper() {
    }

    public static ExamResponse toResponse(Exam exam) {

        return new ExamResponse(
                exam.getId(),
                exam.getCourse().getId(),
                exam.getTitle(),
                exam.getDurationMinutes(),
                exam.getTotalMarks(),
                exam.getPassingMarks(),
                exam.getActive()
        );
    }
}