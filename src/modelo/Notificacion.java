
package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Representa una notificacion dirigida
 * a un cliente del Monedero Virtual.
 *
 * Permite gestionar:
 * - Alertas de saldo bajo.
 * - Depositos y retiros.
 * - Transferencias.
 * - Puntos acumulados.
 * - Cambios de rango.
 * - Transacciones programadas.
 * - Avisos generales del sistema.
 *
 * Las notificaciones seran administradas
 * mediante nuestra ListaCircular propia.
 */
public class Notificacion {

    // ==========================================
    // TIPOS DE NOTIFICACION
    // ==========================================

    public enum Tipo {

        SALDO_BAJO("Saldo bajo"),

        DEPOSITO("Deposito realizado"),

        RETIRO("Retiro realizado"),

        TRANSFERENCIA_ENVIADA("Transferencia enviada"),

        TRANSFERENCIA_RECIBIDA("Transferencia recibida"),

        PUNTOS_GANADOS("Puntos ganados"),

        CANJE_REALIZADO("Canje realizado"),

        CAMBIO_RANGO("Cambio de rango"),

        TRANSACCION_PROGRAMADA("Transaccion programada"),

        RECORDATORIO("Recordatorio"),

        TRANSACCION_FALLIDA("Transaccion fallida"),

        TRANSACCION_REVERTIDA("Transaccion revertida"),

        SISTEMA("Notificacion del sistema");

        private final String descripcion;

        Tipo(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    // ==========================================
    // PRIORIDADES
    // ==========================================

    public enum Prioridad {

        BAJA,
        MEDIA,
        ALTA,
        CRITICA
    }

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final String id;

    private final String idCliente;

    private final Tipo tipo;

    private final String titulo;

    private final String mensaje;

    private final Prioridad prioridad;

    private final LocalDateTime fechaCreacion;

    private boolean leida;

    private LocalDateTime fechaLectura;

    private final String idTransaccion;

    // ==========================================
    // CONSTRUCTOR PRINCIPAL
    // ==========================================

    public Notificacion(
            String idCliente,
            Tipo tipo,
            String titulo,
            String mensaje,
            Prioridad prioridad,
            String idTransaccion) {

        validarTexto(idCliente, "ID del cliente");
        validarTexto(titulo, "Titulo");
        validarTexto(mensaje, "Mensaje");

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de notificacion es obligatorio"
            );
        }

        if (prioridad == null) {
            throw new IllegalArgumentException(
                    "La prioridad es obligatoria"
            );
        }

        this.id = UUID.randomUUID().toString();

        this.idCliente = idCliente;
        this.tipo = tipo;

        this.titulo = titulo;
        this.mensaje = mensaje;

        this.prioridad = prioridad;

        this.fechaCreacion = LocalDateTime.now();

        this.leida = false;
        this.fechaLectura = null;

        this.idTransaccion = idTransaccion;
    }

    // ==========================================
    // CONSTRUCTOR SIMPLIFICADO
    // ==========================================

    public Notificacion(
            String idCliente,
            Tipo tipo,
            String titulo,
            String mensaje) {

        this(
                idCliente,
                tipo,
                titulo,
                mensaje,
                Prioridad.MEDIA,
                null
        );
    }

    // ==========================================
    // METODOS DE CREACION
    // ==========================================

    /**
     * Crea una alerta de saldo bajo.
     */
    public static Notificacion crearSaldoBajo(
            String idCliente,
            String nombreMonedero) {

        return new Notificacion(
                idCliente,
                Tipo.SALDO_BAJO,
                "Alerta de saldo bajo",
                "El monedero " + nombreMonedero
                        + " tiene un saldo bajo.",
                Prioridad.ALTA,
                null
        );
    }

    /**
     * Notifica una transferencia enviada.
     */
    public static Notificacion crearTransferenciaEnviada(
            String idCliente,
            String monto,
            String idTransaccion) {

        return new Notificacion(
                idCliente,
                Tipo.TRANSFERENCIA_ENVIADA,
                "Transferencia realizada",
                "Has enviado $" + monto
                        + " correctamente.",
                Prioridad.MEDIA,
                idTransaccion
        );
    }

    /**
     * Notifica una transferencia recibida.
     */
    public static Notificacion crearTransferenciaRecibida(
            String idCliente,
            String monto,
            String idTransaccion) {

        return new Notificacion(
                idCliente,
                Tipo.TRANSFERENCIA_RECIBIDA,
                "Dinero recibido",
                "Has recibido $" + monto
                        + " en tu monedero.",
                Prioridad.MEDIA,
                idTransaccion
        );
    }

    /**
     * Notifica puntos ganados.
     */
    public static Notificacion crearPuntosGanados(
            String idCliente,
            int puntos,
            String idTransaccion) {

        if (puntos <= 0) {
            throw new IllegalArgumentException(
                    "Los puntos deben ser positivos"
            );
        }

        return new Notificacion(
                idCliente,
                Tipo.PUNTOS_GANADOS,
                "Nuevos puntos acumulados",
                "Has ganado " + puntos
                        + " puntos por tu transaccion.",
                Prioridad.BAJA,
                idTransaccion
        );
    }

    /**
     * Notifica un cambio de rango.
     */
    public static Notificacion crearCambioRango(
            String idCliente,
            Rango nuevoRango) {

        if (nuevoRango == null) {
            throw new IllegalArgumentException(
                    "El rango no puede ser null"
            );
        }

        return new Notificacion(
                idCliente,
                Tipo.CAMBIO_RANGO,
                "Actualizacion de rango",
                "Tu nuevo rango es "
                        + nuevoRango.getNombre() + ".",
                Prioridad.ALTA,
                null
        );
    }

    /**
     * Crea un recordatorio de una
     * transaccion programada.
     */
    public static Notificacion crearRecordatorio(
            String idCliente,
            String descripcion) {

        validarTexto(descripcion, "Descripcion");

        return new Notificacion(
                idCliente,
                Tipo.RECORDATORIO,
                "Transaccion pendiente",
                descripcion,
                Prioridad.MEDIA,
                null
        );
    }

    /**
     * Notifica una operacion fallida.
     */
    public static Notificacion crearTransaccionFallida(
            String idCliente,
            String motivo,
            String idTransaccion) {

        validarTexto(motivo, "Motivo");

        return new Notificacion(
                idCliente,
                Tipo.TRANSACCION_FALLIDA,
                "Operacion fallida",
                motivo,
                Prioridad.ALTA,
                idTransaccion
        );
    }

    /**
     * Notifica una transaccion revertida.
     */
    public static Notificacion crearReversion(
            String idCliente,
            String idTransaccion) {

        validarTexto(
                idTransaccion,
                "ID de la transaccion"
        );

        return new Notificacion(
                idCliente,
                Tipo.TRANSACCION_REVERTIDA,
                "Transaccion revertida",
                "Se ha revertido la transaccion "
                        + idTransaccion + ".",
                Prioridad.ALTA,
                idTransaccion
        );
    }

    // ==========================================
    // METODOS GET
    // ==========================================

    public String getId() {
        return id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean isLeida() {
        return leida;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    // ==========================================
    // CONTROL DE LECTURA
    // ==========================================

    /**
     * Marca la notificacion como leida.
     */
    public void marcarLeida() {

        if (!leida) {
            leida = true;
            fechaLectura = LocalDateTime.now();
        }
    }

    /**
     * Marca la notificacion como no leida.
     */
    public void marcarNoLeida() {

        leida = false;
        fechaLectura = null;
    }

    /**
     * Verifica si requiere atencion prioritaria.
     */
    public boolean esUrgente() {

        return prioridad == Prioridad.ALTA
                || prioridad == Prioridad.CRITICA;
    }

    /**
     * Indica si esta relacionada
     * con una transaccion especifica.
     */
    public boolean tieneTransaccionAsociada() {

        return idTransaccion != null
                && !idTransaccion.isBlank();
    }

    // ==========================================
    // FORMATO
    // ==========================================

    public String getFechaFormateada() {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss"
                );

        return fechaCreacion.format(formato);
    }

    /**
     * Obtiene un resumen para mostrar
     * en la interfaz grafica.
     */
    public String getResumen() {

        String estado = leida
                ? "[LEIDA]"
                : "[NUEVA]";

        return estado
                + " [" + prioridad + "] "
                + titulo
                + " - " + mensaje;
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private static void validarTexto(
            String texto,
            String campo) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " no puede estar vacio"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return "Notificacion{" +
                "id='" + id + '\'' +
                ", cliente='" + idCliente + '\'' +
                ", tipo=" + tipo +
                ", titulo='" + titulo + '\'' +
                ", prioridad=" + prioridad +
                ", leida=" + leida +
                ", fecha=" + getFechaFormateada() +
                '}';
    }
}
