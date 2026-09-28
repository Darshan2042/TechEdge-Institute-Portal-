async function loadPlacementStats() {

    const container =
        document.getElementById("statsContainer");


    try {

        const response =
            await apiFetch(
                "/placements/stats"
            );


        const stats =
            response.data || response;


        container.innerHTML = `

            <div class="card">

                <h3>
                    Total Placed
                </h3>

                <p class="stat-value">
                    ${stats.totalPlaced ?? 0}
                </p>

            </div>


            <div class="card">

                <h3>
                    Hiring Partners
                </h3>

                <p class="stat-value">
                    ${stats.hiringPartners ?? 0}
                </p>

            </div>


            <div class="card">

                <h3>
                    Highest Package
                </h3>

                <p class="stat-value">
                    ₹${stats.highestPackageLpa ?? 0} LPA
                </p>

            </div>


            <div class="card">

                <h3>
                    Average Package
                </h3>

                <p class="stat-value">
                    ₹${stats.averagePackageLpa
                        ? Number(
                            stats.averagePackageLpa
                          ).toFixed(2)
                        : "0"} LPA
                </p>

            </div>

        `;

    } catch (error) {

        container.innerHTML = `
            <div class="card">
                <h3>
                    Unable to load statistics
                </h3>

                <p>
                    ${error.message}
                </p>
            </div>
        `;
    }
}


async function loadPlacements() {

    const container =
        document.getElementById(
            "placementContainer"
        );


    try {

        const response =
            await apiFetch(
                "/placements?page=0&size=6"
            );


        const page =
            response.data || response;


        const placements =
            page.content || [];


        if (placements.length === 0) {

            container.innerHTML = `
                <div class="card">
                    <h3>
                        No placements available
                    </h3>
                </div>
            `;

            return;
        }


        placements.forEach(placement => {

            const card =
                document.createElement("div");

            card.className = "card";


            card.innerHTML = `

                <h3>
                    ${placement.studentName || "-"}
                </h3>

                <p>
                    <strong>Company:</strong>
                    ${placement.companyName || "-"}
                </p>

                <p>
                    <strong>Job:</strong>
                    ${placement.jobTitle || "-"}
                </p>

                <p>
                    <strong>Course:</strong>
                    ${placement.courseTitle || "-"}
                </p>

                <p>
                    <strong>Package:</strong>
                    ₹${placement.packageLpa ?? "-"} LPA
                </p>

                <p>
                    <strong>Placed On:</strong>
                    ${placement.placedOn || "-"}
                </p>

            `;


            container.appendChild(card);

        });

    } catch (error) {

        container.innerHTML = `
            <div class="card">
                <h3>
                    Unable to load placements
                </h3>

                <p>
                    ${error.message}
                </p>
            </div>
        `;
    }
}


document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadPlacementStats();

        loadPlacements();


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