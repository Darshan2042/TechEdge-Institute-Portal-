let currentPage = 0;

const pageSize = 6;


async function loadCourses() {

    const container =
        document.getElementById("courseContainer");

    const loading =
        document.getElementById("loading");

    const errorMessage =
        document.getElementById("errorMessage");

    container.innerHTML = "";

    errorMessage.style.display = "none";

    loading.style.display = "block";


    const search =
        document.getElementById("searchInput").value.trim();

    const category =
        document.getElementById("categoryFilter").value;

    const level =
        document.getElementById("levelFilter").value;


    const params = new URLSearchParams();

    params.append("page", currentPage);
    params.append("size", pageSize);


    if (search) {
        params.append("search", search);
    }

    if (category) {
        params.append("category", category);
    }

    if (level) {
        params.append("level", level);
    }


    try {

        const response =
            await apiFetch(`/courses?${params.toString()}`);


        const page =
            response.data || response;


        renderCourses(page);

    } catch (error) {

        errorMessage.textContent =
            error.message || "Failed to load courses.";

        errorMessage.style.display = "block";

    } finally {

        loading.style.display = "none";
    }
}


function renderCourses(page) {

    const container =
        document.getElementById("courseContainer");

    container.innerHTML = "";


    const courses =
        page.content || [];


    if (courses.length === 0) {

        container.innerHTML = `
            <div class="card">
                <h3>No courses found</h3>
                <p>
                    Try changing your search or filters.
                </p>
            </div>
        `;

        renderPagination(page);

        return;
    }


    courses.forEach(course => {

        const card =
            document.createElement("div");

        card.className = "card";


        card.innerHTML = `

            <span class="badge">
                ${course.category || ""}
            </span>

            <h3>
                ${course.title || "Untitled Course"}
            </h3>

            <p>
                ${course.description || "No description available."}
            </p>

            <p>
                <strong>Level:</strong>
                ${course.level || "-"}
            </p>

            <p>
                <strong>Duration:</strong>
                ${course.durationWeeks || "-"} weeks
            </p>

            <p>
                <strong>Fee:</strong>
                ₹${course.discountedFee ?? course.fee ?? 0}
            </p>

            ${
                course.activeOfferTitle
                    ? `<p>
                        <strong>Offer:</strong>
                        ${course.activeOfferTitle}
                       </p>`
                    : ""
            }

            <a
                href="course-detail.html?id=${course.id}"
                class="btn btn-primary"
            >
                View Course
            </a>
        `;


        container.appendChild(card);

    });


    renderPagination(page);
}


function renderPagination(page) {

    const pagination =
        document.getElementById("pagination");

    pagination.innerHTML = "";


    if (!page || page.totalPages <= 1) {
        return;
    }


    if (currentPage > 0) {

        const previous =
            document.createElement("button");

        previous.className = "btn btn-secondary";

        previous.textContent = "Previous";

        previous.onclick = () => {

            currentPage--;

            loadCourses();
        };

        pagination.appendChild(previous);
    }


    const pageInfo =
        document.createElement("span");

    pageInfo.textContent =
        `Page ${currentPage + 1} of ${page.totalPages}`;

    pagination.appendChild(pageInfo);


    if (!page.last) {

        const next =
            document.createElement("button");

        next.className = "btn btn-primary";

        next.textContent = "Next";

        next.onclick = () => {

            currentPage++;

            loadCourses();
        };

        pagination.appendChild(next);
    }
}


document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadCourses();

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