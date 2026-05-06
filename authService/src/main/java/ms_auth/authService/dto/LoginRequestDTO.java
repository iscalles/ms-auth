package ms_auth.authService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "RUT requerido")
    private String rutUsuario;

    @NotBlank(message = "Contraseña requerida")
    private String password;
}
