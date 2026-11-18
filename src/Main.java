
import modelo.Cliente;

import servicios.ContextoAplicacion;

import vista.VentanaPrincipal;
import vista.tema.TemaAplicacion;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.GraphicsEnvironment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * PRODUCTO:
 * MONEDERO / PRIVATE LEDGER
 *
 * SISTEMA VISUAL:
 * Institutional Noir
 *
 * PROYECTO:
 * Proyecto Final de Estructuras de Datos
 *
 * CLASE:
 * Main.java
 *
 * RESPONSABILIDADES:
 *
 * 1. Iniciar la aplicacion.
 * 2. Crear un contexto financiero compartido.
 * 3. Inicializar los servicios del sistema.
 * 4. Permitir un escenario de demostracion.
 * 5. Ejecutar la auditoria inicial.
 * 6. Inicializar el tema Institutional Noir.
 * 7. Construir VentanaPrincipal.
 * 8. Abrir la interfaz grafica.
 * 9. Gestionar errores de inicializacion.
 *
 * ARQUITECTURA:
 *
 * Main
 *   |
 *   +-- ContextoAplicacion
 *   |      |
 *   |      +-- ClienteService
 *   |      +-- MonederoService
 *   |      +-- TransaccionService
 *   |      +-- PuntosService
 *   |      +-- RangosService
 *   |      +-- ProgramacionService
 *   |      +-- NotificacionService
 *   |      +-- ReporteService
 *   |      +-- IntegridadService
 *   |      +-- CoordinadorOperaciones
 *   |
 *   +-- TemaAplicacion
 *   |
 *   +-- VentanaPrincipal
 *          |
 *          +-- Sidebar
 *          +-- PanelInicio
 *          +-- CardLayout
 *
 * IMPORTANTE:
 *
 * Se crea una sola instancia de
 * ContextoAplicacion.
 *
 * Todas las pantallas comparten
 * los mismos servicios financieros.
 *
 * El modo normal comienza sin cargar
 * datos de demostracion.
 *
 * El argumento --demo crea un escenario
 * utilizando los servicios reales.
 *
 * Java 21.
 */
public final class Main {

    // ========================================================
    // 1. INFORMACION GENERAL
    // ========================================================

    public static final String NOMBRE_APLICACION =
            "MONEDERO / PRIVATE LEDGER";

    public static final String VERSION =
            "1.0.0";

    public static final String NOMBRE_TEMA =
            "Institutional Noir";

    // ========================================================
    // 2. CONTEXTO COMPARTIDO
    // ========================================================

    /**
     * Esta referencia permite acceder
     * al contexto creado durante el inicio.
     *
     * No debe reemplazarse durante
     * la ejecucion normal.
     */
    private static volatile ContextoAplicacion contexto;

    // ========================================================
    // 3. CONSTRUCTOR PRIVADO
    // ========================================================

    private Main() {

        throw new UnsupportedOperationException(
                "Main no puede instanciarse"
        );
    }

    // ========================================================
    // 4. PUNTO DE ENTRADA
    // ========================================================

    /**
     * Inicia el sistema financiero
     * y posteriormente la interfaz Swing.
     *
     * Argumentos:
     *
     * Sin argumentos:
     * Inicializa el sistema sin datos demo.
     *
     * --demo:
     * Carga clientes y operaciones
     * de demostracion.
     */
    public static void main(
            String[] args) {

        imprimirEncabezado();

        try {

            // ----------------------------------
            // PASO 1:
            // CREAR CONTEXTO FINANCIERO
            // ----------------------------------

            System.out.println(
                    "[INICIO] Inicializando servicios..."
            );

            ContextoAplicacion nuevoContexto =
                    new ContextoAplicacion();

            // ----------------------------------
            // PASO 2:
            // COMPROBAR SERVICIOS
            // ----------------------------------

            comprobarServicios(
                    nuevoContexto
            );

            // ----------------------------------
            // PASO 3:
            // DATOS DE DEMOSTRACION
            // ----------------------------------

            boolean modoDemo =
                    contieneArgumento(
                            args,
                            "--demo"
                    );

            if (modoDemo) {

                System.out.println(
                        "[DEMO] Cargando escenario..."
                );

                cargarDatosDemostracion(
                        nuevoContexto
                );

                System.out.println(
                        "[OK] Escenario demo cargado"
                );

            } else {

                System.out.println(
                        "[INICIO] Modo normal"
                );

                System.out.println(
                        "[INICIO] Sin datos demo"
                );
            }

            // ----------------------------------
            // PASO 4:
            // AUDITORIA DE INTEGRIDAD
            // ----------------------------------

            System.out.println(
                    "[AUDITORIA] Verificando sistema..."
            );

            boolean integro =
                    nuevoContexto
                            .getIntegridadService()
                            .sistemaIntegro();

            if (!integro) {

                throw new IllegalStateException(
                        "La auditoria inicial detecto "
                                + "inconsistencias en el sistema."
                );
            }

            System.out.println(
                    "[OK] Auditoria inicial aprobada"
            );

            // ----------------------------------
            // PASO 5:
            // PUBLICAR CONTEXTO COMPARTIDO
            // ----------------------------------

            contexto = nuevoContexto;

            imprimirResumen(
                    nuevoContexto,
                    modoDemo
            );

            // ----------------------------------
            // PASO 6:
            // INICIAR INTERFAZ SWING
            // ----------------------------------

            if (GraphicsEnvironment.isHeadless()) {

                throw new IllegalStateException(
                        "No hay un entorno grafico "
                                + "disponible para iniciar Swing."
                );
            }

            System.out.println(
                    "[INTERFAZ] Preparando Institutional Noir..."
            );

            SwingUtilities.invokeLater(() -> {

                iniciarInterfaz(
                        nuevoContexto
                );
            });

        } catch (Exception error) {

            gestionarErrorInicio(
                    error
            );
        }
    }

    // ========================================================
    // 5. COMPROBAR SERVICIOS
    // ========================================================

    /**
     * Comprueba que ContextoAplicacion
     * haya construido las dependencias
     * necesarias para el sistema.
     */
    private static void comprobarServicios(
            ContextoAplicacion contexto) {

        Objects.requireNonNull(
                contexto,
                "El contexto no puede ser null"
        );

        Objects.requireNonNull(
                contexto.getClienteService(),
                "ClienteService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getMonederoService(),
                "MonederoService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getTransaccionService(),
                "TransaccionService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getPuntosService(),
                "PuntosService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getRangosService(),
                "RangosService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getProgramacionService(),
                "ProgramacionService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getNotificacionService(),
                "NotificacionService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getReporteService(),
                "ReporteService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getIntegridadService(),
                "IntegridadService no inicializado"
        );

        Objects.requireNonNull(
                contexto.getCoordinador(),
                "CoordinadorOperaciones no inicializado"
        );

        System.out.println(
                "[OK] Servicios inicializados"
        );
    }

    // ========================================================
    // 6. INICIAR INTERFAZ GRAFICA
    // ========================================================

    /**
     * Construye la interfaz definitiva.
     *
     * Debe ejecutarse dentro del EDT.
     */
    private static void iniciarInterfaz(
            ContextoAplicacion contextoCompartido) {

        try {

            // ----------------------------------
            // INSTALAR TEMA
            // ----------------------------------

            TemaAplicacion.inicializar();

            System.out.println(
                    "[OK] Tema: "
                            + TemaAplicacion.getNombreTema()
            );

            // ----------------------------------
            // CONSTRUIR VENTANA
            // ----------------------------------

            VentanaPrincipal ventana =
                    new VentanaPrincipal(
                            contextoCompartido
                    );

            // ----------------------------------
            // MOSTRAR DASHBOARD
            // ----------------------------------

            ventana.mostrar();

            System.out.println(
                    "[OK] VentanaPrincipal iniciada"
            );

            System.out.println(
                    "[OK] Dashboard disponible"
            );

            System.out.println(
                    "[OK] Navegacion habilitada"
            );

            System.out.println();

            System.out.println(
                    "MONEDERO / PRIVATE LEDGER"
            );

            System.out.println(
                    "Institutional Noir"
            );

        } catch (Exception error) {

            gestionarErrorInicio(
                    error
            );
        }
    }

    // ========================================================
    // 7. DATOS DE DEMOSTRACION
    // ========================================================

    /**
     * Carga un escenario utilizando
     * los servicios reales.
     *
     * Solo se ejecuta cuando el usuario
     * proporciona el argumento --demo.
     *
     * Las operaciones pasan por
     * CoordinadorOperaciones para aplicar
     * las reglas financieras establecidas.
     *
     * No utiliza balances ficticios
     * directamente en la interfaz.
     */
    private static void cargarDatosDemostracion(
            ContextoAplicacion contexto) {

        Objects.requireNonNull(
                contexto,
                "El contexto es obligatorio"
        );

        // --------------------------------------
        // CLIENTES DE DEMOSTRACION
        // --------------------------------------

        Cliente cliente1 =
                contexto.getClienteService()
                        .registrarCliente(
                                "C001",
                                "Juan",
                                "juan@ejemplo.com"
                        );

        Cliente cliente2 =
                contexto.getClienteService()
                        .registrarCliente(
                                "C002",
                                "Ana",
                                "ana@ejemplo.com"
                        );

        Cliente cliente3 =
                contexto.getClienteService()
                        .registrarCliente(
                                "C003",
                                "Luis",
                                "luis@ejemplo.com"
                        );

        // --------------------------------------
        // MONEDEROS PRINCIPALES
        // --------------------------------------

        contexto.getMonederoService()
                .crearMonederoPrincipal(
                        cliente1.getId(),
                        "M001"
                );

        contexto.getMonederoService()
                .crearMonederoPrincipal(
                        cliente2.getId(),
                        "M002"
                );

        contexto.getMonederoService()
                .crearMonederoPrincipal(
                        cliente3.getId(),
                        "M003"
                );

        // --------------------------------------
        // DEPOSITOS
        // --------------------------------------

        contexto.getCoordinador()
                .depositar(
                        "C001",
                        "M001",
                        new BigDecimal("100000.00")
                );

        contexto.getCoordinador()
                .depositar(
                        "C002",
                        "M002",
                        new BigDecimal("50000.00")
                );

        contexto.getCoordinador()
                .depositar(
                        "C003",
                        "M003",
                        new BigDecimal("30000.00")
                );

        // --------------------------------------
        // TRANSFERENCIA
        // --------------------------------------

        contexto.getCoordinador()
                .transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        new BigDecimal("10000.00"),
                        new BigDecimal("100.00")
                );

        // --------------------------------------
        // RETIRO
        // --------------------------------------

        contexto.getCoordinador()
                .retirar(
                        "C002",
                        "M002",
                        new BigDecimal("5000.00")
                );

        // --------------------------------------
        // PROGRAMACION FUTURA
        // --------------------------------------

        contexto.getProgramacionService()
                .programarDeposito(
                        "C003",
                        "M003",
                        new BigDecimal("2000.00"),
                        LocalDateTime.now().plusDays(3)
                );

        System.out.println(
                "[DEMO] Clientes y operaciones registrados"
        );
    }

    // ========================================================
    // 8. RESUMEN DEL ARRANQUE
    // ========================================================

    private static void imprimirResumen(
            ContextoAplicacion contexto,
            boolean modoDemo) {

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       RESUMEN DEL SISTEMA"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Modo: "
                        + (modoDemo
                        ? "DEMOSTRACION"
                        : "NORMAL")
        );

        System.out.println(
                "Clientes: "
                        + contexto.getClienteService()
                        .cantidadClientes()
        );

        System.out.println(
                "Monederos: "
                        + contexto.getClienteService()
                        .cantidadMonederos()
        );

        System.out.println(
                "Transacciones: "
                        + contexto.getTransaccionService()
                        .cantidadTransacciones()
        );

        System.out.println(
                "Puntos acumulados: "
                        + contexto.getPuntosService()
                        .totalPuntosSistema()
        );

        System.out.println(
                "Saldo consolidado: "
                        + contexto.getClienteService()
                        .saldoTotalSistema()
        );

        System.out.println(
                "Programaciones: "
                        + contexto.getProgramacionService()
                        .cantidadProgramaciones()
        );

        System.out.println(
                "========================================"
        );

        System.out.println();
    }

    // ========================================================
    // 9. ACCESO AL CONTEXTO
    // ========================================================

    /**
     * Devuelve el contexto compartido.
     *
     * Si la aplicacion no se ha iniciado,
     * lanza una excepcion.
     */
    public static ContextoAplicacion getContexto() {

        ContextoAplicacion actual =
                contexto;

        if (actual == null) {

            throw new IllegalStateException(
                    "El contexto aun no esta inicializado"
            );
        }

        return actual;
    }

    // ========================================================
    // 10. ARGUMENTOS
    // ========================================================

    private static boolean contieneArgumento(
            String[] argumentos,
            String buscado) {

        if (argumentos == null
                || buscado == null) {

            return false;
        }

        for (String argumento : argumentos) {

            if (buscado.equalsIgnoreCase(
                    argumento
            )) {

                return true;
            }
        }

        return false;
    }

    // ========================================================
    // 11. GESTION DE ERRORES
    // ========================================================

    /**
     * Registra el error y muestra
     * un mensaje visual cuando existe
     * entorno grafico.
     */
    private static void gestionarErrorInicio(
            Exception error) {

        System.err.println();

        System.err.println(
                "[ERROR] No fue posible iniciar "
                        + NOMBRE_APLICACION
        );

        error.printStackTrace();

        if (GraphicsEnvironment.isHeadless()) {

            return;
        }

        Runnable mostrarError = () -> {

            String mensaje =
                    error.getMessage() == null
                            ? error.getClass()
                            .getSimpleName()
                            : error.getMessage();

            JOptionPane.showMessageDialog(
                    null,
                    "No fue posible iniciar "
                            + "MONEDERO / PRIVATE LEDGER."
                            + "\n\n"
                            + mensaje
                            + "\n\n"
                            + "Consulta la consola "
                            + "para obtener mas detalles.",
                    "Error de inicializacion",
                    JOptionPane.ERROR_MESSAGE
            );
        };

        if (SwingUtilities.isEventDispatchThread()) {

            mostrarError.run();

        } else {

            SwingUtilities.invokeLater(
                    mostrarError
            );
        }
    }

    // ========================================================
    // 12. ENCABEZADO DE CONSOLA
    // ========================================================

    private static void imprimirEncabezado() {

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "          MONEDERO VIRTUAL"
        );

        System.out.println(
                "             PRIVATE LEDGER"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                " VERSION : " + VERSION
        );

        System.out.println(
                " TEMA    : " + NOMBRE_TEMA
        );

        System.out.println(
                " JAVA    : "
                        + System.getProperty(
                        "java.version"
                )
        );

        System.out.println(
                "========================================"
        );

        System.out.println();
    }
}
