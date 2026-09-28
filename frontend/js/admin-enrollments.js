// ======================================
// ADMIN ENROLLMENTS
// ======================================

async function loadEnrollments() {

    if (!isLoggedIn()) {

        window.location.href = "login.html";

        return;
    }


    const user = getCurrentUser();


    if (!user || user.role !== "ADMIN") {

        window.location.href = "dashboard.html";

        return;
    }


    const status =
        document.getElementById(
            "statusFilter"
        ).value;


    const loading =
        document.getElementById("loading");

    const error =
        document.getElementById(
            "errorMessage"
        );

    const container =
        document.getElementById(
            "enrollmentContainer"
        );


    loading.style.display = "block";

    error.style.display = "none";

    container.style.display = "none";


    try {

        let endpoint = "/enrollments";


        if (status) {

            endpoint +=
                `?status=${encodeURIComponent(status)}`;

        }


        const response =
            await apiFetch(endpoint);


        const enrollments =
            response.data || response;


        renderEnrollments(
            Array.isArray(enrollments)
                ? enrollments
                : []
        );


        loading.style.display = "none";

        container.style.display = "block";


    } catch (err) {

        loading.style.display = "none";

        error.textContent =
            err.message ||
            "Unable to load enrollments.";

        error.style.display = "block";

    }
}


// ======================================
// RENDER
// ======================================

function renderEnrollments(enrollments) {

    const tbody =
        document.getElementById(
            "enrollmentTableBody"
        );

    const emptyMessage =
        document.getElementById(
            "emptyMessage"
        );


    tbody.innerHTML = "";


    if (enrollments.length === 0) {

        emptyMessage.style.display =
            "block";

        return;
    }


    emptyMessage.style.display =
        "none";


    enrollments.forEach(
        enrollment => {

            const row =
                document.createElement("tr");


            const status =
                enrollment.status || "-";


            const isApplied =
                status === "APPLIED";


            row.innerHTML = `

                <td>
                    ${enrollment.id ?? "-"}
                </td>

                <td>
                    ${
                        enrollment.studentName ||
                        enrollment.student?.name ||
                        "-"
                    }
                </td>

                <td>
                    ${
                        enrollment.courseTitle ||
                        enrollment.course?.title ||
                        "-"
                    }
                </td>

                <td>
                    ${
                        enrollment.batchCode ||
                        enrollment.batch?.code ||
                        "-"
                    }
                </td>

                <td>
                    <span class="status-badge status-${status.toLowerCase()}">
                        ${status}
                    </span>
                </td>

                <td>
                    ${formatDate(enrollment.appliedAt)}
                </td>

                <td>

                    ${
                        isApplied

                        ?

                        `
                        <button
                            class="btn btn-primary btn-sm"
                            onclick="approveEnrollment(${enrollment.id})"
                        >
                            Approve
                        </button>

                        <button
                            class="btn btn-danger btn-sm"
                            onclick="rejectEnrollment(${enrollment.id})"
                        >
                            Reject
                        </button>
                        `

                        :

                        `<span class="muted-text">
                            No action
                        </span>`
                    }

                </td>

            `;


            tbody.appendChild(row);

        }
    );
}


// ======================================
// APPROVE
// ======================================

async function approveEnrollment(
    enrollmentId
) {

    const confirmed =
        confirm(
            "Are you sure you want to approve this enrollment?"
        );


    if (!confirmed) {
        return;
    }


    try {

        await apiFetch(
            `/enrollments/${enrollmentId}/approve`,
            {
                method: "POST"
            }
        );


        showSuccess(
            "Enrollment approved successfully."
        );


        await loadEnrollments();


    } catch (error) {

        showError(
            error.message ||
            "Unable to approve enrollment."
        );

    }
}


// ======================================
// REJECT
// ======================================

async function rejectEnrollment(
    enrollmentId
) {

    const confirmed =
        confirm(
            "Are you sure you want to reject this enrollment?"
        );


    if (!confirmed) {
        return;
    }


    try {

        await apiFetch(
            `/enrollments/${enrollmentId}/reject`,
            {
                method: "POST"
            }
        );


        showSuccess(
            "Enrollment rejected successfully."
        );


        await loadEnrollments();


    } catch (error) {

        showError(
            error.message ||
            "Unable to reject enrollment."
        );

    }
}


// ======================================
// DATE FORMAT
// ======================================

function formatDate(value) {

    if (!value) {
        return "-";
    }


    const date =
        new Date(value);


    if (Number.isNaN(date.getTime())) {
        return value;
    }


    return date.toLocaleString();
}


// ======================================
// SUCCESS
// ======================================

function showSuccess(message) {

    const success =
        document.getElementById(
            "successMessage"
        );


    success.textContent =
        message;


    success.style.display =
        "block";


    setTimeout(
        () => {

            success.style.display =
                "none";

        },
        3000
    );
}


// ======================================
// ERROR
// ======================================

function showError(message) {

    const error =
        document.getElementById(
            "errorMessage"
        );


    error.textContent =
        message;


    error.style.display =
        "block";
}


// ======================================
// PAGE LOAD
// ======================================

document.addEventListener(
    "DOMContentLoaded",
    loadEnrollments
);