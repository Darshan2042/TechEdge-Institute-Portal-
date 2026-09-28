// frontend/js/admin.js

function requireAdmin() {
    if (!isLoggedIn()) {
        window.location.href = "login.html";
        return false;
    }

    const user = getCurrentUser();

    if (!user || user.role !== "ADMIN") {
        window.location.href = "dashboard.html";
        return false;
    }

    return true;
}


function getList(response) {
    const data = response?.data ?? response;

    if (Array.isArray(data)) {
        return data;
    }

    if (Array.isArray(data?.content)) {
        return data.content;
    }

    return [];
}


function getTotal(response) {
    const data = response?.data ?? response;

    if (Array.isArray(data)) {
        return data.length;
    }

    if (typeof data?.totalElements === "number") {
        return data.totalElements;
    }

    if (Array.isArray(data?.content)) {
        return data.content.length;
    }

    return 0;
}


async function loadDashboard() {

    if (!requireAdmin()) {
        return;
    }

    try {

        const [
            coursesResponse,
            batchesResponse,
            enrollmentsResponse,
            feedbackResponse,
            placementStatsResponse
        ] = await Promise.all([

            apiFetch("/courses?page=0&size=1"),

            apiFetch("/batches?page=0&size=1"),

            apiFetch("/enrollments"),

            apiFetch("/feedback/pending"),

            apiFetch("/placements/stats")

        ]);


        // -------------------------
        // COURSE COUNT
        // -------------------------

        document.getElementById("courseCount").textContent =
            getTotal(coursesResponse);


        // -------------------------
        // BATCH COUNT
        // -------------------------

        document.getElementById("batchCount").textContent =
            getTotal(batchesResponse);


        // -------------------------
        // ENROLLMENT COUNT
        // -------------------------

        document.getElementById("enrollmentCount").textContent =
            getTotal(enrollmentsResponse);


        // -------------------------
        // PENDING FEEDBACK
        // -------------------------

        document.getElementById("feedbackCount").textContent =
            getList(feedbackResponse).length;


        // -------------------------
        // PLACEMENT STATISTICS
        // -------------------------

        const placementStats =
            placementStatsResponse?.data ??
            placementStatsResponse ??
            {};


        const highestPackage =
            placementStats.highestPackage ??
            placementStats.highestPackageLpa ??
            placementStats.maxPackage ??
            0;


        const averagePackage =
            placementStats.averagePackage ??
            placementStats.averagePackageLpa ??
            placementStats.avgPackage ??
            0;


        const placementsThisYear =
            placementStats.placementsThisYear ??
            placementStats.currentYearPlacements ??
            placementStats.totalPlacements ??
            placementStats.placementCount ??
            0;


        const hiringPartners =
            placementStats.hiringPartners ??
            placementStats.totalCompanies ??
            placementStats.companyCount ??
            0;


        document.getElementById("highestPackage").textContent =
            highestPackage || "-";


        document.getElementById("averagePackage").textContent =
            averagePackage || "-";


        document.getElementById("placementsThisYear").textContent =
            placementsThisYear;


        document.getElementById("placedCount").textContent =
            placementsThisYear;


        document.getElementById("hiringPartners").textContent =
            hiringPartners;


        // -------------------------
        // SHOW DASHBOARD
        // -------------------------

        document.getElementById("loading").style.display = "none";

        document.getElementById("dashboardContainer").style.display = "block";

    } catch (error) {

        console.error("Admin dashboard error:", error);

        document.getElementById("loading").style.display = "none";

        const errorMessage =
            document.getElementById("errorMessage");

        errorMessage.textContent =
            error.message || "Unable to load admin dashboard.";

        errorMessage.style.display = "block";
    }
}


document.addEventListener(
    "DOMContentLoaded",
    loadDashboard
);