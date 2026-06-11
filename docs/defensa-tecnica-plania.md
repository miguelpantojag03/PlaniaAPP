# Defensa Tecnica De Plania

Este documento resume como explicar Plania en una presentacion universitaria. La idea es defender el proyecto desde tres angulos: arquitectura, estructuras de datos y patrones de diseno realmente usados.

## 1. Descripcion Corta Del Proyecto

Plania es una agenda inteligente personalizada. Permite registrar usuarios, iniciar sesion, crear tareas, registrar estado de animo, calcular recomendaciones y mostrar un dashboard diario.

La diferencia frente a una agenda comun es que Plania no solo guarda tareas. Tambien calcula un `smartScore` para recomendar que tarea conviene hacer primero segun prioridad, fecha limite, energia requerida, duracion, estado de animo y aplazamientos.

## 2. Arquitectura General

Plania usa arquitectura cliente-servidor:

```text
Frontend HTML/CSS/JS
        |
        | HTTP + JSON + JWT
        v
Backend Spring Boot REST API
        |
        | JPA / Hibernate
        v
PostgreSQL
```

El frontend se encarga de la interfaz, validaciones basicas, consumo de API y manejo del token.

El backend se encarga de reglas de negocio, seguridad, persistencia, validaciones fuertes y respuestas REST.

La base de datos guarda usuarios, tareas, categorias, moods, subtareas y logros.

## 3. Arquitectura Por Capas En Backend

El backend esta organizado por paquetes:

```text
controller
service
repository
model
dto
mapper
config
security
exception
```

### controller

Expone los endpoints REST. Recibe peticiones HTTP y devuelve respuestas JSON.

Ejemplos:

- `AuthController`
- `TaskController`
- `DashboardController`
- `MoodController`
- `RecommendationController`
- `UserController`

### service

Contiene la logica de negocio.

Ejemplos:

- `TaskService`: crear, editar, completar, aplazar y eliminar tareas.
- `AuthService`: registro y login.
- `RecommendationService`: calculo del `smartScore`.
- `DashboardService`: resumen diario.

### repository

Acceso a datos usando Spring Data JPA.

Ejemplos:

- `UserRepository`
- `TaskRepository`
- `MoodRepository`
- `CategoryRepository`

### model

Entidades JPA que representan tablas de base de datos.

Ejemplos:

- `User`
- `Task`
- `Category`
- `Mood`
- `SubTask`

### dto

Objetos para entrada y salida de datos. Evitan exponer entidades directamente.

Ejemplos:

- `LoginRequest`
- `RegisterRequest`
- `AuthResponse`
- `TaskRequest`
- `TaskResponse`
- `DashboardResponse`

### mapper

Convierte entidades a DTOs y DTOs a entidades.

Ejemplos:

- `TaskMapper`
- `MoodMapper`
- `UserMapper`

### security

Maneja autenticacion y autorizacion con JWT.

Ejemplos:

- `JwtService`
- `JwtAuthenticationFilter`
- `CustomUserDetailsService`
- `RestAuthenticationEntryPoint`

### exception

Centraliza errores y respuestas JSON.

Ejemplos:

- `GlobalExceptionHandler`
- `ResourceNotFoundException`
- `BadRequestException`

## 4. Estructuras De Datos Usadas

### List

Se usa `List` para manejar colecciones ordenadas.

Ejemplos:

```java
List<TaskResponse>
List<Category>
List<MoodResponse>
```

Uso en Plania:

- Listar tareas.
- Listar moods.
- Listar categorias iniciales.
- Devolver recomendaciones ordenadas.

### Optional

Se usa `Optional` para manejar resultados que pueden no existir.

Ejemplo:

```java
Optional<User> findByEmail(String email);
Optional<Task> findByIdAndUserId(Long id, Long userId);
```

Uso en Plania:

- Buscar usuario por correo.
- Buscar tarea solo si pertenece al usuario autenticado.
- Evitar `null` y lanzar errores claros cuando no existe un recurso.

### Map

Se usa `Map` para manejar errores de validacion por campo.

Ejemplo:

```java
Map<String, String> validationErrors
```

Uso en Plania:

- Devolver errores como:

```json
{
  "validationErrors": {
    "email": "Email must be valid",
    "password": "Password is required"
  }
}
```

### Enum

Se usan enums para valores fijos y controlados.

Ejemplos:

```java
Priority
EnergyRequired
TaskStatus
MoodType
```

Ventaja:

- Evita strings inconsistentes.
- Facilita validaciones.
- Hace mas claro el dominio.

### LocalDate, LocalTime, LocalDateTime

Se usan clases modernas de fecha y hora.

Uso:

- `LocalDate`: fecha limite, fecha de mood, racha.
- `LocalTime`: hora limite opcional.
- `LocalDateTime`: createdAt, updatedAt, completedAt.

## 5. Patrones Aplicados Realmente

### 5.1 MVC / REST Controller

Spring Boot usa una forma de MVC adaptada a APIs REST.

En Plania:

- Controller recibe la peticion.
- Service procesa la logica.
- Repository consulta la base de datos.
- DTO devuelve JSON.

Ejemplo:

```text
TaskController -> TaskService -> TaskRepository -> PostgreSQL
```

### 5.2 Layered Architecture

Plania separa responsabilidades por capas.

Beneficio:

- El controlador no contiene logica de negocio.
- El servicio no sabe detalles HTTP.
- El repositorio solo consulta datos.
- El codigo es mas mantenible.

### 5.3 Repository Pattern

Spring Data JPA implementa el patron Repository.

Ejemplo:

```java
public interface TaskRepository extends JpaRepository<Task, Long>
```

Beneficio:

- Aisla el acceso a base de datos.
- Evita SQL manual para consultas comunes.
- Permite metodos como `findByUserIdAndStatus`.

### 5.4 DTO Pattern

Se usan DTOs para no exponer entidades.

Ejemplo:

```java
TaskRequest
TaskResponse
```

Beneficio:

- El frontend recibe solo lo necesario.
- No se expone `password`.
- Se controlan nombres y estructura del JSON.

### 5.5 Mapper Pattern

Los mappers convierten entre entidades y DTOs.

Ejemplo:

```java
TaskMapper.toResponse(task)
```

Beneficio:

- Evita repetir conversiones.
- Mantiene controladores y servicios mas limpios.

### 5.6 Dependency Injection

Spring inyecta dependencias por constructor.

Ejemplo:

```java
public TaskService(TaskRepository taskRepository, UserRepository userRepository)
```

Beneficio:

- Codigo desacoplado.
- Mas facil de probar.
- Spring administra los objetos.

### 5.7 Filter Pattern

JWT usa un filtro de seguridad:

```java
JwtAuthenticationFilter
```

Funcion:

- Lee el header `Authorization`.
- Valida el token.
- Autentica al usuario en Spring Security.

### 5.8 Global Exception Handler

Se centraliza el manejo de errores con:

```java
@RestControllerAdvice
```

Beneficio:

- Respuestas JSON consistentes.
- Menos `try/catch` repetidos.
- Errores mas claros para el frontend.

## 6. Patrones Que No Estan Implementados

Estos patrones no estan implementados actualmente. Si los preguntan, se puede explicar asi:

### Strategy Pattern

No esta implementado formalmente.

Donde podria aplicarse:

El algoritmo de recomendacion podria dividirse en estrategias:

```text
DueDateScoreStrategy
PriorityScoreStrategy
MoodEnergyScoreStrategy
PostponedScoreStrategy
```

Version sencilla:

Crear una interfaz:

```java
public interface ScoreRule {
    int calculate(Task task, MoodType moodType);
}
```

Luego cada regla suma puntos. Esto haria el algoritmo mas extensible.

### Factory Pattern

No esta implementado.

Donde podria aplicarse:

Creacion de categorias iniciales o mensajes motivacionales.

Actualmente no hace falta porque la logica es simple.

### Observer Pattern

No esta implementado.

Donde podria aplicarse:

Notificaciones cuando una tarea vence o cuando una racha cambia.

Actualmente Plania no tiene sistema de eventos ni notificaciones en tiempo real.

## 7. Modelo De Datos

Relaciones principales:

```text
User 1 --- N Task
User 1 --- N Category
User 1 --- N Mood
Category 1 --- N Task
Task 1 --- N SubTask
User 1 --- N Achievement
```

Explicacion:

- Un usuario tiene muchas tareas.
- Una tarea pertenece a un usuario.
- Una tarea puede tener una categoria.
- Un usuario registra estados de animo.
- Una tarea puede tener subtareas.

## 8. Seguridad

Plania usa JWT:

1. El usuario hace login.
2. El backend valida email y password.
3. El backend genera un token.
4. El frontend guarda el token en `localStorage`.
5. Cada peticion protegida envia:

```text
Authorization: Bearer TOKEN
```

6. El filtro JWT valida el token.
7. El backend obtiene el usuario autenticado.

Esto impide que un usuario consulte tareas de otro usuario, porque los servicios siempre filtran por `userId`.

## 9. Recomendacion Inteligente

El servicio principal es:

```text
RecommendationService
```

Calcula un `smartScore`.

Factores:

- Tarea vencida.
- Tarea vence hoy.
- Prioridad.
- Energia requerida.
- Estado de animo.
- Duracion estimada.
- Veces aplazada.

La tarea con mayor puntaje se muestra como recomendacion.

## 10. Flujo Principal De Uso

```text
Usuario se registra
Usuario inicia sesion
Frontend guarda JWT
Usuario crea tareas
Usuario registra mood
Dashboard carga resumen
RecommendationService calcula smartScore
Usuario completa tarea
TaskService suma puntos y actualiza racha
Dashboard se actualiza
```

## 11. Como Defender La Arquitectura En Clase

Respuesta sugerida:

```text
Plania usa una arquitectura por capas sobre Spring Boot. El frontend consume una API REST protegida con JWT. El backend separa controladores, servicios, repositorios, entidades, DTOs y mappers. Esto permite mantener responsabilidades claras: los controllers exponen rutas, los services contienen reglas de negocio, los repositories acceden a datos, y los DTOs controlan la comunicacion con el frontend.
```

## 12. Que Esta Completo

- Registro.
- Login.
- JWT.
- Proteccion de rutas.
- CRUD de tareas.
- Mood diario.
- Recomendacion inteligente.
- Dashboard.
- Puntos basicos.
- Racha basica.
- Frontend responsive.
- Perfil.
- Estadisticas.

## 13. Que Esta Incompleto O Para Mejorar

- CRUD completo de categorias en frontend.
- Subtareas en frontend.
- Achievements reales.
- Tests automatizados.
- Graficos reales.
- Refresh token.
- Despliegue.
- Notificaciones.

## 14. Mejoras Simples Para Agregar

### CRUD De Categorias

Agregar:

```text
CategoryController
CategoryService
Category DTOs
categories.html
categories.js
```

### Subtareas

Agregar frontend para endpoints ya planeados:

```text
GET /api/tasks/{taskId}/subtasks
POST /api/tasks/{taskId}/subtasks
PATCH /api/subtasks/{id}/complete
DELETE /api/subtasks/{id}
```

### Strategy Para Recomendacion

Extraer cada regla del `smartScore` a una clase separada.

Esto seria una mejora elegante para explicar patrones de diseno.

## 15. Resumen Final Para Exponer

Plania es un sistema full stack con arquitectura por capas, API REST, seguridad JWT, persistencia con JPA y PostgreSQL, frontend responsive y un algoritmo interno de recomendacion. Usa estructuras de datos como `List`, `Map`, `Optional`, enums y clases de fecha modernas. Aplica patrones reales como Repository, DTO, Mapper, Dependency Injection, Filter y Global Exception Handler.

No se debe afirmar que usa Strategy, Factory u Observer actualmente. Esos son patrones posibles para mejorar, pero no forman parte de la implementacion actual.
