package ms_auth.authService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InicializarCuentaDTO {

    @NotNull(message = "El id de usuario es obligatorio")
    private Long idUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String passwordPlano;

    public InicializarCuentaDTO() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getPasswordPlano() { return passwordPlano; }
    public void setPasswordPlano(String passwordPlano) { this.passwordPlano = passwordPlano; }
}
