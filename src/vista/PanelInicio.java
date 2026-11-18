package vista;

import servicios.ContextoAplicacion;

import vista.componentes.BotonPremium;
import vista.componentes.EncabezadoPanel;
import vista.componentes.TablaPremium;
import vista.componentes.TarjetaEstadistica;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * PANTALLA:
 * PanelInicio.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * 1. Construir el dashboard principal.
 * 2. Mostrar el balance consolidado.
 * 3. Presentar estadisticas del sistema.
 * 4. Incorporar acciones financieras rapidas.
 * 5. Mostrar movimientos recientes.
 * 6. Presentar un resumen de puntos.
 * 7. Refrescar datos desde los servicios.
 * 8. Adaptar la distribucion al espacio.
 *
 * ARQUITECTURA:
 *
 * VentanaPrincipal
 *        |
 *        v
 *    PanelInicio
 *        |
 *        v
 * ContextoAplicacion
 *        |
 *        +-- ClienteService
 *        +-- TransaccionService
 *        +-- PuntosService
 *        +-- ProgramacionService
 *
 * PRINCIPIOS:
 *
 * - No modificar directamente los modelos.
 * - No duplicar la logica financiera.
 * - No crear servicios adicionales.
 * - No mostrar transacciones ficticias.
 * - Mantener una unica fuente de datos.
 * - Actualizar Swing desde el EDT.
 *
 * Java 21.
 */
public final class PanelInicio extends JPanel {

    // ========================================================
    // 1. IDENTIDAD
    // ========================================================

    private static final long serialVersionUID = 1L;

    private static final String TITULO =
            "Overview.";

    private static final String SUBTITULO =
            "Tu capital. Cada movimiento, bajo control.";

    private static final String ETIQUETA_BALANCE =
            "CAPITAL CONSOLIDADO / COP";

    private static final Locale LOCALE_CO =
            Locale.forLanguageTag("es-CO");

    // ========================================================
    // 2. DIMENSIONES
    // ========================================================

    private static final int ANCHO_MINIMO_CONTENIDO =
            580;

    private static final int ANCHO_PANEL_LATERAL =
            278;

    private static final int ALTO_HERO =
            236;

    private static final int SEPARACION =
            Espaciado.DASHBOARD_SEPARACION;

    private static final int BREAKPOINT_TARJETAS =
            900;

    private static final int BREAKPOINT_CUERPO =
            1050;

    // ========================================================
    // 3. ACCIONES RAPIDAS
    // ========================================================

    /**
     * Las acciones se notifican al controlador.
     *
     * PanelInicio no ejecuta depositos,
     * retiros ni transferencias directamente.
     */
    public enum AccionRapida {

        NUEVA_OPERACION,

        TRANSFERIR,

        DEPOSITAR,

        RETIRAR
    }

    private Consumer<AccionRapida> listenerAcciones;

    // ========================================================
    // 4. CONTEXTO COMPARTIDO
    // ========================================================

    private final ContextoAplicacion contexto;

    // ========================================================
    // 5. COMPONENTES PRINCIPALES
    // ========================================================

    private JPanel contenido;

    private JPanel panelColumna;

    private JPanel panelTarjetas;

    private JPanel panelAcciones;

    private JPanel panelCuerpo;

    private JPanel panelActividad;

    private JPanel panelLateral;

    private EncabezadoPanel encabezado;

    private BalanceHero tarjetaBalance;

    // ========================================================
    // 6. TARJETAS ESTADISTICAS
    // ========================================================

    private TarjetaEstadistica tarjetaClientes;

    private TarjetaEstadistica tarjetaMonederos;

    private TarjetaEstadistica tarjetaOperaciones;

    private TarjetaEstadistica tarjetaPuntos;

    // ========================================================
    // 7. MOVIMIENTOS RECIENTES
    // ========================================================

    private TablaPremium tablaMovimientos;

    private TablaPremium.ModeloNoEditable modeloMovimientos;

    private JLabel etiquetaActividad;

    // ========================================================
    // 8. RESUMEN LATERAL
    // ========================================================

    private JLabel etiquetaTotalPuntos;

    private JLabel etiquetaProgramadas;

    private JLabel etiquetaEstadoSistema;

    // ========================================================
    // 9. ESTADO RESPONSIVO
    // ========================================================

    private boolean modoTarjetasCompacto;

    private boolean modoCuerpoCompacto;

    // ========================================================
    // 10. CONSTRUCTOR
    // ========================================================

    /**
     * Recibe el mismo ContextoAplicacion
     * que utilizara VentanaPrincipal.
     *
     * No crea un segundo motor financiero.
     */
    public PanelInicio(
            ContextoAplicacion contexto) {

        super(new BorderLayout());

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "El contexto no puede ser null"
                );

        this.listenerAcciones = null;

        this.modoTarjetasCompacto = false;

        this.modoCuerpoCompacto = false;

        inicializar();

        refrescarDatos();
    }

    // ========================================================
    // 11. INICIALIZACION
    // ========================================================

    private void inicializar() {

        setOpaque(true);

        setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        construirContenido();

        JScrollPane desplazamiento =
                new JScrollPane(
                        contenido,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );

        desplazamiento.setBorder(
                BorderFactory.createEmptyBorder()
        );

        desplazamiento.setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        desplazamiento.getViewport()
                .setBackground(
                        Paleta.FONDO_PRINCIPAL
                );

        desplazamiento.getVerticalScrollBar()
                .setUnitIncrement(20);

        desplazamiento.getHorizontalScrollBar()
                .setUnitIncrement(16);

        add(
                desplazamiento,
                BorderLayout.CENTER
        );

        contenido.addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(
                            ComponentEvent evento) {

                        actualizarDistribucion();
                    }
                }
        );
    }

    // ========================================================
    // 12. CONTENIDO PRINCIPAL
    // ========================================================

    private void construirContenido() {

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

        contenido.setMinimumSize(
                new Dimension(
                        ANCHO_MINIMO_CONTENIDO,
                        0
                )
        );

        panelColumna =
                new JPanel();

        panelColumna.setOpaque(false);

        panelColumna.setLayout(
                new BoxLayout(
                        panelColumna,
                        BoxLayout.Y_AXIS
                )
        );

        construirEncabezado();

        construirBalance();

        construirAccionesRapidas();

        construirEstadisticas();

        construirCuerpo();

        agregarBloque(encabezado);

        agregarBloque(tarjetaBalance);

        agregarBloque(panelAcciones);

        agregarBloque(panelTarjetas);

        agregarBloque(panelCuerpo);

        contenido.add(
                panelColumna,
                BorderLayout.NORTH
        );
    }

    /**
     * Agrega un bloque al flujo vertical
     * manteniendo una separacion uniforme.
     */
    private void agregarBloque(
            JPanel bloque) {

        bloque.setAlignmentX(
                LEFT_ALIGNMENT
        );

        if (panelColumna.getComponentCount() > 0) {

            panelColumna.add(
                    Box.createVerticalStrut(
                            SEPARACION
                    )
            );
        }

        panelColumna.add(
                bloque
        );
    }

    // ========================================================
    // 13. ENCABEZADO
    // ========================================================

    private void construirEncabezado() {

        encabezado =
                EncabezadoPanel.overview();

        encabezado.setTitulo(
                TITULO
        );

        encabezado.setSubtitulo(
                SUBTITULO
        );

        encabezado.setMostrarSeparador(false);

        encabezado.crearAccionPrincipal(
                "Nueva operacion",
                () -> notificarAccion(
                        AccionRapida.NUEVA_OPERACION
                )
        );

        encabezado.crearAccionSecundaria(
                "Actualizar",
                this::refrescarDatos
        );
    }

    // ========================================================
    // 14. TARJETA DE BALANCE
    // ========================================================

    private void construirBalance() {

        tarjetaBalance =
                new BalanceHero();

        tarjetaBalance.setPreferredSize(
                new Dimension(
                        800,
                        ALTO_HERO
                )
        );

        tarjetaBalance.setMinimumSize(
                new Dimension(
                        400,
                        ALTO_HERO
                )
        );
    }

    // ========================================================
    // 15. ACCIONES RAPIDAS
    // ========================================================

    private void construirAccionesRapidas() {

        panelAcciones =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                Espaciado.LG,
                                Espaciado.LG
                        )
                );

        panelAcciones.setOpaque(false);

        BotonPremium nuevaOperacion =
                BotonPremium.primario(
                        "Nueva operacion"
                );

        BotonPremium transferir =
                BotonPremium.secundario(
                        "Transferir"
                );

        BotonPremium depositar =
                BotonPremium.secundario(
                        "Depositar"
                );

        BotonPremium retirar =
                BotonPremium.fantasma(
                        "Retirar"
                );

        nuevaOperacion.addActionListener(
                evento -> notificarAccion(
                        AccionRapida.NUEVA_OPERACION
                )
        );

        transferir.addActionListener(
                evento -> notificarAccion(
                        AccionRapida.TRANSFERIR
                )
        );

        depositar.addActionListener(
                evento -> notificarAccion(
                        AccionRapida.DEPOSITAR
                )
        );

        retirar.addActionListener(
                evento -> notificarAccion(
                        AccionRapida.RETIRAR
                )
        );

        panelAcciones.add(
                nuevaOperacion
        );

        panelAcciones.add(
                transferir
        );

        panelAcciones.add(
                depositar
        );

        panelAcciones.add(
                retirar
        );
    }

    // ========================================================
    // 16. ESTADISTICAS
    // ========================================================

    private void construirEstadisticas() {

        panelTarjetas =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                Espaciado.LG,
                                Espaciado.LG
                        )
                );

        panelTarjetas.setOpaque(false);

        tarjetaClientes =
                TarjetaEstadistica.normal(
                        "CLIENTES",
                        "0",
                        "Registros del sistema"
                );

        tarjetaMonederos =
                TarjetaEstadistica.normal(
                        "MONEDEROS",
                        "0",
                        "Cuentas financieras"
                );

        tarjetaOperaciones =
                TarjetaEstadistica.normal(
                        "OPERACIONES",
                        "0",
                        "Movimientos registrados"
                );

        tarjetaPuntos =
                TarjetaEstadistica.positiva(
                        "PUNTOS",
                        "0",
                        "Acumulados en el sistema"
                );

        tarjetaPuntos.setMostrarLineaAcento(
                true
        );

        panelTarjetas.add(
                tarjetaClientes
        );

        panelTarjetas.add(
                tarjetaMonederos
        );

        panelTarjetas.add(
                tarjetaOperaciones
        );

        panelTarjetas.add(
                tarjetaPuntos
        );
    }

    // ========================================================
    // 17. CUERPO INFERIOR
    // ========================================================

    private void construirCuerpo() {

        panelCuerpo =
                new JPanel(
                        new BorderLayout(
                                SEPARACION,
                                SEPARACION
                        )
                );

        panelCuerpo.setOpaque(false);

        construirActividad();

        construirPanelLateral();

        panelCuerpo.add(
                panelActividad,
                BorderLayout.CENTER
        );

        panelCuerpo.add(
                panelLateral,
                BorderLayout.EAST
        );
    }

    // ========================================================
    // 18. ACTIVIDAD RECIENTE
    // ========================================================

    private void construirActividad() {

        panelActividad =
                crearSuperficie();

        panelActividad.setLayout(
                new BorderLayout(
                        0,
                        Espaciado.LG
                )
        );

        panelActividad.setBorder(
                Espaciado.bordeVacio(
                        Espaciado.TARJETA_PADDING
                )
        );

        JPanel cabecera =
                new JPanel(
                        new BorderLayout()
                );

        cabecera.setOpaque(false);

        JLabel titulo =
                new JLabel(
                        "Actividad reciente"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        etiquetaActividad =
                new JLabel(
                        "Sin movimientos cargados"
                );

        etiquetaActividad.setFont(
                Tipografia.caption()
        );

        etiquetaActividad.setForeground(
                Paleta.ASH
        );

        cabecera.add(
                titulo,
                BorderLayout.WEST
        );

        cabecera.add(
                etiquetaActividad,
                BorderLayout.EAST
        );

        modeloMovimientos =
                new TablaPremium.ModeloNoEditable(
                        new String[]{
                                "FECHA",
                                "OPERACION",
                                "ESTADO",
                                "MONTO"
                        },
                        0
                );

        tablaMovimientos =
                new TablaPremium(
                        modeloMovimientos
                );

        tablaMovimientos.configurarColumnaEstado(
                2
        );

        tablaMovimientos.configurarColumnaMonetaria(
                3
        );

        tablaMovimientos.setMensajeVacio(
                "No hay movimientos para mostrar"
        );

        tablaMovimientos.setPreferredScrollableViewportSize(
                new Dimension(
                        520,
                        250
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        tablaMovimientos
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.setBackground(
                Paleta.FONDO_TARJETA
        );

        scroll.getViewport().setBackground(
                Paleta.FONDO_TARJETA
        );

        scroll.setPreferredSize(
                new Dimension(
                        520,
                        300
                )
        );

        panelActividad.add(
                cabecera,
                BorderLayout.NORTH
        );

        panelActividad.add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 19. PANEL LATERAL
    // ========================================================

    private void construirPanelLateral() {

        panelLateral =
                crearSuperficie();

        panelLateral.setLayout(
                new BoxLayout(
                        panelLateral,
                        BoxLayout.Y_AXIS
                )
        );

        panelLateral.setBorder(
                Espaciado.bordeVacio(
                        Espaciado.TARJETA_PADDING
                )
        );

        panelLateral.setPreferredSize(
                new Dimension(
                        ANCHO_PANEL_LATERAL,
                        0
                )
        );

        JLabel etiquetaSuperior =
                new JLabel(
                        "PRIVATE LEDGER / REWARDS"
                );

        etiquetaSuperior.setFont(
                Tipografia.mono(10f)
        );

        etiquetaSuperior.setForeground(
                Paleta.CHAMPAGNE
        );

        JLabel titulo =
                new JLabel(
                        "Puntos del sistema."
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        etiquetaTotalPuntos =
                new JLabel("0");

        etiquetaTotalPuntos.setFont(
                Tipografia.monoMedium(34f)
        );

        etiquetaTotalPuntos.setForeground(
                Paleta.BONE
        );

        JLabel subtitulo =
                new JLabel(
                        "Puntos acumulados"
                );

        subtitulo.setFont(
                Tipografia.cuerpoPequeno()
        );

        subtitulo.setForeground(
                Paleta.ASH
        );

        JLabel etiquetaProgramacionTitulo =
                new JLabel(
                        "OPERACIONES PROGRAMADAS"
                );

        etiquetaProgramacionTitulo.setFont(
                Tipografia.etiqueta()
        );

        etiquetaProgramacionTitulo.setForeground(
                Paleta.ASH
        );

        etiquetaProgramadas =
                new JLabel("0");

        etiquetaProgramadas.setFont(
                Tipografia.monoMedium(24f)
        );

        etiquetaProgramadas.setForeground(
                Paleta.BONE
        );

        JLabel etiquetaEstadoTitulo =
                new JLabel(
                        "ESTADO DE LA INTERFAZ"
                );

        etiquetaEstadoTitulo.setFont(
                Tipografia.etiqueta()
        );

        etiquetaEstadoTitulo.setForeground(
                Paleta.ASH
        );

        etiquetaEstadoSistema =
                new JLabel(
                        "Datos disponibles"
                );

        etiquetaEstadoSistema.setFont(
                Tipografia.cuerpoPequeno()
        );

        etiquetaEstadoSistema.setForeground(
                Paleta.SAGE
        );

        agregarElementoLateral(
                etiquetaSuperior
        );

        panelLateral.add(
                Box.createVerticalStrut(18)
        );

        agregarElementoLateral(
                titulo
        );

        panelLateral.add(
                Box.createVerticalStrut(24)
        );

        agregarElementoLateral(
                etiquetaTotalPuntos
        );

        panelLateral.add(
                Box.createVerticalStrut(6)
        );

        agregarElementoLateral(
                subtitulo
        );

        panelLateral.add(
                Box.createVerticalStrut(28)
        );

        agregarElementoLateral(
                crearSeparador()
        );

        panelLateral.add(
                Box.createVerticalStrut(24)
        );

        agregarElementoLateral(
                etiquetaProgramacionTitulo
        );

        panelLateral.add(
                Box.createVerticalStrut(8)
        );

        agregarElementoLateral(
                etiquetaProgramadas
        );

        panelLateral.add(
                Box.createVerticalStrut(28)
        );

        agregarElementoLateral(
                etiquetaEstadoTitulo
        );

        panelLateral.add(
                Box.createVerticalStrut(10)
        );

        agregarElementoLateral(
                etiquetaEstadoSistema
        );
    }

    private void agregarElementoLateral(
            javax.swing.JComponent componente) {

        componente.setAlignmentX(
                LEFT_ALIGNMENT
        );

        panelLateral.add(
                componente
        );
    }

    // ========================================================
    // 20. SUPERFICIES
    // ========================================================

    private JPanel crearSuperficie() {

        JPanel superficie =
                new JPanel();

        superficie.setOpaque(true);

        superficie.setBackground(
                Paleta.FONDO_TARJETA
        );

        superficie.setBorder(
                BorderFactory.createLineBorder(
                        Paleta.BORDE_SUAVE,
                        1
                )
        );

        return superficie;
    }

    private JPanel crearSeparador() {

        JPanel separador =
                new JPanel();

        separador.setOpaque(true);

        separador.setBackground(
                Paleta.DIVIDER
        );

        separador.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1
                )
        );

        separador.setPreferredSize(
                new Dimension(
                        1,
                        1
                )
        );

        return separador;
    }

    // ========================================================
    // 21. REFRESCAR DATOS REALES
    // ========================================================

    /**
     * Actualiza el dashboard utilizando
     * los servicios existentes.
     *
     * Esta operacion consulta datos.
     *
     * No crea clientes, monederos
     * ni transacciones.
     */
    public void refrescarDatos() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::refrescarDatos
            );

            return;
        }

        try {

            // ----------------------------------
            // CLIENTES Y MONEDEROS
            // ----------------------------------

            int clientes =
                    contexto.getClienteService()
                            .cantidadClientes();

            int monederos =
                    contexto.getClienteService()
                            .cantidadMonederos();

            // ----------------------------------
            // BALANCE CONSOLIDADO
            // ----------------------------------

            BigDecimal saldo =
                    contexto.getClienteService()
                            .saldoTotalSistema();

            // ----------------------------------
            // OPERACIONES
            // ----------------------------------

            int operaciones =
                    contexto.getTransaccionService()
                            .cantidadTransacciones();

            // ----------------------------------
            // PUNTOS
            // ----------------------------------

            long puntos =
                    contexto.getPuntosService()
                            .totalPuntosSistema();

            // ----------------------------------
            // PROGRAMACIONES
            // ----------------------------------

            int programadas =
                    contexto.getProgramacionService()
                            .cantidadProgramaciones();

            // ----------------------------------
            // REFRESCAR COMPONENTES
            // ----------------------------------

            tarjetaBalance.setSaldo(
                    saldo
            );

            tarjetaClientes.setValorNumerico(
                    clientes
            );

            tarjetaMonederos.setValorNumerico(
                    monederos
            );

            tarjetaOperaciones.setValorNumerico(
                    operaciones
            );

            tarjetaPuntos.setValorNumerico(
                    puntos
            );

            etiquetaTotalPuntos.setText(
                    formatearEntero(puntos)
            );

            etiquetaProgramadas.setText(
                    formatearEntero(programadas)
            );

            etiquetaEstadoSistema.setText(
                    "Datos actualizados"
            );

            etiquetaEstadoSistema.setForeground(
                    Paleta.SAGE
            );

            // La tabla de movimientos se
            // alimentara desde el adaptador
            // de transacciones reales.
            //
            // No generamos registros de ejemplo.

            actualizarEstadoActividad();

        } catch (RuntimeException error) {

            etiquetaEstadoSistema.setText(
                    "No se pudieron consultar los datos"
            );

            etiquetaEstadoSistema.setForeground(
                    Paleta.ERROR
            );

            System.err.println(
                    "[PANEL INICIO] Error: "
                            + error.getMessage()
            );
        }
    }

    // ========================================================
    // 22. REGISTRAR MOVIMIENTO VISUAL
    // ========================================================

    /**
     * Inserta un movimiento en la tabla.
     *
     * Los datos deben proceder de una
     * transaccion real del sistema.
     *
     * Este metodo NO realiza operaciones
     * financieras.
     *
     * No debe utilizarse para registrar
     * movimientos nuevos en el motor.
     */
    public void agregarMovimiento(
            String fecha,
            String operacion,
            String estado,
            BigDecimal monto) {

        Objects.requireNonNull(
                monto,
                "El monto no puede ser null"
        );

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(() ->
                    agregarMovimiento(
                            fecha,
                            operacion,
                            estado,
                            monto
                    )
            );

            return;
        }

        modeloMovimientos.insertRow(
                0,
                new Object[]{
                        textoSeguro(fecha),
                        textoSeguro(operacion),
                        textoSeguro(estado),
                        monto
                }
        );

        // El dashboard solo muestra
        // los diez movimientos recientes
        // cargados en esta vista.

        while (modeloMovimientos.getRowCount() > 10) {

            modeloMovimientos.removeRow(
                    modeloMovimientos.getRowCount() - 1
            );
        }

        actualizarEstadoActividad();
    }

    // ========================================================
    // 23. LIMPIAR MOVIMIENTOS
    // ========================================================

    public void limpiarMovimientos() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::limpiarMovimientos
            );

            return;
        }

        modeloMovimientos.setRowCount(0);

        actualizarEstadoActividad();
    }

    // ========================================================
    // 24. ESTADO DE ACTIVIDAD
    // ========================================================

    private void actualizarEstadoActividad() {

        int cantidad =
                modeloMovimientos.getRowCount();

        etiquetaActividad.setText(
                cantidad == 0
                        ? "Sin movimientos cargados"
                        : cantidad + " movimientos"
        );
    }

    // ========================================================
    // 25. ACCIONES RAPIDAS
    // ========================================================

    /**
     * La ventana principal sera responsable
     * de abrir el formulario adecuado.
     */
    public void setListenerAcciones(
            Consumer<AccionRapida> listener) {

        listenerAcciones = listener;
    }

    private void notificarAccion(
            AccionRapida accion) {

        if (listenerAcciones != null) {

            listenerAcciones.accept(
                    accion
            );

            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "La accion "
                        + accion.name()
                        + " se habilitara al conectar "
                        + "VentanaPrincipal.",
                "Monedero / Private Ledger",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========================================================
    // 26. DISTRIBUCION RESPONSIVA
    // ========================================================

    /**
     * Ajusta las columnas segun
     * el ancho disponible.
     *
     * No utiliza posiciones absolutas.
     */
    private void actualizarDistribucion() {

        int ancho =
                contenido.getWidth()
                        - Espaciado.CONTENIDO_PADDING_HORIZONTAL * 2;

        if (ancho <= 0) {
            return;
        }

        boolean tarjetasCompactas =
                ancho < BREAKPOINT_TARJETAS;

        boolean cuerpoCompacto =
                ancho < BREAKPOINT_CUERPO;

        if (tarjetasCompactas
                != modoTarjetasCompacto) {

            modoTarjetasCompacto =
                    tarjetasCompactas;

            panelTarjetas.setLayout(
                    new GridLayout(
                            tarjetasCompactas ? 2 : 1,
                            tarjetasCompactas ? 2 : 4,
                            Espaciado.LG,
                            Espaciado.LG
                    )
            );

            panelAcciones.setLayout(
                    new GridLayout(
                            tarjetasCompactas ? 2 : 1,
                            tarjetasCompactas ? 2 : 4,
                            Espaciado.LG,
                            Espaciado.LG
                    )
            );
        }

        if (cuerpoCompacto != modoCuerpoCompacto) {

            modoCuerpoCompacto =
                    cuerpoCompacto;

            panelCuerpo.removeAll();

            if (cuerpoCompacto) {

                panelLateral.setPreferredSize(
                        new Dimension(
                                0,
                                230
                        )
                );

                panelCuerpo.add(
                        panelActividad,
                        BorderLayout.CENTER
                );

                panelCuerpo.add(
                        panelLateral,
                        BorderLayout.SOUTH
                );

            } else {

                panelLateral.setPreferredSize(
                        new Dimension(
                                ANCHO_PANEL_LATERAL,
                                0
                        )
                );

                panelCuerpo.add(
                        panelActividad,
                        BorderLayout.CENTER
                );

                panelCuerpo.add(
                        panelLateral,
                        BorderLayout.EAST
                );
            }
        }

        panelColumna.revalidate();

        panelColumna.repaint();
    }

    // ========================================================
    // 27. FORMATO DE NUMEROS
    // ========================================================

    private static String formatearEntero(
            long numero) {

        NumberFormat formato =
                NumberFormat.getIntegerInstance(
                        LOCALE_CO
                );

        return formato.format(numero);
    }

    private static String formatearSaldo(
            BigDecimal saldo) {

        NumberFormat formato =
                NumberFormat.getCurrencyInstance(
                        LOCALE_CO
                );

        formato.setMinimumFractionDigits(2);

        formato.setMaximumFractionDigits(2);

        return formato.format(
                saldo == null
                        ? BigDecimal.ZERO
                        : saldo
        );
    }

    private static String textoSeguro(
            String texto) {

        return texto == null
                ? ""
                : texto;
    }

    // ========================================================
    // 28. GETTERS DE COMPONENTES
    // ========================================================

    public EncabezadoPanel getEncabezado() {
        return encabezado;
    }

    public TablaPremium getTablaMovimientos() {
        return tablaMovimientos;
    }

    public TarjetaEstadistica getTarjetaClientes() {
        return tarjetaClientes;
    }

    public TarjetaEstadistica getTarjetaMonederos() {
        return tarjetaMonederos;
    }

    public TarjetaEstadistica getTarjetaOperaciones() {
        return tarjetaOperaciones;
    }

    public TarjetaEstadistica getTarjetaPuntos() {
        return tarjetaPuntos;
    }

    public ContextoAplicacion getContexto() {
        return contexto;
    }

    // ========================================================
    // 29. TARJETA PROTAGONISTA
    // ========================================================

    /**
     * Tarjeta especial del balance.
     *
     * Integra una fotografia monocromatica del proyecto,
     * escalada proporcionalmente y protegida por un degradado
     * oscuro para mantener legibles las cifras. Usa el relieve
     * original como respaldo si el archivo falta.
     */
    private static final class BalanceHero
            extends JPanel {

        private static final long serialVersionUID = 1L;

        private final JLabel etiquetaSaldo;

        private static final String IMAGEN_HERO =
                "/recursos/imagenes/montanas_monocromo.jpg";

        // Solo se carga una vez para evitar trabajo en cada repaint.
        private static final BufferedImage FOTO_MONTANAS = cargarFotografia();

        /**
         * Busca primero en el classpath y despues en la carpeta
         * de desarrollo. El fallback permite ejecutar desde VS Code.
         */
        private static BufferedImage cargarFotografia() {
            try (InputStream archivo =
                         PanelInicio.class.getResourceAsStream(IMAGEN_HERO)) {
                if (archivo != null) {
                    BufferedImage imagen = ImageIO.read(archivo);
                    if (imagen != null) return imagen;
                }
            } catch (IOException error) {
                System.err.println("[DASHBOARD] Error imagen classpath: "
                        + error.getMessage());
            }

            File local = new File(
                    "src/recursos/imagenes/montanas_monocromo.jpg");
            if (local.isFile()) {
                try {
                    BufferedImage imagen = ImageIO.read(local);
                    if (imagen != null) return imagen;
                } catch (IOException error) {
                    System.err.println("[DASHBOARD] Error imagen local: "
                            + error.getMessage());
                }
            }
            System.err.println("[DASHBOARD] Foto no encontrada; usando relieve de respaldo.");
            return null;
        }

        private BalanceHero() {

            super(new BorderLayout());

            setOpaque(false);

            setBorder(
                    Espaciado.bordeVacio(
                            32,
                            36,
                            28,
                            36
                    )
            );

            JPanel contenidoHero =
                    new JPanel();

            contenidoHero.setOpaque(false);

            contenidoHero.setLayout(
                    new BoxLayout(
                            contenidoHero,
                            BoxLayout.Y_AXIS
                    )
            );

            JLabel etiquetaSuperior =
                    new JLabel(
                            ETIQUETA_BALANCE
                    );

            etiquetaSuperior.setFont(
                    Tipografia.monoMedium(11f)
            );

            etiquetaSuperior.setForeground(
                    Paleta.CHAMPAGNE
            );

            etiquetaSaldo =
                    new JLabel("$ 0,00");

            etiquetaSaldo.setFont(
                    Tipografia.monoMedium(39f)
            );

            etiquetaSaldo.setForeground(
                    Paleta.BONE
            );

            JLabel etiquetaDescripcion =
                    new JLabel(
                            "Balance total de todos los monederos"
                    );

            etiquetaDescripcion.setFont(
                    Tipografia.cuerpoPequeno()
            );

            etiquetaDescripcion.setForeground(
                    Paleta.ASH
            );

            JLabel etiquetaInferior =
                    new JLabel(
                            "PRIVATE LEDGER  /  FINANCIAL OVERVIEW"
                    );

            etiquetaInferior.setFont(
                    Tipografia.mono(10f)
            );

            etiquetaInferior.setForeground(
                    Paleta.TEXTO_TERCIARIO
            );

            etiquetaSuperior.setAlignmentX(
                    LEFT_ALIGNMENT
            );

            etiquetaSaldo.setAlignmentX(
                    LEFT_ALIGNMENT
            );

            etiquetaDescripcion.setAlignmentX(
                    LEFT_ALIGNMENT
            );

            etiquetaInferior.setAlignmentX(
                    LEFT_ALIGNMENT
            );

            contenidoHero.add(
                    etiquetaSuperior
            );

            contenidoHero.add(
                    Box.createVerticalStrut(25)
            );

            contenidoHero.add(
                    etiquetaSaldo
            );

            contenidoHero.add(
                    Box.createVerticalStrut(10)
            );

            contenidoHero.add(
                    etiquetaDescripcion
            );

            contenidoHero.add(
                    Box.createVerticalGlue()
            );

            contenidoHero.add(
                    etiquetaInferior
            );

            add(
                    contenidoHero,
                    BorderLayout.CENTER
            );
        }

        private void setSaldo(
                BigDecimal saldo) {

            etiquetaSaldo.setText(
                    formatearSaldo(saldo)
            );

            repaint();
        }

        // --------------------------------------
        // PINTADO DEL HERO
        // --------------------------------------

        @Override
        protected void paintComponent(
                Graphics graphics) {

            Graphics2D g2 =
                    (Graphics2D) graphics.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int ancho = getWidth();

                int alto = getHeight();

                if (ancho <= 0 || alto <= 0) {
                    return;
                }

                RoundRectangle2D.Float forma =
                        new RoundRectangle2D.Float(
                                0,
                                0,
                                ancho - 1f,
                                alto - 1f,
                                16,
                                16
                        );

                // Fondo de respaldo: siempre visible antes de la foto.
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(38, 39, 43),
                        ancho, alto, new Color(20, 21, 23)));
                g2.fill(forma);
                g2.setClip(forma);

                if (FOTO_MONTANAS != null) {
                    // Equivalente a background-size: cover.
                    // Mantiene proporciones y recorta el sobrante.
                    double factor = Math.max(
                            (double) ancho / FOTO_MONTANAS.getWidth(),
                            (double) alto / FOTO_MONTANAS.getHeight());

                    int fotoAncho = Math.max(1,
                            (int) Math.ceil(FOTO_MONTANAS.getWidth() * factor));
                    int fotoAlto = Math.max(1,
                            (int) Math.ceil(FOTO_MONTANAS.getHeight() * factor));
                    int fotoX = (ancho - fotoAncho) / 2;
                    int fotoY = (alto - fotoAlto) / 2;

                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.drawImage(FOTO_MONTANAS, fotoX, fotoY,
                            fotoAncho, fotoAlto, null);

                    // Tratamiento Institutional Noir: oscurecer imagen.
                    g2.setColor(new Color(10, 11, 13, 105));
                    g2.fillRect(0, 0, ancho, alto);

                    // Más oscuro a la izquierda: el saldo siempre se lee.
                    g2.setPaint(new GradientPaint(
                            0, 0, new Color(8, 9, 12, 238),
                            ancho, 0, new Color(8, 9, 12, 55)));
                    g2.fillRect(0, 0, ancho, alto);

                    // Velo suave superior/inferior para textos secundarios.
                    g2.setPaint(new GradientPaint(
                            0, 0, new Color(8, 9, 12, 55),
                            0, alto, new Color(8, 9, 12, 150)));
                    g2.fillRect(0, 0, ancho, alto);
                } else {
                    // Si la foto no está, se conserva el arte original.
                    dibujarRelieve(g2, ancho, alto);
                }

                g2.setClip(null);

                g2.setStroke(
                        new BasicStroke(1f)
                );

                g2.setColor(
                        Paleta.BORDE_SUAVE
                );

                g2.draw(forma);

            } finally {

                g2.dispose();
            }

            super.paintComponent(graphics);
        }

        // --------------------------------------
        // RELIEVE ABSTRACTO
        // --------------------------------------

        private void dibujarRelieve(
                Graphics2D g2,
                int ancho,
                int alto) {

            double inicio =
                    ancho * 0.58;

            for (int linea = 0;
                 linea < 11;
                 linea++) {

                Path2D.Double camino =
                        new Path2D.Double();

                double baseY =
                        alto * 0.35
                                + linea * 13;

                camino.moveTo(
                        inicio,
                        baseY + 16
                );

                camino.curveTo(
                        ancho * 0.72,
                        baseY - 52,
                        ancho * 0.83,
                        baseY + 68,
                        ancho + 30,
                        baseY - 22
                );

                g2.setColor(
                        new Color(
                                199,
                                167,
                                122,
                                Math.max(
                                        9,
                                        36 - linea * 2
                                )
                        )
                );

                g2.setStroke(
                        new BasicStroke(1f)
                );

                g2.draw(camino);
            }
        }
    }

    // ========================================================
    // 30. DESCRIPCION
    // ========================================================

    public String generarDescripcion() {

        return "PanelInicio"
                + " | Tema: Institutional Noir"
                + " | Vista: Overview"
                + " | Clientes: "
                + tarjetaClientes.getValor()
                + " | Monederos: "
                + tarjetaMonederos.getValor()
                + " | Operaciones: "
                + tarjetaOperaciones.getValor()
                + " | Puntos: "
                + tarjetaPuntos.getValor();
    }
}
