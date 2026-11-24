package cloroapp.usuario.dto;

import cloroapp.usuario.model.Usuario;
import java.util.List;

public record UsuarioDto(
        String id,
        String nombre,
        String correo,
        String rol,
        List<String> granjasAsignadas
) {
    public static UsuarioDto desde(Usuario u) {
        return new UsuarioDto(
                u.getId(),
                u.getNombre(),
                u.getCorreo(),
                u.getRol().name(),
                u.getGranjasAsignadas()
        );
    }
}