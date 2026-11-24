package cloroapp.medicion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "puntos")
public class Punto {
    @Id
    private String id;
    private String idGranja;
    private String codigo;
    private String nombre;
    @Enumerated(EnumType.STRING)
    private TipoPunto tipo;
    private String ubicacion;

    public Punto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdGranja() { return idGranja; }
    public void setIdGranja(String idGranja) { this.idGranja = idGranja; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoPunto getTipo() { return tipo; }
    public void setTipo(TipoPunto tipo) { this.tipo = tipo; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}