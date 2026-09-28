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
// LOAD PENDING FEEDBACK
// ==========================================

async function loadPendingFeedback() {

    const loading =
        document.getElementById("loading");

    const container =
        document.getElementById("feedbackContainer");

    const pendingContainer =
        document.getElementById("pendingFeedback");

    const empty =
        document.getElementById("emptyMessage");


    try {

        loading.style.display = "block";

        container.style.display = "none";

        empty.style.display = "none";


        const response =
            await apiFetch("/feedback/pending");


        const data =
            response.data || response;


        const feedback =
            Array.isArray(data)
                ? data
                : (data.content || []);


        loading.style.display = "none";


        if (feedback.length === 0) {

            empty.style.display = "block";

            return;
        }


        container.style.display = "block";


        pendingContainer.innerHTML =
            feedback.map(item => {

                return `

                    <div class="card">

                        <div style="
                            display:flex;
                            justify-content:space-between;
                            align-items:center;
                            gap:15px;
                            margin-bottom:15px;
                        ">

                            <div>

                                <h3>
                                    ${escapeHtml(
                                        item.studentName || "Student"
                                    )}
                                </h3>

                                <p class="muted-text">
                                    ${escapeHtml(
                                        item.courseTitle || "-"
                                    )}
                                </p>

                            </div>


                            <span class="status-badge status-applied">

                                Pending

                            </span>

                        </div>


                        <div style="
                            margin-bottom:15px;
                            font-size:20px;
                        ">

                            ${renderStars(item.rating)}

                        </div>


                        <p style="
                            margin-bottom:20px;
                            color:#475569;
                        ">

                            ${escapeHtml(
                                item.comments || "No comments"
                            )}

                        </p>


                        <p class="muted-text">

                            Submitted:
                            ${formatDate(item.createdAt)}

                        </p>


                        <div style="
                            margin-top:20px;
                            display:flex;
                            gap:10px;
                        ">

                            <button
                                    class="btn btn-primary btn-sm"
                                    onclick="approveFeedback(${item.id})">

                                Approve & Publish

                            </button>

                            <button
                                    class="btn btn-danger btn-sm"
                                    onclick="deleteFeedback(${item.id})">

                                Remove

                            </button>

                        </div>

                    </div>

                `;

            }).join("");


    } catch (error) {

        loading.style.display = "none";

        showError(
            error.message ||
            "Unable to load pending feedback."
        );
    }
}


// ==========================================
// LOAD PUBLISHED FEEDBACK
// ==========================================

async function loadPublishedFeedback() {

    const container =
        document.getElementById("publishedFeedback");


    try {

        const response =
            await apiFetch("/feedback");


        const data =
            response.data || response;


        const feedback =
            Array.isArray(data)
                ? data
                : (data.content || []);


        if (feedback.length === 0) {

            container.innerHTML = `

                <div class="card text-center">

                    <p>
                        No published feedback yet.
                    </p>

                </div>

            `;

            return;
        }


        container.innerHTML =
            feedback.map(item => {

                return `

                    <div class="card">

                        <h3>
                            ${escapeHtml(
                                item.studentName || "Student"
                            )}
                        </h3>


                        <p class="muted-text">

                            ${escapeHtml(
                                item.courseTitle || "-"
                            )}

                        </p>


                        <div style="
                            margin:15px 0;
                            font-size:20px;
                        ">

                            ${renderStars(item.rating)}

                        </div>


                        <p>

                            ${escapeHtml(
                                item.comments || "No comments"
                            )}

                        </p>


                        ${
                            item.placedAt
                                ? `
                                    <p
                                        class="muted-text"
                                        style="margin-top:15px;"
                                    >
                                        Placed at:
                                        ${escapeHtml(item.placedAt)}
                                    </p>
                                  `
                                : ""
                        }

                    </div>

                `;

            }).join("");


    } catch (error) {

        container.innerHTML = `

            <div class="card text-center">

                <p>
                    Unable to load published feedback.
                </p>

            </div>

        `;
    }
}


// ==========================================
// APPROVE FEEDBACK
// ==========================================

async function approveFeedback(id) {

    clearMessages();


    try {

        await apiFetch(
            `/feedback/${id}/approve`,
            {
                method: "PATCH"
            }
        );


        showSuccess(
            "Feedback approved and published successfully."
        );


        await loadPendingFeedback();

        await loadPublishedFeedback();


    } catch (error) {

        showError(
            error.message ||
            "Unable to approve feedback."
        );
    }
}


// ==========================================
// DELETE FEEDBACK
// ==========================================

async function deleteFeedback(id) {

    const confirmed =
        confirm(
            "Are you sure you want to remove this feedback?"
        );


    if (!confirmed) {
        return;
    }


    clearMessages();


    try {

        await apiFetch(
            `/feedback/${id}`,
            {
                method: "DELETE"
            }
        );


        showSuccess(
            "Feedback removed successfully."
        );


        await loadPendingFeedback();

        await loadPublishedFeedback();


    } catch (error) {

        showError(
            error.message ||
            "Unable to remove feedback."
        );
    }
}


// ==========================================
// STAR DISPLAY
// ==========================================

function renderStars(rating) {

    const value =
        Number(rating) || 0;


    return "★".repeat(value) +
           "☆".repeat(Math.max(0, 5 - value));
}


// ==========================================
// DATE FORMAT
// ==========================================

function formatDate(value) {

    if (!value) {
        return "-";
    }


    const date =
        new Date(value);


    if (Number.isNaN(date.getTime())) {
        return value;
    }


    return date.toLocaleDateString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }
    );
}


// ==========================================
// HTML ESCAPE
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
// MESSAGES
// ==========================================

function showError(message) {

    const element =
        document.getElementById("errorMessage");


    element.textContent = message;

    element.style.display = "block";
}


function showSuccess(message) {

    const element =
        document.getElementById("successMessage");


    element.textContent = message;

    element.style.display = "block";


    setTimeout(() => {

        element.style.display = "none";

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
// INITIALIZE
// ==========================================

document.addEventListener(
    "DOMContentLoaded",
    async function () {

        if (!checkAdmin()) {
            return;
        }


        await loadPendingFeedback();

        await loadPublishedFeedback();

    }
);