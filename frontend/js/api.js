async function apiFetch(endpoint, options = {}) {

    const token = localStorage.getItem("accessToken");

    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers
    });

    if (response.status === 401) {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user");

        window.location.href = "login.html";
        return;
    }

    const data = await response.json().catch(() => null);

    if (!response.ok) {
        throw new Error(
            data?.message || "Something went wrong"
        );
    }

    return data;
}