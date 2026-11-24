package cloroapp.medicion.service;

import cloroapp.medicion.dto.MedicionRequest;
import cloroapp.medicion.dto.RespuestaMedicion;
import cloroapp.medicion.excepcion.ApiException;
import cloroapp.medicion.model.Alerta;
import cloroapp.medicion.model.EstadoAlerta;
import cloroapp.medicion.model.EstadoCloro;
import cloroapp.medicion.model.Medicion;
import cloroapp.medicion.model.Punto;
import cloroapp.medicion.repository.AlertaRepository;
import cloroapp.medicion.repository.MedicionRepository;
import cloroapp.medicion.repository.PuntoRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MedicionService {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId CHILE = ZoneId.of("America/Santiago");
    private static final Map<String, Long> PERIODOS = Map.of(
            "24h", 24L, "7d", 7L * 24, "30d", 30L * 24);

    private final MedicionRepository mediciones;
    private final PuntoRepository puntos;
    private final AlertaRepository alertas;
    private final RangosService rangos;

    public MedicionService(MedicionRepository mediciones, PuntoRepository puntos,
                           AlertaRepository alertas, RangosService rangos) {
        this.mediciones = mediciones;
        this.puntos = puntos;
        this.alertas = alertas;
        this.rangos = rangos;
    }

    public RespuestaMedicion crear(MedicionRequest req) {
        Punto punto = puntos.findById(req.idPunto())
                .orElseThrow(() -> new ApiException(404, "El punto de medición no existe"));

        if (req.nivelCloro() == null || req.nivelCloro() < 0 || req.nivelCloro() > 20) {
            throw new ApiException(422, "El nivel de cloro debe estar entre 0 y 20 mg/L");
        }
        double nivel = Math.round(req.nivelCloro() * 100.0) / 100.0;

        LocalDateTime fecha;
        if (req.fechaHora() == null || req.fechaHora().isBlank()) {
            fecha = LocalDateTime.now(CHILE).withNano(0);
        } else {
            try {
                fecha = LocalDateTime.parse(req.fechaHora().trim(), FORMATO);
            } catch (Exception e) {
                throw new ApiException(422, "La fechaHora debe tener el formato yyyy-MM-dd HH:mm:ss");
            }
        }

        EstadoCloro estado = rangos.clasificar(nivel);
        Medicion m = new Medicion();
        m.setId(UUID.randomUUID().toString());
        m.setIdPunto(punto.getId());
        m.setIdGranja(punto.getIdGranja());
        m.setNivelCloro(nivel);
        m.setEstado(estado);
        m.setDesviacion(rangos.desviacion(nivel));
        m.setFechaHora(fecha);
        m = mediciones.save(m);

        Alerta alerta = null;
        if (estado != EstadoCloro.NORMAL) {
            alerta = new Alerta();
            alerta.setId(UUID.randomUUID().toString());
            alerta.setIdMedicion(m.getId());
            alerta.setIdPunto(punto.getId());
            alerta.setIdGranja(punto.getIdGranja());
            alerta.setGravedad(estado);
            alerta.setDesviacion(m.getDesviacion());
            alerta.setNivelCloro(nivel);
            alerta.setEstado(EstadoAlerta.ABIERTA);
            alerta.setFechaHora(fecha);
            alerta = alertas.save(alerta);
        }
        return new RespuestaMedicion("Medicion registrada exitosamente", m, alerta);
    }

    public List<Medicion> listar(String idPunto, String periodo) {
        if (!puntos.existsById(idPunto)) {
            throw new ApiException(404, "El punto de medición no existe");
        }
        Long horas = periodo == null ? null : PERIODOS.get(periodo.toLowerCase(Locale.ROOT));
        if (horas == null) {
            throw new ApiException(422, "Período inválido: use 24h, 7d o 30d");
        }
        LocalDateTime desde = LocalDateTime.now(CHILE).minusHours(horas);
        return mediciones.findByIdPuntoOrderByFechaHoraAsc(idPunto).stream()
                .filter(m -> !m.getFechaHora().isBefore(desde))
                .toList();
    }
}