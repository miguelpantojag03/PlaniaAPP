document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.querySelector("#loginForm");
    const registerForm = document.querySelector("#registerForm");

    if (loginForm || registerForm) {
        PlaniaSession.redirectIfAuthenticated();
    }

    if (new URLSearchParams(window.location.search).get("expired") === "1") {
        PlaniaUI.showToast("Tu sesion expiro. Inicia sesion nuevamente.", "error");
    }

    if (loginForm) {
        loginForm.addEventListener("submit", handleLogin);
    }

    if (registerForm) {
        registerForm.addEventListener("submit", handleRegister);
    }
});

async function handleLogin(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submitButton = form.querySelector("button[type='submit']");

    PlaniaUI.clearErrors(form);

    const payload = {
        email: form.email.value.trim(),
        password: form.password.value
    };

    if (!validateLogin(form, payload)) {
        return;
    }

    try {
        PlaniaUI.setLoading(submitButton, true);
        const response = await PlaniaApi.login(payload);
        PlaniaStorage.saveSession(response);
        PlaniaUI.showToast("Sesion iniciada correctamente.", "success");
        window.setTimeout(() => {
            window.location.href = "dashboard.html";
        }, 450);
    } catch (error) {
        PlaniaUI.showBackendErrors(form, error);
    } finally {
        PlaniaUI.setLoading(submitButton, false);
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submitButton = form.querySelector("button[type='submit']");

    PlaniaUI.clearErrors(form);

    const payload = {
        name: form.name.value.trim(),
        email: form.email.value.trim(),
        password: form.password.value
    };

    const confirmPassword = form.confirmPassword.value;

    if (!validateRegister(form, payload, confirmPassword)) {
        return;
    }

    try {
        PlaniaUI.setLoading(submitButton, true);
        const response = await PlaniaApi.register(payload);
        PlaniaStorage.saveSession(response);
        PlaniaUI.showToast("Cuenta creada correctamente.", "success");
        window.setTimeout(() => {
            window.location.href = "dashboard.html";
        }, 450);
    } catch (error) {
        PlaniaUI.showBackendErrors(form, error);
    } finally {
        PlaniaUI.setLoading(submitButton, false);
    }
}

function validateLogin(form, payload) {
    let isValid = true;

    if (!payload.email) {
        PlaniaUI.setFieldError(form, "email", "El correo es obligatorio.");
        isValid = false;
    } else if (!isValidEmail(payload.email)) {
        PlaniaUI.setFieldError(form, "email", "Escribe un correo valido.");
        isValid = false;
    }

    if (!payload.password) {
        PlaniaUI.setFieldError(form, "password", "La contrasena es obligatoria.");
        isValid = false;
    }

    return isValid;
}

function validateRegister(form, payload, confirmPassword) {
    let isValid = true;

    if (!payload.name) {
        PlaniaUI.setFieldError(form, "name", "El nombre es obligatorio.");
        isValid = false;
    }

    if (!payload.email) {
        PlaniaUI.setFieldError(form, "email", "El correo es obligatorio.");
        isValid = false;
    } else if (!isValidEmail(payload.email)) {
        PlaniaUI.setFieldError(form, "email", "Escribe un correo valido.");
        isValid = false;
    }

    if (!payload.password) {
        PlaniaUI.setFieldError(form, "password", "La contrasena es obligatoria.");
        isValid = false;
    } else if (payload.password.length < 6) {
        PlaniaUI.setFieldError(form, "password", "Usa minimo 6 caracteres.");
        isValid = false;
    }

    if (!confirmPassword) {
        PlaniaUI.setFieldError(form, "confirmPassword", "Confirma tu contrasena.");
        isValid = false;
    } else if (payload.password !== confirmPassword) {
        PlaniaUI.setFieldError(form, "confirmPassword", "Las contrasenas no coinciden.");
        isValid = false;
    }

    return isValid;
}

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}
