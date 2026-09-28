function saveAuth(data) {

    localStorage.setItem(
        "accessToken",
        data.accessToken
    );

    localStorage.setItem(
        "refreshToken",
        data.refreshToken
    );

    if (data.user) {
        localStorage.setItem(
            "user",
            JSON.stringify(data.user)
        );
    }
}


function getCurrentUser() {

    const user = localStorage.getItem("user");

    return user ? JSON.parse(user) : null;
}


function isLoggedIn() {

    return !!localStorage.getItem("accessToken");
}


function logout() {

    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");

    window.location.href = "index.html";
}