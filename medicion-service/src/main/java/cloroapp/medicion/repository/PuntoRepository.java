package cloroapp.medicion.repository;

import cloroapp.medicion.model.Punto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuntoRepository extends JpaRepository<Punto, String> {
    List<Punto> findByIdGranja(String idGranja);
}