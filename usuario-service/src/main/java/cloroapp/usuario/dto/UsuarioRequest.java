package cloroapp.usuario.dto;

import cloroapp.usuario.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UsuarioRequest(
        @NotBlank String nombre,
        @NotBlank String correo,
        String clave,            // obligatoria al crear; al editar, vacía = no cambiar
        @NotNull Rol rol,
        @NotNull List<String> granjasAsignadas
) {}