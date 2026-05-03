package ms_auth.authService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq")
    @SequenceGenerator(name = "refresh_token_seq", sequenceName = "seq_refresh_token", allocationSize = 1)
    private Long id_token;

    @Column(name = "TOKEN", nullable = false, length = 500)
    private String token;

    @Column(name = "RUT_USUARIO", nullable = false, length = 12)
    private String rutUsuario;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm",timezone = "America/Santiago")
    @Column(name = "FECHA_EXPIRACION", nullable = false)
    private LocalDateTime fechaExpiracion;

    public RefreshToken() {
    }

    public RefreshToken(Long id_token, String token, String rutUsuario, LocalDateTime fechaExpiracion) {
        this.id_token = id_token;
        this.token = token;
        this.rutUsuario = rutUsuario;
        this.fechaExpiracion = fechaExpiracion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getRutUsuario() {
        return rutUsuario;
    }

    public void setRutUsuario(String rutUsuario) {
        this.rutUsuario = rutUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id_token;
    }

    public void setId(Long id) {
        this.id_token = id;
    }
}
