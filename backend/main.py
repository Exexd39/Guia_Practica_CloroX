from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from datetime import datetime
from uuid import uuid4

app = FastAPI(title="API CloroApp")

# Rango de ppm, formato de fecha
CRITICO_MIN = 2.0
NORMAL_MIN = 3.0
NORMAL_MAX = 5.0
CRITICO_MAX = 6.0

FORMATO_FECHA = "%Y-%m-%d %H:%M:%S"

# ---------- DATOS (listas en memoria) ----------
granjas = [
    {"id": "g1", "nombre": "Granja Norte"},
    {"id": "g2", "nombre": "Granja Sur"},
    {"id": "g3", "nombre": "Granja Melipilla"},
]

# id, id_granja, codigo, nombre, tipo, ubicacion, cloro actual, hora
PUNTOS_BASE = [
    ("p1", "g1", "N-TQ-01", "Tanque principal", "TANQUE", "Sala de bombas", 4.20, "10:05"),
    ("p2", "g1", "N-LI-01", "Línea galpón 1", "LINEA", "Galpón 1", 3.60, "10:10"),
    ("p3", "g1", "N-BE-02", "Bebedero galpón 2", "BEBEDERO", "Galpón 2", 2.50, "10:15"),
    ("p4", "g1", "N-BE-03", "Bebedero galpón 3", "BEBEDERO", "Galpón 3", 5.40, "10:20"),
    ("p5", "g2", "S-TQ-01", "Tanque principal", "TANQUE", "Sala de bombas", 4.80, "10:02"),
    ("p6", "g2", "S-LI-01", "Línea galpón 1", "LINEA", "Galpón 1", 3.10, "10:07"),
    ("p7", "g2", "S-BE-02", "Bebedero galpón 2", "BEBEDERO", "Galpón 2", 4.00, "10:12"),
    ("p8", "g2", "S-BE-03", "Bebedero galpón 3", "BEBEDERO", "Galpón 3", 1.80, "10:18"),
    ("p9", "g3", "M-TQ-01", "Tanque principal", "TANQUE", "Sala de bombas", 6.30, "10:03"),
    ("p10", "g3", "M-LI-01", "Línea galpón 1", "LINEA", "Galpón 1", 4.50, "10:09"),
    ("p11", "g3", "M-BE-02", "Bebedero galpón 2", "BEBEDERO", "Galpón 2", 3.80, "10:14"),
    ("p12", "g3", "M-BE-03", "Bebedero galpón 3", "BEBEDERO", "Galpón 3", 2.10, "10:21"),
]

puntos_medicion = [
    {"id": id_p, "id_granja": id_g, "codigo": cod, "nombre": nom, "tipo": tipo, "ubicacion": ubi}
    for id_p, id_g, cod, nom, tipo, ubi, _, _ in PUNTOS_BASE
]

usuarios = [
    {"id": "u1", "nombre": "Ana Pérez", "correo": "operario@cloro.cl",
     "clave": "1234", "rol": "OPERARIO", "granjasAsignadas": ["g1"]},
    {"id": "u2", "nombre": "Luis Soto", "correo": "supervisor@cloro.cl",
     "clave": "1234", "rol": "SUPERVISOR", "granjasAsignadas": ["g1", "g2"]},
    {"id": "u3", "nombre": "Carla Mora", "correo": "jefatura@cloro.cl",
     "clave": "1234", "rol": "JEFATURA", "granjasAsignadas": ["g1", "g2", "g3"]},
]

# Se guardaran datos
base_datos_mediciones = []
base_datos_alertas = []
base_datos_acciones = []

# ---------- FUNCIONES ---------
def calcular_estado(cloro: float) -> str:
    if cloro < CRITICO_MIN or cloro > CRITICO_MAX:
        return "CRITICO"
    if NORMAL_MIN <= cloro <= NORMAL_MAX:
        return "NORMAL"
    return "ADVERTENCIA"

def calcular_desviacion(cloro: float) -> str:
    if cloro < NORMAL_MIN:
        return "INSUFICIENTE"
    if cloro > NORMAL_MAX:
        return "EXCESIVO"
    return "NINGUNA"

def normalizar_fecha(fecha: str | None) -> str:
    if fecha is None:
        return datetime.now().strftime(FORMATO_FECHA)
    try:
        return datetime.strptime(fecha, FORMATO_FECHA).strftime(FORMATO_FECHA)
    except ValueError:
        raise HTTPException(
            status_code=422,
            detail="fecha_hora debe tener formato AAAA-MM-DD HH:MM:SS"
        )

def buscar_punto(id_punto: str):
    for punto in puntos_medicion:
        if punto["id"] == id_punto:
            return punto
    return None

def buscar_usuario(id_usuario: str):
    for u in usuarios:
        if u["id"] == id_usuario:
            return u
    return None

def buscar_alerta(id_alerta: str):
    for a in base_datos_alertas:
        if a["id"] == id_alerta:
            return a
    return None


def crear_alerta(medicion: dict):
    if medicion["estado"] == "NORMAL":
        return None
    # En caso de que si sea alerta, se creara
    alerta = {
        "id": str(uuid4()),
        "id_medicion": medicion["id"],
        "id_punto": medicion["id_punto"],
        "id_granja": medicion["id_granja"],
        "gravedad": medicion["estado"],
        "desviacion": medicion["desviacion"],
        "nivel_cloro": medicion["nivel_cloro"],
        "estado": "ABIERTA",
        "fecha_hora": medicion["fecha_hora"],
    }
    base_datos_alertas.append(alerta)
    return alerta
    
def crear_medicion(id_punto: str, nivel: float, fecha_hora: str | None = None):
    punto = buscar_punto(id_punto)
    if punto is None:
        raise HTTPException(status_code=404, detail="El punto de medicion no existe")
    # En caso de que exista
    nivel = round(nivel, 2)
    medicion = {
        "id": str(uuid4()),
        "id_punto": id_punto,
        "id_granja": punto["id_granja"],
        "nivel_cloro": nivel,
        "estado": calcular_estado(nivel),
        "desviacion": calcular_desviacion(nivel),
        "fecha_hora": normalizar_fecha(fecha_hora),
    }
    base_datos_mediciones.append(medicion)
    alerta = crear_alerta(medicion)
    return medicion, alerta

def ultima_medicion(id_punto: str):
    propias = [m for m in base_datos_mediciones if m["id_punto"] == id_punto]
    if not propias:
        return None
    return max(propias, key=lambda m: m["fecha_hora"])

def alerta_abierta_de(id_punto: str):
    abiertas = [a for a in base_datos_alertas
                if a["id_punto"] == id_punto and a["estado"] == "ABIERTA"]
    if not abiertas:
        return None
    return max(abiertas, key=lambda a: a["fecha_hora"])


# Datos de arranque: las 12 mediciones actuales de la UI (Exclusivo de arranque)
for _id, _g, _c, _n, _t, _u, _cloro, _hora in PUNTOS_BASE:
    crear_medicion(_id, _cloro, f"2026-10-21 {_hora}:00")


# ---------- MODELOS ----------
class MedicionEntrada(BaseModel):
    id_punto: str
    nivel_cloro: float = Field(ge=0, le=20)
    fecha_hora: str | None = None

class LoginEntrada(BaseModel):
    correo: str
    clave: str

class AccionEntrada(BaseModel):
    id_punto: str
    id_usuario: str
    tipo: str
    observaciones: str = Field(min_length=3)

class CierreEntrada(BaseModel):
    id_usuario: str

# ---------- RUTAS ----------
"""
GET
"""
@app.get("/")
def leer_raiz():
    return {"mensaje": "Bienvenido a la Api de CloroApp, ojala funcione jeje"}

# EXCLUSIVO PARA QUE LO LEA RENDER
@app.get("/health")
def health_check():
    return {"status": "Esta todo bien amigo render, te quiero"}

@app.get("/granjas")
def obtener_granjas():
    return granjas

@app.get("/granjas/resumen")
def granjas_con_puntos():
    resultado = []
    for g in granjas:
        puntos = []
        for p in puntos_medicion:
            if p["id_granja"] != g["id"]:
                continue
            m = ultima_medicion(p["id"])
            fecha, hora = m["fecha_hora"].split(" ")
            anio, mes, dia = fecha.split("-")
            puntos.append({
                "id": p["id"],
                "codigo": p["codigo"],
                "nombre": p["nombre"],
                "tipo": p["tipo"],
                "ubicacion": p["ubicacion"],
                "cloroLibre": m["nivel_cloro"],
                "fecha": f"{dia}-{mes}-{anio}",
                "hora": hora[:5],
                "estado": m["estado"],
                "desviacion": m["desviacion"],
            })
        resultado.append({"id": g["id"], "nombre": g["nombre"], "puntos": puntos})
    return resultado

@app.get("/puntos")
def obtener_puntos(id_granja: str | None = None):
    if id_granja is None:
        return puntos_medicion
    return [p for p in puntos_medicion if p["id_granja"] == id_granja]

@app.get("/acciones")
def listar_acciones(id_punto: str | None = None, id_alerta: str | None = None):
    resultado = base_datos_acciones
    if id_punto is not None:
        resultado = [a for a in resultado if a["id_punto"] == id_punto]
    if id_alerta is not None:
        resultado = [a for a in resultado if a["id_alerta"] == id_alerta]
    return resultado

@app.get("/puntos/{id_punto}/mediciones")
def mediciones_de_punto(id_punto: str):
    if buscar_punto(id_punto) is None:
        raise HTTPException(status_code=404, detail="El punto de medicion no existe")
    return [m for m in base_datos_mediciones if m["id_punto"] == id_punto]

@app.get("/mediciones")
def obtener_mediciones():
    return base_datos_mediciones

@app.get("/alertas")
def listar_alertas(
    estado: str | None = None,
    id_granja: str | None = None,
    id_punto: str | None = None,
):
    resultado = base_datos_alertas
    if estado is not None:
        resultado = [a for a in resultado if a["estado"] == estado.upper()]
    if id_granja is not None:
        resultado = [a for a in resultado if a["id_granja"] == id_granja]
    if id_punto is not None:
        resultado = [a for a in resultado if a["id_punto"] == id_punto]
    return sorted(resultado, key=lambda a: a["fecha_hora"], reverse=True)

@app.get("/alertas/{id_alerta}")
def detalle_alerta(id_alerta: str):
    for alerta in base_datos_alertas:
        if alerta["id"] == id_alerta:
            return alerta
    raise HTTPException(status_code=404, detail="La alerta no existe")


"""
POST
"""
@app.post("/mediciones")
def registrar_medicion(entrada: MedicionEntrada):
    medicion, alerta = crear_medicion(
        entrada.id_punto, entrada.nivel_cloro, entrada.fecha_hora
    )
    return {
        "mensaje": "Medicion registrada existosamente",
        "datos": medicion,
        "alerta": alerta
    }

@app.post("/login")
def login(datos: LoginEntrada):
    for u in usuarios:
        if u["correo"].lower() == datos.correo.strip().lower() and u["clave"] == datos.clave:
            return {k: v for k, v in u.items() if k != "clave"}
    raise HTTPException(status_code=401, detail="Correo o contraseña incorrectos") 

@app.post("/acciones")
def registrar_accion(entrada: AccionEntrada):
    if buscar_punto(entrada.id_punto) is None:
        raise HTTPException(status_code=404, detail="El punto de medición no existe")
    usuario = buscar_usuario(entrada.id_usuario)
    if usuario is None:
        raise HTTPException(status_code=404, detail="El usuario no existe")

    alerta = alerta_abierta_de(entrada.id_punto)
    ahora = datetime.now()
    accion = {
        "id": str(uuid4()),
        "id_punto": entrada.id_punto,
        "id_alerta": alerta["id"] if alerta else None,
        "id_medicion": alerta["id_medicion"] if alerta else None,
        "tipo": entrada.tipo,
        "observaciones": entrada.observaciones,
        "responsable": usuario["nombre"],
        "rol": usuario["rol"],
        "fecha": ahora.strftime("%d-%m-%Y"),
        "hora": ahora.strftime("%H:%M"),
        "estado_resolucion": "REGISTRADA",
    }
    base_datos_acciones.append(accion)
    return accion

@app.post("/alertas/{id_alerta}/cerrar")
def cerrar_alerta(id_alerta: str, entrada: CierreEntrada):
    alerta = buscar_alerta(id_alerta)
    if alerta is None:
        raise HTTPException(status_code=404, detail="La alerta no existe")
    usuario = buscar_usuario(entrada.id_usuario)
    if usuario is None:
        raise HTTPException(status_code=404, detail="El usuario no existe")
    if usuario["rol"] != "SUPERVISOR":
        raise HTTPException(status_code=403, detail="Solo un supervisor puede cerrar alertas")
    if alerta["estado"] != "ABIERTA":
        raise HTTPException(status_code=409, detail="La alerta ya esta cerrada")

    acciones = [a for a in base_datos_acciones if a["id_alerta"] == id_alerta]
    if not acciones:
        raise HTTPException(status_code=409, detail="Falta registrar una acción correctiva")

    ultima = ultima_medicion(alerta["id_punto"])
    if ultima["estado"] != "NORMAL" or ultima["fecha_hora"] <= alerta["fecha_hora"]:
        raise HTTPException(status_code=409,detail="Se necesita una nueva medición dentro del rango normal")

    alerta["estado"] = "CERRADA"
    alerta["cerrada_por"] = usuario["nombre"]
    alerta["fecha_cierre"] = datetime.now().strftime(FORMATO_FECHA)
    for a in acciones:
        a["estado_resolucion"] = "RESUELTA"
    return alerta 