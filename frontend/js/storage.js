const PlaniaStorage = (() => {
    const TOKEN_KEY = "plania_token";
    const USER_KEY = "plania_user";

    function saveSession(authResponse) {
        localStorage.setItem(TOKEN_KEY, authResponse.token);
        localStorage.setItem(USER_KEY, JSON.stringify(authResponse.user));
    }

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        const rawUser = localStorage.getItem(USER_KEY);
        return rawUser ? JSON.parse(rawUser) : null;
    }

    function clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    }

    function isAuthenticated() {
        return Boolean(getToken());
    }

    return {
        saveSession,
        getToken,
        getUser,
        clearSession,
        isAuthenticated
    };
})();
