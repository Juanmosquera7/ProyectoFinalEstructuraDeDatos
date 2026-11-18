
package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Representa una transaccion que sera ejecutada
 * en una fecha determinada.
 *
 * Permite programaciones:
 * - Unicas.
 * - Diarias.
 * - Semanales.
 * - Mensuales.
 *
 * Se utilizara con nuestra ColaPrioridad propia.
 *
 * Esta clase administra la programacion,
 * pero no modifica directamente el dinero.
 */
public class TransaccionProgramada
        implements Comparable<TransaccionProgramada> {

    // ==========================================
    // FRECUENCIAS DISPONIBLES
    // ==========================================

    public enum Frecuencia {

        UNICA,
        DIARIA,
        SEMANAL,
        MENSUAL
    }

    // ==========================================
    // ESTADOS DISPONIBLES
    // ==========================================

    public enum Estado {

        ACTIVA,
        PAUSADA,
        COMPLETADA,
        CANCELADA
    }

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final String id;

    private final TipoTransaccion tipo;

    private final String idClienteOrigen;
    private final String idClienteDestino;

    private final String idMonederoOrigen;
    private final String idMonederoDestino;

    private final BigDecimal monto;

    private LocalDateTime fechaEjecucion;

    private final LocalDateTime fechaCreacion;

    private final Frecuencia frecuencia;

    private Estado estado;

    private int ejecucionesRealizadas;

    private LocalDateTime ultimaEjecucion;

    private final String descripcion;

    // ==========================================
    // CONSTRUCTOR PRINCIPAL
    // ==========================================

    public TransaccionProgramada(
            TipoTransaccion tipo,
            String idClienteOrigen,
            String idClienteDestino,
            String idMonederoOrigen,
            String idMonederoDestino,
            BigDecimal monto,
            LocalDateTime fechaEjecucion,
            Frecuencia frecuencia,
            String descripcion) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de transaccion es obligatorio"
            );
        }

        if (tipo != TipoTransaccion.DEPOSITO
                && tipo != TipoTransaccion.TRANSFERENCIA) {

            throw new IllegalArgumentException(
                    "Solo se pueden programar depositos "
                            + "y transferencias"
            );
        }

        if (frecuencia == null) {
            throw new IllegalArgumentException(
                    "La frecuencia es obligatoria"
            );
        }

        validarIdentificadores(
                tipo,
                idClienteOrigen,
                idClienteDestino,
                idMonederoOrigen,
                idMonederoDestino
        );

        this.monto = validarMonto(monto);

        if (fechaEjecucion == null) {
            throw new IllegalArgumentException(
                    "La fecha de ejecucion es obligatoria"
            );
        }

        if (!fechaEjecucion.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La fecha de ejecucion debe ser futura"
            );
        }

        this.id = UUID.randomUUID().toString();

        this.tipo = tipo;

        this.idClienteOrigen = idClienteOrigen;
        this.idClienteDestino = idClienteDestino;

        this.idMonederoOrigen = idMonederoOrigen;
        this.idMonederoDestino = idMonederoDestino;

        this.fechaEjecucion = fechaEjecucion;
        this.fechaCreacion = LocalDateTime.now();

        this.frecuencia = frecuencia;
        this.descripcion = descripcion == null
                ? ""
                : descripcion;

        this.estado = Estado.ACTIVA;
        this.ejecucionesRealizadas = 0;
        this.ultimaEjecucion = null;
    }

    // ==========================================
    // METODOS DE CREACION
    // ==========================================

    /**
     * Crea un deposito programado.
     */
    public static TransaccionProgramada crearDeposito(
            String idCliente,
            String idMonedero,
            BigDecimal monto,
            LocalDateTime fecha,
            Frecuencia frecuencia) {

        return new TransaccionProgramada(
                TipoTransaccion.DEPOSITO,
                null,
                idCliente,
                null,
                idMonedero,
                monto,
                fecha,
                frecuencia,
                "Deposito programado"
        );
    }

    /**
     * Crea una transferencia programada.
     */
    public static TransaccionProgramada crearTransferencia(
            String idClienteOrigen,
            String idClienteDestino,
            String idMonederoOrigen,
            String idMonederoDestino,
            BigDecimal monto,
            LocalDateTime fecha,
            Frecuencia frecuencia) {

        return new TransaccionProgramada(
                TipoTransaccion.TRANSFERENCIA,
                idClienteOrigen,
                idClienteDestino,
                idMonederoOrigen,
                idMonederoDestino,
                monto,
                fecha,
                frecuencia,
                "Transferencia programada"
        );
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    public String getId() {
        return id;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public String getIdClienteOrigen() {
        return idClienteOrigen;
    }

    public String getIdClienteDestino() {
        return idClienteDestino;
    }

    public String getIdMonederoOrigen() {
        return idMonederoOrigen;
    }

    public String getIdMonederoDestino() {
        return idMonederoDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Frecuencia getFrecuencia() {
        return frecuencia;
    }

    public Estado getEstado() {
        return estado;
    }

    public int getEjecucionesRealizadas() {
        return ejecucionesRealizadas;
    }

    public LocalDateTime getUltimaEjecucion() {
        return ultimaEjecucion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // ==========================================
    // CONTROL DE LA PROGRAMACION
    // ==========================================

    /**
     * Determina si la transaccion debe ejecutarse.
     *
     * Solo devuelve true cuando esta activa
     * y su fecha ya llego.
     */
    public boolean estaListaParaEjecutar() {

        return estado == Estado.ACTIVA
                && !fechaEjecucion.isAfter(
                LocalDateTime.now()
        );
    }

    /**
     * Registra una ejecucion exitosa.
     *
     * Si es unica, se marca como completada.
     * Si es recurrente, calcula la proxima fecha.
     *
     * Debe llamarse unicamente despues de que
     * TransaccionService confirme la operacion.
     */
    public void registrarEjecucionExitosa() {

        if (estado != Estado.ACTIVA) {
            throw new IllegalStateException(
                    "La programacion no esta activa"
            );
        }

        if (fechaEjecucion.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "La fecha de ejecucion aun no ha llegado"
            );
        }

        LocalDateTime fechaAnterior = fechaEjecucion;

        ejecucionesRealizadas++;
        ultimaEjecucion = LocalDateTime.now();

        if (frecuencia == Frecuencia.UNICA) {

            estado = Estado.COMPLETADA;

        } else {

            fechaEjecucion = calcularSiguienteFecha(
                    fechaAnterior
            );

            // Si habia ejecuciones atrasadas,
            // saltar hasta la proxima fecha futura.
            while (!fechaEjecucion.isAfter(
                    ultimaEjecucion)) {

                fechaEjecucion = calcularSiguienteFecha(
                        fechaEjecucion
                );
            }
        }
    }

    /**
     * Calcula la siguiente fecha de una
     * transaccion recurrente.
     */
    private LocalDateTime calcularSiguienteFecha(
            LocalDateTime fecha) {

        return switch (frecuencia) {

            case DIARIA -> fecha.plusDays(1);

            case SEMANAL -> fecha.plusWeeks(1);

            case MENSUAL -> fecha.plusMonths(1);

            case UNICA -> fecha;
        };
    }

    /**
     * Pausa una programacion activa.
     */
    public void pausar() {

        if (estado != Estado.ACTIVA) {
            throw new IllegalStateException(
                    "Solo se pueden pausar "
                            + "programaciones activas"
            );
        }

        estado = Estado.PAUSADA;
    }

    /**
     * Reactiva una programacion pausada.
     *
     * Si la fecha ya paso, se reprograma
     * para una fecha futura.
     */
    public void reanudar() {

        if (estado != Estado.PAUSADA) {
            throw new IllegalStateException(
                    "Solo se pueden reanudar "
                            + "programaciones pausadas"
            );
        }

        if (!fechaEjecucion.isAfter(
                LocalDateTime.now())) {

            if (frecuencia == Frecuencia.UNICA) {

                fechaEjecucion =
                        LocalDateTime.now().plusMinutes(1);

            } else {

                while (!fechaEjecucion.isAfter(
                        LocalDateTime.now())) {

                    fechaEjecucion = calcularSiguienteFecha(
                            fechaEjecucion
                    );
                }
            }
        }

        estado = Estado.ACTIVA;
    }

    /**
     * Cancela una programacion activa o pausada.
     */
    public void cancelar() {

        if (estado == Estado.COMPLETADA
                || estado == Estado.CANCELADA) {

            throw new IllegalStateException(
                    "La programacion ya esta finalizada"
            );
        }

        estado = Estado.CANCELADA;
    }

    /**
     * Modifica la fecha de una programacion.
     *
     * Solo permite fechas futuras.
     */
    public void reprogramar(LocalDateTime nuevaFecha) {

        if (estado != Estado.ACTIVA
                && estado != Estado.PAUSADA) {

            throw new IllegalStateException(
                    "No se puede reprogramar "
                            + "una operacion finalizada"
            );
        }

        if (nuevaFecha == null
                || !nuevaFecha.isAfter(
                LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "La nueva fecha debe ser futura"
            );
        }

        fechaEjecucion = nuevaFecha;
    }

    // ==========================================
    // METODOS DE UTILIDAD
    // ==========================================

    public boolean esRecurrente() {
        return frecuencia != Frecuencia.UNICA;
    }

    public boolean estaActiva() {
        return estado == Estado.ACTIVA;
    }

    public boolean estaFinalizada() {

        return estado == Estado.COMPLETADA
                || estado == Estado.CANCELADA;
    }

    /**
     * Permite ordenar programaciones
     * segun su fecha de ejecucion.
     *
     * Si tienen la misma fecha,
     * utiliza el identificador como desempate.
     */
    @Override
    public int compareTo(
            TransaccionProgramada otra) {

        if (otra == null) {
            throw new IllegalArgumentException(
                    "La transaccion comparada es null"
            );
        }

        int resultado = fechaEjecucion.compareTo(
                otra.fechaEjecucion
        );

        if (resultado != 0) {
            return resultado;
        }

        return id.compareTo(otra.id);
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private static void validarIdentificadores(
            TipoTransaccion tipo,
            String clienteOrigen,
            String clienteDestino,
            String monederoOrigen,
            String monederoDestino) {

        if (tipo == TipoTransaccion.DEPOSITO) {

            validarId(clienteDestino, "Cliente destino");
            validarId(monederoDestino, "Monedero destino");

        } else if (tipo == TipoTransaccion.TRANSFERENCIA) {

            validarId(clienteOrigen, "Cliente origen");
            validarId(clienteDestino, "Cliente destino");

            validarId(monederoOrigen, "Monedero origen");
            validarId(monederoDestino, "Monedero destino");

            if (monederoOrigen.equals(monederoDestino)) {
                throw new IllegalArgumentException(
                        "Los monederos deben ser diferentes"
                );
            }
        }
    }

    private static void validarId(
            String id,
            String campo) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }
    }

    private static BigDecimal validarMonto(
            BigDecimal monto) {

        if (monto == null
                || monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser positivo"
            );
        }

        try {

            return monto.setScale(
                    2,
                    RoundingMode.UNNECESSARY
            );

        } catch (ArithmeticException error) {

            throw new IllegalArgumentException(
                    "El monto tiene demasiados decimales"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    public String getFechaFormateada() {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        return fechaEjecucion.format(formato);
    }

    @Override
    public String toString() {

        return "TransaccionProgramada{" +
                "id='" + id + '\'' +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", fecha=" + getFechaFormateada() +
                ", frecuencia=" + frecuencia +
                ", estado=" + estado +
                ", ejecuciones=" + ejecucionesRealizadas +
                '}';
    }
}
