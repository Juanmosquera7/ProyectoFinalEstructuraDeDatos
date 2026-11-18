
package vista;

import modelo.Cliente;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * ============================================================
 *                  MONEDERO VIRTUAL
 *                    PRIVATE LEDGER
 * ============================================================
 *
 * PANEL:
 * PanelPuntos.java
 *
 * ESTILO:
 * Institutional Noir
 *
 * RESPONSABILIDADES:
 *
 * 1. Consultar los puntos del sistema.
 * 2. Mostrar los clientes registrados.
 * 3. Consultar puntos individuales.
 * 4. Consultar rangos individuales.
 * 5. Buscar registros.
 * 6. Mostrar detalles de fidelizacion.
 * 7. Actualizar datos financieros.
 *
 * ARQUITECTURA:
 *
 * VentanaPrincipal
 *       |
 *       v
 * PanelPuntos
 *       |
 *       v
 * ContextoAplicacion
 *       |
 *       +-- ClienteService
 *       +-- PuntosService
 *       +-- RangosService
 *
 * IMPORTANTE:
 *
 * El panel es de solo lectura.
 *
 * No asigna puntos.
 * No modifica rangos.
 * No altera beneficios.
 *
 * Las operaciones financieras son
 * responsabilidad de los servicios.
 *
 * Java 21.
 */
public final class PanelPuntos extends JPanel {

    private static final long serialVersionUID = 1L;

    // ========================================================
    // 1. CONSTANTES
    // ========================================================

    private static final String TITULO =
            "Puntos y rangos.";

    private static final String SUBTITULO =
            "Progresion, fidelizacion y beneficios.";

    private static final String IDENTIFICADOR =
            "WORKSPACE / 005";

    private static final String NO_DISPONIBLE =
            "No disponible";

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

    private JPanel panelEstadisticas;

    private JPanel panelDirectorio;

    private JPanel panelDetalle;

    private JLabel etiquetaPuntosTotales;

    private JLabel etiquetaTotalClientes;

    private JLabel etiquetaResultados;

    private JLabel etiquetaEstado;

    private JLabel etiquetaDetalle;

    private JTextField campoBusqueda;

    private TablaPremium tablaClientes;

    private TablaPremium.ModeloNoEditable modeloTabla;

    // ========================================================
    // 4. ESTADO
    // ========================================================

    private boolean actualizando;

    private boolean datosIndividualesDisponibles;

    // ========================================================
    // 5. CONSTRUCTOR
    // ========================================================

    public PanelPuntos(
            ContextoAplicacion contexto) {

        super(new BorderLayout());

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "El contexto no puede ser null"
                );

        this.actualizando = false;

        this.datosIndividualesDisponibles = false;

        inicializar();

        refrescarDatos();
    }

    // ========================================================
    // 6. INICIALIZACION
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

        construirEstadisticas();

        construirDirectorio();

        construirDetalle();

        agregarBloque(encabezado);

        agregarBloque(panelEstadisticas);

        agregarBloque(panelDirectorio);

        agregarBloque(panelDetalle);

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
    // 7. ORGANIZACION DE BLOQUES
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

        columna.add(bloque);
    }

    // ========================================================
    // 8. ENCABEZADO
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
                "Actualizar datos",
                this::refrescarDatos
        );
    }

    // ========================================================
    // 9. INDICADORES GENERALES
    // ========================================================

    private void construirEstadisticas() {

        panelEstadisticas =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                16,
                                0
                        )
                );

        panelEstadisticas.setOpaque(false);

        etiquetaPuntosTotales =
                crearValorEstadistico();

        etiquetaTotalClientes =
                crearValorEstadistico();

        panelEstadisticas.add(
                crearTarjeta(
                        "PUNTOS ACUMULADOS",
                        etiquetaPuntosTotales,
                        "Total registrado por PuntosService"
                )
        );

        panelEstadisticas.add(
                crearTarjeta(
                        "CLIENTES REGISTRADOS",
                        etiquetaTotalClientes,
                        "Titulares del sistema financiero"
                )
        );
    }

    // ========================================================
    // 10. TARJETAS ESTADISTICAS
    // ========================================================

    private JPanel crearTarjeta(
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
                                22, 24, 22, 24
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
                new JLabel(titulo);

        nombre.setFont(
                Tipografia.mono(10f)
        );

        nombre.setForeground(
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
                Box.createVerticalStrut(14)
        );

        tarjeta.add(valor);

        tarjeta.add(
                Box.createVerticalStrut(10)
        );

        tarjeta.add(detalle);

        return tarjeta;
    }

    private JLabel crearValorEstadistico() {

        JLabel valor =
                new JLabel("0");

        valor.setFont(
                Tipografia.editorial(42f)
        );

        valor.setForeground(
                Paleta.BONE
        );

        return valor;
    }

    // ========================================================
    // 11. DIRECTORIO DE FIDELIZACION
    // ========================================================

    private void construirDirectorio() {

        panelDirectorio =
                crearSuperficie();

        construirBarraBusqueda();

        construirTabla();

        construirPieDirectorio();
    }

    // ========================================================
    // 12. SUPERFICIE REUTILIZABLE
    // ========================================================

    private JPanel crearSuperficie() {

        JPanel superficie =
                new JPanel(
                        new BorderLayout(0, 18)
                );

        superficie.setOpaque(true);

        superficie.setBackground(
                Paleta.FONDO_TARJETA
        );

        superficie.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.BORDE_SUAVE
                        ),
                        BorderFactory.createEmptyBorder(
                                24, 24, 24, 24
                        )
                )
        );

        return superficie;
    }

    // ========================================================
    // 13. BARRA DE BUSQUEDA
    // ========================================================

    private void construirBarraBusqueda() {

        JPanel barra =
                new JPanel(
                        new BorderLayout(16, 0)
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
                        "Directorio de fidelizacion"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        JLabel descripcion =
                new JLabel(
                        "Clientes, puntos y clasificaciones"
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
                new Dimension(290, 42)
        );

        campoBusqueda.setToolTipText(
                "Buscar por cliente, puntos o rango"
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
    // 14. TABLA DE PUNTOS
    // ========================================================

    private void construirTabla() {

        modeloTabla =
                new TablaPremium.ModeloNoEditable(
                        new String[]{
                                "ID CLIENTE",
                                "CLIENTE",
                                "PUNTOS",
                                "RANGO"
                        },
                        0
                );

        tablaClientes =
                new TablaPremium(
                        modeloTabla
                );

        tablaClientes.setMensajeVacio(
                "No hay clientes para mostrar"
        );

        tablaClientes.setFilasAlternas(true);

        tablaClientes.setMostrarSeparadores(true);

        tablaClientes.configurarAnchoColumna(
                0, 135
        );

        tablaClientes.configurarAnchoColumna(
                1, 230
        );

        tablaClientes.configurarAnchoColumna(
                2, 160
        );

        tablaClientes.configurarAnchoColumna(
                3, 180
        );

        tablaClientes.setPreferredScrollableViewportSize(
                new Dimension(800, 340)
        );

        JScrollPane scroll =
                new JScrollPane(
                        tablaClientes
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.getViewport().setBackground(
                Paleta.FONDO_TARJETA
        );

        scroll.setPreferredSize(
                new Dimension(800, 365)
        );

        panelDirectorio.add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 15. PIE DEL DIRECTORIO
    // ========================================================

    private void construirPieDirectorio() {

        JPanel pie =
                new JPanel(
                        new BorderLayout(12, 12)
                );

        pie.setOpaque(false);

        etiquetaResultados =
                new JLabel(
                        "0 registros"
                );

        etiquetaResultados.setFont(
                Tipografia.cuerpoPequeno()
        );

        etiquetaResultados.setForeground(
                Paleta.ASH
        );

        BotonPremium verDetalle =
                BotonPremium.secundario(
                        "Ver detalle"
                );

        verDetalle.addActionListener(
                evento -> mostrarDetalleSeleccionado()
        );

        JPanel superior =
                new JPanel(
                        new BorderLayout()
                );

        superior.setOpaque(false);

        superior.add(
                etiquetaResultados,
                BorderLayout.WEST
        );

        superior.add(
                verDetalle,
                BorderLayout.EAST
        );

        etiquetaEstado =
                new JLabel(
                        "Modulo preparado"
                );

        etiquetaEstado.setFont(
                Tipografia.mono(10f)
        );

        etiquetaEstado.setForeground(
                Paleta.ASH
        );

        pie.add(
                superior,
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
    // 16. DETALLE DE FIDELIZACION
    // ========================================================

    private void construirDetalle() {

        panelDetalle =
                crearSuperficie();

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
                        "Estado del programa"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        etiquetaDetalle =
                new JLabel(
                        "Selecciona un cliente para consultar su progresion."
                );

        etiquetaDetalle.setFont(
                Tipografia.cuerpoPequeno()
        );

        etiquetaDetalle.setForeground(
                Paleta.ASH
        );

        titulo.setAlignmentX(
                LEFT_ALIGNMENT
        );

        etiquetaDetalle.setAlignmentX(
                LEFT_ALIGNMENT
        );

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(12)
        );

        textos.add(etiquetaDetalle);

        panelDetalle.add(
                textos,
                BorderLayout.CENTER
        );

        tablaClientes.getSelectionModel()
                .addListSelectionListener(evento -> {

                    if (!evento.getValueIsAdjusting()) {
                        actualizarDetalle();
                    }
                });
    }

    // ========================================================
    // 17. ACTUALIZAR DATOS
    // ========================================================

    /**
     * Consulta las estadisticas reales
     * del sistema y sus clientes.
     *
     * No modifica puntos ni rangos.
     */

    // ========================================================
    // REFRESCAR PUNTOS Y RANGOS - INTEGRACION REAL
    // ========================================================

    /**
     * Consulta los datos reales del sistema.
     *
     * ESTRUCTURAS UTILIZADAS:
     *
     * TablaHash<String, Cliente>
     * ListaEnlazada<String>
     *
     * El recorrido de clientes se realiza
     * mediante ClienteService.
     *
     * Los puntos y rangos se obtienen
     * directamente del modelo Cliente.
     *
     * No utiliza reflexion.
     * No modifica puntos.
     * No modifica rangos.
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

            // ======================================
            // 1. ESTADISTICAS GENERALES
            // ======================================

            long puntosTotales =
                    contexto.getPuntosService()
                            .totalPuntosSistema();

            int cantidadClientes =
                    contexto.getClienteService()
                            .cantidadClientes();

            etiquetaPuntosTotales.setText(
                    formatearEntero(puntosTotales)
            );

            etiquetaTotalClientes.setText(
                    formatearEntero(cantidadClientes)
            );

            // ======================================
            // 2. CONSULTAR CLIENTES REALES
            // ======================================

            List<Cliente> clientes =
                    new ArrayList<>();

            contexto.getClienteService()
                    .recorrerClientes(
                            clientes::add
                    );

            // Ordenar por identificador para
            // facilitar la visualizacion.

            clientes.sort(
                    Comparator.comparing(
                            Cliente::getId,
                            String.CASE_INSENSITIVE_ORDER
                    )
            );

            // ======================================
            // 3. RECONSTRUIR TABLA
            // ======================================

            modeloTabla.setRowCount(0);

            long sumaPuntosClientes = 0;

            for (Cliente cliente : clientes) {

                // ----------------------------------
                // IDENTIFICACION
                // ----------------------------------

                String idCliente =
                        cliente.getId();

                String nombreCliente =
                        cliente.getNombre();

                // ----------------------------------
                // PUNTOS REALES
                // ----------------------------------

                int puntos =
                        cliente.getPuntos();

                // ----------------------------------
                // RANGO REAL
                // ----------------------------------

                String rango =
                        cliente.getRango() == null
                                ? "NO DISPONIBLE"
                                : cliente.getRango().toString();

                // ----------------------------------
                // SUMATORIA DE CONTROL
                // ----------------------------------

                sumaPuntosClientes += puntos;

                // ----------------------------------
                // AGREGAR FILA
                // ----------------------------------

                modeloTabla.addRow(
                        new Object[]{
                                idCliente,
                                nombreCliente,
                                formatearEntero(puntos),
                                rango
                        }
                );
            }

            // ======================================
            // 4. ACTUALIZAR BUSQUEDA
            // ======================================

            aplicarBusqueda();

            // ======================================
            // 5. VERIFICACION DE CONSISTENCIA
            // ======================================

            datosIndividualesDisponibles =
                    clientes.size() == cantidadClientes
                            && sumaPuntosClientes == puntosTotales;

            if (datosIndividualesDisponibles) {

                actualizarEstado(
                        "PUNTOS Y RANGOS SINCRONIZADOS",
                        false
                );

            } else {

                actualizarEstado(
                        "REVISAR CONSISTENCIA DE PUNTOS",
                        true
                );

                System.err.println(
                        "[PUNTOS] Diferencia detectada."
                );

                System.err.println(
                        "[PUNTOS] Total del servicio: "
                                + puntosTotales
                );

                System.err.println(
                        "[PUNTOS] Suma de clientes: "
                                + sumaPuntosClientes
                );
            }

        } catch (RuntimeException error) {

            datosIndividualesDisponibles = false;

            actualizarEstado(
                    "ERROR AL CONSULTAR PUNTOS",
                    true
            );

            System.err.println(
                    "[PUNTOS] Error: "
                            + error.getMessage()
            );

        } finally {

            actualizando = false;
        }
    }


    // ========================================================
    // 18. CONSULTAR PUNTOS INDIVIDUALES
    // ========================================================

    /**
     * Adaptador de lectura.
     *
     * Busca una API publica compatible
     * con Cliente o con el ID del cliente.
     *
     * Si no existe, devuelve null.
     *
     * Nunca inventa valores individuales.
     */
    private Object obtenerPuntosCliente(
            Cliente cliente) {

        Object servicio =
                contexto.getPuntosService();

        String[] metodos = {
                "consultarPuntos",
                "consultarPuntosCliente",
                "obtenerPuntos",
                "obtenerPuntosCliente",
                "getPuntos",
                "getPuntosCliente",
                "puntosCliente",
                "totalPuntosCliente"
        };

        Object resultado =
                invocarConsulta(
                        servicio,
                        cliente,
                        metodos
                );

        if (resultado == null) {

            resultado =
                    invocarConsulta(
                            cliente,
                            cliente,
                            "getPuntos",
                            "getPuntosAcumulados",
                            "getSaldoPuntos"
                    );
        }

        resultado = desempaquetar(
                resultado
        );

        if (resultado instanceof Number numero) {

            return numero;
        }

        if (resultado instanceof String texto) {

            return texto.isBlank()
                    ? null
                    : texto;
        }

        return null;
    }

    // ========================================================
    // 19. CONSULTAR RANGO INDIVIDUAL
    // ========================================================

    private Object obtenerRangoCliente(
            Cliente cliente) {

        Object servicio =
                contexto.getRangosService();

        String[] metodos = {
                "consultarRango",
                "consultarRangoCliente",
                "obtenerRango",
                "obtenerRangoCliente",
                "getRango",
                "getRangoCliente",
                "clasificarCliente",
                "calcularRango"
        };

        Object resultado =
                invocarConsulta(
                        servicio,
                        cliente,
                        metodos
                );

        if (resultado == null) {

            resultado =
                    invocarConsulta(
                            cliente,
                            cliente,
                            "getRango",
                            "getRangoActual",
                            "getClasificacion"
                    );
        }

        resultado = desempaquetar(
                resultado
        );

        if (resultado == null) {
            return null;
        }

        if (resultado instanceof Enum<?>
                || resultado instanceof String
                || resultado instanceof Number) {

            return resultado;
        }

        Object nombre =
                invocarSinArgumentos(
                        resultado,
                        "getNombre"
                );

        if (nombre != null) {
            return nombre;
        }

        return resultado.getClass().getSimpleName();
    }

    // ========================================================
    // 20. CONSULTAS DE SOLO LECTURA
    // ========================================================

    /**
     * Intenta llamar a metodos publicos
     * con parametros conocidos.
     *
     * Las opciones compatibles son:
     *
     * metodo()
     * metodo(String idCliente)
     * metodo(Cliente cliente)
     *
     * Se omiten las firmas desconocidas.
     */
    private static Object invocarConsulta(
            Object servicio,
            Cliente cliente,
            String... nombres) {

        if (servicio == null
                || cliente == null) {
            return null;
        }

        for (String nombre : nombres) {

            for (Method metodo :
                    servicio.getClass().getMethods()) {

                if (!metodo.getName().equals(nombre)) {
                    continue;
                }

                try {

                    Class<?>[] parametros =
                            metodo.getParameterTypes();

                    Object resultado;

                    if (parametros.length == 0) {

                        resultado =
                                metodo.invoke(
                                        servicio
                                );

                    } else if (parametros.length == 1
                            && parametros[0] == String.class) {

                        resultado =
                                metodo.invoke(
                                        servicio,
                                        cliente.getId()
                                );

                    } else if (parametros.length == 1
                            && parametros[0]
                            .isAssignableFrom(
                                    Cliente.class
                            )) {

                        resultado =
                                metodo.invoke(
                                        servicio,
                                        cliente
                                );

                    } else {
                        continue;
                    }

                    if (resultado != null) {
                        return resultado;
                    }

                } catch (ReflectiveOperationException
                         | SecurityException error) {

                    // Probar siguiente firma.
                }
            }
        }

        return null;
    }

    // ========================================================
    // 21. UTILIDADES REFLEXIVAS
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

            return metodo.invoke(
                    objeto
            );

        } catch (ReflectiveOperationException
                 | SecurityException error) {

            return null;
        }
    }

    private static Object desempaquetar(
            Object valor) {

        if (valor instanceof Optional<?> optional) {

            return optional.orElse(null);
        }

        return valor;
    }

    private static String obtenerNombre(
            Cliente cliente) {

        Object nombre =
                invocarSinArgumentos(
                        cliente,
                        "getNombre"
                );

        if (nombre == null) {

            nombre =
                    invocarSinArgumentos(
                            cliente,
                            "getNombreCompleto"
                    );
        }

        return nombre == null
                ? cliente.getId()
                : nombre.toString();
    }

    // ========================================================
    // 22. BUSQUEDA
    // ========================================================

    private void aplicarBusqueda() {

        if (tablaClientes == null) {
            return;
        }

        tablaClientes.buscar(
                campoBusqueda.getText()
        );

        actualizarResultados();

        actualizarDetalle();
    }

    public void buscarClientes(
            String texto) {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    () -> buscarClientes(texto)
            );

            return;
        }

        campoBusqueda.setText(
                texto == null ? "" : texto
        );
    }

    // ========================================================
    // 23. RESULTADOS
    // ========================================================

    private void actualizarResultados() {

        etiquetaResultados.setText(
                "Mostrando "
                        + tablaClientes.getRowCount()
                        + " de "
                        + modeloTabla.getRowCount()
                        + " clientes"
        );
    }

    // ========================================================
    // 24. CLIENTE SELECCIONADO
    // ========================================================

    public String getClienteSeleccionadoId() {

        Object valor =
                tablaClientes.getValorSeleccionado(
                        0
                );

        return valor == null
                ? null
                : valor.toString();
    }

    // ========================================================
    // 25. DETALLE EN PANTALLA
    // ========================================================

    private void actualizarDetalle() {

        int fila =
                tablaClientes.getSelectedRow();

        if (fila < 0) {

            etiquetaDetalle.setText(
                    "Selecciona un cliente para consultar su progresion."
            );

            return;
        }

        int indiceModelo =
                tablaClientes.convertRowIndexToModel(
                        fila
                );

        String id =
                Objects.toString(
                        modeloTabla.getValueAt(
                                indiceModelo,
                                0
                        ),
                        ""
                );

        String puntos =
                Objects.toString(
                        modeloTabla.getValueAt(
                                indiceModelo,
                                2
                        ),
                        NO_DISPONIBLE
                );

        String rango =
                Objects.toString(
                        modeloTabla.getValueAt(
                                indiceModelo,
                                3
                        ),
                        NO_DISPONIBLE
                );

        etiquetaDetalle.setText(
                "Cliente: " + id
                        + "  |  Puntos: " + puntos
                        + "  |  Rango: " + rango
        );
    }

    // ========================================================
    // 26. MOSTRAR DETALLE
    // ========================================================

    public void mostrarDetalleSeleccionado() {

        int fila =
                tablaClientes.getSelectedRow();

        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un cliente primero.",
                    "Programa de fidelizacion",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int indiceModelo =
                tablaClientes.convertRowIndexToModel(
                        fila
                );

        String id =
                valorCelda(indiceModelo, 0);

        String nombre =
                valorCelda(indiceModelo, 1);

        String puntos =
                valorCelda(indiceModelo, 2);

        String rango =
                valorCelda(indiceModelo, 3);

        JOptionPane.showMessageDialog(
                this,
                "CLIENTE: " + nombre
                        + "\nID: " + id
                        + "\n\nPUNTOS: " + puntos
                        + "\nRANGO: " + rango
                        + "\n\nLos puntos y rangos "
                        + "son administrados por los servicios.",
                "MONEDERO / Fidelizacion",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String valorCelda(
            int fila,
            int columna) {

        return Objects.toString(
                modeloTabla.getValueAt(
                        fila,
                        columna
                ),
                ""
        );
    }

    // ========================================================
    // 27. FORMATO NUMERICO
    // ========================================================

    private static String formatearEntero(
            long valor) {

        return java.text.NumberFormat
                .getIntegerInstance(
                        Locale.forLanguageTag("es-CO")
                )
                .format(valor);
    }

    // ========================================================
    // 28. ESTADO DEL MODULO
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
    // 29. GETTERS
    // ========================================================

    public ContextoAplicacion getContexto() {

        return contexto;
    }

    public EncabezadoPanel getEncabezado() {

        return encabezado;
    }

    public TablaPremium getTablaClientes() {

        return tablaClientes;
    }

    public boolean isDatosIndividualesDisponibles() {

        return datosIndividualesDisponibles;
    }

    // ========================================================
    // 30. DESCRIPCION
    // ========================================================

    public String generarDescripcion() {

        return "PanelPuntos"
                + " | Tema: Institutional Noir"
                + " | Puntos del sistema: "
                + etiquetaPuntosTotales.getText()
                + " | Clientes: "
                + etiquetaTotalClientes.getText()
                + " | Filas visibles: "
                + tablaClientes.getRowCount()
                + " | Detalle completo: "
                + datosIndividualesDisponibles;
    }
}
