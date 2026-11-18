
package estructuras;

/**
 * Implementacion propia de una pila generica.
 *
 * Utiliza nodos enlazados y sigue el principio
 * LIFO (Last In, First Out).
 *
 * En el proyecto se utilizara para gestionar
 * las transacciones reversibles del monedero.
 *
 * @param <T> Tipo de elemento almacenado.
 */
public class Pila<T> {

    private Nodo<T> cima;
    private int tamanio;

    // Constructor
    public Pila() {
        this.cima = null;
        this.tamanio = 0;
    }

    // Verifica si la pila esta vacia.
    public boolean estaVacia() {
        return cima == null;
    }

    // Devuelve la cantidad de elementos.
    public int tamanio() {
        return tamanio;
    }

    // Agrega un elemento a la cima de la pila.
    public void apilar(T dato) {

        Nodo<T> nuevo = new Nodo<>(dato);

        nuevo.setSiguiente(cima);
        cima = nuevo;

        tamanio++;
    }

    // Elimina y devuelve el elemento de la cima.
    public T desapilar() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede desapilar: la pila esta vacia"
            );
        }

        T dato = cima.getDato();

        cima = cima.getSiguiente();
        tamanio--;

        return dato;
    }

    // Consulta el elemento de la cima sin eliminarlo.
    public T cima() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede consultar: la pila esta vacia"
            );
        }

        return cima.getDato();
    }

    // Busca un elemento en la pila.
    // Devuelve su posicion desde la cima,
    // comenzando en 1, o -1 si no existe.
    public int buscar(T dato) {

        Nodo<T> actual = cima;
        int posicion = 1;

        while (actual != null) {

            if (java.util.Objects.equals(
                    actual.getDato(), dato)) {
                return posicion;
            }

            actual = actual.getSiguiente();
            posicion++;
        }

        return -1;
    }

    // Elimina todos los elementos de la pila.
    public void limpiar() {

        cima = null;
        tamanio = 0;
    }

    // Devuelve una representacion de la pila
    // desde la cima hasta el fondo.
    @Override
    public String toString() {

        StringBuilder resultado = new StringBuilder();

        resultado.append("CIMA -> ");

        Nodo<T> actual = cima;

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
