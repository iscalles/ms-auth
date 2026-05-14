package ms_auth.authService.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUENTA_ACCESO")
public class CuentaAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuenta_acceso_seq")
    @SequenceGenerator(name = "cuenta_acceso_seq", sequenceName = "SEQ_CUENTA_ACCESO", allocationSize = 1)
    @Column(name = "ID_CUENTA")
    private Long idCuenta;

    @Column(name = "ID_USUARIO", nullable = false, unique = true)
    private Long idUsuario;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "TOKEN_RECUPERACION", length = 255)
    private String tokenRecuperacion;

    @Column(name = "ESTADO_CUENTA", length = 20)
    private String estadoCuenta;

    @Column(name = "ULTIMO_ACCESO")
    private LocalDateTime ultimoAcceso;

    // Constructores
    public CuentaAcceso() {}

    public CuentaAcceso(Long idUsuario, String passwordHash, String estadoCuenta) {
        this.idUsuario = idUsuario;
        this.passwordHash = passwordHash;
        this.estadoCuenta = estadoCuenta;
    }

    // Getters y Setters
    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTokenRecuperacion() {
        return tokenRecuperacion;
    }

    public void setTokenRecuperacion(String tokenRecuperacion) {
        this.tokenRecuperacion = tokenRecuperacion;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}