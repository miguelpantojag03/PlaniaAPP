# Resumen Rapido: Patrones Y Estructuras

## Estructuras De Datos

| Estructura | Donde se usa | Para que sirve |
| --- | --- | --- |
| `List` | tareas, moods, categorias, recomendaciones | Manejar colecciones ordenadas |
| `Optional` | busquedas por id/email | Evitar null y manejar datos inexistentes |
| `Map` | errores de validacion | Relacionar campo con mensaje |
| `Enum` | prioridad, energia, estado, mood | Controlar valores permitidos |
| `LocalDate` | fechas limite, mood, racha | Manejar fechas sin hora |
| `LocalTime` | hora limite | Manejar horas |
| `LocalDateTime` | createdAt, updatedAt, completedAt | Manejar fecha y hora |

## Patrones Implementados

| Patron | Archivo ejemplo | Explicacion |
| --- | --- | --- |
| Layered Architecture | paquetes `controller`, `service`, `repository` | Separa responsabilidades |
| Repository | `TaskRepository` | Abstrae acceso a datos |
| DTO | `TaskRequest`, `TaskResponse` | Controla entrada/salida JSON |
| Mapper | `TaskMapper` | Convierte entidades y DTOs |
| Dependency Injection | constructores de services | Spring inyecta dependencias |
| Filter | `JwtAuthenticationFilter` | Valida token JWT |
| Global Exception Handler | `GlobalExceptionHandler` | Centraliza errores JSON |

## Patrones No Implementados

| Patron | Estado | Como agregarlo |
| --- | --- | --- |
| Strategy | No implementado | Dividir reglas de `smartScore` en clases |
| Factory | No implementado | Crear categorias/mensajes desde una fabrica |
| Observer | No implementado | Eventos para notificaciones o rachas |

## Frase Para Defensa

```text
Plania aplica arquitectura por capas y patrones comunes de aplicaciones empresariales con Spring Boot. La logica de negocio esta en services, el acceso a datos en repositories, la comunicacion en DTOs, la seguridad en filtros JWT y los errores en un handler global.
```
