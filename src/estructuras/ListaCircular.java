
package estructuras;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementacion propia de una lista
 * simplemente enlazada circular.
 *
 * El ultimo nodo apunta al primero,
 * formando una estructura circular.
 *
 * Se utilizara para gestionar las
 * notificaciones del monedero virtual.
 *
 * @param <T> Tipo de elemento almacenado.
 */
public class ListaCircular<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    // Constructor
    public ListaCircular() {
        this.cabeza = null;
        this.cola = null;
        this.tamanio = 0;
    }

    // Verifica si la lista esta vacia.
    public boolean estaVacia() {
        return tamanio == 0;
    }

    // Devuelve la cantidad de elementos.
    public int tamanio() {
        return tamanio;
    }

    // Agrega un elemento al inicio.
    public void agregarInicio(T dato) {

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
            nuevo.setSiguiente(nuevo);
        } else {
            nuevo.setSiguiente(cabeza);
            cabeza = nuevo;
            cola.setSiguiente(cabeza);
        }

        tamanio++;
    }

    // Agrega un elemento al final.
    public void agregarFinal(T dato) {

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
            nuevo.setSiguiente(nuevo);
        } else {
            nuevo.setSiguiente(cabeza);
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }

        tamanio++;
    }

    // Obtiene un elemento por su posicion.
    // Los indices comienzan en 0.
    public T obtener(int indice) {

        validarIndice(indice);

        Nodo<T> actual = cabeza;

        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }

        return actual.getDato();
    }

    // Busca la primera aparicion de un elemento.
    // Devuelve su indice o -1 si no existe.
    public int buscar(T dato) {

        Nodo<T> actual = cabeza;

        for (int i = 0; i < tamanio; i++) {

            if (Objects.equals(actual.getDato(), dato)) {
                return i;
            }

            actual = actual.getSiguiente();
        }

        return -1;
    }

    // Verifica si un elemento existe.
    public boolean contiene(T dato) {
        return buscar(dato) != -1;
    }

    // Elimina y devuelve el primer elemento.
    public T eliminarInicio() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "La lista circular esta vacia"
            );
        }

        T dato = cabeza.getDato();

        if (tamanio == 1) {
            cabeza = null;
            cola = null;
        } else {
            cabeza = cabeza.getSiguiente();
            cola.setSiguiente(cabeza);
        }

        tamanio--;

        return dato;
    }

    // Elimina y devuelve el ultimo elemento.
    public T eliminarFinal() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "La lista circular esta vacia"
            );
        }

        if (tamanio == 1) {
            return eliminarInicio();
        }

        Nodo<T> anterior = cabeza;

        while (anterior.getSiguiente() != cola) {
            anterior = anterior.getSiguiente();
        }

        T dato = cola.getDato();

        cola = anterior;
        cola.setSiguiente(cabeza);

        tamanio--;

        return dato;
    }

    // Elimina un elemento por su indice.
    public T eliminar(int indice) {

        validarIndice(indice);

        if (indice == 0) {
            return eliminarInicio();
        }

        if (indice == tamanio - 1) {
            return eliminarFinal();
        }

        Nodo<T> anterior = cabeza;

        for (int i = 0; i < indice - 1; i++) {
            anterior = anterior.getSiguiente();
        }

        Nodo<T> eliminado = anterior.getSiguiente();

        anterior.setSiguiente(eliminado.getSiguiente());

        tamanio--;

        return eliminado.getDato();
    }

    // Elimina la primera aparicion de un elemento.
    public boolean eliminarDato(T dato) {

        int indice = buscar(dato);

        if (indice == -1) {
            return false;
        }

        eliminar(indice);
        return true;
    }

    // Devuelve el primer elemento.
    public T obtenerPrimero() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "La lista circular esta vacia"
            );
        }

        return cabeza.getDato();
    }

    // Devuelve el ultimo elemento.
    public T obtenerUltimo() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "La lista circular esta vacia"
            );
        }

        return cola.getDato();
    }

    // Recorre exactamente una vuelta de la lista.
    public void recorrer(Consumer<T> accion) {

        Objects.requireNonNull(
                accion, "La accion no puede ser null"
        );

        Nodo<T> actual = cabeza;

        for (int i = 0; i < tamanio; i++) {

            accion.accept(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    // Rota la lista una posicion hacia adelante.
    // El segundo elemento pasa a ser la cabeza.
    // Util para recorrer notificaciones ciclicamente.
    public void rotar() {

        if (tamanio <= 1) {
            return;
        }

        cabeza = cabeza.getSiguiente();
        cola = cola.getSiguiente();
    }

    // Verifica que el ultimo nodo apunte
    // correctamente al primero.
    public boolean verificarCircularidad() {

        if (estaVacia()) {
            return cabeza == null && cola == null;
        }

        if (cabeza == null || cola == null) {
            return false;
        }

        Nodo<T> actual = cabeza;

        for (int i = 1; i < tamanio; i++) {

            actual = actual.getSiguiente();

            if (actual == null || actual == cabeza) {
                return false;
            }
        }

        return actual == cola &&
                cola.getSiguiente() == cabeza;
    }

    // Elimina todos los elementos.
    public void limpiar() {

        if (cola != null) {
            cola.setSiguiente(null);
        }

        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    // Valida una posicion existente.
    private void validarIndice(int indice) {

        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Indice fuera de rango: " + indice
            );
        }
    }

    // Representacion de la lista circular.
    @Override
    public String toString() {

        if (estaVacia()) {
            return "CABEZA -> NULL";
        }

        StringBuilder resultado =
                new StringBuilder("CABEZA -> ");

        Nodo<T> actual = cabeza;

        for (int i = 0; i < tamanio; i++) {

            resultado.append(actual.getDato());

            if (i < tamanio - 1) {
                resultado.append(" -> ");
            }

            actual = actual.getSiguiente();
        }

        resultado.append(" -> CABEZA");

        return resultado.toString();
    }
}
