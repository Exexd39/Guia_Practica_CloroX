package cloroapp.usuario.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 160)
    private String correo;

    // Por ahora en texto plano (demo). Más adelante: hash con BCrypt.
    @Column(nullable = false, length = 120)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_granjas", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "granja_id", length = 20)
    private List<String> granjasAsignadas = new ArrayList<>();

    public Usuario() {}

    public Usuario(String id, String nombre, String correo, String clave, Rol rol, List<String> granjas) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
        this.rol = rol;
        this.granjasAsignadas = new ArrayList<>(granjas);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getClave() { return clave; }
    public Rol getRol() { return rol; }
    public List<String> getGranjasAsignadas() { return granjasAsignadas; }

    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setClave(String clave) { this.clave = clave; }
    public void setRol(Rol rol) { this.rol = rol; }
    public void setGranjasAsignadas(List<String> granjas) { this.granjasAsignadas = granjas; }
}