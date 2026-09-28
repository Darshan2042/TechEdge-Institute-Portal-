async function loadEnrollments() {

    if (!isLoggedIn()) {
        window.location.href = "login.html";
        return;
    }

    try {

        const response = await apiFetch("/enrollments/my");

        const enrollments = response.data || response || [];

        renderEnrollments(enrollments);

        document.getElementById("loading").style.display = "none";

        await loadAvailableExams(enrollments);

    } catch (error) {

        document.getElementById("loading").style.display = "none";

        showError(
            error.message || "Unable to load enrollments."
        );
    }
}


/* ============================================================
   RENDER ENROLLMENTS
   ============================================================ */

function renderEnrollments(enrollments) {

    const container =
        document.getElementById("enrollmentContainer");

    container.innerHTML = "";

    if (!Array.isArray(enrollments) || enrollments.length === 0) {

        container.innerHTML = `
            <div class="card">

                <h3>
                    No enrollments found
                </h3>

                <p>
                    You have not enrolled in any course yet.
                </p>

                <div style="margin-top:20px;">

                    <a
                        href="courses.html"
                        class="btn btn-primary"
                    >
                        Browse Courses
                    </a>

                </div>

            </div>
        `;

        return;
    }


    enrollments.forEach(enrollment => {

        const card =
            document.createElement("div");

        card.className = "card";


        const status =
            enrollment.status || "-";


        card.innerHTML = `
            <h3>
                ${enrollment.courseTitle || "Course"}
            </h3>

            <p>
                <strong>Batch:</strong>
                ${enrollment.batchCode || "-"}
            </p>

            <p>
                <strong>Status:</strong>
                ${status}
            </p>

            <p>
                <strong>Applied At:</strong>
                ${formatDate(enrollment.appliedAt)}
            </p>

            <p>
                <strong>Original Fee:</strong>
                ₹${enrollment.originalFee ?? "-"}
            </p>

            <p>
                <strong>Payable Fee:</strong>
                ₹${enrollment.payableFee ?? "-"}
            </p>

            ${
                enrollment.appliedOffer
                    ? `
                        <p>
                            <strong>Offer:</strong>
                            ${enrollment.appliedOffer}
                        </p>
                    `
                    : ""
            }
        `;


        /*
         * Withdraw button is allowed only
         * while enrollment is APPLIED.
         */

        if (status === "APPLIED") {

            const actionContainer =
                document.createElement("div");

            actionContainer.style.marginTop = "20px";

            actionContainer.innerHTML = `
                <button
                    class="btn btn-outline"
                    onclick="withdrawEnrollment(${enrollment.id})"
                >
                    Withdraw
                </button>
            `;

            card.appendChild(actionContainer);
        }


        container.appendChild(card);
    });
}


/* ============================================================
   LOAD AVAILABLE EXAMS
   ============================================================ */

async function loadAvailableExams(enrollments) {

    const loading =
        document.getElementById("examLoading");

    const container =
        document.getElementById("examContainer");

    try {

        const response =
            await apiFetch("/exams");

        const exams =
            response.data || response || [];


        /*
         * Exams are available only when the student's
         * enrollment is ACTIVE or COMPLETED.
         */

        const eligibleEnrollments =
            enrollments.filter(enrollment =>
                enrollment.status === "ACTIVE" ||
                enrollment.status === "COMPLETED"
            );


        /*
         * Get course IDs from eligible enrollments.
         */

        const courseIds =
            eligibleEnrollments
                .map(enrollment =>
                    enrollment.courseId
                )
                .filter(courseId =>
                    courseId != null
                );


        /*
         * Show only exams belonging to the student's
         * eligible courses.
         */

        const availableExams =
            Array.isArray(exams)
                ? exams.filter(exam =>
                    courseIds.includes(exam.courseId)
                )
                : [];


        loading.style.display = "none";

        renderExams(availableExams);

    } catch (error) {

        loading.style.display = "none";

        const errorElement =
            document.getElementById("examErrorMessage");

        errorElement.textContent =
            error.message || "Unable to load exams.";

        errorElement.style.display = "block";
    }
}


/* ============================================================
   RENDER EXAMS
   ============================================================ */

function renderExams(exams) {

    const container =
        document.getElementById("examContainer");

    container.innerHTML = "";


    if (!Array.isArray(exams) || exams.length === 0) {

        container.innerHTML = `
            <div class="card">

                <h3>
                    No exams available
                </h3>

                <p>
                    There are currently no exams available
                    for your active or completed courses.
                </p>

            </div>
        `;

        return;
    }


    exams.forEach(exam => {

        const card =
            document.createElement("div");

        card.className = "card";


        card.innerHTML = `
            <h3>
                ${exam.title || "Online Exam"}
            </h3>

            <p>
                <strong>Duration:</strong>
                ${exam.durationMinutes ?? "-"} minutes
            </p>

            <p>
                <strong>Total Marks:</strong>
                ${exam.totalMarks ?? "-"}
            </p>

            <p>
                <strong>Passing Marks:</strong>
                ${exam.passingMarks ?? "-"}
            </p>

            <div
                class="exam-card-actions"
            >

                <a
                    href="exam.html?id=${exam.id}"
                    class="btn btn-primary"
                >
                    Start Exam
                </a>

            </div>
        `;


        container.appendChild(card);
    });
}


/* ============================================================
   WITHDRAW ENROLLMENT
   ============================================================ */

async function withdrawEnrollment(enrollmentId) {

    const confirmed =
        confirm(
            "Are you sure you want to withdraw this enrollment?"
        );

    if (!confirmed) {
        return;
    }


    try {

        await apiFetch(
            `/enrollments/${enrollmentId}`,
            {
                method: "DELETE"
            }
        );


        /*
         * Reload the page data after withdrawal.
         */

        await loadEnrollments();


    } catch (error) {

        showError(
            error.message ||
            "Unable to withdraw enrollment."
        );
    }
}


/* ============================================================
   FORMAT DATE
   ============================================================ */

function formatDate(dateString) {

    if (!dateString) {
        return "-";
    }

    const date =
        new Date(dateString);

    if (Number.isNaN(date.getTime())) {
        return dateString;
    }

    return date.toLocaleDateString();
}


/* ============================================================
   ERROR
   ============================================================ */

function showError(message) {

    const error =
        document.getElementById("errorMessage");

    error.textContent = message;

    error.style.display = "block";
}


/* ============================================================
   INITIALIZE
   ============================================================ */

document.addEventListener(
    "DOMContentLoaded",
    loadEnrollments
);