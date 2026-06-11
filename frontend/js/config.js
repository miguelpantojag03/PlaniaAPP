const isLocalPlaniaHost = ["localhost", "127.0.0.1"].includes(window.location.hostname);

window.PLANIA_API_BASE_URL = window.PLANIA_API_BASE_URL
    || (isLocalPlaniaHost ? "http://localhost:8080/api" : "https://plania-backend.onrender.com/api");
