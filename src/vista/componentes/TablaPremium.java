
package vista.componentes;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import java.math.BigDecimal;
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
 * TablaPremium.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * - Presentar datos mediante JTable.
 * - Aplicar renderizadores personalizados.
 * - Mantener una apariencia editorial.
 * - Permitir ordenamiento de columnas.
 * - Incorporar busqueda y filtrado.
 * - Formatear valores monetarios.
 * - Gestionar seleccion y estados vacios.
 * - Proporcionar paginacion opcional.
 *
 * CARACTERISTICAS:
 *
 * - Fondo Obsidian.
 * - Cabeceras Graphite.
 * - Tipografia Inter e IBM Plex Mono.
 * - Filas alternas discretas.
 * - Seleccion con acento champagne.
 * - Separadores horizontales.
 * - Celdas no editables por defecto.
 * - Compatible con TableModel.
 *
 * IMPORTANTE:
 *
 * La tabla no accede directamente a
 * ClienteService ni TransaccionService.
 *
 * Los paneles deben proporcionar
 * los datos que desean mostrar.
 *
 * Java 21.
 */
public class TablaPremium extends JTable {

    // ========================================================
    // 1. CONSTANTES
    // ========================================================

    private static final long serialVersionUID = 1L;

    private static final int ALTO_FILA =
            Espaciado.TABLA_FILA_ALTURA;

    private static final int ALTO_CABECERA =
            Espaciado.TABLA_CABECERA_ALTURA;

    private static final int PADDING_HORIZONTAL =
            Espaciado.TABLA_PADDING_HORIZONTAL;

    private static final int PADDING_VERTICAL =
            8;

    private static final String TEXTO_VACIO =
            "No hay registros disponibles";

    private static final Locale LOCALE_CO =
            Locale.forLanguageTag("es-CO");

    // ========================================================
    // 2. ESTADOS DE LA TABLA
    // ========================================================

    private boolean filasAlternas;

    private boolean mostrarSeparadores;

    private boolean seleccionResaltada;

    private boolean mostrarEstadoVacio;

    private boolean ordenamientoHabilitado;

    private boolean paginacionHabilitada;

    private String mensajeVacio;

    private int tamanoPagina;

    private int paginaActual;

    private String textoBusqueda;

    // ========================================================
    // 3. MODELO Y ORDENAMIENTO
    // ========================================================

    private TableRowSorter<TableModel> ordenador;

    private RowFilter<TableModel, Integer> filtroBusqueda;

    private RowFilter<TableModel, Integer> filtroExterno;

    // ========================================================
    // 4. RENDERIZADORES
    // ========================================================

    private final RenderizadorTexto renderizadorTexto =
            new RenderizadorTexto();

    private final RenderizadorNumerico renderizadorNumerico =
            new RenderizadorNumerico();

    private final RenderizadorMonetario renderizadorMonetario =
            new RenderizadorMonetario();

    private final RenderizadorEstado renderizadorEstado =
            new RenderizadorEstado();

    private final RenderizadorBooleano renderizadorBooleano =
            new RenderizadorBooleano();

    private final RenderizadorCabecera renderizadorCabecera =
            new RenderizadorCabecera();

    // ========================================================
    // 5. CONSTRUCTORES
    // ========================================================

    /**
     * Construye una tabla sin registros.
     */
    public TablaPremium() {

        this(
                new ModeloNoEditable()
        );
    }

    /**
     * Construye una tabla con nombres
     * de columnas.
     */
    public TablaPremium(
            String... columnas) {

        this(
                new ModeloNoEditable(
                        columnas,
                        0
                )
        );
    }

    /**
     * Construye la tabla utilizando
     * un modelo de datos existente.
     */
    public TablaPremium(
            TableModel modelo) {

        super(
                Objects.requireNonNull(
                        modelo,
                        "El modelo no puede ser null"
                )
        );

        filasAlternas = true;

        mostrarSeparadores = true;

        seleccionResaltada = true;

        mostrarEstadoVacio = true;

        ordenamientoHabilitado = true;

        paginacionHabilitada = false;

        mensajeVacio = TEXTO_VACIO;

        tamanoPagina = 10;

        paginaActual = 1;

        textoBusqueda = "";

        inicializarComponente();
    }

    // ========================================================
    // 6. MODELO NO EDITABLE
    // ========================================================

    /**
     * Modelo adecuado para presentar
     * registros financieros.
     *
     * La edicion debe realizarse mediante
     * formularios y servicios.
     */
    public static class ModeloNoEditable
            extends DefaultTableModel {

        private static final long serialVersionUID = 1L;

        public ModeloNoEditable() {

            super();
        }

        public ModeloNoEditable(
                Object[] columnas,
                int filas) {

            super(
                    columnas,
                    filas
            );
        }

        public ModeloNoEditable(
                String[] columnas,
                int filas) {

            super(
                    columnas,
                    filas
            );
        }

        @Override
        public boolean isCellEditable(
                int fila,
                int columna) {

            return false;
        }
    }

    // ========================================================
    // 7. INICIALIZACION VISUAL
    // ========================================================

    private void inicializarComponente() {

        setFont(
                Tipografia.celdaTabla()
        );

        setForeground(
                Paleta.TABLA_TEXTO
        );

        setBackground(
                Paleta.TABLA_FILA
        );

        setSelectionBackground(
                Paleta.TABLA_SELECCION
        );

        setSelectionForeground(
                Paleta.BONE
        );

        setGridColor(
                Paleta.TABLA_LINEAS
        );

        setRowHeight(
                ALTO_FILA
        );

        setRowMargin(0);

        setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        setShowVerticalLines(false);

        setShowHorizontalLines(
                mostrarSeparadores
        );

        setFillsViewportHeight(true);

        setOpaque(true);

        setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        setRowSelectionAllowed(true);

        setColumnSelectionAllowed(false);

        setCellSelectionEnabled(false);

        setShowGrid(false);

        setShowHorizontalLines(
                mostrarSeparadores
        );

        configurarCabecera();

        configurarRenderizadores();

        configurarOrdenador();

        configurarAccesibilidad();
    }

    // ========================================================
    // 8. CABECERA
    // ========================================================

    private void configurarCabecera() {

        JTableHeader cabecera =
                getTableHeader();

        if (cabecera == null) {
            return;
        }

        cabecera.setFont(
                Tipografia.encabezadoTabla()
        );

        cabecera.setBackground(
                Paleta.TABLA_CABECERA
        );

        cabecera.setForeground(
                Paleta.TABLA_TEXTO_SECUNDARIO
        );

        cabecera.setReorderingAllowed(false);

        cabecera.setResizingAllowed(true);

        cabecera.setPreferredSize(
                new Dimension(
                        cabecera.getPreferredSize().width,
                        ALTO_CABECERA
                )
        );

        cabecera.setDefaultRenderer(
                renderizadorCabecera
        );

        cabecera.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        Paleta.BORDE_SUAVE
                )
        );
    }

    // ========================================================
    // 9. RENDERIZADORES PREDETERMINADOS
    // ========================================================

    private void configurarRenderizadores() {

        setDefaultRenderer(
                Object.class,
                renderizadorTexto
        );

        setDefaultRenderer(
                String.class,
                renderizadorTexto
        );

        setDefaultRenderer(
                Integer.class,
                renderizadorNumerico
        );

        setDefaultRenderer(
                Long.class,
                renderizadorNumerico
        );

        setDefaultRenderer(
                Double.class,
                renderizadorNumerico
        );

        setDefaultRenderer(
                Float.class,
                renderizadorNumerico
        );

        setDefaultRenderer(
                BigDecimal.class,
                renderizadorMonetario
        );

        setDefaultRenderer(
                Boolean.class,
                renderizadorBooleano
        );
    }

    // ========================================================
    // 10. ORDENAMIENTO
    // ========================================================

    private void configurarOrdenador() {

        ordenador =
                new TableRowSorter<>(
                        getModel()
                );

        ordenador.setSortsOnUpdates(true);

        setRowSorter(ordenador);

        aplicarFiltros();
    }

    // ========================================================
    // 11. ACCESIBILIDAD
    // ========================================================

    private void configurarAccesibilidad() {

        getAccessibleContext()
                .setAccessibleName(
                        "Tabla de registros"
                );

        getAccessibleContext()
                .setAccessibleDescription(
                        "Tabla con ordenamiento "
                                + "y seleccion de registros"
                );
    }

    // ========================================================
    // 12. RENDERIZADOR BASE
    // ========================================================

    private abstract class RenderizadorBase
            extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        protected void preparar(
                JLabel etiqueta,
                int fila,
                boolean seleccionada) {

            etiqueta.setOpaque(true);

            etiqueta.setBorder(
                    BorderFactory.createEmptyBorder(
                            PADDING_VERTICAL,
                            PADDING_HORIZONTAL,
                            PADDING_VERTICAL,
                            PADDING_HORIZONTAL
                    )
            );

            etiqueta.setFont(
                    Tipografia.celdaTabla()
            );

            if (seleccionada
                    && seleccionResaltada) {

                etiqueta.setBackground(
                        Paleta.TABLA_SELECCION
                );

                etiqueta.setForeground(
                        Paleta.BONE
                );

            } else {

                etiqueta.setBackground(
                        colorFondoFila(fila)
                );

                etiqueta.setForeground(
                        Paleta.TABLA_TEXTO
                );
            }
        }
    }

    // ========================================================
    // 13. RENDERIZADOR DE TEXTO
    // ========================================================

    private final class RenderizadorTexto
            extends RenderizadorBase {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {

            JLabel etiqueta =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    tabla,
                                    valor,
                                    seleccionada,
                                    foco,
                                    fila,
                                    columna
                            );

            preparar(
                    etiqueta,
                    fila,
                    seleccionada
            );

            etiqueta.setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            etiqueta.setText(
                    valor == null
                            ? ""
                            : valor.toString()
            );

            return etiqueta;
        }
    }

    // ========================================================
    // 14. RENDERIZADOR NUMERICO
    // ========================================================

    private final class RenderizadorNumerico
            extends RenderizadorBase {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {

            JLabel etiqueta =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    tabla,
                                    valor,
                                    seleccionada,
                                    foco,
                                    fila,
                                    columna
                            );

            preparar(
                    etiqueta,
                    fila,
                    seleccionada
            );

            etiqueta.setFont(
                    Tipografia.monoMedium(13f)
            );

            etiqueta.setHorizontalAlignment(
                    SwingConstants.RIGHT
            );

            if (valor instanceof Number numero) {

                etiqueta.setText(
                        formatearNumero(numero)
                );

            } else {

                etiqueta.setText(
                        valor == null
                                ? ""
                                : valor.toString()
                );
            }

            return etiqueta;
        }
    }

    // ========================================================
    // 15. RENDERIZADOR MONETARIO
    // ========================================================

    private final class RenderizadorMonetario
            extends RenderizadorBase {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {

            JLabel etiqueta =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    tabla,
                                    valor,
                                    seleccionada,
                                    foco,
                                    fila,
                                    columna
                            );

            preparar(
                    etiqueta,
                    fila,
                    seleccionada
            );

            etiqueta.setFont(
                    Tipografia.montoTabla()
            );

            etiqueta.setHorizontalAlignment(
                    SwingConstants.RIGHT
            );

            if (valor instanceof BigDecimal monto) {

                etiqueta.setText(
                        formatearMoneda(monto)
                );

                if (!seleccionada) {

                    etiqueta.setForeground(
                            monto.signum() < 0
                                    ? Paleta.FINANZAS_SALIDA
                                    : Paleta.TEXTO_NUMERICO
                    );
                }

            } else {

                etiqueta.setText(
                        valor == null
                                ? ""
                                : valor.toString()
                );
            }

            return etiqueta;
        }
    }

    // ========================================================
    // 16. RENDERIZADOR DE ESTADOS
    // ========================================================

    private final class RenderizadorEstado
            extends RenderizadorBase {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {

            JLabel etiqueta =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    tabla,
                                    valor,
                                    seleccionada,
                                    foco,
                                    fila,
                                    columna
                            );

            preparar(
                    etiqueta,
                    fila,
                    seleccionada
            );

            String estado =
                    valor == null
                            ? ""
                            : valor.toString();

            etiqueta.setText(
                    estado.toUpperCase(LOCALE_CO)
            );

            etiqueta.setFont(
                    Tipografia.medium(11f)
            );

            etiqueta.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            if (!seleccionada) {

                etiqueta.setForeground(
                        colorEstado(estado)
                );
            }

            return etiqueta;
        }
    }

    // ========================================================
    // 17. RENDERIZADOR BOOLEANO
    // ========================================================

    private final class RenderizadorBooleano
            extends RenderizadorBase {

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {

            JLabel etiqueta =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    tabla,
                                    valor,
                                    seleccionada,
                                    foco,
                                    fila,
                                    columna
                            );

            preparar(
                    etiqueta,
                    fila,
                    seleccionada
            );

            boolean activo =
                    Boolean.TRUE.equals(valor);

            etiqueta.setText(
                    activo ? "ACTIVO" : "INACTIVO"
            );

            etiqueta.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            etiqueta.setFont(
                    Tipografia.medium(11f)
            );

            if (!seleccionada) {

                etiqueta.setForeground(
                        activo
                                ? Paleta.EXITO
                                : Paleta.TEXTO_TERCIARIO
                );
            }

            return etiqueta;
        }
    }

    // ========================================================
    // 18. RENDERIZADOR DE CABECERA
    // ========================================================

    private final class RenderizadorCabecera
            extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionada,
                boolean foco,
                int fila,
                int columna) {

            JLabel etiqueta =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    tabla,
                                    valor,
                                    seleccionada,
                                    foco,
                                    fila,
                                    columna
                            );

            etiqueta.setText(
                    valor == null
                            ? ""
                            : valor.toString()
                            .toUpperCase(LOCALE_CO)
            );

            etiqueta.setOpaque(true);

            etiqueta.setBackground(
                    Paleta.TABLA_CABECERA
            );

            etiqueta.setForeground(
                    Paleta.TABLA_TEXTO_SECUNDARIO
            );

            etiqueta.setFont(
                    Tipografia.encabezadoTabla()
            );

            etiqueta.setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            etiqueta.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            PADDING_HORIZONTAL,
                            0,
                            PADDING_HORIZONTAL
                    )
            );

            return etiqueta;
        }
    }

    // ========================================================
    // 19. COLORES DE FILAS
    // ========================================================

    private Color colorFondoFila(
            int fila) {

        if (filasAlternas
                && fila % 2 != 0) {

            return Paleta.TABLA_FILA_ALTERNA;
        }

        return Paleta.TABLA_FILA;
    }

    // ========================================================
    // 20. COLORES DE ESTADO
    // ========================================================

    public static Color colorEstado(
            String estado) {

        if (estado == null) {

            return Paleta.TEXTO_SECUNDARIO;
        }

        return switch (
                estado.trim()
                        .toUpperCase(LOCALE_CO)
                ) {

            case "COMPLETADA",
                 "COMPLETADO",
                 "EXITOSA",
                 "EXITOSO",
                 "APROBADA",
                 "APROBADO",
                 "ACTIVO" -> Paleta.EXITO;

            case "PENDIENTE",
                 "PROGRAMADA",
                 "PROGRAMADO",
                 "EN PROCESO" -> Paleta.ADVERTENCIA;

            case "FALLIDA",
                 "FALLIDO",
                 "RECHAZADA",
                 "RECHAZADO",
                 "ERROR",
                 "BLOQUEADO" -> Paleta.ERROR;

            case "REVERTIDA",
                 "REVERTIDO" -> Paleta.REVERTIDA;

            case "CANCELADA",
                 "CANCELADO",
                 "INACTIVO" -> Paleta.CANCELADA;

            default -> Paleta.TEXTO_SECUNDARIO;
        };
    }

    // ========================================================
    // 21. FORMATEO NUMERICO
    // ========================================================

    public static String formatearNumero(
            Number numero) {

        if (numero == null) {
            return "";
        }

        DecimalFormatSymbols simbolos =
                new DecimalFormatSymbols(LOCALE_CO);

        simbolos.setGroupingSeparator('.');

        simbolos.setDecimalSeparator(',');

        DecimalFormat formato =
                new DecimalFormat(
                        "#,##0.########",
                        simbolos
                );

        return formato.format(numero);
    }

    // ========================================================
    // 22. FORMATEO MONETARIO
    // ========================================================

    public static String formatearMoneda(
            BigDecimal monto) {

        if (monto == null) {
            return "";
        }

        DecimalFormatSymbols simbolos =
                new DecimalFormatSymbols(LOCALE_CO);

        simbolos.setGroupingSeparator('.');

        simbolos.setDecimalSeparator(',');

        DecimalFormat formato =
                new DecimalFormat(
                        "#,##0.00",
                        simbolos
                );

        formato.setRoundingMode(
                java.math.RoundingMode.HALF_UP
        );

        String prefijo =
                monto.signum() < 0 ? "-$ " : "$ ";

        return prefijo + formato.format(
                monto.abs()
        );
    }

    // ========================================================
    // 23. CONFIGURAR COLUMNAS
    // ========================================================

    /**
     * Configura el ancho preferido
     * de una columna visible.
     */
    public void configurarAnchoColumna(
            int columna,
            int ancho) {

        if (columna < 0
                || columna >= getColumnCount()) {

            throw new IllegalArgumentException(
                    "Indice de columna invalido"
            );
        }

        if (ancho <= 0) {

            throw new IllegalArgumentException(
                    "El ancho debe ser positivo"
            );
        }

        TableColumn tablaColumna =
                getColumnModel()
                        .getColumn(columna);

        tablaColumna.setPreferredWidth(
                ancho
        );
    }

    /**
     * Configura una columna monetaria
     * sin cambiar el tipo del modelo.
     */
    public void configurarColumnaMonetaria(
            int columna) {

        validarColumna(columna);

        getColumnModel()
                .getColumn(columna)
                .setCellRenderer(
                        renderizadorMonetario
                );
    }

    /**
     * Configura una columna de estados.
     */
    public void configurarColumnaEstado(
            int columna) {

        validarColumna(columna);

        getColumnModel()
                .getColumn(columna)
                .setCellRenderer(
                        renderizadorEstado
                );
    }

    /**
     * Configura una columna numerica.
     */
    public void configurarColumnaNumerica(
            int columna) {

        validarColumna(columna);

        getColumnModel()
                .getColumn(columna)
                .setCellRenderer(
                        renderizadorNumerico
                );
    }

    /**
     * Configura una columna booleana.
     */
    public void configurarColumnaBooleana(
            int columna) {

        validarColumna(columna);

        getColumnModel()
                .getColumn(columna)
                .setCellRenderer(
                        renderizadorBooleano
                );
    }

    private void validarColumna(
            int columna) {

        if (columna < 0
                || columna >= getColumnCount()) {

            throw new IllegalArgumentException(
                    "Indice de columna invalido"
            );
        }
    }

    // ========================================================
    // 24. ALTERNANCIA DE FILAS
    // ========================================================

    public void setFilasAlternas(
            boolean habilitadas) {

        filasAlternas = habilitadas;

        repaint();
    }

    public boolean isFilasAlternas() {

        return filasAlternas;
    }

    // ========================================================
    // 25. SEPARADORES
    // ========================================================

    public void setMostrarSeparadores(
            boolean mostrar) {

        mostrarSeparadores = mostrar;

        setShowHorizontalLines(mostrar);

        repaint();
    }

    public boolean isMostrarSeparadores() {

        return mostrarSeparadores;
    }

    // ========================================================
    // 26. SELECCION
    // ========================================================

    public void setSeleccionResaltada(
            boolean habilitada) {

        seleccionResaltada = habilitada;

        repaint();
    }

    public boolean isSeleccionResaltada() {

        return seleccionResaltada;
    }

    // ========================================================
    // 27. ORDENAMIENTO
    // ========================================================

    public void setOrdenamientoHabilitado(
            boolean habilitado) {

        ordenamientoHabilitado = habilitado;

        if (ordenador != null) {

            for (int columna = 0;
                 columna < getModel().getColumnCount();
                 columna++) {

                ordenador.setSortable(
                        columna,
                        habilitado
                );
            }
        }
    }

    public boolean isOrdenamientoHabilitado() {

        return ordenamientoHabilitado;
    }

    // ========================================================
    // 28. BUSQUEDA
    // ========================================================

    /**
     * Busca texto en todas las columnas
     * utilizando coincidencias parciales.
     *
     * La busqueda no distingue mayusculas
     * de minusculas.
     *
     * Se escapa el texto ingresado para
     * que no actue como expresion regular.
     */
    public void buscar(
            String texto) {

        textoBusqueda =
                texto == null
                        ? ""
                        : texto.trim();

        if (textoBusqueda.isEmpty()) {

            filtroBusqueda = null;

        } else {

            String expresion =
                    "(?i)"
                            + Pattern.quote(
                            textoBusqueda
                    );

            filtroBusqueda =
                    RowFilter.regexFilter(
                            expresion
                    );
        }

        paginaActual = 1;

        aplicarFiltros();
    }

    /**
     * Elimina el filtro de busqueda.
     */
    public void limpiarBusqueda() {

        buscar("");
    }

    public String getTextoBusqueda() {

        return textoBusqueda;
    }

    // ========================================================
    // 29. FILTRO EXTERNO
    // ========================================================

    /**
     * Permite que los paneles configuren
     * filtros adicionales.
     *
     * Ejemplo:
     * Mostrar solo transacciones pendientes.
     */
    public void setFiltroExterno(
            RowFilter<TableModel, Integer> filtro) {

        filtroExterno = filtro;

        paginaActual = 1;

        aplicarFiltros();
    }

    public void limpiarFiltroExterno() {

        filtroExterno = null;

        paginaActual = 1;

        aplicarFiltros();
    }

    // ========================================================
    // 30. PAGINACION
    // ========================================================

    public void setPaginacionHabilitada(
            boolean habilitada) {

        paginacionHabilitada = habilitada;

        paginaActual = 1;

        aplicarFiltros();
    }

    public boolean isPaginacionHabilitada() {

        return paginacionHabilitada;
    }

    public void setTamanoPagina(
            int cantidad) {

        if (cantidad <= 0) {

            throw new IllegalArgumentException(
                    "El tamano de pagina "
                            + "debe ser positivo"
            );
        }

        tamanoPagina = cantidad;

        paginaActual = 1;

        aplicarFiltros();
    }

    public int getTamanoPagina() {

        return tamanoPagina;
    }

    public int getPaginaActual() {

        return paginaActual;
    }

    /**
     * Establece la pagina visible.
     */
    public void irAPagina(
            int pagina) {

        if (pagina < 1
                || pagina > getTotalPaginas()) {

            throw new IllegalArgumentException(
                    "Pagina fuera de rango"
            );
        }

        paginaActual = pagina;

        aplicarFiltros();
    }

    public void paginaSiguiente() {

        if (paginaActual < getTotalPaginas()) {

            paginaActual++;

            aplicarFiltros();
        }
    }

    public void paginaAnterior() {

        if (paginaActual > 1) {

            paginaActual--;

            aplicarFiltros();
        }
    }

    /**
     * Devuelve el numero total de paginas.
     *
     * El calculo considera los filtros
     * de busqueda y externos.
     */
    public int getTotalPaginas() {

        int registros =
                contarRegistrosFiltrados();

        if (registros == 0) {
            return 1;
        }

        return (int) (
                ((long) registros
                        + tamanoPagina - 1)
                        / tamanoPagina
        );
    }

    // ========================================================
    // 31. CONTAR REGISTROS FILTRADOS
    // ========================================================

    private int contarRegistrosFiltrados() {

        TableModel modelo =
                getModel();

        int total = 0;

        for (int fila = 0;
             fila < modelo.getRowCount();
             fila++) {

            if (cumpleFiltros(fila)) {

                total++;
            }
        }

        return total;
    }

    // ========================================================
    // 32. APLICAR FILTROS
    // ========================================================

    private void aplicarFiltros() {

        if (ordenador == null) {
            return;
        }

        int totalPaginas =
                getTotalPaginas();

        if (paginaActual > totalPaginas) {

            paginaActual = totalPaginas;
        }

        ordenador.setRowFilter(
                new RowFilter<TableModel, Integer>() {

                    @Override
                    public boolean include(
                            Entry<? extends TableModel,
                                    ? extends Integer> entrada) {

                        int fila =
                                entrada.getIdentifier();

                        if (!cumpleFiltros(entrada)) {

                            return false;
                        }

                        if (!paginacionHabilitada) {

                            return true;
                        }

                        int posicion =
                                posicionFiltrada(fila);

                        int inicio =
                                (paginaActual - 1)
                                        * tamanoPagina;

                        int fin =
                                inicio + tamanoPagina;

                        return posicion >= inicio
                                && posicion < fin;
                    }
                }
        );
    }

    // ========================================================
    // 33. COMPROBAR FILTROS
    // ========================================================

    private boolean cumpleFiltros(
            int filaModelo) {

        return cumpleFiltros(
                new EntradaModelo(
                        getModel(),
                        filaModelo
                )
        );
    }

    private boolean cumpleFiltros(
            RowFilter.Entry<? extends TableModel,
                    ? extends Integer> entrada) {

        boolean coincideBusqueda =
                filtroBusqueda == null
                        || filtroBusqueda.include(
                        entrada
                );

        boolean coincideExterno =
                filtroExterno == null
                        || filtroExterno.include(
                        entrada
                );

        return coincideBusqueda
                && coincideExterno;
    }

    // ========================================================
    // 34. POSICION FILTRADA
    // ========================================================

    private int posicionFiltrada(
            int filaModelo) {

        int posicion = 0;

        for (int fila = 0;
             fila <= filaModelo;
             fila++) {

            if (cumpleFiltros(fila)) {

                if (fila == filaModelo) {
                    return posicion;
                }

                posicion++;
            }
        }

        return -1;
    }

    // ========================================================
    // 35. ENTRADA DE MODELO
    // ========================================================

    /**
     * Adaptador para evaluar RowFilter
     * sobre filas del modelo.
     *
     * No almacena copias de datos.
     */
    private static final class EntradaModelo
            extends RowFilter.Entry<TableModel, Integer> {

        private final TableModel modelo;

        private final int fila;

        private EntradaModelo(
                TableModel modelo,
                int fila) {

            this.modelo = modelo;

            this.fila = fila;
        }

        @Override
        public TableModel getModel() {

            return modelo;
        }

        @Override
        public int getValueCount() {

            return modelo.getColumnCount();
        }

        @Override
        public Object getValue(
                int indice) {

            return modelo.getValueAt(
                    fila,
                    indice
            );
        }

        @Override
        public String getStringValue(
                int indice) {

            Object valor =
                    getValue(indice);

            return valor == null
                    ? ""
                    : valor.toString();
        }

        @Override
        public Integer getIdentifier() {

            return fila;
        }
    }

    // ========================================================
    // 36. ESTADO VACIO
    // ========================================================

    public void setMostrarEstadoVacio(
            boolean mostrar) {

        mostrarEstadoVacio = mostrar;

        repaint();
    }

    public boolean isMostrarEstadoVacio() {

        return mostrarEstadoVacio;
    }

    public void setMensajeVacio(
            String mensaje) {

        mensajeVacio =
                mensaje == null
                        ? ""
                        : mensaje;

        repaint();
    }

    public String getMensajeVacio() {

        return mensajeVacio;
    }

    // ========================================================
    // 37. PINTAR ESTADO VACIO
    // ========================================================

    @Override
    protected void paintComponent(
            Graphics graphics) {

        super.paintComponent(graphics);

        if (!mostrarEstadoVacio
                || getRowCount() > 0) {

            return;
        }

        Graphics2D g2 =
                (Graphics2D) graphics.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            g2.setFont(
                    Tipografia.cuerpo()
            );

            g2.setColor(
                    Paleta.TEXTO_TERCIARIO
            );

            java.awt.FontMetrics metricas =
                    g2.getFontMetrics();

            int anchoTexto =
                    metricas.stringWidth(
                            mensajeVacio
                    );

            int x =
                    (getWidth() - anchoTexto) / 2;

            int y =
                    Math.max(
                            60,
                            getHeight() / 2
                    );

            g2.drawString(
                    mensajeVacio,
                    Math.max(12, x),
                    y
            );

        } finally {

            g2.dispose();
        }
    }

    // ========================================================
    // 38. OBTENER FILA SELECCIONADA
    // ========================================================

    /**
     * Devuelve el indice del modelo,
     * no el indice visual.
     *
     * Esto es importante cuando hay
     * filtros u ordenamiento activo.
     */
    public int getFilaModeloSeleccionada() {

        int filaVista =
                getSelectedRow();

        if (filaVista < 0) {
            return -1;
        }

        return convertRowIndexToModel(
                filaVista
        );
    }

    /**
     * Devuelve el valor seleccionado
     * utilizando coordenadas del modelo.
     */
    public Object getValorSeleccionado(
            int columnaModelo) {

        int filaModelo =
                getFilaModeloSeleccionada();

        if (filaModelo < 0) {
            return null;
        }

        if (columnaModelo < 0
                || columnaModelo >= getModel()
                .getColumnCount()) {

            throw new IllegalArgumentException(
                    "Columna fuera de rango"
            );
        }

        return getModel().getValueAt(
                filaModelo,
                columnaModelo
        );
    }

    // ========================================================
    // 39. CARGAR REGISTROS
    // ========================================================

    /**
     * Agrega una fila si el modelo es
     * ModeloNoEditable o DefaultTableModel.
     */
    public void agregarFila(
            Object... valores) {

        if (!(getModel()
                instanceof DefaultTableModel modelo)) {

            throw new IllegalStateException(
                    "El modelo no permite agregar filas"
            );
        }

        modelo.addRow(
                valores
        );
    }

    /**
     * Elimina todos los registros del modelo.
     */
    public void limpiarRegistros() {

        if (!(getModel()
                instanceof DefaultTableModel modelo)) {

            throw new IllegalStateException(
                    "El modelo no permite eliminar filas"
            );
        }

        modelo.setRowCount(0);

        paginaActual = 1;

        aplicarFiltros();
    }

    // ========================================================
    // 40. CAMBIAR MODELO
    // ========================================================

    @Override
    public void setModel(
            TableModel modelo) {

        Objects.requireNonNull(
                modelo,
                "El modelo no puede ser null"
        );

        super.setModel(modelo);

        // Durante la construccion de JTable,
        // los campos personalizados aun no
        // estan inicializados.

        if (renderizadorTexto != null) {

            configurarRenderizadores();

            configurarOrdenador();

            configurarCabecera();
        }
    }

    // ========================================================
    // 41. ESTADISTICAS
    // ========================================================

    public int cantidadRegistrosTotales() {

        return getModel().getRowCount();
    }

    public int cantidadRegistrosVisibles() {

        return getRowCount();
    }

    public int cantidadRegistrosFiltrados() {

        return contarRegistrosFiltrados();
    }

    // ========================================================
    // 42. INFORMACION DEL COMPONENTE
    // ========================================================

    public String generarDescripcion() {

        return "TablaPremium"
                + " | Columnas: " + getColumnCount()
                + " | Registros: "
                + cantidadRegistrosTotales()
                + " | Visibles: "
                + cantidadRegistrosVisibles()
                + " | Pagina: " + paginaActual
                + " | Paginacion: "
                + paginacionHabilitada
                + " | Busqueda: " + textoBusqueda;
    }
}
