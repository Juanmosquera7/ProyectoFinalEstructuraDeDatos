
package estructuras;

/**
 * Nodo generico para estructuras de datos enlazadas.
 *
 * Cada nodo almacena un dato y una referencia
 * al siguiente nodo de la estructura.
 *
 * @param <T> Tipo de dato almacenado.
 */
public class Nodo<T> {

    private T dato;
    private Nodo<T> siguiente;

    // Constructor
    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    // Obtener el dato almacenado
    public T getDato() {
        return dato;
    }

    // Modificar el dato almacenado
    public void setDato(T dato) {
        this.dato = dato;
    }

    // Obtener el siguiente nodo
    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    // Modificar el siguiente nodo
    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
