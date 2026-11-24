package cloroapp.medicion.repository;

import cloroapp.medicion.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, String> {
}