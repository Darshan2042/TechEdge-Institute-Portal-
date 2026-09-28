package com.techedge.portal.service;

import com.techedge.portal.dto.request.ExamRequest;
import com.techedge.portal.dto.request.QuestionRequest;
import com.techedge.portal.dto.response.ExamResponse;

import java.util.List;

public interface ExamService {

    List<ExamResponse> getExams(Long userId);

    ExamResponse getExamById(Long examId);

    ExamResponse createExam(ExamRequest request);

    void addQuestion(Long examId, QuestionRequest request);
}