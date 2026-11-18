
package vista.componentes;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * COMPONENTE:
 * TarjetaEstadistica.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * 1. Mostrar indicadores del dashboard.
 * 2. Presentar valores monetarios y numericos.
 * 3. Aplicar la identidad visual del sistema.
 * 4. Permitir actualizaciones dinamicas.
 * 5. Ofrecer diferentes variantes.
 * 6. Dibujar superficies y bordes con Java2D.
 * 7. Mostrar iconos y detalles opcionales.
 *
 * CARACTERISTICAS:
 *
 * - Fondo Graphite / Elevated.
 * - Bordes discretos.
 * - Esquinas redondeadas.
 * - Tipografia Inter e IBM Plex Mono.
 * - Indicadores cromaticos opcionales.
 * - Compatibilidad con Swing.
 * - Sin dependencias externas.
 *
 * IMPORTANTE:
 *
 * Este componente es exclusivamente visual.
 *
 * No modifica saldos, puntos ni transacciones.
 *
 * Los valores deben obtenerse desde
 * ContextoAplicacion o los servicios.
 *
 * Java 21.
 */
public class TarjetaEstadistica extends JPanel {

    // ========================================================
    // 1. VARIANTES
    // ========================================================

    public enum Variante {

        NORMAL,
        DESTACADA,
        POSITIVA,
        ADVERTENCIA,
        NEGATIVA
    }

    // ========================================================
    // 2. CONFIGURACION
    // ========================================================

    private static final int ANCHO_INICIAL =
            260;

    private static final int ALTO_INICIAL =
            Espaciado.DASHBOARD_TARJETA_ALTURA;

    private static final int ALTO_MINIMO =
            116;

    private static final int RADIO_PREDETERMINADO =
            Espaciado.RADIO_TARJETA;

    private static final int PADDING =
            Espaciado.TARJETA_PADDING;

    private static final int GROSOR_BORDE =
            1;

    // ========================================================
    // 3. COMPONENTES
    // ========================================================

    private JLabel etiquetaTitulo;

    private JLabel etiquetaValor;

    private JLabel etiquetaDescripcion;

    private JLabel etiquetaIndicador;

    private JLabel etiquetaIcono;

    private JPanel panelSuperior;

    private JPanel panelContenido;

    private JPanel panelInferior;

    // ========================================================
    // 4. ESTADO
    // ========================================================

    private Variante variante;

    private String titulo;

    private String valor;

    private String descripcion;

    private String textoIndicador;

    private Icon icono;

    private Color colorAcentoPersonalizado;

    private int radio;

    private boolean mostrarIndicador;

    private boolean mostrarLineaAcento;

    private boolean mostrarBorde;

    private boolean mostrarIcono;

    // ========================================================
    // 5. CONSTRUCTORES
    // ========================================================

    /**
     * Constructor basico.
     */
    public TarjetaEstadistica() {

        this(
                "INDICADOR",
                "0",
                "",
                Variante.NORMAL
        );
    }

    /**
     * Tarjeta con titulo y valor.
     */
    public TarjetaEstadistica(
            String titulo,
            String valor) {

        this(
                titulo,
                valor,
                "",
                Variante.NORMAL
        );
    }

    /**
     * Tarjeta con descripcion.
     */
    public TarjetaEstadistica(
            String titulo,
            String valor,
            String descripcion) {

        this(
                titulo,
                valor,
                descripcion,
                Variante.NORMAL
        );
    }

    /**
     * Constructor principal.
     */
    public TarjetaEstadistica(
            String titulo,
            String valor,
            String descripcion,
            Variante variante) {

        super(new BorderLayout());

        this.titulo =
                textoSeguro(titulo);

        this.valor =
                textoSeguro(valor);

        this.descripcion =
                textoSeguro(descripcion);

        this.variante =
                Objects.requireNonNull(
                        variante,
                        "La variante no puede ser null"
                );

        this.textoIndicador = "";

        this.icono = null;

        this.colorAcentoPersonalizado = null;

        this.radio =
                RADIO_PREDETERMINADO;

        this.mostrarIndicador = false;

        this.mostrarLineaAcento = false;

        this.mostrarBorde = true;

        this.mostrarIcono = false;

        inicializar();
    }

    // ========================================================
    // 6. INICIALIZACION
    // ========================================================

    private void inicializar() {

        setOpaque(false);

        setBorder(
                Espaciado.bordeVacio(
                        PADDING,
                        PADDING,
                        PADDING,
                        PADDING
                )
        );

        setPreferredSize(
                new Dimension(
                        ANCHO_INICIAL,
                        ALTO_INICIAL
                )
        );

        setMinimumSize(
                new Dimension(
                        150,
                        ALTO_MINIMO
                )
        );

        construirEncabezado();

        construirContenido();

        construirPie();

        actualizarEstilos();
    }

    // ========================================================
    // 7. ENCABEZADO
    // ========================================================

    private void construirEncabezado() {

        panelSuperior =
                new JPanel(
                        new BorderLayout(
                                Espaciado.SM,
                                0
                        )
                );

        panelSuperior.setOpaque(false);

        etiquetaTitulo =
                new JLabel(titulo);

        etiquetaTitulo.setFont(
                Tipografia.etiqueta()
        );

        etiquetaTitulo.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiquetaTitulo.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        etiquetaIcono =
                new JLabel();

        etiquetaIcono.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        etiquetaIcono.setForeground(
                Paleta.CHAMPAGNE
        );

        panelSuperior.add(
                etiquetaTitulo,
                BorderLayout.CENTER
        );

        panelSuperior.add(
                etiquetaIcono,
                BorderLayout.EAST
        );

        add(
                panelSuperior,
                BorderLayout.NORTH
        );
    }

    // ========================================================
    // 8. CONTENIDO PRINCIPAL
    // ========================================================

    private void construirContenido() {

        panelContenido =
                new JPanel(
                        new BorderLayout(
                                0,
                                Espaciado.SM
                        )
                );

        panelContenido.setOpaque(false);

        etiquetaValor =
                new JLabel(valor);

        etiquetaValor.setFont(
                Tipografia.montoTarjeta()
        );

        etiquetaValor.setForeground(
                Paleta.TEXTO_NUMERICO
        );

        etiquetaValor.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        etiquetaDescripcion =
                new JLabel(descripcion);

        etiquetaDescripcion.setFont(
                Tipografia.cuerpoPequeno()
        );

        etiquetaDescripcion.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiquetaDescripcion.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        panelContenido.add(
                etiquetaValor,
                BorderLayout.CENTER
        );

        panelContenido.add(
                etiquetaDescripcion,
                BorderLayout.SOUTH
        );

        add(
                panelContenido,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 9. PIE DE TARJETA
    // ========================================================

    private void construirPie() {

        panelInferior =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        panelInferior.setOpaque(false);

        etiquetaIndicador =
                new JLabel();

        etiquetaIndicador.setFont(
                Tipografia.medium(
                        Tipografia.TAMANO_CAPTION
                )
        );

        panelInferior.add(
                etiquetaIndicador
        );

        panelInferior.setVisible(false);

        add(
                panelInferior,
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 10. PINTADO PERSONALIZADO
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

            float arco =
                    radio * 2f;

            RoundRectangle2D.Float fondo =
                    new RoundRectangle2D.Float(
                            0.5f,
                            0.5f,
                            Math.max(0f, ancho - 1f),
                            Math.max(0f, alto - 1f),
                            arco,
                            arco
                    );

            // ----------------------------------
            // FONDO
            // ----------------------------------

            g2.setColor(
                    obtenerColorFondo()
            );

            g2.fill(fondo);

            // ----------------------------------
            // BORDE
            // ----------------------------------

            if (mostrarBorde) {

                g2.setStroke(
                        new BasicStroke(
                                GROSOR_BORDE
                        )
                );

                g2.setColor(
                        obtenerColorBorde()
                );

                g2.draw(fondo);
            }

            // ----------------------------------
            // LINEA DECORATIVA MINIMALISTA
            // ----------------------------------

            if (mostrarLineaAcento) {

                pintarLineaAcento(
                        g2,
                        ancho,
                        alto
                );
            }

        } finally {

            g2.dispose();
        }

        super.paintComponent(graphics);
    }

    // ========================================================
    // 11. CONFIGURACION GRAFICA
    // ========================================================

    private void configurarCalidad(
            Graphics2D g2) {

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
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
    // 12. LINEA DE ACENTO
    // ========================================================

    /**
     * Dibuja una linea muy discreta
     * en la zona inferior de la tarjeta.
     *
     * No utiliza degradados ni sombras.
     */
    private void pintarLineaAcento(
            Graphics2D g2,
            int ancho,
            int alto) {

        int margen =
                PADDING;

        int longitud =
                Math.max(
                        0,
                        Math.min(
                                56,
                                ancho - margen * 2
                        )
                );

        if (longitud <= 0) {
            return;
        }

        int posicionY =
                alto - 13;

        g2.setStroke(
                new BasicStroke(
                        2f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                )
        );

        g2.setColor(
                obtenerColorAcento()
        );

        g2.drawLine(
                margen,
                posicionY,
                margen + longitud,
                posicionY
        );
    }

    // ========================================================
    // 13. COLORES SEGUN VARIANTE
    // ========================================================

    private Color obtenerColorFondo() {

        return switch (variante) {

            case NORMAL ->
                    Paleta.FONDO_TARJETA;

            case DESTACADA ->
                    Paleta.FONDO_SECUNDARIO;

            case POSITIVA ->
                    Paleta.FONDO_TARJETA;

            case ADVERTENCIA ->
                    Paleta.FONDO_TARJETA;

            case NEGATIVA ->
                    Paleta.FONDO_TARJETA;
        };
    }

    private Color obtenerColorBorde() {

        return switch (variante) {

            case NORMAL ->
                    Paleta.BORDE_SUAVE;

            case DESTACADA ->
                    Paleta.BORDE_FUERTE;

            case POSITIVA ->
                    Paleta.BORDE_SUAVE;

            case ADVERTENCIA ->
                    Paleta.BORDE_SUAVE;

            case NEGATIVA ->
                    Paleta.BORDE_SUAVE;
        };
    }

    private Color obtenerColorAcento() {

        if (colorAcentoPersonalizado != null) {

            return colorAcentoPersonalizado;
        }

        return switch (variante) {

            case NORMAL ->
                    Paleta.CHAMPAGNE;

            case DESTACADA ->
                    Paleta.CHAMPAGNE;

            case POSITIVA ->
                    Paleta.SAGE;

            case ADVERTENCIA ->
                    Paleta.ADVERTENCIA;

            case NEGATIVA ->
                    Paleta.CLAY;
        };
    }

    // ========================================================
    // 14. ACTUALIZAR ESTILOS
    // ========================================================

    private void actualizarEstilos() {

        if (etiquetaTitulo == null) {
            return;
        }

        etiquetaTitulo.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiquetaValor.setForeground(
                Paleta.TEXTO_NUMERICO
        );

        etiquetaDescripcion.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiquetaIndicador.setForeground(
                obtenerColorAcento()
        );

        etiquetaIcono.setForeground(
                obtenerColorAcento()
        );

        etiquetaIcono.setIcon(
                mostrarIcono ? icono : null
        );

        etiquetaIndicador.setText(
                mostrarIndicador
                        ? textoIndicador
                        : ""
        );

        panelInferior.setVisible(
                mostrarIndicador
                        && !textoIndicador.isBlank()
        );

        revalidate();

        repaint();
    }

    // ========================================================
    // 15. ACTUALIZAR TITULO
    // ========================================================

    public void setTitulo(
            String titulo) {

        this.titulo =
                textoSeguro(titulo);

        etiquetaTitulo.setText(
                this.titulo
        );

        revalidate();

        repaint();
    }

    public String getTitulo() {

        return titulo;
    }

    // ========================================================
    // 16. ACTUALIZAR VALOR
    // ========================================================

    public void setValor(
            String valor) {

        this.valor =
                textoSeguro(valor);

        etiquetaValor.setText(
                this.valor
        );

        revalidate();

        repaint();
    }

    public String getValor() {

        return valor;
    }

    // ========================================================
    // 17. ACTUALIZAR DESCRIPCION
    // ========================================================

    public void setDescripcion(
            String descripcion) {

        this.descripcion =
                textoSeguro(descripcion);

        etiquetaDescripcion.setText(
                this.descripcion
        );

        revalidate();

        repaint();
    }

    public String getDescripcion() {

        return descripcion;
    }

    // ========================================================
    // 18. ACTUALIZAR INDICADOR
    // ========================================================

    /**
     * El indicador es un texto opcional.
     *
     * Ejemplos:
     *
     * "+12.4% este mes"
     * "3 clientes activos"
     * "Pendiente de revision"
     *
     * Esta clase no calcula porcentajes.
     */
    public void setIndicador(
            String indicador) {

        this.textoIndicador =
                textoSeguro(indicador);

        this.mostrarIndicador =
                !this.textoIndicador.isBlank();

        actualizarEstilos();
    }

    public String getIndicador() {

        return textoIndicador;
    }

    public void setMostrarIndicador(
            boolean mostrar) {

        this.mostrarIndicador = mostrar;

        actualizarEstilos();
    }

    public boolean isMostrarIndicador() {

        return mostrarIndicador;
    }

    // ========================================================
    // 19. ACTUALIZAR VARIANTE
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
    // 20. COLOR PERSONALIZADO
    // ========================================================

    public void setColorAcento(
            Color color) {

        this.colorAcentoPersonalizado =
                color;

        actualizarEstilos();
    }

    public Color getColorAcento() {

        return obtenerColorAcento();
    }

    public void restablecerColorAcento() {

        this.colorAcentoPersonalizado =
                null;

        actualizarEstilos();
    }

    // ========================================================
    // 21. CONFIGURAR ICONO
    // ========================================================

    public void setIcono(
            Icon icono) {

        this.icono = icono;

        this.mostrarIcono =
                icono != null;

        actualizarEstilos();
    }

    public Icon getIcono() {

        return icono;
    }

    public void setMostrarIcono(
            boolean mostrar) {

        this.mostrarIcono = mostrar;

        actualizarEstilos();
    }

    public boolean isMostrarIcono() {

        return mostrarIcono;
    }

    // ========================================================
    // 22. LINEA DE ACENTO
    // ========================================================

    public void setMostrarLineaAcento(
            boolean mostrar) {

        this.mostrarLineaAcento =
                mostrar;

        repaint();
    }

    public boolean isMostrarLineaAcento() {

        return mostrarLineaAcento;
    }

    // ========================================================
    // 23. CONTROLAR BORDE
    // ========================================================

    public void setMostrarBorde(
            boolean mostrar) {

        this.mostrarBorde =
                mostrar;

        repaint();
    }

    public boolean isMostrarBorde() {

        return mostrarBorde;
    }

    // ========================================================
    // 24. RADIO DE ESQUINAS
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
    // 25. TAMANOS
    // ========================================================

    public void usarTamanoCompacto() {

        setPreferredSize(
                new Dimension(
                        220,
                        120
                )
        );

        setBorder(
                Espaciado.bordeVacio(
                        Espaciado.TARJETA_PADDING_COMPACTO
                )
        );

        etiquetaValor.setFont(
                Tipografia.monoMedium(22f)
        );

        revalidate();

        repaint();
    }

    public void usarTamanoNormal() {

        setPreferredSize(
                new Dimension(
                        ANCHO_INICIAL,
                        ALTO_INICIAL
                )
        );

        setBorder(
                Espaciado.bordeVacio(
                        PADDING
                )
        );

        etiquetaValor.setFont(
                Tipografia.montoTarjeta()
        );

        revalidate();

        repaint();
    }

    public void usarTamanoGrande() {

        setPreferredSize(
                new Dimension(
                        360,
                        Espaciado.TARJETA_ALTURA_GRANDE
                )
        );

        setBorder(
                Espaciado.bordeVacio(
                        Espaciado.TARJETA_PADDING_GRANDE
                )
        );

        etiquetaValor.setFont(
                Tipografia.monoMedium(36f)
        );

        revalidate();

        repaint();
    }

    // ========================================================
    // 26. VALORES NUMERICOS
    // ========================================================

    /**
     * Actualiza el valor desde un entero.
     */
    public void setValorNumerico(
            int numero) {

        setValor(
                formatearEntero(numero)
        );
    }

    /**
     * Actualiza el valor desde un long.
     */
    public void setValorNumerico(
            long numero) {

        setValor(
                formatearEntero(numero)
        );
    }

    // ========================================================
    // 27. VALORES MONETARIOS
    // ========================================================

    /**
     * Actualiza la tarjeta utilizando
     * BigDecimal.
     *
     * Se formatea en pesos colombianos.
     *
     * BigDecimal evita utilizar double
     * para las cantidades financieras.
     */
    public void setValorMonetario(
            BigDecimal cantidad) {

        Objects.requireNonNull(
                cantidad,
                "La cantidad no puede ser null"
        );

        NumberFormat formato =
                NumberFormat.getCurrencyInstance(
                        Locale.forLanguageTag("es-CO")
                );

        formato.setMinimumFractionDigits(2);

        formato.setMaximumFractionDigits(2);

        setValor(
                formato.format(cantidad)
        );
    }

    // ========================================================
    // 28. ACTUALIZACION COMPLETA
    // ========================================================

    /**
     * Actualiza varios campos de una vez.
     *
     * Recomendado para refrescar
     * estadisticas del dashboard.
     */
    public void actualizar(
            String titulo,
            String valor,
            String descripcion) {

        this.titulo =
                textoSeguro(titulo);

        this.valor =
                textoSeguro(valor);

        this.descripcion =
                textoSeguro(descripcion);

        etiquetaTitulo.setText(
                this.titulo
        );

        etiquetaValor.setText(
                this.valor
        );

        etiquetaDescripcion.setText(
                this.descripcion
        );

        revalidate();

        repaint();
    }

    // ========================================================
    // 29. ACTUALIZACION EN EL HILO SWING
    // ========================================================

    /**
     * Permite solicitar una actualizacion
     * desde otro hilo.
     *
     * Los componentes Swing deben
     * modificarse en el Event Dispatch Thread.
     *
     * Este metodo programa la actualizacion
     * de forma segura.
     */
    public void actualizarEnInterfaz(
            String titulo,
            String valor,
            String descripcion) {

        if (SwingUtilities.isEventDispatchThread()) {

            actualizar(
                    titulo,
                    valor,
                    descripcion
            );

        } else {

            SwingUtilities.invokeLater(() -> {

                actualizar(
                        titulo,
                        valor,
                        descripcion
                );
            });
        }
    }

    // ========================================================
    // 30. FORMATO NUMERICO
    // ========================================================

    public static String formatearEntero(
            long numero) {

        NumberFormat formato =
                NumberFormat.getIntegerInstance(
                        Locale.forLanguageTag("es-CO")
                );

        formato.setGroupingUsed(true);

        return formato.format(numero);
    }

    /**
     * Formatea una cantidad monetaria
     * sin modificar el BigDecimal original.
     */
    public static String formatearMoneda(
            BigDecimal cantidad) {

        Objects.requireNonNull(
                cantidad,
                "La cantidad no puede ser null"
        );

        NumberFormat formato =
                NumberFormat.getCurrencyInstance(
                        Locale.forLanguageTag("es-CO")
                );

        formato.setMinimumFractionDigits(2);

        formato.setMaximumFractionDigits(2);

        return formato.format(cantidad);
    }

    // ========================================================
    // 31. FABRICAS DE TARJETAS
    // ========================================================

    public static TarjetaEstadistica normal(
            String titulo,
            String valor,
            String descripcion) {

        return new TarjetaEstadistica(
                titulo,
                valor,
                descripcion,
                Variante.NORMAL
        );
    }

    public static TarjetaEstadistica destacada(
            String titulo,
            String valor,
            String descripcion) {

        return new TarjetaEstadistica(
                titulo,
                valor,
                descripcion,
                Variante.DESTACADA
        );
    }

    public static TarjetaEstadistica positiva(
            String titulo,
            String valor,
            String descripcion) {

        return new TarjetaEstadistica(
                titulo,
                valor,
                descripcion,
                Variante.POSITIVA
        );
    }

    public static TarjetaEstadistica advertencia(
            String titulo,
            String valor,
            String descripcion) {

        return new TarjetaEstadistica(
                titulo,
                valor,
                descripcion,
                Variante.ADVERTENCIA
        );
    }

    public static TarjetaEstadistica negativa(
            String titulo,
            String valor,
            String descripcion) {

        return new TarjetaEstadistica(
                titulo,
                valor,
                descripcion,
                Variante.NEGATIVA
        );
    }

    // ========================================================
    // 32. INFORMACION ACCESIBLE
    // ========================================================

    /**
     * Proporciona una descripcion textual
     * para tecnologias de asistencia.
     */
    public void actualizarAccesibilidad() {

        if (getAccessibleContext() == null) {
            return;
        }

        getAccessibleContext().setAccessibleName(
                titulo
        );

        getAccessibleContext().setAccessibleDescription(
                titulo
                        + ": "
                        + valor
                        + ". "
                        + descripcion
        );
    }

    // ========================================================
    // 33. VALIDACION DE TEXTO
    // ========================================================

    private static String textoSeguro(
            String texto) {

        return texto == null
                ? ""
                : texto;
    }

    // ========================================================
    // 34. DESCRIPCION DEL COMPONENTE
    // ========================================================

    public String generarDescripcion() {

        return "TarjetaEstadistica"
                + " | Titulo: " + titulo
                + " | Valor: " + valor
                + " | Descripcion: " + descripcion
                + " | Variante: " + variante
                + " | Indicador: " + textoIndicador;
    }
}
