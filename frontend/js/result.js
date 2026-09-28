const params =
    new URLSearchParams(
        window.location.search
    );

const attemptId =
    params.get("id");


// ======================================
// LOAD RESULT
// ======================================

async function loadResult() {

    if (!isLoggedIn()) {

        window.location.href =
            "login.html";

        return;
    }


    if (!attemptId) {

        showError(
            "Attempt ID is missing."
        );

        return;
    }


    try {

        const response =
            await apiFetch(
                `/attempts/${attemptId}/result`
            );


        const result =
            response.data || response;


        renderResult(result);


    } catch (error) {

        showError(
            error.message ||
            "Unable to load exam result."
        );

    }
}


// ======================================
// RENDER RESULT
// ======================================

function renderResult(result) {

    document.getElementById("loading")
        .style.display = "none";


    document.getElementById("resultContainer")
        .style.display = "block";


    document.getElementById("examTitle")
        .textContent =
        "Exam Result";


    document.getElementById("score")
        .textContent =
        `${result.score ?? 0} / ${result.totalMarks ?? 0}`;


    document.getElementById("totalMarks")
        .textContent =
        result.totalMarks ?? 0;


    document.getElementById("resultStatus")
        .textContent =
        result.passed
            ? "PASSED"
            : "FAILED";


    document.getElementById("percentage")
        .textContent =
        `${result.percentage ?? 0}%`;


    document.getElementById("correctCount")
        .textContent =
        result.correctCount ?? 0;


    document.getElementById("wrongCount")
        .textContent =
        result.wrongCount ?? 0;


    document.getElementById("unansweredCount")
        .textContent =
        result.unansweredCount ?? 0;


    renderQuestions(
        result.questions || []
    );
}


// ======================================
// QUESTIONS
// ======================================

function renderQuestions(questions) {

    const container =
        document.getElementById(
            "answersContainer"
        );


    container.innerHTML = "";


    if (questions.length === 0) {

        container.innerHTML = `
            <div class="card">
                <p>
                    No question review available.
                </p>
            </div>
        `;

        return;
    }


    questions.forEach(
        (question, index) => {

            const card =
                document.createElement("div");


            card.className =
                "card exam-question";


            const resultClass =
                question.isCorrect
                    ? "result-pass"
                    : "result-fail";


            card.innerHTML = `

                <h3>
                    ${index + 1}.
                    ${question.questionText}
                </h3>

                <p>
                    <strong>
                        Your selected option ID:
                    </strong>

                    ${
                        question.selectedOptionId
                        ?? "Not answered"
                    }
                </p>

                <p>
                    <strong>
                        Correct option ID:
                    </strong>

                    ${
                        question.correctOptionId
                        ?? "Not available"
                    }
                </p>

                <p
                    class="${resultClass}"
                    style="font-weight:700;"
                >
                    ${
                        question.isCorrect
                        ? "Correct"
                        : question.selectedOptionId == null
                            ? "Not Answered"
                            : "Wrong"
                    }
                </p>

            `;


            container.appendChild(card);

        }
    );
}


// ======================================
// ERROR
// ======================================

function showError(message) {

    document.getElementById("loading")
        .style.display = "none";


    const error =
        document.getElementById(
            "errorMessage"
        );


    error.textContent = message;

    error.style.display = "block";
}


// ======================================
// PAGE LOAD
// ======================================

document.addEventListener(
    "DOMContentLoaded",
    loadResult
);