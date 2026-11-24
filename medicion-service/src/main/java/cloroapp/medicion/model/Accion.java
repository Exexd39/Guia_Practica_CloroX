package cloroapp.medicion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "acciones")
public class Accion {
    @Id
    private String id;
    private String idPunto;
    private String idAlerta;
    private String idMedicion;
    private String tipo;
    private String observaciones;
    private String responsable;
    private String rol;
    private String fecha;   // dd-MM-yyyy
    private String hora;    // HH:mm
    private String estadoResolucion;

    public Accion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdPunto() { return idPunto; }
    public void setIdPunto(String idPunto) { this.idPunto = idPunto; }
    public String getIdAlerta() { return idAlerta; }
    public void setIdAlerta(String idAlerta) { this.idAlerta = idAlerta; }
    public String getIdMedicion() { return idMedicion; }
    public void setIdMedicion(String idMedicion) { this.idMedicion = idMedicion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getEstadoResolucion() { return estadoResolucion; }
    public void setEstadoResolucion(String estadoResolucion) { this.estadoResolucion = estadoResolucion; }
}