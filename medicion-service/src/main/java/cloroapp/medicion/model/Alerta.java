package cloroapp.medicion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {
    @Id
    private String id;
    private String idMedicion;
    private String idPunto;
    private String idGranja;
    @Enumerated(EnumType.STRING)
    private EstadoCloro gravedad;
    @Enumerated(EnumType.STRING)
    private TipoDesviacion desviacion;
    private double nivelCloro;
    @Enumerated(EnumType.STRING)
    private EstadoAlerta estado;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHora;
    private String cerradaPor;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCierre;

    public Alerta() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdMedicion() { return idMedicion; }
    public void setIdMedicion(String idMedicion) { this.idMedicion = idMedicion; }
    public String getIdPunto() { return idPunto; }
    public void setIdPunto(String idPunto) { this.idPunto = idPunto; }
    public String getIdGranja() { return idGranja; }
    public void setIdGranja(String idGranja) { this.idGranja = idGranja; }
    public EstadoCloro getGravedad() { return gravedad; }
    public void setGravedad(EstadoCloro gravedad) { this.gravedad = gravedad; }
    public TipoDesviacion getDesviacion() { return desviacion; }
    public void setDesviacion(TipoDesviacion desviacion) { this.desviacion = desviacion; }
    public double getNivelCloro() { return nivelCloro; }
    public void setNivelCloro(double nivelCloro) { this.nivelCloro = nivelCloro; }
    public EstadoAlerta getEstado() { return estado; }
    public void setEstado(EstadoAlerta estado) { this.estado = estado; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getCerradaPor() { return cerradaPor; }
    public void setCerradaPor(String cerradaPor) { this.cerradaPor = cerradaPor; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
}