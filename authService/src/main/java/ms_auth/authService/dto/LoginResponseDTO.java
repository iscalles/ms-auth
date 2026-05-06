package ms_auth.authService.dto;

public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String rutUsuario;
    private String tipoRol;
    private Long expiresIn;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String accessToken, String refreshToken, String rutUsuario, String tipoRol, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.rutUsuario = rutUsuario;
        this.tipoRol = tipoRol;
        this.expiresIn = expiresIn;
    }

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

    public String getRutUsuario() {
        return rutUsuario;
    }

    public void setRutUsuario(String rutUsuario) {
        this.rutUsuario = rutUsuario;
    }

    public String getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}