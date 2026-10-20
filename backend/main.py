from fastapi import FastAPI
from pydantic import BaseModel, Field
from datetime import datetime

app = FastAPI(title="API CloroApp")

# Este sera el modelo de datos, para la medicion de cloro, etc
class MedicionCloro(BaseModel):
    id_galpon: str
    nivel_cloro: float
    estado: str
    fecha_hora: str = Field(default_factory=lambda: datetime.now().strftime("%Y-%m-%d %H:%M:%S")) # esto en caso de que el celu no mande fecha, tmb para q se renueve el registro

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
def registrar_medicion(medicion: MedicionCloro):
    base_datos_mediciones.append(medicion.model_dump())
    return {"mensaje": "Medicion registrada exitosamente", "datos": medicion}