
package estructuras;

/**
 * Implementacion propia de un nodo doble generico.
 *
 * Cada nodo almacena:
 * - Un dato de tipo T.
 * - Una referencia al nodo anterior.
 * - Una referencia al nodo siguiente.
 *
 * Puede utilizarse en listas doblemente enlazadas,
 * listas circulares dobles y otras estructuras.
 *
 * @param <T> Tipo de dato almacenado.
 */
public class NodoDoble<T> {

    private T dato;
    private NodoDoble<T> anterior;
    private NodoDoble<T> siguiente;

    // Constructor principal.
    public NodoDoble(T dato) {

        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }

    // Constructor con referencias.
    public NodoDoble(
            T dato,
            NodoDoble<T> anterior,
            NodoDoble<T> siguiente) {

        this.dato = dato;
        this.anterior = anterior;
        this.siguiente = siguiente;
    }

    // Obtiene el dato almacenado.
    public T getDato() {
        return dato;
    }

    // Modifica el dato almacenado.
    public void setDato(T dato) {
        this.dato = dato;
    }

    // Obtiene el nodo anterior.
    public NodoDoble<T> getAnterior() {
        return anterior;
    }

    // Modifica la referencia al nodo anterior.
    public void setAnterior(NodoDoble<T> anterior) {
        this.anterior = anterior;
    }

    // Obtiene el nodo siguiente.
    public NodoDoble<T> getSiguiente() {
        return siguiente;
    }

    // Modifica la referencia al nodo siguiente.
    public void setSiguiente(NodoDoble<T> siguiente) {
        this.siguiente = siguiente;
    }

    // Verifica si no tiene nodo anterior.
    public boolean esPrimero() {
        return anterior == null;
    }

    // Verifica si no tiene nodo siguiente.
    public boolean esUltimo() {
        return siguiente == null;
    }

    // Elimina las referencias a otros nodos.
    public void desvincular() {

        anterior = null;
        siguiente = null;
    }

    // Representacion textual del nodo.
    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
