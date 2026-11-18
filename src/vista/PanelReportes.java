
package vista;

import servicios.ContextoAplicacion;

import vista.componentes.BotonPremium;
import vista.componentes.EncabezadoPanel;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import java.awt.datatransfer.StringSelection;

import java.io.IOException;

import java.math.BigDecimal;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.text.NumberFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.Objects;

/**
 * ============================================================
 *                   MONEDERO VIRTUAL
 *                     PRIVATE LEDGER
 * ============================================================
 *
 * CLASE:
 * PanelReportes.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir
 *
 * RESPONSABILIDADES:
 *
 * 1. Consultar indicadores financieros.
 * 2. Generar reportes generales.
 * 3. Ejecutar auditorias de integridad.
 * 4. Mostrar los resultados.
 * 5. Exportar reportes a TXT.
 * 6. Copiar informes al portapapeles.
 * 7. Actualizar informacion del sistema.
 *
 * ARQUITECTURA:
 *
 * VentanaPrincipal
 *       |
 *       v
 * PanelReportes
 *       |
 *       v
 * ContextoAplicacion
 *       |
 *       +-- ClienteService
 *       +-- MonederoService
 *       +-- TransaccionService
 *       +-- PuntosService
 *       +-- ProgramacionService
 *       +-- ReporteService
 *       +-- IntegridadService
 *
 * IMPORTANTE:
 *
 * Este modulo es de consulta.
 *
 * No modifica saldos.
 * No registra transacciones.
 * No modifica puntos.
 * No ejecuta programaciones.
 *
 * Los reportes se generan utilizando
 * los servicios existentes.
 *
 * Java 21.
 */
public final class PanelReportes extends JPanel {

    private static final long serialVersionUID = 1L;

    // ========================================================
    // 1. CONSTANTES
    // ========================================================

    private static final String TITULO =
            "Reportes.";

    private static final String SUBTITULO =
            "Analisis financiero, informes y auditoria.";

    private static final String IDENTIFICADOR =
            "WORKSPACE / 008";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    private static final DateTimeFormatter FORMATO_ARCHIVO =
            DateTimeFormatter.ofPattern(
                    "yyyyMMdd_HHmmss"
            );

    // ========================================================
    // 2. TIPO DE INFORME
    // ========================================================

    public enum TipoInforme {

        NINGUNO,
        GENERAL,
        AUDITORIA
    }

    // ========================================================
    // 3. CONTEXTO COMPARTIDO
    // ========================================================

    private final ContextoAplicacion contexto;

    // ========================================================
    // 4. COMPONENTES PRINCIPALES
    // ========================================================

    private EncabezadoPanel encabezado;

    private JPanel contenido;

    private JPanel columna;

    private JPanel panelIndicadores;

    private JPanel panelAcciones;

    private JPanel panelVisor;

    // ========================================================
    // 5. INDICADORES
    // ========================================================

    private JLabel indicadorSaldo;

    private JLabel indicadorClientes;

    private JLabel indicadorMonederos;

    private JLabel indicadorTransacciones;

    private JLabel indicadorPuntos;

    private JLabel indicadorProgramaciones;

    // ========================================================
    // 6. VISOR DE INFORMES
    // ========================================================

    private JTextArea areaReporte;

    private JLabel etiquetaTipoInforme;

    private JLabel etiquetaFechaInforme;

    private JLabel etiquetaEstado;

    private JLabel etiquetaAuditoria;

    // ========================================================
    // 7. ESTADO
    // ========================================================

    private TipoInforme tipoActual;

    private String reporteActual;

    private boolean actualizando;

    private Boolean ultimaAuditoria;

    // ========================================================
    // 8. CONSTRUCTOR
    // ========================================================

    public PanelReportes(
            ContextoAplicacion contexto) {

        super(new BorderLayout());

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "ContextoAplicacion no puede ser null"
                );

        this.tipoActual =
                TipoInforme.NINGUNO;

        this.reporteActual = "";

        this.actualizando = false;

        this.ultimaAuditoria = null;

        inicializar();

        refrescarDatos();
    }

    // ========================================================
    // 9. INICIALIZACION
    // ========================================================

    private void inicializar() {

        setOpaque(true);

        setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        contenido =
                new JPanel(
                        new BorderLayout()
                );

        contenido.setOpaque(true);

        contenido.setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        contenido.setBorder(
                Espaciado.bordeContenido()
        );

        columna =
                new JPanel();

        columna.setOpaque(false);

        columna.setLayout(
                new BoxLayout(
                        columna,
                        BoxLayout.Y_AXIS
                )
        );

        construirEncabezado();

        construirIndicadores();

        construirAcciones();

        construirVisor();

        agregarBloque(
                encabezado
        );

        agregarBloque(
                panelIndicadores
        );

        agregarBloque(
                panelAcciones
        );

        agregarBloque(
                panelVisor
        );

        contenido.add(
                columna,
                BorderLayout.NORTH
        );

        JScrollPane scroll =
                new JScrollPane(
                        contenido,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.getViewport().setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(18);

        add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 10. AGREGAR BLOQUES
    // ========================================================

    private void agregarBloque(
            JPanel bloque) {

        bloque.setAlignmentX(
                LEFT_ALIGNMENT
        );

        if (columna.getComponentCount() > 0) {

            columna.add(
                    Box.createVerticalStrut(24)
            );
        }

        columna.add(
                bloque
        );
    }

    // ========================================================
    // 11. ENCABEZADO
    // ========================================================

    private void construirEncabezado() {

        encabezado =
                new EncabezadoPanel(
                        TITULO,
                        SUBTITULO,
                        IDENTIFICADOR,
                        EncabezadoPanel.Variante.NORMAL
                );

        encabezado.crearAccionPrincipal(
                "Generar reporte",
                this::generarReporteGeneral
        );

        encabezado.crearAccionSecundaria(
                "Actualizar",
                this::refrescarDatos
        );
    }

    // ========================================================
    // 12. SUPERFICIE REUTILIZABLE
    // ========================================================

    private JPanel crearSuperficie() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(0, 16)
                );

        panel.setOpaque(true);

        panel.setBackground(
                Paleta.FONDO_TARJETA
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.BORDE_SUAVE,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                22, 24, 22, 24
                        )
                )
        );

        return panel;
    }

    // ========================================================
    // 13. INDICADORES DEL SISTEMA
    // ========================================================

    private void construirIndicadores() {

        panelIndicadores =
                new JPanel(
                        new GridLayout(
                                2, 3, 14, 14
                        )
                );

        panelIndicadores.setOpaque(false);

        indicadorSaldo =
                crearValorIndicador();

        indicadorClientes =
                crearValorIndicador();

        indicadorMonederos =
                crearValorIndicador();

        indicadorTransacciones =
                crearValorIndicador();

        indicadorPuntos =
                crearValorIndicador();

        indicadorProgramaciones =
                crearValorIndicador();

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "CAPITAL CONSOLIDADO",
                        indicadorSaldo,
                        "Saldo total del sistema"
                )
        );

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "CLIENTES",
                        indicadorClientes,
                        "Registros disponibles"
                )
        );

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "MONEDEROS",
                        indicadorMonederos,
                        "Cuentas administradas"
                )
        );

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "TRANSACCIONES",
                        indicadorTransacciones,
                        "Operaciones registradas"
                )
        );

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "PUNTOS",
                        indicadorPuntos,
                        "Programa de fidelizacion"
                )
        );

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "PROGRAMACIONES",
                        indicadorProgramaciones,
                        "Operaciones futuras"
                )
        );
    }

    // ========================================================
    // 14. TARJETA DE INDICADOR
    // ========================================================

    private JPanel crearTarjetaIndicador(
            String titulo,
            JLabel valor,
            String descripcion) {

        JPanel tarjeta =
                crearSuperficie();

        JPanel columnaTarjeta =
                new JPanel();

        columnaTarjeta.setOpaque(false);

        columnaTarjeta.setLayout(
                new BoxLayout(
                        columnaTarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etiqueta =
                new JLabel(titulo);

        etiqueta.setFont(
                Tipografia.mono(10f)
        );

        etiqueta.setForeground(
                Paleta.CHAMPAGNE
        );

        JLabel detalle =
                new JLabel(descripcion);

        detalle.setFont(
                Tipografia.cuerpoPequeno()
        );

        detalle.setForeground(
                Paleta.ASH
        );

        etiqueta.setAlignmentX(
                LEFT_ALIGNMENT
        );

        valor.setAlignmentX(
                LEFT_ALIGNMENT
        );

        detalle.setAlignmentX(
                LEFT_ALIGNMENT
        );

        columnaTarjeta.add(
                etiqueta
        );

        columnaTarjeta.add(
                Box.createVerticalStrut(12)
        );

        columnaTarjeta.add(
                valor
        );

        columnaTarjeta.add(
                Box.createVerticalStrut(8)
        );

        columnaTarjeta.add(
                detalle
        );

        tarjeta.add(
                columnaTarjeta,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    // ========================================================
    // 15. CREAR VALOR
    // ========================================================

    private JLabel crearValorIndicador() {

        JLabel valor =
                new JLabel("0");

        valor.setFont(
                Tipografia.editorial(30f)
        );

        valor.setForeground(
                Paleta.BONE
        );

        return valor;
    }

    // ========================================================
    // 16. PANEL DE ACCIONES
    // ========================================================

    private void construirAcciones() {

        panelAcciones =
                crearSuperficie();

        JPanel superior =
                new JPanel(
                        new BorderLayout()
                );

        superior.setOpaque(false);

        JLabel titulo =
                new JLabel(
                        "Centro de control"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        etiquetaAuditoria =
                new JLabel(
                        "AUDITORIA NO EJECUTADA"
                );

        etiquetaAuditoria.setFont(
                Tipografia.mono(10f)
        );

        etiquetaAuditoria.setForeground(
                Paleta.ASH
        );

        superior.add(
                titulo,
                BorderLayout.WEST
        );

        superior.add(
                etiquetaAuditoria,
                BorderLayout.EAST
        );

        panelAcciones.add(
                superior,
                BorderLayout.NORTH
        );

        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        acciones.setOpaque(false);

        BotonPremium botonReporte =
                BotonPremium.primario(
                        "Reporte general"
                );

        BotonPremium botonAuditoria =
                BotonPremium.secundario(
                        "Ejecutar auditoria"
                );

        BotonPremium botonCopiar =
                BotonPremium.fantasma(
                        "Copiar informe"
                );

        BotonPremium botonExportar =
                BotonPremium.secundario(
                        "Exportar TXT"
                );

        botonReporte.addActionListener(
                evento -> generarReporteGeneral()
        );

        botonAuditoria.addActionListener(
                evento -> ejecutarAuditoria()
        );

        botonCopiar.addActionListener(
                evento -> copiarReporte()
        );

        botonExportar.addActionListener(
                evento -> exportarReporte()
        );

        acciones.add(
                botonReporte
        );

        acciones.add(
                botonAuditoria
        );

        acciones.add(
                botonCopiar
        );

        acciones.add(
                botonExportar
        );

        panelAcciones.add(
                acciones,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 17. VISOR DE INFORMES
    // ========================================================

    private void construirVisor() {

        panelVisor =
                crearSuperficie();

        JPanel superior =
                new JPanel(
                        new BorderLayout(
                                12,
                                8
                        )
                );

        superior.setOpaque(false);

        JLabel titulo =
                new JLabel(
                        "Visor de informes"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        etiquetaTipoInforme =
                new JLabel(
                        "SIN INFORME"
                );

        etiquetaTipoInforme.setFont(
                Tipografia.mono(10f)
        );

        etiquetaTipoInforme.setForeground(
                Paleta.CHAMPAGNE
        );

        superior.add(
                titulo,
                BorderLayout.WEST
        );

        superior.add(
                etiquetaTipoInforme,
                BorderLayout.EAST
        );

        panelVisor.add(
                superior,
                BorderLayout.NORTH
        );

        // ----------------------------------
        // AREA DE TEXTO
        // ----------------------------------

        areaReporte =
                new JTextArea();

        areaReporte.setEditable(false);

        areaReporte.setFocusable(true);

        areaReporte.setLineWrap(false);

        areaReporte.setFont(
                Tipografia.mono(12f)
        );

        areaReporte.setBackground(
                Paleta.FONDO_SECUNDARIO
        );

        areaReporte.setForeground(
                Paleta.BONE
        );

        areaReporte.setCaretColor(
                Paleta.CHAMPAGNE
        );

        areaReporte.setSelectionColor(
                Paleta.ACENTO_SUAVE
        );

        areaReporte.setMargin(
                new java.awt.Insets(
                        18, 20, 18, 20
                )
        );

        areaReporte.setText(
                "========================================\n"
                        + "       MONEDERO / PRIVATE LEDGER\n"
                        + "========================================\n\n"
                        + "CENTRO DE REPORTES\n\n"
                        + "Selecciona una de las opciones:\n\n"
                        + "  1. Reporte general\n"
                        + "  2. Ejecutar auditoria\n\n"
                        + "Los informes se obtienen directamente\n"
                        + "de los servicios financieros.\n\n"
                        + "========================================"
        );

        areaReporte.setCaretPosition(0);

        JScrollPane scroll =
                new JScrollPane(
                        areaReporte,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        Paleta.BORDE_SUAVE
                )
        );

        scroll.getViewport().setBackground(
                Paleta.FONDO_SECUNDARIO
        );

        scroll.setPreferredSize(
                new Dimension(
                        850,
                        390
                )
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(18);

        panelVisor.add(
                scroll,
                BorderLayout.CENTER
        );

        // ----------------------------------
        // INFORMACION INFERIOR
        // ----------------------------------

        JPanel pie =
                new JPanel(
                        new BorderLayout()
                );

        pie.setOpaque(false);

        etiquetaFechaInforme =
                new JLabel(
                        "SIN FECHA DE GENERACION"
                );

        etiquetaFechaInforme.setFont(
                Tipografia.mono(10f)
        );

        etiquetaFechaInforme.setForeground(
                Paleta.ASH
        );

        etiquetaEstado =
                new JLabel(
                        "MODULO PREPARADO"
                );

        etiquetaEstado.setFont(
                Tipografia.mono(10f)
        );

        etiquetaEstado.setForeground(
                Paleta.SAGE
        );

        pie.add(
                etiquetaFechaInforme,
                BorderLayout.WEST
        );

        pie.add(
                etiquetaEstado,
                BorderLayout.EAST
        );

        panelVisor.add(
                pie,
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 18. REFRESCAR ESTADISTICAS
    // ========================================================

    /**
     * Consulta el estado actual
     * de los servicios.
     *
     * No genera automaticamente
     * un nuevo informe de auditoria.
     *
     * Esto permite distinguir entre
     * estadisticas actualizadas
     * e informes generados previamente.
     */
    public void refrescarDatos() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::refrescarDatos
            );

            return;
        }

        if (actualizando) {
            return;
        }

        actualizando = true;

        try {

            int clientes =
                    contexto.getClienteService()
                            .cantidadClientes();

            int monederos =
                    contexto.getClienteService()
                            .cantidadMonederos();

            int transacciones =
                    contexto.getTransaccionService()
                            .cantidadTransacciones();

            long puntos =
                    contexto.getPuntosService()
                            .totalPuntosSistema();

            int programaciones =
                    contexto.getProgramacionService()
                            .cantidadProgramaciones();

            BigDecimal saldo =
                    contexto.getClienteService()
                            .saldoTotalSistema();

            // ----------------------------------
            // ACTUALIZAR INTERFAZ
            // ----------------------------------

            indicadorClientes.setText(
                    formatearEntero(clientes)
            );

            indicadorMonederos.setText(
                    formatearEntero(monederos)
            );

            indicadorTransacciones.setText(
                    formatearEntero(transacciones)
            );

            indicadorPuntos.setText(
                    formatearEntero(puntos)
            );

            indicadorProgramaciones.setText(
                    formatearEntero(programaciones)
            );

            indicadorSaldo.setText(
                    formatearMoneda(saldo)
            );

            actualizarEstado(
                    "ESTADISTICAS ACTUALIZADAS",
                    Paleta.SAGE
            );

        } catch (RuntimeException error) {

            actualizarEstado(
                    "ERROR AL ACTUALIZAR ESTADISTICAS",
                    Paleta.ERROR
            );

            System.err.println(
                    "[REPORTES] "
                            + mensajeError(error)
            );

        } finally {

            actualizando = false;
        }
    }

    // ========================================================
    // 19. GENERAR REPORTE GENERAL
    // ========================================================

    /**
     * Genera el reporte financiero
     * utilizando ReporteService.
     *
     * No construye un reporte ficticio
     * a partir de las etiquetas.
     */
    public void generarReporteGeneral() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::generarReporteGeneral
            );

            return;
        }

        try {

            String informe =
                    contexto.getReporteService()
                            .generarReporteGeneral();

            if (informe == null
                    || informe.isBlank()) {

                throw new IllegalStateException(
                        "ReporteService devolvio "
                                + "un informe vacio."
                );
            }

            mostrarInforme(
                    TipoInforme.GENERAL,
                    informe
            );

            refrescarDatos();

            actualizarEstado(
                    "REPORTE GENERAL GENERADO",
                    Paleta.SAGE
            );

        } catch (RuntimeException error) {

            mostrarError(
                    "No fue posible generar el reporte general.",
                    error
            );
        }
    }

    // ========================================================
    // 20. EJECUTAR AUDITORIA
    // ========================================================

    /**
     * Ejecuta la verificacion de
     * integridad utilizando
     * IntegridadService.
     *
     * El resultado se obtiene
     * del motor financiero.
     */
    public void ejecutarAuditoria() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::ejecutarAuditoria
            );

            return;
        }

        try {

            boolean integro =
                    contexto.getIntegridadService()
                            .sistemaIntegro();

            String detalle =
                    contexto.getIntegridadService()
                            .generarReporteIntegridad();

            if (detalle == null
                    || detalle.isBlank()) {

                detalle =
                        "El servicio no devolvio "
                                + "un detalle textual.";
            }

            String resultado =
                    "========================================\n"
                            + "     AUDITORIA DE INTEGRIDAD\n"
                            + "========================================\n\n"
                            + "Fecha: "
                            + LocalDateTime.now()
                            .format(FORMATO_FECHA)
                            + "\n"
                            + "Resultado: "
                            + (integro
                            ? "APROBADA"
                            : "INCONSISTENCIAS DETECTADAS")
                            + "\n\n"
                            + "----------------------------------------\n"
                            + detalle;

            ultimaAuditoria =
                    integro;

            etiquetaAuditoria.setText(
                    integro
                            ? "INTEGRIDAD APROBADA"
                            : "INCONSISTENCIAS DETECTADAS"
            );

            etiquetaAuditoria.setForeground(
                    integro
                            ? Paleta.SAGE
                            : Paleta.ERROR
            );

            mostrarInforme(
                    TipoInforme.AUDITORIA,
                    resultado
            );

            actualizarEstado(
                    integro
                            ? "AUDITORIA APROBADA"
                            : "AUDITORIA CON ADVERTENCIAS",
                    integro
                            ? Paleta.SAGE
                            : Paleta.ADVERTENCIA
            );

            if (!integro) {

                JOptionPane.showMessageDialog(
                        this,
                        "La auditoria detecto "
                                + "inconsistencias.\n\n"
                                + "Consulta el informe "
                                + "para conocer los detalles.",
                        "Advertencia de integridad",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (RuntimeException error) {

            ultimaAuditoria = null;

            etiquetaAuditoria.setText(
                    "AUDITORIA NO COMPLETADA"
            );

            etiquetaAuditoria.setForeground(
                    Paleta.ERROR
            );

            mostrarError(
                    "No fue posible ejecutar la auditoria.",
                    error
            );
        }
    }

    // ========================================================
    // 21. MOSTRAR INFORME
    // ========================================================

    private void mostrarInforme(
            TipoInforme tipo,
            String contenidoInforme) {

        Objects.requireNonNull(
                tipo,
                "TipoInforme es obligatorio"
        );

        Objects.requireNonNull(
                contenidoInforme,
                "El informe no puede ser null"
        );

        tipoActual =
                tipo;

        reporteActual =
                contenidoInforme;

        areaReporte.setText(
                contenidoInforme
        );

        areaReporte.setCaretPosition(0);

        etiquetaTipoInforme.setText(
                switch (tipo) {

                    case GENERAL ->
                            "REPORTE GENERAL";

                    case AUDITORIA ->
                            "AUDITORIA DE INTEGRIDAD";

                    case NINGUNO ->
                            "SIN INFORME";
                }
        );

        etiquetaFechaInforme.setText(
                "GENERADO: "
                        + LocalDateTime.now()
                        .format(FORMATO_FECHA)
        );
    }

    // ========================================================
    // 22. COPIAR INFORME
    // ========================================================

    /**
     * Copia el informe generado
     * al portapapeles del sistema.
     */
    public void copiarReporte() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::copiarReporte
            );

            return;
        }

        if (!validarInformeDisponible()) {
            return;
        }

        try {

            StringSelection seleccion =
                    new StringSelection(
                            reporteActual
                    );

            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(
                            seleccion,
                            null
                    );

            actualizarEstado(
                    "INFORME COPIADO",
                    Paleta.SAGE
            );

            JOptionPane.showMessageDialog(
                    this,
                    "El informe se copio "
                            + "al portapapeles.",
                    "Copiar informe",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalStateException
                 | SecurityException error) {

            mostrarError(
                    "No fue posible acceder al portapapeles.",
                    error
            );
        }
    }

    // ========================================================
    // 23. EXPORTAR INFORME
    // ========================================================

    /**
     * Exporta exactamente el informe
     * generado por el servicio.
     *
     * No exporta el texto inicial
     * de bienvenida del visor.
     */
    public void exportarReporte() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::exportarReporte
            );

            return;
        }

        if (!validarInformeDisponible()) {
            return;
        }

        String prefijo =
                tipoActual == TipoInforme.AUDITORIA
                        ? "auditoria_monedero"
                        : "reporte_monedero";

        String nombreArchivo =
                prefijo
                        + "_"
                        + LocalDateTime.now()
                        .format(FORMATO_ARCHIVO)
                        + ".txt";

        JFileChooser selector =
                new JFileChooser();

        selector.setDialogTitle(
                "Exportar informe financiero"
        );

        selector.setSelectedFile(
                new java.io.File(
                        nombreArchivo
                )
        );

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Archivo de texto (*.txt)",
                        "txt"
                )
        );

        int seleccion =
                selector.showSaveDialog(
                        this
                );

        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destino =
                selector.getSelectedFile()
                        .toPath();

        if (!destino.toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".txt")) {

            destino =
                    Path.of(
                            destino.toString() + ".txt"
                    );
        }

        // ----------------------------------
        // CONFIRMAR SOBRESCRITURA
        // ----------------------------------

        if (Files.exists(destino)) {

            int reemplazar =
                    JOptionPane.showConfirmDialog(
                            this,
                            "El archivo ya existe.\n\n"
                                    + "¿Deseas reemplazarlo?",
                            "Confirmar exportacion",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (reemplazar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // ----------------------------------
        // ESCRIBIR ARCHIVO
        // ----------------------------------

        try {

            Files.writeString(
                    destino,
                    reporteActual,
                    StandardCharsets.UTF_8
            );

            actualizarEstado(
                    "INFORME EXPORTADO",
                    Paleta.SAGE
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Informe exportado correctamente.\n\n"
                            + destino.toAbsolutePath(),
                    "Exportacion completada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException
                 | SecurityException error) {

            mostrarError(
                    "No fue posible exportar el informe.",
                    error
            );
        }
    }

    // ========================================================
    // 24. VALIDAR INFORME
    // ========================================================

    private boolean validarInformeDisponible() {

        if (tipoActual == TipoInforme.NINGUNO
                || reporteActual.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes generar "
                            + "un reporte o ejecutar "
                            + "una auditoria.",
                    "Informe no disponible",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return false;
        }

        return true;
    }

    // ========================================================
    // 25. FORMATO DE ENTEROS
    // ========================================================

    private static String formatearEntero(
            long valor) {

        NumberFormat formato =
                NumberFormat.getIntegerInstance(
                        Locale.forLanguageTag("es-CO")
                );

        return formato.format(valor);
    }

    // ========================================================
    // 26. FORMATO MONETARIO
    // ========================================================

    private static String formatearMoneda(
            BigDecimal monto) {

        if (monto == null) {

            return "NO DISPONIBLE";
        }

        NumberFormat formato =
                NumberFormat.getCurrencyInstance(
                        Locale.forLanguageTag("es-CO")
                );

        formato.setMinimumFractionDigits(2);

        formato.setMaximumFractionDigits(2);

        return formato.format(
                monto
        );
    }

    // ========================================================
    // 27. ACTUALIZAR ESTADO
    // ========================================================

    private void actualizarEstado(
            String mensaje,
            Color color) {

        if (etiquetaEstado == null) {
            return;
        }

        etiquetaEstado.setText(
                mensaje == null
                        ? ""
                        : mensaje
        );

        etiquetaEstado.setForeground(
                color == null
                        ? Paleta.ASH
                        : color
        );
    }

    // ========================================================
    // 28. MOSTRAR ERRORES
    // ========================================================

    private void mostrarError(
            String descripcion,
            Exception error) {

        String detalle =
                mensajeError(
                        error
                );

        System.err.println(
                "[PANEL REPORTES] "
                        + descripcion
                        + " "
                        + detalle
        );

        actualizarEstado(
                "ERROR DE OPERACION",
                Paleta.ERROR
        );

        JOptionPane.showMessageDialog(
                this,
                descripcion
                        + "\n\n"
                        + detalle,
                "MONEDERO / Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ========================================================
    // 29. MENSAJE DE ERROR
    // ========================================================

    private static String mensajeError(
            Throwable error) {

        if (error == null) {

            return "Error desconocido";
        }

        String mensaje =
                error.getMessage();

        if (mensaje == null
                || mensaje.isBlank()) {

            return error.getClass()
                    .getSimpleName();
        }

        return mensaje;
    }

    // ========================================================
    // 30. CONSULTAR ESTADO DE AUDITORIA
    // ========================================================

    /**
     * null:
     * No existe resultado concluyente.
     *
     * true:
     * Ultima auditoria aprobada.
     *
     * false:
     * Ultima auditoria detecto problemas.
     *
     * No representa una verificacion
     * permanente en tiempo real.
     */
    public Boolean getUltimaAuditoria() {

        return ultimaAuditoria;
    }

    // ========================================================
    // 31. GETTERS
    // ========================================================

    public ContextoAplicacion getContexto() {

        return contexto;
    }

    public EncabezadoPanel getEncabezado() {

        return encabezado;
    }

    public JTextArea getAreaReporte() {

        return areaReporte;
    }

    public TipoInforme getTipoActual() {

        return tipoActual;
    }

    public String getReporteActual() {

        return reporteActual;
    }

    // ========================================================
    // 32. DESCRIPCION DEL MODULO
    // ========================================================

    public String generarDescripcion() {

        return "PanelReportes"
                + " | Tema: Institutional Noir"
                + " | Tipo: "
                + tipoActual
                + " | Clientes: "
                + indicadorClientes.getText()
                + " | Monederos: "
                + indicadorMonederos.getText()
                + " | Transacciones: "
                + indicadorTransacciones.getText()
                + " | Ultima auditoria: "
                + (ultimaAuditoria == null
                ? "NO EJECUTADA"
                : ultimaAuditoria
                ? "APROBADA"
                : "CON INCONSISTENCIAS");
    }
}
