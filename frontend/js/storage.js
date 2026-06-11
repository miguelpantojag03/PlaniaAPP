const PlaniaStorage = (() => {
    const TOKEN_KEY = "plania_token";
    const USER_KEY = "plania_user";

    function saveSession(authResponse) {
        localStorage.setItem(TOKEN_KEY, authResponse.token);
        localStorage.setItem(USER_KEY, JSON.stringify(authResponse.user));
    }

    function saveUser(user) {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    }

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        const rawUser = localStorage.getItem(USER_KEY);
        try {
            return rawUser ? JSON.parse(rawUser) : null;
        } catch (error) {
            clearSession();
            return null;
        }
    }

    function clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    }

    function isAuthenticated() {
        const token = getToken();
        if (!token) {
            return false;
        }

        if (isTokenExpired(token)) {
            clearSession();
            return false;
        }

        return true;
    }

    function isTokenExpired(token) {
        try {
            const payload = JSON.parse(atob(token.split(".")[1]));
            if (!payload.exp) {
                return false;
            }
            return payload.exp * 1000 <= Date.now();
        } catch (error) {
            return true;
        }
    }

    return {
        saveSession,
        saveUser,
        getToken,
        getUser,
        clearSession,
        isAuthenticated
    };
})();
