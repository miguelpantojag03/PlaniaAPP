const PlaniaApi = (() => {
    const API_BASE_URL = "http://localhost:8080/api";

    async function request(path, options = {}) {
        const token = PlaniaStorage.getToken();
        const headers = {
            "Content-Type": "application/json",
            ...(options.headers || {})
        };

        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE_URL}${path}`, {
            ...options,
            headers
        });

        if (response.status === 401) {
            PlaniaStorage.clearSession();
            window.location.href = "login.html";
            return null;
        }

        const hasBody = response.status !== 204;
        const data = hasBody ? await response.json() : null;

        if (!response.ok) {
            throw data || { message: "No pudimos completar la solicitud." };
        }

        return data;
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
        completeTask: (id) => request(`/tasks/${id}/complete`, {
            method: "PATCH"
        }),
        postponeTask: (id) => request(`/tasks/${id}/postpone`, {
            method: "PATCH"
        })
    };
})();
