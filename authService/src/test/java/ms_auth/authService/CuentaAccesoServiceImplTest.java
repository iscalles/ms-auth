package ms_auth.authService;

import ms_auth.authService.client.UsuarioClient;
import ms_auth.authService.model.CuentaAcceso;
import ms_auth.authService.repository.CuentaAccesoRepository;
import ms_auth.authService.repository.RefreshTokenRepository;
import ms_auth.authService.service.EmailService;
import ms_auth.authService.service.PasswordService;
import ms_auth.authService.service.impl.CuentaAccesoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaAccesoServiceImplTest {

    @Mock private CuentaAccesoRepository cuentaAccesoRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordService passwordService;
    @Mock private UsuarioClient usuarioClient;
    @Mock private EmailService emailService;

    private CuentaAccesoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CuentaAccesoServiceImpl(
                cuentaAccesoRepository, refreshTokenRepository,
                passwordService, usuarioClient, emailService);
    }

    // ── inicializarCuenta ─────────────────────────────────────────────────────

    @Test
    void inicializar_cuentaYaExiste_lanzaExcepcion() {
        when(cuentaAccesoRepository.findByIdUsuario(1L))
                .thenReturn(Optional.of(new CuentaAcceso()));

        assertThatThrownBy(() -> service.inicializarCuenta(1L, "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya tiene una cuenta");
    }

    @Test
    void inicializar_nueva_creaConEstadoActivo() {
        when(cuentaAccesoRepository.findByIdUsuario(1L)).thenReturn(Optional.empty());
        when(passwordService.encriptarPassword("pass123")).thenReturn("$hash$");
        when(cuentaAccesoRepository.save(any())).thenAnswer(inv -> {
            CuentaAcceso c = inv.getArgument(0);
            c.setIdCuenta(1L);
            return c;
        });
        when(usuarioClient.obtenerUsuarioInterno(1L)).thenThrow(new RuntimeException("no disponible"));

        CuentaAcceso resultado = service.inicializarCuenta(1L, "pass123");

        assertThat(resultado.getEstadoCuenta()).isEqualTo("ACTIVO");
        assertThat(resultado.isDebeCambiarPassword()).isTrue();
        assertThat(resultado.getPasswordHash()).isEqualTo("$hash$");
        verify(cuentaAccesoRepository).save(any(CuentaAcceso.class));
    }

    // ── cambiarContrasena ─────────────────────────────────────────────────────

    @Test
    void cambiarPassword_usuarioSinCuenta_lanzaExcepcion() {
        when(cuentaAccesoRepository.findByIdUsuario(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.cambiarContrasena(99L, "vieja", "nueva"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontró una cuenta");
    }

    @Test
    void cambiarPassword_passwordActualIncorrecto_lanzaExcepcion() {
        CuentaAcceso cuenta = cuenta(1L, "$hashViejo$", "ACTIVO");
        when(cuentaAccesoRepository.findByIdUsuario(1L)).thenReturn(Optional.of(cuenta));
        when(passwordService.validarPassword("wrongPass", "$hashViejo$")).thenReturn(false);

        assertThatThrownBy(() -> service.cambiarContrasena(1L, "wrongPass", "nueva"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("contraseña actual es incorrecta");
    }

    @Test
    void cambiarPassword_correcto_guardaHashNuevoYLimpiaFlag() {
        CuentaAcceso cuenta = cuenta(1L, "$hashViejo$", "ACTIVO");
        cuenta.setDebeCambiarPassword(true);
        when(cuentaAccesoRepository.findByIdUsuario(1L)).thenReturn(Optional.of(cuenta));
        when(passwordService.validarPassword("viejaPass", "$hashViejo$")).thenReturn(true);
        when(passwordService.encriptarPassword("nuevaPass")).thenReturn("$hashNuevo$");

        service.cambiarContrasena(1L, "viejaPass", "nuevaPass");

        assertThat(cuenta.getPasswordHash()).isEqualTo("$hashNuevo$");
        assertThat(cuenta.isDebeCambiarPassword()).isFalse();
        verify(cuentaAccesoRepository).save(cuenta);
    }

    // ── desactivarCuentaPorUsuario ────────────────────────────────────────────

    @Test
    void desactivar_cuentaExiste_setEliminadoYRevocaTokens() {
        CuentaAcceso cuenta = cuenta(1L, "$hash$", "ACTIVO");
        when(cuentaAccesoRepository.findByIdUsuario(1L)).thenReturn(Optional.of(cuenta));

        service.desactivarCuentaPorUsuario(1L);

        assertThat(cuenta.getEstadoCuenta()).isEqualTo("ELIMINADO");
        verify(cuentaAccesoRepository).save(cuenta);
        verify(refreshTokenRepository).deleteByIdUsuario(1L);
    }

    @Test
    void desactivar_cuentaNoExiste_noHaceNada() {
        when(cuentaAccesoRepository.findByIdUsuario(99L)).thenReturn(Optional.empty());

        service.desactivarCuentaPorUsuario(99L);

        verify(cuentaAccesoRepository, never()).save(any());
        verify(refreshTokenRepository, never()).deleteByIdUsuario(anyLong());
    }

    // ── actualizarCuentaAcceso ────────────────────────────────────────────────

    @Test
    void actualizar_noExiste_lanzaExcepcion() {
        when(cuentaAccesoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizarCuentaAcceso(new CuentaAcceso(), 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void eliminar_existente_invocaDelete() {
        CuentaAcceso c = cuenta(1L, "$hash$", "ACTIVO");
        when(cuentaAccesoRepository.findById(1L)).thenReturn(Optional.of(c));

        service.eliminarCuentaAcceso(1L);
        verify(cuentaAccesoRepository).delete(c);
    }

    @Test
    void eliminar_noExiste_lanzaExcepcion() {
        when(cuentaAccesoRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminarCuentaAcceso(5L))
                .isInstanceOf(RuntimeException.class);
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private CuentaAcceso cuenta(Long id, String hash, String estado) {
        CuentaAcceso c = new CuentaAcceso();
        c.setIdCuenta(id);
        c.setIdUsuario(id);
        c.setPasswordHash(hash);
        c.setEstadoCuenta(estado);
        return c;
    }
}
