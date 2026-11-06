# CloroApp

App Android para consultar y registrar el nivel de **cloro libre** en el agua de granjas avícolas, con alertas, historial y modo sin conexión.

Proyecto académico del caso **"Medición de cloro en línea"** (DSY1105 · Desarrollo de Aplicaciones Móviles, Duoc UC). Todos los datos son **ficticios**: no se conecta a ningún sistema real de la empresa.

## Qué hace

- **Consulta** granjas y puntos de medición con su estado (normal / advertencia / crítico) en forma de semáforo.
- **Registra mediciones** desde el celular, con validación del valor ingresado.
- **Genera alertas automáticamente** cuando una medición sale del rango, indicando si el cloro es *insuficiente* o *excesivo*.
- **Registra acciones correctivas** y permite al supervisor **cerrar la alerta** solo cuando se cumplen las condiciones (ver más abajo).
- **Historial** de las últimas 24 horas, 7 días y 30 días, con promedio, máximo y mínimo.
- **Modo sin conexión:** muestra los últimos datos guardados y deja registrar mediciones que se envían solas cuando vuelve internet.
- **Notificaciones locales** para alertas críticas de las granjas del usuario.
- **Inicio de sesión simulado** con tres perfiles.
- **Cámara** Evidencia fotográfica en las acciones correctivas, con cámara (permiso CAMERA en tiempo de ejecución), galería (Photo Picker) y almacenamiento interno privado.

## Trello
Se hizo una lista de tareas en trello: *https://trello.com/invite/b/6aec1453a32776e45a9f1849/ATTIf2b3483ffbcc43fe057bffbdb60686a775EAA94E/cloroapp*

## Roles

| Rol | Qué puede hacer |
|---|---|
| **Operario** | Ve sus granjas asignadas, registra mediciones y acciones correctivas. |
| **Supervisor** | Ve sus granjas, revisa alertas, registra acciones y **cierra alertas**. |
| **Jefatura** | Vista general de todas las granjas e históricos. Solo consulta. |

## Rangos de cloro libre (ppm = mg/L)

Valores académicos propuestos en la hoja de respuestas del caso.

| Estado | Condición |
|---|---|
| Normal | 3 a 5 |
| Advertencia | 2 a menos de 3, o más de 5 hasta 6 |
| Crítico | menos de 2, o más de 6 |

El servidor es la única fuente de verdad: el celular envía solo el punto y el valor medido, y el servidor calcula el estado, el tipo de desviación, la hora y las alertas.

## Reglas de las alertas

Una alerta se cierra únicamente si:

1. quien la cierra es **supervisor** (si no, el servidor responde 403),
2. existe al menos una **acción correctiva** registrada, y
3. hay una **medición posterior dentro del rango normal** en ese punto.

Si falta algo, el servidor responde 409 con el motivo, y la app lo muestra tal cual.

## Arquitectura

```
Pantalla (Compose)
      │  observa StateFlow
      ▼
  Repositorio ──► Retrofit ──► API FastAPI (Render)
      │
      └────────► Room (copia local + mediciones pendientes)
```

- Primero se muestra lo guardado en Room y luego se actualiza desde la API.
- Si no hay conexión, las mediciones se guardan como pendientes (con la hora real en que se midieron) y se envían al abrir el dashboard o al tocar *Actualizar*.
- La respuesta del servidor se convierte con clases de transporte (DTO) a los modelos que usa la interfaz.

## Tecnologías

**Android:** Kotlin · Jetpack Compose (Material 3) · Navigation Compose · Retrofit + Gson · Room 2.6.1 (KSP) · WorkManager · `StateFlow`.
Min SDK 24.

**Backend:** Python 3.10 o superior · FastAPI · Uvicorn · datos en memoria.

## Estructura del repositorio

```
.
├── app/                     # App Android
│   └── src/main/java/com/example/cloroapp/
│       ├── data/            # Repositorio, Room, Retrofit, modelos, notificaciones
│       └── ui/              # Pantallas, navegación y tema
├── backend/
│   ├── main.py              # API FastAPI
│   └── requirements.txt
└── README.md
```

## Cómo ejecutarlo

### API desplegada

La app apunta por defecto a `https://cloroapp-api.onrender.com/` (en `RetrofitClient.kt`).

> El servidor corre en el plan gratuito de Render: si lleva ~15 minutos sin uso se duerme y la primera petición puede tardar hasta 1 minuto. Los datos están en memoria, así que se reinician cada vez que el servidor se reinicia. Antes de una demostración, abre `/health` para despertarlo.

### API en tu computador

```bash
cd backend
python -m venv .venv
.venv\Scripts\activate          # en Linux/macOS: source .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload
```

La documentación interactiva queda en `https://cloroapp-api.onrender.com/docs`.  
Nota: Para que el emulador la use, cambia `BASE_URL` en `RetrofitClient.kt` a `http://10.0.2.2:8000/` (la configuración de red ya permite ese tráfico sin cifrar).

### App Android

1. Abre el proyecto en Android Studio y sincroniza Gradle.
2. Ejecuta en un emulador o en un celular con Android 7.0 (API 24) o superior.
3. En Android 13 o superior, acepta el permiso de notificaciones.

## Usuarios de prueba

| Correo | Clave | Rol | Granjas |
|---|---|---|---|
| `operario@cloro.cl` | `1234` | Operario | Norte |
| `supervisor@cloro.cl` | `1234` | Supervisor | Norte, Sur |
| `jefatura@cloro.cl` | `1234` | Jefatura | Norte, Sur, Melipilla |

## Datos de ejemplo

3 granjas (Norte, Sur, Melipilla) con 4 puntos cada una: tanque principal, línea del galpón 1 y bebederos de los galpones 2 y 3. Al iniciar, el servidor genera 30 días de historial ficticio (una medición cada 6 horas) más una medición actual por punto.

## API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/health` | Verificación de estado del servidor |
| GET | `/granjas` | Lista de granjas |
| GET | `/granjas/resumen` | Granjas con sus puntos y última medición |
| GET | `/puntos?id_granja=` | Puntos de medición |
| GET | `/puntos/{id}/mediciones?periodo=24h\|7d\|30d` | Historial de un punto |
| GET | `/mediciones` | Todas las mediciones |
| GET | `/resumen?periodo=&id_granja=` | Promedio, máximo, mínimo y alertas abiertas |
| GET | `/alertas?estado=&id_granja=&id_punto=` | Alertas, filtrables |
| GET | `/alertas/{id}` | Detalle de una alerta |
| GET | `/acciones?id_punto=&id_alerta=` | Acciones correctivas |
| POST | `/login` | Inicio de sesión simulado |
| POST | `/mediciones` | Registra una medición (calcula estado y alerta) |
| POST | `/acciones` | Registra una acción correctiva |
| POST | `/alertas/{id}/cerrar` | Cierra una alerta (solo supervisor) |

Códigos de error usados: `401` credenciales incorrectas, `403` sin permiso, `404` no existe, `409` conflicto con el estado actual, `422` datos inválidos.

## Limitaciones conocidas

- Los datos del servidor están en memoria: no hay base de datos persistente.
- La sesión es simulada: el servidor identifica al usuario por el `id_usuario` que envía la app, sin tokens, y las claves de prueba están en texto plano. En un sistema real habría autenticación segura.
- Sin conexión, el inicio de sesión usa los usuarios de prueba locales.
- El historial sin conexión solo está disponible para los períodos ya consultados durante la sesión.
- Las mediciones pendientes se envían al abrir el dashboard o al actualizar; la revisión automática de alertas críticas se ejecuta cada 15 minutos como mínimo, según lo permite Android.
- No incluye integración con AMINO ni ERP, notificaciones push reales, exportación a PDF/Excel ni escalamiento automático de alertas (previstos como evolución futura en el caso).
- Cuando se accede hacia alguna alerta desde notificaciones aparecera la pantalla de Login, tambien llegan otras notificaciones de otros usuarios.
- Las fotos de evidencia se guardan solo en el celular, no se envían al servidor.

## Autores

- [Exequiel Romero] — servidor, datos y Room
- [Nicolás Morales] — interfaz de usuario
