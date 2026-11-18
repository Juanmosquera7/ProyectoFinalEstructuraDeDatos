
package modelo;

/**
 * Representa los rangos de los clientes
 * del sistema Monedero Virtual.
 *
 * Los rangos dependen de los puntos acumulados:
 *
 * BRONCE:   0 - 500 puntos
 * PLATA:    501 - 1000 puntos
 * ORO:      1001 - 5000 puntos
 * PLATINO:  Mas de 5000 puntos
 *
 * Cada rango puede ofrecer beneficios
 * adicionales en las transacciones.
 */
public enum Rango {

    BRONCE(
            "Bronce",
            0,
            500,
            0
    ),

    PLATA(
            "Plata",
            501,
            1000,
            5
    ),

    ORO(
            "Oro",
            1001,
            5000,
            10
    ),

    PLATINO(
            "Platino",
            5001,
            Integer.MAX_VALUE,
            15
    );

    private final String nombre;
    private final int puntosMinimos;
    private final int puntosMaximos;
    private final int descuentoComision;

    // Constructor del enum.
    Rango(
            String nombre,
            int puntosMinimos,
            int puntosMaximos,
            int descuentoComision) {

        this.nombre = nombre;
        this.puntosMinimos = puntosMinimos;
        this.puntosMaximos = puntosMaximos;
        this.descuentoComision = descuentoComision;
    }

    // Obtiene el nombre del rango.
    public String getNombre() {
        return nombre;
    }

    // Obtiene el minimo de puntos del rango.
    public int getPuntosMinimos() {
        return puntosMinimos;
    }

    // Obtiene el maximo de puntos del rango.
    public int getPuntosMaximos() {
        return puntosMaximos;
    }

    // Obtiene el porcentaje de descuento
    // aplicado a las comisiones.
    public int getDescuentoComision() {
        return descuentoComision;
    }

    // Verifica si una cantidad de puntos
    // pertenece a este rango.
    public boolean contienePuntos(int puntos) {

        return puntos >= puntosMinimos
                && puntos <= puntosMaximos;
    }

    /**
     * Determina automaticamente el rango
     * correspondiente a una cantidad de puntos.
     *
     * @param puntos Puntos acumulados del cliente.
     * @return Rango correspondiente.
     */
    public static Rango calcularRango(int puntos) {

        if (puntos < 0) {
            throw new IllegalArgumentException(
                    "Los puntos no pueden ser negativos"
            );
        }

        for (Rango rango : Rango.values()) {

            if (rango.contienePuntos(puntos)) {
                return rango;
            }
        }

        throw new IllegalStateException(
                "No se encontro un rango valido"
        );
    }

    // Calcula cuantos puntos faltan
    // para alcanzar el siguiente rango.
    public int puntosParaSiguienteRango(int puntos) {

        if (puntos < 0) {
            throw new IllegalArgumentException(
                    "Los puntos no pueden ser negativos"
            );
        }

        Rango actual = calcularRango(puntos);

        if (actual == PLATINO) {
            return 0;
        }

        return actual.puntosMaximos - puntos + 1;
    }

    // Obtiene el siguiente rango.
    // Platino no tiene un rango superior.
    public Rango siguienteRango() {

        return switch (this) {

            case BRONCE -> PLATA;

            case PLATA -> ORO;

            case ORO -> PLATINO;

            case PLATINO -> PLATINO;
        };
    }

    // Verifica si el rango tiene descuentos.
    public boolean tieneBeneficios() {
        return descuentoComision > 0;
    }

    // Devuelve el nombre legible del rango.
    @Override
    public String toString() {
        return nombre;
    }
}
