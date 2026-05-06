package ms_auth.authService.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank(message = "RUT requerido")
    private String rutUsuario;

    @NotBlank(message = "Contraseña requerida")
    private String password;

    public LoginRequestDTO() {}

    public LoginRequestDTO(String rutUsuario, String password) {
        this.rutUsuario = rutUsuario;
        this.password = password;
    }

    public String getRutUsuario() {
        return rutUsuario;
    }

    public void setRutUsuario(String rutUsuario) {
        this.rutUsuario = rutUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}