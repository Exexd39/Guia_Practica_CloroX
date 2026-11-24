package cloroapp.medicion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "mediciones")
public class Medicion {
    @Id
    private String id;
    private String idPunto;
    private String idGranja;
    private double nivelCloro;
    @Enumerated(EnumType.STRING)
    private EstadoCloro estado;
    @Enumerated(EnumType.STRING)
    private TipoDesviacion desviacion;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHora;

    public Medicion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdPunto() { return idPunto; }
    public void setIdPunto(String idPunto) { this.idPunto = idPunto; }
    public String getIdGranja() { return idGranja; }
    public void setIdGranja(String idGranja) { this.idGranja = idGranja; }
    public double getNivelCloro() { return nivelCloro; }
    public void setNivelCloro(double nivelCloro) { this.nivelCloro = nivelCloro; }
    public EstadoCloro getEstado() { return estado; }
    public void setEstado(EstadoCloro estado) { this.estado = estado; }
    public TipoDesviacion getDesviacion() { return desviacion; }
    public void setDesviacion(TipoDesviacion desviacion) { this.desviacion = desviacion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}