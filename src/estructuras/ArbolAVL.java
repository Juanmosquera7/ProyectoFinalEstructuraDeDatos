
package estructuras;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Implementacion propia de un Arbol AVL generico.
 *
 * Mantiene el arbol balanceado automaticamente
 * mediante rotaciones simples y dobles:
 *
 * LL: Rotacion derecha.
 * RR: Rotacion izquierda.
 * LR: Rotacion izquierda-derecha.
 * RL: Rotacion derecha-izquierda.
 *
 * Permite varios valores asociados a una misma clave.
 *
 * @param <K> Tipo de clave comparable.
 * @param <V> Tipo de valor almacenado.
 */
public class ArbolAVL<K extends Comparable<? super K>, V> {

    // Nodo interno del arbol AVL.
    private class NodoAVL {

        K clave;
        ListaEnlazada<V> valores;

        NodoAVL izquierdo;
        NodoAVL derecho;

        int altura;

        NodoAVL(K clave, V valor) {
            this.clave = clave;

            this.valores = new ListaEnlazada<>();
            this.valores.agregarFinal(valor);

            this.izquierdo = null;
            this.derecho = null;

            this.altura = 1;
        }
    }

    private NodoAVL raiz;

    private int cantidadEntradas;
    private int cantidadNodos;

    // Constructor.
    public ArbolAVL() {
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

    // Cantidad de claves diferentes.
    public int cantidadNodos() {
        return cantidadNodos;
    }

    // Obtiene la altura de un nodo.
    private int alturaNodo(NodoAVL nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    // Devuelve la altura del arbol.
    // Arbol vacio = 0.
    public int altura() {
        return alturaNodo(raiz);
    }

    // Actualiza la altura de un nodo.
    private void actualizarAltura(NodoAVL nodo) {

        if (nodo != null) {
            nodo.altura = 1 + Math.max(
                    alturaNodo(nodo.izquierdo),
                    alturaNodo(nodo.derecho)
            );
        }
    }

    // Calcula el factor de equilibrio.
    // FE = altura izquierda - altura derecha.
    private int factorEquilibrio(NodoAVL nodo) {

        if (nodo == null) {
            return 0;
        }

        return alturaNodo(nodo.izquierdo)
                - alturaNodo(nodo.derecho);
    }

    // Rotacion simple hacia la derecha (LL).
    private NodoAVL rotarDerecha(NodoAVL y) {

        NodoAVL x = y.izquierdo;
        NodoAVL temporal = x.derecho;

        x.derecho = y;
        y.izquierdo = temporal;

        actualizarAltura(y);
        actualizarAltura(x);

        return x;
    }

    // Rotacion simple hacia la izquierda (RR).
    private NodoAVL rotarIzquierda(NodoAVL x) {

        NodoAVL y = x.derecho;
        NodoAVL temporal = y.izquierdo;

        y.izquierdo = x;
        x.derecho = temporal;

        actualizarAltura(x);
        actualizarAltura(y);

        return y;
    }

    // Restablece el equilibrio de un subarbol.
    private NodoAVL balancear(NodoAVL nodo) {

        if (nodo == null) {
            return null;
        }

        actualizarAltura(nodo);

        int equilibrio = factorEquilibrio(nodo);

        // Subarbol cargado hacia la izquierda.
        if (equilibrio > 1) {

            // Caso LR.
            if (factorEquilibrio(nodo.izquierdo) < 0) {
                nodo.izquierdo = rotarIzquierda(
                        nodo.izquierdo
                );
            }

            // Caso LL o segunda rotacion de LR.
            return rotarDerecha(nodo);
        }

        // Subarbol cargado hacia la derecha.
        if (equilibrio < -1) {

            // Caso RL.
            if (factorEquilibrio(nodo.derecho) > 0) {
                nodo.derecho = rotarDerecha(
                        nodo.derecho
                );
            }

            // Caso RR o segunda rotacion de RL.
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    // Inserta una clave y su valor.
    public void insertar(K clave, V valor) {

        validarClave(clave);

        raiz = insertarRecursivo(raiz, clave, valor);

        cantidadEntradas++;
    }

    // Insercion recursiva con balanceo.
    private NodoAVL insertarRecursivo(
            NodoAVL actual, K clave, V valor) {

        if (actual == null) {
            cantidadNodos++;
            return new NodoAVL(clave, valor);
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
            return actual;
        }

        return balancear(actual);
    }

    // Busca todos los valores asociados a una clave.
    // Devuelve una lista independiente.
    public ListaEnlazada<V> buscar(K clave) {

        validarClave(clave);

        ListaEnlazada<V> resultado = new ListaEnlazada<>();

        NodoAVL nodo = buscarNodo(clave);

        if (nodo != null) {
            nodo.valores.recorrer(resultado::agregarFinal);
        }

        return resultado;
    }

    // Busca internamente un nodo.
    private NodoAVL buscarNodo(K clave) {

        NodoAVL actual = raiz;

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

    // Verifica si una clave existe.
    public boolean contieneClave(K clave) {

        validarClave(clave);

        return buscarNodo(clave) != null;
    }

    // Verifica si existe un par clave-valor.
    public boolean contiene(K clave, V valor) {

        validarClave(clave);

        NodoAVL nodo = buscarNodo(clave);

        return nodo != null &&
                nodo.valores.contiene(valor);
    }

    // Elimina un valor especifico de una clave.
    public boolean eliminar(K clave, V valor) {

        validarClave(clave);

        NodoAVL nodo = buscarNodo(clave);

        if (nodo == null ||
                !nodo.valores.contiene(valor)) {
            return false;
        }

        // Si hay varios valores, solo elimina uno.
        if (nodo.valores.tamanio() > 1) {

            nodo.valores.eliminarDato(valor);

        } else {

            // Si era el ultimo, elimina el nodo.
            raiz = eliminarNodo(raiz, clave);
            cantidadNodos--;
        }

        cantidadEntradas--;

        return true;
    }

    // Elimina todos los valores asociados a una clave.
    public int eliminarClave(K clave) {

        validarClave(clave);

        NodoAVL nodo = buscarNodo(clave);

        if (nodo == null) {
            return 0;
        }

        int eliminados = nodo.valores.tamanio();

        raiz = eliminarNodo(raiz, clave);

        cantidadEntradas -= eliminados;
        cantidadNodos--;

        return eliminados;
    }

    // Elimina un nodo y mantiene el balance AVL.
    private NodoAVL eliminarNodo(
            NodoAVL actual, K clave) {

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

            // Caso 1 y 2: cero o un hijo.
            if (actual.izquierdo == null) {
                return actual.derecho;
            }

            if (actual.derecho == null) {
                return actual.izquierdo;
            }

            // Caso 3: dos hijos.
            // Buscar sucesor inorden.
            NodoAVL sucesor = minimoNodo(actual.derecho);

            // Copiar clave y valores del sucesor.
            actual.clave = sucesor.clave;
            actual.valores = sucesor.valores;

            // Eliminar el nodo sucesor original.
            actual.derecho = eliminarNodo(
                    actual.derecho, sucesor.clave
            );
        }

        return balancear(actual);
    }

    // Busca el nodo con menor clave.
    private NodoAVL minimoNodo(NodoAVL actual) {

        while (actual.izquierdo != null) {
            actual = actual.izquierdo;
        }

        return actual;
    }

    // Obtiene la menor clave.
    public K minimo() {

        if (estaVacio()) {
            throw new IllegalStateException(
                    "El arbol AVL esta vacio"
            );
        }

        return minimoNodo(raiz).clave;
    }

    // Obtiene la mayor clave.
    public K maximo() {

        if (estaVacio()) {
            throw new IllegalStateException(
                    "El arbol AVL esta vacio"
            );
        }

        NodoAVL actual = raiz;

        while (actual.derecho != null) {
            actual = actual.derecho;
        }

        return actual.clave;
    }

    // Recorrido inorden: claves ascendentes.
    public void inorden(BiConsumer<K, V> accion) {

        Objects.requireNonNull(accion);

        inordenRecursivo(raiz, accion);
    }

    private void inordenRecursivo(
            NodoAVL actual, BiConsumer<K, V> accion) {

        if (actual == null) {
            return;
        }

        inordenRecursivo(actual.izquierdo, accion);

        actual.valores.recorrer(
                valor -> accion.accept(actual.clave, valor)
        );

        inordenRecursivo(actual.derecho, accion);
    }

    // Recorrido preorden: raiz, izquierda, derecha.
    public void preorden(BiConsumer<K, V> accion) {

        Objects.requireNonNull(accion);

        preordenRecursivo(raiz, accion);
    }

    private void preordenRecursivo(
            NodoAVL actual, BiConsumer<K, V> accion) {

        if (actual == null) {
            return;
        }

        actual.valores.recorrer(
                valor -> accion.accept(actual.clave, valor)
        );

        preordenRecursivo(actual.izquierdo, accion);
        preordenRecursivo(actual.derecho, accion);
    }

    // Recorrido postorden: izquierda, derecha, raiz.
    public void postorden(BiConsumer<K, V> accion) {

        Objects.requireNonNull(accion);

        postordenRecursivo(raiz, accion);
    }

    private void postordenRecursivo(
            NodoAVL actual, BiConsumer<K, V> accion) {

        if (actual == null) {
            return;
        }

        postordenRecursivo(actual.izquierdo, accion);
        postordenRecursivo(actual.derecho, accion);

        actual.valores.recorrer(
                valor -> accion.accept(actual.clave, valor)
        );
    }

    // Verifica recursivamente las propiedades AVL.
    public boolean verificarIntegridad() {

        int alturaCalculada = verificarNodo(
                raiz, null, null
        );

        return alturaCalculada >= 0
                && contarNodos(raiz) == cantidadNodos
                && contarEntradas(raiz) == cantidadEntradas;
    }

    // Retorna -1 si encuentra alguna inconsistencia.
    private int verificarNodo(
            NodoAVL actual, K minimo, K maximo) {

        if (actual == null) {
            return 0;
        }

        if (actual.clave == null ||
                actual.valores.estaVacia()) {
            return -1;
        }

        if (minimo != null &&
                actual.clave.compareTo(minimo) <= 0) {
            return -1;
        }

        if (maximo != null &&
                actual.clave.compareTo(maximo) >= 0) {
            return -1;
        }

        int izquierda = verificarNodo(
                actual.izquierdo, minimo, actual.clave
        );

        int derecha = verificarNodo(
                actual.derecho, actual.clave, maximo
        );

        if (izquierda < 0 || derecha < 0) {
            return -1;
        }

        // Verificar factor de equilibrio.
        if (Math.abs(izquierda - derecha) > 1) {
            return -1;
        }

        int alturaEsperada =
                1 + Math.max(izquierda, derecha);

        // Verificar altura almacenada.
        if (actual.altura != alturaEsperada) {
            return -1;
        }

        return alturaEsperada;
    }

    // Cuenta los nodos recursivamente.
    private int contarNodos(NodoAVL actual) {

        if (actual == null) {
            return 0;
        }

        return 1
                + contarNodos(actual.izquierdo)
                + contarNodos(actual.derecho);
    }

    // Cuenta todas las entradas almacenadas.
    private int contarEntradas(NodoAVL actual) {

        if (actual == null) {
            return 0;
        }

        return actual.valores.tamanio()
                + contarEntradas(actual.izquierdo)
                + contarEntradas(actual.derecho);
    }

    // Elimina todos los datos.
    public void limpiar() {

        raiz = null;
        cantidadEntradas = 0;
        cantidadNodos = 0;
    }

    // Valida que la clave no sea null.
    private void validarClave(K clave) {

        if (clave == null) {
            throw new IllegalArgumentException(
                    "La clave no puede ser null"
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
