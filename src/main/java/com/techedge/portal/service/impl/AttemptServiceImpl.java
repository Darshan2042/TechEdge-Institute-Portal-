package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.SubmitAnswersRequest;
import com.techedge.portal.dto.response.AttemptResultResponse;
import com.techedge.portal.dto.response.AttemptStartResponse;
import com.techedge.portal.dto.response.AttemptSummaryResponse;
import com.techedge.portal.entity.Answer;
import com.techedge.portal.entity.Attempt;
import com.techedge.portal.entity.enums.AttemptStatus;
import com.techedge.portal.entity.Enrollment;
import com.techedge.portal.entity.enums.EnrollmentStatus;
import com.techedge.portal.entity.Exam;
import com.techedge.portal.entity.Option;
import com.techedge.portal.entity.Question;
import com.techedge.portal.entity.Student;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ForbiddenException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.AttemptMapper;
import com.techedge.portal.repository.AnswerRepository;
import com.techedge.portal.repository.AttemptRepository;
import com.techedge.portal.repository.EnrollmentRepository;
import com.techedge.portal.repository.ExamRepository;
import com.techedge.portal.repository.OptionRepository;
import com.techedge.portal.repository.QuestionRepository;
import com.techedge.portal.repository.StudentRepository;
import com.techedge.portal.service.AttemptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;

    public AttemptServiceImpl(
            AttemptRepository attemptRepository,
            ExamRepository examRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            AnswerRepository answerRepository) {

        this.attemptRepository = attemptRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.answerRepository = answerRepository;
    }

    // ============================================================
    // START ATTEMPT
    // ============================================================

    @Override
    @Transactional
    public AttemptStartResponse startAttempt(
            Long examId,
            Long userId) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam not found"));

        // Exam must be active
        if (!Boolean.TRUE.equals(exam.getActive())) {
            throw new BusinessRuleException(
                    "Exam is inactive");
        }

        // Student must have ACTIVE or COMPLETED enrollment
        // for this exam's course.
        boolean enrolled = enrollmentRepository
                .findByStudentId(student.getId())
                .stream()
                .anyMatch(enrollment ->
                        isValidEnrollmentForCourse(
                                enrollment,
                                exam));

        if (!enrolled) {
            throw new BusinessRuleException(
                    "Student is not ACTIVE or COMPLETED in a batch for this course");
        }

        // Only one IN_PROGRESS attempt is allowed.
        List<Attempt> existingAttempts =
                attemptRepository.findByStudentIdAndExamId(
                        student.getId(),
                        examId);

        Attempt existingInProgress =
                existingAttempts.stream()
                        .filter(attempt ->
                                attempt.getStatus()
                                        == AttemptStatus.IN_PROGRESS)
                        .findFirst()
                        .orElse(null);

        if (existingInProgress != null) {
            throw new BusinessRuleException(
                    "An IN_PROGRESS attempt already exists. " +
                            "Resume attempt " +
                            existingInProgress.getId());
        }

        LocalDateTime startedAt = LocalDateTime.now();

        LocalDateTime expiresAt =
                startedAt.plusMinutes(
                        exam.getDurationMinutes());

        Attempt attempt = new Attempt();

        attempt.setStudent(student);
        attempt.setExam(exam);
        attempt.setStartedAt(startedAt);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);

        Attempt savedAttempt =
                attemptRepository.save(attempt);

        // Get exam questions
        List<Question> questions =
                questionRepository.findByExamId(examId);

        List<AttemptStartResponse.QuestionResponse>
                questionResponses = new ArrayList<>();

        for (Question question : questions) {

            List<Option> options =
                    optionRepository.findByQuestionId(
                            question.getId());

            List<AttemptStartResponse.OptionResponse>
                    optionResponses = options.stream()
                    .map(option ->
                            new AttemptStartResponse.OptionResponse(
                                    option.getId(),
                                    option.getOptionText()
                            ))
                    .toList();

            questionResponses.add(
                    new AttemptStartResponse.QuestionResponse(
                            question.getId(),
                            question.getQuestionText(),
                            question.getMarks(),
                            optionResponses
                    )
            );
        }

        return new AttemptStartResponse(
                savedAttempt.getId(),
                exam.getTitle(),
                exam.getDurationMinutes(),
                startedAt,
                expiresAt,
                exam.getTotalMarks(),
                questionResponses
        );
    }

    // ============================================================
    // SAVE ANSWERS / AUTOSAVE
    // ============================================================

    @Override
    @Transactional
    public void saveAnswers(
            Long attemptId,
            Long userId,
            SubmitAnswersRequest request) {

        Attempt attempt =
                getOwnedAttempt(attemptId, userId);

        if (attempt.getStatus()
                != AttemptStatus.IN_PROGRESS) {

            throw new BusinessRuleException(
                    "Only an IN_PROGRESS attempt can save answers");
        }

        if (request == null
                || request.answers() == null
                || request.answers().isEmpty()) {

            return;
        }

        for (SubmitAnswersRequest.AnswerRequest
                answerRequest : request.answers()) {

            Question question =
                    questionRepository.findById(
                                    answerRequest.questionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Question not found"));

            // Question must belong to this exam.
            if (!question.getExam()
                    .getId()
                    .equals(attempt.getExam().getId())) {

                throw new BusinessRuleException(
                        "Question does not belong to this exam");
            }

            Option selectedOption = null;

            // selectedOptionId can be null for unanswered question.
            if (answerRequest.selectedOptionId() != null) {

                selectedOption =
                        optionRepository.findById(
                                        answerRequest.selectedOptionId())
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Selected option not found"));

                // Option must belong to the selected question.
                if (!selectedOption.getQuestion()
                        .getId()
                        .equals(question.getId())) {

                    throw new BusinessRuleException(
                            "Selected option does not belong to the question");
                }
            }

            /*
             * Idempotent autosave:
             *
             * If answer already exists for this
             * attempt + question -> update it.
             *
             * Otherwise -> create it.
             */
            Answer answer =
                    answerRepository
                            .findByAttemptIdAndQuestionId(
                                    attemptId,
                                    question.getId())
                            .orElseGet(Answer::new);

            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(selectedOption);

            answerRepository.save(answer);
        }
    }

    // ============================================================
    // SUBMIT ATTEMPT
    // ============================================================

    @Override
    @Transactional
    public AttemptResultResponse submitAttempt(
            Long attemptId,
            Long userId,
            SubmitAnswersRequest request) {

        Attempt attempt =
                getOwnedAttempt(attemptId, userId);

        /*
         * Submitting twice is not allowed.
         */
        if (attempt.getStatus()
                != AttemptStatus.IN_PROGRESS) {

            throw new BusinessRuleException(
                    "Attempt has already been submitted");
        }

        /*
         * Save final answers if the request contains them.
         *
         * If request is empty, previously autosaved
         * answers will be used.
         */
        if (request != null
                && request.answers() != null
                && !request.answers().isEmpty()) {

            saveAnswers(
                    attemptId,
                    userId,
                    request);
        }

        /*
         * Get all questions of the exam.
         */
        List<Question> questions =
                questionRepository.findByExamId(
                        attempt.getExam().getId());

        /*
         * Get all saved answers.
         */
        List<Answer> answers =
                answerRepository.findByAttemptId(
                        attemptId);

        int score = 0;
        int correctCount = 0;
        int wrongCount = 0;
        int unansweredCount = 0;

        List<AttemptResultResponse.QuestionResultResponse>
                questionResults = new ArrayList<>();

        for (Question question : questions) {

            /*
             * Find answer for this question.
             */
            Answer answer = answers.stream()
                    .filter(savedAnswer ->
                            savedAnswer.getQuestion()
                                    .getId()
                                    .equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            Long selectedOptionId = null;
            Long correctOptionId = null;

            boolean isCorrect = false;

            /*
             * Find the correct option.
             */
            List<Option> options =
                    optionRepository.findByQuestionId(
                            question.getId());

            Option correctOption =
                    options.stream()
                            .filter(option ->
                                    Boolean.TRUE.equals(
                                            option.getCorrect()))
                            .findFirst()
                            .orElse(null);

            if (correctOption != null) {
                correctOptionId =
                        correctOption.getId();
            }

            /*
             * No answer = unanswered.
             */
            if (answer == null
                    || answer.getSelectedOption() == null) {

                unansweredCount++;

            } else {

                selectedOptionId =
                        answer.getSelectedOption().getId();

                /*
                 * Correct answer:
                 * add question marks.
                 */
                if (Boolean.TRUE.equals(
                        answer.getSelectedOption()
                                .getCorrect())) {

                    isCorrect = true;

                    correctCount++;

                    score += question.getMarks();

                } else {

                    /*
                     * Wrong answer:
                     * no negative marking.
                     */
                    wrongCount++;
                }
            }

            questionResults.add(
                    new AttemptResultResponse.QuestionResultResponse(
                            question.getQuestionText(),
                            selectedOptionId,
                            correctOptionId,
                            isCorrect
                    )
            );
        }

        /*
         * Server time decides whether the attempt expired.
         *
         * We DO NOT trust the browser/client.
         */
        LocalDateTime submittedAt =
                LocalDateTime.now();

        LocalDateTime expiresAt =
                attempt.getStartedAt()
                        .plusMinutes(
                                attempt.getExam()
                                        .getDurationMinutes());

        boolean expired =
                submittedAt.isAfter(expiresAt);

        /*
         * Store score and submission time.
         */
        attempt.setScore(score);
        attempt.setSubmittedAt(submittedAt);

        if (expired) {

            attempt.setStatus(
                    AttemptStatus.EXPIRED);

        } else {

            attempt.setStatus(
                    AttemptStatus.SUBMITTED);
        }

        attemptRepository.save(attempt);

        /*
         * Calculate percentage.
         */
        double percentage =
                attempt.getExam().getTotalMarks() == 0
                        ? 0.0
                        : (score * 100.0)
                        / attempt.getExam().getTotalMarks();

        /*
         * Passing rule:
         *
         * score >= passingMarks
         */
        boolean passed =
                score >= attempt.getExam()
                        .getPassingMarks();

        return new AttemptResultResponse(
                attempt.getId(),
                score,
                attempt.getExam().getTotalMarks(),
                percentage,
                passed,
                correctCount,
                wrongCount,
                unansweredCount,
                submittedAt,
                expired,
                questionResults
        );
    }

    // ============================================================
    // OWNERSHIP CHECK
    // ============================================================

    private Attempt getOwnedAttempt(
            Long attemptId,
            Long userId) {

        Attempt attempt =
                attemptRepository.findById(attemptId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attempt not found"));

        /*
         * Make sure the logged-in user owns this attempt.
         */
        if (!attempt.getStudent()
                .getUser()
                .getId()
                .equals(userId)) {

            /*
             * Use your project's existing exception
             * handling/security convention here.
             */
            throw new ForbiddenException(
                    "You are not allowed to access this attempt");
        }

        return attempt;
    }

    // ============================================================
    // ENROLLMENT CHECK
    // ============================================================

    private boolean isValidEnrollmentForCourse(
            Enrollment enrollment,
            Exam exam) {

        /*
         * Student must be ACTIVE or COMPLETED.
         */
        if (enrollment.getStatus()
                != EnrollmentStatus.ACTIVE
                && enrollment.getStatus()
                != EnrollmentStatus.COMPLETED) {

            return false;
        }

        /*
         * Enrollment's batch must belong
         * to the exam's course.
         */
        return enrollment.getBatch()
                .getCourse()
                .getId()
                .equals(
                        exam.getCourse().getId()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptResultResponse getResult(
            Long attemptId,
            Long userId,
            boolean admin) {

        Attempt attempt;

        if (admin) {

            attempt = attemptRepository.findById(attemptId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Attempt not found"));

        } else {

            attempt = getOwnedAttempt(
                    attemptId,
                    userId
            );
        }

        if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            throw new BusinessRuleException(
                    "Attempt has not been submitted yet");
        }

        List<Question> questions =
                questionRepository.findByExamId(
                        attempt.getExam().getId());

        List<Answer> answers =
                answerRepository.findByAttemptId(
                        attemptId);

        int score = attempt.getScore() == null
                ? 0
                : attempt.getScore();

        int correctCount = 0;
        int wrongCount = 0;
        int unansweredCount = 0;

        List<AttemptResultResponse.QuestionResultResponse>
                questionResults = new ArrayList<>();

        for (Question question : questions) {

            Answer answer = answers.stream()
                    .filter(savedAnswer ->
                            savedAnswer.getQuestion()
                                    .getId()
                                    .equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            Long selectedOptionId = null;
            Long correctOptionId = null;
            boolean isCorrect = false;

            List<Option> options =
                    optionRepository.findByQuestionId(
                            question.getId());

            Option correctOption =
                    options.stream()
                            .filter(option ->
                                    Boolean.TRUE.equals(
                                            option.getCorrect()))
                            .findFirst()
                            .orElse(null);

            if (correctOption != null) {
                correctOptionId =
                        correctOption.getId();
            }

            if (answer == null
                    || answer.getSelectedOption() == null) {

                unansweredCount++;

            } else {

                selectedOptionId =
                        answer.getSelectedOption().getId();

                if (Boolean.TRUE.equals(
                        answer.getSelectedOption().getCorrect())) {

                    isCorrect = true;
                    correctCount++;

                } else {

                    wrongCount++;
                }
            }

            questionResults.add(
                    new AttemptResultResponse.QuestionResultResponse(
                            question.getQuestionText(),
                            selectedOptionId,
                            correctOptionId,
                            isCorrect
                    )
            );
        }

        double percentage =
                attempt.getExam().getTotalMarks() == 0
                        ? 0.0
                        : (score * 100.0)
                        / attempt.getExam().getTotalMarks();

        boolean passed =
                score >= attempt.getExam().getPassingMarks();

        return new AttemptResultResponse(
                attempt.getId(),
                score,
                attempt.getExam().getTotalMarks(),
                percentage,
                passed,
                correctCount,
                wrongCount,
                unansweredCount,
                attempt.getSubmittedAt(),
                attempt.getStatus() == AttemptStatus.EXPIRED,
                questionResults
        );
    }

    // ============================================================
// RESUME ATTEMPT
// ============================================================

    @Override
    @Transactional(readOnly = true)
    public AttemptStartResponse getAttempt(
            Long attemptId,
            Long userId) {

        Attempt attempt =
                getOwnedAttempt(attemptId, userId);

        if (attempt.getStatus()
                != AttemptStatus.IN_PROGRESS) {

            throw new BusinessRuleException(
                    "Only an IN_PROGRESS attempt can be resumed");
        }

        Exam exam = attempt.getExam();

        LocalDateTime startedAt =
                attempt.getStartedAt();

        LocalDateTime expiresAt =
                startedAt.plusMinutes(
                        exam.getDurationMinutes());

        List<Question> questions =
                questionRepository.findByExamId(
                        exam.getId());

        List<AttemptStartResponse.QuestionResponse>
                questionResponses = new ArrayList<>();

        for (Question question : questions) {

            List<Option> options =
                    optionRepository.findByQuestionId(
                            question.getId());

            List<AttemptStartResponse.OptionResponse>
                    optionResponses =
                    options.stream()
                            .map(option ->
                                    new AttemptStartResponse.OptionResponse(
                                            option.getId(),
                                            option.getOptionText()
                                    ))
                            .toList();

            questionResponses.add(
                    new AttemptStartResponse.QuestionResponse(
                            question.getId(),
                            question.getQuestionText(),
                            question.getMarks(),
                            optionResponses
                    )
            );
        }

        return new AttemptStartResponse(
                attempt.getId(),
                exam.getTitle(),
                exam.getDurationMinutes(),
                startedAt,
                expiresAt,
                exam.getTotalMarks(),
                questionResponses
        );
    }

    // ============================================================
// MY ATTEMPTS
// ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<AttemptSummaryResponse> getMyAttempts(
            Long userId) {

        Student student =
                studentRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student profile not found"));

        return attemptRepository
                .findByStudentId(student.getId())
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }


// ============================================================
// ADMIN - ALL ATTEMPTS FOR AN EXAM
// ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<AttemptSummaryResponse> getExamAttempts(
            Long examId) {

        // Make sure the exam exists.
        examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam not found"));

        return attemptRepository
                .findByExamId(examId)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }


// ============================================================
// ATTEMPT SUMMARY MAPPER
// ============================================================

    private AttemptSummaryResponse toSummaryResponse(
            Attempt attempt) {

        return new AttemptSummaryResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getExam().getTitle(),
                attempt.getStatus(),
                attempt.getScore(),
                attempt.getExam().getTotalMarks(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt()
        );
    }
}