
package estructuras;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Implementacion propia de una cola de prioridad
 * mediante un monticulo binario minimo (Min-Heap).
 *
 * El elemento con menor valor de prioridad
 * se procesa primero.
 *
 * No utiliza PriorityQueue ni ArrayList.
 *
 * @param <T> Tipo de elemento almacenado.
 */
public class ColaPrioridad<T> {

    private Object[] elementos;
    private int tamanio;
    private final Comparator<? super T> comparador;

    // Constructor con comparador personalizado.
    public ColaPrioridad(Comparator<? super T> comparador) {

        if (comparador == null) {
            throw new IllegalArgumentException(
                    "El comparador no puede ser null"
            );
        }

        this.elementos = new Object[10];
        this.tamanio = 0;
        this.comparador = comparador;
    }

    // Verifica si la cola esta vacia.
    public boolean estaVacia() {
        return tamanio == 0;
    }

    // Devuelve la cantidad de elementos.
    public int tamanio() {
        return tamanio;
    }

    // Agrega un elemento respetando la prioridad.
    public void encolar(T dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "El elemento no puede ser null"
            );
        }

        asegurarCapacidad();

        elementos[tamanio] = dato;

        subir(tamanio);

        tamanio++;
    }

    // Elimina y devuelve el elemento de mayor prioridad.
    public T desencolar() {

        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La cola de prioridad esta vacia"
            );
        }

        T resultado = obtenerElemento(0);

        tamanio--;

        if (tamanio == 0) {
            elementos[0] = null;
            return resultado;
        }

        elementos[0] = elementos[tamanio];
        elementos[tamanio] = null;

        bajar(0);

        return resultado;
    }

    // Consulta el elemento prioritario sin eliminarlo.
    public T frente() {

        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La cola de prioridad esta vacia"
            );
        }

        return obtenerElemento(0);
    }

    // Desplaza un elemento hacia arriba del monticulo.
    private void subir(int indice) {

        while (indice > 0) {

            int padre = (indice - 1) / 2;

            if (comparar(indice, padre) >= 0) {
                break;
            }

            intercambiar(indice, padre);

            indice = padre;
        }
    }

    // Desplaza un elemento hacia abajo del monticulo.
    private void bajar(int indice) {

        while (true) {

            int izquierdo = 2 * indice + 1;
            int derecho = 2 * indice + 2;
            int menor = indice;

            if (izquierdo < tamanio &&
                    comparar(izquierdo, menor) < 0) {
                menor = izquierdo;
            }

            if (derecho < tamanio &&
                    comparar(derecho, menor) < 0) {
                menor = derecho;
            }

            if (menor == indice) {
                break;
            }

            intercambiar(indice, menor);

            indice = menor;
        }
    }

    // Compara dos posiciones del monticulo.
    private int comparar(int indice1, int indice2) {

        return comparador.compare(
                obtenerElemento(indice1),
                obtenerElemento(indice2)
        );
    }

    // Intercambia dos elementos.
    private void intercambiar(int indice1, int indice2) {

        Object auxiliar = elementos[indice1];

        elementos[indice1] = elementos[indice2];

        elementos[indice2] = auxiliar;
    }

    // Amplia el arreglo cuando alcanza su capacidad.
    private void asegurarCapacidad() {

        if (tamanio < elementos.length) {
            return;
        }

        Object[] nuevo = new Object[elementos.length * 2];

        for (int i = 0; i < elementos.length; i++) {
            nuevo[i] = elementos[i];
        }

        elementos = nuevo;
    }

    // Recupera un elemento realizando una conversion.
    @SuppressWarnings("unchecked")
    private T obtenerElemento(int indice) {
        return (T) elementos[indice];
    }

    // Elimina todos los elementos.
    public void limpiar() {

        for (int i = 0; i < tamanio; i++) {
            elementos[i] = null;
        }

        tamanio = 0;
    }

    // Representacion del monticulo interno.
    // El arreglo no esta completamente ordenado.
    @Override
    public String toString() {

        StringBuilder resultado = new StringBuilder("[");

        for (int i = 0; i < tamanio; i++) {

            resultado.append(elementos[i]);

            if (i < tamanio - 1) {
                resultado.append(", ");
            }
        }

        resultado.append("]");

        return resultado.toString();
    }
}
