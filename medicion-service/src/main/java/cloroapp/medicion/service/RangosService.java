package cloroapp.medicion.service;

import cloroapp.medicion.model.EstadoCloro;
import cloroapp.medicion.model.TipoDesviacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RangosService {

    @Value("${cloro.rango.normal.min}")       private double normalMin;
    @Value("${cloro.rango.normal.max}")       private double normalMax;
    @Value("${cloro.rango.advertencia.min}")  private double advMin;
    @Value("${cloro.rango.advertencia.max}")  private double advMax;

    public EstadoCloro clasificar(double v) {
        if (v >= normalMin && v <= normalMax) return EstadoCloro.NORMAL;
        if (v >= advMin && v <= advMax) return EstadoCloro.ADVERTENCIA;
        return EstadoCloro.CRITICO;
    }

    public TipoDesviacion desviacion(double v) {
        if (v >= normalMin && v <= normalMax) return TipoDesviacion.NINGUNA;
        return v < normalMin ? TipoDesviacion.INSUFICIENTE : TipoDesviacion.EXCESIVO;
    }
}