
package vista;

import modelo.Cliente;
import modelo.Monedero;

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
import javax.swing.JComboBox;
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * ============================================================
 *                 MONEDERO VIRTUAL
 *                   PRIVATE LEDGER
 * ============================================================
 *
 * CLASE:
 * PanelMonederos.java
 *
 * ESTILO:
 * Institutional Noir
 *
 * RESPONSABILIDADES:
 *
 * 1. Mostrar monederos registrados.
 * 2. Consultar saldos financieros.
 * 3. Crear monederos principales.
 * 4. Buscar registros.
 * 5. Mostrar estadisticas.
 * 6. Seleccionar monederos.
 * 7. Refrescar informacion.
 * 8. Notificar cambios al dashboard.
 *
 * ARQUITECTURA:
 *
 * VentanaPrincipal
 *       |
 *       v
 * PanelMonederos
 *       |
 *       v
 * ContextoAplicacion
 *       |
 *       +-- ClienteService
 *       |
 *       +-- MonederoService
 *
 * No modifica saldos directamente.
 *
 * Todas las operaciones financieras
 * pasan por los servicios existentes.
 *
 * Java 21.
 */
public final class PanelMonederos extends JPanel {

    private static final long serialVersionUID = 1L;

    // ========================================================
    // 1. CONSTANTES
    // ========================================================

    private static final String TITULO =
            "Monederos.";

    private static final String SUBTITULO =
            "Administracion del capital disponible.";

    private static final String IDENTIFICADOR =
            "WORKSPACE / 003";

    private static final int MAX_ID = 30;

    // ========================================================
    // 2. CONTEXTO
    // ========================================================

    private final ContextoAplicacion contexto;

    // ========================================================
    // 3. COMPONENTES
    // ========================================================

    private EncabezadoPanel encabezado;

    private TarjetaEstadistica tarjetaSaldo;

    private TarjetaEstadistica tarjetaMonederos;

    private TarjetaEstadistica tarjetaClientes;

    private JPanel contenido;

    private JPanel columna;

    private JPanel panelEstadisticas;

    private JPanel panelDirectorio;

    private TablaPremium tablaMonederos;

    private TablaPremium.ModeloNoEditable modeloTabla;

    private JTextField campoBusqueda;

    private JLabel etiquetaResultados;

    private JLabel etiquetaEstado;

    // ========================================================
    // 4. ESTADO
    // ========================================================

    private Runnable listenerActualizacion;

    private boolean actualizando;

    private boolean inventarioDisponible;

    // ========================================================
    // 5. CONSTRUCTOR
    // ========================================================

    public PanelMonederos(
            ContextoAplicacion contexto) {

        super(new BorderLayout());

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "El contexto no puede ser null"
                );

        listenerActualizacion = null;

        actualizando = false;

        inventarioDisponible = true;

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

        construirContenido();

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
    // 7. ESTRUCTURA PRINCIPAL
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

        agregarBloque(
                encabezado
        );

        agregarBloque(
                panelEstadisticas
        );

        agregarBloque(
                panelDirectorio
        );

        contenido.add(
                columna,
                BorderLayout.NORTH
        );
    }

    // ========================================================
    // 8. AGREGAR BLOQUES
    // ========================================================

    private void agregarBloque(
            JPanel panel) {

        panel.setAlignmentX(
                LEFT_ALIGNMENT
        );

        if (columna.getComponentCount() > 0) {

            columna.add(
                    Box.createVerticalStrut(
                            Espaciado.DASHBOARD_SEPARACION
                    )
            );
        }

        columna.add(panel);
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
                "Nuevo monedero",
                this::abrirFormularioCreacion
        );

        encabezado.crearAccionSecundaria(
                "Actualizar",
                this::refrescarDatos
        );
    }

    // ========================================================
    // 10. ESTADISTICAS
    // ========================================================

    private void construirEstadisticas() {

        panelEstadisticas =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                Espaciado.LG,
                                0
                        )
                );

        panelEstadisticas.setOpaque(false);

        tarjetaSaldo =
                TarjetaEstadistica.destacada(
                        "CAPITAL CONSOLIDADO",
                        "$ 0,00",
                        "Saldo total del sistema"
                );

        tarjetaSaldo.setMostrarLineaAcento(
                true
        );

        tarjetaMonederos =
                TarjetaEstadistica.normal(
                        "MONEDEROS",
                        "0",
                        "Cuentas financieras registradas"
                );

        tarjetaClientes =
                TarjetaEstadistica.normal(
                        "CLIENTES",
                        "0",
                        "Titulares registrados"
                );

        panelEstadisticas.add(
                tarjetaSaldo
        );

        panelEstadisticas.add(
                tarjetaMonederos
        );

        panelEstadisticas.add(
                tarjetaClientes
        );
    }

    // ========================================================
    // 11. DIRECTORIO
    // ========================================================

    private void construirDirectorio() {

        panelDirectorio =
                new JPanel(
                        new BorderLayout(
                                0,
                                Espaciado.LG
                        )
                );

        panelDirectorio.setOpaque(true);

        panelDirectorio.setBackground(
                Paleta.FONDO_TARJETA
        );

        panelDirectorio.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.BORDE_SUAVE,
                                1
                        ),
                        Espaciado.bordeTarjeta()
                )
        );

        construirBuscador();

        construirTabla();

        construirPie();
    }

    // ========================================================
    // 12. BUSCADOR
    // ========================================================

    private void construirBuscador() {

        JPanel superior =
                new JPanel(
                        new BorderLayout(
                                Espaciado.LG,
                                0
                        )
                );

        superior.setOpaque(false);

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
                        "Directorio de monederos"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        JLabel descripcion =
                new JLabel(
                        "Cuentas y capital administrado"
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
                Tipografia.textoFormulario()
        );

        campoBusqueda.setForeground(
                Paleta.INPUT_TEXTO
        );

        campoBusqueda.setBackground(
                Paleta.INPUT_FONDO
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
                        250,
                        Espaciado.INPUT_ALTURA
                )
        );

        campoBusqueda.setToolTipText(
                "Buscar por monedero, cliente o tipo"
        );

        campoBusqueda.getAccessibleContext()
                .setAccessibleName(
                        "Buscar monederos"
                );

        campoBusqueda.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {

                                buscar();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {

                                buscar();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {

                                buscar();
                            }
                        }
                );

        superior.add(
                textos,
                BorderLayout.CENTER
        );

        superior.add(
                campoBusqueda,
                BorderLayout.EAST
        );

        panelDirectorio.add(
                superior,
                BorderLayout.NORTH
        );
    }

    // ========================================================
    // 13. TABLA
    // ========================================================

    private void construirTabla() {

        modeloTabla =
                new TablaPremium.ModeloNoEditable(
                        new String[]{
                                "ID MONEDERO",
                                "ID CLIENTE",
                                "TITULAR",
                                "TIPO",
                                "SALDO / COP"
                        },
                        0
                );

        tablaMonederos =
                new TablaPremium(
                        modeloTabla
                );

        tablaMonederos.setMensajeVacio(
                "No hay monederos para mostrar"
        );

        tablaMonederos.setFilasAlternas(
                true
        );

        tablaMonederos.configurarColumnaMonetaria(
                4
        );

        tablaMonederos.configurarAnchoColumna(
                0, 120
        );

        tablaMonederos.configurarAnchoColumna(
                1, 110
        );

        tablaMonederos.configurarAnchoColumna(
                2, 180
        );

        tablaMonederos.configurarAnchoColumna(
                3, 140
        );

        tablaMonederos.configurarAnchoColumna(
                4, 180
        );

        tablaMonederos.setPreferredScrollableViewportSize(
                new Dimension(
                        800,
                        310
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        tablaMonederos
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.getViewport().setBackground(
                Paleta.FONDO_TARJETA
        );

        scroll.setPreferredSize(
                new Dimension(
                        800,
                        360
                )
        );

        panelDirectorio.add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 14. PIE DEL DIRECTORIO
    // ========================================================

    private void construirPie() {

        JPanel pie =
                new JPanel(
                        new BorderLayout()
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
                        "Consultar saldo"
                );

        consultar.usarTamanoPequeno();

        consultar.addActionListener(
                evento -> consultarSaldoSeleccionado()
        );

        pie.add(
                etiquetaResultados,
                BorderLayout.WEST
        );

        pie.add(
                consultar,
                BorderLayout.EAST
        );

        etiquetaEstado =
                new JLabel(
                        "Directorio preparado"
                );

        etiquetaEstado.setFont(
                Tipografia.caption()
        );

        etiquetaEstado.setForeground(
                Paleta.ASH
        );

        JPanel pieCompleto =
                new JPanel(
                        new BorderLayout(
                                0,
                                Espaciado.MD
                        )
                );

        pieCompleto.setOpaque(false);

        pieCompleto.add(
                pie,
                BorderLayout.NORTH
        );

        pieCompleto.add(
                etiquetaEstado,
                BorderLayout.SOUTH
        );

        panelDirectorio.add(
                pieCompleto,
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 15. REFRESCAR DATOS REALES
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

            int totalMonederos =
                    contexto.getClienteService()
                            .cantidadMonederos();

            int totalClientes =
                    contexto.getClienteService()
                            .cantidadClientes();

            BigDecimal saldoTotal =
                    contexto.getClienteService()
                            .saldoTotalSistema();

            tarjetaMonederos.setValorNumerico(
                    totalMonederos
            );

            tarjetaClientes.setValorNumerico(
                    totalClientes
            );

            tarjetaSaldo.setValorMonetario(
                    saldoTotal
            );

            // ----------------------------------
            // LEER CUENTAS REALES
            // ----------------------------------

            Map<String, RegistroMonedero> registros =
                    new LinkedHashMap<>();

            List<Cliente> clientes =
                    new ArrayList<>();

            contexto.getClienteService()
                    .recorrerClientes(
                            clientes::add
                    );

            for (Cliente cliente : clientes) {

                cargarMonederosCliente(
                        cliente,
                        registros
                );
            }

            List<RegistroMonedero> ordenados =
                    new ArrayList<>(
                            registros.values()
                    );

            ordenados.sort(
                    Comparator.comparing(
                            RegistroMonedero::idCliente
                    ).thenComparing(
                            RegistroMonedero::idMonedero
                    )
            );

            modeloTabla.setRowCount(0);

            for (RegistroMonedero registro : ordenados) {

                modeloTabla.addRow(
                        new Object[]{
                                registro.idMonedero(),
                                registro.idCliente(),
                                registro.nombreCliente(),
                                registro.tipo(),
                                registro.saldo()
                        }
                );
            }

            tablaMonederos.buscar(
                    campoBusqueda.getText()
            );

            actualizarResultados();

            inventarioDisponible =
                    registros.size() == totalMonederos;

            if (inventarioDisponible) {

                etiquetaEstado.setText(
                        "Datos financieros actualizados"
                );

                etiquetaEstado.setForeground(
                        Paleta.SAGE
                );

            } else {

                etiquetaEstado.setText(
                        "Mostrando "
                                + registros.size()
                                + " de "
                                + totalMonederos
                                + " monederos. Revisar adaptador."
                );

                etiquetaEstado.setForeground(
                        Paleta.ADVERTENCIA
                );
            }

        } catch (RuntimeException error) {

            etiquetaEstado.setText(
                    "Error al consultar monederos"
            );

            etiquetaEstado.setForeground(
                    Paleta.ERROR
            );

            System.err.println(
                    "[MONEDEROS] "
                            + error.getMessage()
            );

        } finally {

            actualizando = false;
        }
    }

    // ========================================================
    // 16. REGISTRO INTERNO DE LA VISTA
    // ========================================================

    /**
     * Representacion de lectura.
     *
     * No reemplaza el modelo Monedero.
     *
     * Solo almacena datos para JTable.
     */
    private record RegistroMonedero(
            String idMonedero,
            String idCliente,
            String nombreCliente,
            String tipo,
            BigDecimal saldo) {
    }

    // ========================================================
    // 17. CARGAR MONEDEROS DE UN CLIENTE
    // ========================================================

    /**
     * Adaptador de lectura.
     *
     * La estructura concreta de la lista
     * de monederos aun no esta confirmada.
     *
     * Se admiten colecciones Java,
     * estructuras con recorrer(Consumer)
     * y nodos enlazados.
     *
     * No modifica los objetos del motor.
     */

    // ========================================================
    // CARGAR MONEDEROS DESDE LISTA ENLAZADA
    // ========================================================

    /**
     * Consulta los monederos pertenecientes
     * a un cliente registrado.
     *
     * ESTRUCTURAS UTILIZADAS:
     *
     * TablaHash<String, Cliente>
     * ListaEnlazada<Monedero>
     *
     * No utiliza reflexion para recorrer
     * los monederos.
     *
     * No modifica las cuentas financieras.
     */

    // ========================================================
    // CARGAR MONEDEROS - INTEGRACION DEFINITIVA
    // ========================================================

    /**
     * Consulta los monederos reales del cliente.
     *
     * ESTRUCTURAS UTILIZADAS:
     *
     * TablaHash<String, Cliente>
     * ListaEnlazada<Monedero>
     *
     * No utiliza reflexion.
     * No modifica saldos.
     * No altera los monederos.
     *
     * Los registros se obtienen directamente
     * del modelo y de ClienteService.
     */
    private void cargarMonederosCliente(
            Cliente cliente,
            Map<String, RegistroMonedero> registros) {

        Objects.requireNonNull(
                cliente,
                "El cliente no puede ser null"
        );

        String idCliente =
                cliente.getId();

        String nombreCliente =
                cliente.getNombre();

        // ----------------------------------
        // RECORRER LISTA ENLAZADA REAL
        // ----------------------------------

        contexto.getClienteService()
                .listarMonederos(idCliente)
                .recorrer(monedero -> {

                    if (monedero == null) {
                        return;
                    }

                    // Identificador real.
                    String idMonedero =
                            monedero.getId();

                    // Nombre y tipo reales.
                    String nombreMonedero =
                            monedero.getNombre();

                    String tipo =
                            monedero.getTipo();

                    // Saldo consultado mediante
                    // el servicio financiero.
                    BigDecimal saldo =
                            contexto.getClienteService()
                                    .consultarSaldoMonedero(
                                            idCliente,
                                            idMonedero
                                    );

                    // Identificador único
                    // dentro del directorio.
                    String clave =
                            idCliente + ":" + idMonedero;

                    // Registrar para JTable.
                    registros.putIfAbsent(
                            clave,
                            new RegistroMonedero(
                                    idMonedero,
                                    idCliente,
                                    nombreCliente,
                                    tipo,
                                    saldo
                            )
                    );
                });
    }



    // ========================================================
    // 18. RECORRER ESTRUCTURAS
    // ========================================================

    private void recorrerEstructura(
            Object objeto,
            String idCliente,
            String nombreCliente,
            Map<String, RegistroMonedero> registros,
            Set<Object> visitados,
            int profundidad) {

        if (objeto == null
                || profundidad > 10000
                || !visitados.add(objeto)) {

            return;
        }

        // ----------------------------------
        // MONEDERO DIRECTO
        // ----------------------------------

        if (objeto instanceof Monedero monedero) {

            registrarMonedero(
                    monedero,
                    idCliente,
                    nombreCliente,
                    registros
            );

            return;
        }

        // ----------------------------------
        // MAP
        // ----------------------------------

        if (objeto instanceof Map<?, ?> mapa) {

            for (Object valor : mapa.values()) {

                recorrerEstructura(
                        valor,
                        idCliente,
                        nombreCliente,
                        registros,
                        visitados,
                        profundidad + 1
                );
            }

            return;
        }

        // ----------------------------------
        // ITERABLE
        // ----------------------------------

        if (objeto instanceof Iterable<?> iterable) {

            for (Object valor : iterable) {

                recorrerEstructura(
                        valor,
                        idCliente,
                        nombreCliente,
                        registros,
                        visitados,
                        profundidad + 1
                );
            }

            return;
        }

        // ----------------------------------
        // ARREGLOS
        // ----------------------------------

        if (objeto.getClass().isArray()) {

            int longitud =
                    Array.getLength(objeto);

            for (int i = 0; i < longitud; i++) {

                recorrerEstructura(
                        Array.get(objeto, i),
                        idCliente,
                        nombreCliente,
                        registros,
                        visitados,
                        profundidad + 1
                );
            }

            return;
        }

        // ----------------------------------
        // RECORRER(CONSUMER)
        // ----------------------------------

        try {

            Method recorrer =
                    objeto.getClass().getMethod(
                            "recorrer",
                            Consumer.class
                    );

            Consumer<Object> consumidor =
                    valor -> recorrerEstructura(
                            valor,
                            idCliente,
                            nombreCliente,
                            registros,
                            visitados,
                            profundidad + 1
                    );

            recorrer.invoke(
                    objeto,
                    consumidor
            );

            return;

        } catch (ReflectiveOperationException
                 | SecurityException ignorado) {

            // Continuamos con nodos enlazados.
        }

        // ----------------------------------
        // NODOS ENLAZADOS
        // ----------------------------------

        String nombreClase =
                objeto.getClass()
                        .getSimpleName()
                        .toLowerCase(
                                java.util.Locale.ROOT
                        );

        if (nombreClase.contains("nodo")) {

            Object dato =
                    invocarPrimero(
                            objeto,
                            "getDato",
                            "getValor",
                            "getElemento"
                    );

            Object siguiente =
                    invocarPrimero(
                            objeto,
                            "getSiguiente",
                            "getNext"
                    );

            recorrerEstructura(
                    dato,
                    idCliente,
                    nombreCliente,
                    registros,
                    visitados,
                    profundidad + 1
            );

            recorrerEstructura(
                    siguiente,
                    idCliente,
                    nombreCliente,
                    registros,
                    visitados,
                    profundidad + 1
            );

            return;
        }

        // ----------------------------------
        // CABEZA DE LISTA
        // ----------------------------------

        if (nombreClase.contains("lista")
                || nombreClase.contains("cola")) {

            Object cabeza =
                    invocarPrimero(
                            objeto,
                            "getCabeza",
                            "getPrimero",
                            "getInicio",
                            "getFrente"
                    );

            recorrerEstructura(
                    cabeza,
                    idCliente,
                    nombreCliente,
                    registros,
                    visitados,
                    profundidad + 1
            );
        }
    }

    // ========================================================
    // 19. REGISTRAR MONEDERO EN LA VISTA
    // ========================================================

    private void registrarMonedero(
            Monedero monedero,
            String idCliente,
            String nombreCliente,
            Map<String, RegistroMonedero> registros) {

        String idMonedero =
                leerTexto(
                        monedero,
                        "getId",
                        "getIdMonedero",
                        "getCodigo"
                );

        if (idMonedero.isBlank()) {
            return;
        }

        String tipo =
                leerTexto(
                        monedero,
                        "getTipo",
                        "getTipoMonedero"
                );

        if (tipo.isBlank()) {

            tipo = "PRINCIPAL";
        }

        BigDecimal saldo =
                contexto.getMonederoService()
                        .consultarSaldo(
                                idCliente,
                                idMonedero
                        );

        String clave =
                idCliente + ":" + idMonedero;

        registros.putIfAbsent(
                clave,
                new RegistroMonedero(
                        idMonedero,
                        idCliente,
                        nombreCliente,
                        tipo,
                        saldo
                )
        );
    }

    // ========================================================
    // 20. UTILIDADES DE LECTURA
    // ========================================================

    private static Object invocarSinArgumentos(
            Object objeto,
            String metodo) {

        if (objeto == null) {
            return null;
        }

        try {

            Method encontrado =
                    objeto.getClass().getMethod(
                            metodo
                    );

            return encontrado.invoke(
                    objeto
            );

        } catch (ReflectiveOperationException
                 | SecurityException error) {

            return null;
        }
    }

    private static Object invocarPrimero(
            Object objeto,
            String... nombres) {

        for (String nombre : nombres) {

            Object resultado =
                    invocarSinArgumentos(
                            objeto,
                            nombre
                    );

            if (resultado != null) {

                return resultado;
            }
        }

        return null;
    }

    private static String leerTexto(
            Object objeto,
            String... metodos) {

        Object valor =
                invocarPrimero(
                        objeto,
                        metodos
                );

        return valor == null
                ? ""
                : valor.toString();
    }

    // ========================================================
    // 21. BUSQUEDA
    // ========================================================

    private void buscar() {

        if (tablaMonederos == null) {
            return;
        }

        tablaMonederos.buscar(
                campoBusqueda.getText()
        );

        actualizarResultados();
    }

    public void buscarMonederos(
            String texto) {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    () -> buscarMonederos(texto)
            );

            return;
        }

        campoBusqueda.setText(
                texto == null ? "" : texto
        );
    }

    private void actualizarResultados() {

        etiquetaResultados.setText(
                "Mostrando "
                        + tablaMonederos.getRowCount()
                        + " de "
                        + modeloTabla.getRowCount()
                        + " registros"
        );
    }

    // ========================================================
    // 22. FORMULARIO DE CREACION
    // ========================================================

    public void abrirFormularioCreacion() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::abrirFormularioCreacion
            );

            return;
        }

        List<Cliente> clientes =
                new ArrayList<>();

        contexto.getClienteService()
                .recorrerClientes(
                        clientes::add
                );

        if (clientes.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes registrar un cliente.",
                    "Monedero / Registro",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        // ----------------------------------
        // SELECCIONAR TITULAR
        // ----------------------------------

        JComboBox<ClienteOpcion> selector =
                new JComboBox<>();

        for (Cliente cliente : clientes) {

            String nombre =
                    leerTexto(
                            cliente,
                            "getNombre",
                            "getNombreCompleto"
                    );

            selector.addItem(
                    new ClienteOpcion(
                            cliente.getId(),
                            nombre
                    )
            );
        }

        selector.setFont(
                Tipografia.textoFormulario()
        );

        // ----------------------------------
        // ID DEL MONEDERO
        // ----------------------------------

        JTextField campoId =
                new JTextField();

        campoId.setFont(
                Tipografia.textoFormulario()
        );

        campoId.setBackground(
                Paleta.INPUT_FONDO
        );

        campoId.setForeground(
                Paleta.INPUT_TEXTO
        );

        campoId.setCaretColor(
                Paleta.CHAMPAGNE
        );

        campoId.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.INPUT_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 12, 10, 12
                        )
                )
        );

        // ----------------------------------
        // CONSTRUIR FORMULARIO
        // ----------------------------------

        JPanel formulario =
                new JPanel(
                        new GridLayout(
                                4,
                                1,
                                0,
                                10
                        )
                );

        formulario.setBackground(
                Paleta.FONDO_SECUNDARIO
        );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 8, 12, 8
                )
        );

        JLabel etiquetaCliente =
                new JLabel(
                        "CLIENTE TITULAR"
                );

        etiquetaCliente.setFont(
                Tipografia.etiqueta()
        );

        etiquetaCliente.setForeground(
                Paleta.ASH
        );

        JLabel etiquetaId =
                new JLabel(
                        "ID DEL MONEDERO"
                );

        etiquetaId.setFont(
                Tipografia.etiqueta()
        );

        etiquetaId.setForeground(
                Paleta.ASH
        );

        formulario.add(
                etiquetaCliente
        );

        formulario.add(
                selector
        );

        formulario.add(
                etiquetaId
        );

        formulario.add(
                campoId
        );

        // ----------------------------------
        // CONFIRMACION
        // ----------------------------------

        while (true) {

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            formulario,
                            "MONEDERO / Crear monedero principal",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (respuesta != JOptionPane.OK_OPTION) {

                return;
            }

            ClienteOpcion opcion =
                    (ClienteOpcion)
                            selector.getSelectedItem();

            String idMonedero =
                    campoId.getText().trim();

            if (opcion == null) {
                return;
            }

            if (idMonedero.isBlank()) {

                mostrarAdvertencia(
                        "El ID del monedero es obligatorio."
                );

                continue;
            }

            if (idMonedero.length() > MAX_ID) {

                mostrarAdvertencia(
                        "El ID no puede superar "
                                + MAX_ID
                                + " caracteres."
                );

                continue;
            }

            if (!idMonedero.matches(
                    "[A-Za-z0-9_-]+"
            )) {

                mostrarAdvertencia(
                        "El ID contiene caracteres invalidos."
                );

                continue;
            }

            // ----------------------------------
            // CREAR EN EL MOTOR FINANCIERO
            // ----------------------------------

            try {

                contexto.getMonederoService()
                        .crearMonederoPrincipal(
                                opcion.id(),
                                idMonedero
                        );

                refrescarDatos();

                if (listenerActualizacion != null) {

                    listenerActualizacion.run();
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Monedero creado correctamente.\n\n"
                                + "Cliente: "
                                + opcion.id()
                                + "\n"
                                + "Monedero: "
                                + idMonedero,
                        "Registro exitoso",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;

            } catch (RuntimeException error) {

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible crear el monedero.\n\n"
                                + Objects.toString(
                                error.getMessage(),
                                "Error desconocido"
                        ),
                        "Error de registro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // ========================================================
    // 23. OPCION DE CLIENTE
    // ========================================================

    private record ClienteOpcion(
            String id,
            String nombre) {

        @Override
        public String toString() {

            if (nombre == null
                    || nombre.isBlank()) {

                return id;
            }

            return id + " - " + nombre;
        }
    }

    // ========================================================
    // 24. CONSULTAR SALDO SELECCIONADO
    // ========================================================

    public void consultarSaldoSeleccionado() {

        String idMonedero =
                getMonederoSeleccionadoId();

        String idCliente =
                getClienteSeleccionadoId();

        if (idMonedero == null
                || idCliente == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un monedero primero.",
                    "Consulta de saldo",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        try {

            BigDecimal saldo =
                    contexto.getMonederoService()
                            .consultarSaldo(
                                    idCliente,
                                    idMonedero
                            );

            JOptionPane.showMessageDialog(
                    this,
                    "CLIENTE: " + idCliente
                            + "\nMONEDERO: " + idMonedero
                            + "\n\nSALDO DISPONIBLE:\n"
                            + TablaPremium.formatearMoneda(
                            saldo
                    )
                            + " COP",
                    "MONEDERO / Saldo",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (RuntimeException error) {

            JOptionPane.showMessageDialog(
                    this,
                    Objects.toString(
                            error.getMessage(),
                            "No se pudo consultar el saldo"
                    ),
                    "Error financiero",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================================
    // 25. SELECCION
    // ========================================================

    public String getMonederoSeleccionadoId() {

        Object valor =
                tablaMonederos.getValorSeleccionado(
                        0
                );

        return valor == null
                ? null
                : valor.toString();
    }

    public String getClienteSeleccionadoId() {

        Object valor =
                tablaMonederos.getValorSeleccionado(
                        1
                );

        return valor == null
                ? null
                : valor.toString();
    }

    // ========================================================
    // 26. NOTIFICACION DE CAMBIOS
    // ========================================================

    public void setListenerActualizacion(
            Runnable listener) {

        listenerActualizacion = listener;
    }

    // ========================================================
    // 27. MENSAJES
    // ========================================================

    private void mostrarAdvertencia(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Validacion",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // ========================================================
    // 28. GETTERS
    // ========================================================

    public ContextoAplicacion getContexto() {

        return contexto;
    }

    public EncabezadoPanel getEncabezado() {

        return encabezado;
    }

    public TablaPremium getTablaMonederos() {

        return tablaMonederos;
    }

    public boolean isInventarioDisponible() {

        return inventarioDisponible;
    }

    // ========================================================
    // 29. INFORMACION
    // ========================================================

    public String generarDescripcion() {

        return "PanelMonederos"
                + " | Tema: Institutional Noir"
                + " | Saldo: "
                + tarjetaSaldo.getValor()
                + " | Monederos: "
                + tarjetaMonederos.getValor()
                + " | Clientes: "
                + tarjetaClientes.getValor()
                + " | Filas: "
                + modeloTabla.getRowCount()
                + " | Inventario completo: "
                + inventarioDisponible;
    }
}
