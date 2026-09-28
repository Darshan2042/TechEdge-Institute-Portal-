let selectedExamId = null;


// ==========================================
// ADMIN CHECK
// ==========================================

function checkAdmin() {

    const user = getCurrentUser();

    if (!isLoggedIn() || !user || user.role !== "ADMIN") {
        window.location.href = "login.html";
        return false;
    }

    return true;
}


// ==========================================
// LOAD COURSES
// ==========================================

async function loadCourses() {

    try {

        const response = await apiFetch(
            "/courses?page=0&size=100"
        );

        const data = response.data || response;

        const courses = data.content || data;

        const select =
            document.getElementById("courseId");

        select.innerHTML =
            '<option value="">Select Course</option>';

        courses.forEach(course => {

            const option =
                document.createElement("option");

            option.value = course.id;

            option.textContent =
                course.title;

            select.appendChild(option);
        });

    } catch (error) {

        showError(
            error.message || "Unable to load courses."
        );
    }
}


// ==========================================
// LOAD EXAMS
// ==========================================

async function loadExams() {

    const loading =
        document.getElementById("loading");

    const examList =
        document.getElementById("examList");

    try {

        loading.style.display = "block";
        examList.style.display = "none";

        const response =
            await apiFetch("/exams");

        const exams =
            response.data || response;

        loading.style.display = "none";
        examList.style.display = "grid";

        if (!Array.isArray(exams) || exams.length === 0) {

            examList.innerHTML = `
                <div class="card text-center">
                    <p>No exams found.</p>
                </div>
            `;

            return;
        }


        examList.innerHTML =
            exams.map(exam => `

                <div class="card">

                    <h3>
                        ${exam.title || "Untitled Exam"}
                    </h3>

                    <p>
                        Duration:
                        ${exam.durationMinutes ?? "-"}
                        minutes
                    </p>

                    <p>
                        Total Marks:
                        ${exam.totalMarks ?? "-"}
                    </p>

                    <p>
                        Passing Marks:
                        ${exam.passingMarks ?? "-"}
                    </p>

                    <p>
                        Course:
                        ${exam.courseTitle || exam.course?.title || "-"}
                    </p>

                    <button
                        class="btn btn-primary"
                        style="margin-top:15px;"
                        onclick="selectExam(${exam.id}, '${escapeHtml(exam.title || "Exam")}')">

                        Add Question

                    </button>

                </div>

            `).join("");

    } catch (error) {

        loading.style.display = "none";

        showError(
            error.message || "Unable to load exams."
        );
    }
}


// ==========================================
// SELECT EXAM
// ==========================================

function selectExam(examId, examTitle) {

    selectedExamId = examId;

    document.getElementById(
        "selectedExamName"
    ).textContent =
        `Adding questions to: ${examTitle}`;

    document.getElementById(
        "questionSection"
    ).style.display = "block";

    document.getElementById(
        "questionSection"
    ).scrollIntoView({
        behavior: "smooth"
    });
}


// ==========================================
// CREATE EXAM
// ==========================================

async function createExam(event) {

    event.preventDefault();

    clearMessages();

    const courseId =
        Number(document.getElementById("courseId").value);

    const title =
        document.getElementById("title").value.trim();

    const durationMinutes =
        Number(document.getElementById("durationMinutes").value);

    const totalMarks =
        Number(document.getElementById("totalMarks").value);

    const passingMarks =
        Number(document.getElementById("passingMarks").value);


    if (!courseId) {

        showError("Please select a course.");
        return;
    }


    if (passingMarks > totalMarks) {

        showError(
            "Passing marks cannot be greater than total marks."
        );

        return;
    }


    const requestBody = {

        courseId: courseId,

        title: title,

        durationMinutes: durationMinutes,

        totalMarks: totalMarks,

        passingMarks: passingMarks
    };


    try {

        const response =
            await apiFetch("/exams", {

                method: "POST",

                body: JSON.stringify(requestBody)
            });


        const createdExam =
            response.data || response;


        showSuccess(
            `Exam created successfully. Exam ID: ${createdExam.id}`
        );


        document.getElementById(
            "examForm"
        ).reset();


        await loadExams();


    } catch (error) {

        showError(
            error.message || "Unable to create exam."
        );
    }
}


// ==========================================
// ADD QUESTION
// ==========================================

async function addQuestion(event) {

    event.preventDefault();

    clearMessages();


    if (!selectedExamId) {

        showError(
            "Please select an exam first."
        );

        return;
    }


    const questionText =
        document.getElementById(
            "questionText"
        ).value.trim();


    const marks =
        Number(
            document.getElementById(
                "questionMarks"
            ).value
        );


    const options = [

        {
            optionText:
                document.getElementById("option1")
                    .value.trim(),

            correct: false
        },

        {
            optionText:
                document.getElementById("option2")
                    .value.trim(),

            correct: false
        },

        {
            optionText:
                document.getElementById("option3")
                    .value.trim(),

            correct: false
        },

        {
            optionText:
                document.getElementById("option4")
                    .value.trim(),

            correct: false
        }
    ];


    const selectedCorrect =
        document.querySelector(
            'input[name="correctOption"]:checked'
        );


    if (!selectedCorrect) {

        showError(
            "Please select exactly one correct option."
        );

        return;
    }


    const correctIndex =
        Number(selectedCorrect.value);


    options[correctIndex].correct = true;


    const requestBody = {

        questionText: questionText,

        marks: marks,

        options: options
    };


    try {

        await apiFetch(
            `/exams/${selectedExamId}/questions`,
            {
                method: "POST",

                body: JSON.stringify(requestBody)
            }
        );


        showSuccess(
            "Question added successfully."
        );


        document.getElementById(
            "questionForm"
        ).reset();


        document.getElementById(
            "questionMarks"
        ).value = 1;


    } catch (error) {

        showError(
            error.message || "Unable to add question."
        );
    }
}


// ==========================================
// MESSAGES
// ==========================================

function showError(message) {

    const error =
        document.getElementById("errorMessage");

    error.textContent = message;

    error.style.display = "block";
}


function showSuccess(message) {

    const success =
        document.getElementById("successMessage");

    success.textContent = message;

    success.style.display = "block";

    setTimeout(() => {

        success.style.display = "none";

    }, 4000);
}


function clearMessages() {

    document.getElementById(
        "errorMessage"
    ).style.display = "none";

    document.getElementById(
        "successMessage"
    ).style.display = "none";
}


// ==========================================
// ESCAPE HTML
// ==========================================

function escapeHtml(value) {

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


// ==========================================
// INITIALIZE
// ==========================================

document.addEventListener(
    "DOMContentLoaded",
    async function () {

        if (!checkAdmin()) {
            return;
        }

        await loadCourses();

        await loadExams();
    }
);


// ==========================================
// FORM EVENTS
// ==========================================

document.getElementById(
    "examForm"
).addEventListener(
    "submit",
    createExam
);


document.getElementById(
    "questionForm"
).addEventListener(
    "submit",
    addQuestion
);