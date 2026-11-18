
package estructuras;

import java.util.Objects;

/**
 * Implementacion propia de una cola generica.
 *
 * Utiliza nodos enlazados y sigue el principio
 * FIFO (First In, First Out).
 *
 * No utiliza Queue, LinkedList ni otras
 * estructuras de datos de Java.
 *
 * @param <T> Tipo de elemento almacenado.
 */
public class Cola<T> {

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    // Constructor
    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamanio = 0;
    }

    // Verifica si la cola esta vacia.
    public boolean estaVacia() {
        return frente == null;
    }

    // Devuelve la cantidad de elementos.
    public int tamanio() {
        return tamanio;
    }

    // Agrega un elemento al final de la cola.
    public void encolar(T dato) {

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }

        tamanio++;
    }

    // Elimina y devuelve el primer elemento.
    public T desencolar() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede desencolar: la cola esta vacia"
            );
        }

        T dato = frente.getDato();

        frente = frente.getSiguiente();
        tamanio--;

        // Si no quedan elementos, reiniciar el fin.
        if (estaVacia()) {
            fin = null;
        }

        return dato;
    }

    // Consulta el primer elemento sin eliminarlo.
    public T frente() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede consultar: la cola esta vacia"
            );
        }

        return frente.getDato();
    }

    // Consulta el ultimo elemento sin eliminarlo.
    public T ultimo() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede consultar: la cola esta vacia"
            );
        }

        return fin.getDato();
    }

    // Busca un elemento dentro de la cola.
    // Devuelve su posicion desde el frente,
    // comenzando en 1, o -1 si no existe.
    public int buscar(T dato) {

        Nodo<T> actual = frente;
        int posicion = 1;

        while (actual != null) {

            if (Objects.equals(actual.getDato(), dato)) {
                return posicion;
            }

            actual = actual.getSiguiente();
            posicion++;
        }

        return -1;
    }

    // Verifica si un elemento existe.
    public boolean contiene(T dato) {
        return buscar(dato) != -1;
    }

    // Elimina todos los elementos de la cola.
    public void limpiar() {

        frente = null;
        fin = null;
        tamanio = 0;
    }

    // Representa la cola desde el frente hasta el fin.
    @Override
    public String toString() {

        StringBuilder resultado = new StringBuilder();

        resultado.append("FRENTE -> ");

        Nodo<T> actual = frente;

        while (actual != null) {

            resultado.append(actual.getDato());

            if (actual.getSiguiente() != null) {
                resultado.append(" -> ");
            }

            actual = actual.getSiguiente();
        }

        resultado.append(" -> NULL");

        return resultado.toString();
    }
}
