
package servicios;

import modelo.Cliente;
import modelo.Monedero;
import modelo.Rango;
import modelo.Transaccion;

import estructuras.ListaEnlazada;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ==================================================
 *              CONTEXTO DE APLICACION
 * ==================================================
 *
 * PROYECTO:
 * Monedero Virtual con Sistema de Puntos.
 *
 * RESPONSABILIDAD:
 *
 * Centralizar la inicializacion y el acceso
 * a todos los servicios del sistema.
 *
 * El contexto permite compartir una unica
 * instancia de cada servicio entre:
 *
 * - Main.
 * - VentanaPrincipal.
 * - PanelInicio.
 * - PanelClientes.
 * - PanelMonederos.
 * - PanelTransacciones.
 * - PanelPuntos.
 * - PanelHistorial.
 * - PanelProgramadas.
 * - PanelReportes.
 *
 * ARQUITECTURA:
 *
 * Main
 *   |
 *   v
 * ContextoAplicacion
 *   |
 *   +-- ClienteService
 *   +-- MonederoService
 *   +-- TransaccionService
 *   +-- PuntosService
 *   +-- RangosService
 *   +-- ProgramacionService
 *   +-- NotificacionService
 *   +-- ReporteService
 *   +-- IntegridadService
 *   +-- CoordinadorOperaciones
 *
 * PRINCIPIOS:
 *
 * - Una sola instancia de cada servicio.
 * - Dependencias compartidas.
 * - Separacion entre logica y presentacion.
 * - No depende de Java Swing.
 * - No utiliza colecciones de Java.
 *
 * Java 21.
 */
public final class ContextoAplicacion {

    // ==========================================
    // INFORMACION GENERAL
    // ==========================================

    public static final String NOMBRE_APLICACION =
            "Monedero Virtual";

    public static final String NOMBRE_EDITORIAL =
            "Private Ledger";

    public static final String VERSION =
            "1.0.0";

    // ==========================================
    // SERVICIOS PRINCIPALES
    // ==========================================

    private final ClienteService clienteService;

    private final MonederoService monederoService;

    private final TransaccionService transaccionService;

    // ==========================================
    // PUNTOS Y RANGOS
    // ==========================================

    private final PuntosService puntosService;

    private final RangosService rangosService;

    // ==========================================
    // PROGRAMACIONES Y NOTIFICACIONES
    // ==========================================

    private final ProgramacionService programacionService;

    private final NotificacionService notificacionService;

    // ==========================================
    // REPORTES E INTEGRIDAD
    // ==========================================

    private final ReporteService reporteService;

    private final IntegridadService integridadService;

    // ==========================================
    // COORDINADOR DE OPERACIONES
    // ==========================================

    private final CoordinadorOperaciones coordinadorOperaciones;

    // ==========================================
    // ESTADO DE INICIALIZACION
    // ==========================================

    private final LocalDateTime fechaInicializacion;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    /**
     * Inicializa todos los servicios
     * utilizando dependencias compartidas.
     *
     * El contexto comienza sin clientes
     * ni transacciones registradas.
     *
     * Los datos de demostracion se cargan
     * mediante un metodo independiente.
     */
    public ContextoAplicacion() {

        fechaInicializacion =
                LocalDateTime.now();

        // --------------------------------------
        // 1. CLIENTES
        // --------------------------------------

        clienteService =
                new ClienteService();

        // --------------------------------------
        // 2. MONEDEROS
        // --------------------------------------

        monederoService =
                new MonederoService(
                        clienteService
                );

        // --------------------------------------
        // 3. TRANSACCIONES
        // --------------------------------------

        transaccionService =
                new TransaccionService(
                        clienteService,
                        monederoService
                );

        // --------------------------------------
        // 4. PUNTOS Y BENEFICIOS
        // --------------------------------------

        puntosService =
                new PuntosService(
                        clienteService
                );

        // --------------------------------------
        // 5. RANGOS, ABB Y AVL
        // --------------------------------------

        rangosService =
                new RangosService(
                        clienteService
                );

        // --------------------------------------
        // 6. TRANSACCIONES PROGRAMADAS
        // --------------------------------------

        programacionService =
                new ProgramacionService(
                        clienteService,
                        monederoService,
                        transaccionService
                );

        // --------------------------------------
        // 7. NOTIFICACIONES
        // --------------------------------------

        notificacionService =
                new NotificacionService(
                        clienteService
                );

        // --------------------------------------
        // 8. REPORTES
        // --------------------------------------

        reporteService =
                new ReporteService(
                        clienteService,
                        monederoService,
                        transaccionService,
                        puntosService,
                        rangosService,
                        programacionService,
                        notificacionService
                );

        // --------------------------------------
        // 9. AUDITORIA
        // --------------------------------------

        integridadService =
                new IntegridadService(
                        clienteService,
                        monederoService,
                        transaccionService,
                        puntosService,
                        rangosService,
                        programacionService,
                        notificacionService
                );

        // --------------------------------------
        // 10. COORDINADOR FINANCIERO
        // --------------------------------------

        coordinadorOperaciones =
                new CoordinadorOperaciones(
                        clienteService,
                        monederoService,
                        transaccionService,
                        puntosService,
                        notificacionService
                );
    }

    // ==========================================
    // GETTERS PRINCIPALES
    // ==========================================

    /**
     * Devuelve el servicio de clientes.
     */
    public ClienteService getClienteService() {

        return clienteService;
    }

    /**
     * Devuelve el servicio de monederos.
     */
    public MonederoService getMonederoService() {

        return monederoService;
    }

    /**
     * Devuelve el servicio de transacciones.
     */
    public TransaccionService getTransaccionService() {

        return transaccionService;
    }

    /**
     * Devuelve el servicio de puntos.
     */
    public PuntosService getPuntosService() {

        return puntosService;
    }

    /**
     * Devuelve el servicio de rangos.
     */
    public RangosService getRangosService() {

        return rangosService;
    }

    /**
     * Devuelve el servicio de programaciones.
     */
    public ProgramacionService getProgramacionService() {

        return programacionService;
    }

    /**
     * Devuelve el servicio de notificaciones.
     */
    public NotificacionService getNotificacionService() {

        return notificacionService;
    }

    /**
     * Devuelve el servicio de reportes.
     */
    public ReporteService getReporteService() {

        return reporteService;
    }

    /**
     * Devuelve el servicio de integridad.
     */
    public IntegridadService getIntegridadService() {

        return integridadService;
    }

    /**
     * Devuelve el coordinador de operaciones.
     */
    public CoordinadorOperaciones getCoordinadorOperaciones() {

        return coordinadorOperaciones;
    }

    // ==========================================
    // ALIAS PARA LA INTERFAZ
    // ==========================================

    /**
     * Estos metodos permiten que las vistas
     * utilicen nombres mas cortos.
     *
     * No crean nuevas instancias.
     */

    public ClienteService getClientes() {
        return clienteService;
    }

    public MonederoService getMonederos() {
        return monederoService;
    }

    public TransaccionService getTransacciones() {
        return transaccionService;
    }

    public PuntosService getPuntos() {
        return puntosService;
    }

    public RangosService getRangos() {
        return rangosService;
    }

    public ProgramacionService getProgramaciones() {
        return programacionService;
    }

    public NotificacionService getNotificaciones() {
        return notificacionService;
    }

    public ReporteService getReportes() {
        return reporteService;
    }

    public IntegridadService getIntegridad() {
        return integridadService;
    }

    public CoordinadorOperaciones getCoordinador() {
        return coordinadorOperaciones;
    }

    // ==========================================
    // INFORMACION DEL SISTEMA
    // ==========================================

    public String getNombreAplicacion() {

        return NOMBRE_APLICACION;
    }

    public String getNombreEditorial() {

        return NOMBRE_EDITORIAL;
    }

    public String getVersion() {

        return VERSION;
    }

    public LocalDateTime getFechaInicializacion() {

        return fechaInicializacion;
    }

    // ==========================================
    // ESTADISTICAS DE CLIENTES
    // ==========================================

    public int cantidadClientes() {

        return clienteService.cantidadClientes();
    }

    public int cantidadClientesActivos() {

        return clienteService.cantidadClientesActivos();
    }

    public int cantidadClientesInactivos() {

        return cantidadClientes()
                - cantidadClientesActivos();
    }

    // ==========================================
    // ESTADISTICAS DE MONEDEROS
    // ==========================================

    public int cantidadMonederos() {

        return monederoService.cantidadMonederos();
    }

    public int cantidadMonederosActivos() {

        return monederoService.cantidadMonederosActivos();
    }

    public int cantidadMonederosBloqueados() {

        return monederoService.cantidadMonederosBloqueados();
    }

    // ==========================================
    // ESTADISTICAS DE TRANSACCIONES
    // ==========================================

    public int cantidadTransacciones() {

        return transaccionService.cantidadTransacciones();
    }

    public int cantidadTransaccionesReversibles() {

        return transaccionService.cantidadReversibles();
    }

    // ==========================================
    // ESTADISTICAS FINANCIERAS
    // ==========================================

    public BigDecimal saldoTotalSistema() {

        return monederoService.saldoTotalSistema();
    }

    public BigDecimal comisionesTotales() {

        return transaccionService.consultarTotalComisiones();
    }

    // ==========================================
    // ESTADISTICAS DE PUNTOS
    // ==========================================

    public long totalPuntosSistema() {

        return puntosService.totalPuntosSistema();
    }

    public int cantidadBeneficiosCanjeados() {

        return puntosService.cantidadBeneficiosCanjeados();
    }

    public int cantidadClientesPorRango(
            Rango rango) {

        Objects.requireNonNull(
                rango,
                "El rango no puede ser null"
        );

        return rangosService.cantidadClientesPorRango(
                rango
        );
    }

    // ==========================================
    // ESTADISTICAS DE PROGRAMACIONES
    // ==========================================

    public int cantidadProgramaciones() {

        return programacionService.cantidadProgramaciones();
    }

    public int cantidadProgramacionesActivas() {

        return programacionService
                .cantidadProgramacionesActivas();
    }

    public int cantidadProgramacionesVencidas() {

        return programacionService.cantidadVencidas();
    }

    // ==========================================
    // ESTADISTICAS DE NOTIFICACIONES
    // ==========================================

    public int cantidadNotificaciones() {

        return notificacionService.cantidadNotificaciones();
    }

    public int cantidadNotificacionesNoLeidas() {

        return notificacionService.totalNoLeidasSistema();
    }

    // ==========================================
    // CONSULTAS DE CLIENTES
    // ==========================================

    public Cliente buscarCliente(
            String idCliente) {

        return clienteService.buscarCliente(
                idCliente
        );
    }

    public Cliente obtenerCliente(
            String idCliente) {

        return clienteService.obtenerCliente(
                idCliente
        );
    }

    public ListaEnlazada<Cliente> listarClientes() {

        return clienteService.listarClientes();
    }

    // ==========================================
    // CONSULTAS DE MONEDEROS
    // ==========================================

    public Monedero buscarMonedero(
            String idCliente,
            String idMonedero) {

        return monederoService.buscarMonedero(
                idCliente,
                idMonedero
        );
    }

    public BigDecimal consultarSaldo(
            String idCliente,
            String idMonedero) {

        return monederoService.consultarSaldo(
                idCliente,
                idMonedero
        );
    }

    public BigDecimal consultarSaldoCliente(
            String idCliente) {

        return monederoService
                .consultarSaldoTotalCliente(
                        idCliente
                );
    }

    // ==========================================
    // CONSULTAS DE TRANSACCIONES
    // ==========================================

    public Transaccion buscarTransaccion(
            String idTransaccion) {

        return transaccionService.buscarTransaccion(
                idTransaccion
        );
    }

    public ListaEnlazada<Transaccion>
    obtenerHistorialGeneral() {

        return transaccionService.obtenerHistorial();
    }

    public ListaEnlazada<Transaccion>
    obtenerHistorialCliente(
            String idCliente) {

        return transaccionService.obtenerHistorialCliente(
                idCliente
        );
    }

    public ListaEnlazada<Transaccion>
    obtenerUltimasTransacciones(
            int cantidad) {

        return transaccionService.obtenerUltimasTransacciones(
                cantidad
        );
    }

    // ==========================================
    // CONSULTAS DE RANKINGS
    // ==========================================

    public ListaEnlazada<Cliente>
    obtenerRankingClientes() {

        return rangosService.obtenerRankingDescendente();
    }

    public ListaEnlazada<Cliente>
    obtenerTopClientes(
            int cantidad) {

        return rangosService.obtenerTop(
                cantidad
        );
    }

    // ==========================================
    // REPORTES
    // ==========================================

    public String generarReporteGeneral() {

        return reporteService.generarReporteGeneral();
    }

    public String generarReporteClientes() {

        return reporteService.generarReporteClientes();
    }

    public String generarReporteMonederos() {

        return reporteService.generarReporteMonederos();
    }

    public String generarReporteTransacciones() {

        return reporteService.generarReporteTransacciones();
    }

    public String generarReportePuntos() {

        return reporteService.generarReportePuntos();
    }

    public String generarReporteRangos() {

        return reporteService.generarReporteRangos();
    }

    public String generarReporteRanking() {

        return reporteService.generarReporteRanking();
    }

    public String generarReporteProgramaciones() {

        return reporteService.generarReporteProgramaciones();
    }

    public String generarReporteNotificaciones() {

        return reporteService.generarReporteNotificaciones();
    }

    public String generarReporteCompleto() {

        return reporteService.generarReporteCompleto();
    }

    // ==========================================
    // AUDITORIAS
    // ==========================================

    /**
     * Ejecuta la auditoria completa
     * de los servicios configurados.
     */
    public IntegridadService.ResultadoAuditoria
    ejecutarAuditoriaCompleta() {

        return integridadService.ejecutarAuditoriaCompleta();
    }

    /**
     * Devuelve true cuando no se
     * detectan errores de integridad.
     */
    public boolean verificarIntegridad() {

        return integridadService.sistemaIntegro();
    }

    /**
     * Genera un informe textual
     * de todas las comprobaciones.
     */
    public String generarReporteIntegridad() {

        return integridadService.generarReporteIntegridad();
    }

    /**
     * Genera un resumen de la auditoria.
     */
    public String generarResumenIntegridad() {

        return integridadService.generarResumenIntegridad();
    }

    // ==========================================
    // DATOS DE DEMOSTRACION
    // ==========================================

    /**
     * Carga datos de demostracion
     * utilizando los servicios reales.
     *
     * Solamente se puede ejecutar
     * sobre un sistema vacio.
     *
     * Los movimientos se procesan mediante
     * CoordinadorOperaciones para generar
     * puntos y notificaciones.
     *
     * No elimina informacion existente.
     */
    public void cargarDatosDemostracion() {

        if (cantidadClientes() > 0
                || cantidadMonederos() > 0
                || cantidadTransacciones() > 0) {

            throw new IllegalStateException(
                    "La demostracion requiere "
                            + "un sistema vacio"
            );
        }

        // --------------------------------------
        // REGISTRO DE CLIENTES
        // --------------------------------------

        clienteService.registrarCliente(
                "C001",
                "Juan",
                "juan@ejemplo.com"
        );

        clienteService.registrarCliente(
                "C002",
                "Ana",
                "ana@ejemplo.com"
        );

        clienteService.registrarCliente(
                "C003",
                "Luis",
                "luis@ejemplo.com"
        );

        // --------------------------------------
        // CREACION DE MONEDEROS
        // --------------------------------------

        monederoService.crearMonederoPrincipal(
                "C001",
                "M001"
        );

        monederoService.crearMonederoPrincipal(
                "C002",
                "M002"
        );

        monederoService.crearMonederoPrincipal(
                "C003",
                "M003"
        );

        // --------------------------------------
        // DEPOSITOS
        // --------------------------------------

        coordinadorOperaciones.depositar(
                "C001",
                "M001",
                new BigDecimal("100000")
        );

        coordinadorOperaciones.depositar(
                "C002",
                "M002",
                new BigDecimal("50000")
        );

        coordinadorOperaciones.depositar(
                "C003",
                "M003",
                new BigDecimal("30000")
        );

        // --------------------------------------
        // TRANSFERENCIA
        // --------------------------------------

        coordinadorOperaciones.transferir(
                "C001",
                "M001",
                "C002",
                "M002",
                new BigDecimal("10000"),
                new BigDecimal("100")
        );

        // --------------------------------------
        // RETIRO
        // --------------------------------------

        coordinadorOperaciones.retirar(
                "C002",
                "M002",
                new BigDecimal("5000")
        );

        // --------------------------------------
        // PROGRAMACION FUTURA
        // --------------------------------------

        programacionService.programarDeposito(
                "C003",
                "M003",
                new BigDecimal("2000"),
                LocalDateTime.now().plusDays(3)
        );

        // --------------------------------------
        // AUDITORIA FINAL
        // --------------------------------------

        if (!verificarIntegridad()) {

            throw new IllegalStateException(
                    "Los datos de demostracion "
                            + "no superaron la auditoria"
            );
        }
    }

    // ==========================================
    // RESUMEN DEL SISTEMA
    // ==========================================

    /**
     * Genera un resumen de informacion
     * para la pantalla principal.
     */
    public String generarResumenSistema() {

        StringBuilder resultado =
                new StringBuilder();

        resultado.append(
                "========================================\n"
        );

        resultado.append(
                "MONEDERO VIRTUAL\n"
        );

        resultado.append(
                "PRIVATE LEDGER\n"
        );

        resultado.append(
                "========================================\n\n"
        );

        resultado.append(
                "Version: "
        ).append(VERSION).append("\n");

        resultado.append(
                "Clientes: "
        ).append(cantidadClientes()).append("\n");

        resultado.append(
                "Clientes activos: "
        ).append(cantidadClientesActivos()).append("\n");

        resultado.append(
                "Monederos: "
        ).append(cantidadMonederos()).append("\n");

        resultado.append(
                "Monederos activos: "
        ).append(cantidadMonederosActivos()).append("\n");

        resultado.append(
                "Transacciones: "
        ).append(cantidadTransacciones()).append("\n");

        resultado.append(
                "Saldo total: $"
        ).append(
                saldoTotalSistema().toPlainString()
        ).append("\n");

        resultado.append(
                "Puntos actuales: "
        ).append(totalPuntosSistema()).append("\n");

        resultado.append(
                "Beneficios canjeados: "
        ).append(cantidadBeneficiosCanjeados()).append("\n");

        resultado.append(
                "Programaciones: "
        ).append(cantidadProgramaciones()).append("\n");

        resultado.append(
                "Notificaciones: "
        ).append(cantidadNotificaciones()).append("\n");

        return resultado.toString();
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return NOMBRE_APLICACION
                + " / " + NOMBRE_EDITORIAL
                + " v" + VERSION
                + " | Clientes: " + cantidadClientes()
                + " | Monederos: " + cantidadMonederos()
                + " | Transacciones: "
                + cantidadTransacciones()
                + " | Saldo: $"
                + saldoTotalSistema().toPlainString()
                + " | Puntos: "
                + totalPuntosSistema();
    }
}
