const params = new URLSearchParams(
    window.location.search
);

const courseId = params.get("id");


async function loadCourseDetail() {

    const loading =
        document.getElementById("loading");

    const errorMessage =
        document.getElementById("errorMessage");

    const detail =
        document.getElementById("courseDetail");


    if (!courseId) {

        loading.style.display = "none";

        errorMessage.textContent =
            "Course ID is missing.";

        errorMessage.style.display = "block";

        return;
    }


    try {

        const response =
            await apiFetch(
                `/courses/${courseId}`
            );


        const course =
            response.data || response;


        renderCourse(course);


        loading.style.display = "none";

        detail.style.display = "block";

    } catch (error) {

        loading.style.display = "none";

        errorMessage.textContent =
            error.message ||
            "Failed to load course details.";

        errorMessage.style.display = "block";
    }
}


function renderCourse(course) {

    document.getElementById("courseTitle")
        .textContent =
        course.title || "-";


    document.getElementById("courseCategory")
        .textContent =
        course.category || "-";


    document.getElementById("courseDescription")
        .textContent =
        course.description ||
        "No description available.";


    document.getElementById("courseCode")
        .textContent =
        course.code || "-";


    document.getElementById("courseLevel")
        .textContent =
        course.level || "-";


    document.getElementById("courseDuration")
        .textContent =
        course.durationWeeks || "-";


    document.getElementById("courseFee")
        .textContent =
        course.discountedFee ??
        course.fee ??
        "0";


    const offerRow =
        document.getElementById("offerRow");

    const offer =
        document.getElementById("courseOffer");


    if (course.activeOfferTitle) {

        offer.textContent =
            course.activeOfferTitle;

    } else {

        offerRow.style.display = "none";
    }


    renderTopics(course.topics || []);

    renderBatches(course.batches || []);
}


function renderTopics(topics) {

    const topicList =
        document.getElementById("topicList");

    topicList.innerHTML = "";


    if (topics.length === 0) {

        topicList.innerHTML = `
            <li>No topics available.</li>
        `;

        return;
    }


    topics.forEach(topic => {

        const li =
            document.createElement("li");

        li.textContent =
            topic.title || topic.name || "-";

        topicList.appendChild(li);

    });
}


function renderBatches(batches) {

    const container =
        document.getElementById("batchContainer");

    container.innerHTML = "";


    if (batches.length === 0) {

        container.innerHTML = `
            <div class="card">
                <h3>No upcoming batches</h3>
                <p>
                    Please check again later.
                </p>
            </div>
        `;

        return;
    }


    batches.forEach(batch => {

        const card =
            document.createElement("div");

        card.className = "card";


        card.innerHTML = `

            <span class="badge">
                ${batch.status || ""}
            </span>

            <h3>
                ${batch.batchCode || "Batch"}
            </h3>

            <p>
                <strong>Start:</strong>
                ${batch.startDate || "-"}
            </p>

            <p>
                <strong>End:</strong>
                ${batch.endDate || "-"}
            </p>

            <p>
                <strong>Timing:</strong>
                ${batch.timing || "-"}
            </p>

            <p>
                <strong>Mode:</strong>
                ${batch.mode || "-"}
            </p>

            <p>
                <strong>Trainer:</strong>
                ${batch.trainerName || "-"}
            </p>

            <p>
                <strong>Available Seats:</strong>
                ${batch.availableSeats ?? 0}
            </p>

        `;

        container.appendChild(card);

    });
}


document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadCourseDetail();


        const loginLink =
            document.getElementById("loginLink");

        const logoutBtn =
            document.getElementById("logoutBtn");


        if (isLoggedIn()) {

            loginLink.style.display = "none";

            logoutBtn.style.display =
                "inline-block";
        }
    }
);