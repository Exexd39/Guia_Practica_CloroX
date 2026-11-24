package cloroapp.medicion.service;

import cloroapp.medicion.cliente.UsuarioClient;
import cloroapp.medicion.dto.PuntoRequest;
import cloroapp.medicion.excepcion.ApiException;
import cloroapp.medicion.model.Punto;
import cloroapp.medicion.repository.GranjaRepository;
import cloroapp.medicion.repository.MedicionRepository;
import cloroapp.medicion.repository.PuntoRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PuntoService {

    private final PuntoRepository puntos;
    private final GranjaRepository granjas;
    private final MedicionRepository mediciones;
    private final UsuarioClient usuarioClient;

    public PuntoService(PuntoRepository puntos, GranjaRepository granjas,
                        MedicionRepository mediciones, UsuarioClient usuarioClient) {
        this.puntos = puntos;
        this.granjas = granjas;
        this.mediciones = mediciones;
        this.usuarioClient = usuarioClient;
    }

    public List<Punto> listar(String idGranja) {
        if (idGranja == null || idGranja.isBlank()) return puntos.findAll();
        if (!granjas.existsById(idGranja)) {
            throw new ApiException(404, "La granja no existe");
        }
        return puntos.findByIdGranja(idGranja);
    }

    public Punto obtener(String id) {
        return puntos.findById(id)
                .orElseThrow(() -> new ApiException(404, "El punto de medición no existe"));
    }

    public Punto crear(PuntoRequest req, String idUsuario) {
        usuarioClient.exigirJefatura(idUsuario);
        validar(req);
        if (!granjas.existsById(req.idGranja())) {
            throw new ApiException(404, "La granja no existe");
        }
        if (puntos.findAll().stream().anyMatch(p -> p.getCodigo().equals(req.codigo()))) {
            throw new ApiException(409, "Ya existe un punto con ese código");
        }
        Punto p = new Punto();
        p.setId(UUID.randomUUID().toString());
        p.setIdGranja(req.idGranja());
        p.setCodigo(req.codigo());
        p.setNombre(req.nombre());
        p.setTipo(req.tipo());
        p.setUbicacion(req.ubicacion());
        return puntos.save(p);
    }

    public Punto actualizar(String id, PuntoRequest req, String idUsuario) {
        usuarioClient.exigirJefatura(idUsuario);
        Punto p = obtener(id);
        validar(req);
        if (!granjas.existsById(req.idGranja())) {
            throw new ApiException(404, "La granja no existe");
        }
        boolean codigoRepetido = puntos.findAll().stream()
                .anyMatch(x -> x.getCodigo().equals(req.codigo()) && !x.getId().equals(id));
        if (codigoRepetido) {
            throw new ApiException(409, "Ya existe un punto con ese código");
        }
        p.setIdGranja(req.idGranja());
        p.setCodigo(req.codigo());
        p.setNombre(req.nombre());
        p.setTipo(req.tipo());
        p.setUbicacion(req.ubicacion());
        return puntos.save(p);
    }

    public void eliminar(String id, String idUsuario) {
        usuarioClient.exigirJefatura(idUsuario);
        Punto p = obtener(id);
        if (mediciones.countByIdPunto(id) > 0) {
            throw new ApiException(409, "No se puede borrar: el punto tiene mediciones registradas");
        }
        puntos.delete(p);
    }

    private void validar(PuntoRequest req) {
        if (req.idGranja() == null || req.idGranja().isBlank()
                || req.codigo() == null || req.codigo().isBlank()
                || req.nombre() == null || req.nombre().isBlank()
                || req.tipo() == null) {
            throw new ApiException(422, "idGranja, codigo, nombre y tipo son obligatorios");
        }
        if (req.nombre().length() < 3 || req.nombre().length() > 60) {
            throw new ApiException(422, "El nombre debe tener entre 3 y 60 caracteres");
        }
    }
}