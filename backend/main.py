from fastapi import FastAPI
from pydantic import BaseModel, Field
from datetime import datetime

app = FastAPI(title="API CloroApp")

# Rangos que se ocuparan para mediciones en ppm de cloro
CRITICO_MIN = 0.5
NORMAL_MIN = 1.0
NORMAL_MAX = 3.0
CRITICO_MAX = 5.0

# funcion que calculara el estado de ppm
def calcular_estado(cloro: float) -> str:
    if cloro < CRITICO_MIN or cloro > CRITICO_MAX:
        return "critico"
    if NORMAL_MIN <= cloro <= NORMAL_MAX:
        return "normal"
    return "advertencia"

# Clase para medicion
class MedicionEntrada(BaseModel):
    id_galpon: str
    nivel_cloro: float = Field(ge=0, le=20)

# Este sera el modelo de datos, para la medicion de cloro, etc
class MedicionCloro(BaseModel):
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

# Aqui van todas las rutas
# GET
@app.get("/")
def leer_raiz():
    return {"mensaje": "Bienvenido a la Api de CloroApp, ojala funcione jeje"}

@app.get("/granjas")
def obtener_granjas():
    return granjas_disponibles

@app.get("/mediciones")
def obtener_todas_mediciones():
    return base_datos_mediciones

# POST
@app.post("/mediciones")
def registrar_medicion(entrada: MedicionEntrada):
    medicion = MedicionCloro(
        id_galpon=entrada.id_galpon,
        nivel_cloro=entrada.nivel_cloro,
        estado=calcular_estado(entrada.nivel_cloro),
        fecha_hora=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
    )
    base_datos_mediciones.append(medicion.model_dump())
    return {"mensaje": "Medicion registrada exitosamente", "datos": medicion}