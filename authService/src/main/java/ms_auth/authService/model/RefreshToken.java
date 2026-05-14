package ms_auth.authService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REFRESH_TOKEN")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq")
    @SequenceGenerator(name = "refresh_token_seq", sequenceName = "SEQ_REFRESH_TOKEN", allocationSize = 1)
    @Column(name = "ID_TOKEN")
    private Long idToken;

    @Column(name = "TOKEN", nullable = false, length = 500)
    private String token;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "America/Santiago")
    @Column(name = "FECHA_EXPIRACION", nullable = false)
    private LocalDateTime fechaExpiracion;

    // Constructores
    public RefreshToken() {}

    public RefreshToken(String token, Long idUsuario, LocalDateTime fechaExpiracion) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.fechaExpiracion = fechaExpiracion;
    }

    // Getters y Setters
    public Long getIdToken() {
        return idToken;
    }

    public void setIdToken(Long idToken) {
        this.idToken = idToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
}