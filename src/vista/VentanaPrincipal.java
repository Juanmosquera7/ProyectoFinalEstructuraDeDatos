package vista;

import servicios.ContextoAplicacion;

import vista.componentes.Sidebar;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.TemaAplicacion;
import vista.tema.Tipografia;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.EnumMap;
import java.util.Objects;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * VENTANA:
 * VentanaPrincipal.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * 1. Administrar la ventana principal.
 * 2. Integrar el sistema de diseno.
 * 3. Incorporar Sidebar y PanelInicio.
 * 4. Gestionar navegacion con CardLayout.
 * 5. Compartir ContextoAplicacion.
 * 6. Conectar acciones del dashboard.
 * 7. Refrescar la informacion financiera.
 * 8. Sincronizar las ocho secciones reales.
 * 9. Proporcionar atajos de navegacion.
 *
 * ARQUITECTURA:
 *
 * Main
 *   |
 *   v
 * ContextoAplicacion
 *   |
 *   v
 * VentanaPrincipal
 *   |
 *   +-- Sidebar
 *   |
 *   +-- CardLayout
 *         |
 *         +-- PanelInicio
 *         +-- Clientes
 *         +-- Monederos
 *         +-- Transacciones
 *         +-- Puntos
 *         +-- Historial
 *         +-- Programadas
 *         +-- Reportes
 *
 * IMPORTANTE:
 *
 * Esta clase no ejecuta directamente
 * depositos, retiros ni transferencias.
 *
 * Esas operaciones corresponden a
 * los servicios y sus formularios.
 *
 * Las ocho pantallas se registran con
 * implementaciones reales.
 *
 * Java 21.
 */
public final class VentanaPrincipal extends JFrame {

    // ========================================================
    // 1. IDENTIDAD
    // ========================================================

    private static final long serialVersionUID = 1L;

    public static final String TITULO_APLICACION =
            "MONEDERO / PRIVATE LEDGER";

    public static final String VERSION =
            "1.0.0";

    // ========================================================
    // 2. CONTEXTO FINANCIERO
    // ========================================================

    /**
     * Instancia compartida por todos
     * los paneles del sistema.
     */
    private final ContextoAplicacion contexto;

    // ========================================================
    // 3. COMPONENTES PRINCIPALES
    // ========================================================

    private Sidebar sidebar;

    private JPanel contenedorPrincipal;

    private JPanel contenedorVistas;

    private CardLayout administradorVistas;

    private PanelInicio panelInicio;

    private JPanel barraInferior;

    private JLabel etiquetaEstado;

    private JLabel etiquetaSeccion;

    // ========================================================
    // 4. NAVEGACION
    // ========================================================

    private Sidebar.Seccion seccionActual;

    /**
     * Mapa de las vistas registradas.
     *
     * Utilizamos EnumMap porque las claves
     * pertenecen al enum Sidebar.Seccion.
     */
    private final EnumMap<Sidebar.Seccion, JComponent>
            vistas = new EnumMap<>(
            Sidebar.Seccion.class
    );

    // ========================================================
    // 5. CONSTRUCTOR
    // ========================================================

    /**
     * Recibe una unica instancia de
     * ContextoAplicacion.
     *
     * Main.java sera responsable de
     * construir ese contexto.
     */
    public VentanaPrincipal(
            ContextoAplicacion contexto) {

        super(TITULO_APLICACION);

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "ContextoAplicacion no puede ser null"
                );

        this.seccionActual =
                Sidebar.Seccion.INICIO;

        inicializarVentana();
    }

    // ========================================================
    // 6. INICIALIZACION
    // ========================================================

    private void inicializarVentana() {

        // Se recomienda construir esta
        // ventana en el hilo EDT de Swing.

        TemaAplicacion.inicializar();

        configurarVentana();

        construirEstructura();

        construirSidebar();

        construirVistas();

        construirBarraInferior();

        conectarEventos();

        configurarAtajos();

        navegarA(
                Sidebar.Seccion.INICIO
        );

        setLocationRelativeTo(null);
    }

    // ========================================================
    // 7. CONFIGURACION DE VENTANA
    // ========================================================

    private void configurarVentana() {

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setMinimumSize(
                Espaciado.ventanaMinima()
        );

        setSize(
                Espaciado.ventanaInicial()
        );

        getContentPane().setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        getContentPane().setLayout(
                new BorderLayout()
        );

        setResizable(true);
    }

    // ========================================================
    // 8. ESTRUCTURA PRINCIPAL
    // ========================================================

    private void construirEstructura() {

        contenedorPrincipal =
                new JPanel(
                        new BorderLayout()
                );

        contenedorPrincipal.setOpaque(true);

        contenedorPrincipal.setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        administradorVistas =
                new CardLayout();

        contenedorVistas =
                new JPanel(
                        administradorVistas
                );

        contenedorVistas.setOpaque(true);

        contenedorVistas.setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        contenedorPrincipal.add(
                contenedorVistas,
                BorderLayout.CENTER
        );

        getContentPane().add(
                contenedorPrincipal,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 9. SIDEBAR
    // ========================================================

    private void construirSidebar() {

        sidebar =
                new Sidebar(
                        Sidebar.Seccion.INICIO
                );

        sidebar.setPreferredSize(
                new Dimension(
                        Espaciado.SIDEBAR_ANCHO,
                        0
                )
        );

        getContentPane().add(
                sidebar,
                BorderLayout.WEST
        );

        sidebar.setListenerNavegacion(
                this::navegarA
        );
    }

    // ========================================================
    // 10. REGISTRAR VISTAS
    // ========================================================

    private void construirVistas() {

        // --------------------------------------
        // DASHBOARD REAL
        // --------------------------------------

        panelInicio =
                new PanelInicio(
                        contexto
                );

        registrarVista(
                Sidebar.Seccion.INICIO,
                panelInicio
        );

        // --------------------------------------
        // VISTAS REALES
        // --------------------------------------

        PanelClientes panelClientes =
                new PanelClientes(contexto);

        panelClientes.setListenerActualizacion(
                panelInicio::refrescarDatos
        );

        registrarVista(
                Sidebar.Seccion.CLIENTES,
                panelClientes
        );

        PanelMonederos panelMonederos =
                new PanelMonederos(contexto);

        panelMonederos.setListenerActualizacion(
                () -> {
                    panelInicio.refrescarDatos();
                }
        );

        registrarVista(
                Sidebar.Seccion.MONEDEROS,
                panelMonederos
        );


        // ==========================================
        // PANEL DE TRANSACCIONES
        // ==========================================

        PanelTransacciones panelTransacciones =
                new PanelTransacciones(contexto);

        panelTransacciones.setListenerActualizacion(
                () -> {

                    // Actualizar dashboard.
                    panelInicio.refrescarDatos();

                    // Actualizar directorio de clientes.
                    JComponent clientes =
                            vistas.get(
                                    Sidebar.Seccion.CLIENTES
                            );

                    if (clientes instanceof PanelClientes panel) {
                        panel.refrescarDatos();
                    }

                    // Actualizar saldos de monederos.
                    JComponent monederos =
                            vistas.get(
                                    Sidebar.Seccion.MONEDEROS
                            );

                    if (monederos instanceof PanelMonederos panel) {
                        panel.refrescarDatos();
                    }
                }
        );

        registrarVista(
                Sidebar.Seccion.TRANSACCIONES,
                panelTransacciones
        );



        // ==========================================
        // PANEL DE PUNTOS Y RANGOS
        // ==========================================

        PanelPuntos panelPuntos =
                new PanelPuntos(contexto);

        registrarVista(
                Sidebar.Seccion.PUNTOS,
                panelPuntos
        );



        // ==========================================
        // PANEL DE HISTORIAL
        // ==========================================

        PanelHistorial panelHistorial =
                new PanelHistorial(contexto);

        registrarVista(
                Sidebar.Seccion.HISTORIAL,
                panelHistorial
        );



        // ==========================================
        // PANEL DE OPERACIONES PROGRAMADAS
        // ==========================================

        PanelProgramadas panelProgramadas =
                new PanelProgramadas(contexto);

        panelProgramadas.setListenerActualizacion(
                () -> {

                    // Actualizar dashboard principal.
                    panelInicio.refrescarDatos();

                    // Actualizar las estadisticas de
                    // los otros paneles financieros.
                    JComponent transacciones =
                            vistas.get(
                                    Sidebar.Seccion.TRANSACCIONES
                            );

                    if (transacciones instanceof PanelTransacciones panel) {
                        panel.refrescarDatos();
                    }
                }
        );

        registrarVista(
                Sidebar.Seccion.PROGRAMADAS,
                panelProgramadas
        );



        // ==========================================
        // PANEL DE REPORTES Y AUDITORIA
        // ==========================================

        PanelReportes panelReportes =
                new PanelReportes(contexto);

        registrarVista(
                Sidebar.Seccion.REPORTES,
                panelReportes
        );

    }

    // ========================================================
    // 11. REGISTRAR VISTA
    // ========================================================

    /**
     * Registra una vista en CardLayout.
     *
     * Si la seccion ya tenia un panel,
     * elimina el anterior.
     *
     * Permite conectar posteriormente
     * las vistas reales sin reconstruir
     * toda la ventana.
     */
    public void registrarVista(
            Sidebar.Seccion seccion,
            JComponent vista) {

        Objects.requireNonNull(
                seccion,
                "La seccion no puede ser null"
        );

        Objects.requireNonNull(
                vista,
                "La vista no puede ser null"
        );

        if (vista.getParent() != null
                && vista.getParent() != contenedorVistas) {

            throw new IllegalArgumentException(
                    "La vista pertenece a otro contenedor"
            );
        }

        JComponent anterior =
                vistas.get(seccion);

        if (anterior == vista) {
            return;
        }



        if (anterior != null) {

            contenedorVistas.remove(
                    anterior
            );
        }

        vistas.put(
                seccion,
                vista
        );

        contenedorVistas.add(
                vista,
                seccion.name()
        );

        if (seccion == seccionActual) {

            administradorVistas.show(
                    contenedorVistas,
                    seccion.name()
            );
        }

        contenedorVistas.revalidate();

        contenedorVistas.repaint();
    }

    // ========================================================
    // 13. BARRA INFERIOR
    // ========================================================

    private void construirBarraInferior() {

        barraInferior =
                new JPanel(
                        new BorderLayout()
                );

        barraInferior.setOpaque(true);

        barraInferior.setBackground(
                Paleta.GRAPHITE
        );

        barraInferior.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                Paleta.BORDE_SUAVE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                20,
                                9,
                                20
                        )
                )
        );

        etiquetaEstado =
                new JLabel(
                        "SISTEMA LISTO"
                );

        etiquetaEstado.setFont(
                Tipografia.mono(10f)
        );

        etiquetaEstado.setForeground(
                Paleta.SAGE
        );

        etiquetaSeccion =
                new JLabel(
                        "WORKSPACE / OVERVIEW"
                );

        etiquetaSeccion.setFont(
                Tipografia.mono(10f)
        );

        etiquetaSeccion.setForeground(
                Paleta.TEXTO_TERCIARIO
        );

        barraInferior.add(
                etiquetaEstado,
                BorderLayout.WEST
        );

        barraInferior.add(
                etiquetaSeccion,
                BorderLayout.EAST
        );

        getContentPane().add(
                barraInferior,
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 14. CONECTAR EVENTOS
    // ========================================================

    private void conectarEventos() {

        // Acciones rapidas del dashboard.

        panelInicio.setListenerAcciones(
                this::procesarAccionRapida
        );
    }

    // ========================================================
    // 15. CAMBIAR DE SECCION
    // ========================================================

    /**
     * Navega a una seccion registrada.
     *
     * Actualiza:
     *
     * - CardLayout.
     * - Sidebar.
     * - Barra de estado.
     * - Informacion de la ventana.
     */
    public void navegarA(
            Sidebar.Seccion seccion) {

        Objects.requireNonNull(
                seccion,
                "La seccion no puede ser null"
        );

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    () -> navegarA(seccion)
            );

            return;
        }

        if (!vistas.containsKey(seccion)) {

            mostrarEstado(
                    "SECCION NO REGISTRADA",
                    Paleta.ADVERTENCIA
            );

            return;
        }

        seccionActual = seccion;

        administradorVistas.show(
                contenedorVistas,
                seccion.name()
        );

        sidebar.setSeccionActiva(
                seccion
        );

        etiquetaSeccion.setText(
                "WORKSPACE / "
                        + seccion.name()
        );

        setTitle(
                TITULO_APLICACION
                        + " — "
                        + seccion.getEtiqueta()
        );


        // La vista visible consulta datos actuales sin crear
        // servicios ni duplicar operaciones financieras.
        refrescarVista(seccion);

        // 4. Estado de la aplicacion
        mostrarEstado(
                "SISTEMA LISTO",
                Paleta.SAGE
        );

        contenedorVistas.revalidate();

        contenedorVistas.repaint();
    }

    // ========================================================
    // REFRESCAR VISTA REGISTRADA
    // ========================================================

    private void refrescarVista(Sidebar.Seccion seccion) {
        JComponent vista = vistas.get(seccion);
        if (vista == null) {
            return;
        }

        switch (seccion) {
            case INICIO -> {
                if (vista instanceof PanelInicio panel) {
                    panel.refrescarDatos();
                }
            }
            case CLIENTES -> {
                if (vista instanceof PanelClientes panel) {
                    panel.refrescarDatos();
                }
            }
            case MONEDEROS -> {
                if (vista instanceof PanelMonederos panel) {
                    panel.refrescarDatos();
                }
            }
            case TRANSACCIONES -> {
                if (vista instanceof PanelTransacciones panel) {
                    panel.refrescarDatos();
                }
            }
            case PUNTOS -> {
                if (vista instanceof PanelPuntos panel) {
                    panel.refrescarDatos();
                }
            }
            case HISTORIAL -> {
                if (vista instanceof PanelHistorial panel) {
                    panel.refrescarDatos();
                }
            }
            case PROGRAMADAS -> {
                if (vista instanceof PanelProgramadas panel) {
                    panel.refrescarDatos();
                }
            }
            case REPORTES -> {
                if (vista instanceof PanelReportes panel) {
                    panel.refrescarDatos();
                }
            }
        }
    }

    /** F5 actualiza la seccion visible (no solo el dashboard). */
    public void refrescarVistaActual() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refrescarVistaActual);
            return;
        }
        refrescarVista(seccionActual);
        mostrarEstado("SECCION ACTUALIZADA", Paleta.SAGE);
    }

    // ========================================================
    // 16. ACCIONES RAPIDAS
    // ========================================================

    /**
     * En esta primera version, las acciones
     * rapidas abren la seccion correspondiente.
     *
     * Los formularios financieros se
     * conectaran posteriormente.
     *
     * NO se ejecutan transacciones aqui.
     */

    // ========================================================
    // ACCIONES RAPIDAS DEL DASHBOARD
    // ========================================================

    private void procesarAccionRapida(
            PanelInicio.AccionRapida accion) {

        Objects.requireNonNull(
                accion,
                "La accion no puede ser null"
        );

        // Abrir pantalla de transacciones.
        navegarA(
                Sidebar.Seccion.TRANSACCIONES
        );

        JComponent vista =
                vistas.get(
                        Sidebar.Seccion.TRANSACCIONES
                );

        if (!(vista instanceof PanelTransacciones panel)) {

            mostrarEstado(
                    "PANEL DE TRANSACCIONES NO DISPONIBLE",
                    Paleta.ERROR
            );

            return;
        }

        // Seleccionar formulario correspondiente.
        switch (accion) {

            case NUEVA_OPERACION, DEPOSITAR -> {

                panel.seleccionarOperacion(
                        PanelTransacciones.TipoOperacion.DEPOSITO
                );
            }

            case RETIRAR -> {

                panel.seleccionarOperacion(
                        PanelTransacciones.TipoOperacion.RETIRO
                );
            }

            case TRANSFERIR -> {

                panel.seleccionarOperacion(
                        PanelTransacciones.TipoOperacion.TRANSFERENCIA
                );
            }
        }

        mostrarEstado(
                "FORMULARIO DE OPERACION DISPONIBLE",
                Paleta.CHAMPAGNE
        );
    }


    // ========================================================
    // 17. BARRA DE ESTADO
    // ========================================================

    public void mostrarEstado(
            String mensaje,
            Color color) {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    () -> mostrarEstado(
                            mensaje,
                            color
                    )
            );

            return;
        }

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
    // 18. ACTUALIZAR DATOS
    // ========================================================

    /**
     * Solicita al dashboard consultar
     * nuevamente los servicios.
     */
    public void refrescarDashboard() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::refrescarDashboard
            );

            return;
        }

        panelInicio.refrescarDatos();

        mostrarEstado(
                "CONSULTA DE DATOS REALIZADA",
                Paleta.SAGE
        );
    }

    // ========================================================
    // 19. ATAJOS DE TECLADO
    // ========================================================

    /**
     * Ctrl + 1: Inicio.
     * Ctrl + 2: Clientes.
     * Ctrl + 3: Monederos.
     * Ctrl + 4: Transacciones.
     * Ctrl + 5: Puntos.
     * Ctrl + 6: Historial.
     * Ctrl + 7: Programadas.
     * Ctrl + 8: Reportes.
     *
     * F5: Actualizar la seccion visible.
     */
    private void configurarAtajos() {

        JRootPane raiz =
                getRootPane();

        Sidebar.Seccion[] secciones =
                Sidebar.Seccion.values();

        for (int indice = 0;
             indice < secciones.length;
             indice++) {

            Sidebar.Seccion seccion =
                    secciones[indice];

            int numero =
                    indice + 1;

            String nombreAccion =
                    "navegar-" + numero;

            KeyStroke atajo =
                    KeyStroke.getKeyStroke(
                            KeyEvent.VK_1 + indice,
                            KeyEvent.CTRL_DOWN_MASK
                    );

            raiz.getInputMap(
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            ).put(
                    atajo,
                    nombreAccion
            );

            raiz.getActionMap().put(
                    nombreAccion,
                    new AbstractAction() {

                        @Override
                        public void actionPerformed(
                                ActionEvent evento) {

                            navegarA(
                                    seccion
                            );
                        }
                    }
            );
        }

        raiz.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_F5,
                        0
                ),
                "refrescar-dashboard"
        );

        raiz.getActionMap().put(
                "refrescar-dashboard",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            ActionEvent evento) {

                        refrescarVistaActual();
                    }
                }
        );
    }

    // ========================================================
    // 20. MOSTRAR VENTANA
    // ========================================================

    /**
     * Abre la aplicacion.
     *
     * Las operaciones graficas se
     * realizan en el EDT.
     */
    public void mostrar() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::mostrar
            );

            return;
        }

        setLocationRelativeTo(null);

        setVisible(true);

        refrescarDashboard();
    }

    // ========================================================
    // 21. MODO COMPACTO
    // ========================================================

    public void setSidebarCompacta(
            boolean compacta) {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    () -> setSidebarCompacta(
                            compacta
                    )
            );

            return;
        }

        sidebar.setModoCompacto(
                compacta
        );

        revalidate();

        repaint();
    }

    public boolean isSidebarCompacta() {

        return sidebar.isModoCompacto();
    }

    // ========================================================
    // 22. GETTERS
    // ========================================================

    public ContextoAplicacion getContexto() {

        return contexto;
    }

    public Sidebar getSidebar() {

        return sidebar;
    }

    public PanelInicio getPanelInicio() {

        return panelInicio;
    }

    public Sidebar.Seccion getSeccionActual() {

        return seccionActual;
    }

    public JComponent getVista(
            Sidebar.Seccion seccion) {

        return vistas.get(
                Objects.requireNonNull(seccion)
        );
    }

    // ========================================================
    // 23. INFORMACION DE LA VENTANA
    // ========================================================

    public String generarDescripcion() {

        return "VentanaPrincipal"
                + " | Producto: "
                + TITULO_APLICACION
                + " | Version: "
                + VERSION
                + " | Tema: Institutional Noir"
                + " | Seccion: "
                + seccionActual
                + " | Vistas registradas: "
                + vistas.size()
                + " | Sidebar compacta: "
                + sidebar.isModoCompacto();
    }
}
