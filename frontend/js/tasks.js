let allTasks = [];
let taskToDelete = null;

document.addEventListener("DOMContentLoaded", () => {
    if (!PlaniaSession.requireAuth()) {
        return;
    }

    bindEvents();
    loadTasks();
});

function bindEvents() {
    PlaniaSession.bindLogoutButtons();
    document.querySelector("#retryTasks")?.addEventListener("click", loadTasks);
    document.querySelector("#openCreateTask")?.addEventListener("click", openCreateModal);
    document.querySelector("#floatingCreateTask")?.addEventListener("click", openCreateModal);
    document.querySelector("#taskForm")?.addEventListener("submit", saveTask);
    document.querySelector("#confirmDelete")?.addEventListener("click", deleteSelectedTask);

    document.querySelectorAll("[data-close-modal]").forEach((button) => {
        button.addEventListener("click", closeTaskModal);
    });

    document.querySelectorAll("[data-close-delete]").forEach((button) => {
        button.addEventListener("click", closeDeleteModal);
    });

    ["searchInput", "statusFilter", "priorityFilter", "categoryFilter", "dateFilter"].forEach((id) => {
        document.querySelector(`#${id}`)?.addEventListener("input", renderTasks);
    });
}

async function loadTasks() {
    setTasksState("loading");

    try {
        allTasks = await PlaniaApi.getTasks();
        populateCategoryFilter();
        renderTasks();
        setTasksState("content");
    } catch (error) {
        console.error(error);
        setTasksState("error");
    }
}

function renderTasks() {
    const container = document.querySelector("#tasksList");
    const filteredTasks = filterTasks();

    if (!filteredTasks.length) {
        container.innerHTML = `
            <div class="empty-state">
                <h2>No encontramos tareas</h2>
                <p>Ajusta los filtros o crea una nueva tarea para empezar.</p>
                <button class="button button-primary" type="button" data-empty-create>Nueva tarea</button>
            </div>
        `;
        container.querySelector("[data-empty-create]")?.addEventListener("click", openCreateModal);
        return;
    }

    container.innerHTML = filteredTasks.map((task) => taskTemplate(task)).join("");

    container.querySelectorAll("[data-complete]").forEach((button) => {
        button.addEventListener("click", () => completeTask(button.dataset.complete));
    });

    container.querySelectorAll("[data-postpone]").forEach((button) => {
        button.addEventListener("click", () => postponeTask(button.dataset.postpone));
    });

    container.querySelectorAll("[data-edit]").forEach((button) => {
        button.addEventListener("click", () => openEditModal(Number(button.dataset.edit)));
    });

    container.querySelectorAll("[data-delete]").forEach((button) => {
        button.addEventListener("click", () => openDeleteModal(Number(button.dataset.delete)));
    });
}

function filterTasks() {
    const search = document.querySelector("#searchInput").value.trim().toLowerCase();
    const status = document.querySelector("#statusFilter").value;
    const priority = document.querySelector("#priorityFilter").value;
    const category = document.querySelector("#categoryFilter").value;
    const date = document.querySelector("#dateFilter").value;

    return allTasks.filter((task) => {
        const text = `${task.title} ${task.description || ""}`.toLowerCase();
        const matchesSearch = !search || text.includes(search);
        const matchesStatus = !status || task.status === status;
        const matchesPriority = !priority || task.priority === priority;
        const matchesCategory = !category || task.categoryName === category;
        const matchesDate = !date || task.dueDate === date;
        return matchesSearch && matchesStatus && matchesPriority && matchesCategory && matchesDate;
    });
}

function populateCategoryFilter() {
    const select = document.querySelector("#categoryFilter");
    if (!select) return;

    const currentValue = select.value;
    const categories = [...new Set(allTasks.map((task) => task.categoryName).filter(Boolean))].sort();

    select.innerHTML = `<option value="">Todas</option>` + categories.map((category) => (
        `<option value="${escapeHtml(category)}">${escapeHtml(category)}</option>`
    )).join("");

    if (categories.includes(currentValue)) {
        select.value = currentValue;
    }
}

function taskTemplate(task) {
    const isCompleted = task.status === "COMPLETED";
    const alertClass = task.procrastinationAlert ? "is-alert" : "";

    return `
        <article class="task-card ${isCompleted ? "is-completed" : ""} ${alertClass}">
            <div>
                <h3>${escapeHtml(task.title)}</h3>
                <p>${escapeHtml(task.description || "Sin descripcion.")}</p>
                <div class="task-meta">
                    ${priorityBadge(task.priority)}
                    ${energyBadge(task.energyRequired)}
                    <span class="badge">${statusLabel(task.status)}</span>
                    ${task.categoryName ? `<span class="badge">${escapeHtml(task.categoryName)}</span>` : ""}
                    <span class="badge">${formatDate(task.dueDate)}</span>
                    <span class="badge">${task.dueTime ? task.dueTime.substring(0, 5) : "Sin hora"}</span>
                    <span class="badge">${task.estimatedMinutes} min</span>
                </div>
                ${task.procrastinationAlert ? `<p class="procrastination-note">Has aplazado esta tarea varias veces. Te recomendamos dividirla en pasos pequenos.</p>` : ""}
            </div>
            <div class="task-actions">
                ${!isCompleted ? `<button class="button button-secondary" type="button" data-complete="${task.id}">Completar</button>` : ""}
                ${!isCompleted ? `<button class="button button-light" type="button" data-postpone="${task.id}">Aplazar</button>` : ""}
                <button class="button button-ghost" type="button" data-edit="${task.id}">Editar</button>
                <button class="button button-danger" type="button" data-delete="${task.id}">Eliminar</button>
            </div>
        </article>
    `;
}

function openCreateModal() {
    const form = document.querySelector("#taskForm");
    form.reset();
    PlaniaUI.clearErrors(form);
    form.taskId.value = "";
    form.status.value = "PENDING";
    document.querySelector("#taskModalTitle").textContent = "Nueva tarea";
    openTaskModal();
}

function openEditModal(taskId) {
    const task = allTasks.find((item) => item.id === taskId);
    if (!task) return;

    const form = document.querySelector("#taskForm");
    PlaniaUI.clearErrors(form);
    form.taskId.value = task.id;
    form.title.value = task.title;
    form.description.value = task.description || "";
    form.dueDate.value = task.dueDate;
    form.dueTime.value = task.dueTime ? task.dueTime.substring(0, 5) : "";
    form.priority.value = task.priority;
    form.energyRequired.value = task.energyRequired;
    form.estimatedMinutes.value = task.estimatedMinutes;
    form.status.value = task.status;
    document.querySelector("#taskModalTitle").textContent = "Editar tarea";
    openTaskModal();
}

function openTaskModal() {
    document.querySelector("#taskModal").classList.remove("hidden");
}

function closeTaskModal() {
    document.querySelector("#taskModal").classList.add("hidden");
}

async function saveTask(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submitButton = form.querySelector("button[type='submit']");
    PlaniaUI.clearErrors(form);

    const payload = buildPayload(form);
    if (!validateTaskForm(form, payload)) {
        return;
    }

    try {
        PlaniaUI.setLoading(submitButton, true);
        if (form.taskId.value) {
            await PlaniaApi.updateTask(form.taskId.value, {
                ...payload,
                status: form.status.value
            });
            PlaniaUI.showToast("Tarea actualizada.", "success");
        } else {
            await PlaniaApi.createTask(payload);
            PlaniaUI.showToast("Tarea creada.", "success");
        }
        closeTaskModal();
        await loadTasks();
    } catch (error) {
        PlaniaUI.showBackendErrors(form, error);
    } finally {
        PlaniaUI.setLoading(submitButton, false);
    }
}

function buildPayload(form) {
    return {
        title: form.title.value.trim(),
        description: form.description.value.trim() || null,
        dueDate: form.dueDate.value,
        dueTime: form.dueTime.value || null,
        priority: form.priority.value,
        energyRequired: form.energyRequired.value,
        estimatedMinutes: Number(form.estimatedMinutes.value),
        categoryId: null
    };
}

function validateTaskForm(form, payload) {
    let isValid = true;

    if (!payload.title) {
        PlaniaUI.setFieldError(form, "title", "El titulo es obligatorio.");
        isValid = false;
    }

    if (!payload.dueDate) {
        PlaniaUI.setFieldError(form, "dueDate", "La fecha limite es obligatoria.");
        isValid = false;
    }

    if (!payload.priority) {
        PlaniaUI.setFieldError(form, "priority", "Selecciona una prioridad.");
        isValid = false;
    }

    if (!payload.energyRequired) {
        PlaniaUI.setFieldError(form, "energyRequired", "Selecciona la energia requerida.");
        isValid = false;
    }

    if (!payload.estimatedMinutes || payload.estimatedMinutes <= 0) {
        PlaniaUI.setFieldError(form, "estimatedMinutes", "El tiempo debe ser mayor a 0.");
        isValid = false;
    }

    return isValid;
}

async function completeTask(taskId) {
    try {
        await PlaniaApi.completeTask(taskId);
        PlaniaUI.showToast("Tarea completada. Sumaste puntos.", "success");
        await loadTasks();
    } catch (error) {
        PlaniaUI.showToast(error.message || "No pudimos completar la tarea.", "error");
    }
}

async function postponeTask(taskId) {
    try {
        await PlaniaApi.postponeTask(taskId);
        PlaniaUI.showToast("Tarea aplazada para manana.", "success");
        await loadTasks();
    } catch (error) {
        PlaniaUI.showToast(error.message || "No pudimos aplazar la tarea.", "error");
    }
}

function openDeleteModal(taskId) {
    taskToDelete = taskId;
    document.querySelector("#deleteModal").classList.remove("hidden");
}

function closeDeleteModal() {
    taskToDelete = null;
    document.querySelector("#deleteModal").classList.add("hidden");
}

async function deleteSelectedTask() {
    if (!taskToDelete) return;

    try {
        await PlaniaApi.deleteTask(taskToDelete);
        PlaniaUI.showToast("Tarea eliminada.", "success");
        closeDeleteModal();
        await loadTasks();
    } catch (error) {
        PlaniaUI.showToast(error.message || "No pudimos eliminar la tarea.", "error");
    }
}

function setTasksState(state) {
    document.querySelector("#tasksLoading").classList.toggle("hidden", state !== "loading");
    document.querySelector("#tasksError").classList.toggle("hidden", state !== "error");
    document.querySelector("#tasksContent").classList.toggle("hidden", state !== "content");
}

function statusLabel(status) {
    return {
        PENDING: "Pendiente",
        COMPLETED: "Completada",
        POSTPONED: "Aplazada",
        CANCELLED: "Cancelada"
    }[status] || status;
}

function priorityBadge(priority) {
    const label = {
        URGENT: "Urgente",
        HIGH: "Alta",
        MEDIUM: "Media",
        LOW: "Baja"
    }[priority] || priority;

    return `<span class="badge badge-priority-${String(priority).toLowerCase()}">${label}</span>`;
}

function energyBadge(energy) {
    const label = {
        HIGH: "Energia alta",
        MEDIUM: "Energia media",
        LOW: "Energia baja"
    }[energy] || energy;

    return `<span class="badge badge-energy">${label}</span>`;
}

function formatDate(value) {
    if (!value) return "";
    const date = new Date(`${value}T00:00:00`);
    return new Intl.DateTimeFormat("es-CO", {
        day: "numeric",
        month: "short",
        year: "numeric"
    }).format(date);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
