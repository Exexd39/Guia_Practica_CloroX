package cloroapp.medicion.dto;

import cloroapp.medicion.model.Alerta;
import cloroapp.medicion.model.Medicion;

public record RespuestaMedicion(String mensaje, Medicion datos, Alerta alerta) {}