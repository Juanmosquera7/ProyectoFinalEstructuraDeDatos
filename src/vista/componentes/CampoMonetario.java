
package vista.componentes;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.awt.geom.RoundRectangle2D;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * COMPONENTE:
 * CampoMonetario.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * 1. Permitir introducir cantidades monetarias.
 * 2. Validar entradas utilizando BigDecimal.
 * 3. Aplicar formato monetario colombiano.
 * 4. Evitar caracteres no permitidos.
 * 5. Limitar la cantidad de decimales.
 * 6. Establecer montos maximos.
 * 7. Mostrar estados de foco y error.
 * 8. Dibujar el campo mediante Java2D.
 *
 * FORMATO:
 *
 * Entrada:  1250000,50
 * Salida:   1.250.000,50
 *
 * La interfaz dibuja:
 *
 * $ 1.250.000,50 COP
 *
 * El simbolo $ y COP no forman parte
 * del documento del JTextField.
 *
 * IMPORTANTE:
 *
 * Esta clase no ejecuta operaciones
 * financieras ni modifica saldos.
 *
 * Los montos validados se entregan
 * a los servicios mediante BigDecimal.
 *
 * Java 21.
 */
public class CampoMonetario extends JTextField {

    // ========================================================
    // 1. CONFIGURACION GENERAL
    // ========================================================

    private static final long serialVersionUID = 1L;

    private static final int ANCHO_PREDETERMINADO =
            280;

    private static final int ALTO_PREDETERMINADO =
            Espaciado.INPUT_ALTURA;

    private static final int RADIO_PREDETERMINADO =
            Espaciado.RADIO_CAMPO;

    private static final int MAXIMO_DIGITOS_ENTEROS =
            15;

    private static final int MAXIMO_DECIMALES =
            2;

    private static final int PADDING_IZQUIERDO =
            38;

    private static final int PADDING_DERECHO =
            54;

    private static final String SIMBOLO_MONEDA =
            "$";

    private static final String CODIGO_MONEDA =
            "COP";

    private static final String TEXTO_AYUDA =
            "0,00";

    // ========================================================
    // 2. PATRONES DE ENTRADA
    // ========================================================

    /**
     * Permite estados intermedios durante
     * la escritura.
     *
     * Ejemplos:
     *
     * 125
     * 1250
     * 1.250
     * 1250,
     * 1250,5
     * 1250,50
     *
     * La validacion definitiva es mas
     * estricta que este patron.
     */
    private static final Pattern PATRON_EDICION =
            Pattern.compile(
                    "\\d*(?:\\.\\d{0,3})*(?:,\\d{0,2})?"
            );

    /**
     * Formatos finales aceptados:
     *
     * 1250000
     * 1.250.000
     * 1250000,50
     * 1.250.000,50
     */
    private static final Pattern PATRON_FINAL =
            Pattern.compile(
                    "(?:\\d+|\\d{1,3}(?:\\.\\d{3})+)"
                            + "(?:,\\d{1,2})?"
            );

    // ========================================================
    // 3. ESTADO DEL COMPONENTE
    // ========================================================

    private BigDecimal montoMaximo;

    private boolean permitirCero;

    private boolean campoObligatorio;

    private boolean mostrarSimbolo;

    private boolean mostrarCodigo;

    private boolean mostrarError;

    private boolean formatoAutomatico;

    private int radio;

    private String mensajeError;

    private String textoAyuda;

    // ========================================================
    // 4. CONSTRUCTORES
    // ========================================================

    /**
     * Campo monetario vacio.
     */
    public CampoMonetario() {

        this(null);
    }

    /**
     * Campo con importe inicial.
     *
     * Si montoInicial es null,
     * el campo comienza vacio.
     */
    public CampoMonetario(
            BigDecimal montoInicial) {

        super();

        montoMaximo = null;

        permitirCero = false;

        campoObligatorio = true;

        mostrarSimbolo = true;

        mostrarCodigo = true;

        mostrarError = false;

        formatoAutomatico = true;

        radio = RADIO_PREDETERMINADO;

        mensajeError = "";

        textoAyuda = TEXTO_AYUDA;

        inicializarComponente();

        if (montoInicial != null) {

            setMonto(montoInicial);
        }
    }

    // ========================================================
    // 5. INICIALIZACION
    // ========================================================

    private void inicializarComponente() {

        setFont(
                Tipografia.monoMedium(
                        Tipografia.TAMANO_CUERPO_GRANDE
                )
        );

        setForeground(
                Paleta.INPUT_TEXTO
        );

        setBackground(
                Paleta.INPUT_FONDO
        );

        setCaretColor(
                Paleta.CHAMPAGNE
        );

        setSelectionColor(
                Paleta.ACENTO_SUAVE
        );

        setSelectedTextColor(
                Paleta.BONE
        );

        setHorizontalAlignment(
                SwingConstants.LEFT
        );

        setOpaque(false);

        setBorder(
                javax.swing.BorderFactory
                        .createEmptyBorder(
                                8,
                                PADDING_IZQUIERDO,
                                8,
                                PADDING_DERECHO
                        )
        );

        setPreferredSize(
                new Dimension(
                        ANCHO_PREDETERMINADO,
                        ALTO_PREDETERMINADO
                )
        );

        setMinimumSize(
                new Dimension(
                        150,
                        Espaciado.INPUT_ALTURA_COMPACTA
                )
        );

        getAccessibleContext()
                .setAccessibleName(
                        "Importe monetario en pesos colombianos"
                );

        getAccessibleContext()
                .setAccessibleDescription(
                        "Introduzca una cantidad en COP. "
                                + "Utilice coma para decimales."
                );

        instalarFiltro();

        instalarEventos();
    }

    // ========================================================
    // 6. FILTRO DEL DOCUMENTO
    // ========================================================

    private void instalarFiltro() {

        AbstractDocument documento =
                (AbstractDocument) getDocument();

        documento.setDocumentFilter(
                new FiltroMonetario()
        );
    }

    /**
     * Restringe la entrada a caracteres
     * permitidos sin modificar el modelo
     * financiero.
     */
    private final class FiltroMonetario
            extends DocumentFilter {

        @Override
        public void insertString(
                FilterBypass fb,
                int offset,
                String texto,
                AttributeSet atributos)
                throws BadLocationException {

            replace(
                    fb,
                    offset,
                    0,
                    texto,
                    atributos
            );
        }

        @Override
        public void replace(
                FilterBypass fb,
                int offset,
                int longitud,
                String texto,
                AttributeSet atributos)
                throws BadLocationException {

            if (texto == null) {
                return;
            }

            String actual =
                    fb.getDocument().getText(
                            0,
                            fb.getDocument().getLength()
                    );

            String resultado =
                    actual.substring(0, offset)
                            + texto
                            + actual.substring(
                            offset + longitud
                    );

            if (!entradaPermitida(resultado)) {

                java.awt.Toolkit
                        .getDefaultToolkit()
                        .beep();

                return;
            }

            super.replace(
                    fb,
                    offset,
                    longitud,
                    texto,
                    atributos
            );
        }

        @Override
        public void remove(
                FilterBypass fb,
                int offset,
                int longitud)
                throws BadLocationException {

            super.remove(
                    fb,
                    offset,
                    longitud
            );
        }
    }

    // ========================================================
    // 7. COMPROBAR ENTRADA PERMITIDA
    // ========================================================

    /**
     * Valida la entrada durante la escritura.
     *
     * No exige que el numero este completo.
     */
    private boolean entradaPermitida(
            String texto) {

        if (texto == null) {
            return false;
        }

        if (texto.isEmpty()) {
            return true;
        }

        if (!PATRON_EDICION
                .matcher(texto)
                .matches()) {

            return false;
        }

        String parteEntera =
                texto.split(",", -1)[0];

        String digitos =
                parteEntera.replace(".", "");

        return digitos.length()
                <= MAXIMO_DIGITOS_ENTEROS;
    }

    // ========================================================
    // 8. EVENTOS
    // ========================================================

    private void instalarEventos() {

        addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(
                            FocusEvent evento) {

                        limpiarErrorVisual();

                        // Durante la edicion, se retiran
                        // los separadores de miles.
                        //
                        // La coma decimal se conserva.

                        if (formatoAutomatico) {

                            String texto = getText();

                            if (!texto.isBlank()) {

                                setText(
                                        texto.replace(".", "")
                                );
                            }
                        }

                        repaint();
                    }

                    @Override
                    public void focusLost(
                            FocusEvent evento) {

                        if (getText().isBlank()) {

                            if (campoObligatorio) {

                                establecerError(
                                        "El importe es obligatorio"
                                );
                            }

                            repaint();

                            return;
                        }

                        if (validarMonto()
                                && formatoAutomatico) {

                            formatearContenido();
                        }

                        repaint();
                    }
                }
        );

        getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(
                            DocumentEvent evento) {

                        alModificarTexto();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent evento) {

                        alModificarTexto();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent evento) {

                        alModificarTexto();
                    }
                }
        );
    }

    // ========================================================
    // 9. MODIFICACION DEL TEXTO
    // ========================================================

    private void alModificarTexto() {

        limpiarErrorVisual();

        repaint();
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

            // ----------------------------------
            // FONDO
            // ----------------------------------

            g2.setColor(
                    isEnabled()
                            ? Paleta.INPUT_FONDO
                            : Paleta.INPUT_DESHABILITADO
            );

            g2.fill(
                    new RoundRectangle2D.Float(
                            0.5f,
                            0.5f,
                            Math.max(0, ancho - 1),
                            Math.max(0, alto - 1),
                            radio * 2f,
                            radio * 2f
                    )
            );

        } finally {

            g2.dispose();
        }

        // Pintar el texto, seleccion y cursor
        // utilizando JTextField.

        super.paintComponent(graphics);

        // Decoraciones encima del texto.

        pintarDecoraciones(graphics);
    }

    // ========================================================
    // 11. DECORACIONES MONETARIAS
    // ========================================================

    private void pintarDecoraciones(
            Graphics graphics) {

        Graphics2D g2 =
                (Graphics2D) graphics.create();

        try {

            configurarCalidad(g2);

            FontMetrics metricas =
                    g2.getFontMetrics(getFont());

            int centroVertical =
                    (getHeight()
                            + metricas.getAscent()
                            - metricas.getDescent()) / 2;

            // ----------------------------------
            // SIMBOLO $
            // ----------------------------------

            if (mostrarSimbolo) {

                g2.setFont(
                        Tipografia.monoMedium(14f)
                );

                g2.setColor(
                        isEnabled()
                                ? Paleta.CHAMPAGNE
                                : Paleta.TEXTO_DESHABILITADO
                );

                g2.drawString(
                        SIMBOLO_MONEDA,
                        16,
                        centroVertical
                );
            }

            // ----------------------------------
            // CODIGO COP
            // ----------------------------------

            if (mostrarCodigo) {

                g2.setFont(
                        Tipografia.medium(11f)
                );

                g2.setColor(
                        Paleta.TEXTO_TERCIARIO
                );

                FontMetrics codigoMetricas =
                        g2.getFontMetrics();

                int x =
                        getWidth()
                                - codigoMetricas.stringWidth(
                                CODIGO_MONEDA
                        )
                                - 14;

                g2.drawString(
                        CODIGO_MONEDA,
                        x,
                        centroVertical
                );
            }

            // ----------------------------------
            // PLACEHOLDER
            // ----------------------------------

            if (getText().isEmpty()
                    && !isFocusOwner()
                    && !textoAyuda.isBlank()) {

                g2.setFont(getFont());

                g2.setColor(
                        Paleta.INPUT_PLACEHOLDER
                );

                g2.drawString(
                        textoAyuda,
                        PADDING_IZQUIERDO,
                        centroVertical
                );
            }

        } finally {

            g2.dispose();
        }
    }

    // ========================================================
    // 12. BORDE PERSONALIZADO
    // ========================================================

    @Override
    protected void paintBorder(
            Graphics graphics) {

        Graphics2D g2 =
                (Graphics2D) graphics.create();

        try {

            configurarCalidad(g2);

            Color color;

            float grosor = 1f;

            if (!isEnabled()) {

                color = Paleta.BORDE_SUAVE;

            } else if (mostrarError) {

                color = Paleta.BORDE_ERROR;

                grosor = 1.5f;

            } else if (isFocusOwner()) {

                color = Paleta.BORDE_FOCO;

                grosor = 1.5f;

            } else {

                color = Paleta.INPUT_BORDE;
            }

            g2.setColor(color);

            g2.setStroke(
                    new BasicStroke(grosor)
            );

            float margen =
                    grosor / 2f + 0.5f;

            g2.draw(
                    new RoundRectangle2D.Float(
                            margen,
                            margen,
                            Math.max(
                                    0f,
                                    getWidth() - margen * 2f
                            ),
                            Math.max(
                                    0f,
                                    getHeight() - margen * 2f
                            ),
                            radio * 2f,
                            radio * 2f
                    )
            );

        } finally {

            g2.dispose();
        }
    }

    // ========================================================
    // 13. CALIDAD DE DIBUJO
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
    }

    // ========================================================
    // 14. OBTENER MONTO
    // ========================================================

    /**
     * Devuelve un BigDecimal exacto.
     *
     * Lanza IllegalStateException si
     * la entrada no es valida.
     */
    public BigDecimal getMonto() {

        String texto = getText().trim();

        if (texto.isEmpty()
                && !campoObligatorio) {

            return BigDecimal.ZERO
                    .setScale(
                            MAXIMO_DECIMALES
                    );
        }

        String error =
                comprobarMonto(texto);

        if (error != null) {

            throw new IllegalStateException(
                    error
            );
        }

        return convertirBigDecimal(
                texto
        );
    }

    /**
     * Alias para obtener el importe.
     */
    public BigDecimal obtenerMonto() {

        return getMonto();
    }

    // ========================================================
    // 15. ASIGNAR MONTO
    // ========================================================

    /**
     * Establece un valor monetario
     * desde BigDecimal.
     *
     * No acepta valores negativos.
     *
     * La representacion admite hasta
     * dos decimales.
     */
    public void setMonto(
            BigDecimal monto) {

        Objects.requireNonNull(
                monto,
                "El monto no puede ser null"
        );

        if (monto.signum() < 0) {

            throw new IllegalArgumentException(
                    "El monto no puede ser negativo"
            );
        }

        BigDecimal normalizado;

        try {

            normalizado = monto.setScale(
                    MAXIMO_DECIMALES,
                    RoundingMode.UNNECESSARY
            );

        } catch (ArithmeticException error) {

            throw new IllegalArgumentException(
                    "El monto admite maximo "
                            + "dos decimales",
                    error
            );
        }

        if (montoMaximo != null
                && normalizado.compareTo(
                montoMaximo
        ) > 0) {

            throw new IllegalArgumentException(
                    "El monto supera el limite permitido"
            );
        }

        if (normalizado.precision()
                - normalizado.scale()
                > MAXIMO_DIGITOS_ENTEROS) {

            throw new IllegalArgumentException(
                    "El monto supera la cantidad "
                            + "de digitos permitidos"
            );
        }

        setText(
                formatearMonto(normalizado)
        );

        limpiarErrorVisual();
    }

    // ========================================================
    // 16. VALIDACION GENERAL
    // ========================================================

    /**
     * Comprueba el contenido actual.
     *
     * Devuelve true cuando el importe
     * puede utilizarse.
     *
     * Si hay un problema, muestra el
     * estado visual de error.
     */
    public boolean validarMonto() {

        String error =
                comprobarMonto(
                        getText().trim()
                );

        if (error != null) {

            establecerError(error);

            return false;
        }

        limpiarErrorVisual();

        return true;
    }

    /**
     * Comprueba el valor sin mostrar
     * errores en el componente.
     */
    public boolean esMontoValido() {

        return comprobarMonto(
                getText().trim()
        ) == null;
    }

    // ========================================================
    // 17. COMPROBACION DE MONTO
    // ========================================================

    /**
     * Devuelve null si el contenido
     * es valido.
     *
     * En caso contrario devuelve
     * la descripcion del error.
     */
    private String comprobarMonto(
            String texto) {

        if (texto == null
                || texto.isBlank()) {

            return campoObligatorio
                    ? "El importe es obligatorio"
                    : null;
        }

        if (!PATRON_FINAL
                .matcher(texto)
                .matches()) {

            return "Formato invalido. "
                    + "Ejemplo: 1.250,50";
        }

        String parteEntera =
                texto.split(",", -1)[0];

        String digitos =
                parteEntera.replace(".", "");

        if (digitos.length()
                > MAXIMO_DIGITOS_ENTEROS) {

            return "El importe tiene demasiados digitos";
        }

        BigDecimal monto;

        try {

            monto = convertirBigDecimal(
                    texto
            );

        } catch (NumberFormatException error) {

            return "No se pudo interpretar el importe";
        }

        if (monto.signum() < 0) {

            return "No se permiten importes negativos";
        }

        if (monto.signum() == 0
                && !permitirCero) {

            return "El importe debe ser mayor que cero";
        }

        if (montoMaximo != null
                && monto.compareTo(montoMaximo) > 0) {

            return "El importe supera el maximo de "
                    + formatearMonto(montoMaximo);
        }

        return null;
    }

    // ========================================================
    // 18. CONVERSION A BIGDECIMAL
    // ========================================================

    /**
     * Convierte formato colombiano
     * a representacion decimal exacta.
     *
     * Ejemplo:
     *
     * 1.250.000,50
     *
     * Se convierte a:
     *
     * 1250000.50
     */
    private BigDecimal convertirBigDecimal(
            String texto) {

        String normalizado =
                texto.replace(".", "")
                        .replace(",", ".");

        return new BigDecimal(
                normalizado
        ).setScale(
                MAXIMO_DECIMALES,
                RoundingMode.UNNECESSARY
        );
    }

    // ========================================================
    // 19. FORMATEO MONETARIO
    // ========================================================

    /**
     * Formatea un BigDecimal utilizando:
     *
     * "." como separador de miles.
     * "," como separador decimal.
     *
     * Siempre presenta dos decimales.
     */
    public static String formatearMonto(
            BigDecimal monto) {

        Objects.requireNonNull(
                monto,
                "El monto no puede ser null"
        );

        DecimalFormatSymbols simbolos =
                new DecimalFormatSymbols(
                        Locale.forLanguageTag("es-CO")
                );

        simbolos.setGroupingSeparator('.');

        simbolos.setDecimalSeparator(',');

        DecimalFormat formato =
                new DecimalFormat(
                        "#,##0.00",
                        simbolos
                );

        formato.setRoundingMode(
                RoundingMode.UNNECESSARY
        );

        return formato.format(
                monto
        );
    }

    // ========================================================
    // 20. FORMATEAR CONTENIDO
    // ========================================================

    /**
     * Se utiliza al perder el foco.
     *
     * No modifica el valor financiero.
     */
    public void formatearContenido() {

        if (!esMontoValido()) {
            return;
        }

        if (getText().isBlank()) {
            return;
        }

        BigDecimal monto =
                convertirBigDecimal(
                        getText().trim()
                );

        setText(
                formatearMonto(monto)
        );
    }

    // ========================================================
    // 21. MONTO MAXIMO
    // ========================================================

    /**
     * Establece un limite superior.
     *
     * null elimina el limite.
     */
    public void setMontoMaximo(
            BigDecimal maximo) {

        if (maximo != null) {

            if (maximo.signum() < 0) {

                throw new IllegalArgumentException(
                        "El maximo no puede ser negativo"
                );
            }

            if (maximo.scale() > MAXIMO_DECIMALES) {

                try {

                    maximo = maximo.setScale(
                            MAXIMO_DECIMALES,
                            RoundingMode.UNNECESSARY
                    );

                } catch (ArithmeticException error) {

                    throw new IllegalArgumentException(
                            "El maximo admite dos decimales",
                            error
                    );
                }
            }
        }

        montoMaximo = maximo;

        limpiarErrorVisual();
    }

    public BigDecimal getMontoMaximo() {

        return montoMaximo;
    }

    // ========================================================
    // 22. PERMITIR CERO
    // ========================================================

    public void setPermitirCero(
            boolean permitir) {

        permitirCero = permitir;
    }

    public boolean isPermitirCero() {

        return permitirCero;
    }

    // ========================================================
    // 23. CAMPO OBLIGATORIO
    // ========================================================

    public void setCampoObligatorio(
            boolean obligatorio) {

        campoObligatorio = obligatorio;
    }

    public boolean isCampoObligatorio() {

        return campoObligatorio;
    }

    // ========================================================
    // 24. TEXTO DE AYUDA
    // ========================================================

    public void setTextoAyuda(
            String texto) {

        textoAyuda =
                texto == null ? "" : texto;

        repaint();
    }

    public String getTextoAyuda() {

        return textoAyuda;
    }

    // ========================================================
    // 25. MOSTRAR SIMBOLO
    // ========================================================

    public void setMostrarSimbolo(
            boolean mostrar) {

        mostrarSimbolo = mostrar;

        repaint();
    }

    public boolean isMostrarSimbolo() {

        return mostrarSimbolo;
    }

    // ========================================================
    // 26. MOSTRAR CODIGO
    // ========================================================

    public void setMostrarCodigo(
            boolean mostrar) {

        mostrarCodigo = mostrar;

        repaint();
    }

    public boolean isMostrarCodigo() {

        return mostrarCodigo;
    }

    // ========================================================
    // 27. FORMATO AUTOMATICO
    // ========================================================

    public void setFormatoAutomatico(
            boolean habilitado) {

        formatoAutomatico = habilitado;
    }

    public boolean isFormatoAutomatico() {

        return formatoAutomatico;
    }

    // ========================================================
    // 28. RADIO DE ESQUINAS
    // ========================================================

    public void setRadio(
            int nuevoRadio) {

        if (nuevoRadio < 0) {

            throw new IllegalArgumentException(
                    "El radio no puede ser negativo"
            );
        }

        radio = nuevoRadio;

        repaint();
    }

    public int getRadio() {

        return radio;
    }

    // ========================================================
    // 29. ESTADO DE ERROR
    // ========================================================

    /**
     * Muestra un error personalizado.
     *
     * Puede utilizarse para errores
     * adicionales procedentes del servicio.
     */
    public void establecerError(
            String mensaje) {

        mensajeError =
                mensaje == null
                        ? "Importe invalido"
                        : mensaje;

        mostrarError = true;

        setToolTipText(
                mensajeError
        );

        getAccessibleContext()
                .setAccessibleDescription(
                        mensajeError
                );

        repaint();
    }

    /**
     * Elimina el estado visual de error.
     */
    public void limpiarErrorVisual() {

        mostrarError = false;

        mensajeError = "";

        setToolTipText(null);

        if (getAccessibleContext() != null) {

            getAccessibleContext()
                    .setAccessibleDescription(
                            "Importe en COP. "
                                    + "Use coma para decimales."
                    );
        }

        repaint();
    }

    public boolean tieneError() {

        return mostrarError;
    }

    public String getMensajeError() {

        return mensajeError;
    }

    // ========================================================
    // 30. LIMPIAR CAMPO
    // ========================================================

    public void limpiar() {

        setText("");

        limpiarErrorVisual();
    }

    // ========================================================
    // 31. RESTRICCIONES DE TAMANO
    // ========================================================

    public void usarTamanoCompacto() {

        setPreferredSize(
                new Dimension(
                        220,
                        Espaciado.INPUT_ALTURA_COMPACTA
                )
        );

        revalidate();
    }

    public void usarTamanoNormal() {

        setPreferredSize(
                new Dimension(
                        ANCHO_PREDETERMINADO,
                        Espaciado.INPUT_ALTURA
                )
        );

        revalidate();
    }

    public void usarTamanoGrande() {

        setPreferredSize(
                new Dimension(
                        360,
                        Espaciado.INPUT_ALTURA_GRANDE
                )
        );

        revalidate();
    }

    // ========================================================
    // 32. ACTUALIZACION SEGURA EN SWING
    // ========================================================

    /**
     * Permite establecer el importe
     * desde otro hilo.
     *
     * La modificacion se ejecuta
     * en el Event Dispatch Thread.
     *
     * Este metodo no espera la finalizacion
     * de la actualizacion.
     */
    public void setMontoEnInterfaz(
            BigDecimal monto) {

        Objects.requireNonNull(
                monto,
                "El monto no puede ser null"
        );

        if (SwingUtilities.isEventDispatchThread()) {

            setMonto(monto);

        } else {

            SwingUtilities.invokeLater(() -> {

                setMonto(monto);
            });
        }
    }

    // ========================================================
    // 33. INFORMACION DEL COMPONENTE
    // ========================================================

    public String generarDescripcion() {

        return "CampoMonetario"
                + " | Texto: " + getText()
                + " | Valido: " + esMontoValido()
                + " | Obligatorio: " + campoObligatorio
                + " | Permitir cero: " + permitirCero
                + " | Maximo: " + montoMaximo
                + " | Error: " + mensajeError;
    }
}
