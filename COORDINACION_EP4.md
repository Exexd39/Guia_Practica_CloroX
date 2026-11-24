# Coordinación EP4 · CloroApp (exe + Nico)

> **Fecha límite: viernes.** Este archivo define quién toca qué para que no nos choquemos.
> Léelo antes de tocar cualquier cosa y mantenlo actualizado si algo cambia.

---

## 1. Reparto de tareas (según contrato §6)

### 👤 exe (Exequiel)
| Área | Detalle |
|---|---|
| `medicion-service/` | Todo el microservicio: granjas, puntos, mediciones, alertas, historial (puerto 8082) |
| Migración de la app a la API | `RetrofitClient`, `CloroApiService`, `Repositorio` → apuntar a `http://10.0.2.2:8082/api/` |
| Refactor + tests de la app | `Repositorio` (inyección de dependencias), `Validaciones`, `RangoCloro` |
| Cobertura | JaCoCo en el módulo `app` |
| APK firmado | `.jks` + `keystore.properties` (ambos van al repo, ya está en `.gitignore` revertido) |
| README / Trello | Compartido: solo añadir secciones, no reescribir |

### 👤 Nico (Nicolás)
| Área | Detalle |
|---|---|
| `usuario-service/` | Todo el microservicio: usuarios, login, roles, permisos, granjas asignadas (puerto 8081) |
| Pantallas nuevas | Usuarios admin, puntos (CRUD completo), configuración |
| API externa del clima | Open-Meteo (o la que se decida) para la pantalla de clima |
| Tests de ViewModel | `LoginViewModel`, `DashboardViewModel`, `HistorialViewModel`, etc. |

---

## 2. Reglas de oro anti-choque

1. **Cada uno solo toca SU carpeta de servicio**: `usuario-service/` es de Nico, `medicion-service/` es de exe.
   Si necesitas algo del otro, **se lo pides**, no lo edites.
2. **Archivos compartidos de la app** (ver tabla §3): solo se *añaden* funciones/DTOs propios.
   **Nunca** reordenar, borrar ni reescribir lo que puso el otro.
3. **No se hace commit directo en `main`**. Cada uno en su rama (ver §4) y se integra avisando.
4. Si un archivo compartido te resulta incómoda, **dilo y lo separamos** antes de que haya conflicto.
5. **Commits chicos y seguidos** (1 por feature terminada) — así los conflictos se resuelven rápido.
6. Antes de `git pull`, avísame/avísale al otro que vas a integrar.

---

## 3. Archivos compartidos: quién pone qué

| Archivo | exe pone... | Nico pone... |
|---|---|---|
| `app/.../data/network/RetrofitClient.kt` | `BASE_URL` a `:8082` (mediciones) | — |
| `app/.../data/network/CloroApiService.kt` | endpoints de mediciones/alertas | endpoints de login/usuarios |
| `app/.../data/network/Dtos.kt` | DTOs de medición/granja/alerta | DTOs de usuario/login |
| `app/.../ui/navigation/AppNavigation.kt` | rutas de mediciones/historial | rutas de usuarios/puntos/clima |
| `app/build.gradle` y `gradle/libs.versions.toml` | dependencias de testing/cobertura | dependencias de las pantallas nuevas |
| `app/src/main/AndroidManifest.xml` | permisos (cámara si aplica) | permisos (internet/clima si aplica) |
| `README.md`, Trello | secciones de backend/tests | secciones de pantallas/clima |

> **Regla de convención en archivos compartidos:** cada uno agrega lo suyo **al final de su sección**
> (o en un bloque claramente marcado con un comentario `// --- exe ---` / `// --- Nico ---`).
> Si ves algo que no entiendes o que parece del otro, **no lo toques, pregunta**.

---

## 4. Ramas y commits

```
main                    ← siempre estable, se integra avisando
├── feat/usuario-svc    ← Nico: usuario-service
├── feat/medicion-svc   ← exe: medicion-service
├── feat/pantallas-admin← Nico: pantallas de usuarios/puntos
└── feat/migracion-api  ← exe: cambio de la app a los microservicios
```

- **Formato de commit:** `tipo: descripción corta`
  Ejemplos: `feat: login con JWT en usuario-service`, `fix: BASE_URL a 10.0.2.2`, `test: validaciones de RangoCloro`
- Integración: `git pull origin main` en tu rama → resolver conflicto → avisar → merge a `main`.
- Si un conflicto es en un archivo compartido y no estás seguro, **no lo resuelvas a la fuerza**: avísame.

---

## 5. Decisiones ya tomadas (§11 del contrato)

- **Lenguaje de los servicios:** Java (Spring Boot 3.5.6 + Maven, igual que Omega-Titanio).
- **Bases de datos:** MySQL local, una por servicio:
  - `cloro_usuario` → usuario-service (:8081, usuario `omega`/`omega123`)
  - `cloro_medicion` → medicion-service (:8082, usuario `omega`/`omega123`)
  - Script: `crear-bases.sql` en la raíz (ejecutarlo antes del primer arranque).
- **Formato de datos:** JSON camelCase, enums en mayúsculas, fechas `"yyyy-MM-dd HH:mm:ss"`
  (zona `America/Santiago`), IDs de texto, errores `{"detail": "..."}`.
- **Tests:** JUnit 5 (el que trae `spring-boot-starter-test`).

---

## 6. Puntos de integración entre servicios

- **medicion-service → usuario-service:** `GET http://localhost:8081/api/usuarios/{id}`
  (para validar `X-Usuario-Id` en operaciones de administrador). Nico expone ese endpoint,
  exe lo consume. **Ese contrato no se cambia sin avisar.**
- **App → servicios:** desde el emulador, `10.0.2.2` equivale a `localhost` de la PC:
  - login/usuarios → `http://10.0.2.2:8081/api/`
  - mediciones/alertas → `http://10.0.2.2:8082/api/`

---

## 7. Checklist rápido antes de empezar hoy

- [ ] Nico: crear `cloro_usuario` (ejecutar `crear-bases.sql`) y arrancar `usuario-service` en :8081.
- [ ] exe: crear `cloro_medicion` (mismo script) y arrancar `medicion-service` en :8082.
- [ ] Ambos: `git pull`, crear su rama, y confirmar que cada uno ve el trabajo del otro sin tocárselo.
- [ ] Ambos: avisar en el Trello qué ticket está tomando para no duplicar.

---

*Última actualización: jueves — actualizar esta tabla si el reparto cambia.*
