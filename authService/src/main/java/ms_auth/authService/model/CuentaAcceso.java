package ms_auth.authService.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "CUENTA_ACCESO")
public class CuentaAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuenta_acceso_seq")
    @SequenceGenerator(name = "cuenta_acceso_seq", sequenceName = "seq_cuenta_acceso", allocationSize = 1)
    @Column(name = "ID_CUENTA")
    private Long id_cuenta;

    @Setter
    @Getter
    @Column(name = "RUT_USUARIO", nullable = false, length = 12, unique = true)
    private String rutUsuario;

    @Setter
    @Getter
    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Setter
    @Getter
    @Column(name = "TOKEN_RECUPERACION", length = 255)
    private String tokenRecuperacion;

    @Setter
    @Getter
    @ColumnDefault("'ACTIVO'")
    @Column(name = "ESTADO_CUENTA", length = 20)
    private String estadoCuenta;

    @Setter
    @Getter
    @Column(name = "ULTIMO_ACCESO")
    private LocalDateTime ultimoAcceso;

    // Constructores
    public CuentaAcceso() {}

    public CuentaAcceso(String rutUsuario, String passwordHash, String estadoCuenta) {
        this.rutUsuario = rutUsuario;
        this.passwordHash = passwordHash;
        this.estadoCuenta = estadoCuenta;
    }

    // Getters y Setters
    public Long getId() { return id_cuenta; }
    public void setId(Long id) { this.id_cuenta = id; }

}