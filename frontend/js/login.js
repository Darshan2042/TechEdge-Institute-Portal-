document
    .getElementById("loginForm")
    .addEventListener("submit", async function (event) {

        event.preventDefault();


        const email =
            document.getElementById("email").value.trim();

        const password =
            document.getElementById("password").value;


        const errorMessage =
            document.getElementById("errorMessage");


        errorMessage.style.display = "none";


        try {

            const response = await apiFetch(
                "/auth/login",
                {
                    method: "POST",
                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                }
            );


            const data =
                response.data || response;


            saveAuth(data);


            const user = data.user;


            if (
                user &&
                user.role === "ADMIN"
            ) {

                window.location.href =
                    "admin.html";

            } else {

                window.location.href =
                    "dashboard.html";

            }


        } catch (error) {

            errorMessage.textContent =
                error.message ||
                "Login failed.";

            errorMessage.style.display =
                "block";
        }

    });