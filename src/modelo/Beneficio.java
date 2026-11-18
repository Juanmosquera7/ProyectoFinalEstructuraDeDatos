
package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Representa un beneficio obtenido mediante
 * el canje de puntos del Monedero Virtual.
 *
 * Beneficios disponibles:
 *
 * 100 puntos:
 * Descuento del 10% en la comision
 * de una transferencia.
 *
 * 500 puntos:
 * Retiros sin comision durante 30 dias.
 *
 * 1000 puntos:
 * Bono de 50 unidades monetarias.
 *
 * Esta clase administra los datos y estados.
 *
 * El descuento de puntos y la aplicacion
 * financiera corresponden a los servicios.
 */
public class Beneficio {

    // ==========================================
    // TIPOS DE BENEFICIO
    // ==========================================

    public enum Tipo {

        DESCUENTO_TRANSFERENCIA(
                "Descuento en transferencia",
                100,
                10,
                BigDecimal.ZERO,
                0
        ),

        RETIROS_SIN_COMISION(
                "Retiros sin comision",
                500,
                100,
                BigDecimal.ZERO,
                30
        ),

        BONO_SALDO(
                "Bono de saldo",
                1000,
                0,
                new BigDecimal("50.00"),
                0
        );

        private final String descripcion;

        private final int costoPuntos;

        private final int porcentajeDescuento;

        private final BigDecimal montoBono;

        private final int duracionDias;

        Tipo(
                String descripcion,
                int costoPuntos,
                int porcentajeDescuento,
                BigDecimal montoBono,
                int duracionDias) {

            this.descripcion = descripcion;
            this.costoPuntos = costoPuntos;
            this.porcentajeDescuento = porcentajeDescuento;
            this.montoBono = montoBono;
            this.duracionDias = duracionDias;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public int getCostoPuntos() {
            return costoPuntos;
        }

        public int getPorcentajeDescuento() {
            return porcentajeDescuento;
        }

        public BigDecimal getMontoBono() {
            return montoBono;
        }

        public int getDuracionDias() {
            return duracionDias;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    // ==========================================
    // ESTADOS DEL BENEFICIO
    // ==========================================

    public enum Estado {

        DISPONIBLE,
        ACTIVO,
        UTILIZADO,
        VENCIDO,
        CANCELADO
    }

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final String id;

    private final String idCliente;

    private final Tipo tipo;

    private Estado estado;

    private final LocalDateTime fechaCanje;

    private final LocalDateTime fechaVencimiento;

    private LocalDateTime fechaUtilizacion;

    private int cantidadUsos;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    /**
     * Crea un beneficio asociado a un cliente.
     *
     * El beneficio se crea en estado DISPONIBLE.
     *
     * No descuenta puntos automaticamente.
     */
    public Beneficio(
            String idCliente,
            Tipo tipo) {

        validarId(idCliente);

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de beneficio es obligatorio"
            );
        }

        this.id = UUID.randomUUID().toString();

        this.idCliente = idCliente;
        this.tipo = tipo;

        this.estado = Estado.DISPONIBLE;

        this.fechaCanje = LocalDateTime.now();

        this.fechaUtilizacion = null;

        this.cantidadUsos = 0;

        if (tipo.getDuracionDias() > 0) {

            this.fechaVencimiento =
                    fechaCanje.plusDays(
                            tipo.getDuracionDias()
                    );

        } else {

            this.fechaVencimiento = null;
        }
    }

    // ==========================================
    // METODOS DE CREACION
    // ==========================================

    /**
     * Crea un beneficio de descuento
     * del 10% en una transferencia.
     */
    public static Beneficio crearDescuentoTransferencia(
            String idCliente) {

        return new Beneficio(
                idCliente,
                Tipo.DESCUENTO_TRANSFERENCIA
        );
    }

    /**
     * Crea un beneficio de retiros sin
     * comision durante 30 dias.
     */
    public static Beneficio crearRetirosSinComision(
            String idCliente) {

        return new Beneficio(
                idCliente,
                Tipo.RETIROS_SIN_COMISION
        );
    }

    /**
     * Crea un beneficio de bono de saldo.
     */
    public static Beneficio crearBonoSaldo(
            String idCliente) {

        return new Beneficio(
                idCliente,
                Tipo.BONO_SALDO
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

    public Estado getEstado() {
        actualizarVencimiento();
        return estado;
    }

    public LocalDateTime getFechaCanje() {
        return fechaCanje;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public LocalDateTime getFechaUtilizacion() {
        return fechaUtilizacion;
    }

    public int getCantidadUsos() {
        return cantidadUsos;
    }

    public int getCostoPuntos() {
        return tipo.getCostoPuntos();
    }

    public int getPorcentajeDescuento() {
        return tipo.getPorcentajeDescuento();
    }

    public BigDecimal getMontoBono() {
        return tipo.getMontoBono();
    }

    public String getDescripcion() {
        return tipo.getDescripcion();
    }

    // ==========================================
    // ACTIVACION DEL BENEFICIO
    // ==========================================

    /**
     * Activa un beneficio previamente canjeado.
     *
     * Debe ser llamado por el servicio despues
     * de confirmar el descuento de puntos.
     */
    public void activar() {

        actualizarVencimiento();

        if (estado != Estado.DISPONIBLE) {

            throw new IllegalStateException(
                    "El beneficio no esta disponible "
                            + "para activacion"
            );
        }

        estado = Estado.ACTIVO;
    }

    // ==========================================
    // UTILIZACION DEL BENEFICIO
    // ==========================================

    /**
     * Registra la utilizacion del beneficio.
     *
     * Los descuentos de transferencia y
     * bonos de saldo son de un solo uso.
     *
     * Los retiros sin comision pueden
     * utilizarse varias veces mientras
     * el beneficio se encuentre vigente.
     */
    public void utilizar() {

        actualizarVencimiento();

        if (estado != Estado.ACTIVO) {

            throw new IllegalStateException(
                    "El beneficio no esta activo"
            );
        }

        cantidadUsos++;

        fechaUtilizacion = LocalDateTime.now();

        if (tipo == Tipo.DESCUENTO_TRANSFERENCIA
                || tipo == Tipo.BONO_SALDO) {

            estado = Estado.UTILIZADO;
        }
    }

    // ==========================================
    // VALIDACION DE DISPONIBILIDAD
    // ==========================================

    /**
     * Comprueba si el beneficio esta activo
     * y todavia puede utilizarse.
     */
    public boolean estaDisponibleParaUso() {

        actualizarVencimiento();

        return estado == Estado.ACTIVO;
    }

    /**
     * Comprueba si el beneficio tiene
     * fecha de vencimiento.
     */
    public boolean tieneVencimiento() {

        return fechaVencimiento != null;
    }

    /**
     * Comprueba si el beneficio ha vencido.
     */
    public boolean estaVencido() {

        actualizarVencimiento();

        return estado == Estado.VENCIDO;
    }

    /**
     * Actualiza automaticamente el estado
     * de los beneficios temporales.
     */
    public void actualizarVencimiento() {

        if (fechaVencimiento == null) {
            return;
        }

        if ((estado == Estado.ACTIVO
                || estado == Estado.DISPONIBLE)
                && !LocalDateTime.now().isBefore(
                fechaVencimiento)) {

            estado = Estado.VENCIDO;
        }
    }

    // ==========================================
    // CANCELACION
    // ==========================================

    /**
     * Cancela un beneficio disponible o activo.
     *
     * La devolucion de puntos, si corresponde,
     * debera gestionarse desde PuntosService.
     */
    public void cancelar() {

        actualizarVencimiento();

        if (estado == Estado.UTILIZADO
                || estado == Estado.VENCIDO
                || estado == Estado.CANCELADO) {

            throw new IllegalStateException(
                    "No se puede cancelar "
                            + "un beneficio finalizado"
            );
        }

        estado = Estado.CANCELADO;
    }

    // ==========================================
    // CALCULO DE DESCUENTOS
    // ==========================================

    /**
     * Calcula el valor de una comision
     * despues de aplicar el descuento.
     *
     * No consume automaticamente el beneficio.
     */
    public BigDecimal calcularComisionConDescuento(
            BigDecimal comisionOriginal) {

        if (comisionOriginal == null
                || comisionOriginal.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "La comision no puede ser negativa"
            );
        }

        if (!estaDisponibleParaUso()) {

            throw new IllegalStateException(
                    "El beneficio no esta disponible"
            );
        }

        if (tipo == Tipo.BONO_SALDO) {

            throw new IllegalStateException(
                    "El bono de saldo no aplica "
                            + "descuentos de comision"
            );
        }

        BigDecimal porcentaje = BigDecimal.valueOf(
                tipo.getPorcentajeDescuento()
        );

        BigDecimal descuento = comisionOriginal
                .multiply(porcentaje)
                .divide(BigDecimal.valueOf(100));

        BigDecimal resultado =
                comisionOriginal.subtract(descuento);

        return resultado.setScale(
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

    // ==========================================
    // CONSULTA DE COSTO
    // ==========================================

    /**
     * Verifica si una cantidad de puntos
     * es suficiente para adquirir el beneficio.
     */
    public boolean puedeCanjear(int puntosDisponibles) {

        return puntosDisponibles >= tipo.getCostoPuntos();
    }

    // ==========================================
    // FORMATO DE FECHAS
    // ==========================================

    public String getFechaCanjeFormateada() {

        return formatearFecha(fechaCanje);
    }

    public String getFechaVencimientoFormateada() {

        if (fechaVencimiento == null) {
            return "Sin vencimiento";
        }

        return formatearFecha(fechaVencimiento);
    }

    private String formatearFecha(
            LocalDateTime fecha) {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss"
                );

        return fecha.format(formato);
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private static void validarId(String idCliente) {

        if (idCliente == null
                || idCliente.isBlank()) {

            throw new IllegalArgumentException(
                    "El ID del cliente es obligatorio"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return "Beneficio{" +
                "id='" + id + '\'' +
                ", cliente='" + idCliente + '\'' +
                ", tipo=" + tipo +
                ", costoPuntos=" + getCostoPuntos() +
                ", estado=" + getEstado() +
                ", usos=" + cantidadUsos +
                ", fechaCanje=" +
                getFechaCanjeFormateada() +
                ", vencimiento=" +
                getFechaVencimientoFormateada() +
                '}';
    }
}
