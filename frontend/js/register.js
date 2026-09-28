document
    .getElementById("registerForm")
    .addEventListener("submit", async function (event) {

        event.preventDefault();


        const errorMessage =
            document.getElementById("errorMessage");

        const successMessage =
            document.getElementById("successMessage");


        errorMessage.style.display = "none";
        successMessage.style.display = "none";


        const request = {

            fullName:
                document
                    .getElementById("fullName")
                    .value
                    .trim(),

            email:
                document
                    .getElementById("email")
                    .value
                    .trim(),

            phone:
                document
                    .getElementById("phone")
                    .value
                    .trim(),

            password:
                document
                    .getElementById("password")
                    .value
        };


        try {

            await apiFetch(
                "/auth/register",
                {
                    method: "POST",

                    body: JSON.stringify(request)
                }
            );


            successMessage.textContent =
                "Registration successful. Redirecting to login...";

            successMessage.style.display =
                "block";


            document
                .getElementById("registerForm")
                .reset();


            setTimeout(() => {

                window.location.href =
                    "login.html";

            }, 1200);


        } catch (error) {

            errorMessage.textContent =
                error.message ||
                "Registration failed.";

            errorMessage.style.display =
                "block";
        }

    });