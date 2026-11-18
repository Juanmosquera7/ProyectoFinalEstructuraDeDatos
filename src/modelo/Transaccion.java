
package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Representa una transaccion financiera
 * del sistema Monedero Virtual.
 *
 * Permite registrar:
 * - Depositos.
 * - Retiros.
 * - Transferencias.
 * - Gastos.
 * - Canjes de puntos.
 * - Bonificaciones.
 * - Reversiones.
 *
 * Esta clase almacena la informacion.
 * No modifica directamente los saldos.
 *
 * Las operaciones financieras seran
 * gestionadas por TransaccionService.
 */
public class Transaccion {

    // ==========================================
    // ESTADOS DE UNA TRANSACCION
    // ==========================================

    public enum Estado {

        PENDIENTE,
        COMPLETADA,
        REVERTIDA,
        FALLIDA,
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
    private final BigDecimal comision;

    private final LocalDateTime fechaCreacion;

    private Estado estado;

    private int puntosGenerados;

    private final String descripcion;

    private final String idTransaccionOriginal;

    private LocalDateTime fechaProcesamiento;

    // ==========================================
    // CONSTRUCTOR PRINCIPAL
    // ==========================================

    public Transaccion(
            TipoTransaccion tipo,
            String idClienteOrigen,
            String idClienteDestino,
            String idMonederoOrigen,
            String idMonederoDestino,
            BigDecimal monto,
            BigDecimal comision,
            String descripcion,
            String idTransaccionOriginal) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de transaccion es obligatorio"
            );
        }

        validarIdentificadores(
                tipo,
                idClienteOrigen,
                idClienteDestino,
                idMonederoOrigen,
                idMonederoDestino,
                idTransaccionOriginal
        );

        validarMonto(monto);

        validarComision(comision);

        this.id = UUID.randomUUID().toString();

        this.tipo = tipo;

        this.idClienteOrigen = idClienteOrigen;
        this.idClienteDestino = idClienteDestino;

        this.idMonederoOrigen = idMonederoOrigen;
        this.idMonederoDestino = idMonederoDestino;

        this.monto = normalizar(monto);
        this.comision = normalizar(comision);

        this.fechaCreacion = LocalDateTime.now();

        this.estado = Estado.PENDIENTE;

        this.puntosGenerados = 0;

        this.descripcion = descripcion == null
                ? ""
                : descripcion;

        this.idTransaccionOriginal =
                idTransaccionOriginal;

        this.fechaProcesamiento = null;
    }

    // ==========================================
    // CONSTRUCTORES SIMPLIFICADOS
    // ==========================================

    /**
     * Crea un deposito.
     *
     * El cliente y monedero de destino
     * reciben el dinero.
     */
    public static Transaccion crearDeposito(
            String idCliente,
            String idMonedero,
            BigDecimal monto) {

        return new Transaccion(
                TipoTransaccion.DEPOSITO,
                null,
                idCliente,
                null,
                idMonedero,
                monto,
                BigDecimal.ZERO,
                "Deposito al monedero",
                null
        );
    }

    /**
     * Crea un retiro.
     *
     * El dinero sale del monedero indicado.
     */
    public static Transaccion crearRetiro(
            String idCliente,
            String idMonedero,
            BigDecimal monto) {

        return new Transaccion(
                TipoTransaccion.RETIRO,
                idCliente,
                null,
                idMonedero,
                null,
                monto,
                BigDecimal.ZERO,
                "Retiro del monedero",
                null
        );
    }

    /**
     * Crea una transferencia entre monederos.
     */
    public static Transaccion crearTransferencia(
            String idClienteOrigen,
            String idClienteDestino,
            String idMonederoOrigen,
            String idMonederoDestino,
            BigDecimal monto,
            BigDecimal comision) {

        return new Transaccion(
                TipoTransaccion.TRANSFERENCIA,
                idClienteOrigen,
                idClienteDestino,
                idMonederoOrigen,
                idMonederoDestino,
                monto,
                comision,
                "Transferencia entre monederos",
                null
        );
    }

    /**
     * Crea un gasto realizado por un cliente.
     */
    public static Transaccion crearGasto(
            String idCliente,
            String idMonedero,
            BigDecimal monto,
            String descripcion) {

        return new Transaccion(
                TipoTransaccion.GASTO,
                idCliente,
                null,
                idMonedero,
                null,
                monto,
                BigDecimal.ZERO,
                descripcion,
                null
        );
    }

    /**
     * Crea un registro de reversion.
     *
     * El servicio sera responsable de
     * compensar el dinero y los puntos.
     */
    public static Transaccion crearReversion(
            Transaccion original) {

        if (original == null) {
            throw new IllegalArgumentException(
                    "La transaccion original es obligatoria"
            );
        }

        if (original.getEstado() != Estado.COMPLETADA) {
            throw new IllegalStateException(
                    "Solo se pueden revertir "
                            + "transacciones completadas"
            );
        }

        if (original.getTipo() != TipoTransaccion.DEPOSITO
                && original.getTipo() != TipoTransaccion.RETIRO
                && original.getTipo()
                != TipoTransaccion.TRANSFERENCIA) {

            throw new IllegalStateException(
                    "Este tipo de transaccion "
                            + "no admite reversion"
            );
        }

        return new Transaccion(
                TipoTransaccion.REVERSO,
                original.idClienteOrigen,
                original.idClienteDestino,
                original.idMonederoOrigen,
                original.idMonederoDestino,
                original.monto,
                original.comision,
                "Reversion de " + original.id,
                original.id
        );
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private static void validarIdentificadores(
            TipoTransaccion tipo,
            String clienteOrigen,
            String clienteDestino,
            String monederoOrigen,
            String monederoDestino,
            String transaccionOriginal) {

        switch (tipo) {

            case DEPOSITO -> {

                validarId(clienteDestino, "Cliente destino");

                validarId(
                        monederoDestino,
                        "Monedero destino"
                );
            }

            case RETIRO, GASTO -> {

                validarId(clienteOrigen, "Cliente origen");

                validarId(
                        monederoOrigen,
                        "Monedero origen"
                );
            }

            case TRANSFERENCIA -> {

                validarId(clienteOrigen, "Cliente origen");
                validarId(clienteDestino, "Cliente destino");

                validarId(monederoOrigen, "Monedero origen");
                validarId(monederoDestino, "Monedero destino");

                if (monederoOrigen.equals(monederoDestino)) {

                    throw new IllegalArgumentException(
                            "No se puede transferir "
                                    + "al mismo monedero"
                    );
                }
            }

            case REVERSO -> {

                validarId(
                        transaccionOriginal,
                        "Transaccion original"
                );
            }

            case CANJE_PUNTOS, BONIFICACION -> {

                if (esIdVacio(clienteOrigen)
                        && esIdVacio(clienteDestino)) {

                    throw new IllegalArgumentException(
                            "Debe existir un cliente asociado"
                    );
                }
            }
        }
    }

    private static void validarId(
            String valor,
            String campo) {

        if (esIdVacio(valor)) {

            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }
    }

    private static boolean esIdVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static void validarMonto(BigDecimal monto) {

        if (monto == null
                || monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
            );
        }

        normalizar(monto);
    }

    private static void validarComision(
            BigDecimal comision) {

        if (comision == null
                || comision.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "La comision no puede ser negativa"
            );
        }

        normalizar(comision);
    }

    private static BigDecimal normalizar(
            BigDecimal cantidad) {

        try {

            return cantidad.setScale(
                    2,
                    RoundingMode.UNNECESSARY
            );

        } catch (ArithmeticException error) {

            throw new IllegalArgumentException(
                    "La cantidad no puede tener "
                            + "mas de dos decimales significativos"
            );
        }
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

    public BigDecimal getComision() {
        return comision;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public Estado getEstado() {
        return estado;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIdTransaccionOriginal() {
        return idTransaccionOriginal;
    }

    // ==========================================
    // ESTADOS DE LA TRANSACCION
    // ==========================================

    /**
     * Marca una transaccion como completada.
     */
    public void marcarCompletada() {

        if (estado != Estado.PENDIENTE) {

            throw new IllegalStateException(
                    "Solo pueden completarse "
                            + "transacciones pendientes"
            );
        }

        estado = Estado.COMPLETADA;
        fechaProcesamiento = LocalDateTime.now();
    }

    /**
     * Marca una transaccion completada
     * como revertida.
     */
    public void marcarRevertida() {

        if (estado != Estado.COMPLETADA) {

            throw new IllegalStateException(
                    "Solo pueden revertirse "
                            + "transacciones completadas"
            );
        }

        estado = Estado.REVERTIDA;
    }

    /**
     * Marca una transaccion pendiente
     * como fallida.
     */
    public void marcarFallida() {

        if (estado != Estado.PENDIENTE) {

            throw new IllegalStateException(
                    "Solo pueden fallar "
                            + "transacciones pendientes"
            );
        }

        estado = Estado.FALLIDA;
        fechaProcesamiento = LocalDateTime.now();
    }

    /**
     * Cancela una transaccion pendiente.
     */
    public void cancelar() {

        if (estado != Estado.PENDIENTE) {

            throw new IllegalStateException(
                    "Solo pueden cancelarse "
                            + "transacciones pendientes"
            );
        }

        estado = Estado.CANCELADA;
    }

    public boolean estaCompletada() {
        return estado == Estado.COMPLETADA;
    }

    public boolean estaPendiente() {
        return estado == Estado.PENDIENTE;
    }

    public boolean estaRevertida() {
        return estado == Estado.REVERTIDA;
    }

    public boolean esReversion() {
        return tipo == TipoTransaccion.REVERSO;
    }

    // ==========================================
    // PUNTOS GENERADOS
    // ==========================================

    /**
     * Registra los puntos otorgados
     * por una transaccion completada.
     *
     * El calculo y la asignacion real
     * seran responsabilidad de PuntosService.
     */
    public void registrarPuntosGenerados(int puntos) {

        if (estado != Estado.COMPLETADA) {

            throw new IllegalStateException(
                    "La transaccion debe estar completada"
            );
        }

        if (puntos < 0) {

            throw new IllegalArgumentException(
                    "Los puntos no pueden ser negativos"
            );
        }

        if (!tipo.generaPuntos() && puntos != 0) {

            throw new IllegalArgumentException(
                    "Este tipo de transaccion "
                            + "no genera puntos"
            );
        }

        puntosGenerados = puntos;
    }

    // ==========================================
    // CALCULOS FINANCIEROS
    // ==========================================

    /**
     * Calcula el costo total de una operacion.
     *
     * Monto + comision.
     */
    public BigDecimal calcularCostoTotal() {

        return monto.add(comision);
    }

    /**
     * Calcula los puntos base segun el tipo
     * y el monto de la transaccion.
     *
     * Deposito:      1 punto por cada 100.
     * Retiro:        2 puntos por cada 100.
     * Transferencia: 3 puntos por cada 100.
     *
     * Los montos inferiores a 100 no
     * generan puntos base.
     */
    public int calcularPuntosBase() {

        if (!tipo.generaPuntos()) {
            return 0;
        }

        BigDecimal bloques = monto.divideToIntegralValue(
                new BigDecimal("100")
        );

        BigDecimal resultado = bloques.multiply(
                BigDecimal.valueOf(
                        tipo.getPuntosPorCada100()
                )
        );

        return resultado.intValueExact();
    }

    // ==========================================
    // FORMATO Y REPRESENTACION
    // ==========================================

    public String getFechaFormateada() {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss"
                );

        return fechaCreacion.format(formato);
    }

    @Override
    public String toString() {

        return "Transaccion{" +
                "id='" + id + '\'' +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", comision=" + comision +
                ", estado=" + estado +
                ", puntos=" + puntosGenerados +
                ", fecha=" + getFechaFormateada() +
                '}';
    }
}
