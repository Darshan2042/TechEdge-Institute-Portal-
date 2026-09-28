async function loadOffers() {

    const container =
        document.getElementById("offerContainer");

    const loading =
        document.getElementById("loading");

    const errorMessage =
        document.getElementById("errorMessage");


    try {

        const response =
            await apiFetch("/offers");


        const offers =
            response.data || response;


        renderOffers(offers);

    } catch (error) {

        errorMessage.textContent =
            error.message || "Failed to load offers.";

        errorMessage.style.display = "block";

    } finally {

        loading.style.display = "none";
    }
}


function renderOffers(offers) {

    const container =
        document.getElementById("offerContainer");

    container.innerHTML = "";


    if (!offers || offers.length === 0) {

        container.innerHTML = `
            <div class="card">
                <h3>No active offers</h3>
                <p>
                    Check again later for new offers.
                </p>
            </div>
        `;

        return;
    }


    offers.forEach(offer => {

        const card =
            document.createElement("div");

        card.className = "card";


        card.innerHTML = `

            <span class="badge">
                ${offer.discountPercent ?? 0}% OFF
            </span>

            <h3>
                ${offer.title || "Special Offer"}
            </h3>

            <p>
                ${offer.description || ""}
            </p>

            <p>
                <strong>Coupon:</strong>
                ${offer.couponCode || "-"}
            </p>

            <p>
                <strong>Valid From:</strong>
                ${offer.validFrom || "-"}
            </p>

            <p>
                <strong>Valid Until:</strong>
                ${offer.validTo || "-"}
            </p>

        `;


        container.appendChild(card);

    });
}


document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadOffers();


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