package vista;

import modelo.Cliente;
import modelo.TipoTransaccion;
import modelo.TransaccionProgramada;
import servicios.ContextoAplicacion;
import vista.componentes.BotonPremium;
import vista.componentes.EncabezadoPanel;
import vista.componentes.TablaPremium;
import vista.tema.Espaciado;
import vista.tema.Paleta;
import vista.tema.Tipografia;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * MONEDERO / PRIVATE LEDGER — Institutional Noir.
 * Registro y gestion de operaciones programadas.
 * Los saldos solo pueden cambiar a traves de los servicios financieros.
 */
public final class PanelProgramadas extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_ENTRADA =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_SALIDA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private final ContextoAplicacion contexto;
    private EncabezadoPanel encabezado;
    private JComboBox<TipoTransaccion> selectorTipo;
    private JComboBox<TransaccionProgramada.Frecuencia> selectorFrecuencia;
    private JComboBox<String> selectorClienteOrigen;
    private JComboBox<String> selectorClienteDestino;
    private JTextField campoMonederoOrigen;
    private JTextField campoMonederoDestino;
    private JTextField campoMonto;
    private JTextField campoFecha;
    private JTextField campoBusqueda;
    private JLabel etiquetaOrigen;
    private JLabel etiquetaMonederoOrigen;
    private JLabel indicadorProgramaciones;
    private JLabel indicadorClientes;
    private JLabel etiquetaResultados;
    private JLabel etiquetaEstado;
    private TablaPremium tabla;
    private TablaPremium.ModeloNoEditable modeloTabla;
    private final List<TransaccionProgramada> registros = new ArrayList<>();
    private boolean actualizando;
    private boolean registrosCompletos;
    private Runnable listenerActualizacion;

    public PanelProgramadas(ContextoAplicacion contexto) {
        super(new BorderLayout());
        this.contexto = Objects.requireNonNull(contexto, "ContextoAplicacion obligatorio");
        inicializar();
        refrescarDatos();
    }

    private void inicializar() {
        setBackground(Paleta.FONDO_PRINCIPAL);
        JPanel contenido = new JPanel();
        contenido.setBackground(Paleta.FONDO_PRINCIPAL);
        contenido.setBorder(Espaciado.bordeContenido());
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        encabezado = new EncabezadoPanel(
                "Programadas.",
                "Depositos, transferencias y administracion de ejecuciones futuras.",
                "WORKSPACE / 007",
                EncabezadoPanel.Variante.NORMAL
        );
        encabezado.crearAccionSecundaria("Actualizar", this::refrescarDatos);
        agregarBloque(contenido, encabezado);
        agregarBloque(contenido, construirIndicadores());
        agregarBloque(contenido, construirFormulario());
        agregarBloque(contenido, construirDirectorio());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Paleta.FONDO_PRINCIPAL);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
    }

    private void agregarBloque(JPanel contenedor, JPanel bloque) {
        bloque.setAlignmentX(LEFT_ALIGNMENT);
        bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                bloque.getPreferredSize().height));
        if (contenedor.getComponentCount() > 0) {
            contenedor.add(Box.createVerticalStrut(24));
        }
        contenedor.add(bloque);
    }

    private JPanel superficie() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(Paleta.FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.BORDE_SUAVE),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)));
        return panel;
    }

    private JPanel construirIndicadores() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);
        indicadorProgramaciones = new JLabel("0");
        indicadorClientes = new JLabel("0");
        panel.add(tarjeta("PROGRAMACIONES REGISTRADAS", indicadorProgramaciones));
        panel.add(tarjeta("CLIENTES DISPONIBLES", indicadorClientes));
        return panel;
    }

    private JPanel tarjeta(String titulo, JLabel valor) {
        JPanel p = superficie();
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(Tipografia.mono(10f));
        etiqueta.setForeground(Paleta.CHAMPAGNE);
        valor.setFont(Tipografia.editorial(40f));
        valor.setForeground(Paleta.BONE);
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        columna.add(etiqueta);
        columna.add(Box.createVerticalStrut(12));
        columna.add(valor);
        p.add(columna, BorderLayout.CENTER);
        return p;
    }

    private JPanel construirFormulario() {
        JPanel p = superficie();
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Nueva operacion programada");
        titulo.setFont(Tipografia.tituloTarjeta());
        titulo.setForeground(Paleta.BONE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        columna.add(titulo);
        columna.add(Box.createVerticalStrut(12));
        JLabel ayuda = new JLabel("El registro no mueve dinero hasta su ejecucion por ProgramacionService.");
        ayuda.setFont(Tipografia.cuerpoPequeno());
        ayuda.setForeground(Paleta.ASH);
        ayuda.setAlignmentX(LEFT_ALIGNMENT);
        columna.add(ayuda);
        columna.add(Box.createVerticalStrut(22));

        selectorTipo = new JComboBox<>(new TipoTransaccion[]{
                TipoTransaccion.DEPOSITO, TipoTransaccion.TRANSFERENCIA});
        selectorFrecuencia = new JComboBox<>(TransaccionProgramada.Frecuencia.values());
        selectorClienteOrigen = new JComboBox<>();
        selectorClienteDestino = new JComboBox<>();
        for (JComboBox<?> c : new JComboBox<?>[]{selectorTipo, selectorFrecuencia,
                selectorClienteOrigen, selectorClienteDestino}) {
            c.setFont(Tipografia.cuerpo());
            c.setBackground(Paleta.INPUT_FONDO);
            c.setForeground(Paleta.INPUT_TEXTO);
        }

        campoMonederoOrigen = campo("Ejemplo: M001");
        campoMonederoDestino = campo("Ejemplo: M003");
        campoMonto = campo("Ejemplo: 2000.00");
        campoFecha = campo("AAAA-MM-DD HH:mm");
        reiniciarFecha();

        agregarCampo(columna, "TIPO DE OPERACION", selectorTipo);
        agregarCampo(columna, "FRECUENCIA", selectorFrecuencia);
        etiquetaOrigen = agregarCampo(columna, "CLIENTE ORIGEN", selectorClienteOrigen);
        etiquetaMonederoOrigen = agregarCampo(columna, "MONEDERO ORIGEN", campoMonederoOrigen);
        agregarCampo(columna, "CLIENTE DESTINO / TITULAR", selectorClienteDestino);
        agregarCampo(columna, "MONEDERO DESTINO", campoMonederoDestino);
        agregarCampo(columna, "MONTO / COP", campoMonto);
        agregarCampo(columna, "FECHA Y HORA DE EJECUCION", campoFecha);
        selectorTipo.addActionListener(e -> actualizarFormularioPorTipo());
        actualizarFormularioPorTipo();

        BotonPremium boton = BotonPremium.primario("Revisar y registrar");
        boton.addActionListener(e -> registrarOperacion());
        boton.setAlignmentX(LEFT_ALIGNMENT);
        columna.add(boton);
        p.add(columna, BorderLayout.CENTER);
        return p;
    }

    private JTextField campo(String ejemplo) {
        JTextField t = new JTextField();
        t.setFont(Tipografia.cuerpo());
        t.setBackground(Paleta.INPUT_FONDO);
        t.setForeground(Paleta.INPUT_TEXTO);
        t.setCaretColor(Paleta.CHAMPAGNE);
        t.setToolTipText(ejemplo);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.INPUT_BORDE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        return t;
    }

    private JLabel agregarCampo(JPanel contenedor, String titulo, JComponent componente) {
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(Tipografia.etiqueta());
        etiqueta.setForeground(Paleta.TEXTO_SECUNDARIO);
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        componente.setAlignmentX(LEFT_ALIGNMENT);
        componente.setPreferredSize(new Dimension(440, 42));
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        contenedor.add(etiqueta);
        contenedor.add(Box.createVerticalStrut(8));
        contenedor.add(componente);
        contenedor.add(Box.createVerticalStrut(18));
        return etiqueta;
    }

    private void actualizarFormularioPorTipo() {
        boolean transferencia = selectorTipo.getSelectedItem() == TipoTransaccion.TRANSFERENCIA;
        selectorClienteOrigen.setEnabled(transferencia);
        campoMonederoOrigen.setEnabled(transferencia);
        etiquetaOrigen.setEnabled(transferencia);
        etiquetaMonederoOrigen.setEnabled(transferencia);
        if (!transferencia) campoMonederoOrigen.setText("");
    }

    private JPanel construirDirectorio() {
        JPanel p = superficie();
        JPanel cabecera = new JPanel(new BorderLayout(16, 0));
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("Registro de programaciones");
        titulo.setFont(Tipografia.tituloTarjeta());
        titulo.setForeground(Paleta.BONE);
        campoBusqueda = campo("Buscar ID, cliente, monedero, estado o frecuencia");
        campoBusqueda.setPreferredSize(new Dimension(270, 42));
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscar(); }
            public void removeUpdate(DocumentEvent e) { buscar(); }
            public void changedUpdate(DocumentEvent e) { buscar(); }
        });
        cabecera.add(titulo, BorderLayout.WEST);
        cabecera.add(campoBusqueda, BorderLayout.EAST);
        p.add(cabecera, BorderLayout.NORTH);

        modeloTabla = new TablaPremium.ModeloNoEditable(new String[]{
                "ID", "TIPO", "CLIENTES", "MONEDEROS", "MONTO / COP",
                "FECHA PROGRAMADA", "FRECUENCIA", "ESTADO"}, 0);
        tabla = new TablaPremium(modeloTabla);
        tabla.setMensajeVacio("No hay programaciones visibles");
        tabla.setFilasAlternas(true);
        tabla.configurarAnchoColumna(0, 150);
        tabla.configurarAnchoColumna(1, 135);
        tabla.configurarAnchoColumna(2, 160);
        tabla.configurarAnchoColumna(3, 160);
        tabla.configurarAnchoColumna(4, 150);
        tabla.configurarAnchoColumna(5, 160);
        tabla.configurarAnchoColumna(6, 115);
        tabla.configurarAnchoColumna(7, 125);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setPreferredScrollableViewportSize(new Dimension(900, 300));
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(900, 330));
        p.add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(0, 12));
        pie.setOpaque(false);
        etiquetaResultados = new JLabel("0 registros");
        etiquetaResultados.setFont(Tipografia.cuerpoPequeno());
        etiquetaResultados.setForeground(Paleta.ASH);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        acciones.setOpaque(false);
        botonAccion(acciones, "Ver detalle", this::mostrarDetalleSeleccionado, 0);
        botonAccion(acciones, "Pausar", this::pausarSeleccionada, 1);
        botonAccion(acciones, "Reanudar", this::reanudarSeleccionada, 1);
        botonAccion(acciones, "Reprogramar", this::reprogramarSeleccionada, 1);
        botonAccion(acciones, "Cancelar", this::cancelarSeleccionada, 1);
        botonAccion(acciones, "Ejecutar vencidas", this::ejecutarVencidas, 1);

        etiquetaEstado = new JLabel("MODULO PREPARADO");
        etiquetaEstado.setFont(Tipografia.mono(10f));
        etiquetaEstado.setForeground(Paleta.ASH);
        pie.add(etiquetaResultados, BorderLayout.NORTH);
        pie.add(acciones, BorderLayout.CENTER);
        pie.add(etiquetaEstado, BorderLayout.SOUTH);
        p.add(pie, BorderLayout.SOUTH);
        return p;
    }

    private void botonAccion(JPanel destino, String etiqueta, Runnable accion, int secundario) {
        BotonPremium boton = secundario == 0
                ? BotonPremium.primario(etiqueta)
                : BotonPremium.secundario(etiqueta);
        boton.addActionListener(e -> accion.run());
        destino.add(boton);
    }

    public void refrescarDatos() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refrescarDatos);
            return;
        }
        if (actualizando) return;
        actualizando = true;
        try {
            int total = contexto.getProgramacionService().cantidadProgramaciones();
            int clientes = contexto.getClienteService().cantidadClientes();
            indicadorProgramaciones.setText(Integer.toString(total));
            indicadorClientes.setText(Integer.toString(clientes));
            actualizarSelectoresClientes();

            List<TransaccionProgramada> lista = consultarProgramaciones();
            registros.clear();
            modeloTabla.setRowCount(0);
            for (TransaccionProgramada p : lista) {
                registros.add(p);
                modeloTabla.addRow(new Object[]{
                        p.getId(), p.getTipo().name(), participantes(p, true),
                        participantes(p, false), formatearMoneda(p.getMonto()),
                        p.getFechaFormateada(), p.getFrecuencia().name(), p.getEstado().name()
                });
            }
            buscar();
            registrosCompletos = lista.size() == total;
            estado(registrosCompletos ? "PROGRAMACIONES ACTUALIZADAS"
                            : "LECTURA INCOMPLETA: " + lista.size() + " / " + total,
                    !registrosCompletos);
        } catch (RuntimeException ex) {
            registrosCompletos = false;
            estado("ERROR DE CONSULTA: " + mensaje(ex), true);
            System.err.println("[PROGRAMADAS] " + mensaje(ex));
        } finally {
            actualizando = false;
        }
    }

    private List<TransaccionProgramada> consultarProgramaciones() {
        List<TransaccionProgramada> resultado = new ArrayList<>();
        contexto.getProgramacionService().listarProgramaciones()
                .recorrer(p -> { if (p != null) resultado.add(p); });
        resultado.sort(Comparator.comparing(TransaccionProgramada::getFechaEjecucion)
                .thenComparing(TransaccionProgramada::getId));
        return resultado;
    }

    private void actualizarSelectoresClientes() {
        Object origen = selectorClienteOrigen.getSelectedItem();
        Object destino = selectorClienteDestino.getSelectedItem();
        List<String> ids = new ArrayList<>();
        contexto.getClienteService().recorrerClientes(c -> {
            if (c != null && c.isActivo()) ids.add(c.getId());
        });
        ids.sort(String::compareTo);
        selectorClienteOrigen.removeAllItems();
        selectorClienteDestino.removeAllItems();
        for (String id : ids) {
            selectorClienteOrigen.addItem(id);
            selectorClienteDestino.addItem(id);
        }
        restaurarSeleccion(selectorClienteOrigen, origen);
        restaurarSeleccion(selectorClienteDestino, destino);
    }

    private void restaurarSeleccion(JComboBox<String> combo, Object anterior) {
        if (anterior == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (anterior.equals(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private String participantes(TransaccionProgramada p, boolean clientes) {
        String origen = clientes ? p.getIdClienteOrigen() : p.getIdMonederoOrigen();
        String destino = clientes ? p.getIdClienteDestino() : p.getIdMonederoDestino();
        if (p.getTipo() == TipoTransaccion.DEPOSITO) return visible(destino);
        return visible(origen) + " -> " + visible(destino);
    }

    private static String visible(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }

    /** Conservado por compatibilidad con la version anterior. */
    public void programarDeposito() {
        if (selectorTipo != null) selectorTipo.setSelectedItem(TipoTransaccion.DEPOSITO);
        registrarOperacion();
    }

    public void registrarOperacion() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::registrarOperacion);
            return;
        }
        try {
            TipoTransaccion tipo = (TipoTransaccion) selectorTipo.getSelectedItem();
            TransaccionProgramada.Frecuencia frecuencia =
                    (TransaccionProgramada.Frecuencia) selectorFrecuencia.getSelectedItem();
            if (tipo == null || frecuencia == null) throw new IllegalArgumentException("Selecciona tipo y frecuencia.");
            String clienteDestino = seleccionado(selectorClienteDestino, "cliente destino");
            String monederoDestino = idMonedero(campoMonederoDestino.getText());
            BigDecimal monto = interpretarMonto(campoMonto.getText());
            LocalDateTime fecha = interpretarFecha(campoFecha.getText());
            validarFutura(fecha);
            String clienteOrigen = null;
            String monederoOrigen = null;
            if (tipo == TipoTransaccion.TRANSFERENCIA) {
                clienteOrigen = seleccionado(selectorClienteOrigen, "cliente origen");
                monederoOrigen = idMonedero(campoMonederoOrigen.getText());
                if (monederoOrigen.equals(monederoDestino)) {
                    throw new IllegalArgumentException("Los monederos de origen y destino deben ser distintos.");
                }
            }
            // Validar los participantes antes de presentar la confirmacion.
            contexto.getClienteService().obtenerMonederoActivo(clienteDestino, monederoDestino);
            if (tipo == TipoTransaccion.TRANSFERENCIA) {
                contexto.getClienteService().obtenerMonederoActivo(clienteOrigen, monederoOrigen);
            }
            String resumen = "TIPO: " + tipo + "\nFRECUENCIA: " + frecuencia
                    + "\nORIGEN: " + visible(clienteOrigen) + " / " + visible(monederoOrigen)
                    + "\nDESTINO: " + clienteDestino + " / " + monederoDestino
                    + "\nMONTO: " + formatearMoneda(monto)
                    + "\nPRIMERA FECHA: " + fecha.format(FORMATO_SALIDA)
                    + "\n\nEl dinero solo se movera cuando se procese la programacion."
                    + "\n\n¿Confirmar registro?";
            if (JOptionPane.showConfirmDialog(this, resumen, "Confirmar programacion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) return;
            validarFutura(fecha);
            if (tipo == TipoTransaccion.DEPOSITO) {
                contexto.getProgramacionService().programarDeposito(
                        clienteDestino, monederoDestino, monto, fecha, frecuencia);
            } else {
                contexto.getProgramacionService().programarTransferencia(
                        clienteOrigen, monederoOrigen, clienteDestino, monederoDestino,
                        monto, fecha, frecuencia);
            }
            refrescarYNotificar();
            campoMonto.setText("");
            reiniciarFecha();
            JOptionPane.showMessageDialog(this, "Programacion registrada correctamente.",
                    "MONEDERO / Programadas", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            error("No se pudo registrar la programacion", ex);
        }
    }

    private String seleccionado(JComboBox<String> combo, String campo) {
        Object valor = combo.getSelectedItem();
        if (valor == null || valor.toString().isBlank()) {
            throw new IllegalArgumentException("Selecciona " + campo + ".");
        }
        return valor.toString();
    }

    private String idMonedero(String texto) {
        String id = texto == null ? "" : texto.trim();
        if (id.isBlank() || id.length() > 30 || !id.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("ID de monedero invalido (letras, numeros, _ o -; maximo 30).");
        }
        return id;
    }

    private BigDecimal interpretarMonto(String texto) {
        String limpio = texto == null ? "" : texto.trim().replace(',', '.');
        if (!limpio.matches("\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("Monto invalido. Ejemplo: 2000.50");
        }
        BigDecimal monto = new BigDecimal(limpio).setScale(2, RoundingMode.UNNECESSARY);
        if (monto.signum() <= 0) throw new IllegalArgumentException("El monto debe ser positivo.");
        return monto;
    }

    private LocalDateTime interpretarFecha(String texto) {
        try {
            return LocalDateTime.parse(texto == null ? "" : texto.trim(), FORMATO_ENTRADA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha invalida. Utiliza AAAA-MM-DD HH:mm.", ex);
        }
    }

    private void validarFutura(LocalDateTime fecha) {
        if (!fecha.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha debe ser futura.");
        }
    }

    private void reiniciarFecha() {
        campoFecha.setText(LocalDateTime.now().plusDays(3)
                .withSecond(0).withNano(0).format(FORMATO_ENTRADA));
    }

    private TransaccionProgramada programacionSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) throw new IllegalArgumentException("Selecciona primero una programacion de la tabla.");
        int modelo = tabla.convertRowIndexToModel(fila);
        if (modelo < 0 || modelo >= registros.size()) {
            throw new IllegalStateException("La seleccion ya no corresponde al directorio.");
        }
        String id = registros.get(modelo).getId();
        TransaccionProgramada p = contexto.getProgramacionService().buscarProgramacion(id);
        if (p == null) throw new IllegalStateException("La programacion ya no existe.");
        return p;
    }

    private void ejecutarAccion(String titulo, TransaccionProgramada.Estado requerido,
                                java.util.function.Consumer<String> accion) {
        try {
            TransaccionProgramada p = programacionSeleccionada();
            if (p.getEstado() != requerido) {
                throw new IllegalStateException("Esta accion requiere estado " + requerido
                        + ". Estado actual: " + p.getEstado());
            }
            String pregunta = titulo + " la programacion:\n\n" + p.getId()
                    + "\n" + p.getTipo() + " / " + formatearMoneda(p.getMonto())
                    + "\n\n¿Confirmar?";
            if (JOptionPane.showConfirmDialog(this, pregunta, "Confirmar " + titulo.toLowerCase(Locale.ROOT),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
            accion.accept(p.getId());
            refrescarYNotificar();
            estado("OPERACION REALIZADA: " + titulo.toUpperCase(Locale.ROOT), false);
        } catch (RuntimeException ex) {
            error("No fue posible " + titulo.toLowerCase(Locale.ROOT), ex);
        }
    }

    public void pausarSeleccionada() {
        ejecutarAccion("Pausar", TransaccionProgramada.Estado.ACTIVA,
                id -> contexto.getProgramacionService().pausarProgramacion(id));
    }

    public void reanudarSeleccionada() {
        ejecutarAccion("Reanudar", TransaccionProgramada.Estado.PAUSADA,
                id -> contexto.getProgramacionService().reanudarProgramacion(id));
    }

    public void cancelarSeleccionada() {
        try {
            TransaccionProgramada p = programacionSeleccionada();
            if (p.estaFinalizada()) throw new IllegalStateException("La programacion ya esta finalizada.");
            if (JOptionPane.showConfirmDialog(this,
                    "Cancelar definitivamente esta programacion?\n\n" + p.getId()
                            + "\nLa operacion permanecera en el historial.",
                    "Confirmar cancelacion", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
            contexto.getProgramacionService().cancelarProgramacion(p.getId());
            refrescarYNotificar();
            estado("PROGRAMACION CANCELADA", false);
        } catch (RuntimeException ex) {
            error("No fue posible cancelar", ex);
        }
    }

    public void reprogramarSeleccionada() {
        try {
            TransaccionProgramada p = programacionSeleccionada();
            if (p.estaFinalizada()) throw new IllegalStateException("No se puede reprogramar una operacion finalizada.");
            String inicial = p.getFechaEjecucion().format(FORMATO_ENTRADA);
            String entrada = JOptionPane.showInputDialog(this,
                    "Nueva fecha (AAAA-MM-DD HH:mm):\n" + p.getId(), inicial);
            if (entrada == null) return;
            LocalDateTime nueva = interpretarFecha(entrada);
            validarFutura(nueva);
            if (JOptionPane.showConfirmDialog(this,
                    "Cambiar fecha al " + nueva.format(FORMATO_SALIDA) + "?",
                    "Confirmar nueva fecha", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) return;
            validarFutura(nueva);
            contexto.getProgramacionService().reprogramar(p.getId(), nueva);
            refrescarYNotificar();
            estado("FECHA ACTUALIZADA", false);
        } catch (RuntimeException ex) {
            error("No fue posible reprogramar", ex);
        }
    }

    public void ejecutarVencidas() {
        try {
            int vencidas = contexto.getProgramacionService().cantidadVencidas();
            if (vencidas == 0) {
                JOptionPane.showMessageDialog(this, "No hay operaciones vencidas para ejecutar.",
                        "Programaciones", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this,
                    "Se intentaran procesar " + vencidas + " programacion(es) vencida(s)."
                            + "\nEsta accion puede modificar saldos y generar transacciones."
                            + "\n\n¿Continuar?",
                    "Ejecutar operaciones vencidas", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
            final int[] exitosas = {0};
            final int[] fallidas = {0};
            StringBuilder detalle = new StringBuilder();
            contexto.getProgramacionService().ejecutarPendientes()
                    .recorrer(r -> {
                        if (r.isExitosa()) exitosas[0]++; else fallidas[0]++;
                        detalle.append(r.getIdProgramacion()).append(" : ")
                                .append(r.isExitosa() ? "OK" : "FALLO")
                                .append(" - ").append(r.getMensaje()).append('\n');
                    });
            refrescarYNotificar();
            estado("EJECUCIONES: " + exitosas[0] + " OK / " + fallidas[0] + " FALLIDAS",
                    fallidas[0] > 0);
            JTextArea area = new JTextArea("Exitosas: " + exitosas[0]
                    + "\nFallidas: " + fallidas[0] + "\n\n" + detalle, 13, 65);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "Resultado de ejecucion", fallidas[0] > 0
                            ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            error("No fue posible procesar las operaciones vencidas", ex);
        }
    }

    public void mostrarDetalleSeleccionado() {
        try {
            TransaccionProgramada p = programacionSeleccionada();
            String ultima = p.getUltimaEjecucion() == null ? "SIN EJECUCIONES"
                    : p.getUltimaEjecucion().format(FORMATO_SALIDA);
            String texto = "MONEDERO / PRIVATE LEDGER\n"
                    + "========================================\n"
                    + "ID: " + p.getId() + "\n"
                    + "TIPO: " + p.getTipo() + "\n"
                    + "ESTADO: " + p.getEstado() + "\n"
                    + "FRECUENCIA: " + p.getFrecuencia() + "\n"
                    + "CLIENTE ORIGEN: " + visible(p.getIdClienteOrigen()) + "\n"
                    + "MONEDERO ORIGEN: " + visible(p.getIdMonederoOrigen()) + "\n"
                    + "CLIENTE DESTINO: " + visible(p.getIdClienteDestino()) + "\n"
                    + "MONEDERO DESTINO: " + visible(p.getIdMonederoDestino()) + "\n"
                    + "MONTO: " + formatearMoneda(p.getMonto()) + "\n"
                    + "FECHA PROGRAMADA: " + p.getFechaFormateada() + "\n"
                    + "EJECUCIONES: " + p.getEjecucionesRealizadas() + "\n"
                    + "ULTIMA EJECUCION: " + ultima + "\n"
                    + "DESCRIPCION: " + p.getDescripcion();
            JTextArea area = new JTextArea(texto, 22, 60);
            area.setEditable(false);
            area.setFont(Tipografia.mono(12f));
            area.setCaretPosition(0);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "MONEDERO / Detalle de programacion", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            error("No fue posible consultar el detalle", ex);
        }
    }

    private void buscar() {
        if (tabla == null) return;
        tabla.buscar(campoBusqueda.getText());
        etiquetaResultados.setText("Mostrando " + tabla.getRowCount()
                + " de " + modeloTabla.getRowCount() + " registros");
    }

    public void buscarProgramaciones(String texto) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> buscarProgramaciones(texto));
            return;
        }
        campoBusqueda.setText(texto == null ? "" : texto);
    }

    private void refrescarYNotificar() {
        refrescarDatos();
        if (listenerActualizacion != null) {
            try {
                listenerActualizacion.run();
            } catch (RuntimeException ex) {
                System.err.println("[PROGRAMADAS] Error refrescando otras vistas: " + mensaje(ex));
            }
        }
    }

    private void estado(String texto, boolean advertencia) {
        if (etiquetaEstado != null) {
            etiquetaEstado.setText(texto);
            etiquetaEstado.setForeground(advertencia ? Paleta.ADVERTENCIA : Paleta.SAGE);
        }
    }

    private void error(String descripcion, RuntimeException ex) {
        estado("ERROR: " + descripcion, true);
        JOptionPane.showMessageDialog(this, descripcion + ":\n" + mensaje(ex),
                "MONEDERO / Error", JOptionPane.ERROR_MESSAGE);
    }

    private static String mensaje(Throwable ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }

    private static String formatearMoneda(BigDecimal monto) {
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
        n.setMinimumFractionDigits(2);
        n.setMaximumFractionDigits(2);
        return n.format(monto);
    }

    public void setListenerActualizacion(Runnable listener) {
        this.listenerActualizacion = listener;
    }

    public ContextoAplicacion getContexto() { return contexto; }
    public EncabezadoPanel getEncabezado() { return encabezado; }
    public TablaPremium getTablaProgramaciones() { return tabla; }
    public boolean isRegistrosCompletos() { return registrosCompletos; }
    public int getRegistrosVisibles() { return tabla.getRowCount(); }
    public String generarDescripcion() {
        return "PanelProgramadas | Tema: Institutional Noir | Programaciones: "
                + indicadorProgramaciones.getText() + " | Registros: " + registros.size()
                + " | Lectura completa: " + registrosCompletos;
    }
}
