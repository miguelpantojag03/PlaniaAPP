# Plania

Plania es una agenda inteligente personalizada. La aplicacion no solo guarda tareas: tambien ayudara al usuario a decidir que hacer primero segun prioridad, fecha limite, energia requerida, estado de animo y habitos.

## Fase Actual

Fase 6: dashboard diario con saludo, fecha, estado de animo, conteos de tareas, puntos, racha, recomendacion y tareas del dia.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Jakarta Validation
- PostgreSQL
- Maven

## Estructura

```text
backend/
  src/main/java/com/plania/
    config/
    controller/
    dto/
    exception/
    mapper/
    model/
    repository/
    security/
    service/
  src/main/resources/
    application.properties
    db/init.sql
```

## Ejecutar El Backend

1. Crear una base de datos PostgreSQL llamada `plania_db`.
2. Revisar usuario y contrasena en `backend/src/main/resources/application.properties`.
3. Entrar a la carpeta `backend`.
4. Ejecutar:

```bash
mvn spring-boot:run
```

Para crear un usuario demo y categorias iniciales, ejecutar con perfil `dev`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Usuario demo:

- Email: `demo@plania.com`
- Password: `demo12345`

## Probar Autenticacion

Registrar usuario:

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```

```json
{
  "name": "Juan Perez",
  "email": "juan@plania.com",
  "password": "password123"
}
```

Iniciar sesion:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

```json
{
  "email": "juan@plania.com",
  "password": "password123"
}
```

La respuesta incluye un token. Para endpoints protegidos se usa:

```text
Authorization: Bearer TOKEN_AQUI
```

Consultar usuario actual:

```http
GET http://localhost:8080/api/users/me
Authorization: Bearer TOKEN_AQUI
```

Actualizar usuario actual:

```http
PUT http://localhost:8080/api/users/me
Content-Type: application/json
Authorization: Bearer TOKEN_AQUI
```

```json
{
  "name": "Juan Perez Actualizado",
  "email": "juan.actualizado@plania.com"
}
```

## Probar CRUD De Tareas

Crear tarea:

```http
POST http://localhost:8080/api/tasks
Content-Type: application/json
Authorization: Bearer TOKEN_AQUI
```

```json
{
  "title": "Estudiar para el parcial de calculo",
  "description": "Repasar limites, derivadas y ejercicios del taller",
  "dueDate": "2026-06-07",
  "dueTime": "18:00:00",
  "priority": "HIGH",
  "energyRequired": "MEDIUM",
  "estimatedMinutes": 90,
  "categoryId": 1
}
```

Endpoints de autenticacion:

```text
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
```

Endpoints de usuario:

```text
GET    /api/users/me
PUT    /api/users/me
```

Endpoints de tareas:

```text
GET    /api/tasks
GET    /api/tasks/today
GET    /api/tasks/pending
GET    /api/tasks/completed
GET    /api/tasks/{id}
POST   /api/tasks
PUT    /api/tasks/{id}
DELETE /api/tasks/{id}
PATCH  /api/tasks/{id}/complete
PATCH  /api/tasks/{id}/postpone
```

## Probar Estado De Animo

Registrar o actualizar el estado de animo del dia:

```http
POST http://localhost:8080/api/moods
Content-Type: application/json
Authorization: Bearer TOKEN_AQUI
```

```json
{
  "moodType": "TIRED",
  "note": "Dormí poco, pero quiero avanzar con tareas pequeñas"
}
```

Tambien puedes registrar un dia especifico que no sea futuro:

```json
{
  "moodType": "ENERGETIC",
  "note": "Buen dia para tareas pesadas",
  "date": "2026-06-06"
}
```

Consultar estado de animo de hoy:

```http
GET http://localhost:8080/api/moods/today
Authorization: Bearer TOKEN_AQUI
```

Consultar historial:

```http
GET http://localhost:8080/api/moods/history
Authorization: Bearer TOKEN_AQUI
```

Valores permitidos para `moodType`:

```text
ENERGETIC
NORMAL
TIRED
STRESSED
UNMOTIVATED
```

## Probar Recomendaciones

Obtener lista de tareas activas ordenadas por recomendacion:

```http
GET http://localhost:8080/api/recommendations/today
Authorization: Bearer TOKEN_AQUI
```

Obtener la mejor tarea para hacer primero:

```http
GET http://localhost:8080/api/recommendations/best-task
Authorization: Bearer TOKEN_AQUI
```

El algoritmo calcula `smartScore` para tareas `PENDING` y `POSTPONED`.

Reglas principales:

```text
Tarea vencida: +50
Vence hoy: +40
Vence manana: +30
Vence en menos de 3 dias: +20
Prioridad urgente: +40
Prioridad alta: +30
Prioridad media: +15
Prioridad baja: +5
Aplazada mas de 2 veces: +15
Usuario cansado/estresado/sin ganas + baja energia: +10
Usuario cansado/estresado + alta energia: -10
Usuario con energia + alta energia: +10
Dura menos de 30 minutos: +5
Dura mas de 120 minutos y usuario cansado: -10
```

Si el usuario no registro estado de animo hoy, Plania usa `NORMAL`.

## Probar Dashboard

Obtener resumen diario:

```http
GET http://localhost:8080/api/dashboard/today
Authorization: Bearer TOKEN_AQUI
```

La respuesta incluye:

```text
greeting
currentDate
todayMood
pendingTasks
completedTasksToday
completedTasksTotal
totalPoints
currentStreak
recommendedTask
todayTasks
motivationalMessage
```

El dashboard reutiliza el algoritmo de recomendacion. Si no hay tareas pendientes o aplazadas, `recommendedTask` devuelve `null`.

## Frontend

Fase 6 del frontend: estadisticas y perfil con metricas calculadas desde tareas, historial de animo y datos del usuario.

Archivos creados:

```text
frontend/
  assets/
    icons/
    images/
      plania-hero.png
  css/
    auth.css
    dashboard.css
    profile.css
    stats.css
    tasks.css
    styles.css
    responsive.css
  js/
    api.js
    auth.js
    dashboard.js
    profile.js
    session.js
    stats.js
    tasks.js
    storage.js
    ui.js
  index.html
  dashboard.html
  login.html
  profile.html
  register.html
  stats.html
  tasks.html
```

Para abrir la portada, usa el archivo:

```text
frontend/index.html
```

Tambien puedes usar la extension Live Server de VS Code sobre la carpeta `frontend`.

Pantallas de autenticacion:

```text
frontend/login.html
frontend/register.html
```

El frontend espera el backend en:

```text
http://localhost:8080/api
```

Cuando el login o registro es exitoso, guarda:

```text
plania_token
plania_user
```

en `localStorage` y redirige a `dashboard.html`, que se construira en la siguiente fase del frontend.

Dashboard:

```text
frontend/dashboard.html
```

Consume estos endpoints:

```text
GET   /api/dashboard/today
POST  /api/moods
PATCH /api/tasks/{id}/complete
PATCH /api/tasks/{id}/postpone
```

El dashboard requiere token JWT en `localStorage`. Si no hay sesion, redirige a `login.html`.

Gestion de tareas:

```text
frontend/tasks.html
```

Consume estos endpoints:

```text
GET    /api/tasks
POST   /api/tasks
PUT    /api/tasks/{id}
DELETE /api/tasks/{id}
PATCH  /api/tasks/{id}/complete
PATCH  /api/tasks/{id}/postpone
```

Incluye buscador, filtros por estado, prioridad, categoria y fecha, modal de crear/editar tarea, confirmacion de eliminacion y boton flotante en movil.

API y sesion:

```text
frontend/js/api.js
frontend/js/storage.js
frontend/js/session.js
```

`api.js` centraliza las peticiones a `http://localhost:8080/api`, agrega automaticamente `Authorization: Bearer TOKEN`, maneja errores de red, respuestas sin JSON y errores HTTP.

`storage.js` guarda y lee `plania_token` y `plania_user` desde `localStorage`.

`session.js` protege paginas privadas, redirige si no hay token, limpia la sesion si el backend responde `401` y reutiliza el logout.

Estadisticas y perfil:

```text
frontend/stats.html
frontend/profile.html
```

`stats.html` calcula tareas completadas hoy, completadas en la semana, puntos, racha, promedio diario, categoria mas trabajada, estado de animo mas frecuente y distribucion por estado.

`profile.html` muestra nombre, correo, fecha de creacion, puntos, racha, permite editar nombre/correo y cerrar sesion.
