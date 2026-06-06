document.addEventListener("DOMContentLoaded", () => {
    if (!PlaniaSession.requireAuth()) return;
    PlaniaSession.bindLogoutButtons();
    document.querySelector("#retryStats")?.addEventListener("click", loadStats);
    loadStats();
});

async function loadStats() {
    setStatsState("loading");
    try {
        const [user, tasks, moods] = await Promise.all([
            PlaniaApi.getCurrentUser(),
            PlaniaApi.getTasks(),
            PlaniaApi.getMoodHistory()
        ]);
        renderStats(user, tasks || [], moods || []);
        setStatsState("content");
    } catch (error) {
        console.error(error);
        setStatsState("error");
    }
}

function renderStats(user, tasks, moods) {
    const today = new Date();
    const weekStart = new Date(today);
    weekStart.setDate(today.getDate() - 6);

    const completedTasks = tasks.filter((task) => task.status === "COMPLETED");
    const completedToday = completedTasks.filter((task) => isSameDay(task.completedAt, today)).length;
    const completedWeek = completedTasks.filter((task) => {
        if (!task.completedAt) return false;
        const completedAt = new Date(task.completedAt);
        return completedAt >= startOfDay(weekStart) && completedAt <= endOfDay(today);
    }).length;

    document.querySelector("#completedToday").textContent = completedToday;
    document.querySelector("#completedWeek").textContent = completedWeek;
    document.querySelector("#totalPoints").textContent = user.totalPoints ?? 0;
    document.querySelector("#currentStreak").textContent = user.currentStreak ?? 0;
    document.querySelector("#averageCompleted").textContent = (completedWeek / 7).toFixed(1);
    document.querySelector("#topCategory").textContent = topValue(completedTasks.map((task) => task.categoryName).filter(Boolean)) || "Sin datos";
    document.querySelector("#topMood").textContent = moodLabel(topValue(moods.map((mood) => mood.moodType).filter(Boolean))) || "Sin datos";

    renderStatusBars(tasks);
}

function renderStatusBars(tasks) {
    const container = document.querySelector("#statusBars");
    const total = Math.max(tasks.length, 1);
    const statuses = [
        ["PENDING", "Pendientes"],
        ["POSTPONED", "Aplazadas"],
        ["COMPLETED", "Completadas"],
        ["CANCELLED", "Canceladas"]
    ];

    container.innerHTML = statuses.map(([status, label]) => {
        const count = tasks.filter((task) => task.status === status).length;
        const width = Math.round((count / total) * 100);
        return `
            <div class="bar-row">
                <div class="bar-label"><span>${label}</span><span>${count}</span></div>
                <div class="bar-track"><div class="bar-fill" style="width: ${width}%"></div></div>
            </div>
        `;
    }).join("");
}

function topValue(values) {
    const counts = values.reduce((acc, value) => {
        acc[value] = (acc[value] || 0) + 1;
        return acc;
    }, {});

    return Object.entries(counts).sort((a, b) => b[1] - a[1])[0]?.[0] || null;
}

function moodLabel(mood) {
    return {
        ENERGETIC: "Con energia",
        NORMAL: "Normal",
        TIRED: "Cansado",
        STRESSED: "Estresado",
        UNMOTIVATED: "Sin ganas"
    }[mood] || mood;
}

function isSameDay(value, date) {
    if (!value) return false;
    const parsed = new Date(value);
    return parsed.getFullYear() === date.getFullYear()
        && parsed.getMonth() === date.getMonth()
        && parsed.getDate() === date.getDate();
}

function startOfDay(date) {
    return new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0, 0);
}

function endOfDay(date) {
    return new Date(date.getFullYear(), date.getMonth(), date.getDate(), 23, 59, 59, 999);
}

function setStatsState(state) {
    document.querySelector("#statsLoading").classList.toggle("hidden", state !== "loading");
    document.querySelector("#statsError").classList.toggle("hidden", state !== "error");
    document.querySelector("#statsContent").classList.toggle("hidden", state !== "content");
}
