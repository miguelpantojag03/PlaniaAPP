const moodOptions = [
    { value: "ENERGETIC", label: "Con energia", hint: "Ideal para tareas intensas" },
    { value: "NORMAL", label: "Normal", hint: "Buen ritmo para avanzar" },
    { value: "TIRED", label: "Cansado", hint: "Mejor tareas pequenas" },
    { value: "STRESSED", label: "Estresado", hint: "Prioriza con calma" },
    { value: "UNMOTIVATED", label: "Sin ganas", hint: "Empieza con algo corto" }
];

document.addEventListener("DOMContentLoaded", () => {
    if (!PlaniaSession.requireAuth()) {
        return;
    }

    document.querySelector("#retryButton")?.addEventListener("click", loadDashboard);
    PlaniaSession.bindLogoutButtons();
    loadDashboard();
});

async function loadDashboard() {
    setDashboardState("loading");

    try {
        const dashboard = await PlaniaApi.getDashboard();
        renderDashboard(dashboard);
        setDashboardState("content");
    } catch (error) {
        console.error(error);
        setDashboardState("error");
    }
}

function renderDashboard(dashboard) {
    document.querySelector("#dashboardGreeting").textContent = dashboard.greeting || "Hola. Este es tu plan para hoy.";
    document.querySelector("#dashboardDate").textContent = formatDate(dashboard.currentDate);
    document.querySelector("#pendingTasks").textContent = dashboard.pendingTasks ?? 0;
    document.querySelector("#completedToday").textContent = dashboard.completedTasksToday ?? 0;
    document.querySelector("#totalPoints").textContent = dashboard.totalPoints ?? 0;
    document.querySelector("#currentStreak").textContent = dashboard.currentStreak ?? 0;
    document.querySelector("#motivationalMessage").textContent = dashboard.motivationalMessage || "";

    renderMoodSelector(dashboard.todayMood || "NORMAL");
    renderRecommendation(dashboard.recommendedTask);
    renderTodayTasks(dashboard.todayTasks || []);
}

function renderMoodSelector(currentMood) {
    const container = document.querySelector("#moodSelector");
    container.innerHTML = moodOptions.map((mood) => `
        <button class="mood-option ${mood.value === currentMood ? "active" : ""}" type="button" data-mood="${mood.value}" role="radio" aria-checked="${mood.value === currentMood}">
            <strong>${mood.label}</strong>
            <span>${mood.hint}</span>
        </button>
    `).join("");

    container.querySelectorAll(".mood-option").forEach((button) => {
        button.addEventListener("click", async () => {
            await saveMood(button.dataset.mood);
        });
    });
}

async function saveMood(moodType) {
    try {
        await PlaniaApi.saveMood({ moodType });
        PlaniaUI.showToast("Estado de animo guardado.", "success");
        await loadDashboard();
    } catch (error) {
        PlaniaUI.showToast(error.message || "No pudimos guardar tu estado de animo.", "error");
    }
}

function renderRecommendation(recommendation) {
    const container = document.querySelector("#recommendationCard");

    if (!recommendation || !recommendation.task) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>No hay una tarea recomendada</h3>
                <p>Crea tareas pendientes para que Plania pueda sugerirte por donde empezar.</p>
                <a class="button button-primary" href="tasks.html">Crear tarea</a>
            </div>
        `;
        return;
    }

    const task = recommendation.task;
    container.innerHTML = `
        <article class="recommendation-card">
            <div>
                <h3>${escapeHtml(task.title)}</h3>
                <p class="recommendation-reason">${escapeHtml(recommendation.reason)}</p>
            </div>
            <div class="metadata-row">
                ${priorityBadge(task.priority)}
                ${energyBadge(task.energyRequired)}
                <span class="badge">${formatDate(task.dueDate)}</span>
                <span class="badge">${task.estimatedMinutes} min</span>
                <span class="badge">Score ${recommendation.smartScore}</span>
            </div>
            <div class="card-actions">
                <a class="button button-light" href="tasks.html">Empezar</a>
                <button class="button button-secondary" type="button" data-complete="${task.id}">Completar</button>
            </div>
        </article>
    `;

    container.querySelector("[data-complete]")?.addEventListener("click", async (event) => {
        await completeTask(event.currentTarget.dataset.complete);
    });
}

function renderTodayTasks(tasks) {
    const container = document.querySelector("#todayTasksList");

    if (!tasks.length) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>No tienes tareas para hoy</h3>
                <p>Crea tu primera tarea y empieza a organizar tu dia con Plania.</p>
                <a class="button button-primary" href="tasks.html">Nueva tarea</a>
            </div>
        `;
        return;
    }

    container.innerHTML = tasks.map((task) => `
        <article class="task-card">
            <div>
                <h3>${escapeHtml(task.title)}</h3>
                <p>${escapeHtml(task.description || "Sin descripcion.")}</p>
                <div class="task-meta">
                    ${priorityBadge(task.priority)}
                    ${energyBadge(task.energyRequired)}
                    ${task.categoryName ? `<span class="badge">${escapeHtml(task.categoryName)}</span>` : ""}
                    <span class="badge">${task.dueTime ? task.dueTime.substring(0, 5) : "Sin hora"}</span>
                    <span class="badge">${task.estimatedMinutes} min</span>
                </div>
            </div>
            <div class="task-actions">
                ${task.status !== "COMPLETED" ? `<button class="button button-secondary" type="button" data-complete="${task.id}">Completar</button>` : ""}
                ${task.status !== "COMPLETED" ? `<button class="button button-light" type="button" data-postpone="${task.id}">Aplazar</button>` : ""}
                <a class="button button-ghost" href="tasks.html">Editar</a>
            </div>
        </article>
    `).join("");

    container.querySelectorAll("[data-complete]").forEach((button) => {
        button.addEventListener("click", async () => completeTask(button.dataset.complete));
    });

    container.querySelectorAll("[data-postpone]").forEach((button) => {
        button.addEventListener("click", async () => postponeTask(button.dataset.postpone));
    });
}

async function completeTask(taskId) {
    try {
        await PlaniaApi.completeTask(taskId);
        PlaniaUI.showToast("Tarea completada. Sumaste puntos.", "success");
        await loadDashboard();
    } catch (error) {
        PlaniaUI.showToast(error.message || "No pudimos completar la tarea.", "error");
    }
}

async function postponeTask(taskId) {
    try {
        await PlaniaApi.postponeTask(taskId);
        PlaniaUI.showToast("Tarea aplazada para manana.", "success");
        await loadDashboard();
    } catch (error) {
        PlaniaUI.showToast(error.message || "No pudimos aplazar la tarea.", "error");
    }
}

function setDashboardState(state) {
    document.querySelector("#dashboardLoading").classList.toggle("hidden", state !== "loading");
    document.querySelector("#dashboardError").classList.toggle("hidden", state !== "error");
    document.querySelector("#dashboardContent").classList.toggle("hidden", state !== "content");
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
        weekday: "long",
        day: "numeric",
        month: "long",
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
