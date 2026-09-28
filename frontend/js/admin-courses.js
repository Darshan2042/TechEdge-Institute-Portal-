let courses = [];
let batches = [];
let offers = [];

let editingCourseId = null;
let editingBatchId = null;
let editingOfferId = null;


// =====================================
// ADMIN CHECK
// =====================================

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


// =====================================
// RESPONSE HELPERS
// =====================================

function unwrap(response) {
    return response?.data ?? response;
}


function getList(response) {

    const data = unwrap(response);

    if (Array.isArray(data)) {
        return data;
    }

    if (Array.isArray(data?.content)) {
        return data.content;
    }

    return [];
}


// =====================================
// MESSAGE HELPERS
// =====================================

function showSuccess(message) {

    const element =
        document.getElementById("successMessage");

    element.textContent = message;
    element.style.display = "block";

    setTimeout(() => {
        element.style.display = "none";
    }, 3000);
}


function showError(message) {

    const element =
        document.getElementById("errorMessage");

    element.textContent = message;
    element.style.display = "block";

    setTimeout(() => {
        element.style.display = "none";
    }, 5000);
}


// =====================================
// LOAD COURSES
// =====================================

async function loadCourses() {

    try {

        const response =
            await apiFetch("/courses?page=0&size=100");

        courses = getList(response);

        renderCourses();

        populateCourseSelect();

    } catch (error) {

        showError(
            error.message || "Unable to load courses."
        );

    } finally {

        document.getElementById(
            "coursesLoading"
        ).style.display = "none";

    }
}


// =====================================
// RENDER COURSES
// =====================================

function renderCourses() {

    const container =
        document.getElementById("coursesContainer");

    const empty =
        document.getElementById("coursesEmpty");


    container.innerHTML = "";


    if (!courses.length) {

        container.style.display = "none";
        empty.style.display = "block";

        return;
    }


    empty.style.display = "none";
    container.style.display = "grid";


    courses.forEach(course => {

        const card =
            document.createElement("div");

        card.className =
            "card admin-course-card";


        const title =
            course.title || "-";

        const code =
            course.code || "-";

        const category =
            course.category || "-";

        const level =
            course.level || "-";

        const duration =
            course.durationWeeks ??
            course.duration ??
            "-";

        const fee =
            course.fee ?? "-";

        const active =
            course.active ??
            course.isActive ??
            true;


        card.innerHTML = `
            <h3>${escapeHtml(title)}</h3>

            <p>
                ${escapeHtml(
                    course.description || "No description."
                )}
            </p>

            <div class="course-meta">

                <span>
                    ${escapeHtml(code)}
                </span>

                <span>
                    ${escapeHtml(category)}
                </span>

                <span>
                    ${escapeHtml(level)}
                </span>

                <span>
                    ${duration} weeks
                </span>

                <span>
                    ₹${fee}
                </span>

                <span>
                    ${active ? "ACTIVE" : "INACTIVE"}
                </span>

            </div>

            <div class="admin-card-actions">

                <button
                    class="btn btn-outline btn-sm"
                    onclick="editCourse(${course.id})"
                >
                    Edit
                </button>

                <button
                    class="btn btn-danger btn-sm"
                    onclick="deleteCourse(${course.id})"
                >
                    Delete
                </button>

            </div>
        `;


        container.appendChild(card);

    });
}


// =====================================
// ESCAPE HTML
// =====================================

function escapeHtml(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


// =====================================
// COURSE SELECT
// =====================================

function populateCourseSelect() {

    const select =
        document.getElementById("batchCourse");

    select.innerHTML = `
        <option value="">
            Select Course
        </option>
    `;


    courses.forEach(course => {

        const option =
            document.createElement("option");

        option.value = course.id;

        option.textContent =
            `${course.code || ""} - ${course.title}`;

        select.appendChild(option);

    });
}


// =====================================
// CREATE / UPDATE COURSE
// =====================================

async function saveCourse(event) {

    event.preventDefault();


    const course = {

        code:
            document.getElementById(
                "courseCode"
            ).value.trim(),

        title:
            document.getElementById(
                "courseTitle"
            ).value.trim(),

        description:
            document.getElementById(
                "courseDescription"
            ).value.trim(),

        category:
            document.getElementById(
                "courseCategory"
            ).value,

        durationWeeks:
            Number(
                document.getElementById(
                    "durationWeeks"
                ).value
            ),

        fee:
            Number(
                document.getElementById(
                    "courseFee"
                ).value
            ),

        level:
            document.getElementById(
                "courseLevel"
            ).value,

        active:
            document.getElementById(
                "courseActive"
            ).checked

    };


    try {

        if (editingCourseId) {

            await apiFetch(
                `/courses/${editingCourseId}`,
                {
                    method: "PUT",
                    body: JSON.stringify(course)
                }
            );

            showSuccess(
                "Course updated successfully."
            );

        } else {

            await apiFetch(
                "/courses",
                {
                    method: "POST",
                    body: JSON.stringify(course)
                }
            );

            showSuccess(
                "Course created successfully."
            );
        }


        resetCourseForm();

        await loadCourses();

    } catch (error) {

        showError(
            error.message ||
            "Unable to save course."
        );
    }
}


// =====================================
// EDIT COURSE
// =====================================

function editCourse(id) {

    const course =
        courses.find(
            item => Number(item.id) === Number(id)
        );


    if (!course) {
        return;
    }


    editingCourseId = id;


    document.getElementById(
        "courseFormTitle"
    ).textContent = "Update Course";


    document.getElementById(
        "courseSubmitButton"
    ).textContent = "Update Course";


    document.getElementById(
        "courseCancelButton"
    ).style.display = "inline-block";


    document.getElementById(
        "courseCode"
    ).value = course.code || "";


    document.getElementById(
        "courseTitle"
    ).value = course.title || "";


    document.getElementById(
        "courseDescription"
    ).value = course.description || "";


    document.getElementById(
        "courseCategory"
    ).value = course.category || "";


    document.getElementById(
        "courseLevel"
    ).value = course.level || "";


    document.getElementById(
        "durationWeeks"
    ).value =
        course.durationWeeks ??
        course.duration ??
        "";


    document.getElementById(
        "courseFee"
    ).value = course.fee ?? "";


    document.getElementById(
        "courseActive"
    ).checked =
        course.active ??
        course.isActive ??
        true;


    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


// =====================================
// DELETE COURSE
// =====================================

async function deleteCourse(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this course?"
        );


    if (!confirmed) {
        return;
    }


    try {

        await apiFetch(
            `/courses/${id}`,
            {
                method: "DELETE"
            }
        );


        showSuccess(
            "Course deleted successfully."
        );


        await loadCourses();


    } catch (error) {

        showError(
            error.message ||
            "Unable to delete course."
        );
    }
}


// =====================================
// RESET COURSE FORM
// =====================================

function resetCourseForm() {

    editingCourseId = null;

    document.getElementById(
        "courseForm"
    ).reset();


    document.getElementById(
        "courseActive"
    ).checked = true;


    document.getElementById(
        "courseFormTitle"
    ).textContent = "Create Course";


    document.getElementById(
        "courseSubmitButton"
    ).textContent = "Create Course";


    document.getElementById(
        "courseCancelButton"
    ).style.display = "none";
}


// =====================================
// LOAD BATCHES
// =====================================

async function loadBatches() {

    try {

        const response =
            await apiFetch("/batches");

        batches = getList(response);

        renderBatches();

    } catch (error) {

        showError(
            error.message ||
            "Unable to load batches."
        );

    } finally {

        document.getElementById(
            "batchesLoading"
        ).style.display = "none";
    }
}


// =====================================
// RENDER BATCHES
// =====================================

function renderBatches() {

    const container =
        document.getElementById(
            "batchesContainer"
        );


    const empty =
        document.getElementById(
            "batchesEmpty"
        );


    container.innerHTML = "";


    if (!batches.length) {

        container.style.display = "none";
        empty.style.display = "block";

        return;
    }


    empty.style.display = "none";
    container.style.display = "block";


    const wrapper =
        document.createElement("div");

    wrapper.className =
        "table-wrapper";


    const table =
        document.createElement("table");

    table.className =
        "admin-batch-table";


    table.innerHTML = `
        <thead>

            <tr>

                <th>Batch Code</th>

                <th>Course</th>

                <th>Start</th>

                <th>End</th>

                <th>Timing</th>

                <th>Mode</th>

                <th>Trainer</th>

                <th>Seats</th>

                <th>Status</th>

                <th>Action</th>

            </tr>

        </thead>

        <tbody></tbody>
    `;


    const tbody =
        table.querySelector("tbody");


    batches.forEach(batch => {

        const row =
            document.createElement("tr");


        const course =
            courses.find(
                c =>
                    Number(c.id) ===
                    Number(
                        batch.courseId ??
                        batch.course?.id
                    )
            );


        const courseName =
            batch.courseTitle ||
            batch.course?.title ||
            course?.title ||
            "-";


        const batchCode =
            batch.batchCode ||
            batch.code ||
            "-";


        const startDate =
            batch.startDate || "-";


        const endDate =
            batch.endDate || "-";


        const timing =
            batch.timing || "-";


        const mode =
            batch.mode || "-";


        const trainer =
            batch.trainerName ||
            batch.trainer ||
            "-";


        const totalSeats =
            batch.totalSeats ?? "-";


        const availableSeats =
            batch.availableSeats ?? "-";


        const status =
            batch.status || "-";


        row.innerHTML = `

            <td>
                ${escapeHtml(batchCode)}
            </td>

            <td>
                ${escapeHtml(courseName)}
            </td>

            <td>
                ${escapeHtml(startDate)}
            </td>

            <td>
                ${escapeHtml(endDate)}
            </td>

            <td>
                ${escapeHtml(timing)}
            </td>

            <td>
                ${escapeHtml(mode)}
            </td>

            <td>
                ${escapeHtml(trainer)}
            </td>

            <td>
                ${availableSeats} / ${totalSeats}
            </td>

            <td>
                <span class="batch-status">
                    ${escapeHtml(status)}
                </span>
            </td>

            <td>

                <button
                    class="btn btn-outline btn-sm"
                    onclick="editBatch(${batch.id})"
                >
                    Edit
                </button>

            </td>
        `;


        tbody.appendChild(row);

    });


    wrapper.appendChild(table);

    container.appendChild(wrapper);
}


// =====================================
// CREATE / UPDATE BATCH
// =====================================

async function saveBatch(event) {

    event.preventDefault();


    const batch = {

        courseId:
            Number(
                document.getElementById(
                    "batchCourse"
                ).value
            ),

        batchCode:
            document.getElementById(
                "batchCode"
            ).value.trim(),

        startDate:
            document.getElementById(
                "startDate"
            ).value,

        endDate:
            document.getElementById(
                "endDate"
            ).value,

        timing:
            document.getElementById(
                "timing"
            ).value.trim(),

        mode:
            document.getElementById(
                "batchMode"
            ).value,

        trainerName:
            document.getElementById(
                "trainerName"
            ).value.trim(),

        totalSeats:
            Number(
                document.getElementById(
                    "totalSeats"
                ).value
            ),

        status:
            document.getElementById(
                "batchStatus"
            ).value
    };


    try {

        if (editingBatchId) {

            await apiFetch(
                `/batches/${editingBatchId}`,
                {
                    method: "PUT",
                    body: JSON.stringify(batch)
                }
            );

            showSuccess(
                "Batch updated successfully."
            );

        } else {

            await apiFetch(
                "/batches",
                {
                    method: "POST",
                    body: JSON.stringify(batch)
                }
            );

            showSuccess(
                "Batch created successfully."
            );
        }


        resetBatchForm();

        await loadBatches();

    } catch (error) {

        showError(
            error.message ||
            "Unable to save batch."
        );
    }
}


// =====================================
// EDIT BATCH
// =====================================

function editBatch(id) {

    const batch =
        batches.find(
            item => Number(item.id) === Number(id)
        );


    if (!batch) {
        return;
    }


    editingBatchId = id;


    document.getElementById(
        "batchFormTitle"
    ).textContent = "Update Batch";


    document.getElementById(
        "batchSubmitButton"
    ).textContent = "Update Batch";


    document.getElementById(
        "batchCancelButton"
    ).style.display = "inline-block";


    document.getElementById(
        "batchCourse"
    ).value =
        batch.courseId ??
        batch.course?.id ??
        "";


    document.getElementById(
        "batchCode"
    ).value =
        batch.batchCode ||
        batch.code ||
        "";


    document.getElementById(
        "startDate"
    ).value =
        batch.startDate || "";


    document.getElementById(
        "endDate"
    ).value =
        batch.endDate || "";


    document.getElementById(
        "timing"
    ).value =
        batch.timing || "";


    document.getElementById(
        "batchMode"
    ).value =
        batch.mode || "";


    document.getElementById(
        "trainerName"
    ).value =
        batch.trainerName ||
        batch.trainer ||
        "";


    document.getElementById(
        "totalSeats"
    ).value =
        batch.totalSeats ?? "";


    document.getElementById(
        "batchStatus"
    ).value =
        batch.status || "UPCOMING";


    document.getElementById(
        "batchForm"
    ).scrollIntoView({
        behavior: "smooth"
    });
}


// =====================================
// RESET BATCH FORM
// =====================================

function resetBatchForm() {

    editingBatchId = null;


    document.getElementById(
        "batchForm"
    ).reset();


    document.getElementById(
        "batchFormTitle"
    ).textContent = "Create Batch";


    document.getElementById(
        "batchSubmitButton"
    ).textContent = "Create Batch";


    document.getElementById(
        "batchCancelButton"
    ).style.display = "none";
}


// =====================================
// INITIALIZE
// =====================================

async function initializePage() {

    if (!requireAdmin()) {
        return;
    }


    await loadCourses();

    await loadBatches();
}


// =====================================
// EVENT LISTENERS
// =====================================

document
    .getElementById("courseForm")
    .addEventListener(
        "submit",
        saveCourse
    );


document
    .getElementById("batchForm")
    .addEventListener(
        "submit",
        saveBatch
    );


document
    .getElementById("courseCancelButton")
    .addEventListener(
        "click",
        resetCourseForm
    );


document
    .getElementById("batchCancelButton")
    .addEventListener(
        "click",
        resetBatchForm
    );


document.addEventListener(
    "DOMContentLoaded",
    initializePage
);


// =====================================
// LOAD ALL OFFERS
// =====================================

async function loadOffers() {

    try {

        const response =
            await apiFetch("/offers/all");

        offers = getList(response);

        renderOffers();

        populateOfferSelect();

    } catch (error) {

        showError(
            error.message ||
            "Unable to load offers."
        );

    } finally {

        document.getElementById(
            "offersLoading"
        ).style.display = "none";
    }
}


// =====================================
// RENDER OFFERS
// =====================================

function renderOffers() {

    const container =
        document.getElementById(
            "offersContainer"
        );

    const empty =
        document.getElementById(
            "offersEmpty"
        );


    container.innerHTML = "";


    if (!offers.length) {

        container.style.display = "none";
        empty.style.display = "block";

        return;
    }


    empty.style.display = "none";
    container.style.display = "grid";


    offers.forEach(offer => {

        const card =
            document.createElement("div");

        card.className =
            "card admin-offer-card";


        const active =
            offer.active ??
            offer.isActive ??
            false;


        const discount =
            offer.discountPercent ??
            offer.discount ??
            0;


        card.innerHTML = `

            <h3>
                ${escapeHtml(
                    offer.title || "-"
                )}
            </h3>

            <div class="offer-discount">
                ${discount}% OFF
            </div>

            <span class="offer-code">
                ${escapeHtml(
                    offer.couponCode || "NO CODE"
                )}
            </span>

            <p>
                ${escapeHtml(
                    offer.description ||
                    "No description."
                )}
            </p>

            <div class="offer-dates">

                ${escapeHtml(
                    offer.validFrom || "-"
                )}

                →

                ${escapeHtml(
                    offer.validTo || "-"
                )}

            </div>

            <p>
                Status:
                <strong>
                    ${active ? "ACTIVE" : "INACTIVE"}
                </strong>
            </p>

            <div class="offer-actions">

                <button
                    class="btn btn-outline btn-sm"
                    onclick="editOffer(${offer.id})"
                >
                    Edit
                </button>

                ${
                    active
                    ?
                    `<button
                        class="btn btn-danger btn-sm"
                        onclick="deactivateOffer(${offer.id})"
                    >
                        Deactivate
                    </button>`
                    :
                    ""
                }

            </div>
        `;


        container.appendChild(card);

    });
}


// =====================================
// POPULATE OFFER SELECT
// =====================================

function populateOfferSelect() {

    const select =
        document.getElementById("linkOffer");


    select.innerHTML = `
        <option value="">
            Select Offer
        </option>
    `;


    offers.forEach(offer => {

        const option =
            document.createElement("option");

        option.value = offer.id;

        option.textContent =
            `${offer.title} - ${offer.discountPercent}%`;


        select.appendChild(option);

    });
}


// =====================================
// SAVE OFFER
// =====================================

async function saveOffer(event) {

    event.preventDefault();


    const offer = {

        title:
            document.getElementById(
                "offerTitle"
            ).value.trim(),

        description:
            document.getElementById(
                "offerDescription"
            ).value.trim(),

        discountPercent:
            Number(
                document.getElementById(
                    "discountPercent"
                ).value
            ),

        couponCode:
            document.getElementById(
                "couponCode"
            ).value.trim(),

        validFrom:
            document.getElementById(
                "validFrom"
            ).value,

        validTo:
            document.getElementById(
                "validTo"
            ).value,

        active:
            document.getElementById(
                "offerActive"
            ).checked
    };


    try {

        if (editingOfferId) {

            await apiFetch(
                `/offers/${editingOfferId}`,
                {
                    method: "PUT",
                    body: JSON.stringify(offer)
                }
            );

            showSuccess(
                "Offer updated successfully."
            );

        } else {

            await apiFetch(
                "/offers",
                {
                    method: "POST",
                    body: JSON.stringify(offer)
                }
            );

            showSuccess(
                "Offer created successfully."
            );
        }


        resetOfferForm();

        await loadOffers();

    } catch (error) {

        showError(
            error.message ||
            "Unable to save offer."
        );
    }
}


// =====================================
// EDIT OFFER
// =====================================

function editOffer(id) {

    const offer =
        offers.find(
            item =>
                Number(item.id) ===
                Number(id)
        );


    if (!offer) {
        return;
    }


    editingOfferId = id;


    document.getElementById(
        "offerFormTitle"
    ).textContent = "Update Offer";


    document.getElementById(
        "offerSubmitButton"
    ).textContent = "Update Offer";


    document.getElementById(
        "offerCancelButton"
    ).style.display = "inline-block";


    document.getElementById(
        "offerTitle"
    ).value =
        offer.title || "";


    document.getElementById(
        "offerDescription"
    ).value =
        offer.description || "";


    document.getElementById(
        "discountPercent"
    ).value =
        offer.discountPercent ?? "";


    document.getElementById(
        "couponCode"
    ).value =
        offer.couponCode || "";


    document.getElementById(
        "validFrom"
    ).value =
        offer.validFrom || "";


    document.getElementById(
        "validTo"
    ).value =
        offer.validTo || "";


    document.getElementById(
        "offerActive"
    ).checked =
        offer.active ??
        offer.isActive ??
        false;


    document.getElementById(
        "offerForm"
    ).scrollIntoView({
        behavior: "smooth"
    });
}


// =====================================
// DEACTIVATE OFFER
// =====================================

async function deactivateOffer(id) {

    const confirmed =
        confirm(
            "Are you sure you want to deactivate this offer?"
        );


    if (!confirmed) {
        return;
    }


    try {

        await apiFetch(
            `/offers/${id}`,
            {
                method: "DELETE"
            }
        );


        showSuccess(
            "Offer deactivated successfully."
        );


        await loadOffers();

    } catch (error) {

        showError(
            error.message ||
            "Unable to deactivate offer."
        );
    }
}


// =====================================
// RESET OFFER FORM
// =====================================

function resetOfferForm() {

    editingOfferId = null;


    document.getElementById(
        "offerForm"
    ).reset();


    document.getElementById(
        "offerActive"
    ).checked = true;


    document.getElementById(
        "offerFormTitle"
    ).textContent = "Create Offer";


    document.getElementById(
        "offerSubmitButton"
    ).textContent = "Create Offer";


    document.getElementById(
        "offerCancelButton"
    ).style.display = "none";
}


// =====================================
// LINK OFFER TO COURSE
// =====================================

async function linkOfferToCourse(event) {

    event.preventDefault();


    const courseId =
        document.getElementById(
            "linkCourse"
        ).value;


    const offerId =
        document.getElementById(
            "linkOffer"
        ).value;


    if (!courseId || !offerId) {
        return;
    }


    try {

        await apiFetch(
            `/courses/${courseId}/offers/${offerId}`,
            {
                method: "POST"
            }
        );


        showSuccess(
            "Offer linked to course successfully."
        );


        document.getElementById(
            "linkOfferForm"
        ).reset();


    } catch (error) {

        showError(
            error.message ||
            "Unable to link offer."
        );
    }
}


// =====================================
// INITIALIZE PAGE
// =====================================

async function initializePage() {

    if (!requireAdmin()) {
        return;
    }


    await loadCourses();

    await loadBatches();

    await loadOffers();


    // Populate course selector
    const courseSelect =
        document.getElementById(
            "linkCourse"
        );


    courseSelect.innerHTML = `
        <option value="">
            Select Course
        </option>
    `;


    courses.forEach(course => {

        const option =
            document.createElement("option");

        option.value = course.id;

        option.textContent =
            `${course.code || ""} - ${course.title}`;

        courseSelect.appendChild(option);

    });
}


// =====================================
// EVENT LISTENERS
// =====================================

document
    .getElementById("offerForm")
    .addEventListener(
        "submit",
        saveOffer
    );


document
    .getElementById("offerCancelButton")
    .addEventListener(
        "click",
        resetOfferForm
    );


document
    .getElementById("linkOfferForm")
    .addEventListener(
        "submit",
        linkOfferToCourse
    );