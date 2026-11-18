
package estructuras;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementacion propia de una lista simplemente enlazada.
 *
 * Utiliza nodos genericos y no depende de ArrayList
 * ni de LinkedList de Java.
 *
 * @param <T> Tipo de elementos almacenados.
 */
public class ListaEnlazada<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    // Constructor
    public ListaEnlazada() {
        this.cabeza = null;
        this.cola = null;
        this.tamanio = 0;
    }

    // Verifica si la lista esta vacia.
    public boolean estaVacia() {
        return cabeza == null;
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
        } else {
            nuevo.setSiguiente(cabeza);
            cabeza = nuevo;
        }

        tamanio++;
    }

    // Agrega un elemento al final.
    public void agregarFinal(T dato) {

        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }

        tamanio++;
    }

    // Inserta un elemento en una posicion especifica.
    // La primera posicion es 0.
    public void insertar(int indice, T dato) {

        validarIndiceInsercion(indice);

        if (indice == 0) {
            agregarInicio(dato);
            return;
        }

        if (indice == tamanio) {
            agregarFinal(dato);
            return;
        }

        Nodo<T> anterior = obtenerNodo(indice - 1);
        Nodo<T> nuevo = new Nodo<>(dato);

        nuevo.setSiguiente(anterior.getSiguiente());
        anterior.setSiguiente(nuevo);

        tamanio++;
    }

    // Obtiene un elemento mediante su indice.
    public T obtener(int indice) {

        validarIndice(indice);

        return obtenerNodo(indice).getDato();
    }

    // Modifica el dato almacenado en una posicion.
    public void modificar(int indice, T dato) {

        validarIndice(indice);

        obtenerNodo(indice).setDato(dato);
    }

    // Busca la primera posicion de un elemento.
    // Retorna -1 si no existe.
    public int buscar(T dato) {

        Nodo<T> actual = cabeza;
        int indice = 0;

        while (actual != null) {

            if (Objects.equals(actual.getDato(), dato)) {
                return indice;
            }

            actual = actual.getSiguiente();
            indice++;
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
                    "La lista esta vacia"
            );
        }

        T dato = cabeza.getDato();

        cabeza = cabeza.getSiguiente();
        tamanio--;

        if (tamanio == 0) {
            cola = null;
        }

        return dato;
    }

    // Elimina y devuelve el ultimo elemento.
    public T eliminarFinal() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "La lista esta vacia"
            );
        }

        if (tamanio == 1) {
            return eliminarInicio();
        }

        Nodo<T> anterior = obtenerNodo(tamanio - 2);
        T dato = cola.getDato();

        anterior.setSiguiente(null);
        cola = anterior;
        tamanio--;

        return dato;
    }

    // Elimina un elemento mediante su indice.
    public T eliminar(int indice) {

        validarIndice(indice);

        if (indice == 0) {
            return eliminarInicio();
        }

        if (indice == tamanio - 1) {
            return eliminarFinal();
        }

        Nodo<T> anterior = obtenerNodo(indice - 1);
        Nodo<T> eliminado = anterior.getSiguiente();

        anterior.setSiguiente(eliminado.getSiguiente());

        tamanio--;

        return eliminado.getDato();
    }

    // Elimina la primera aparicion de un dato.
    // Retorna true si lo encontro.
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
                    "La lista esta vacia"
            );
        }

        return cabeza.getDato();
    }

    // Devuelve el ultimo elemento.
    public T obtenerUltimo() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "La lista esta vacia"
            );
        }

        return cola.getDato();
    }

    // Invierte el orden de los nodos.
    public void invertir() {

        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;

        cola = cabeza;

        while (actual != null) {

            Nodo<T> siguiente = actual.getSiguiente();

            actual.setSiguiente(anterior);

            anterior = actual;
            actual = siguiente;
        }

        cabeza = anterior;
    }

    // Recorre la lista aplicando una accion
    // a cada uno de sus elementos.
    public void recorrer(Consumer<T> accion) {

        Objects.requireNonNull(
                accion, "La accion no puede ser null"
        );

        Nodo<T> actual = cabeza;

        while (actual != null) {

            accion.accept(actual.getDato());

            actual = actual.getSiguiente();
        }
    }

    // Elimina todos los elementos.
    public void limpiar() {

        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    // Obtiene internamente el nodo de una posicion.
    private Nodo<T> obtenerNodo(int indice) {

        Nodo<T> actual = cabeza;

        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }

        return actual;
    }

    // Valida un indice existente.
    private void validarIndice(int indice) {

        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Indice fuera de rango: " + indice
            );
        }
    }

    // Valida una posicion de insercion.
    private void validarIndiceInsercion(int indice) {

        if (indice < 0 || indice > tamanio) {
            throw new IndexOutOfBoundsException(
                    "Indice de insercion invalido: " + indice
            );
        }
    }

    // Representacion de la lista como texto.
    @Override
    public String toString() {

        StringBuilder resultado = new StringBuilder("[");

        Nodo<T> actual = cabeza;

        while (actual != null) {

            resultado.append(actual.getDato());

            if (actual.getSiguiente() != null) {
                resultado.append(", ");
            }

            actual = actual.getSiguiente();
        }

        resultado.append("]");

        return resultado.toString();
    }
}
