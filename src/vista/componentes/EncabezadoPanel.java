
package vista.componentes;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import java.util.Locale;
import java.util.Objects;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * COMPONENTE:
 * EncabezadoPanel.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * 1. Mostrar el titulo de cada pantalla.
 * 2. Presentar un subtitulo descriptivo.
 * 3. Mostrar un identificador editorial.
 * 4. Incorporar una fecha opcional.
 * 5. Administrar acciones del encabezado.
 * 6. Ofrecer variantes de presentacion.
 * 7. Dibujar separadores discretos.
 * 8. Adaptarse a ventanas estrechas.
 *
 * EJEMPLOS:
 *
 * OVERVIEW
 * Tu capital, bajo control.
 *
 * CLIENTES
 * Directorio de cuentas registradas.
 *
 * MONEDEROS
 * Administracion del capital disponible.
 *
 * TRANSACCIONES
 * Depositos, retiros y transferencias.
 *
 * HISTORIAL
 * Archivo cronologico de operaciones.
 *
 * Este componente no ejecuta operaciones
 * financieras ni consulta servicios.
 *
 * Solo administra elementos visuales.
 *
 * Java 21.
 */
public final class EncabezadoPanel extends JPanel {

    // ========================================================
    // 1. VARIANTES
    // ========================================================

    public enum Variante {

        /**
         * Encabezado principal.
         *
         * Titulo grande y composicion
         * editorial.
         */
        EDITORIAL,

        /**
         * Encabezado habitual.
         *
         * Recomendado para los paneles
         * secundarios.
         */
        NORMAL,

        /**
         * Encabezado de menor altura.
         *
         * Ideal para dialogos y secciones.
         */
        COMPACTO
    }

    // ========================================================
    // 2. CONSTANTES
    // ========================================================

    private static final long serialVersionUID = 1L;

    private static final Locale LOCALE_CO =
            Locale.forLanguageTag("es-CO");

    private static final int SEPARACION_SUPERIOR =
            18;

    private static final int SEPARACION_TITULO =
            10;

    private static final int SEPARACION_ACCIONES =
            12;

    private static final int ESPACIO_INFERIOR =
            20;

    private static final int BREAKPOINT_ESTRECHO =
            780;

    private static final String TEXTO_FECHA_PREDETERMINADO =
            "";

    // ========================================================
    // 3. ESTADO
    // ========================================================

    private String titulo;

    private String subtitulo;

    private String identificador;

    private String textoFecha;

    private Variante variante;

    private boolean mostrarIdentificador;

    private boolean mostrarFecha;

    private boolean mostrarSeparador;

    private boolean modoCompactoAutomatico;

    private boolean distribucionEstrecha;

    private Color colorAcento;

    // ========================================================
    // 4. COMPONENTES PRINCIPALES
    // ========================================================

    private JPanel panelSuperior;

    private JPanel panelCentral;

    private JPanel panelTitulos;

    private JPanel panelAcciones;

    private JPanel panelContenido;

    private JLabel etiquetaIdentificador;

    private JLabel etiquetaFecha;

    private JLabel etiquetaTitulo;

    private JLabel etiquetaSubtitulo;

    private JComponent accionPrincipal;

    private JComponent accionSecundaria;

    // ========================================================
    // 5. CONSTRUCTORES
    // ========================================================

    /**
     * Constructor vacio.
     */
    public EncabezadoPanel() {

        this(
                "Overview.",
                "Tu capital, bajo control.",
                "WORKSPACE / 001",
                Variante.EDITORIAL
        );
    }

    /**
     * Constructor con titulo.
     */
    public EncabezadoPanel(
            String titulo) {

        this(
                titulo,
                "",
                "",
                Variante.NORMAL
        );
    }

    /**
     * Constructor con titulo y subtitulo.
     */
    public EncabezadoPanel(
            String titulo,
            String subtitulo) {

        this(
                titulo,
                subtitulo,
                "",
                Variante.NORMAL
        );
    }

    /**
     * Constructor principal.
     */
    public EncabezadoPanel(
            String titulo,
            String subtitulo,
            String identificador,
            Variante variante) {

        super(new BorderLayout());

        this.titulo = textoSeguro(titulo);

        this.subtitulo = textoSeguro(subtitulo);

        this.identificador =
                textoSeguro(identificador);

        this.variante = Objects.requireNonNull(
                variante,
                "La variante no puede ser null"
        );

        this.textoFecha =
                TEXTO_FECHA_PREDETERMINADO;

        this.mostrarIdentificador =
                !this.identificador.isBlank();

        this.mostrarFecha = false;

        this.mostrarSeparador = true;

        this.modoCompactoAutomatico = true;

        this.distribucionEstrecha = false;

        this.colorAcento =
                Paleta.CHAMPAGNE;

        this.accionPrincipal = null;

        this.accionSecundaria = null;

        inicializarComponente();
    }

    // ========================================================
    // 6. INICIALIZACION
    // ========================================================

    private void inicializarComponente() {

        setOpaque(false);

        setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        ESPACIO_INFERIOR,
                        0
                )
        );

        construirComponentes();

        configurarAccesibilidad();

        actualizarEstilos();

        addComponentListener(
                new java.awt.event.ComponentAdapter() {

                    @Override
                    public void componentResized(
                            java.awt.event.ComponentEvent evento) {

                        if (modoCompactoAutomatico) {

                            actualizarDistribucion();
                        }
                    }
                }
        );
    }

    // ========================================================
    // 7. CONSTRUCCION GENERAL
    // ========================================================

    private void construirComponentes() {

        // --------------------------------------
        // PANEL SUPERIOR
        // --------------------------------------

        panelSuperior =
                new JPanel(
                        new BorderLayout()
                );

        panelSuperior.setOpaque(false);

        etiquetaIdentificador =
                new JLabel(identificador);

        etiquetaIdentificador.setFont(
                Tipografia.monoMedium(11f)
        );

        etiquetaIdentificador.setForeground(
                colorAcento
        );

        etiquetaFecha =
                new JLabel(textoFecha);

        etiquetaFecha.setFont(
                Tipografia.mono(11f)
        );

        etiquetaFecha.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiquetaFecha.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        panelSuperior.add(
                etiquetaIdentificador,
                BorderLayout.WEST
        );

        panelSuperior.add(
                etiquetaFecha,
                BorderLayout.EAST
        );

        // --------------------------------------
        // TITULOS
        // --------------------------------------

        panelTitulos =
                new JPanel();

        panelTitulos.setOpaque(false);

        panelTitulos.setLayout(
                new BoxLayout(
                        panelTitulos,
                        BoxLayout.Y_AXIS
                )
        );

        etiquetaTitulo =
                new JLabel(titulo);

        etiquetaTitulo.setForeground(
                Paleta.TEXTO_PRINCIPAL
        );

        etiquetaSubtitulo =
                new JLabel(subtitulo);

        etiquetaSubtitulo.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiquetaTitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        etiquetaSubtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelTitulos.add(
                etiquetaTitulo
        );

        panelTitulos.add(
                Box.createVerticalStrut(
                        SEPARACION_TITULO
                )
        );

        panelTitulos.add(
                etiquetaSubtitulo
        );

        // --------------------------------------
        // ACCIONES
        // --------------------------------------

        panelAcciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                SEPARACION_ACCIONES,
                                0
                        )
                );

        panelAcciones.setOpaque(false);

        // --------------------------------------
        // PANEL CENTRAL
        // --------------------------------------

        panelCentral =
                new JPanel(
                        new BorderLayout(
                                Espaciado.XXL,
                                0
                        )
                );

        panelCentral.setOpaque(false);

        panelCentral.add(
                panelTitulos,
                BorderLayout.CENTER
        );

        panelCentral.add(
                panelAcciones,
                BorderLayout.EAST
        );

        // --------------------------------------
        // CONTENIDO GENERAL
        // --------------------------------------

        panelContenido =
                new JPanel(
                        new BorderLayout(
                                0,
                                SEPARACION_SUPERIOR
                        )
                );

        panelContenido.setOpaque(false);

        panelContenido.add(
                panelSuperior,
                BorderLayout.NORTH
        );

        panelContenido.add(
                panelCentral,
                BorderLayout.CENTER
        );

        add(
                panelContenido,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 8. ESTILOS SEGUN VARIANTE
    // ========================================================

    private void actualizarEstilos() {

        Font fuenteTitulo;

        Font fuenteSubtitulo;

        int separacionInferior;

        switch (variante) {

            case EDITORIAL -> {

                fuenteTitulo =
                        Tipografia.tituloEditorial();

                fuenteSubtitulo =
                        Tipografia.cuerpoGrande();

                separacionInferior = 28;
            }

            case NORMAL -> {

                fuenteTitulo =
                        Tipografia.tituloPagina();

                fuenteSubtitulo =
                        Tipografia.cuerpo();

                separacionInferior = 24;
            }

            case COMPACTO -> {

                fuenteTitulo =
                        Tipografia.tituloTarjeta();

                fuenteSubtitulo =
                        Tipografia.cuerpoPequeno();

                separacionInferior = 16;
            }

            default -> {

                fuenteTitulo =
                        Tipografia.tituloPagina();

                fuenteSubtitulo =
                        Tipografia.cuerpo();

                separacionInferior = 24;
            }
        }

        etiquetaTitulo.setFont(
                fuenteTitulo
        );

        etiquetaTitulo.setForeground(
                Paleta.TEXTO_PRINCIPAL
        );

        etiquetaSubtitulo.setFont(
                fuenteSubtitulo
        );

        etiquetaSubtitulo.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiquetaIdentificador.setForeground(
                colorAcento
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        separacionInferior,
                        0
                )
        );

        etiquetaIdentificador.setVisible(
                mostrarIdentificador
                        && !identificador.isBlank()
        );

        etiquetaFecha.setVisible(
                mostrarFecha
                        && !textoFecha.isBlank()
        );

        etiquetaSubtitulo.setVisible(
                !subtitulo.isBlank()
        );

        panelSuperior.setVisible(
                etiquetaIdentificador.isVisible()
                        || etiquetaFecha.isVisible()
        );

        actualizarAcciones();

        actualizarDistribucion();

        actualizarAccesibilidad();

        revalidate();

        repaint();
    }

    // ========================================================
    // 9. DISTRIBUCION ADAPTABLE
    // ========================================================

    /**
     * En ventanas estrechas, los botones
     * aparecen debajo del titulo.
     *
     * En ventanas amplias, se ubican
     * a la derecha.
     *
     * No utiliza posiciones absolutas.
     */
    private void actualizarDistribucion() {

        if (panelCentral == null) {
            return;
        }

        boolean estrecho =
                modoCompactoAutomatico
                        && getWidth() > 0
                        && getWidth()
                        < BREAKPOINT_ESTRECHO;

        if (distribucionEstrecha == estrecho) {
            return;
        }

        distribucionEstrecha = estrecho;

        panelCentral.removeAll();

        if (estrecho) {

            JPanel vertical =
                    new JPanel();

            vertical.setOpaque(false);

            vertical.setLayout(
                    new BoxLayout(
                            vertical,
                            BoxLayout.Y_AXIS
                    )
            );

            panelTitulos.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            panelAcciones.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            vertical.add(panelTitulos);

            if (tieneAcciones()) {

                vertical.add(
                        Box.createVerticalStrut(
                                Espaciado.XL
                        )
                );

                vertical.add(panelAcciones);
            }

            panelCentral.add(
                    vertical,
                    BorderLayout.CENTER
            );

        } else {

            panelCentral.add(
                    panelTitulos,
                    BorderLayout.CENTER
            );

            panelCentral.add(
                    panelAcciones,
                    BorderLayout.EAST
            );
        }

        panelCentral.revalidate();

        panelCentral.repaint();
    }

    // ========================================================
    // 10. ACTUALIZAR ACCIONES
    // ========================================================

    private void actualizarAcciones() {

        panelAcciones.removeAll();

        if (accionSecundaria != null) {

            panelAcciones.add(
                    accionSecundaria
            );
        }

        if (accionPrincipal != null) {

            panelAcciones.add(
                    accionPrincipal
            );
        }

        panelAcciones.setVisible(
                tieneAcciones()
        );

        panelAcciones.revalidate();

        panelAcciones.repaint();
    }

    private boolean tieneAcciones() {

        return accionPrincipal != null
                || accionSecundaria != null;
    }

    // ========================================================
    // 11. TITULO
    // ========================================================

    public void setTitulo(
            String titulo) {

        this.titulo =
                textoSeguro(titulo);

        etiquetaTitulo.setText(
                this.titulo
        );

        actualizarAccesibilidad();

        revalidate();

        repaint();
    }

    public String getTitulo() {

        return titulo;
    }

    // ========================================================
    // 12. SUBTITULO
    // ========================================================

    public void setSubtitulo(
            String subtitulo) {

        this.subtitulo =
                textoSeguro(subtitulo);

        etiquetaSubtitulo.setText(
                this.subtitulo
        );

        etiquetaSubtitulo.setVisible(
                !this.subtitulo.isBlank()
        );

        actualizarAccesibilidad();

        revalidate();

        repaint();
    }

    public String getSubtitulo() {

        return subtitulo;
    }

    // ========================================================
    // 13. IDENTIFICADOR EDITORIAL
    // ========================================================

    public void setIdentificador(
            String identificador) {

        this.identificador =
                textoSeguro(identificador);

        etiquetaIdentificador.setText(
                this.identificador
        );

        actualizarEstilos();
    }

    public String getIdentificador() {

        return identificador;
    }

    public void setMostrarIdentificador(
            boolean mostrar) {

        mostrarIdentificador = mostrar;

        actualizarEstilos();
    }

    public boolean isMostrarIdentificador() {

        return mostrarIdentificador;
    }

    // ========================================================
    // 14. FECHA
    // ========================================================

    /**
     * Muestra una fecha proporcionada
     * por el controlador o la vista.
     *
     * No incorpora un temporizador.
     */
    public void setFecha(
            LocalDate fecha) {

        if (fecha == null) {

            textoFecha = "";

            mostrarFecha = false;

        } else {

            textoFecha =
                    formatearFecha(fecha);

            mostrarFecha = true;
        }

        etiquetaFecha.setText(
                textoFecha
        );

        actualizarEstilos();
    }

    /**
     * Permite establecer texto libre.
     *
     * Ejemplos:
     *
     * "17 NOV 2026"
     * "ACTUALIZADO HOY"
     */
    public void setTextoFecha(
            String texto) {

        textoFecha =
                textoSeguro(texto);

        mostrarFecha =
                !textoFecha.isBlank();

        etiquetaFecha.setText(
                textoFecha
        );

        actualizarEstilos();
    }

    public String getTextoFecha() {

        return textoFecha;
    }

    public void setMostrarFecha(
            boolean mostrar) {

        mostrarFecha = mostrar;

        actualizarEstilos();
    }

    public boolean isMostrarFecha() {

        return mostrarFecha;
    }

    // ========================================================
    // 15. FORMATO DE FECHA
    // ========================================================

    /**
     * Formato editorial:
     *
     * 17 NOV 2026
     */
    public static String formatearFecha(
            LocalDate fecha) {

        Objects.requireNonNull(
                fecha,
                "La fecha no puede ser null"
        );

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd MMM yyyy",
                        LOCALE_CO
                );

        return fecha.format(formato)
                .toUpperCase(LOCALE_CO);
    }

    // ========================================================
    // 16. VARIANTE
    // ========================================================

    public void setVariante(
            Variante variante) {

        this.variante =
                Objects.requireNonNull(
                        variante,
                        "La variante no puede ser null"
                );

        actualizarEstilos();
    }

    public Variante getVariante() {

        return variante;
    }

    // ========================================================
    // 17. COLOR DE ACENTO
    // ========================================================

    public void setColorAcento(
            Color color) {

        colorAcento =
                Objects.requireNonNull(
                        color,
                        "El color no puede ser null"
                );

        etiquetaIdentificador.setForeground(
                colorAcento
        );

        repaint();
    }

    public Color getColorAcento() {

        return colorAcento;
    }

    // ========================================================
    // 18. ACCION PRINCIPAL
    // ========================================================

    /**
     * Permite instalar cualquier componente
     * Swing como accion principal.
     *
     * Se recomienda BotonPremium.
     */
    public void setAccionPrincipal(
            JComponent componente) {

        accionPrincipal = componente;

        actualizarAcciones();

        actualizarDistribucion();

        revalidate();
    }

    public JComponent getAccionPrincipal() {

        return accionPrincipal;
    }

    /**
     * Crea automaticamente un
     * BotonPremium primario.
     */
    public BotonPremium crearAccionPrincipal(
            String texto,
            Runnable accion) {

        BotonPremium boton =
                BotonPremium.primario(
                        textoSeguro(texto)
                );

        if (accion != null) {

            boton.addActionListener(
                    evento -> accion.run()
            );
        }

        setAccionPrincipal(
                boton
        );

        return boton;
    }

    // ========================================================
    // 19. ACCION SECUNDARIA
    // ========================================================

    public void setAccionSecundaria(
            JComponent componente) {

        accionSecundaria = componente;

        actualizarAcciones();

        actualizarDistribucion();

        revalidate();
    }

    public JComponent getAccionSecundaria() {

        return accionSecundaria;
    }

    /**
     * Crea automaticamente un
     * BotonPremium secundario.
     */
    public BotonPremium crearAccionSecundaria(
            String texto,
            Runnable accion) {

        BotonPremium boton =
                BotonPremium.secundario(
                        textoSeguro(texto)
                );

        if (accion != null) {

            boton.addActionListener(
                    evento -> accion.run()
            );
        }

        setAccionSecundaria(
                boton
        );

        return boton;
    }

    // ========================================================
    // 20. ELIMINAR ACCIONES
    // ========================================================

    public void limpiarAcciones() {

        accionPrincipal = null;

        accionSecundaria = null;

        actualizarAcciones();

        actualizarDistribucion();

        revalidate();

        repaint();
    }

    // ========================================================
    // 21. SEPARADOR EDITORIAL
    // ========================================================

    /**
     * El separador se dibuja como una linea
     * horizontal muy tenue.
     *
     * No requiere otro componente Swing.
     */
    @Override
    protected void paintComponent(
            Graphics graphics) {

        super.paintComponent(graphics);

        if (!mostrarSeparador) {
            return;
        }

        Graphics2D g2 =
                (Graphics2D) graphics.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    Paleta.DIVIDER
            );

            int y =
                    getHeight() - 1;

            if (y >= 0) {

                g2.drawLine(
                        0,
                        y,
                        Math.max(0, getWidth() - 1),
                        y
                );
            }

        } finally {

            g2.dispose();
        }
    }

    // ========================================================
    // 22. CONFIGURAR SEPARADOR
    // ========================================================

    public void setMostrarSeparador(
            boolean mostrar) {

        mostrarSeparador = mostrar;

        repaint();
    }

    public boolean isMostrarSeparador() {

        return mostrarSeparador;
    }

    // ========================================================
    // 23. DISTRIBUCION AUTOMATICA
    // ========================================================

    public void setModoCompactoAutomatico(
            boolean habilitado) {

        modoCompactoAutomatico = habilitado;

        actualizarDistribucion();

        revalidate();

        repaint();
    }

    public boolean isModoCompactoAutomatico() {

        return modoCompactoAutomatico;
    }

    public boolean isDistribucionEstrecha() {

        return distribucionEstrecha;
    }

    // ========================================================
    // 24. ACCESIBILIDAD
    // ========================================================

    private void configurarAccesibilidad() {

        actualizarAccesibilidad();
    }

    private void actualizarAccesibilidad() {

        if (getAccessibleContext() == null) {
            return;
        }

        getAccessibleContext()
                .setAccessibleName(
                        titulo
                );

        getAccessibleContext()
                .setAccessibleDescription(
                        titulo
                                + ". "
                                + subtitulo
                );
    }

    // ========================================================
    // 25. ACTUALIZACION COMPLETA
    // ========================================================

    /**
     * Permite reutilizar el encabezado
     * al cambiar de pantalla.
     */
    public void actualizar(
            String titulo,
            String subtitulo,
            String identificador) {

        this.titulo =
                textoSeguro(titulo);

        this.subtitulo =
                textoSeguro(subtitulo);

        this.identificador =
                textoSeguro(identificador);

        etiquetaTitulo.setText(
                this.titulo
        );

        etiquetaSubtitulo.setText(
                this.subtitulo
        );

        etiquetaIdentificador.setText(
                this.identificador
        );

        mostrarIdentificador =
                !this.identificador.isBlank();

        actualizarEstilos();
    }

    // ========================================================
    // 26. ACTUALIZACION EN EDT
    // ========================================================

    /**
     * Programa una actualizacion segura
     * en el hilo de eventos de Swing.
     */
    public void actualizarEnInterfaz(
            String titulo,
            String subtitulo,
            String identificador) {

        if (SwingUtilities.isEventDispatchThread()) {

            actualizar(
                    titulo,
                    subtitulo,
                    identificador
            );

        } else {

            SwingUtilities.invokeLater(() -> {

                actualizar(
                        titulo,
                        subtitulo,
                        identificador
                );
            });
        }
    }

    // ========================================================
    // 27. GETTERS DE COMPONENTES
    // ========================================================

    public JLabel getEtiquetaTitulo() {

        return etiquetaTitulo;
    }

    public JLabel getEtiquetaSubtitulo() {

        return etiquetaSubtitulo;
    }

    public JLabel getEtiquetaIdentificador() {

        return etiquetaIdentificador;
    }

    public JLabel getEtiquetaFecha() {

        return etiquetaFecha;
    }

    // ========================================================
    // 28. METODOS DE FABRICA
    // ========================================================

    /**
     * Encabezado principal del dashboard.
     */
    public static EncabezadoPanel overview() {

        EncabezadoPanel encabezado =
                new EncabezadoPanel(
                        "Overview.",
                        "Tu capital. Cada movimiento, bajo control.",
                        "WORKSPACE / 001",
                        Variante.EDITORIAL
                );

        encabezado.setFecha(
                LocalDate.now()
        );

        return encabezado;
    }

    /**
     * Encabezado para clientes.
     */
    public static EncabezadoPanel clientes() {

        return new EncabezadoPanel(
                "Clientes.",
                "Directorio de cuentas registradas.",
                "WORKSPACE / 002",
                Variante.NORMAL
        );
    }

    /**
     * Encabezado para monederos.
     */
    public static EncabezadoPanel monederos() {

        return new EncabezadoPanel(
                "Monederos.",
                "Administracion del capital disponible.",
                "WORKSPACE / 003",
                Variante.NORMAL
        );
    }

    /**
     * Encabezado para transacciones.
     */
    public static EncabezadoPanel transacciones() {

        return new EncabezadoPanel(
                "Transacciones.",
                "Depositos, retiros y transferencias.",
                "WORKSPACE / 004",
                Variante.NORMAL
        );
    }

    /**
     * Encabezado para puntos.
     */
    public static EncabezadoPanel puntos() {

        return new EncabezadoPanel(
                "Puntos y rangos.",
                "Beneficios, progresion y recompensas.",
                "WORKSPACE / 005",
                Variante.NORMAL
        );
    }

    /**
     * Encabezado para historial.
     */
    public static EncabezadoPanel historial() {

        return new EncabezadoPanel(
                "Historial.",
                "Archivo cronologico de movimientos.",
                "WORKSPACE / 006",
                Variante.NORMAL
        );
    }

    /**
     * Encabezado para programaciones.
     */
    public static EncabezadoPanel programadas() {

        return new EncabezadoPanel(
                "Programadas.",
                "Operaciones futuras y recurrentes.",
                "WORKSPACE / 007",
                Variante.NORMAL
        );
    }

    /**
     * Encabezado para reportes.
     */
    public static EncabezadoPanel reportes() {

        return new EncabezadoPanel(
                "Reportes.",
                "Analisis, estadisticas e integridad.",
                "WORKSPACE / 008",
                Variante.NORMAL
        );
    }

    // ========================================================
    // 29. VALIDACION DE TEXTO
    // ========================================================

    private static String textoSeguro(
            String texto) {

        return texto == null ? "" : texto;
    }

    // ========================================================
    // 30. INFORMACION DEL COMPONENTE
    // ========================================================

    public String generarDescripcion() {

        return "EncabezadoPanel"
                + " | Titulo: " + titulo
                + " | Variante: " + variante
                + " | Identificador: " + identificador
                + " | Fecha: " + textoFecha
                + " | Accion principal: "
                + (accionPrincipal != null)
                + " | Accion secundaria: "
                + (accionSecundaria != null);
    }
}
