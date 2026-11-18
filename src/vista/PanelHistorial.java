
package vista;

import modelo.Transaccion;
import servicios.ContextoAplicacion;

import vista.componentes.BotonPremium;
import vista.componentes.EncabezadoPanel;
import vista.componentes.TablaPremium;

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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.io.BufferedWriter;
import java.io.IOException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.text.NumberFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * ============================================================
 *                   MONEDERO VIRTUAL
 *                     PRIVATE LEDGER
 * ============================================================
 *
 * PANEL:
 * PanelHistorial.java
 *
 * ESTILO:
 * Institutional Noir
 *
 * RESPONSABILIDADES:
 *
 * 1. Consultar transacciones registradas.
 * 2. Visualizar movimientos financieros.
 * 3. Buscar movimientos.
 * 4. Examinar una transaccion.
 * 5. Exportar registros a CSV.
 * 6. Actualizar indicadores.
 * 7. Consultar el servicio compartido.
 *
 * IMPORTANTE:
 *
 * Este panel es exclusivamente
 * de lectura.
 *
 * No modifica transacciones.
 * No cambia saldos.
 * No ejecuta movimientos.
 *
 * La lectura utiliza un adaptador
 * para distintas implementaciones
 * de colecciones Java.
 *
 * Java 21.
 */
public final class PanelHistorial extends JPanel {

    private static final long serialVersionUID = 1L;

    // ========================================================
    // 1. CONSTANTES
    // ========================================================

    private static final String TITULO =
            "Historial.";

    private static final String SUBTITULO =
            "Archivo cronologico de movimientos financieros.";

    private static final String IDENTIFICADOR =
            "WORKSPACE / 006";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    private static final int LIMITE_RECORRIDO =
            10000;

    // ========================================================
    // 2. CONTEXTO COMPARTIDO
    // ========================================================

    private final ContextoAplicacion contexto;

    // ========================================================
    // 3. COMPONENTES
    // ========================================================

    private EncabezadoPanel encabezado;

    private JPanel contenido;

    private JPanel columna;

    private JPanel panelIndicadores;

    private JPanel panelDirectorio;

    private JLabel etiquetaTotal;

    private JLabel etiquetaVisibles;

    private JLabel etiquetaEstado;

    private JLabel etiquetaResultados;

    private JTextField campoBusqueda;

    private TablaPremium tabla;

    private TablaPremium.ModeloNoEditable modelo;

    // ========================================================
    // 4. ESTADO
    // ========================================================

    private boolean actualizando;

    private boolean historialCompleto;

    private final List<Movimiento> movimientos =
            new ArrayList<>();

    // ========================================================
    // 5. REPRESENTACION DE LECTURA
    // ========================================================

    /**
     * Contiene los datos necesarios
     * para presentar una transaccion.
     *
     * No sustituye a modelo.Transaccion.
     */
    private record Movimiento(
            String fecha,
            String id,
            String tipo,
            String origen,
            String destino,
            BigDecimal monto,
            String estado,
            String detalle) {
    }

    // ========================================================
    // 6. CONSTRUCTOR
    // ========================================================

    public PanelHistorial(
            ContextoAplicacion contexto) {

        super(new BorderLayout());

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "ContextoAplicacion es obligatorio"
                );

        this.actualizando = false;

        this.historialCompleto = false;

        inicializar();

        refrescarDatos();
    }

    // ========================================================
    // 7. INICIALIZAR PANTALLA
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

        construirDirectorio();

        agregarBloque(encabezado);

        agregarBloque(panelIndicadores);

        agregarBloque(panelDirectorio);

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
    // 8. AGREGAR BLOQUES
    // ========================================================

    private void agregarBloque(
            JPanel bloque) {

        bloque.setAlignmentX(
                LEFT_ALIGNMENT
        );

        if (columna.getComponentCount() > 0) {

            columna.add(
                    Box.createVerticalStrut(
                            24
                    )
            );
        }

        columna.add(
                bloque
        );
    }

    // ========================================================
    // 9. ENCABEZADO
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
                "Exportar CSV",
                this::exportarCSV
        );

        encabezado.crearAccionSecundaria(
                "Actualizar",
                this::refrescarDatos
        );
    }

    // ========================================================
    // 10. INDICADORES
    // ========================================================

    private void construirIndicadores() {

        panelIndicadores =
                new JPanel(
                        new GridLayout(
                                1, 2, 16, 0
                        )
                );

        panelIndicadores.setOpaque(false);

        etiquetaTotal =
                crearValorIndicador(
                        "0"
                );

        etiquetaVisibles =
                crearValorIndicador(
                        "0"
                );

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "OPERACIONES REGISTRADAS",
                        etiquetaTotal,
                        "Total registrado por el motor"
                )
        );

        panelIndicadores.add(
                crearTarjetaIndicador(
                        "REGISTROS VISIBLES",
                        etiquetaVisibles,
                        "Coincidencias del filtro actual"
                )
        );
    }

    // ========================================================
    // 11. TARJETA INDICADORA
    // ========================================================

    private JPanel crearTarjetaIndicador(
            String titulo,
            JLabel valor,
            String descripcion) {

        JPanel tarjeta =
                new JPanel();

        tarjeta.setOpaque(true);

        tarjeta.setBackground(
                Paleta.FONDO_TARJETA
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.BORDE_SUAVE
                        ),
                        BorderFactory.createEmptyBorder(
                                20, 22, 20, 22
                        )
                )
        );

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel nombre =
                new JLabel(
                        titulo
                );

        nombre.setFont(
                Tipografia.mono(10f)
        );

        nombre.setForeground(
                Paleta.CHAMPAGNE
        );

        JLabel detalle =
                new JLabel(
                        descripcion
                );

        detalle.setFont(
                Tipografia.cuerpoPequeno()
        );

        detalle.setForeground(
                Paleta.ASH
        );

        nombre.setAlignmentX(
                LEFT_ALIGNMENT
        );

        valor.setAlignmentX(
                LEFT_ALIGNMENT
        );

        detalle.setAlignmentX(
                LEFT_ALIGNMENT
        );

        tarjeta.add(nombre);

        tarjeta.add(
                Box.createVerticalStrut(12)
        );

        tarjeta.add(valor);

        tarjeta.add(
                Box.createVerticalStrut(8)
        );

        tarjeta.add(detalle);

        return tarjeta;
    }

    private JLabel crearValorIndicador(
            String valor) {

        JLabel etiqueta =
                new JLabel(valor);

        etiqueta.setFont(
                Tipografia.editorial(38f)
        );

        etiqueta.setForeground(
                Paleta.BONE
        );

        return etiqueta;
    }

    // ========================================================
    // 12. DIRECTORIO
    // ========================================================

    private void construirDirectorio() {

        panelDirectorio =
                new JPanel(
                        new BorderLayout(
                                0, 20
                        )
                );

        panelDirectorio.setOpaque(true);

        panelDirectorio.setBackground(
                Paleta.FONDO_TARJETA
        );

        panelDirectorio.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.BORDE_SUAVE
                        ),
                        BorderFactory.createEmptyBorder(
                                24, 24, 24, 24
                        )
                )
        );

        construirBarraBusqueda();

        construirTabla();

        construirPie();
    }

    // ========================================================
    // 13. BARRA DE BUSQUEDA
    // ========================================================

    private void construirBarraBusqueda() {

        JPanel barra =
                new JPanel(
                        new BorderLayout(
                                16, 0
                        )
                );

        barra.setOpaque(false);

        JPanel textos =
                new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Registro de movimientos"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        JLabel descripcion =
                new JLabel(
                        "Historial financiero de solo lectura"
                );

        descripcion.setFont(
                Tipografia.cuerpoPequeno()
        );

        descripcion.setForeground(
                Paleta.ASH
        );

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(6)
        );

        textos.add(descripcion);

        campoBusqueda =
                new JTextField();

        campoBusqueda.setFont(
                Tipografia.cuerpo()
        );

        campoBusqueda.setBackground(
                Paleta.INPUT_FONDO
        );

        campoBusqueda.setForeground(
                Paleta.INPUT_TEXTO
        );

        campoBusqueda.setCaretColor(
                Paleta.CHAMPAGNE
        );

        campoBusqueda.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.INPUT_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 12, 10, 12
                        )
                )
        );

        campoBusqueda.setPreferredSize(
                new Dimension(
                        290, 42
                )
        );

        campoBusqueda.setToolTipText(
                "Buscar por fecha, ID, tipo, origen, destino o estado"
        );

        campoBusqueda.getAccessibleContext()
                .setAccessibleName(
                        "Buscar movimientos"
                );

        campoBusqueda.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {

                                aplicarBusqueda();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {

                                aplicarBusqueda();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {

                                aplicarBusqueda();
                            }
                        }
                );

        barra.add(
                textos,
                BorderLayout.CENTER
        );

        barra.add(
                campoBusqueda,
                BorderLayout.EAST
        );

        panelDirectorio.add(
                barra,
                BorderLayout.NORTH
        );
    }

    // ========================================================
    // 14. TABLA
    // ========================================================

    private void construirTabla() {

        modelo =
                new TablaPremium.ModeloNoEditable(
                        new String[]{
                                "FECHA",
                                "ID",
                                "TIPO",
                                "ORIGEN",
                                "DESTINO",
                                "MONTO / COP",
                                "ESTADO"
                        },
                        0
                );

        tabla =
                new TablaPremium(
                        modelo
                );

        tabla.setMensajeVacio(
                "No hay movimientos para mostrar"
        );

        tabla.setFilasAlternas(
                true
        );

        tabla.setMostrarSeparadores(
                true
        );

        tabla.configurarAnchoColumna(0, 160);
        tabla.configurarAnchoColumna(1, 100);
        tabla.configurarAnchoColumna(2, 135);
        tabla.configurarAnchoColumna(3, 110);
        tabla.configurarAnchoColumna(4, 110);
        tabla.configurarAnchoColumna(5, 155);
        tabla.configurarAnchoColumna(6, 110);

        tabla.configurarColumnaMonetaria(
                5
        );

        tabla.setPreferredScrollableViewportSize(
                new Dimension(
                        1020, 370
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.getViewport().setBackground(
                Paleta.FONDO_TARJETA
        );

        scroll.setPreferredSize(
                new Dimension(
                        1020, 390
                )
        );

        panelDirectorio.add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 15. PIE DEL DIRECTORIO
    // ========================================================

    private void construirPie() {

        JPanel pie =
                new JPanel(
                        new BorderLayout(
                                12, 10
                        )
                );

        pie.setOpaque(false);

        etiquetaResultados =
                new JLabel(
                        "0 registros"
                );

        etiquetaResultados.setFont(
                Tipografia.caption()
        );

        etiquetaResultados.setForeground(
                Paleta.ASH
        );

        BotonPremium consultar =
                BotonPremium.secundario(
                        "Ver detalle"
                );

        consultar.addActionListener(
                evento -> mostrarDetalleSeleccionado()
        );

        JPanel fila =
                new JPanel(
                        new BorderLayout()
                );

        fila.setOpaque(false);

        fila.add(
                etiquetaResultados,
                BorderLayout.WEST
        );

        fila.add(
                consultar,
                BorderLayout.EAST
        );

        etiquetaEstado =
                new JLabel(
                        "Archivo financiero preparado"
                );

        etiquetaEstado.setFont(
                Tipografia.caption()
        );

        etiquetaEstado.setForeground(
                Paleta.ASH
        );

        pie.add(
                fila,
                BorderLayout.NORTH
        );

        pie.add(
                etiquetaEstado,
                BorderLayout.SOUTH
        );

        panelDirectorio.add(
                pie,
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 16. REFRESCAR HISTORIAL
    // ========================================================

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

            int total =
                    contexto.getTransaccionService()
                            .cantidadTransacciones();

            etiquetaTotal.setText(
                    Integer.toString(total)
            );

            List<Transaccion> registros =
                    consultarTransacciones();

            movimientos.clear();

            modelo.setRowCount(0);

            int indice = 1;

            for (Transaccion transaccion : registros) {

                Movimiento movimiento =
                        convertirMovimiento(
                                transaccion,
                                indice
                        );

                movimientos.add(
                        movimiento
                );

                modelo.addRow(
                        new Object[]{
                                movimiento.fecha(),
                                movimiento.id(),
                                movimiento.tipo(),
                                movimiento.origen(),
                                movimiento.destino(),
                                movimiento.monto(),
                                movimiento.estado()
                        }
                );

                indice++;
            }

            aplicarBusqueda();

            historialCompleto =
                    registros.size() == total;

            if (historialCompleto) {

                actualizarEstado(
                        "HISTORIAL ACTUALIZADO",
                        false
                );

            } else {

                actualizarEstado(
                        "LECTURA PARCIAL: "
                                + registros.size()
                                + " DE "
                                + total
                                + " MOVIMIENTOS",
                        true
                );
            }

        } catch (RuntimeException error) {

            historialCompleto = false;

            actualizarEstado(
                    "ERROR AL CONSULTAR EL HISTORIAL",
                    true
            );

            System.err.println(
                    "[HISTORIAL] "
                            + error.getMessage()
            );

        } finally {

            actualizando = false;
        }
    }

    // ========================================================
    // 17. CONSULTAR TRANSACCIONES
    // ========================================================

    /**
     * Adaptador temporal de lectura.
     *
     * Busca una coleccion expuesta por
     * TransaccionService y la recorre
     * sin modificar sus elementos.
     *
     * Si el servicio utiliza otra API,
     * el panel informara lectura parcial.
     */

    // ========================================================
    // CONSULTAR HISTORIAL REAL
    // ========================================================

    /**
     * Obtiene todas las transacciones registradas.
     *
     * ESTRUCTURA UTILIZADA:
     * ListaEnlazada<Transaccion>
     *
     * El servicio devuelve una copia de la lista.
     *
     * Recorremos los nodos mediante el metodo
     * recorrer(Consumer), implementado en
     * nuestra estructura personalizada.
     *
     * No modificamos el historial original.
     */
    private List<Transaccion> consultarTransacciones() {

        List<Transaccion> resultado =
                new ArrayList<>();

        contexto.getTransaccionService()
                .obtenerHistorial()
                .recorrer(resultado::add);

        return resultado;
    }


    // ========================================================
    // 18. RECORRER OBJETOS
    // ========================================================

    private void recorrerObjeto(
            Object objeto,
            List<Transaccion> encontrados,
            Set<Object> visitados,
            int profundidad) {

        if (objeto == null
                || profundidad > LIMITE_RECORRIDO
                || !visitados.add(objeto)) {

            return;
        }

        if (objeto instanceof Transaccion t) {

            encontrados.add(t);
            return;
        }

        if (objeto instanceof Map<?, ?> mapa) {

            for (Object valor : mapa.values()) {

                recorrerObjeto(
                        valor,
                        encontrados,
                        visitados,
                        profundidad + 1
                );
            }

            return;
        }

        if (objeto instanceof Iterable<?> iterable) {

            for (Object elemento : iterable) {

                recorrerObjeto(
                        elemento,
                        encontrados,
                        visitados,
                        profundidad + 1
                );
            }

            return;
        }

        if (objeto.getClass().isArray()) {

            int cantidad =
                    Array.getLength(objeto);

            for (int i = 0; i < cantidad; i++) {

                recorrerObjeto(
                        Array.get(objeto, i),
                        encontrados,
                        visitados,
                        profundidad + 1
                );
            }

            return;
        }

        // Estructuras personalizadas:
        // ListaEnlazada, ListaCircular, etc.

        if (recorrerConConsumer(
                objeto,
                "recorrer",
                encontrados,
                visitados,
                profundidad
        )) {
            return;
        }

        String clase =
                objeto.getClass()
                        .getSimpleName()
                        .toLowerCase(Locale.ROOT);

        if (clase.contains("nodo")) {

            recorrerObjeto(
                    leerPrimero(
                            objeto,
                            "getDato",
                            "getValor",
                            "getElemento"
                    ),
                    encontrados,
                    visitados,
                    profundidad + 1
            );

            recorrerObjeto(
                    leerPrimero(
                            objeto,
                            "getSiguiente",
                            "getNext"
                    ),
                    encontrados,
                    visitados,
                    profundidad + 1
            );

            // Apoyo para nodos con campos
            // privados en lugar de getters.

            for (Field campo :
                    obtenerCampos(objeto.getClass())) {

                String nombre =
                        campo.getName()
                                .toLowerCase(Locale.ROOT);

                if (nombre.equals("dato")
                        || nombre.equals("valor")
                        || nombre.equals("elemento")
                        || nombre.equals("siguiente")) {

                    recorrerObjeto(
                            leerCampo(
                                    campo,
                                    objeto
                            ),
                            encontrados,
                            visitados,
                            profundidad + 1
                    );
                }
            }

            return;
        }

        if (clase.contains("lista")
                || clase.contains("cola")
                || clase.contains("historial")) {

            recorrerObjeto(
                    leerPrimero(
                            objeto,
                            "getCabeza",
                            "getPrimero",
                            "getInicio",
                            "getFrente"
                    ),
                    encontrados,
                    visitados,
                    profundidad + 1
            );

            for (Field campo :
                    obtenerCampos(objeto.getClass())) {

                String nombre =
                        campo.getName()
                                .toLowerCase(Locale.ROOT);

                if (nombre.equals("cabeza")
                        || nombre.equals("primero")
                        || nombre.equals("inicio")
                        || nombre.equals("frente")) {

                    recorrerObjeto(
                            leerCampo(
                                    campo,
                                    objeto
                            ),
                            encontrados,
                            visitados,
                            profundidad + 1
                    );
                }
            }
        }
    }

    // ========================================================
    // 19. RECORRIDO MEDIANTE CONSUMER
    // ========================================================

    private boolean recorrerConConsumer(
            Object objeto,
            String nombreMetodo,
            List<Transaccion> encontrados,
            Set<Object> visitados,
            int profundidad) {

        try {

            Method metodo =
                    objeto.getClass().getMethod(
                            nombreMetodo,
                            Consumer.class
                    );

            Consumer<Object> consumidor =
                    elemento -> recorrerObjeto(
                            elemento,
                            encontrados,
                            visitados,
                            profundidad + 1
                    );

            metodo.invoke(
                    objeto,
                    consumidor
            );

            return true;

        } catch (ReflectiveOperationException
                 | SecurityException error) {

            return false;
        }
    }

    // ========================================================
    // 20. UTILIDADES REFLEXIVAS
    // ========================================================

    private static Object invocarSinArgumentos(
            Object objeto,
            String nombre) {

        if (objeto == null) {
            return null;
        }

        try {

            Method metodo =
                    objeto.getClass().getMethod(
                            nombre
                    );

            return metodo.invoke(objeto);

        } catch (ReflectiveOperationException
                 | SecurityException error) {

            return null;
        }
    }

    private static Object leerPrimero(
            Object objeto,
            String... nombres) {

        for (String nombre : nombres) {

            Object valor =
                    invocarSinArgumentos(
                            objeto,
                            nombre
                    );

            if (valor != null) {
                return valor;
            }
        }

        return null;
    }

    private static List<Field> obtenerCampos(
            Class<?> clase) {

        List<Field> campos =
                new ArrayList<>();

        Class<?> actual = clase;

        while (actual != null
                && actual != Object.class) {

            Collections.addAll(
                    campos,
                    actual.getDeclaredFields()
            );

            actual = actual.getSuperclass();
        }

        return campos;
    }

    private static Object leerCampo(
            Field campo,
            Object objeto) {

        try {

            campo.setAccessible(true);

            return campo.get(objeto);

        } catch (ReflectiveOperationException
                 | RuntimeException error) {

            return null;
        }
    }

    private static String leerTexto(
            Object objeto,
            String... metodos) {

        Object valor =
                leerPrimero(
                        objeto,
                        metodos
                );

        if (valor == null) {

            for (String metodo : metodos) {

                if (!metodo.startsWith("get")) {
                    continue;
                }

                String propiedad =
                        metodo.substring(3);

                if (propiedad.isBlank()) {
                    continue;
                }

                String nombreCampo =
                        Character.toLowerCase(
                                propiedad.charAt(0)
                        ) + propiedad.substring(1);

                for (Field campo :
                        obtenerCampos(objeto.getClass())) {

                    if (campo.getName().equals(
                            nombreCampo
                    )) {

                        valor =
                                leerCampo(
                                        campo,
                                        objeto
                                );

                        break;
                    }
                }

                if (valor != null) {
                    break;
                }
            }
        }

        return convertirTexto(valor);
    }

    private static String convertirTexto(
            Object valor) {

        if (valor == null) {
            return "";
        }

        if (valor instanceof String
                || valor instanceof Number
                || valor instanceof Enum<?>) {

            return valor.toString();
        }

        Object id =
                leerPrimero(
                        valor,
                        "getId",
                        "getCodigo",
                        "getIdMonedero"
                );

        return id == null
                ? valor.toString()
                : id.toString();
    }

    // ========================================================
    // 21. CONVERTIR TRANSACCION
    // ========================================================


    // ========================================================
    // CONVERTIR TRANSACCION REAL
    // ========================================================

    /**
     * Convierte una transaccion del modelo
     * en un registro de presentacion.
     *
     * No utiliza reflexion.
     *
     * No modifica la transaccion.
     *
     * Se respetan los identificadores
     * reales de origen y destino.
     */
    private Movimiento convertirMovimiento(
            Transaccion transaccion,
            int indice) {

        Objects.requireNonNull(
                transaccion,
                "La transaccion no puede ser null"
        );

        // ----------------------------------
        // IDENTIFICADOR
        // ----------------------------------

        String id =
                transaccion.getId();

        // ----------------------------------
        // TIPO DE OPERACION
        // ----------------------------------

        String tipo =
                transaccion.getTipo().name();

        // ----------------------------------
        // FECHA REAL
        // ----------------------------------

        String fecha =
                transaccion.getFechaFormateada();

        // ----------------------------------
        // MONEDEROS
        // ----------------------------------

        String origen =
                valorVisible(
                        transaccion.getIdMonederoOrigen()
                );

        String destino =
                valorVisible(
                        transaccion.getIdMonederoDestino()
                );

        // ----------------------------------
        // DATOS FINANCIEROS
        // ----------------------------------

        BigDecimal monto =
                transaccion.getMonto();

        BigDecimal comision =
                transaccion.getComision();

        String estado =
                transaccion.getEstado().name();

        // ----------------------------------
        // INFORMACION ADICIONAL
        // ----------------------------------

        String clienteOrigen =
                valorVisible(
                        transaccion.getIdClienteOrigen()
                );

        String clienteDestino =
                valorVisible(
                        transaccion.getIdClienteDestino()
                );

        String descripcion =
                valorVisible(
                        transaccion.getDescripcion()
                );

        String idOriginal =
                valorVisible(
                        transaccion.getIdTransaccionOriginal()
                );

        // ----------------------------------
        // DETALLE COMPLETO
        // ----------------------------------

        String detalle =
                "MONEDERO / PRIVATE LEDGER"
                        + "\n"
                        + "========================================"
                        + "\n\nID TRANSACCION:"
                        + "\n" + id
                        + "\n\nTIPO:"
                        + "\n" + tipo
                        + "\n\nFECHA:"
                        + "\n" + fecha
                        + "\n\nESTADO:"
                        + "\n" + estado
                        + "\n\n----------------------------------------"
                        + "\nCLIENTE ORIGEN: " + clienteOrigen
                        + "\nMONEDERO ORIGEN: " + origen
                        + "\n\nCLIENTE DESTINO: " + clienteDestino
                        + "\nMONEDERO DESTINO: " + destino
                        + "\n\n----------------------------------------"
                        + "\nMONTO: " + formatearMoneda(monto)
                        + "\nCOMISION: " + formatearMoneda(comision)
                        + "\nCOSTO TOTAL: "
                        + formatearMoneda(
                        transaccion.calcularCostoTotal()
                )
                        + "\nPUNTOS GENERADOS: "
                        + transaccion.getPuntosGenerados()
                        + "\n\n----------------------------------------"
                        + "\nDESCRIPCION:"
                        + "\n" + descripcion
                        + "\n\nTRANSACCION ORIGINAL:"
                        + "\n" + idOriginal;

        return new Movimiento(
                fecha,
                id,
                tipo,
                origen,
                destino,
                monto,
                estado,
                detalle
        );
    }

    // ========================================================
    // REPRESENTAR VALORES OPCIONALES
    // ========================================================

    private static String valorVisible(
            String valor) {

        if (valor == null || valor.isBlank()) {
            return "—";
        }

        return valor;
    }


    // ========================================================
    // 22. LEER FECHA
    // ========================================================

    private String obtenerFecha(
            Transaccion transaccion) {

        Object valor =
                leerPrimero(
                        transaccion,
                        "getFecha",
                        "getFechaHora",
                        "getFechaTransaccion",
                        "getFechaCreacion"
                );

        if (valor instanceof LocalDateTime fecha) {

            return fecha.format(
                    FORMATO_FECHA
            );
        }

        return convertirTexto(valor);
    }

    // ========================================================
    // 23. LEER MONTO
    // ========================================================

    private BigDecimal obtenerMonto(
            Transaccion transaccion) {

        Object valor =
                leerPrimero(
                        transaccion,
                        "getMonto",
                        "getImporte",
                        "getValor",
                        "getCantidad"
                );

        if (valor == null) {

            for (Field campo :
                    obtenerCampos(transaccion.getClass())) {

                String nombre =
                        campo.getName()
                                .toLowerCase(Locale.ROOT);

                if (nombre.equals("monto")
                        || nombre.equals("importe")) {

                    valor = leerCampo(
                            campo,
                            transaccion
                    );

                    break;
                }
            }
        }

        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }

        if (valor instanceof Number numero) {

            return new BigDecimal(
                    numero.toString()
            );
        }

        if (valor != null) {

            try {

                return new BigDecimal(
                        valor.toString()
                );

            } catch (NumberFormatException ignorado) {
                // No asignar un valor inventado.
            }
        }

        return null;
    }

    // ========================================================
    // 24. BUSQUEDA
    // ========================================================

    private void aplicarBusqueda() {

        if (tabla == null) {
            return;
        }

        tabla.buscar(
                campoBusqueda.getText()
        );

        actualizarResultados();
    }

    public void buscarMovimientos(
            String texto) {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    () -> buscarMovimientos(texto)
            );

            return;
        }

        campoBusqueda.setText(
                texto == null ? "" : texto
        );
    }

    private void actualizarResultados() {

        int visibles =
                tabla.getRowCount();

        int total =
                modelo.getRowCount();

        etiquetaVisibles.setText(
                Integer.toString(visibles)
        );

        etiquetaResultados.setText(
                "Mostrando "
                        + visibles
                        + " de "
                        + total
                        + " registros"
        );
    }

    // ========================================================
    // 25. DETALLE SELECCIONADO
    // ========================================================

    public void mostrarDetalleSeleccionado() {

        int filaVista =
                tabla.getSelectedRow();

        if (filaVista < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un movimiento primero.",
                    "Detalle de transaccion",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(
                        filaVista
                );

        if (filaModelo < 0
                || filaModelo >= movimientos.size()) {

            return;
        }

        Movimiento movimiento =
                movimientos.get(
                        filaModelo
                );

        JOptionPane.showMessageDialog(
                this,
                movimiento.detalle(),
                "MONEDERO / Movimiento",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========================================================
    // 26. EXPORTAR CSV
    // ========================================================

    /**
     * Exporta las filas visibles,
     * respetando los filtros aplicados.
     *
     * No altera las transacciones.
     */
    public void exportarCSV() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::exportarCSV
            );

            return;
        }

        if (tabla.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No hay movimientos visibles para exportar.",
                    "Exportar historial",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        if (!historialCompleto) {

            int continuar =
                    JOptionPane.showConfirmDialog(
                            this,
                            "La lectura del historial es parcial.\n"
                                    + "El archivo puede omitir movimientos.\n\n"
                                    + "¿Exportar los registros visibles?",
                            "Advertencia de integridad",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (continuar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        JFileChooser selector =
                new JFileChooser();

        selector.setDialogTitle(
                "Exportar historial financiero"
        );

        selector.setSelectedFile(
                new java.io.File(
                        "historial_monedero.csv"
                )
        );

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Archivo CSV (*.csv)",
                        "csv"
                )
        );

        if (selector.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {

            return;
        }

        Path destino =
                selector.getSelectedFile().toPath();

        if (!destino.toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".csv")) {

            destino = Path.of(
                    destino + ".csv"
            );
        }

        if (Files.exists(destino)) {

            int reemplazar =
                    JOptionPane.showConfirmDialog(
                            this,
                            "El archivo ya existe.\n"
                                    + "¿Deseas reemplazarlo?",
                            "Confirmar exportacion",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (reemplazar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {

            escribirCSV(
                    destino
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Archivo exportado correctamente.\n\n"
                            + destino.toAbsolutePath(),
                    "Exportacion completada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException error) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible exportar el archivo.\n\n"
                            + error.getMessage(),
                    "Error de exportacion",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================================
    // 27. ESCRIBIR CSV
    // ========================================================

    private void escribirCSV(
            Path destino) throws IOException {

        try (BufferedWriter escritor =
                     Files.newBufferedWriter(
                             destino,
                             StandardCharsets.UTF_8
                     )) {

            // BOM para compatibilidad con Excel.

            escritor.write('\uFEFF');

            for (int columna = 0;
                 columna < modelo.getColumnCount();
                 columna++) {

                if (columna > 0) {
                    escritor.write(';');
                }

                escritor.write(
                        escaparCSV(
                                modelo.getColumnName(columna)
                        )
                );
            }

            escritor.newLine();

            for (int filaVista = 0;
                 filaVista < tabla.getRowCount();
                 filaVista++) {

                for (int columna = 0;
                     columna < modelo.getColumnCount();
                     columna++) {

                    if (columna > 0) {
                        escritor.write(';');
                    }

                    Object valor =
                            tabla.getValueAt(
                                    filaVista,
                                    columna
                            );

                    escritor.write(
                            escaparCSV(
                                    valor == null
                                            ? ""
                                            : valor.toString()
                            )
                    );
                }

                escritor.newLine();
            }
        }
    }

    // ========================================================
    // 28. ESCAPAR CSV
    // ========================================================

    private static String escaparCSV(
            String texto) {

        String seguro =
                texto == null ? "" : texto;

        // Prevenir interpretacion de
        // formulas por hojas de calculo.

        if (!seguro.isEmpty()) {

            char primero =
                    seguro.stripLeading().isEmpty()
                            ? ' '
                            : seguro.stripLeading().charAt(0);

            if (primero == '='
                    || primero == '+'
                    || primero == '-'
                    || primero == '@'
                    || primero == '\t') {

                seguro = "'" + seguro;
            }
        }

        return "\""
                + seguro.replace("\"", "\"\"")
                + "\"";
    }

    // ========================================================
    // 29. FORMATO MONETARIO
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

        return formato.format(monto);
    }

    // ========================================================
    // 30. ESTADO
    // ========================================================

    private void actualizarEstado(
            String mensaje,
            boolean advertencia) {

        etiquetaEstado.setText(
                mensaje
        );

        etiquetaEstado.setForeground(
                advertencia
                        ? Paleta.ADVERTENCIA
                        : Paleta.SAGE
        );
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

    public TablaPremium getTablaHistorial() {

        return tabla;
    }

    public boolean isHistorialCompleto() {

        return historialCompleto;
    }

    public int getCantidadMovimientosCargados() {

        return movimientos.size();
    }

    // ========================================================
    // 32. DESCRIPCION
    // ========================================================

    public String generarDescripcion() {

        return "PanelHistorial"
                + " | Tema: Institutional Noir"
                + " | Movimientos cargados: "
                + movimientos.size()
                + " | Visibles: "
                + tabla.getRowCount()
                + " | Lectura completa: "
                + historialCompleto;
    }
}
