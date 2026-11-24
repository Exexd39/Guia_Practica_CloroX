package cloroapp.medicion.repository;

import cloroapp.medicion.model.Medicion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicionRepository extends JpaRepository<Medicion, String> {
    List<Medicion> findByIdPuntoOrderByFechaHoraAsc(String idPunto);
    long countByIdPunto(String idPunto);
}