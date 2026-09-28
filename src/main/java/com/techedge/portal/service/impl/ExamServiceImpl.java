package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.ExamRequest;
import com.techedge.portal.dto.response.ExamResponse;
import com.techedge.portal.entity.Course;
import com.techedge.portal.entity.Exam;
import com.techedge.portal.entity.Enrollment;
import com.techedge.portal.entity.enums.EnrollmentStatus;
import com.techedge.portal.entity.Student;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.ExamMapper;
import com.techedge.portal.repository.CourseRepository;
import com.techedge.portal.repository.EnrollmentRepository;
import com.techedge.portal.repository.ExamRepository;
import com.techedge.portal.repository.StudentRepository;
import com.techedge.portal.service.ExamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techedge.portal.dto.request.QuestionRequest;
import com.techedge.portal.entity.Option;
import com.techedge.portal.entity.Question;
import com.techedge.portal.repository.QuestionRepository;
import com.techedge.portal.repository.OptionRepository;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public ExamServiceImpl(
            ExamRepository examRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository) {

        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExams(Long userId) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student profile not found"));

        List<Enrollment> enrollments =
                enrollmentRepository.findByStudentId(student.getId());

        return enrollments.stream()
                .filter(enrollment ->
                        enrollment.getStatus() == EnrollmentStatus.ACTIVE
                                || enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .map(enrollment ->
                        examRepository.findByCourseIdAndActiveTrue(
                                enrollment.getBatch().getCourse().getId()))
                .flatMap(List::stream)
                .distinct()
                .map(ExamMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long examId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        return ExamMapper.toResponse(exam);
    }

    @Override
    @Transactional
    public ExamResponse createExam(ExamRequest request) {

        if (request.passingMarks() > request.totalMarks()) {
            throw new BusinessRuleException(
                    "Passing marks cannot be greater than total marks");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found"));

        Exam exam = new Exam();

        exam.setCourse(course);
        exam.setTitle(request.title());
        exam.setDurationMinutes(request.durationMinutes());
        exam.setTotalMarks(request.totalMarks());
        exam.setPassingMarks(request.passingMarks());

        if (request.active() != null) {
            exam.setActive(request.active());
        } else {
            exam.setActive(true);
        }

        Exam savedExam = examRepository.save(exam);

        return ExamMapper.toResponse(savedExam);
    }

    @Override
    @Transactional
    public void addQuestion(Long examId, QuestionRequest request) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        if (request.options().size() != 4) {
            throw new BusinessRuleException(
                    "Exactly 4 options are required");
        }

        long correctCount = request.options()
                .stream()
                .filter(QuestionRequest.OptionRequest::correct)
                .count();

        if (correctCount != 1) {
            throw new BusinessRuleException(
                    "Exactly one option must be correct");
        }

        Question question = new Question();

        question.setExam(exam);
        question.setQuestionText(request.questionText());
        question.setMarks(request.marks());

        Question savedQuestion = questionRepository.save(question);

        for (QuestionRequest.OptionRequest optionRequest : request.options()) {

            Option option = new Option();

            option.setQuestion(savedQuestion);
            option.setOptionText(optionRequest.optionText());
            option.setCorrect(optionRequest.correct());

            optionRepository.save(option);
        }
    }
}