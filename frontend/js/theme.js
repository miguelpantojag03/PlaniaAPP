const PlaniaTheme = (() => {
    const THEME_KEY = "plania_theme";

    function getPreferredTheme() {
        const savedTheme = localStorage.getItem(THEME_KEY);
        if (savedTheme === "dark" || savedTheme === "light") {
            return savedTheme;
        }

        return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    }

    function applyTheme(theme) {
        document.documentElement.dataset.theme = theme;
        localStorage.setItem(THEME_KEY, theme);
        updateButtons(theme);
    }

    function updateButtons(theme) {
        document.querySelectorAll("[data-theme-icon]").forEach((icon) => {
            icon.textContent = theme === "dark" ? "Oscuro" : "Claro";
        });
    }

    function toggleTheme() {
        const currentTheme = document.documentElement.dataset.theme || getPreferredTheme();
        applyTheme(currentTheme === "dark" ? "light" : "dark");
    }

    function bind() {
        applyTheme(getPreferredTheme());
        document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
            button.addEventListener("click", toggleTheme);
        });
    }

    return {
        bind,
        applyTheme
    };
})();

document.addEventListener("DOMContentLoaded", PlaniaTheme.bind);
