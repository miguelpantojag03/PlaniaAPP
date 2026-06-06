const PlaniaApi = (() => {
    const API_BASE_URL = "http://localhost:8080/api";

    async function request(path, options = {}) {
        const token = PlaniaStorage.getToken();
        const headers = buildHeaders(options.headers || {}, token);

        let response;
        try {
            response = await fetch(`${API_BASE_URL}${path}`, {
                ...options,
                headers
            });
        } catch (error) {
            throw {
                status: 0,
                message: "No pudimos conectar con el servidor. Verifica que el backend este corriendo."
            };
        }

        const data = await parseResponse(response);

        if (response.status === 401) {
            if (window.PlaniaSession) {
                PlaniaSession.expireSession();
            } else {
                PlaniaStorage.clearSession();
                window.location.href = "login.html";
            }
            return null;
        }

        if (!response.ok) {
            throw normalizeError(data, response);
        }

        return data;
    }

    function buildHeaders(customHeaders, token) {
        const headers = { ...customHeaders };

        if (!headers["Content-Type"] && !headers["content-type"]) {
            headers["Content-Type"] = "application/json";
        }

        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        return headers;
    }

    async function parseResponse(response) {
        if (response.status === 204) {
            return null;
        }

        const contentType = response.headers.get("content-type") || "";

        if (contentType.includes("application/json")) {
            return response.json();
        }

        const text = await response.text();
        return text ? { message: text } : null;
    }

    function normalizeError(data, response) {
        return {
            status: data?.status || response.status,
            error: data?.error || response.statusText,
            message: data?.message || "No pudimos completar la solicitud.",
            path: data?.path,
            validationErrors: data?.validationErrors
        };
    }

    return {
        login: (payload) => request("/auth/login", {
            method: "POST",
            body: JSON.stringify(payload)
        }),
        register: (payload) => request("/auth/register", {
            method: "POST",
            body: JSON.stringify(payload)
        }),
        getCurrentUser: () => request("/users/me"),
        updateCurrentUser: (payload) => request("/users/me", {
            method: "PUT",
            body: JSON.stringify(payload)
        }),
        getDashboard: () => request("/dashboard/today"),
        getTasks: () => request("/tasks"),
        createTask: (payload) => request("/tasks", {
            method: "POST",
            body: JSON.stringify(payload)
        }),
        updateTask: (id, payload) => request(`/tasks/${id}`, {
            method: "PUT",
            body: JSON.stringify(payload)
        }),
        deleteTask: (id) => request(`/tasks/${id}`, {
            method: "DELETE"
        }),
        saveMood: (payload) => request("/moods", {
            method: "POST",
            body: JSON.stringify(payload)
        }),
        getMoodHistory: () => request("/moods/history"),
        completeTask: (id) => request(`/tasks/${id}/complete`, {
            method: "PATCH"
        }),
        postponeTask: (id) => request(`/tasks/${id}/postpone`, {
            method: "PATCH"
        })
    };
})();
