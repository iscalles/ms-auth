package ms_auth.authService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.remitente}")
    private String remitente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Envía la contraseña temporal al crear una cuenta. No lanza excepción si falla:
    // un correo no enviado no debe impedir que la cuenta quede creada y usable.
    // El login es por RUT (no por correo), así que el RUT es el dato de acceso real.
    public void enviarContrasenaInicial(String correoDestino, String nombre, String rutUsuario, String passwordTemporal) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(correoDestino);
            mensaje.setSubject("Acceso al Libro de Clases Digital - Colegio Bernardo O'Higgins");
            mensaje.setText(
                "Hola " + nombre + ",\n\n" +
                "Se creó tu cuenta de acceso al sistema del colegio. Para ingresar usa tu RUT, no tu correo.\n\n" +
                "RUT de acceso: " + rutUsuario + "\n" +
                "Contraseña temporal: " + passwordTemporal + "\n\n" +
                "Por seguridad, deberás definir una nueva contraseña la primera vez que inicies sesión.\n\n" +
                "Si no esperabas este correo, contacta al administrador del colegio.\n\n" +
                "Colegio Bernardo O'Higgins"
            );
            mailSender.send(mensaje);
            logger.info("Correo de bienvenida enviado a {}", correoDestino);
        } catch (Exception e) {
            logger.error("No se pudo enviar el correo de bienvenida a {}: {}", correoDestino, e.getMessage(), e);
        }
    }

    // Envía una contraseña temporal nueva cuando el usuario solicita recuperar su acceso.
    public void enviarRecuperacionPassword(String correoDestino, String nombre, String rutUsuario, String passwordTemporal) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(correoDestino);
            mensaje.setSubject("Recuperación de contraseña - Colegio Bernardo O'Higgins");
            mensaje.setText(
                "Hola " + nombre + ",\n\n" +
                "Solicitaste recuperar el acceso a tu cuenta del Libro de Clases Digital. " +
                "Te generamos una contraseña temporal para que puedas volver a entrar.\n\n" +
                "RUT de acceso: " + rutUsuario + "\n" +
                "Contraseña temporal: " + passwordTemporal + "\n\n" +
                "Por seguridad, deberás definir una nueva contraseña la primera vez que inicies sesión con esta.\n\n" +
                "Si no fuiste tú quien solicitó esto, contacta de inmediato al administrador del colegio.\n\n" +
                "Colegio Bernardo O'Higgins"
            );
            mailSender.send(mensaje);
            logger.info("Correo de recuperación enviado a {}", correoDestino);
        } catch (Exception e) {
            logger.error("No se pudo enviar el correo de recuperación a {}: {}", correoDestino, e.getMessage(), e);
        }
    }
}
