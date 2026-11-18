package vista.componentes;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.util.EnumMap;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * COMPONENTE:
 * Sidebar.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * - Mostrar la identidad del producto.
 * - Proporcionar navegacion lateral.
 * - Gestionar la seccion seleccionada.
 * - Notificar cambios a VentanaPrincipal.
 * - Dibujar iconos minimalistas con Java2D.
 * - Permitir un modo compacto.
 * - Mantener navegacion por teclado.
 *
 * ARQUITECTURA:
 *
 * VentanaPrincipal
 *       |
 *       v
 *     Sidebar
 *       |
 *       +-- Inicio
 *       +-- Clientes
 *       +-- Monederos
 *       +-- Transacciones
 *       +-- Puntos
 *       +-- Historial
 *       +-- Programadas
 *       +-- Reportes
 *
 * No contiene dependencias financieras.
 *
 * Java 21.
 */
public final class Sidebar extends JPanel {

    // ========================================================
    // 1. SECCIONES DE NAVEGACION
    // ========================================================

    public enum Seccion {

        INICIO(
                "Overview",
                "Inicio"
        ),

        CLIENTES(
                "Clientes",
                "Clientes"
        ),

        MONEDEROS(
                "Monederos",
                "Monederos"
        ),

        TRANSACCIONES(
                "Transacciones",
                "Transacciones"
        ),

        PUNTOS(
                "Puntos y rangos",
                "Puntos y rangos"
        ),

        HISTORIAL(
                "Historial",
                "Historial"
        ),

        PROGRAMADAS(
                "Programadas",
                "Programadas"
        ),

        REPORTES(
                "Reportes",
                "Reportes"
        );

        private final String etiqueta;
        private final String descripcion;

        Seccion(
                String etiqueta,
                String descripcion) {

            this.etiqueta = etiqueta;
            this.descripcion = descripcion;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // ========================================================
    // 2. CONSTANTES VISUALES
    // ========================================================

    private static final int ANCHO_NORMAL =
            Espaciado.SIDEBAR_ANCHO;

    private static final int ANCHO_COMPACTO =
            Espaciado.SIDEBAR_ANCHO_COLAPSADO;

    private static final int ALTURA_ITEM =
            Espaciado.SIDEBAR_ITEM_ALTURA;

    private static final int SEPARACION_ITEM =
            Espaciado.SIDEBAR_ITEM_SEPARACION;

    private static final int TAMANO_ICONO =
            Espaciado.SIDEBAR_ICONO;

    private static final String VERSION =
            "V 1.0.0";

    // ========================================================
    // 3. ESTADO
    // ========================================================

    private Seccion seccionActiva;

    private boolean modoCompacto;

    private Consumer<Seccion> listenerNavegacion;

    // ========================================================
    // 4. COMPONENTES
    // ========================================================

    private JPanel panelSuperior;

    private JPanel panelMenu;

    private JPanel panelInferior;

    private JScrollPane scrollMenu;

    private JLabel etiquetaMarca;

    private JLabel etiquetaSubtitulo;

    private JLabel etiquetaVersion;

    private JLabel etiquetaSistema;

    private JButton botonColapsar;

    private final EnumMap<Seccion, BotonNavegacion> botones =
            new EnumMap<>(Seccion.class);

    // ========================================================
    // 5. CONSTRUCTORES
    // ========================================================

    public Sidebar() {

        this(Seccion.INICIO);
    }

    public Sidebar(
            Seccion seccionInicial) {

        super(new BorderLayout());

        seccionActiva =
                Objects.requireNonNull(
                        seccionInicial,
                        "La seccion inicial no puede ser null"
                );

        modoCompacto = false;

        listenerNavegacion = null;

        inicializar();
    }

    // ========================================================
    // 6. INICIALIZACION
    // ========================================================

    private void inicializar() {

        setOpaque(true);

        setBackground(
                Paleta.SIDEBAR_FONDO
        );

        setPreferredSize(
                new Dimension(
                        ANCHO_NORMAL,
                        0
                )
        );

        setMinimumSize(
                new Dimension(
                        ANCHO_COMPACTO,
                        0
                )
        );

        setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        0,
                        1,
                        Paleta.SIDEBAR_DIVISOR
                )
        );

        construirEncabezado();

        construirMenu();

        construirPie();

        actualizarModoVisual();
    }

    // ========================================================
    // 7. ENCABEZADO
    // ========================================================

    private void construirEncabezado() {

        panelSuperior =
                new JPanel(
                        new BorderLayout(
                                0,
                                Espaciado.SM
                        )
                );

        panelSuperior.setOpaque(false);

        JPanel encabezadoMarca =
                new JPanel(
                        new BorderLayout()
                );

        encabezadoMarca.setOpaque(false);

        etiquetaMarca =
                new JLabel("M.");

        etiquetaMarca.setFont(
                Tipografia.semiBold(31f)
        );

        etiquetaMarca.setForeground(
                Paleta.BONE
        );

        etiquetaSubtitulo =
                new JLabel(
                        "PRIVATE LEDGER / 01"
                );

        etiquetaSubtitulo.setFont(
                Tipografia.mono(10f)
        );

        etiquetaSubtitulo.setForeground(
                Paleta.TEXTO_TERCIARIO
        );

        JPanel bloqueMarca =
                new JPanel();

        bloqueMarca.setOpaque(false);

        bloqueMarca.setLayout(
                new BoxLayout(
                        bloqueMarca,
                        BoxLayout.Y_AXIS
                )
        );

        etiquetaMarca.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        etiquetaSubtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        bloqueMarca.add(etiquetaMarca);

        bloqueMarca.add(
                Box.createVerticalStrut(5)
        );

        bloqueMarca.add(etiquetaSubtitulo);

        encabezadoMarca.add(
                bloqueMarca,
                BorderLayout.CENTER
        );

        botonColapsar =
                new JButton();

        botonColapsar.setIcon(
                iconoLucide(TipoIcono.PANEL, Paleta.ASH, 17)
        );

        botonColapsar.setToolTipText(
                "Contraer navegacion"
        );

        botonColapsar.setFocusable(true);

        botonColapsar.setOpaque(false);

        botonColapsar.setContentAreaFilled(false);

        botonColapsar.setBorderPainted(false);

        botonColapsar.setFocusPainted(true);

        botonColapsar.setForeground(
                Paleta.ASH
        );

        botonColapsar.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        botonColapsar.setPreferredSize(
                new Dimension(32, 32)
        );

        botonColapsar.addActionListener(
                evento -> alternarModoCompacto()
        );

        encabezadoMarca.add(
                botonColapsar,
                BorderLayout.EAST
        );

        panelSuperior.add(
                encabezadoMarca,
                BorderLayout.CENTER
        );

        JPanel separador =
                new JPanel();

        separador.setOpaque(true);

        separador.setBackground(
                Paleta.SIDEBAR_DIVISOR
        );

        separador.setPreferredSize(
                new Dimension(1, 1)
        );

        panelSuperior.add(
                separador,
                BorderLayout.SOUTH
        );

        add(
                panelSuperior,
                BorderLayout.NORTH
        );
    }

    // ========================================================
    // 8. MENU DE NAVEGACION
    // ========================================================

    private void construirMenu() {

        panelMenu =
                new JPanel();

        panelMenu.setOpaque(false);

        panelMenu.setLayout(
                new BoxLayout(
                        panelMenu,
                        BoxLayout.Y_AXIS
                )
        );

        for (Seccion seccion : Seccion.values()) {

            BotonNavegacion boton =
                    new BotonNavegacion(seccion);

            boton.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            boton.addActionListener(
                    evento -> seleccionarDesdeUsuario(
                            seccion
                    )
            );

            botones.put(
                    seccion,
                    boton
            );

            panelMenu.add(boton);

            panelMenu.add(
                    Box.createVerticalStrut(
                            SEPARACION_ITEM
                    )
            );
        }

        scrollMenu =
                new JScrollPane(
                        panelMenu,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                );

        scrollMenu.setOpaque(false);

        scrollMenu.getViewport()
                .setOpaque(false);

        scrollMenu.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollMenu.setBackground(
                Paleta.SIDEBAR_FONDO
        );

        scrollMenu.getVerticalScrollBar()
                .setUnitIncrement(14);

        add(
                scrollMenu,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 9. PIE EDITORIAL
    // ========================================================

    private void construirPie() {

        panelInferior =
                new JPanel();

        panelInferior.setOpaque(false);

        panelInferior.setLayout(
                new BoxLayout(
                        panelInferior,
                        BoxLayout.Y_AXIS
                )
        );

        etiquetaSistema =
                new JLabel(
                        "MONEDERO / SYSTEM"
                );

        etiquetaSistema.setFont(
                Tipografia.mono(10f)
        );

        etiquetaSistema.setForeground(
                Paleta.TEXTO_TERCIARIO
        );

        etiquetaVersion =
                new JLabel(VERSION);

        etiquetaVersion.setFont(
                Tipografia.mono(10f)
        );

        etiquetaVersion.setForeground(
                Paleta.ASH
        );

        etiquetaSistema.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        etiquetaVersion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelInferior.add(
                etiquetaSistema
        );

        panelInferior.add(
                Box.createVerticalStrut(6)
        );

        panelInferior.add(
                etiquetaVersion
        );

        add(
                panelInferior,
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 10. NAVEGACION
    // ========================================================

    /**
     * Se ejecuta cuando el usuario
     * selecciona una opcion.
     */
    private void seleccionarDesdeUsuario(
            Seccion seccion) {

        setSeccionActiva(seccion);

        if (listenerNavegacion != null) {

            listenerNavegacion.accept(
                    seccion
            );
        }
    }

    /**
     * Cambia la seleccion visual.
     *
     * No dispara el listener.
     *
     * Esto evita ciclos cuando
     * VentanaPrincipal actualiza la sidebar.
     */
    public void setSeccionActiva(
            Seccion seccion) {

        seccionActiva =
                Objects.requireNonNull(
                        seccion,
                        "La seccion no puede ser null"
                );

        actualizarSeleccion();
    }

    public Seccion getSeccionActiva() {

        return seccionActiva;
    }

    /**
     * Registra la accion que ejecutara
     * VentanaPrincipal cuando el usuario
     * seleccione una seccion.
     */
    public void setListenerNavegacion(
            Consumer<Seccion> listener) {

        listenerNavegacion = listener;
    }

    public Consumer<Seccion> getListenerNavegacion() {

        return listenerNavegacion;
    }

    /**
     * Selecciona una seccion y notifica
     * al controlador.
     */
    public void navegarA(
            Seccion seccion) {

        seleccionarDesdeUsuario(
                Objects.requireNonNull(seccion)
        );
    }

    // ========================================================
    // 11. ACTUALIZAR SELECCION
    // ========================================================

    private void actualizarSeleccion() {

        for (Seccion seccion : Seccion.values()) {

            BotonNavegacion boton =
                    botones.get(seccion);

            if (boton != null) {

                boton.setActivo(
                        seccion == seccionActiva
                );
            }
        }

        repaint();
    }

    // ========================================================
    // 12. MODO COMPACTO
    // ========================================================

    public void setModoCompacto(
            boolean compacto) {

        if (modoCompacto == compacto) {
            return;
        }

        modoCompacto = compacto;

        actualizarModoVisual();
    }

    public boolean isModoCompacto() {

        return modoCompacto;
    }

    public void alternarModoCompacto() {

        setModoCompacto(
                !modoCompacto
        );
    }

    // ========================================================
    // 13. ACTUALIZAR MODO VISUAL
    // ========================================================

    private void actualizarModoVisual() {

        int ancho =
                modoCompacto
                        ? ANCHO_COMPACTO
                        : ANCHO_NORMAL;

        int padding =
                modoCompacto
                        ? 10
                        : Espaciado.SIDEBAR_PADDING_HORIZONTAL;

        setPreferredSize(
                new Dimension(
                        ancho,
                        0
                )
        );

        panelSuperior.setBorder(
                BorderFactory.createEmptyBorder(
                        Espaciado.SIDEBAR_PADDING_VERTICAL,
                        padding,
                        28,
                        padding
                )
        );

        scrollMenu.setBorder(
                BorderFactory.createEmptyBorder(
                        28,
                        padding,
                        12,
                        padding
                )
        );

        panelInferior.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        padding,
                        28,
                        padding
                )
        );

        etiquetaSubtitulo.setVisible(
                !modoCompacto
        );

        etiquetaSistema.setVisible(
                !modoCompacto
        );

        etiquetaVersion.setText(
                modoCompacto
                        ? "1.0"
                        : VERSION
        );

        etiquetaVersion.setAlignmentX(
                modoCompacto
                        ? Component.CENTER_ALIGNMENT
                        : Component.LEFT_ALIGNMENT
        );

        botonColapsar.setToolTipText(
                modoCompacto
                        ? "Expandir navegacion"
                        : "Contraer navegacion"
        );

        // En modo compacto mostramos el
        // monograma y ocultamos el subtitulo.

        etiquetaMarca.setText(
                "M."
        );

        etiquetaMarca.setFont(
                Tipografia.semiBold(
                        modoCompacto ? 23f : 31f
                )
        );

        // Reducir los botones a iconos.

        int anchoBoton =
                ancho - padding * 2 - 1;

        for (BotonNavegacion boton : botones.values()) {

            boton.setCompacto(
                    modoCompacto
            );

            boton.setMaximumSize(
                    new Dimension(
                            anchoBoton,
                            ALTURA_ITEM
                    )
            );

            boton.setPreferredSize(
                    new Dimension(
                            anchoBoton,
                            ALTURA_ITEM
                    )
            );
        }

        actualizarSeleccion();

        revalidate();

        repaint();
    }

    // ========================================================
    // 14. CONSULTAR BOTONES
    // ========================================================

    /**
     * Permite obtener un boton para
     * configurar su estado.
     *
     * Por ejemplo:
     *
     * sidebar.getBoton(Seccion.REPORTES)
     *        .setEnabled(false);
     */
    public JButton getBoton(
            Seccion seccion) {

        return botones.get(
                Objects.requireNonNull(seccion)
        );
    }

    /**
     * Habilita o deshabilita una seccion.
     */
    public void setSeccionHabilitada(
            Seccion seccion,
            boolean habilitada) {

        JButton boton =
                getBoton(seccion);

        if (boton != null) {

            boton.setEnabled(
                    habilitada
            );
        }
    }

    // ========================================================
    // 15. BOTON DE NAVEGACION
    // ========================================================

    /**
     * Boton interno de la sidebar.
     *
     * Conserva la funcionalidad nativa
     * de JButton:
     *
     * - ActionListener.
     * - Navegacion por teclado.
     * - Accesibilidad.
     * - Estado deshabilitado.
     *
     * El fondo y la seleccion se dibujan
     * mediante Java2D.
     */
    private final class BotonNavegacion extends JButton {

        private final Seccion seccion;

        private boolean activo;

        private boolean compacto;

        private BotonNavegacion(
                Seccion seccion) {

            super(seccion.getEtiqueta());

            this.seccion = seccion;

            activo = false;

            compacto = false;

            setFont(
                    Tipografia.navegacion()
            );

            setForeground(
                    Paleta.SIDEBAR_TEXTO
            );

            setOpaque(false);

            setContentAreaFilled(false);

            setBorderPainted(false);

            setFocusPainted(false);

            setRolloverEnabled(true);

            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            setHorizontalTextPosition(
                    SwingConstants.RIGHT
            );

            setIconTextGap(
                    Espaciado.SIDEBAR_ICONO_TEXTO
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            14,
                            0,
                            10
                    )
            );

            setIcon(
                    iconoLucide(tipoIcono(seccion), Paleta.SIDEBAR_TEXTO, TAMANO_ICONO)
            );

            setPreferredSize(
                    new Dimension(
                            200,
                            ALTURA_ITEM
                    )
            );

            setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            ALTURA_ITEM
                    )
            );

            getAccessibleContext()
                    .setAccessibleName(
                            seccion.getDescripcion()
                    );

            addFocusListener(
                    new FocusAdapter() {

                        @Override
                        public void focusGained(
                                FocusEvent evento) {

                            repaint();
                        }

                        @Override
                        public void focusLost(
                                FocusEvent evento) {

                            repaint();
                        }
                    }
            );
        }

        private void setActivo(
                boolean activo) {

            this.activo = activo;

            setForeground(
                    activo
                            ? Paleta.SIDEBAR_TEXTO_ACTIVO
                            : Paleta.SIDEBAR_TEXTO
            );

            setFont(
                    activo
                            ? Tipografia.navegacionActiva()
                            : Tipografia.navegacion()
            );

            setIcon(
                    iconoLucide(tipoIcono(seccion), activo ? Paleta.CHAMPAGNE : Paleta.SIDEBAR_TEXTO, TAMANO_ICONO)
            );

            getAccessibleContext()
                    .setAccessibleDescription(
                            activo
                                    ? "Seccion actual"
                                    : "Abrir seccion"
                    );

            repaint();
        }

        private void setCompacto(
                boolean compacto) {

            this.compacto = compacto;

            setText(
                    compacto
                            ? ""
                            : seccion.getEtiqueta()
            );

            setToolTipText(
                    compacto
                            ? seccion.getEtiqueta()
                            : null
            );

            setHorizontalAlignment(
                    compacto
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            compacto ? 4 : 14,
                            0,
                            compacto ? 4 : 10
                    )
            );

            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics graphics) {

            Graphics2D g2 =
                    (Graphics2D) graphics.create();

            try {

                configurarCalidad(g2);

                int ancho = getWidth();

                int alto = getHeight();

                if (ancho > 0 && alto > 0) {

                    Color fondo = null;

                    if (activo) {

                        fondo =
                                Paleta.SIDEBAR_ITEM_ACTIVO;

                    } else if (isEnabled()
                            && getModel().isRollover()) {

                        fondo =
                                Paleta.SIDEBAR_ITEM_HOVER;
                    }

                    if (fondo != null) {

                        g2.setColor(fondo);

                        g2.fillRoundRect(
                                1,
                                1,
                                Math.max(0, ancho - 2),
                                Math.max(0, alto - 2),
                                Espaciado.RADIO_MEDIO * 2,
                                Espaciado.RADIO_MEDIO * 2
                        );
                    }

                    // Linea de seleccion champagne.
                    // Se utiliza solamente en el
                    // modo de navegacion expandido.

                    if (activo && !compacto) {

                        g2.setColor(
                                Paleta.SIDEBAR_INDICADOR
                        );

                        g2.fillRoundRect(
                                1,
                                12,
                                Espaciado.SIDEBAR_INDICADOR_ANCHO,
                                Math.max(0, alto - 24),
                                3,
                                3
                        );
                    }

                    if (isFocusOwner()) {

                        g2.setColor(
                                Paleta.CHAMPAGNE
                        );

                        g2.setStroke(
                                new BasicStroke(1.3f)
                        );

                        g2.drawRoundRect(
                                1,
                                1,
                                Math.max(0, ancho - 3),
                                Math.max(0, alto - 3),
                                10,
                                10
                        );
                    }
                }

            } finally {

                g2.dispose();
            }

            super.paintComponent(
                    graphics
            );
        }
    }

    // ========================================================
    // 16. TIPOS DE ICONOS
    // ========================================================

    private enum TipoIcono {

        INICIO,
        CLIENTES,
        MONEDEROS,
        TRANSACCIONES,
        PUNTOS,
        HISTORIAL,
        PROGRAMADAS,
        REPORTES,
        PANEL
    }

    private static TipoIcono tipoIcono(
            Seccion seccion) {

        return switch (seccion) {

            case INICIO ->
                    TipoIcono.INICIO;

            case CLIENTES ->
                    TipoIcono.CLIENTES;

            case MONEDEROS ->
                    TipoIcono.MONEDEROS;

            case TRANSACCIONES ->
                    TipoIcono.TRANSACCIONES;

            case PUNTOS ->
                    TipoIcono.PUNTOS;

            case HISTORIAL ->
                    TipoIcono.HISTORIAL;

            case PROGRAMADAS ->
                    TipoIcono.PROGRAMADAS;

            case REPORTES ->
                    TipoIcono.REPORTES;
        };
    }


    /** Vincula las opciones del menú a los SVG descargados de Lucide. */
    private static Icon iconoLucide(TipoIcono tipo, Color color, int tamano) {
        String archivo = switch (tipo) {
            case INICIO -> "layout-dashboard.svg";
            case CLIENTES -> "users.svg";
            case MONEDEROS -> "wallet.svg";
            case TRANSACCIONES -> "arrow-left-right.svg";
            case PUNTOS -> "award.svg";
            case HISTORIAL -> "history.svg";
            case PROGRAMADAS -> "calendar-clock.svg";
            case REPORTES -> "chart-no-axes-combined.svg";
            case PANEL -> "menu.svg";
        };
        Icon icono = IconosLucide.cargar(archivo, color, tamano);
        return icono != null ? icono : new IconoNavegacion(tipo, color, tamano);
    }

    // ========================================================
    // 17. ICONOGRAFIA VECTORIAL
    // ========================================================

    /**
     * Iconos dibujados con Java2D.
     *
     * No necesitamos PNG, SVG externos
     * ni fuentes especiales de iconos.
     *
     * Cada icono utiliza un lienzo
     * conceptual de 24 x 24 unidades.
     */
    private static final class IconoNavegacion
            implements Icon {

        private final TipoIcono tipo;

        private final Color color;

        private final int tamano;

        private IconoNavegacion(
                TipoIcono tipo,
                Color color,
                int tamano) {

            this.tipo = tipo;

            this.color = color;

            this.tamano = tamano;
        }

        @Override
        public int getIconWidth() {

            return tamano;
        }

        @Override
        public int getIconHeight() {

            return tamano;
        }

        @Override
        public void paintIcon(
                Component componente,
                Graphics graphics,
                int x,
                int y) {

            Graphics2D g2 =
                    (Graphics2D) graphics.create();

            try {

                configurarCalidad(g2);

                g2.translate(x, y);

                double escala =
                        tamano / 24.0;

                g2.scale(
                        escala,
                        escala
                );

                g2.setColor(color);

                g2.setStroke(
                        new BasicStroke(
                                1.75f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                dibujarIcono(g2);

            } finally {

                g2.dispose();
            }
        }

        private void dibujarIcono(
                Graphics2D g2) {

            switch (tipo) {

                case INICIO ->
                        dibujarInicio(g2);

                case CLIENTES ->
                        dibujarClientes(g2);

                case MONEDEROS ->
                        dibujarMonederos(g2);

                case TRANSACCIONES ->
                        dibujarTransacciones(g2);

                case PUNTOS ->
                        dibujarPuntos(g2);

                case HISTORIAL ->
                        dibujarHistorial(g2);

                case PROGRAMADAS ->
                        dibujarProgramadas(g2);

                case REPORTES ->
                        dibujarReportes(g2);

                case PANEL ->
                        dibujarPanel(g2);
            }
        }

        // --------------------------------------
        // OVERVIEW
        // --------------------------------------

        private void dibujarInicio(
                Graphics2D g2) {

            g2.drawRoundRect(3, 3, 7, 7, 1, 1);

            g2.drawRoundRect(14, 3, 7, 7, 1, 1);

            g2.drawRoundRect(3, 14, 7, 7, 1, 1);

            g2.drawRoundRect(14, 14, 7, 7, 1, 1);
        }

        // --------------------------------------
        // CLIENTES
        // --------------------------------------

        private void dibujarClientes(
                Graphics2D g2) {

            g2.drawOval(8, 3, 8, 8);

            g2.drawArc(
                    4, 13, 16, 9,
                    0, 180
            );

            g2.drawArc(
                    1, 7, 7, 9,
                    70, 170
            );

            g2.drawArc(
                    16, 7, 7, 9,
                    -60, 170
            );
        }

        // --------------------------------------
        // MONEDEROS
        // --------------------------------------

        private void dibujarMonederos(
                Graphics2D g2) {

            g2.drawRoundRect(
                    3, 6, 18, 15, 3, 3
            );

            g2.drawLine(
                    4, 6, 17, 6
            );

            g2.drawRoundRect(
                    13, 11, 9, 6, 2, 2
            );

            g2.fillOval(
                    16, 13, 2, 2
            );
        }

        // --------------------------------------
        // TRANSACCIONES
        // --------------------------------------

        private void dibujarTransacciones(
                Graphics2D g2) {

            g2.drawLine(
                    4, 7, 20, 7
            );

            g2.drawLine(
                    16, 3, 20, 7
            );

            g2.drawLine(
                    16, 11, 20, 7
            );

            g2.drawLine(
                    20, 17, 4, 17
            );

            g2.drawLine(
                    8, 13, 4, 17
            );

            g2.drawLine(
                    8, 21, 4, 17
            );
        }

        // --------------------------------------
        // PUNTOS Y RANGOS
        // --------------------------------------

        private void dibujarPuntos(
                Graphics2D g2) {

            g2.drawOval(
                    6, 3, 12, 12
            );

            g2.drawLine(
                    9, 15, 7, 22
            );

            g2.drawLine(
                    7, 22, 12, 19
            );

            g2.drawLine(
                    12, 19, 17, 22
            );

            g2.drawLine(
                    17, 22, 15, 15
            );

            g2.drawLine(
                    12, 6, 12, 12
            );

            g2.drawLine(
                    9, 9, 15, 9
            );
        }

        // --------------------------------------
        // HISTORIAL
        // --------------------------------------

        private void dibujarHistorial(
                Graphics2D g2) {

            g2.drawArc(
                    3, 3, 18, 18,
                    -70, 290
            );

            g2.drawLine(
                    3, 4, 3, 10
            );

            g2.drawLine(
                    3, 10, 9, 10
            );

            g2.drawLine(
                    12, 7, 12, 12
            );

            g2.drawLine(
                    12, 12, 16, 14
            );
        }

        // --------------------------------------
        // PROGRAMADAS
        // --------------------------------------

        private void dibujarProgramadas(
                Graphics2D g2) {

            g2.drawRoundRect(
                    3, 5, 18, 16, 2, 2
            );

            g2.drawLine(
                    3, 10, 21, 10
            );

            g2.drawLine(
                    8, 3, 8, 7
            );

            g2.drawLine(
                    16, 3, 16, 7
            );

            g2.drawLine(
                    8, 15, 10, 17
            );

            g2.drawLine(
                    10, 17, 16, 13
            );
        }

        // --------------------------------------
        // REPORTES
        // --------------------------------------

        private void dibujarReportes(
                Graphics2D g2) {

            g2.drawLine(
                    3, 21, 21, 21
            );

            g2.drawRoundRect(
                    5, 12, 4, 9, 1, 1
            );

            g2.drawRoundRect(
                    11, 8, 4, 13, 1, 1
            );

            g2.drawRoundRect(
                    17, 3, 4, 18, 1, 1
            );
        }

        // --------------------------------------
        // COLAPSAR PANEL
        // --------------------------------------

        private void dibujarPanel(
                Graphics2D g2) {

            g2.drawRoundRect(
                    3, 4, 18, 16, 2, 2
            );

            g2.drawLine(
                    10, 4, 10, 20
            );

            g2.drawLine(
                    15, 9, 18, 12
            );

            g2.drawLine(
                    18, 12, 15, 15
            );
        }

        private static void configurarCalidad(
                Graphics2D g2) {

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE
            );
        }
    }

    // ========================================================
    // 18. CALIDAD GRAFICA GENERAL
    // ========================================================

    private static void configurarCalidad(
            Graphics2D g2) {

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
    }

    // ========================================================
    // 19. INFORMACION DEL COMPONENTE
    // ========================================================

    public String generarDescripcion() {

        return "Sidebar"
                + " | Tema: Institutional Noir"
                + " | Seccion: " + seccionActiva
                + " | Compacta: " + modoCompacto
                + " | Ancho: "
                + (modoCompacto
                ? ANCHO_COMPACTO
                : ANCHO_NORMAL);
    }
}
