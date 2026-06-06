# Plania

Plania es una agenda inteligente personalizada. No solo guarda tareas: ayuda al usuario a decidir que hacer primero segun prioridad, fecha limite, energia requerida, estado de animo, tiempo estimado y aplazamientos.

Frase principal:

```text
Plania no solo te recuerda tus tareas, te ayuda a decidir que hacer primero.
```

## Estado Del Proyecto

Backend y frontend base integrados.

- Backend Spring Boot con API REST, JPA, PostgreSQL, JWT, tareas, moods, recomendaciones y dashboard.
- Frontend HTML, CSS y JavaScript puro con portada, autenticacion, dashboard, tareas, estadisticas, perfil, responsive y manejo de sesion.

Repositorio:

```text
https://github.com/miguelpantojag03/PlaniaAPP.git
```

## Tecnologias

Backend:

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Jakarta Validation
- PostgreSQL
- Maven
- JWT

Frontend:

- HTML5
- CSS3 moderno
- JavaScript puro
- LocalStorage
- Fetch API
- Responsive design

## Estructura

```text
Plania/
  backend/
    pom.xml
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

  frontend/
    assets/images/plania-hero.png
    css/
      auth.css
      dashboard.css
      profile.css
      responsive.css
      stats.css
      styles.css
      tasks.css
    js/
      api.js
      auth.js
      dashboard.js
      profile.js
      session.js
      stats.js
      storage.js
      tasks.js
      ui.js
    index.html
    login.html
    register.html
    dashboard.html
    tasks.html
    stats.html
    profile.html
```

## Ejecutar Backend

1. Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE plania_db;
```

2. Revisar credenciales en:

```text
backend/src/main/resources/application.properties
```

Configuracion actual:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/plania_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Ejecutar backend:

```bash
cd C:\Users\migue\Desktop\Plania\backend
mvn spring-boot:run
```

Con usuario demo y categorias iniciales:

```bash
cd C:\Users\migue\Desktop\Plania\backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Usuario demo:

```text
Email: demo@plania.com
Password: demo12345
```

## Ejecutar Frontend

En otra terminal:

```bash
cd C:\Users\migue\Desktop\Plania\frontend
python -m http.server 5500 --bind 127.0.0.1
```

Abrir:

```text
http://127.0.0.1:5500/
```

Pantallas:

```text
http://127.0.0.1:5500/index.html
http://127.0.0.1:5500/login.html
http://127.0.0.1:5500/register.html
http://127.0.0.1:5500/dashboard.html
http://127.0.0.1:5500/tasks.html
http://127.0.0.1:5500/stats.html
http://127.0.0.1:5500/profile.html
```

## Flujo De Prueba Recomendado

1. Iniciar PostgreSQL.
2. Crear `plania_db`.
3. Ejecutar backend con perfil `dev`.
4. Ejecutar frontend en puerto `5500`.
5. Abrir `login.html`.
6. Iniciar sesion con `demo@plania.com` y `demo12345`, o crear cuenta.
7. Crear tareas desde `tasks.html`.
8. Registrar estado de animo desde `dashboard.html`.
9. Revisar recomendacion inteligente.
10. Completar o aplazar tareas.
11. Revisar estadisticas y perfil.

## API Principal

Auth:

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
```

Users:

```text
GET /api/users/me
PUT /api/users/me
```

Tasks:

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

Mood:

```text
POST /api/moods
GET  /api/moods/today
GET  /api/moods/history
```

Recommendations:

```text
GET /api/recommendations/today
GET /api/recommendations/best-task
```

Dashboard:

```text
GET /api/dashboard
GET /api/dashboard/today
```

## Autenticacion

El backend usa JWT. Al iniciar sesion o registrarse, el frontend guarda:

```text
plania_token
plania_user
```

en `localStorage`.

Cada peticion protegida envia:

```text
Authorization: Bearer TOKEN
```

Si el backend responde `401`, el frontend limpia la sesion y redirige a `login.html`.

## Algoritmo De Recomendacion

Plania calcula un `smartScore` para tareas `PENDING` y `POSTPONED`.

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

Si no hay mood registrado hoy, Plania usa `NORMAL`.

## Frontend

El frontend incluye:

- Portada moderna.
- Login y registro.
- Dashboard diario.
- Selector de estado de animo.
- Recomendacion inteligente.
- Gestion de tareas.
- Buscador y filtros.
- Modales para crear, editar y eliminar tareas.
- Estadisticas.
- Perfil editable.
- Toast notifications.
- Loading, error y empty states.
- Sidebar en escritorio.
- Bottom navigation en movil.
- Boton flotante movil.

## Validaciones

Backend:

- DTOs con Jakarta Validation.
- Emails validos.
- Password minimo.
- Fechas y campos obligatorios.
- Tareas asociadas al usuario autenticado.

Frontend:

- Login y registro.
- Crear y editar tareas.
- Editar perfil.
- Mensajes visuales por campo.
- Manejo de errores del backend.

## Errores Comunes

PostgreSQL no conecta:

- Verifica que PostgreSQL este iniciado.
- Verifica que exista `plania_db`.
- Revisa usuario y password en `application.properties`.

Login falla:

- Verifica que el backend este corriendo en `http://localhost:8080`.
- Verifica que el usuario exista.
- Si usas usuario demo, ejecuta el backend con perfil `dev`.

Frontend muestra error al cargar dashboard:

- Verifica que haya token en `localStorage`.
- Verifica que el backend este activo.
- Vuelve a iniciar sesion.

El navegador bloquea peticiones:

- Usa el servidor local del frontend en `http://127.0.0.1:5500`.
- El backend tiene CORS preparado para `localhost:5500` y `127.0.0.1:5500`.

## Verificaciones

Backend:

```bash
cd C:\Users\migue\Desktop\Plania\backend
mvn test
```

Frontend:

```bash
cd C:\Users\migue\Desktop\Plania
node --check frontend\js\api.js
node --check frontend\js\auth.js
node --check frontend\js\dashboard.js
node --check frontend\js\tasks.js
node --check frontend\js\stats.js
node --check frontend\js\profile.js
node --check frontend\js\session.js
node --check frontend\js\storage.js
node --check frontend\js\ui.js
```

## Mejoras Futuras

- CRUD completo de categorias en frontend.
- Subtareas y modo anti-procrastinacion completo en frontend.
- Gamificacion mas avanzada.
- Graficos reales para estadisticas.
- Tests automatizados backend y frontend.
- Despliegue en la nube.
- Refresh tokens o blacklist para logout JWT avanzado.
