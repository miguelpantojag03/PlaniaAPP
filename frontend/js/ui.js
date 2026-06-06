const PlaniaUI = (() => {
    function showToast(message, type = "success") {
        const host = document.querySelector("#toastHost");
        if (!host) return;

        const toast = document.createElement("div");
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        host.appendChild(toast);

        window.setTimeout(() => {
            toast.remove();
        }, 3600);
    }

    function setLoading(button, isLoading) {
        if (!button) return;
        button.classList.toggle("is-loading", isLoading);
        button.disabled = isLoading;
    }

    function clearErrors(form) {
        form.querySelectorAll(".field-error").forEach((error) => {
            error.textContent = "";
        });
        form.querySelectorAll(".is-invalid").forEach((field) => {
            field.classList.remove("is-invalid");
        });
    }

    function setFieldError(form, fieldName, message) {
        const field = form.elements[fieldName];
        const error = form.querySelector(`[data-error-for="${fieldName}"]`);

        if (field) {
            field.classList.add("is-invalid");
        }

        if (error) {
            error.textContent = message;
        }
    }

    function showBackendErrors(form, error) {
        if (error.validationErrors) {
            Object.entries(error.validationErrors).forEach(([field, message]) => {
                setFieldError(form, field, message);
            });
            return;
        }

        showToast(error.message || "Ocurrio un error. Intenta nuevamente.", "error");
    }

    return {
        showToast,
        setLoading,
        clearErrors,
        setFieldError,
        showBackendErrors
    };
})();
