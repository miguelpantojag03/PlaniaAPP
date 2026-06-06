const PlaniaSession = (() => {
    const publicPages = ["index.html", "login.html", "register.html", ""];

    function currentPage() {
        return window.location.pathname.split("/").pop();
    }

    function requireAuth() {
        if (!PlaniaStorage.isAuthenticated()) {
            window.location.href = "login.html";
            return false;
        }
        return true;
    }

    function redirectIfAuthenticated() {
        if (PlaniaStorage.isAuthenticated()) {
            window.location.href = "dashboard.html";
        }
    }

    function protectCurrentPage() {
        if (!publicPages.includes(currentPage())) {
            requireAuth();
        }
    }

    function expireSession() {
        PlaniaStorage.clearSession();
        window.location.href = "login.html?expired=1";
    }

    function logout() {
        PlaniaStorage.clearSession();
        window.location.href = "login.html";
    }

    function bindLogoutButtons() {
        document.querySelectorAll("[data-logout], #logoutButton").forEach((button) => {
            button.addEventListener("click", logout);
        });
    }

    return {
        requireAuth,
        redirectIfAuthenticated,
        protectCurrentPage,
        expireSession,
        logout,
        bindLogoutButtons
    };
})();
