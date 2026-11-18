
package vista;

import modelo.Cliente;
import servicios.ContextoAplicacion;

import vista.componentes.BotonPremium;
import vista.componentes.EncabezadoPanel;

import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * ============================================================
 *                   MONEDERO VIRTUAL
 *                     PRIVATE LEDGER
 * ============================================================
 *
 * PANEL:
 * PanelTransacciones.java
 *
 * ESTILO:
 * Institutional Noir
 *
 * RESPONSABILIDADES:
 *
 * 1. Ejecutar depositos.
 * 2. Ejecutar retiros.
 * 3. Ejecutar transferencias.
 * 4. Validar datos de entrada.
 * 5. Solicitar confirmacion.
 * 6. Utilizar CoordinadorOperaciones.
 * 7. Refrescar estadisticas.
 * 8. Notificar cambios al dashboard.
 *
 * IMPORTANTE:
 *
 * Este panel nunca modifica directamente
 * el saldo de un monedero.
 *
 * Todas las operaciones se ejecutan
 * mediante CoordinadorOperaciones.
 *
 * Los servicios son compartidos mediante
 * ContextoAplicacion.
 *
 * Java 21.
 */
public final class PanelTransacciones extends JPanel {

    private static final long serialVersionUID = 1L;

    // ========================================================
    // 1. TIPOS DE OPERACION
    // ========================================================

    public enum TipoOperacion {
        DEPOSITO,
        RETIRO,
        TRANSFERENCIA
    }

    // ========================================================
    // 2. CONTEXTO
    // ========================================================

    private final ContextoAplicacion contexto;

    // ========================================================
    // 3. COMPONENTES GENERALES
    // ========================================================

    private EncabezadoPanel encabezado;

    private JTabbedPane pestanas;

    private JLabel etiquetaOperaciones;

    private JLabel etiquetaSaldo;

    private JLabel etiquetaEstado;

    private Runnable listenerActualizacion;

    // ========================================================
    // 4. FORMULARIO DEPOSITO
    // ========================================================

    private JComboBox<String> depositoCliente;

    private JTextField depositoMonedero;

    private JTextField depositoMonto;

    // ========================================================
    // 5. FORMULARIO RETIRO
    // ========================================================

    private JComboBox<String> retiroCliente;

    private JTextField retiroMonedero;

    private JTextField retiroMonto;

    // ========================================================
    // 6. FORMULARIO TRANSFERENCIA
    // ========================================================

    private JComboBox<String> transferenciaOrigenCliente;

    private JTextField transferenciaOrigenMonedero;

    private JComboBox<String> transferenciaDestinoCliente;

    private JTextField transferenciaDestinoMonedero;

    private JTextField transferenciaMonto;

    private JTextField transferenciaComision;

    // ========================================================
    // 7. ESTADO INTERNO
    // ========================================================

    private boolean operacionEnCurso;

    // ========================================================
    // 8. CONSTRUCTOR
    // ========================================================

    public PanelTransacciones(
            ContextoAplicacion contexto) {

        super(new BorderLayout());

        this.contexto =
                Objects.requireNonNull(
                        contexto,
                        "ContextoAplicacion no puede ser null"
                );

        this.operacionEnCurso = false;

        inicializar();

        refrescarDatos();
    }

    // ========================================================
    // 9. INICIALIZACION
    // ========================================================

    private void inicializar() {

        setOpaque(true);

        setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        JPanel contenido =
                new JPanel(
                        new BorderLayout(0, 22)
                );

        contenido.setOpaque(true);

        contenido.setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        contenido.setBorder(
                Espaciado.bordeContenido()
        );

        construirEncabezado();

        construirPestanas();

        construirBarraInferior();

        contenido.add(
                encabezado,
                BorderLayout.NORTH
        );

        contenido.add(
                pestanas,
                BorderLayout.CENTER
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
    // 10. ENCABEZADO
    // ========================================================

    private void construirEncabezado() {

        encabezado =
                new EncabezadoPanel(
                        "Transacciones.",
                        "Depositos, retiros y transferencias.",
                        "WORKSPACE / 004",
                        EncabezadoPanel.Variante.NORMAL
                );

        encabezado.crearAccionSecundaria(
                "Actualizar",
                this::refrescarDatos
        );
    }

    // ========================================================
    // 11. PESTANAS
    // ========================================================

    private void construirPestanas() {

        pestanas =
                new JTabbedPane();

        pestanas.setFont(
                Tipografia.cuerpo()
        );

        pestanas.setBackground(
                Paleta.FONDO_SECUNDARIO
        );

        pestanas.setForeground(
                Paleta.BONE
        );

        pestanas.setBorder(
                BorderFactory.createEmptyBorder()
        );

        pestanas.addTab(
                "Depositar",
                construirFormularioDeposito()
        );

        pestanas.addTab(
                "Retirar",
                construirFormularioRetiro()
        );

        pestanas.addTab(
                "Transferir",
                construirFormularioTransferencia()
        );
    }

    // ========================================================
    // 12. FORMULARIO DE DEPOSITO
    // ========================================================

    private JPanel construirFormularioDeposito() {

        JPanel formulario =
                crearFormularioBase(
                        "Nuevo deposito",
                        "Agregar capital a un monedero."
                );

        depositoCliente =
                crearSelectorCliente();

        depositoMonedero =
                crearCampo(
                        "Ejemplo: M001"
                );

        depositoMonto =
                crearCampo(
                        "Ejemplo: 50000.00"
                );

        agregarCampo(
                formulario,
                "CLIENTE TITULAR",
                depositoCliente
        );

        agregarCampo(
                formulario,
                "ID DEL MONEDERO",
                depositoMonedero
        );

        agregarCampo(
                formulario,
                "IMPORTE / COP",
                depositoMonto
        );

        BotonPremium boton =
                BotonPremium.primario(
                        "Revisar deposito"
                );

        boton.addActionListener(
                evento -> ejecutarDeposito()
        );

        agregarBoton(
                formulario,
                boton
        );

        return formulario;
    }

    // ========================================================
    // 13. FORMULARIO DE RETIRO
    // ========================================================

    private JPanel construirFormularioRetiro() {

        JPanel formulario =
                crearFormularioBase(
                        "Nuevo retiro",
                        "Retirar capital de un monedero."
                );

        retiroCliente =
                crearSelectorCliente();

        retiroMonedero =
                crearCampo(
                        "Ejemplo: M001"
                );

        retiroMonto =
                crearCampo(
                        "Ejemplo: 10000.00"
                );

        agregarCampo(
                formulario,
                "CLIENTE TITULAR",
                retiroCliente
        );

        agregarCampo(
                formulario,
                "ID DEL MONEDERO",
                retiroMonedero
        );

        agregarCampo(
                formulario,
                "IMPORTE / COP",
                retiroMonto
        );

        BotonPremium boton =
                BotonPremium.primario(
                        "Revisar retiro"
                );

        boton.addActionListener(
                evento -> ejecutarRetiro()
        );

        agregarBoton(
                formulario,
                boton
        );

        return formulario;
    }

    // ========================================================
    // 14. FORMULARIO DE TRANSFERENCIA
    // ========================================================

    private JPanel construirFormularioTransferencia() {

        JPanel formulario =
                crearFormularioBase(
                        "Nueva transferencia",
                        "Mover capital entre monederos."
                );

        transferenciaOrigenCliente =
                crearSelectorCliente();

        transferenciaOrigenMonedero =
                crearCampo(
                        "Ejemplo: M001"
                );

        transferenciaDestinoCliente =
                crearSelectorCliente();

        transferenciaDestinoMonedero =
                crearCampo(
                        "Ejemplo: M002"
                );

        transferenciaMonto =
                crearCampo(
                        "Ejemplo: 10000.00"
                );

        transferenciaComision =
                crearCampo(
                        "Ejemplo: 100.00"
                );

        transferenciaComision.setText(
                "0"
        );

        agregarCampo(
                formulario,
                "CLIENTE ORIGEN",
                transferenciaOrigenCliente
        );

        agregarCampo(
                formulario,
                "MONEDERO ORIGEN",
                transferenciaOrigenMonedero
        );

        agregarCampo(
                formulario,
                "CLIENTE DESTINO",
                transferenciaDestinoCliente
        );

        agregarCampo(
                formulario,
                "MONEDERO DESTINO",
                transferenciaDestinoMonedero
        );

        agregarCampo(
                formulario,
                "IMPORTE / COP",
                transferenciaMonto
        );

        agregarCampo(
                formulario,
                "COMISION / COP",
                transferenciaComision
        );

        BotonPremium boton =
                BotonPremium.primario(
                        "Revisar transferencia"
                );

        boton.addActionListener(
                evento -> ejecutarTransferencia()
        );

        agregarBoton(
                formulario,
                boton
        );

        return formulario;
    }

    // ========================================================
    // 15. FORMULARIO BASE
    // ========================================================

    private JPanel crearFormularioBase(
            String titulo,
            String descripcion) {

        JPanel superficie =
                new JPanel(
                        new BorderLayout()
                );

        superficie.setOpaque(true);

        superficie.setBackground(
                Paleta.FONDO_TARJETA
        );

        superficie.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.BORDE_SUAVE,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                28, 32, 28, 32
                        )
                )
        );

        JPanel columna =
                new JPanel();

        columna.setOpaque(false);

        columna.setLayout(
                new BoxLayout(
                        columna,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etiquetaTitulo =
                new JLabel(titulo);

        etiquetaTitulo.setFont(
                Tipografia.tituloTarjeta()
        );

        etiquetaTitulo.setForeground(
                Paleta.BONE
        );

        JLabel etiquetaDescripcion =
                new JLabel(descripcion);

        etiquetaDescripcion.setFont(
                Tipografia.cuerpoPequeno()
        );

        etiquetaDescripcion.setForeground(
                Paleta.ASH
        );

        etiquetaTitulo.setAlignmentX(
                LEFT_ALIGNMENT
        );

        etiquetaDescripcion.setAlignmentX(
                LEFT_ALIGNMENT
        );

        columna.add(
                etiquetaTitulo
        );

        columna.add(
                Box.createVerticalStrut(8)
        );

        columna.add(
                etiquetaDescripcion
        );

        columna.add(
                Box.createVerticalStrut(24)
        );

        superficie.add(
                columna,
                BorderLayout.NORTH
        );

        // Cada superficie mantiene una
        // referencia a su columna mediante
        // una propiedad de cliente de Swing.

        superficie.putClientProperty(
                "formulario.columna",
                columna
        );

        return superficie;
    }

    // ========================================================
    // 16. AGREGAR CAMPO AL FORMULARIO
    // ========================================================

    private void agregarCampo(
            JPanel formulario,
            String titulo,
            JComponent componente) {

        JPanel columna =
                obtenerColumna(
                        formulario
                );

        JLabel etiqueta =
                new JLabel(
                        titulo
                );

        etiqueta.setFont(
                Tipografia.etiqueta()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiqueta.setAlignmentX(
                LEFT_ALIGNMENT
        );

        componente.setAlignmentX(
                LEFT_ALIGNMENT
        );

        componente.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        columna.add(
                etiqueta
        );

        columna.add(
                Box.createVerticalStrut(8)
        );

        columna.add(
                componente
        );

        columna.add(
                Box.createVerticalStrut(20)
        );
    }

    // ========================================================
    // 17. AGREGAR BOTON
    // ========================================================

    private void agregarBoton(
            JPanel formulario,
            BotonPremium boton) {

        JPanel columna =
                obtenerColumna(
                        formulario
                );

        boton.setAlignmentX(
                LEFT_ALIGNMENT
        );

        boton.setMaximumSize(
                boton.getPreferredSize()
        );

        columna.add(
                Box.createVerticalStrut(8)
        );

        columna.add(
                boton
        );

        columna.add(
                Box.createVerticalStrut(12)
        );
    }

    // ========================================================
    // 18. OBTENER COLUMNA
    // ========================================================

    private JPanel obtenerColumna(
            JPanel formulario) {

        Object valor =
                formulario.getClientProperty(
                        "formulario.columna"
                );

        if (valor instanceof JPanel panel) {
            return panel;
        }

        throw new IllegalStateException(
                "Formulario sin columna interna."
        );
    }

    // ========================================================
    // 19. CREAR SELECTOR DE CLIENTE
    // ========================================================

    private JComboBox<String> crearSelectorCliente() {

        JComboBox<String> selector =
                new JComboBox<>();

        selector.setFont(
                Tipografia.cuerpo()
        );

        selector.setBackground(
                Paleta.INPUT_FONDO
        );

        selector.setForeground(
                Paleta.INPUT_TEXTO
        );

        selector.setPreferredSize(
                new Dimension(
                        440,
                        42
                )
        );

        return selector;
    }

    // ========================================================
    // 20. CREAR CAMPO DE TEXTO
    // ========================================================

    private JTextField crearCampo(
            String ejemplo) {

        JTextField campo =
                new JTextField();

        campo.setFont(
                Tipografia.cuerpo()
        );

        campo.setBackground(
                Paleta.INPUT_FONDO
        );

        campo.setForeground(
                Paleta.INPUT_TEXTO
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
                                Paleta.INPUT_BORDE,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 12, 10, 12
                        )
                )
        );

        campo.setToolTipText(
                ejemplo
        );

        campo.setPreferredSize(
                new Dimension(
                        440,
                        42
                )
        );

        return campo;
    }

    // ========================================================
    // 21. ACTUALIZAR DATOS
    // ========================================================

    public void refrescarDatos() {

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    this::refrescarDatos
            );

            return;
        }

        try {

            List<String> ids =
                    obtenerIdsClientes();

            cargarSelector(
                    depositoCliente,
                    ids
            );

            cargarSelector(
                    retiroCliente,
                    ids
            );

            cargarSelector(
                    transferenciaOrigenCliente,
                    ids
            );

            cargarSelector(
                    transferenciaDestinoCliente,
                    ids
            );

            int operaciones =
                    contexto.getTransaccionService()
                            .cantidadTransacciones();

            BigDecimal saldo =
                    contexto.getClienteService()
                            .saldoTotalSistema();

            etiquetaOperaciones.setText(
                    "TRANSACCIONES: " + operaciones
            );

            etiquetaSaldo.setText(
                    "CAPITAL: " + formatearMoneda(saldo)
            );

            actualizarEstado(
                    "DATOS FINANCIEROS ACTUALIZADOS",
                    false
            );

        } catch (RuntimeException error) {

            actualizarEstado(
                    "ERROR DE CONSULTA: "
                            + mensajeError(error),
                    true
            );

            System.err.println(
                    "[TRANSACCIONES] "
                            + mensajeError(error)
            );
        }
    }

    // ========================================================
    // 22. OBTENER CLIENTES
    // ========================================================

    private List<String> obtenerIdsClientes() {

        List<String> ids =
                new ArrayList<>();

        contexto.getClienteService()
                .recorrerClientes(cliente -> {

                    if (cliente != null
                            && cliente.getId() != null) {

                        ids.add(
                                cliente.getId()
                        );
                    }
                });

        ids.sort(
                Comparator.naturalOrder()
        );

        return ids;
    }

    // ========================================================
    // 23. CARGAR SELECTOR
    // ========================================================

    private void cargarSelector(
            JComboBox<String> selector,
            List<String> ids) {

        Object anterior =
                selector.getSelectedItem();

        selector.removeAllItems();

        for (String id : ids) {

            selector.addItem(id);
        }

        if (anterior != null) {

            selector.setSelectedItem(
                    anterior
            );

            // Si el elemento ya no existe,
            // seleccionar el primer registro.

            if (!anterior.equals(
                    selector.getSelectedItem()
            )) {

                if (selector.getItemCount() > 0) {
                    selector.setSelectedIndex(0);
                }
            }
        }
    }

    // ========================================================
    // 24. EJECUTAR DEPOSITO
    // ========================================================

    public void ejecutarDeposito() {

        if (operacionEnCurso) {
            return;
        }

        try {

            String cliente =
                    obtenerCliente(
                            depositoCliente
                    );

            String monedero =
                    validarId(
                            depositoMonedero.getText(),
                            "monedero"
                    );

            BigDecimal monto =
                    leerMontoPositivo(
                            depositoMonto.getText(),
                            "importe"
                    );

            String resumen =
                    "OPERACION: DEPOSITO"
                            + "\n\nCLIENTE: " + cliente
                            + "\nMONEDERO: " + monedero
                            + "\nIMPORTE: "
                            + formatearMoneda(monto);

            if (!confirmarOperacion(resumen)) {
                return;
            }

            operacionEnCurso = true;

            contexto.getCoordinador()
                    .depositar(
                            cliente,
                            monedero,
                            monto
                    );

            finalizarOperacion(
                    "Deposito realizado correctamente."
            );

            depositoMonto.setText("");

        } catch (IllegalArgumentException error) {

            mostrarAdvertencia(
                    mensajeError(error)
            );

        } catch (RuntimeException error) {

            mostrarErrorOperacion(error);

        } finally {

            operacionEnCurso = false;
        }
    }

    // ========================================================
    // 25. EJECUTAR RETIRO
    // ========================================================

    public void ejecutarRetiro() {

        if (operacionEnCurso) {
            return;
        }

        try {

            String cliente =
                    obtenerCliente(
                            retiroCliente
                    );

            String monedero =
                    validarId(
                            retiroMonedero.getText(),
                            "monedero"
                    );

            BigDecimal monto =
                    leerMontoPositivo(
                            retiroMonto.getText(),
                            "importe"
                    );

            String resumen =
                    "OPERACION: RETIRO"
                            + "\n\nCLIENTE: " + cliente
                            + "\nMONEDERO: " + monedero
                            + "\nIMPORTE: "
                            + formatearMoneda(monto);

            if (!confirmarOperacion(resumen)) {
                return;
            }

            operacionEnCurso = true;

            contexto.getCoordinador()
                    .retirar(
                            cliente,
                            monedero,
                            monto
                    );

            finalizarOperacion(
                    "Retiro realizado correctamente."
            );

            retiroMonto.setText("");

        } catch (IllegalArgumentException error) {

            mostrarAdvertencia(
                    mensajeError(error)
            );

        } catch (RuntimeException error) {

            mostrarErrorOperacion(error);

        } finally {

            operacionEnCurso = false;
        }
    }

    // ========================================================
    // 26. EJECUTAR TRANSFERENCIA
    // ========================================================

    public void ejecutarTransferencia() {

        if (operacionEnCurso) {
            return;
        }

        try {

            String clienteOrigen =
                    obtenerCliente(
                            transferenciaOrigenCliente
                    );

            String monederoOrigen =
                    validarId(
                            transferenciaOrigenMonedero.getText(),
                            "monedero origen"
                    );

            String clienteDestino =
                    obtenerCliente(
                            transferenciaDestinoCliente
                    );

            String monederoDestino =
                    validarId(
                            transferenciaDestinoMonedero.getText(),
                            "monedero destino"
                    );

            BigDecimal monto =
                    leerMontoPositivo(
                            transferenciaMonto.getText(),
                            "importe"
                    );

            BigDecimal comision =
                    leerMontoNoNegativo(
                            transferenciaComision.getText(),
                            "comision"
                    );

            if (clienteOrigen.equals(clienteDestino)
                    && monederoOrigen.equals(
                    monederoDestino
            )) {

                throw new IllegalArgumentException(
                        "El origen y destino no pueden "
                                + "ser el mismo monedero."
                );
            }

            BigDecimal totalDebito =
                    monto.add(
                            comision
                    );

            String resumen =
                    "OPERACION: TRANSFERENCIA"
                            + "\n\nORIGEN"
                            + "\nCliente: " + clienteOrigen
                            + "\nMonedero: " + monederoOrigen
                            + "\n\nDESTINO"
                            + "\nCliente: " + clienteDestino
                            + "\nMonedero: " + monederoDestino
                            + "\n\nIMPORTE: "
                            + formatearMoneda(monto)
                            + "\nCOMISION: "
                            + formatearMoneda(comision)
                            + "\nIMPORTE + COMISION: "
                            + formatearMoneda(totalDebito)
                            + "\n\nLa aplicacion definitiva "
                            + "de la comision depende de "
                            + "las reglas del motor financiero.";

            if (!confirmarOperacion(resumen)) {
                return;
            }

            operacionEnCurso = true;

            contexto.getCoordinador()
                    .transferir(
                            clienteOrigen,
                            monederoOrigen,
                            clienteDestino,
                            monederoDestino,
                            monto,
                            comision
                    );

            finalizarOperacion(
                    "Transferencia realizada correctamente."
            );

            transferenciaMonto.setText("");

        } catch (IllegalArgumentException error) {

            mostrarAdvertencia(
                    mensajeError(error)
            );

        } catch (RuntimeException error) {

            mostrarErrorOperacion(error);

        } finally {

            operacionEnCurso = false;
        }
    }

    // ========================================================
    // 27. VALIDAR IDENTIFICADOR
    // ========================================================

    private String validarId(
            String valor,
            String nombre) {

        String limpio =
                valor == null
                        ? ""
                        : valor.trim();

        if (limpio.isBlank()) {

            throw new IllegalArgumentException(
                    "Debes introducir el ID del "
                            + nombre + "."
            );
        }

        if (limpio.length() > 30) {

            throw new IllegalArgumentException(
                    "El ID del " + nombre
                            + " es demasiado largo."
            );
        }

        return limpio;
    }

    // ========================================================
    // 28. OBTENER CLIENTE
    // ========================================================

    private String obtenerCliente(
            JComboBox<String> selector) {

        Object valor =
                selector.getSelectedItem();

        if (valor == null) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un cliente."
            );
        }

        return valor.toString();
    }

    // ========================================================
    // 29. VALIDACION MONETARIA
    // ========================================================

    /**
     * Formatos permitidos:
     *
     * 10000
     * 10000.50
     * 10000,50
     *
     * No se permiten separadores
     * de miles para evitar ambiguedad.
     *
     * Ejemplo:
     * 1.000 se interpreta como decimal,
     * no como mil.
     */
    private BigDecimal leerMontoPositivo(
            String texto,
            String nombre) {

        BigDecimal monto =
                leerMonto(
                        texto,
                        nombre
                );

        if (monto.signum() <= 0) {

            throw new IllegalArgumentException(
                    "El " + nombre
                            + " debe ser mayor que cero."
            );
        }

        return monto;
    }

    private BigDecimal leerMontoNoNegativo(
            String texto,
            String nombre) {

        BigDecimal monto =
                leerMonto(
                        texto,
                        nombre
                );

        if (monto.signum() < 0) {

            throw new IllegalArgumentException(
                    "La " + nombre
                            + " no puede ser negativa."
            );
        }

        return monto;
    }

    private BigDecimal leerMonto(
            String texto,
            String nombre) {

        if (texto == null
                || texto.isBlank()) {

            throw new IllegalArgumentException(
                    "Debes introducir el " + nombre + "."
            );
        }

        String limpio =
                texto.trim()
                        .replace(',', '.');

        if (!limpio.matches(
                "\\d+(\\.\\d{1,2})?"
        )) {

            throw new IllegalArgumentException(
                    "Formato monetario invalido para "
                            + nombre
                            + ". Usa, por ejemplo, 10000.50"
            );
        }

        try {

            BigDecimal valor =
                    new BigDecimal(
                            limpio
                    );

            return valor.setScale(
                    2,
                    RoundingMode.UNNECESSARY
            );

        } catch (NumberFormatException
                 | ArithmeticException error) {

            throw new IllegalArgumentException(
                    "No fue posible interpretar el "
                            + nombre + ".",
                    error
            );
        }
    }

    // ========================================================
    // 30. CONFIRMACION DE OPERACION
    // ========================================================

    private boolean confirmarOperacion(
            String resumen) {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        resumen
                                + "\n\n¿Confirmar esta operacion?",
                        "MONEDERO / Confirmacion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        return respuesta ==
                JOptionPane.YES_OPTION;
    }

    // ========================================================
    // 31. FINALIZAR OPERACION
    // ========================================================

    private void finalizarOperacion(
            String mensaje) {

        refrescarDatos();

        if (listenerActualizacion != null) {

            try {

                listenerActualizacion.run();

            } catch (RuntimeException error) {

                System.err.println(
                        "[INTERFAZ] No fue posible "
                                + "actualizar una vista: "
                                + mensajeError(error)
                );
            }
        }

        actualizarEstado(
                "OPERACION REALIZADA",
                false
        );

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "MONEDERO / Operacion exitosa",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========================================================
    // 32. MENSAJES DE ERROR
    // ========================================================

    private void mostrarAdvertencia(
            String mensaje) {

        actualizarEstado(
                "REVISA LOS DATOS",
                true
        );

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Validacion de operacion",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarErrorOperacion(
            RuntimeException error) {

        actualizarEstado(
                "OPERACION RECHAZADA",
                true
        );

        System.err.println(
                "[TRANSACCIONES] "
                        + mensajeError(error)
        );

        JOptionPane.showMessageDialog(
                this,
                "No fue posible completar la operacion."
                        + "\n\n"
                        + mensajeError(error),
                "Error financiero",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static String mensajeError(
            Throwable error) {

        if (error == null) {
            return "Error desconocido";
        }

        return error.getMessage() == null
                ? error.getClass().getSimpleName()
                : error.getMessage();
    }

    // ========================================================
    // 33. FORMATEO MONETARIO
    // ========================================================

    private static String formatearMoneda(
            BigDecimal monto) {

        NumberFormat formato =
                NumberFormat.getCurrencyInstance(
                        Locale.forLanguageTag("es-CO")
                );

        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);

        return formato.format(
                monto == null
                        ? BigDecimal.ZERO
                        : monto
        );
    }

    // ========================================================
    // 34. BARRA INFERIOR
    // ========================================================

    private void construirBarraInferior() {

        JPanel barra =
                new JPanel(
                        new BorderLayout(
                                12,
                                12
                        )
                );

        barra.setOpaque(true);

        barra.setBackground(
                Paleta.FONDO_SECUNDARIO
        );

        barra.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1, 0, 0, 0,
                                Paleta.BORDE_SUAVE
                        ),
                        BorderFactory.createEmptyBorder(
                                12, 20, 12, 20
                        )
                )
        );

        JPanel indicadores =
                new JPanel(
                        new GridLayout(
                                1, 2, 14, 0
                        )
                );

        indicadores.setOpaque(false);

        etiquetaOperaciones =
                new JLabel(
                        "TRANSACCIONES: 0"
                );

        etiquetaSaldo =
                new JLabel(
                        "CAPITAL: $ 0,00"
                );

        etiquetaOperaciones.setFont(
                Tipografia.mono(11f)
        );

        etiquetaSaldo.setFont(
                Tipografia.mono(11f)
        );

        etiquetaOperaciones.setForeground(
                Paleta.CHAMPAGNE
        );

        etiquetaSaldo.setForeground(
                Paleta.BONE
        );

        indicadores.add(
                etiquetaOperaciones
        );

        indicadores.add(
                etiquetaSaldo
        );

        etiquetaEstado =
                new JLabel(
                        "SISTEMA LISTO",
                        SwingConstants.RIGHT
                );

        etiquetaEstado.setFont(
                Tipografia.mono(10f)
        );

        etiquetaEstado.setForeground(
                Paleta.SAGE
        );

        barra.add(
                indicadores,
                BorderLayout.CENTER
        );

        barra.add(
                etiquetaEstado,
                BorderLayout.SOUTH
        );

        add(
                barra,
                BorderLayout.SOUTH
        );
    }

    // ========================================================
    // 35. ACTUALIZAR ESTADO
    // ========================================================

    private void actualizarEstado(
            String mensaje,
            boolean error) {

        if (etiquetaEstado == null) {
            return;
        }

        etiquetaEstado.setText(
                mensaje
        );

        etiquetaEstado.setForeground(
                error
                        ? Paleta.ERROR
                        : Paleta.SAGE
        );
    }

    // ========================================================
    // 36. SELECCIONAR OPERACION
    // ========================================================

    /**
     * Permite que VentanaPrincipal
     * abra directamente el formulario
     * solicitado por PanelInicio.
     */
    public void seleccionarOperacion(
            TipoOperacion tipo) {

        Objects.requireNonNull(
                tipo,
                "TipoOperacion no puede ser null"
        );

        if (!SwingUtilities.isEventDispatchThread()) {

            SwingUtilities.invokeLater(
                    () -> seleccionarOperacion(tipo)
            );

            return;
        }

        switch (tipo) {

            case DEPOSITO ->
                    pestanas.setSelectedIndex(0);

            case RETIRO ->
                    pestanas.setSelectedIndex(1);

            case TRANSFERENCIA ->
                    pestanas.setSelectedIndex(2);
        }
    }

    // ========================================================
    // 37. LISTENER DE ACTUALIZACION
    // ========================================================

    public void setListenerActualizacion(
            Runnable listener) {

        this.listenerActualizacion = listener;
    }

    // ========================================================
    // 38. GETTERS
    // ========================================================

    public ContextoAplicacion getContexto() {

        return contexto;
    }

    public EncabezadoPanel getEncabezado() {

        return encabezado;
    }

    public JTabbedPane getPestanas() {

        return pestanas;
    }

    // ========================================================
    // 39. DESCRIPCION
    // ========================================================

    public String generarDescripcion() {

        TipoOperacion actual =
                switch (pestanas.getSelectedIndex()) {

                    case 0 -> TipoOperacion.DEPOSITO;
                    case 1 -> TipoOperacion.RETIRO;
                    default -> TipoOperacion.TRANSFERENCIA;
                };

        return "PanelTransacciones"
                + " | Tema: Institutional Noir"
                + " | Operacion: " + actual
                + " | Estado: "
                + etiquetaEstado.getText()
                + " | Operacion en curso: "
                + operacionEnCurso;
    }
}
