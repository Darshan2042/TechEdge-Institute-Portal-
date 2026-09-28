async function loadDashboard() {

    const loading =
        document.getElementById("loading");

    const dashboard =
        document.getElementById("dashboard");

    const errorMessage =
        document.getElementById("errorMessage");


    if (!isLoggedIn()) {

        window.location.href =
            "login.html";

        return;
    }


    try {

        /*
         * Get authenticated user
         */
        const userResponse =
            await apiFetch("/auth/me");

        const user =
            userResponse.data ||
            userResponse;


        /*
         * Get student's enrollments
         */
        const enrollmentResponse =
            await apiFetch("/enrollments/my");

        const enrollmentData =
            enrollmentResponse.data ||
            enrollmentResponse;

        const enrollments =
            Array.isArray(enrollmentData)
                ? enrollmentData
                : enrollmentData.content || [];


        /*
         * Display user
         */
        document.getElementById(
            "studentName"
        ).textContent =
            user.fullName || "-";


        document.getElementById(
            "accountName"
        ).textContent =
            user.fullName || "-";


        document.getElementById(
            "accountEmail"
        ).textContent =
            user.email || "-";


        document.getElementById(
            "accountPhone"
        ).textContent =
            user.phone || "-";


        document.getElementById(
            "accountRole"
        ).textContent =
            user.role || "STUDENT";


        /*
         * Enrollment count
         */
        document.getElementById(
            "enrollmentCount"
        ).textContent =
            enrollments.length;


        loading.style.display = "none";

        dashboard.style.display = "block";


    } catch (error) {

        loading.style.display = "none";

        errorMessage.textContent =
            error.message ||
            "Unable to load dashboard.";

        errorMessage.style.display =
            "block";
    }
}


document.addEventListener(
    "DOMContentLoaded",
    loadDashboard
);