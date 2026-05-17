package ms_auth.authService.dto;

import jakarta.validation.constraints.NotBlank;

public class CrearCuentaRequestDTO {
    @NotBlank(message = "RUT requerido")
    private String rutUsuario;

    @NotBlank(message = "Contraseña requerida")
    private String password;

    private String tipoRol;

    public CrearCuentaRequestDTO() {}

    public CrearCuentaRequestDTO(String rutUsuario, String password, String tipoRol) {
        this.rutUsuario = rutUsuario;
        this.password = password;
        this.tipoRol = tipoRol;
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

    public String getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }
}