package ms_auth.authService.client;

import java.util.Set;

public class UsuarioDTOInternal {
    private Long idUsuario;
    private String rutUsuario;
    private String nombre;
    private String apellidos;
    private String correo;
    private Set<String> roles;

    public UsuarioDTOInternal() {}

    public UsuarioDTOInternal(Long idUsuario, String rutUsuario, String nombre,
                              String apellidos, String correo, Set<String> roles) {
        this.idUsuario = idUsuario;
        this.rutUsuario = rutUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.roles = roles;
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRutUsuario() {
        return rutUsuario;
    }

    public void setRutUsuario(String rutUsuario) {
        this.rutUsuario = rutUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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
}