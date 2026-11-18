
package estructuras;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Implementacion propia de una tabla hash generica.
 *
 * Utiliza:
 * - Arreglo de buckets.
 * - Encadenamiento separado para colisiones.
 * - Redimensionamiento automatico.
 * - Factor de carga maximo de 0.75.
 *
 * No utiliza HashMap, Hashtable ni LinkedList.
 *
 * @param <K> Tipo de clave.
 * @param <V> Tipo de valor.
 */
public class TablaHash<K, V> {

    private static final int CAPACIDAD_INICIAL = 16;
    private static final double FACTOR_CARGA = 0.75;

    // Nodo interno para almacenar pares clave-valor.
    private static class Entrada<K, V> {

        private final K clave;
        private V valor;
        private Entrada<K, V> siguiente;

        public Entrada(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.siguiente = null;
        }
    }

    private Entrada<K, V>[] tabla;
    private int tamanio;

    // Constructor con capacidad inicial predeterminada.
    public TablaHash() {
        this(CAPACIDAD_INICIAL);
    }

    // Constructor con capacidad personalizada.
    @SuppressWarnings("unchecked")
    public TablaHash(int capacidad) {

        if (capacidad <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser positiva"
            );
        }

        tabla = (Entrada<K, V>[]) new Entrada[capacidad];
        tamanio = 0;
    }

    // Calcula la posicion de una clave.
    private int calcularIndice(K clave) {

        int hash = clave.hashCode();

        hash = hash ^ (hash >>> 16);

        return (hash & 0x7fffffff) % tabla.length;
    }

    // Inserta una nueva clave o actualiza su valor.
    // Devuelve el valor anterior, o null si no existia.
    public V insertar(K clave, V valor) {

        validarClave(clave);

        int indice = calcularIndice(clave);

        Entrada<K, V> actual = tabla[indice];

        // Buscar si la clave ya existe.
        while (actual != null) {

            if (actual.clave.equals(clave)) {

                V anterior = actual.valor;
                actual.valor = valor;

                return anterior;
            }

            actual = actual.siguiente;
        }

        // Ampliar antes de insertar si es necesario.
        if ((tamanio + 1.0) / tabla.length > FACTOR_CARGA) {

            redimensionar();

            indice = calcularIndice(clave);
        }

        // Insertar al inicio de la cadena.
        Entrada<K, V> nueva = new Entrada<>(clave, valor);

        nueva.siguiente = tabla[indice];
        tabla[indice] = nueva;

        tamanio++;

        return null;
    }

    // Obtiene el valor asociado a una clave.
    // Devuelve null si no existe.
    public V obtener(K clave) {

        validarClave(clave);

        int indice = calcularIndice(clave);

        Entrada<K, V> actual = tabla[indice];

        while (actual != null) {

            if (actual.clave.equals(clave)) {
                return actual.valor;
            }

            actual = actual.siguiente;
        }

        return null;
    }

    // Verifica si existe una clave.
    public boolean contieneClave(K clave) {

        validarClave(clave);

        int indice = calcularIndice(clave);

        Entrada<K, V> actual = tabla[indice];

        while (actual != null) {

            if (actual.clave.equals(clave)) {
                return true;
            }

            actual = actual.siguiente;
        }

        return false;
    }

    // Elimina una clave y devuelve su valor.
    // Devuelve null si la clave no existe.
    public V eliminar(K clave) {

        validarClave(clave);

        int indice = calcularIndice(clave);

        Entrada<K, V> actual = tabla[indice];
        Entrada<K, V> anterior = null;

        while (actual != null) {

            if (actual.clave.equals(clave)) {

                if (anterior == null) {
                    tabla[indice] = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }

                tamanio--;

                return actual.valor;
            }

            anterior = actual;
            actual = actual.siguiente;
        }

        return null;
    }

    // Devuelve la cantidad de pares clave-valor.
    public int tamanio() {
        return tamanio;
    }

    // Verifica si la tabla esta vacia.
    public boolean estaVacia() {
        return tamanio == 0;
    }

    // Devuelve la capacidad actual del arreglo.
    public int capacidad() {
        return tabla.length;
    }

    // Calcula el factor de carga.
    public double factorCarga() {
        return (double) tamanio / tabla.length;
    }

    // Redimensiona la tabla y redistribuye las entradas.
    @SuppressWarnings("unchecked")
    private void redimensionar() {

        if (tabla.length > Integer.MAX_VALUE / 2) {
            throw new IllegalStateException(
                    "Capacidad maxima alcanzada"
            );
        }

        Entrada<K, V>[] anterior = tabla;

        tabla = (Entrada<K, V>[])
                new Entrada[anterior.length * 2];

        // Reubicar cada entrada en el nuevo arreglo.
        for (Entrada<K, V> entrada : anterior) {

            while (entrada != null) {

                Entrada<K, V> siguiente = entrada.siguiente;

                int indice = calcularIndice(entrada.clave);

                entrada.siguiente = tabla[indice];
                tabla[indice] = entrada;

                entrada = siguiente;
            }
        }
    }

    // Recorre todos los pares clave-valor.
    public void recorrer(BiConsumer<K, V> accion) {

        Objects.requireNonNull(
                accion, "La accion no puede ser null"
        );

        for (Entrada<K, V> entrada : tabla) {

            Entrada<K, V> actual = entrada;

            while (actual != null) {

                accion.accept(actual.clave, actual.valor);

                actual = actual.siguiente;
            }
        }
    }

    // Elimina todos los registros.
    public void limpiar() {

        for (int i = 0; i < tabla.length; i++) {
            tabla[i] = null;
        }

        tamanio = 0;
    }

    // Valida que la clave no sea null.
    private void validarClave(K clave) {

        if (clave == null) {
            throw new IllegalArgumentException(
                    "La clave no puede ser null"
            );
        }
    }

    // Representacion textual de la tabla hash.
    @Override
    public String toString() {

        StringBuilder resultado = new StringBuilder();

        resultado.append("TABLA HASH\n");

        for (int i = 0; i < tabla.length; i++) {

            resultado.append("[")
                    .append(i)
                    .append("] ");

            Entrada<K, V> actual = tabla[i];

            while (actual != null) {

                resultado.append("(")
                        .append(actual.clave)
                        .append(" = ")
                        .append(actual.valor)
                        .append(")");

                if (actual.siguiente != null) {
                    resultado.append(" -> ");
                }

                actual = actual.siguiente;
            }

            resultado.append("\n");
        }

        return resultado.toString();
    }
}
