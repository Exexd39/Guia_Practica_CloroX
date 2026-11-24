package cloroapp.medicion.dto;

import cloroapp.medicion.model.TipoPunto;

public record PuntoRequest(String idGranja, String codigo, String nombre, TipoPunto tipo, String ubicacion) {}