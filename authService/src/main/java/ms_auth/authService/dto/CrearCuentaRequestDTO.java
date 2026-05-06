package ms_auth.authService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearCuentaRequestDTO {
    @NotBlank(message = "RUT requerido")
    private String rutUsuario;

    @NotBlank(message = "Contraseña requerida")
    private String password;

    private String tipoRol; // ESTUDIANTE, DOCENTE, APODERADO
}
