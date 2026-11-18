
package vista;

import modelo.Cliente;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * ============================================================
 *                    MONEDERO VIRTUAL
 * ============================================================
 *
 * PANTALLA:
 * PanelClientes.java
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * RESPONSABILIDADES:
 *
 * 1. Mostrar los clientes registrados.
 * 2. Registrar clientes nuevos.
 * 3. Validar los formularios.
 * 4. Consultar ClienteService.
 * 5. Buscar clientes.
 * 6. Mostrar estadisticas reales.
 * 7. Seleccionar clientes.
 * 8. Actualizar datos sin reiniciar.
 * 9. Mantener la integracion con el dashboard.
 *
 * ARQUITECTURA:
 *
 * VentanaPrincipal
 *       |
 *       v
 * PanelClientes
 *       |
 *       v
 * ContextoAplicacion
 *       |
 *       v
 * ClienteService
 *       |
 *       v
 * Modelo Cliente
 *
 * IMPORTANTE:
 *
 * Esta clase no crea ClienteService.
 *
 * Utiliza el servicio compartido.
 *
 * El registro se realiza exclusivamente
 * mediante ClienteService.registrarCliente().
 *
 * No incorpora datos ficticios.
 *
 * Java 21.
 */
public final class PanelClientes extends JPanel {

    private static final long serialVersionUID = 1L;

    // ========================================================
    // 1. CONSTANTES
    // ========================================================

    private static final String TITULO =
            "Clientes.";

    private static final String SUBTITULO =
            "Directorio de cuentas registradas.";

    private static final String IDENTIFICADOR =
            "WORKSPACE / 002";

    private static final int MAX_ID = 30;

    private static final int MAX_NOMBRE = 100;

    private static final int MAX_CORREO = 150;

    // ========================================================
    // 2. CONTEXTO COMPARTIDO
    // ========================================================

    private final ContextoAplicacion contexto;

    // ========================================================
    // 3. COMPONENTES PRINCIPALES
    // ========================================================

    private JPanel contenido;

    private JPanel columnaPrincipal;

    private EncabezadoPanel encabezado;

    private TarjetaEstadistica tarjetaTotal;

    private TarjetaEstadistica tarjetaActivos;

    private JPanel panelEstadisticas;

    private JPanel panelDirectorio;

    private JTextField campoBusqueda;

    private TablaPremium tablaClientes;

    private TablaPremium.ModeloNoEditable modeloTabla;

    private JLabel etiquetaResultados;

    private JLabel etiquetaEstado;

    // ========================================================
    // 4. ESTADO
    // ========================================================

    private Runnable listenerActualizacion;

    private boolean actualizando;

    // ========================================================
    // 5. CONSTRUCTOR
    // ========================================================

    /**
     * Recibe el mismo contexto que
     * VentanaPrincipal y PanelInicio.
     */
    public PanelClientes(
            ContextoAplicacion contexto) {

        super(new BorderLayout());

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "El contexto no puede ser null"
                );

        this.listenerActualizacion = null;

        this.actualizando = false;

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
    // 7. CONTENIDO GENERAL
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

        columnaPrincipal =
                new JPanel();

        columnaPrincipal.setOpaque(false);

        columnaPrincipal.setLayout(
                new BoxLayout(
                        columnaPrincipal,
                        BoxLayout.Y_AXIS
                )
        );

        construirEncabezado();

        construirEstadisticas();

        construirDirectorio();

        construirPie();

        agregarBloque(encabezado);

        agregarBloque(panelEstadisticas);

        agregarBloque(panelDirectorio);

        contenido.add(
                columnaPrincipal,
                BorderLayout.NORTH
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

        if (columnaPrincipal.getComponentCount() > 0) {

            columnaPrincipal.add(
                    Box.createVerticalStrut(
                            Espaciado.DASHBOARD_SEPARACION
                    )
            );
        }

        columnaPrincipal.add(
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
                "Nuevo cliente",
                this::abrirFormularioRegistro
        );

        encabezado.crearAccionSecundaria(
                "Actualizar",
                this::refrescarDatos
        );
    }

    // ========================================================
    // 10. TARJETAS ESTADISTICAS
    // ========================================================

    private void construirEstadisticas() {

        panelEstadisticas =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                Espaciado.LG,
                                0
                        )
                );

        panelEstadisticas.setOpaque(false);

        tarjetaTotal =
                TarjetaEstadistica.normal(
                        "CLIENTES REGISTRADOS",
                        "0",
                        "Total de cuentas en el sistema"
                );

        tarjetaActivos =
                TarjetaEstadistica.positiva(
                        "CLIENTES ACTIVOS",
                        "0",
                        "Cuentas actualmente activas"
                );

        tarjetaActivos.setMostrarLineaAcento(
                true
        );

        panelEstadisticas.add(
                tarjetaTotal
        );

        panelEstadisticas.add(
                tarjetaActivos
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

        construirCabeceraDirectorio();

        construirTabla();

        panelDirectorio.add(
                crearPieDirectorio(),
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 12. CABECERA DEL DIRECTORIO
    // ========================================================

    private void construirCabeceraDirectorio() {

        JPanel superior =
                new JPanel(
                        new BorderLayout(
                                Espaciado.LG,
                                Espaciado.MD
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
                        "Directorio de clientes"
                );

        titulo.setFont(
                Tipografia.tituloTarjeta()
        );

        titulo.setForeground(
                Paleta.BONE
        );

        JLabel descripcion =
                new JLabel(
                        "Consulta y administracion de registros"
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

        // ----------------------------------
        // BUSCADOR
        // ----------------------------------

        campoBusqueda =
                new JTextField();

        campoBusqueda.setFont(
                Tipografia.textoFormulario()
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

        campoBusqueda.setSelectionColor(
                Paleta.ACENTO_SUAVE
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
                        290,
                        Espaciado.INPUT_ALTURA
                )
        );

        campoBusqueda.setToolTipText(
                "Buscar por ID, nombre o correo"
        );

        campoBusqueda.getAccessibleContext()
                .setAccessibleName(
                        "Buscar clientes"
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
    // 13. TABLA DE CLIENTES
    // ========================================================

    private void construirTabla() {

        modeloTabla =
                new TablaPremium.ModeloNoEditable(
                        new String[]{
                                "ID CLIENTE",
                                "NOMBRE",
                                "CORREO ELECTRONICO"
                        },
                        0
                );

        tablaClientes =
                new TablaPremium(
                        modeloTabla
                );

        tablaClientes.setMensajeVacio(
                "No hay clientes registrados"
        );

        tablaClientes.setFilasAlternas(
                true
        );

        tablaClientes.setMostrarSeparadores(
                true
        );

        tablaClientes.configurarAnchoColumna(
                0,
                120
        );

        tablaClientes.configurarAnchoColumna(
                1,
                220
        );

        tablaClientes.configurarAnchoColumna(
                2,
                340
        );

        tablaClientes.setPreferredScrollableViewportSize(
                new Dimension(
                        760,
                        320
                )
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
                new Dimension(
                        760,
                        360
                )
        );

        panelDirectorio.add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // ========================================================
    // 14. PIE DE DIRECTORIO
    // ========================================================

    private JPanel crearPieDirectorio() {

        JPanel pie =
                new JPanel(
                        new BorderLayout()
                );

        pie.setOpaque(false);

        pie.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        0,
                        0,
                        0
                )
        );

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

        JLabel ayuda =
                new JLabel(
                        "Selecciona un registro para consultar su ID"
                );

        ayuda.setFont(
                Tipografia.caption()
        );

        ayuda.setForeground(
                Paleta.TEXTO_TERCIARIO
        );

        pie.add(
                etiquetaResultados,
                BorderLayout.WEST
        );

        pie.add(
                ayuda,
                BorderLayout.EAST
        );

        tablaClientes.getSelectionModel()
                .addListSelectionListener(evento -> {

                    if (evento.getValueIsAdjusting()) {
                        return;
                    }

                    String id =
                            getClienteSeleccionadoId();

                    if (id == null) {

                        ayuda.setText(
                                "Selecciona un registro para consultar su ID"
                        );

                    } else {

                        ayuda.setText(
                                "Cliente seleccionado: " + id
                        );
                    }
                });

        return pie;
    }

    // ========================================================
    // 15. PIE GENERAL
    // ========================================================

    private void construirPie() {

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
    }

    // ========================================================
    // 16. CARGAR CLIENTES REALES
    // ========================================================

    /**
     * Actualiza la informacion utilizando
     * ClienteService.
     *
     * Se ejecuta en el hilo de Swing.
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

            // ----------------------------------
            // RECUPERAR CLIENTES
            // ----------------------------------

            List<Cliente> clientes =
                    new ArrayList<>();

            contexto.getClienteService()
                    .recorrerClientes(
                            clientes::add
                    );

            // Orden estable para el directorio.

            clientes.sort(
                    Comparator.comparing(
                            cliente -> textoSeguro(
                                    cliente.getId()
                            ),
                            String.CASE_INSENSITIVE_ORDER
                    )
            );

            // ----------------------------------
            // ACTUALIZAR MODELO
            // ----------------------------------

            modeloTabla.setRowCount(0);

            for (Cliente cliente : clientes) {

                modeloTabla.addRow(
                        new Object[]{
                                cliente.getId(),
                                obtenerNombre(cliente),
                                obtenerCorreo(cliente)
                        }
                );
            }

            // ----------------------------------
            // ESTADISTICAS
            // ----------------------------------

            int total =
                    contexto.getClienteService()
                            .cantidadClientes();

            int activos =
                    contexto.getClienteService()
                            .cantidadClientesActivos();

            tarjetaTotal.setValorNumerico(
                    total
            );

            tarjetaActivos.setValorNumerico(
                    activos
            );

            // ----------------------------------
            // FILTRO
            // ----------------------------------

            tablaClientes.buscar(
                    campoBusqueda.getText()
            );

            actualizarResultados();

            etiquetaEstado.setText(
                    "Datos actualizados correctamente"
            );

            etiquetaEstado.setForeground(
                    Paleta.SAGE
            );

        } catch (RuntimeException error) {

            mostrarError(
                    "No fue posible consultar los clientes",
                    error
            );

        } finally {

            actualizando = false;
        }
    }

    // ========================================================
    // 17. ADAPTACION DE CAMPOS DEL MODELO
    // ========================================================

    /**
     * Esta adaptacion evita acoplar
     * la vista a un nombre especifico
     * de getter para nombre o correo.
     *
     * Se consideran las convenciones
     * comunes de los modelos existentes.
     *
     * En caso de no encontrarse un getter,
     * se devuelve una cadena vacia.
     *
     * El identificador si utiliza
     * directamente Cliente.getId().
     */
    private static String obtenerNombre(
            Cliente cliente) {

        return leerPropiedad(
                cliente,
                "getNombre",
                "getNombreCompleto"
        );
    }

    private static String obtenerCorreo(
            Cliente cliente) {

        return leerPropiedad(
                cliente,
                "getCorreo",
                "getEmail",
                "getCorreoElectronico"
        );
    }

    private static String leerPropiedad(
            Cliente cliente,
            String... candidatos) {

        for (String nombreMetodo : candidatos) {

            try {

                Method metodo =
                        cliente.getClass()
                                .getMethod(
                                        nombreMetodo
                                );

                Object resultado =
                        metodo.invoke(
                                cliente
                        );

                return resultado == null
                        ? ""
                        : resultado.toString();

            } catch (ReflectiveOperationException
                     | SecurityException ignorado) {

                // Probar el siguiente
                // nombre de getter.
            }
        }

        return "";
    }

    // ========================================================
    // 18. BUSCAR CLIENTES
    // ========================================================

    private void buscar() {

        if (tablaClientes == null) {
            return;
        }

        tablaClientes.buscar(
                campoBusqueda.getText()
        );

        actualizarResultados();
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
                textoSeguro(texto)
        );
    }

    public void limpiarBusqueda() {

        buscarClientes("");
    }

    // ========================================================
    // 19. ACTUALIZAR RESULTADOS
    // ========================================================

    private void actualizarResultados() {

        int visibles =
                tablaClientes.getRowCount();

        int total =
                modeloTabla.getRowCount();

        etiquetaResultados.setText(
                "Mostrando "
                        + visibles
                        + " de "
                        + total
                        + " registros"
        );
    }

    // ========================================================
    // 20. FORMULARIO DE REGISTRO
    // ========================================================

    /**
     * Abre un formulario modal.
     *
     * No registra nada hasta que
     * el usuario confirma.
     */
    public void abrirFormularioRegistro() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::abrirFormularioRegistro
            );

            return;
        }

        JTextField campoId =
                crearCampoFormulario();

        JTextField campoNombre =
                crearCampoFormulario();

        JTextField campoCorreo =
                crearCampoFormulario();

        campoId.setToolTipText(
                "Ejemplo: C004"
        );

        campoNombre.setToolTipText(
                "Nombre del cliente"
        );

        campoCorreo.setToolTipText(
                "Ejemplo: cliente@ejemplo.com"
        );

        JPanel formulario =
                new JPanel(
                        new GridLayout(
                                6,
                                1,
                                0,
                                8
                        )
                );

        formulario.setBackground(
                Paleta.FONDO_SECUNDARIO
        );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        8,
                        12,
                        8
                )
        );

        formulario.add(
                crearEtiqueta(
                        "IDENTIFICADOR"
                )
        );

        formulario.add(
                campoId
        );

        formulario.add(
                crearEtiqueta(
                        "NOMBRE COMPLETO"
                )
        );

        formulario.add(
                campoNombre
        );

        formulario.add(
                crearEtiqueta(
                        "CORREO ELECTRONICO"
                )
        );

        formulario.add(
                campoCorreo
        );

        // ----------------------------------
        // DIALOGO DE CONFIRMACION
        // ----------------------------------

        while (true) {

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            formulario,
                            "MONEDERO / Registrar cliente",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (respuesta != JOptionPane.OK_OPTION) {

                return;
            }

            String id =
                    campoId.getText().trim();

            String nombre =
                    campoNombre.getText().trim();

            String correo =
                    campoCorreo.getText().trim();

            // ----------------------------------
            // VALIDACION
            // ----------------------------------

            String error =
                    validarFormulario(
                            id,
                            nombre,
                            correo
                    );

            if (error != null) {

                JOptionPane.showMessageDialog(
                        this,
                        error,
                        "Validacion de cliente",
                        JOptionPane.WARNING_MESSAGE
                );

                continue;
            }

            // ----------------------------------
            // REGISTRO REAL
            // ----------------------------------

            try {

                Cliente registrado =
                        contexto.getClienteService()
                                .registrarCliente(
                                        id,
                                        nombre,
                                        correo
                                );

                refrescarDatos();

                if (listenerActualizacion != null) {

                    listenerActualizacion.run();
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Cliente registrado correctamente.\n\n"
                                + "ID: " + registrado.getId()
                                + "\n"
                                + "Nombre: " + nombre
                                + "\n"
                                + "Correo: " + correo,
                        "Registro exitoso",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;

            } catch (RuntimeException excepcion) {

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible registrar el cliente.\n\n"
                                + textoSeguro(
                                excepcion.getMessage()
                        ),
                        "Error de registro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // ========================================================
    // 21. CAMPOS DEL FORMULARIO
    // ========================================================

    private JTextField crearCampoFormulario() {

        JTextField campo =
                new JTextField();

        campo.setFont(
                Tipografia.textoFormulario()
        );

        campo.setForeground(
                Paleta.INPUT_TEXTO
        );

        campo.setBackground(
                Paleta.INPUT_FONDO
        );

        campo.setCaretColor(
                Paleta.CHAMPAGNE
        );

        campo.setSelectionColor(
                Paleta.ACENTO_SUAVE
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.INPUT_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 12, 10, 12
                        )
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        340,
                        Espaciado.INPUT_ALTURA
                )
        );

        return campo;
    }

    // ========================================================
    // 22. ETIQUETAS DEL FORMULARIO
    // ========================================================

    private JLabel crearEtiqueta(
            String texto) {

        JLabel etiqueta =
                new JLabel(texto);

        etiqueta.setFont(
                Tipografia.etiqueta()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        return etiqueta;
    }

    // ========================================================
    // 23. VALIDACION DE DATOS
    // ========================================================

    /**
     * Valida estructura y longitud.
     *
     * Las reglas de negocio definitivas,
     * como IDs duplicados, pertenecen
     * a ClienteService.
     */
    private String validarFormulario(
            String id,
            String nombre,
            String correo) {

        if (id.isBlank()) {

            return "El ID del cliente es obligatorio.";
        }

        if (id.length() > MAX_ID) {

            return "El ID no puede superar "
                    + MAX_ID + " caracteres.";
        }

        if (!id.matches("[A-Za-z0-9_-]+")) {

            return "El ID solo puede contener "
                    + "letras, numeros, guiones "
                    + "y guiones bajos.";
        }

        if (nombre.isBlank()) {

            return "El nombre es obligatorio.";
        }

        if (nombre.length() > MAX_NOMBRE) {

            return "El nombre supera "
                    + MAX_NOMBRE + " caracteres.";
        }

        if (correo.isBlank()) {

            return "El correo es obligatorio.";
        }

        if (correo.length() > MAX_CORREO) {

            return "El correo supera "
                    + MAX_CORREO + " caracteres.";
        }

        if (!correo.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
        )) {

            return "Introduce una direccion "
                    + "de correo valida.";
        }

        return null;
    }

    // ========================================================
    // 24. CLIENTE SELECCIONADO
    // ========================================================

    /**
     * Devuelve el ID del cliente
     * seleccionado en la tabla.
     *
     * Funciona aunque el usuario
     * ordene o filtre las filas.
     */
    public String getClienteSeleccionadoId() {

        if (tablaClientes == null) {
            return null;
        }

        Object valor =
                tablaClientes.getValorSeleccionado(
                        0
                );

        return valor == null
                ? null
                : valor.toString();
    }

    // ========================================================
    // 25. CONSULTAR CLIENTE SELECCIONADO
    // ========================================================

    /**
     * Muestra el registro actualmente
     * seleccionado.
     *
     * Este metodo no modifica datos.
     */
    public void mostrarClienteSeleccionado() {

        String id =
                getClienteSeleccionadoId();

        if (id == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un cliente primero.",
                    "Directorio de clientes",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int fila =
                tablaClientes
                        .getFilaModeloSeleccionada();

        String nombre =
                Objects.toString(
                        modeloTabla.getValueAt(fila, 1),
                        ""
                );

        String correo =
                Objects.toString(
                        modeloTabla.getValueAt(fila, 2),
                        ""
                );

        JOptionPane.showMessageDialog(
                this,
                "ID: " + id
                        + "\nNombre: " + nombre
                        + "\nCorreo: " + correo,
                "Detalle del cliente",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========================================================
    // 26. NOTIFICAR ACTUALIZACION
    // ========================================================

    /**
     * VentanaPrincipal puede utilizar
     * este listener para refrescar
     * PanelInicio cuando se registre
     * un cliente nuevo.
     */
    public void setListenerActualizacion(
            Runnable listener) {

        listenerActualizacion = listener;
    }

    // ========================================================
    // 27. MANEJO DE ERRORES
    // ========================================================

    private void mostrarError(
            String titulo,
            RuntimeException error) {

        etiquetaEstado.setText(
                titulo
        );

        etiquetaEstado.setForeground(
                Paleta.ERROR
        );

        System.err.println(
                "[PANEL CLIENTES] "
                        + titulo
                        + ": "
                        + error.getMessage()
        );

        JOptionPane.showMessageDialog(
                this,
                titulo
                        + "\n\n"
                        + textoSeguro(
                        error.getMessage()
                ),
                "MONEDERO / Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ========================================================
    // 28. UTILIDADES
    // ========================================================

    private static String textoSeguro(
            String texto) {

        return texto == null
                ? ""
                : texto;
    }

    // ========================================================
    // 29. GETTERS
    // ========================================================

    public ContextoAplicacion getContexto() {

        return contexto;
    }

    public TablaPremium getTablaClientes() {

        return tablaClientes;
    }

    public EncabezadoPanel getEncabezado() {

        return encabezado;
    }

    public TarjetaEstadistica getTarjetaTotal() {

        return tarjetaTotal;
    }

    public TarjetaEstadistica getTarjetaActivos() {

        return tarjetaActivos;
    }

    // ========================================================
    // 30. INFORMACION
    // ========================================================

    public String generarDescripcion() {

        return "PanelClientes"
                + " | Tema: Institutional Noir"
                + " | Total: "
                + tarjetaTotal.getValor()
                + " | Activos: "
                + tarjetaActivos.getValor()
                + " | Visibles: "
                + tablaClientes.getRowCount()
                + " | Busqueda: "
                + campoBusqueda.getText();
    }
}
