package ms_auth.authService.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
public class CuentaAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuenta_acceso_seq")
    @SequenceGenerator(name = "acceso_cuenta_seq", sequenceName = "seq_cuenta_acceso", allocationSize = 1)
    private Long id_cuenta;

    @Column(name = "RUT_USUARIO", nullable = false, length = 12)
    private String rutUsuario;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "TOKEN_RECUPERACION", length = 100)
    private String tokenRecuperacion;

    @ColumnDefault("'ACTIVO'")
    @Column(name = "ESTADO_CUENTA", length = 20)
    private String estadoCuenta;

    @Column(name = "ULTIMO_ACCESO")
    private LocalDateTime ultimoAcceso;

    public CuentaAcceso() {
    }

    public CuentaAcceso(Long id_cuenta, String rutUsuario, String passwordHash, String tokenRecuperacion, String estadoCuenta, LocalDateTime ultimoAcceso) {
        this.id_cuenta = id_cuenta;
        this.rutUsuario = rutUsuario;
        this.passwordHash = passwordHash;
        this.tokenRecuperacion = tokenRecuperacion;
        this.estadoCuenta = estadoCuenta;
        this.ultimoAcceso = ultimoAcceso;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public String getTokenRecuperacion() {
        return tokenRecuperacion;
    }

    public void setTokenRecuperacion(String tokenRecuperacion) {
        this.tokenRecuperacion = tokenRecuperacion;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRutUsuario() {
        return rutUsuario;
    }

    public void setRutUsuario(String rutUsuario) {
        this.rutUsuario = rutUsuario;
    }

    public Long getId() {
        return id_cuenta;
    }

    public void setId(Long id) {
        this.id_cuenta = id;
    }
}
