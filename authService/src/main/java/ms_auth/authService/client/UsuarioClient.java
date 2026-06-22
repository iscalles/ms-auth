package ms_auth.authService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "ms-usuario",
        url = "${ms-usuario.url:http://localhost:8081}"
)
public interface UsuarioClient {

    /*Obtiene usuario interno por RUT*/
    @GetMapping("/usuario/interno/rut/{rut}")
    UsuarioDTOInternal obtenerUsuarioPorRut(@PathVariable("rut") String rutUsuario);

    /*Obtiene datos del usuario por ID*/
    @GetMapping("/usuario/interno/{idUsuario}")
    UsuarioDTOInternal obtenerUsuarioInterno(@PathVariable("idUsuario") Long idUsuario);

    /*Obtiene usuario interno por correo (para recuperación de contraseña)*/
    @GetMapping("/usuario/interno/correo/{correo}")
    UsuarioDTOInternal obtenerUsuarioPorCorreo(@PathVariable("correo") String correo);
}