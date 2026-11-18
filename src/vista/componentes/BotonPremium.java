package vista.componentes;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import java.util.Objects;
import java.util.Locale;
import java.text.Normalizer;

/**
 * ============================================================
 *                     MONEDERO VIRTUAL
 * ============================================================
 *
 * COMPONENTE:
 * BotonPremium.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * - Dibujar botones con Java2D.
 * - Aplicar la paleta oficial.
 * - Gestionar diferentes variantes.
 * - Animar cambios de estado.
 * - Mantener compatibilidad con JButton.
 * - Permitir acciones mediante ActionListener.
 * - Proporcionar un foco de teclado visible.
 *
 * VARIANTES:
 *
 * PRIMARIO:
 * Fondo champagne y texto oscuro.
 *
 * SECUNDARIO:
 * Fondo grafito y texto claro.
 *
 * FANTASMA:
 * Fondo transparente y hover discreto.
 *
 * PELIGRO:
 * Fondo terracota para acciones destructivas.
 *
 * El componente no contiene logica financiera.
 *
 * Java 21.
 */
public class BotonPremium extends JButton {

    // ========================================================
    // 1. VARIANTES
    // ========================================================

    public enum Variante {

        PRIMARIO,
        SECUNDARIO,
        FANTASMA,
        PELIGRO
    }

    // ========================================================
    // 2. CONFIGURACION
    // ========================================================

    private static final int INTERVALO_ANIMACION =
            Espaciado.ANIMACION_FRAME_MS;

    private static final int DURACION_ANIMACION =
            Espaciado.ANIMACION_HOVER;

    private static final float VELOCIDAD_PRESION =
            0.35f;

    private static final float VELOCIDAD_LIBERACION =
            0.25f;

    private static final int ALFA_MINIMO =
            0;

    private static final int ALFA_MAXIMO =
            255;

    // ========================================================
    // 3. ESTADO VISUAL
    // ========================================================

    private Variante variante;

    private int radio;

    private boolean animacionesHabilitadas;

    private boolean contornoVisible;

    private float progresoHover;

    private float progresoPresion;

    private float destinoHover;

    private float destinoPresion;

    private Timer temporizador;

    // Nombre del recurso Lucide asignado al boton.
    // Null indica icono personalizado o boton sin icono.
    private String archivoIconoLucide;

    private int tamanoIconoLucide = 17;

    // ========================================================
    // 4. CONSTRUCTORES
    // ========================================================

    /**
     * Crea un boton primario sin texto.
     */
    public BotonPremium() {

        this("", Variante.PRIMARIO);
    }

    /**
     * Crea un boton primario.
     */
    public BotonPremium(String texto) {

        this(texto, Variante.PRIMARIO);
    }

    /**
     * Crea un boton con variante.
     */
    public BotonPremium(
            String texto,
            Variante variante) {

        super(texto == null ? "" : texto);

        this.variante = Objects.requireNonNull(
                variante,
                "La variante no puede ser null"
        );

        this.radio =
                Espaciado.RADIO_BOTON;

        this.animacionesHabilitadas = true;

        this.contornoVisible = true;

        this.progresoHover = 0f;

        this.progresoPresion = 0f;

        this.destinoHover = 0f;

        this.destinoPresion = 0f;

        inicializarComponente();
        asignarIconoAutomatico();
    }

    /**
     * Crea un boton con icono.
     */
    public BotonPremium(
            String texto,
            Icon icono,
            Variante variante) {

        this(texto, variante);

        // Los iconos entregados explicitamente siempre prevalecen.
        if (icono != null) {
            archivoIconoLucide = null;
            setIcon(icono);
        }
    }

    // ========================================================
    // 5. INICIALIZACION
    // ========================================================

    private void inicializarComponente() {

        setFont(
                Tipografia.boton()
        );

        setForeground(
                colorTextoNormal()
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

        setHorizontalAlignment(CENTER);

        setVerticalAlignment(CENTER);

        setHorizontalTextPosition(
                TRAILING
        );

        setVerticalTextPosition(
                CENTER
        );

        setIconTextGap(
                Espaciado.BOTON_SEPARACION_ICONO
        );

        setMargin(
                new Insets(
                        0, 0, 0, 0
                )
        );

        setBorder(
                new EmptyBorder(
                        0,
                        Espaciado.BOTON_PADDING_HORIZONTAL,
                        0,
                        Espaciado.BOTON_PADDING_HORIZONTAL
                )
        );

        setPreferredSize(
                new Dimension(
                        Espaciado.BOTON_ANCHO_ACCION,
                        Espaciado.BOTON_ALTURA
                )
        );

        setMinimumSize(
                new Dimension(
                        44,
                        Espaciado.BOTON_ALTURA_PEQUENO
                )
        );

        configurarEventos();

        configurarAnimacion();
    }

    // ========================================================
    // ICONOS LUCIDE - RECURSOS REALES
    // ========================================================

    /**
     * Asigna automaticamente un icono segun la accion del boton.
     * No altera los ActionListener ni el texto mostrado.
     */
    private void asignarIconoAutomatico() {
        String archivo = inferirIcono(getText());
        if (archivo != null) {
            setIconoLucide(archivo);
        }
    }

    /**
     * Permite elegir un icono SVG descargado en recursos/iconos.
     * Ejemplo: boton.setIconoLucide("plus.svg");
     * Pasar null elimina el icono administrado por Lucide.
     */
    public void setIconoLucide(String archivo) {
        if (archivo == null || archivo.isBlank()) {
            archivoIconoLucide = null;
            setIcon(null);
            setDisabledIcon(null);
            revalidate();
            repaint();
            return;
        }
        if (!archivo.matches("[a-z0-9-]+\\.svg")) {
            throw new IllegalArgumentException("Nombre SVG no valido: " + archivo);
        }
        archivoIconoLucide = archivo;
        refrescarIconoLucide();
    }

    public String getIconoLucide() {
        return archivoIconoLucide;
    }

    public void setTamanoIconoLucide(int tamano) {
        if (tamano < 8 || tamano > 48) {
            throw new IllegalArgumentException("El tamano debe estar entre 8 y 48");
        }
        tamanoIconoLucide = tamano;
        refrescarIconoLucide();
    }

    /**
     * Respeta el texto actual, incluidos sinonimos frecuentes.
     * Para acciones ambiguas se puede usar setIconoLucide().
     */
    private static String inferirIcono(String texto) {
        if (texto == null || texto.isBlank()) return null;
        String t = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).trim();
        if (t.startsWith("crear") || t.startsWith("agregar")
                || t.startsWith("anadir") || t.startsWith("nuevo")
                || t.startsWith("registrar") || t.startsWith("programar")) {
            return "plus.svg";
        }
        if (t.startsWith("actualizar") || t.startsWith("refrescar")
                || t.startsWith("recargar") || t.startsWith("sincronizar")) {
            return "refresh-cw.svg";
        }
        if (t.startsWith("buscar") || t.startsWith("consultar")) {
            return "search.svg";
        }
        if (t.startsWith("descargar") || t.startsWith("exportar")) {
            return "download.svg";
        }
        if (t.startsWith("cancelar") || t.startsWith("descartar")) {
            return "x-circle.svg";
        }
        return null;
    }

    /**
     * Icono del color adecuado, mas variante deshabilitada.
     * En JButton el UI delegate dibuja el icono sobre el fondo Java2D.
     */
    private void refrescarIconoLucide() {
        if (archivoIconoLucide == null || variante == null) return;
        Color normal = colorTextoNormal();
        Icon icono = IconosLucide.cargar(
                archivoIconoLucide, normal, tamanoIconoLucide);
        if (icono == null) {
            // No interferir con la operacion si falta un recurso.
            setIcon(null);
            setDisabledIcon(null);
            System.err.println("[BOTON] Icono Lucide no disponible: " + archivoIconoLucide);
            return;
        }
        setIcon(icono);
        setDisabledIcon(IconosLucide.cargar(
                archivoIconoLucide, Paleta.TEXTO_DESHABILITADO,
                tamanoIconoLucide));
        revalidate();
        repaint();
    }

    // ========================================================
    // 6. EVENTOS DEL COMPONENTE
    // ========================================================

    private void configurarEventos() {

        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent evento) {

                        actualizarDestinoHover(true);
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent evento) {

                        actualizarDestinoHover(false);

                        actualizarDestinoPresion(false);
                    }

                    @Override
                    public void mousePressed(
                            MouseEvent evento) {

                        if (isEnabled()
                                && SwingUtilities
                                .isLeftMouseButton(evento)) {

                            actualizarDestinoPresion(true);
                        }
                    }

                    @Override
                    public void mouseReleased(
                            MouseEvent evento) {

                        actualizarDestinoPresion(false);
                    }
                }
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

                        actualizarDestinoPresion(false);

                        repaint();
                    }
                }
        );

        addHierarchyListener(evento -> {

            if ((evento.getChangeFlags()
                    & HierarchyEvent.SHOWING_CHANGED) != 0
                    && !isShowing()) {

                detenerAnimacion();
            }
        });

        // El ButtonModel nativo de Swing
        // controla los eventos del teclado.
        //
        // Esto permite activar el boton
        // mediante Espacio y evita duplicar
        // los ActionEvent existentes.

        getModel().addChangeListener(evento -> {

            boolean presionado =
                    isEnabled()
                            && getModel().isPressed();

            actualizarDestinoPresion(
                    presionado
            );

            repaint();
        });
    }

    // ========================================================
    // 7. TEMPORIZADOR DE ANIMACION
    // ========================================================

    private void configurarAnimacion() {

        temporizador = new Timer(
                INTERVALO_ANIMACION,
                evento -> avanzarAnimacion()
        );

        temporizador.setRepeats(true);

        temporizador.setCoalesce(true);
    }

    // ========================================================
    // 8. ACTUALIZACION DE HOVER
    // ========================================================

    private void actualizarDestinoHover(
            boolean activo) {

        destinoHover =
                activo && isEnabled()
                        ? 1f
                        : 0f;

        iniciarAnimacion();
    }

    // ========================================================
    // 9. ACTUALIZACION DE PRESION
    // ========================================================

    private void actualizarDestinoPresion(
            boolean activo) {

        destinoPresion =
                activo && isEnabled()
                        ? 1f
                        : 0f;

        iniciarAnimacion();
    }

    // ========================================================
    // 10. INICIAR ANIMACION
    // ========================================================

    private void iniciarAnimacion() {

        if (!animacionesHabilitadas) {

            progresoHover = destinoHover;

            progresoPresion = destinoPresion;

            detenerAnimacion();

            repaint();

            return;
        }

        if (!isShowing()) {

            progresoHover = destinoHover;

            progresoPresion = destinoPresion;

            repaint();

            return;
        }

        if (!temporizador.isRunning()) {

            temporizador.start();
        }
    }

    // ========================================================
    // 11. AVANZAR ANIMACION
    // ========================================================

    private void avanzarAnimacion() {

        float avanceHover =
                Math.min(
                        1f,
                        (float) INTERVALO_ANIMACION
                                / DURACION_ANIMACION
                                * 2.0f
                );

        progresoHover = aproximar(
                progresoHover,
                destinoHover,
                avanceHover
        );

        float velocidadPresion =
                destinoPresion > progresoPresion
                        ? VELOCIDAD_PRESION
                        : VELOCIDAD_LIBERACION;

        progresoPresion = aproximar(
                progresoPresion,
                destinoPresion,
                velocidadPresion
        );

        repaint();

        boolean hoverTerminado =
                Math.abs(
                        progresoHover - destinoHover
                ) < 0.001f;

        boolean presionTerminada =
                Math.abs(
                        progresoPresion - destinoPresion
                ) < 0.001f;

        if (hoverTerminado && presionTerminada) {

            progresoHover = destinoHover;

            progresoPresion = destinoPresion;

            detenerAnimacion();
        }
    }

    // ========================================================
    // 12. INTERPOLACION
    // ========================================================

    private float aproximar(
            float actual,
            float objetivo,
            float velocidad) {

        float nuevo =
                actual + (objetivo - actual)
                        * velocidad;

        if (Math.abs(nuevo - objetivo) < 0.001f) {

            return objetivo;
        }

        return nuevo;
    }

    // ========================================================
    // 13. DETENER ANIMACION
    // ========================================================

    private void detenerAnimacion() {

        if (temporizador != null
                && temporizador.isRunning()) {

            temporizador.stop();
        }
    }

    // ========================================================
    // 14. PINTADO PRINCIPAL
    // ========================================================

    @Override
    protected void paintComponent(
            Graphics graphics) {

        Graphics2D g2 =
                (Graphics2D) graphics.create();

        try {

            configurarCalidad(g2);

            int ancho = getWidth();

            int alto = getHeight();

            if (ancho <= 0 || alto <= 0) {
                return;
            }

            // Mantener un margen interior
            // para el indicador de foco.

            int margen = 3;

            float x = margen;

            float y = margen;

            float w = Math.max(
                    0,
                    ancho - margen * 2
            );

            float h = Math.max(
                    0,
                    alto - margen * 2
            );

            float arco =
                    radio * 2f;

            RoundRectangle2D.Float forma =
                    new RoundRectangle2D.Float(
                            x,
                            y,
                            w,
                            h,
                            arco,
                            arco
                    );

            // ----------------------------------
            // FONDO
            // ----------------------------------

            Color fondo =
                    calcularColorFondo();

            if (fondo.getAlpha() > 0) {

                g2.setColor(fondo);

                g2.fill(forma);
            }

            // ----------------------------------
            // CONTORNO
            // ----------------------------------

            if (contornoVisible) {

                Color borde =
                        calcularColorBorde();

                if (borde.getAlpha() > 0) {

                    g2.setStroke(
                            new BasicStroke(1f)
                    );

                    g2.setColor(borde);

                    g2.draw(forma);
                }
            }

            // ----------------------------------
            // INDICADOR DE FOCO
            // ----------------------------------

            if (isFocusOwner() && isEnabled()) {

                g2.setColor(
                        Paleta.CHAMPAGNE
                );

                g2.setStroke(
                        new BasicStroke(1.5f)
                );

                RoundRectangle2D.Float foco =
                        new RoundRectangle2D.Float(
                                1f,
                                1f,
                                Math.max(0, ancho - 2f),
                                Math.max(0, alto - 2f),
                                arco + 4f,
                                arco + 4f
                        );

                g2.draw(foco);
            }

        } finally {

            g2.dispose();
        }

        // JButton pinta el texto y el icono
        // mediante su delegado UI.
        //
        // Como desactivamos contentAreaFilled
        // y borderPainted, el delegado no
        // debe pintar el fondo del boton.
        //
        // El foco se dibuja arriba.

        super.paintComponent(graphics);
    }

    // ========================================================
    // 15. CALIDAD GRAFICA
    // ========================================================

    private void configurarCalidad(
            Graphics2D g2) {

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        g2.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE
        );
    }

    // ========================================================
    // 16. COLORES DE FONDO
    // ========================================================

    private Color calcularColorFondo() {

        if (!isEnabled()) {

            return switch (variante) {

                case PRIMARIO ->
                        Paleta.ACENTO_DESHABILITADO;

                case SECUNDARIO, PELIGRO ->
                        Paleta.FONDO_TERCIARIO;

                case FANTASMA ->
                        Paleta.conAlpha(
                                Paleta.FONDO_TERCIARIO,
                                0
                        );
            };
        }

        Color normal =
                colorFondoNormal();

        Color hover =
                colorFondoHover();

        Color presionado =
                colorFondoPresionado();

        Color resultado =
                Paleta.mezclar(
                        normal,
                        hover,
                        progresoHover
                );

        resultado =
                Paleta.mezclar(
                        resultado,
                        presionado,
                        progresoPresion
                );

        return resultado;
    }

    // ========================================================
    // 17. COLOR DE FONDO NORMAL
    // ========================================================

    private Color colorFondoNormal() {

        return switch (variante) {

            case PRIMARIO ->
                    Paleta.BOTON_PRIMARIO;

            case SECUNDARIO ->
                    Paleta.BOTON_SECUNDARIO;

            case FANTASMA ->
                    Paleta.conAlpha(
                            Paleta.BOTON_FANTASMA_HOVER,
                            ALFA_MINIMO
                    );

            case PELIGRO ->
                    Paleta.BOTON_PELIGRO;
        };
    }

    // ========================================================
    // 18. COLOR DE FONDO HOVER
    // ========================================================

    private Color colorFondoHover() {

        return switch (variante) {

            case PRIMARIO ->
                    Paleta.BOTON_PRIMARIO_HOVER;

            case SECUNDARIO ->
                    Paleta.BOTON_SECUNDARIO_HOVER;

            case FANTASMA ->
                    Paleta.BOTON_FANTASMA_HOVER;

            case PELIGRO ->
                    Paleta.BOTON_PELIGRO_HOVER;
        };
    }

    // ========================================================
    // 19. COLOR DE FONDO PRESIONADO
    // ========================================================

    private Color colorFondoPresionado() {

        return switch (variante) {

            case PRIMARIO ->
                    Paleta.BOTON_PRIMARIO_PRESIONADO;

            case SECUNDARIO ->
                    Paleta.oscurecer(
                            Paleta.BOTON_SECUNDARIO,
                            0.18
                    );

            case FANTASMA ->
                    Paleta.FONDO_TERCIARIO;

            case PELIGRO ->
                    Paleta.oscurecer(
                            Paleta.BOTON_PELIGRO,
                            0.18
                    );
        };
    }

    // ========================================================
    // 20. COLOR DEL CONTORNO
    // ========================================================

    private Color calcularColorBorde() {

        if (!isEnabled()) {

            return Paleta.conAlpha(
                    Paleta.BORDE_SUAVE,
                    100
            );
        }

        return switch (variante) {

            case PRIMARIO ->
                    Paleta.conAlpha(
                            Paleta.CHAMPAGNE,
                            0
                    );

            case SECUNDARIO ->
                    Paleta.mezclar(
                            Paleta.BORDE_SUAVE,
                            Paleta.BORDE_FUERTE,
                            progresoHover
                    );

            case FANTASMA ->
                    Paleta.conAlpha(
                            Paleta.BORDE_SUAVE,
                            (int) (
                                    110 * progresoHover
                            )
                    );

            case PELIGRO ->
                    Paleta.conAlpha(
                            Paleta.CLAY,
                            90
                    );
        };
    }

    // ========================================================
    // 21. COLOR DEL TEXTO
    // ========================================================

    private Color colorTextoNormal() {

        if (!isEnabled()) {

            return Paleta.TEXTO_DESHABILITADO;
        }

        return switch (variante) {

            case PRIMARIO ->
                    Paleta.BOTON_PRIMARIO_TEXTO;

            case SECUNDARIO, FANTASMA ->
                    Paleta.BONE;

            case PELIGRO ->
                    Paleta.OBSIDIAN;
        };
    }

    // ========================================================
    // 22. ESTADO HABILITADO
    // ========================================================

    @Override
    public void setEnabled(
            boolean habilitado) {

        super.setEnabled(habilitado);

        // Durante la construccion de JButton,
        // los campos del componente pueden
        // no estar inicializados.

        if (!habilitado) {

            destinoHover = 0f;

            destinoPresion = 0f;

            progresoHover = 0f;

            progresoPresion = 0f;

            detenerAnimacion();
        }

        setCursor(
                habilitado
                        ? Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );

        if (variante != null) {

            setForeground(
                    colorTextoNormal()
            );
            refrescarIconoLucide();
        }

        repaint();
    }

    // ========================================================
    // 23. CAMBIAR VARIANTE
    // ========================================================

    public void setVariante(
            Variante variante) {

        this.variante = Objects.requireNonNull(
                variante,
                "La variante no puede ser null"
        );

        setForeground(
                colorTextoNormal()
        );

        refrescarIconoLucide();
        repaint();
    }

    public Variante getVariante() {

        return variante;
    }

    // ========================================================
    // 24. CAMBIAR RADIO
    // ========================================================

    public void setRadio(
            int radio) {

        if (radio < 0) {

            throw new IllegalArgumentException(
                    "El radio no puede ser negativo"
            );
        }

        this.radio = radio;

        repaint();
    }

    public int getRadio() {

        return radio;
    }

    // ========================================================
    // 25. CONTROLAR ANIMACIONES
    // ========================================================

    public void setAnimacionesHabilitadas(
            boolean habilitadas) {

        this.animacionesHabilitadas =
                habilitadas;

        if (!habilitadas) {

            detenerAnimacion();

            progresoHover = destinoHover;

            progresoPresion = destinoPresion;

            repaint();
        }
    }

    public boolean isAnimacionesHabilitadas() {

        return animacionesHabilitadas;
    }

    // ========================================================
    // 26. CONTROLAR CONTORNO
    // ========================================================

    public void setContornoVisible(
            boolean visible) {

        this.contornoVisible = visible;

        repaint();
    }

    public boolean isContornoVisible() {

        return contornoVisible;
    }

    // ========================================================
    // 27. TAMAÑOS PREDEFINIDOS
    // ========================================================

    public void usarTamanoPequeno() {

        setFont(
                Tipografia.medium(12f)
        );

        setPreferredSize(
                new Dimension(
                        112,
                        Espaciado.BOTON_ALTURA_PEQUENO
                )
        );

        revalidate();
    }

    public void usarTamanoNormal() {

        setFont(
                Tipografia.boton()
        );

        setPreferredSize(
                new Dimension(
                        Espaciado.BOTON_ANCHO_ACCION,
                        Espaciado.BOTON_ALTURA
                )
        );

        revalidate();
    }

    public void usarTamanoGrande() {

        setFont(
                Tipografia.semiBold(15f)
        );

        setPreferredSize(
                new Dimension(
                        172,
                        Espaciado.BOTON_ALTURA_GRANDE
                )
        );

        revalidate();
    }

    // ========================================================
    // 28. TAMAÑO PREFERIDO
    // ========================================================

    /**
     * Calcula un ancho suficiente para
     * representar texto e icono.
     *
     * Respeta el ancho minimo recomendado.
     */
    @Override
    public Dimension getPreferredSize() {

        Dimension base =
                super.getPreferredSize();

        if (base == null) {

            return new Dimension(
                    Espaciado.BOTON_ANCHO_ACCION,
                    Espaciado.BOTON_ALTURA
            );
        }

        Insets margen =
                getInsets();

        FontMetrics metricas =
                getFontMetrics(getFont());

        String texto =
                getText() == null
                        ? ""
                        : getText();

        int anchoTexto =
                metricas.stringWidth(texto);

        Icon icono =
                getIcon();

        int anchoIcono =
                icono == null
                        ? 0
                        : icono.getIconWidth();

        int separacion =
                icono != null && !texto.isEmpty()
                        ? getIconTextGap()
                        : 0;

        int anchoNecesario =
                anchoTexto
                        + anchoIcono
                        + separacion
                        + margen.left
                        + margen.right
                        + 12;

        int ancho =
                Math.max(
                        base.width,
                        anchoNecesario
                );

        return new Dimension(
                ancho,
                base.height
        );
    }

    // ========================================================
    // 29. FABRICAS DE BOTONES
    // ========================================================

    public static BotonPremium primario(
            String texto) {

        return new BotonPremium(
                texto,
                Variante.PRIMARIO
        );
    }

    public static BotonPremium secundario(
            String texto) {

        return new BotonPremium(
                texto,
                Variante.SECUNDARIO
        );
    }

    public static BotonPremium fantasma(
            String texto) {

        return new BotonPremium(
                texto,
                Variante.FANTASMA
        );
    }

    public static BotonPremium peligro(
            String texto) {

        return new BotonPremium(
                texto,
                Variante.PELIGRO
        );
    }

    // ========================================================
    // 30. FABRICAS CON ICONOS
    // ========================================================

    public static BotonPremium primario(
            String texto,
            Icon icono) {

        return new BotonPremium(
                texto,
                icono,
                Variante.PRIMARIO
        );
    }

    public static BotonPremium secundario(
            String texto,
            Icon icono) {

        return new BotonPremium(
                texto,
                icono,
                Variante.SECUNDARIO
        );
    }

    public static BotonPremium fantasma(
            String texto,
            Icon icono) {

        return new BotonPremium(
                texto,
                icono,
                Variante.FANTASMA
        );
    }

    public static BotonPremium peligro(
            String texto,
            Icon icono) {

        return new BotonPremium(
                texto,
                icono,
                Variante.PELIGRO
        );
    }

    // ========================================================
    // 31. INFORMACION DEL COMPONENTE
    // ========================================================

    public String generarDescripcion() {

        return "BotonPremium"
                + " | Variante: " + variante
                + " | Texto: " + getText()
                + " | Habilitado: " + isEnabled()
                + " | Radio: " + radio
                + " | Animaciones: "
                + animacionesHabilitadas;
    }
}
