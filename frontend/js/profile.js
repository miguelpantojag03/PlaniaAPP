let currentUser = null;

document.addEventListener("DOMContentLoaded", () => {
    if (!PlaniaSession.requireAuth()) return;
    PlaniaSession.bindLogoutButtons();
    document.querySelector("#retryProfile")?.addEventListener("click", loadProfile);
    document.querySelector("#profileForm")?.addEventListener("submit", updateProfile);
    loadProfile();
});

async function loadProfile() {
    setProfileState("loading");
    try {
        currentUser = await PlaniaApi.getCurrentUser();
        renderProfile(currentUser);
        setProfileState("content");
    } catch (error) {
        console.error(error);
        setProfileState("error");
    }
}

function renderProfile(user) {
    document.querySelector("#profileInitial").textContent = (user.name || "P").charAt(0).toUpperCase();
    document.querySelector("#profileName").textContent = user.name;
    document.querySelector("#profileEmail").textContent = user.email;
    document.querySelector("#createdAt").textContent = `Cuenta creada: ${formatDate(user.createdAt)}`;
    document.querySelector("#pointsLabel").textContent = `${user.totalPoints ?? 0} puntos`;
    document.querySelector("#streakLabel").textContent = `${user.currentStreak ?? 0} dias de racha`;

    const form = document.querySelector("#profileForm");
    form.name.value = user.name;
    form.email.value = user.email;
}

async function updateProfile(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submitButton = form.querySelector("button[type='submit']");
    PlaniaUI.clearErrors(form);

    const payload = {
        name: form.name.value.trim(),
        email: form.email.value.trim()
    };

    if (!validateProfile(form, payload)) return;

    try {
        PlaniaUI.setLoading(submitButton, true);
        const updatedUser = await PlaniaApi.updateCurrentUser(payload);
        PlaniaStorage.saveUser(updatedUser);
        renderProfile(updatedUser);
        PlaniaUI.showToast("Perfil actualizado.", "success");
    } catch (error) {
        PlaniaUI.showBackendErrors(form, error);
    } finally {
        PlaniaUI.setLoading(submitButton, false);
    }
}

function validateProfile(form, payload) {
    let isValid = true;
    if (!payload.name) {
        PlaniaUI.setFieldError(form, "name", "El nombre es obligatorio.");
        isValid = false;
    }

    if (!payload.email) {
        PlaniaUI.setFieldError(form, "email", "El correo es obligatorio.");
        isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(payload.email)) {
        PlaniaUI.setFieldError(form, "email", "Escribe un correo valido.");
        isValid = false;
    }

    return isValid;
}

function formatDate(value) {
    if (!value) return "Sin fecha";
    return new Intl.DateTimeFormat("es-CO", {
        day: "numeric",
        month: "long",
        year: "numeric"
    }).format(new Date(value));
}

function setProfileState(state) {
    document.querySelector("#profileLoading").classList.toggle("hidden", state !== "loading");
    document.querySelector("#profileError").classList.toggle("hidden", state !== "error");
    document.querySelector("#profileContent").classList.toggle("hidden", state !== "content");
}
