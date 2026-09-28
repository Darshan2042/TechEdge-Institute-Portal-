const params = new URLSearchParams(window.location.search);

const examId = params.get("id");

let attemptId = null;
let expiresAt = null;

let timerInterval = null;
let autosaveInterval = null;

let submitting = false;


/* ============================================================
   INITIALIZE EXAM
   ============================================================ */

async function initializeExam() {

    if (!isLoggedIn()) {
        window.location.href = "login.html";
        return;
    }

    if (!examId) {
        showError("Exam ID is missing.");
        return;
    }

    try {

        /*
         * STEP 1
         * Get exam metadata.
         *
         * This response must NOT contain questions.
         */
        const examResponse =
            await apiFetch(`/exams/${examId}`);

        const exam =
            examResponse.data || examResponse;


        /*
         * Render exam metadata.
         */
        document.getElementById("examTitle").textContent =
            exam.title || "Online Exam";

        document.getElementById("totalMarks").textContent =
            exam.totalMarks ?? "-";

        document.getElementById("passingMarks").textContent =
            exam.passingMarks ?? "-";


        /*
         * STEP 2
         * Check whether the student already has
         * an IN_PROGRESS attempt.
         */
        const attemptsResponse =
            await apiFetch("/attempts/my");

        const attempts =
            attemptsResponse.data ||
            attemptsResponse ||
            [];


        const existingAttempt =
            Array.isArray(attempts)
                ? attempts.find(attempt =>
                    Number(attempt.examId) === Number(examId) &&
                    attempt.status === "IN_PROGRESS"
                )
                : null;


        let attemptResponse;


        /*
         * STEP 3
         *
         * If an unfinished attempt exists,
         * resume it.
         *
         * Otherwise create a new attempt.
         */
        if (existingAttempt) {

            attemptResponse =
                await apiFetch(
                    `/attempts/${existingAttempt.attemptId}`
                );

        } else {

            attemptResponse =
                await apiFetch(
                    `/exams/${examId}/attempts`,
                    {
                        method: "POST"
                    }
                );
        }


        const attempt =
            attemptResponse.data ||
            attemptResponse;


        /*
         * STEP 4
         * Save attempt information.
         */
        attemptId =
            attempt.attemptId;

        expiresAt =
            new Date(attempt.expiresAt);


        /*
         * Validate server expiry time.
         */
        if (Number.isNaN(expiresAt.getTime())) {

            throw new Error(
                "Invalid exam expiry time received from server."
            );
        }


        /*
         * STEP 5
         * Render questions received from
         * the attempt-start/resume response.
         */
        renderQuestions(
            attempt.questions || []
        );


        /*
         * STEP 6
         * Start server-based countdown.
         */
        startTimer();


        /*
         * STEP 7
         * Autosave every 30 seconds.
         */
        if (autosaveInterval) {
            clearInterval(autosaveInterval);
        }

        autosaveInterval =
            setInterval(
                saveAnswers,
                30000
            );


        /*
         * Show exam.
         */
        document.getElementById("loading").style.display =
            "none";

        document.getElementById("examContainer").style.display =
            "block";


    } catch (error) {

        showError(
            error.message ||
            "Unable to start exam."
        );
    }
}


/* ============================================================
   RENDER QUESTIONS
   ============================================================ */

function renderQuestions(questions) {

    const container =
        document.getElementById(
            "questionsContainer"
        );

    container.innerHTML = "";


    if (!Array.isArray(questions) || questions.length === 0) {

        container.innerHTML = `
            <div class="card">

                <h3>
                    No questions available
                </h3>

                <p>
                    This exam does not currently contain
                    any questions.
                </p>

            </div>
        `;

        return;
    }


    questions.forEach((question, index) => {

        const questionCard =
            document.createElement("div");

        questionCard.className =
            "card exam-question";


        let optionsHtml = "";


        /*
         * IMPORTANT:
         *
         * We only use:
         * optionId
         * optionText
         *
         * There is NO isCorrect field here.
         */
        (question.options || []).forEach(option => {

            optionsHtml += `
                <label class="option">

                    <input
                        type="radio"
                        name="question-${question.questionId}"
                        value="${option.optionId}"
                    >

                    <span>
                        ${option.optionText}
                    </span>

                </label>
            `;
        });


        questionCard.innerHTML = `

            <h3>
                ${index + 1}.
                ${question.questionText}
            </h3>

            <div class="options">

                ${optionsHtml}

            </div>
        `;


        container.appendChild(
            questionCard
        );
    });


    /*
     * Autosave immediately when
     * student selects an answer.
     */
    document
        .querySelectorAll(
            ".option input"
        )
        .forEach(input => {

            input.addEventListener(
                "change",
                saveAnswers
            );
        });
}


/* ============================================================
   TIMER
   ============================================================ */

function startTimer() {

    if (timerInterval) {
        clearInterval(timerInterval);
    }

    updateTimer();

    timerInterval =
        setInterval(
            updateTimer,
            1000
        );
}


function updateTimer() {

    if (!expiresAt) {
        return;
    }


    const now =
        new Date();


    const remaining =
        expiresAt.getTime() -
        now.getTime();


    /*
     * Time finished.
     */
    if (remaining <= 0) {

        clearInterval(
            timerInterval
        );

        if (autosaveInterval) {

            clearInterval(
                autosaveInterval
            );
        }


        document.getElementById(
            "timer"
        ).textContent = "00:00";


        /*
         * Server will determine whether
         * the attempt is expired.
         */
        submitExam();

        return;
    }


    const totalSeconds =
        Math.floor(
            remaining / 1000
        );


    const minutes =
        Math.floor(
            totalSeconds / 60
        );


    const seconds =
        totalSeconds % 60;


    document.getElementById(
        "timer"
    ).textContent =
        `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
}


/* ============================================================
   COLLECT ANSWERS
   ============================================================ */

function collectAnswers() {

    const answers = [];


    document
        .querySelectorAll(
            ".exam-question"
        )
        .forEach(questionCard => {

            const input =
                questionCard.querySelector(
                    "input:checked"
                );


            if (!input) {
                return;
            }


            const questionId =
                input.name.replace(
                    "question-",
                    ""
                );


            answers.push({

                questionId:
                    Number(questionId),

                selectedOptionId:
                    Number(input.value)
            });
        });


    return answers;
}


/* ============================================================
   AUTOSAVE
   ============================================================ */

async function saveAnswers() {

    if (!attemptId || submitting) {
        return;
    }


    const answers =
        collectAnswers();


    /*
     * Nothing to save.
     */
    if (answers.length === 0) {
        return;
    }


    try {

        await apiFetch(
            `/attempts/${attemptId}/answers`,
            {
                method: "PUT",

                body: JSON.stringify({
                    answers: answers
                })
            }
        );


        console.log(
            "Answers autosaved."
        );


    } catch (error) {

        /*
         * Autosave failure should not
         * immediately kick the student
         * out of the exam.
         */
        console.error(
            "Autosave failed:",
            error.message
        );
    }
}


/* ============================================================
   SUBMIT EXAM
   ============================================================ */

async function submitExam() {

    if (
        submitting ||
        !attemptId
    ) {
        return;
    }


    submitting = true;


    const submitButton =
        document.getElementById(
            "submitButton"
        );


    if (submitButton) {
        submitButton.disabled = true;
    }


    try {

        /*
         * Stop autosave before submission.
         */
        if (autosaveInterval) {

            clearInterval(
                autosaveInterval
            );
        }


        /*
         * Stop timer.
         */
        if (timerInterval) {

            clearInterval(
                timerInterval
            );
        }


        /*
         * Send the latest answers.
         *
         * Backend accepts the final answer set.
         */
        const answers =
            collectAnswers();


        await apiFetch(
            `/attempts/${attemptId}/submit`,
            {
                method: "POST",

                body: JSON.stringify({
                    answers: answers
                })
            }
        );


        /*
         * Redirect to result.
         */
        window.location.href =
            `result.html?id=${attemptId}`;


    } catch (error) {

        submitting = false;


        if (submitButton) {
            submitButton.disabled = false;
        }


        showError(
            error.message ||
            "Unable to submit exam."
        );
    }
}


/* ============================================================
   ERROR
   ============================================================ */

function showError(message) {

    const loading =
        document.getElementById(
            "loading"
        );

    if (loading) {
        loading.style.display = "none";
    }


    const error =
        document.getElementById(
            "errorMessage"
        );


    if (error) {

        error.textContent =
            message;

        error.style.display =
            "block";
    }
}


/* ============================================================
   FORM SUBMIT
   ============================================================ */

document.addEventListener(
    "DOMContentLoaded",
    () => {

        const examForm =
            document.getElementById(
                "examForm"
            );


        if (examForm) {

            examForm.addEventListener(
                "submit",
                async function(event) {

                    event.preventDefault();

                    await submitExam();
                }
            );
        }


        initializeExam();
    }
);