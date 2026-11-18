
package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Representa un monedero virtual perteneciente
 * a un cliente del sistema.
 *
 * Cada cliente puede tener multiples monederos:
 * - Principal
 * - Ahorros
 * - Gastos
 * - Otros
 *
 * Permite administrar saldos, realizar depositos,
 * retiros y controlar el estado del monedero.
 *
 * Los registros de transacciones, puntos y
 * notificaciones seran gestionados por los
 * servicios correspondientes.
 */
public class Monedero {

    private final String id;
    private final String idCliente;

    private String nombre;
    private String tipo;

    private BigDecimal saldo;

    private boolean activo;

    // Constructor principal.
    public Monedero(
            String id,
            String idCliente,
            String nombre,
            String tipo) {

        validarTexto(id, "ID del monedero");
        validarTexto(idCliente, "ID del cliente");
        validarTexto(nombre, "Nombre del monedero");
        validarTexto(tipo, "Tipo del monedero");

        this.id = id;
        this.idCliente = idCliente;

        this.nombre = nombre;
        this.tipo = tipo;

        this.saldo = BigDecimal.ZERO.setScale(2);
        this.activo = true;
    }

    // Constructor simplificado.
    public Monedero(
            String id,
            String idCliente,
            String nombre) {

        this(id, idCliente, nombre, "PRINCIPAL");
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

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public boolean isActivo() {
        return activo;
    }

    // ==========================================
    // MODIFICACION DE INFORMACION
    // ==========================================

    // Modifica el nombre del monedero.
    public void setNombre(String nombre) {

        validarTexto(nombre, "Nombre del monedero");

        this.nombre = nombre;
    }

    // Modifica el tipo de monedero.
    public void setTipo(String tipo) {

        validarTexto(tipo, "Tipo del monedero");

        this.tipo = tipo;
    }

    // ==========================================
    // OPERACIONES FINANCIERAS
    // ==========================================

    /**
     * Agrega dinero al saldo disponible.
     *
     * @param monto Cantidad que se desea depositar.
     */
    public void depositar(BigDecimal monto) {

        validarActivo();
        validarMonto(monto);

        BigDecimal cantidad = normalizarMonto(monto);

        saldo = saldo.add(cantidad);
    }

    /**
     * Retira dinero del monedero.
     *
     * Rechaza montos invalidos y operaciones
     * que superen el saldo disponible.
     */
    public void retirar(BigDecimal monto) {

        validarActivo();
        validarMonto(monto);

        BigDecimal cantidad = normalizarMonto(monto);

        if (saldo.compareTo(cantidad) < 0) {

            throw new IllegalArgumentException(
                    "Saldo insuficiente en el monedero "
                            + id
            );
        }

        saldo = saldo.subtract(cantidad);
    }

    /**
     * Verifica si existe saldo suficiente
     * para realizar una operacion.
     */
    public boolean tieneSaldoSuficiente(
            BigDecimal monto) {

        validarMonto(monto);

        BigDecimal cantidad = normalizarMonto(monto);

        return saldo.compareTo(cantidad) >= 0;
    }

    /**
     * Devuelve el saldo disponible.
     */
    public BigDecimal consultarSaldo() {
        return saldo;
    }

    /**
     * Verifica si el saldo es inferior
     * a un limite establecido.
     *
     * Sera utilizado por las notificaciones
     * automaticas de saldo bajo.
     */
    public boolean tieneSaldoBajo(
            BigDecimal limite) {

        if (limite == null ||
                limite.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El limite no puede ser negativo"
            );
        }

        BigDecimal limiteNormalizado =
                normalizarMonto(limite);

        return saldo.compareTo(limiteNormalizado) < 0;
    }

    // ==========================================
    // ESTADO DEL MONEDERO
    // ==========================================

    /**
     * Bloquea el monedero.
     *
     * Un monedero bloqueado no permite
     * depositos ni retiros.
     */
    public void bloquear() {
        activo = false;
    }

    /**
     * Habilita nuevamente el monedero.
     */
    public void desbloquear() {
        activo = true;
    }

    /**
     * Verifica que el monedero este activo.
     */
    private void validarActivo() {

        if (!activo) {

            throw new IllegalStateException(
                    "El monedero " + id
                            + " se encuentra bloqueado"
            );
        }
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    /**
     * Valida que un monto sea positivo
     * y tenga como maximo dos decimales.
     */
    private void validarMonto(BigDecimal monto) {

        if (monto == null) {

            throw new IllegalArgumentException(
                    "El monto no puede ser null"
            );
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
            );
        }

        try {

            monto.setScale(2, RoundingMode.UNNECESSARY);

        } catch (ArithmeticException error) {

            throw new IllegalArgumentException(
                    "El monto no puede tener mas "
                            + "de dos decimales significativos"
            );
        }
    }

    /**
     * Normaliza los montos monetarios
     * a dos posiciones decimales.
     */
    private BigDecimal normalizarMonto(
            BigDecimal monto) {

        return monto.setScale(
                2,
                RoundingMode.UNNECESSARY
        );
    }

    /**
     * Verifica que un texto sea valido.
     */
    private void validarTexto(
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

        return "Monedero{" +
                "id='" + id + '\'' +
                ", idCliente='" + idCliente + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", saldo=" + saldo +
                ", activo=" + activo +
                '}';
    }
}
