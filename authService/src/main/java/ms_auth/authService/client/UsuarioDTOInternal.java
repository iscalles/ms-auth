package ms_auth.authService.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public class UsuarioDTOInternal {
    private Long idUsuario;
    private String rutUsuario;

    @JsonProperty("nombreUsuario")
    private String nombre;

    @JsonProperty("primerApellidoUsuario")
    private String primerApellido;

    @JsonProperty("segundoApellidoUsuario")
    private String segundoApellido;

    @JsonProperty("correoUsuario")
    private String correo;

    private Set<String> roles;

    public UsuarioDTOInternal() {}

    // Getters
    public Long getIdUsuario() { return idUsuario; }
    public String getRutUsuario() { return rutUsuario; }
    public String getNombre() { return nombre; }

    public String getApellidos() {
        return (primerApellido != null ? primerApellido : "") + " " + (segundoApellido != null ? segundoApellido : "");
    }

    public String getCorreo() { return correo; }
    public Set<String> getRoles() { return roles; }

    // Setters
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public void setRutUsuario(String rutUsuario) { this.rutUsuario = rutUsuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}