
package estructuras;

import java.util.function.BiConsumer;

/**
 * Arbol binario de busqueda generico (ABB).
 *
 * Organiza elementos mediante claves comparables.
 * Permite varias entradas con la misma clave.
 *
 * En el monedero virtual:
 * - Clave: puntos acumulados (Integer).
 * - Valor: identificador del cliente (String).
 *
 * No utiliza TreeMap, TreeSet ni ArrayList.
 *
 * @param <K> Tipo de clave comparable.
 * @param <V> Tipo del valor almacenado.
 */
public class ArbolBinario<K extends Comparable<? super K>, V> {

    // Nodo interno del arbol.
    private class NodoArbol {

        K clave;
        ListaEnlazada<V> valores;
        NodoArbol izquierdo;
        NodoArbol derecho;

        NodoArbol(K clave, V valor) {
            this.clave = clave;
            this.valores = new ListaEnlazada<>();
            this.valores.agregarFinal(valor);
        }
    }

    private NodoArbol raiz;
    private int cantidadEntradas;
    private int cantidadNodos;

    // Constructor
    public ArbolBinario() {
        raiz = null;
        cantidadEntradas = 0;
        cantidadNodos = 0;
    }

    // Verifica si el arbol esta vacio.
    public boolean estaVacio() {
        return raiz == null;
    }

    // Cantidad total de pares clave-valor.
    public int tamanio() {
        return cantidadEntradas;
    }

    // Cantidad de claves distintas.
    public int cantidadNodos() {
        return cantidadNodos;
    }

    // Inserta una clave y un valor.
    public void insertar(K clave, V valor) {

        validarClave(clave);

        raiz = insertarRecursivo(raiz, clave, valor);

        cantidadEntradas++;
    }

    private NodoArbol insertarRecursivo(
            NodoArbol actual, K clave, V valor) {

        if (actual == null) {
            cantidadNodos++;
            return new NodoArbol(clave, valor);
        }

        int comparacion = clave.compareTo(actual.clave);

        if (comparacion < 0) {

            actual.izquierdo = insertarRecursivo(
                    actual.izquierdo, clave, valor
            );

        } else if (comparacion > 0) {

            actual.derecho = insertarRecursivo(
                    actual.derecho, clave, valor
            );

        } else {

            // Misma clave: agregar otro valor.
            actual.valores.agregarFinal(valor);
        }

        return actual;
    }

    // Busca todos los valores asociados a una clave.
    // Devuelve una lista independiente.
    public ListaEnlazada<V> buscar(K clave) {

        validarClave(clave);

        NodoArbol nodo = buscarNodo(raiz, clave);

        ListaEnlazada<V> resultado = new ListaEnlazada<>();

        if (nodo != null) {
            nodo.valores.recorrer(resultado::agregarFinal);
        }

        return resultado;
    }

    // Busca un nodo mediante su clave.
    private NodoArbol buscarNodo(NodoArbol actual, K clave) {

        while (actual != null) {

            int comparacion = clave.compareTo(actual.clave);

            if (comparacion == 0) {
                return actual;
            }

            if (comparacion < 0) {
                actual = actual.izquierdo;
            } else {
                actual = actual.derecho;
            }
        }

        return null;
    }

    // Verifica si existe una clave.
    public boolean contieneClave(K clave) {

        validarClave(clave);

        return buscarNodo(raiz, clave) != null;
    }

    // Verifica si existe un par clave-valor.
    public boolean contiene(K clave, V valor) {

        validarClave(clave);

        NodoArbol nodo = buscarNodo(raiz, clave);

        return nodo != null && nodo.valores.contiene(valor);
    }

    // Elimina un valor especifico asociado a una clave.
    // Si era el ultimo valor, elimina el nodo del arbol.
    public boolean eliminar(K clave, V valor) {

        validarClave(clave);

        NodoArbol nodo = buscarNodo(raiz, clave);

        if (nodo == null || !nodo.valores.contiene(valor)) {
            return false;
        }

        if (nodo.valores.tamanio() > 1) {

            nodo.valores.eliminarDato(valor);

        } else {

            raiz = eliminarNodo(raiz, clave);
            cantidadNodos--;
        }

        cantidadEntradas--;

        return true;
    }

    // Elimina todos los valores asociados a una clave.
    // Devuelve la cantidad de entradas eliminadas.
    public int eliminarClave(K clave) {

        validarClave(clave);

        NodoArbol nodo = buscarNodo(raiz, clave);

        if (nodo == null) {
            return 0;
        }

        int eliminados = nodo.valores.tamanio();

        raiz = eliminarNodo(raiz, clave);

        cantidadEntradas -= eliminados;
        cantidadNodos--;

        return eliminados;
    }

    // Elimina un nodo conservando la propiedad del ABB.
    private NodoArbol eliminarNodo(NodoArbol actual, K clave) {

        if (actual == null) {
            return null;
        }

        int comparacion = clave.compareTo(actual.clave);

        if (comparacion < 0) {

            actual.izquierdo = eliminarNodo(
                    actual.izquierdo, clave
            );

        } else if (comparacion > 0) {

            actual.derecho = eliminarNodo(
                    actual.derecho, clave
            );

        } else {

            // Caso 1: sin hijo izquierdo.
            if (actual.izquierdo == null) {
                return actual.derecho;
            }

            // Caso 2: sin hijo derecho.
            if (actual.derecho == null) {
                return actual.izquierdo;
            }

            // Caso 3: tiene dos hijos.
            // Reemplazar con el sucesor inorden.
            NodoArbol sucesor = minimoNodo(actual.derecho);

            actual.clave = sucesor.clave;
            actual.valores = sucesor.valores;

            actual.derecho = eliminarNodo(
                    actual.derecho, sucesor.clave
            );
        }

        return actual;
    }

    // Devuelve el nodo con la clave minima.
    private NodoArbol minimoNodo(NodoArbol actual) {

        while (actual.izquierdo != null) {
            actual = actual.izquierdo;
        }

        return actual;
    }

    // Devuelve la menor clave del arbol.
    public K minimo() {

        if (estaVacio()) {
            throw new IllegalStateException(
                    "El arbol esta vacio"
            );
        }

        return minimoNodo(raiz).clave;
    }

    // Devuelve la mayor clave del arbol.
    public K maximo() {

        if (estaVacio()) {
            throw new IllegalStateException(
                    "El arbol esta vacio"
            );
        }

        NodoArbol actual = raiz;

        while (actual.derecho != null) {
            actual = actual.derecho;
        }

        return actual.clave;
    }

    // Recorre todos los elementos en orden ascendente.
    public void inorden(BiConsumer<K, V> accion) {

        validarAccion(accion);

        inordenRecursivo(raiz, accion);
    }

    private void inordenRecursivo(
            NodoArbol actual, BiConsumer<K, V> accion) {

        if (actual == null) {
            return;
        }

        inordenRecursivo(actual.izquierdo, accion);

        actual.valores.recorrer(
                valor -> accion.accept(actual.clave, valor)
        );

        inordenRecursivo(actual.derecho, accion);
    }

    // Recorre primero la raiz y luego los hijos.
    public void preorden(BiConsumer<K, V> accion) {

        validarAccion(accion);

        preordenRecursivo(raiz, accion);
    }

    private void preordenRecursivo(
            NodoArbol actual, BiConsumer<K, V> accion) {

        if (actual == null) {
            return;
        }

        actual.valores.recorrer(
                valor -> accion.accept(actual.clave, valor)
        );

        preordenRecursivo(actual.izquierdo, accion);
        preordenRecursivo(actual.derecho, accion);
    }

    // Recorre primero los hijos y despues la raiz.
    public void postorden(BiConsumer<K, V> accion) {

        validarAccion(accion);

        postordenRecursivo(raiz, accion);
    }

    private void postordenRecursivo(
            NodoArbol actual, BiConsumer<K, V> accion) {

        if (actual == null) {
            return;
        }

        postordenRecursivo(actual.izquierdo, accion);
        postordenRecursivo(actual.derecho, accion);

        actual.valores.recorrer(
                valor -> accion.accept(actual.clave, valor)
        );
    }

    // Calcula la altura del arbol.
    // Arbol vacio: -1.
    // Arbol con solo la raiz: 0.
    public int altura() {
        return calcularAltura(raiz);
    }

    private int calcularAltura(NodoArbol actual) {

        if (actual == null) {
            return -1;
        }

        int izquierda = calcularAltura(actual.izquierdo);
        int derecha = calcularAltura(actual.derecho);

        return 1 + Math.max(izquierda, derecha);
    }

    // Verifica recursivamente el orden del ABB.
    public boolean verificarIntegridad() {

        return verificarOrden(raiz, null, null)
                && contarNodos(raiz) == cantidadNodos
                && contarEntradas(raiz) == cantidadEntradas;
    }

    private boolean verificarOrden(
            NodoArbol actual, K minimo, K maximo) {

        if (actual == null) {
            return true;
        }

        if (actual.clave == null ||
                actual.valores.estaVacia()) {
            return false;
        }

        if (minimo != null &&
                actual.clave.compareTo(minimo) <= 0) {
            return false;
        }

        if (maximo != null &&
                actual.clave.compareTo(maximo) >= 0) {
            return false;
        }

        return verificarOrden(
                actual.izquierdo, minimo, actual.clave
        ) && verificarOrden(
                actual.derecho, actual.clave, maximo
        );
    }

    private int contarNodos(NodoArbol actual) {

        if (actual == null) {
            return 0;
        }

        return 1
                + contarNodos(actual.izquierdo)
                + contarNodos(actual.derecho);
    }

    private int contarEntradas(NodoArbol actual) {

        if (actual == null) {
            return 0;
        }

        return actual.valores.tamanio()
                + contarEntradas(actual.izquierdo)
                + contarEntradas(actual.derecho);
    }

    // Elimina todos los elementos.
    public void limpiar() {

        raiz = null;
        cantidadEntradas = 0;
        cantidadNodos = 0;
    }

    // Valida que una clave no sea null.
    private void validarClave(K clave) {

        if (clave == null) {
            throw new IllegalArgumentException(
                    "La clave no puede ser null"
            );
        }
    }

    private void validarAccion(BiConsumer<K, V> accion) {

        if (accion == null) {
            throw new IllegalArgumentException(
                    "La accion no puede ser null"
            );
        }
    }

    // Representacion textual en orden ascendente.
    @Override
    public String toString() {

        StringBuilder resultado = new StringBuilder("[");

        final boolean[] primero = {true};

        inorden((clave, valor) -> {

            if (!primero[0]) {
                resultado.append(", ");
            }

            resultado.append("(")
                    .append(clave)
                    .append(": ")
                    .append(valor)
                    .append(")");

            primero[0] = false;
        });

        resultado.append("]");

        return resultado.toString();
    }
}
