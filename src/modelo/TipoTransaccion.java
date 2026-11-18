
package modelo;

/**
 * Define los tipos de transacciones
 * disponibles en el Monedero Virtual.
 *
 * Cada tipo tiene una descripcion y una
 * cantidad de puntos otorgados por cada
 * 100 unidades monetarias.
 */
public enum TipoTransaccion {

    DEPOSITO("Deposito", 1),

    RETIRO("Retiro", 2),

    TRANSFERENCIA("Transferencia", 3),

    GASTO("Gasto", 0),

    CANJE_PUNTOS("Canje de puntos", 0),

    BONIFICACION("Bonificacion", 0),

    REVERSO("Reversion de transaccion", 0);

    private final String descripcion;
    private final int puntosPorCada100;

    // Constructor del enum.
    TipoTransaccion(
            String descripcion,
            int puntosPorCada100) {

        this.descripcion = descripcion;
        this.puntosPorCada100 = puntosPorCada100;
    }

    // Obtiene la descripcion del tipo.
    public String getDescripcion() {
        return descripcion;
    }

    // Obtiene los puntos otorgados
    // por cada 100 unidades monetarias.
    public int getPuntosPorCada100() {
        return puntosPorCada100;
    }

    // Indica si la transaccion genera puntos.
    public boolean generaPuntos() {
        return puntosPorCada100 > 0;
    }

    // Indica si representa un movimiento
    // financiero ordinario.
    public boolean esMovimientoFinanciero() {

        return this == DEPOSITO
                || this == RETIRO
                || this == TRANSFERENCIA
                || this == GASTO;
    }

    // Indica si es una operacion de ajuste
    // o de beneficio.
    public boolean esOperacionEspecial() {

        return this == CANJE_PUNTOS
                || this == BONIFICACION
                || this == REVERSO;
    }

    // Devuelve la descripcion legible.
    @Override
    public String toString() {
        return descripcion;
    }
}
