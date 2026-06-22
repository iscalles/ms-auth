package ms_auth.authService.dto;

import java.util.Set;

public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long idUsuario;
    private String nombre;
    private String correo;
    private Set<String> roles;
    private Long expiresIn;
    private boolean debeCambiarPassword;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String accessToken, String refreshToken, Long idUsuario,
                            String nombre, String correo, Set<String> roles, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.roles = roles;
        this.expiresIn = expiresIn;
    }

    // Getters y Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public void setDebeCambiarPassword(boolean debeCambiarPassword) {
        this.debeCambiarPassword = debeCambiarPassword;
    }
}