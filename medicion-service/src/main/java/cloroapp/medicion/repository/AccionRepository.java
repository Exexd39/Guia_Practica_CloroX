package cloroapp.medicion.repository;

import cloroapp.medicion.model.Accion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccionRepository extends JpaRepository<Accion, String> {
}