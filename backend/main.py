from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from datetime import datetime

app = FastAPI(title="API CloroApp")

# Rangos que se ocuparan para mediciones en ppm de cloro
CRITICO_MIN = 2.0
NORMAL_MIN = 3.0
NORMAL_MAX = 5.0
CRITICO_MAX = 6.0

# FUNCIONES
def calcular_estado(cloro: float) -> str:
    if cloro < CRITICO_MIN or cloro > CRITICO_MAX:
        return "critico"
    if NORMAL_MIN <= cloro <= NORMAL_MAX:
        return "normal"
    return "advertencia"

def buscar_punto(id_punto: str):
    for punto in puntos_medicion:
        if punto["id"] == id_punto:
            return punto
    return None

# Clase para medicion
class MedicionEntrada(BaseModel):
    id_punto: str
    nivel_cloro: float = Field(ge=0, le=20)

# Este sera el modelo de datos, para la medicion de cloro, etc
class MedicionCloro(BaseModel):
    id_punto: str
    id_galpon: str
    nivel_cloro: float
    estado: str
    fecha_hora: str


# Aca simularemos la abse de datos (que seria un array simple jeje)
base_datos_mediciones = []

granjas_disponibles = [
    {"id": "G1", "nombre": "Galpón 1 - Sector Norte"},
    {"id": "G2", "nombre": "Galpón 2 - Sector Sur"}
]

puntos_medicion = [
    {"id": "G1-BEB", "id_galpon": "G1", "nombre": "Bebedero"},
    {"id": "G1-TAN", "id_galpon": "G1", "nombre": "Tanque"},
    {"id": "G2-BEB", "id_galpon": "G2", "nombre": "Bebedero"},
    {"id": "G2-TAN", "id_galpon": "G2", "nombre": "Tanque"},
]

# Aqui van todas las rutas #
# GET
@app.get("/")
def leer_raiz():
    return {"mensaje": "Bienvenido a la Api de CloroApp, ojala funcione jeje"}

@app.get("/puntos")
def obtener_puntos(id_galpon: str | None = None):
    if id_galpon is None:
        return puntos_medicion
    return [p for p in puntos_medicion if p["id_galpon"] == id_galpon]

@app.get("/puntos/{id_punto}/mediciones")
def mediciones_de_punto(id_punto: str):
    if buscar_punto(id_punto) is None:
        raise HTTPException(status_code=404, detail="El punto de medicion no existe")
    return [m for m in base_datos_mediciones if m["id_punto"] == id_punto]

# POST
@app.post("/mediciones")
def registrar_medicion(entrada: MedicionEntrada):
    punto = buscar_punto(entrada.id_punto)
    if punto is None:
        raise HTTPException(status_code=404, detail="El punto de medición no existe")

    medicion = MedicionCloro(
        id_punto=entrada.id_punto,
        id_galpon=punto["id_galpon"],
        nivel_cloro=entrada.nivel_cloro,
        estado=calcular_estado(entrada.nivel_cloro),
        fecha_hora=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
    )
    base_datos_mediciones.append(medicion.model_dump())
    return {"mensaje": "Medicion registrada exitosamente", "datos": medicion}