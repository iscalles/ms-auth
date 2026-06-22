package ms_auth.authService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RecuperarPasswordRequestDTO {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Correo inválido")
    private String correo;

    public RecuperarPasswordRequestDTO() {}

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
