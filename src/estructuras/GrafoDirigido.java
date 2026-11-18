
package estructuras;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementacion propia de un grafo dirigido
 * y ponderado mediante listas de adyacencia.
 *
 * Cada vertice representa un cliente, monedero
 * o categoria de gasto.
 *
 * Cada arista representa una relacion dirigida.
 *
 * El peso representa el monto acumulado
 * de las operaciones entre dos vertices.
 *
 * No utiliza HashMap, ArrayList ni LinkedList.
 */
public class GrafoDirigido {

    /**
     * Representa una conexion dirigida.
     */
    public static class Arista {

        private final String origen;
        private final String destino;

        private BigDecimal peso;
        private int cantidadOperaciones;

        public Arista(
                String origen,
                String destino,
                BigDecimal peso) {

            this.origen = origen;
            this.destino = destino;
            this.peso = peso;
            this.cantidadOperaciones = 1;
        }

        public String getOrigen() {
            return origen;
        }

        public String getDestino() {
            return destino;
        }

        public BigDecimal getPeso() {
            return peso;
        }

        public int getCantidadOperaciones() {
            return cantidadOperaciones;
        }

        private void acumular(BigDecimal monto) {
            peso = peso.add(monto);
            cantidadOperaciones++;
        }

        @Override
        public String toString() {
            return origen + " -> " + destino
                    + " | Total: " + peso
                    + " | Operaciones: "
                    + cantidadOperaciones;
        }
    }

    /**
     * Representa un vertice del grafo.
     */
    private static class Vertice {

        private final String id;
        private final ListaEnlazada<Arista> adyacentes;

        public Vertice(String id) {
            this.id = id;
            this.adyacentes = new ListaEnlazada<>();
        }
    }

    // Indice de vertices mediante tabla hash propia.
    private final TablaHash<String, Vertice> vertices;

    // Conserva el orden de insercion de los vertices.
    private final ListaEnlazada<String> ordenVertices;

    private int cantidadAristas;

    // Constructor.
    public GrafoDirigido() {

        vertices = new TablaHash<>();
        ordenVertices = new ListaEnlazada<>();

        cantidadAristas = 0;
    }

    // Devuelve la cantidad de vertices.
    public int cantidadVertices() {
        return vertices.tamanio();
    }

    // Devuelve la cantidad de conexiones distintas.
    public int cantidadAristas() {
        return cantidadAristas;
    }

    // Verifica si el grafo esta vacio.
    public boolean estaVacio() {
        return vertices.estaVacia();
    }

    // Agrega un vertice si todavia no existe.
    public boolean agregarVertice(String id) {

        validarId(id);

        if (vertices.contieneClave(id)) {
            return false;
        }

        Vertice nuevo = new Vertice(id);

        vertices.insertar(id, nuevo);
        ordenVertices.agregarFinal(id);

        return true;
    }

    // Verifica si existe un vertice.
    public boolean contieneVertice(String id) {

        validarId(id);

        return vertices.contieneClave(id);
    }

    /**
     * Registra una operacion dirigida.
     *
     * Si la conexion no existe, crea una arista.
     * Si ya existe, acumula el monto y aumenta
     * la cantidad de operaciones.
     *
     * Permite conexiones de un vertice consigo mismo.
     */
    public void agregarArista(
            String origen,
            String destino,
            BigDecimal monto) {

        validarId(origen);
        validarId(destino);
        validarMonto(monto);

        // Registrar automaticamente ambos vertices.
        agregarVertice(origen);
        agregarVertice(destino);

        Vertice verticeOrigen = vertices.obtener(origen);

        Arista existente = buscarArista(
                verticeOrigen, destino
        );

        if (existente != null) {

            existente.acumular(monto);

        } else {

            Arista nueva = new Arista(
                    origen, destino, monto
            );

            verticeOrigen.adyacentes.agregarFinal(nueva);

            cantidadAristas++;
        }
    }

    // Busca una arista especifica.
    private Arista buscarArista(
            Vertice origen,
            String destino) {

        if (origen == null) {
            return null;
        }

        for (int i = 0;
             i < origen.adyacentes.tamanio();
             i++) {

            Arista actual = origen.adyacentes.obtener(i);

            if (actual.getDestino().equals(destino)) {
                return actual;
            }
        }

        return null;
    }

    // Verifica si existe una conexion dirigida.
    public boolean existeArista(
            String origen,
            String destino) {

        validarId(origen);
        validarId(destino);

        Vertice vertice = vertices.obtener(origen);

        return buscarArista(vertice, destino) != null;
    }

    // Obtiene el monto acumulado de una conexion.
    public BigDecimal obtenerPeso(
            String origen,
            String destino) {

        validarId(origen);
        validarId(destino);

        Vertice vertice = vertices.obtener(origen);

        Arista arista = buscarArista(vertice, destino);

        if (arista == null) {
            return BigDecimal.ZERO;
        }

        return arista.getPeso();
    }

    // Obtiene cuantas operaciones existen
    // entre dos vertices.
    public int obtenerCantidadOperaciones(
            String origen,
            String destino) {

        validarId(origen);
        validarId(destino);

        Vertice vertice = vertices.obtener(origen);

        Arista arista = buscarArista(vertice, destino);

        if (arista == null) {
            return 0;
        }

        return arista.getCantidadOperaciones();
    }

    /**
     * Devuelve una lista independiente de las
     * conexiones salientes de un vertice.
     *
     * Las aristas se copian para evitar
     * modificaciones externas del grafo.
     */
    public ListaEnlazada<Arista> obtenerAdyacentes(
            String id) {

        validarId(id);

        ListaEnlazada<Arista> resultado =
                new ListaEnlazada<>();

        Vertice vertice = vertices.obtener(id);

        if (vertice == null) {
            return resultado;
        }

        vertice.adyacentes.recorrer(arista -> {

            Arista copia = new Arista(
                    arista.getOrigen(),
                    arista.getDestino(),
                    arista.getPeso()
            );

            // Conservar cantidad de operaciones.
            copia.cantidadOperaciones =
                    arista.getCantidadOperaciones();

            resultado.agregarFinal(copia);
        });

        return resultado;
    }

    /**
     * Calcula el monto total enviado
     * desde un vertice.
     */
    public BigDecimal totalEnviado(String id) {

        validarId(id);

        Vertice vertice = vertices.obtener(id);

        if (vertice == null) {
            return BigDecimal.ZERO;
        }

        final BigDecimal[] total = {
                BigDecimal.ZERO
        };

        vertice.adyacentes.recorrer(arista ->
                total[0] = total[0].add(
                        arista.getPeso()
                )
        );

        return total[0];
    }

    /**
     * Calcula el monto total recibido
     * por un vertice.
     */
    public BigDecimal totalRecibido(String id) {

        validarId(id);

        final BigDecimal[] total = {
                BigDecimal.ZERO
        };

        recorrerAristas(arista -> {

            if (arista.getDestino().equals(id)) {
                total[0] = total[0].add(
                        arista.getPeso()
                );
            }
        });

        return total[0];
    }

    // Calcula la cantidad de conexiones salientes.
    public int gradoSalida(String id) {

        validarId(id);

        Vertice vertice = vertices.obtener(id);

        if (vertice == null) {
            return 0;
        }

        return vertice.adyacentes.tamanio();
    }

    // Calcula la cantidad de conexiones entrantes.
    public int gradoEntrada(String id) {

        validarId(id);

        final int[] cantidad = {0};

        recorrerAristas(arista -> {

            if (arista.getDestino().equals(id)) {
                cantidad[0]++;
            }
        });

        return cantidad[0];
    }

    /**
     * Obtiene el destino al que un vertice
     * ha enviado mayor cantidad de dinero.
     *
     * Devuelve null si no tiene conexiones.
     */
    public String destinoMasFrecuente(String id) {

        validarId(id);

        Vertice vertice = vertices.obtener(id);

        if (vertice == null ||
                vertice.adyacentes.estaVacia()) {
            return null;
        }

        Arista mejor = null;

        for (int i = 0;
             i < vertice.adyacentes.tamanio();
             i++) {

            Arista actual =
                    vertice.adyacentes.obtener(i);

            if (mejor == null ||
                    actual.getCantidadOperaciones() >
                            mejor.getCantidadOperaciones()) {

                mejor = actual;
            }
        }

        return mejor == null
                ? null
                : mejor.getDestino();
    }

    /**
     * Recorre todas las aristas del grafo.
     *
     * Entrega copias de las aristas para
     * proteger los datos internos.
     */
    public void recorrerAristas(
            Consumer<Arista> accion) {

        Objects.requireNonNull(
                accion, "La accion no puede ser null"
        );

        ordenVertices.recorrer(id -> {

            Vertice vertice = vertices.obtener(id);

            vertice.adyacentes.recorrer(arista -> {

                Arista copia = new Arista(
                        arista.getOrigen(),
                        arista.getDestino(),
                        arista.getPeso()
                );

                copia.cantidadOperaciones =
                        arista.getCantidadOperaciones();

                accion.accept(copia);
            });
        });
    }

    /**
     * Verifica si existe un camino dirigido
     * desde un vertice hasta otro.
     *
     * Utiliza DFS (Depth First Search)
     * con recursividad.
     */
    public boolean existeCamino(
            String origen,
            String destino) {

        validarId(origen);
        validarId(destino);

        if (!contieneVertice(origen) ||
                !contieneVertice(destino)) {
            return false;
        }

        TablaHash<String, Boolean> visitados =
                new TablaHash<>();

        return dfs(origen, destino, visitados);
    }

    // Busqueda recursiva en profundidad.
    private boolean dfs(
            String actual,
            String destino,
            TablaHash<String, Boolean> visitados) {

        if (actual.equals(destino)) {
            return true;
        }

        visitados.insertar(actual, true);

        Vertice vertice = vertices.obtener(actual);

        for (int i = 0;
             i < vertice.adyacentes.tamanio();
             i++) {

            Arista arista =
                    vertice.adyacentes.obtener(i);

            String siguiente = arista.getDestino();

            if (!visitados.contieneClave(siguiente)) {

                if (dfs(
                        siguiente,
                        destino,
                        visitados)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Elimina una conexion completa.
     *
     * Devuelve true si existia.
     */
    public boolean eliminarArista(
            String origen,
            String destino) {

        validarId(origen);
        validarId(destino);

        Vertice vertice = vertices.obtener(origen);

        if (vertice == null) {
            return false;
        }

        for (int i = 0;
             i < vertice.adyacentes.tamanio();
             i++) {

            Arista actual =
                    vertice.adyacentes.obtener(i);

            if (actual.getDestino().equals(destino)) {

                vertice.adyacentes.eliminar(i);

                cantidadAristas--;

                return true;
            }
        }

        return false;
    }

    /**
     * Elimina un vertice y todas sus
     * conexiones entrantes y salientes.
     */
    public boolean eliminarVertice(String id) {

        validarId(id);

        Vertice vertice = vertices.obtener(id);

        if (vertice == null) {
            return false;
        }

        // Eliminar conexiones salientes.
        cantidadAristas -=
                vertice.adyacentes.tamanio();

        // Eliminar conexiones entrantes.
        ordenVertices.recorrer(origen -> {

            if (!origen.equals(id)) {

                if (eliminarArista(origen, id)) {
                    // La cantidad se actualiza
                    // dentro de eliminarArista.
                }
            }
        });

        vertices.eliminar(id);
        ordenVertices.eliminarDato(id);

        return true;
    }

    /**
     * Verifica la consistencia general del grafo:
     * - Todos los destinos existen.
     * - Todos los origenes son correctos.
     * - Los montos son positivos.
     * - La cantidad de aristas coincide.
     */
    public boolean verificarIntegridad() {

        if (ordenVertices.tamanio() !=
                vertices.tamanio()) {
            return false;
        }

        final boolean[] valido = {true};
        final int[] contador = {0};

        ordenVertices.recorrer(id -> {

            Vertice vertice = vertices.obtener(id);

            if (vertice == null ||
                    !vertice.id.equals(id)) {

                valido[0] = false;
                return;
            }

            vertice.adyacentes.recorrer(arista -> {

                contador[0]++;

                if (!arista.getOrigen().equals(id) ||
                        !vertices.contieneClave(
                                arista.getDestino()) ||
                        arista.getPeso().compareTo(
                                BigDecimal.ZERO) <= 0 ||
                        arista.getCantidadOperaciones() <= 0) {

                    valido[0] = false;
                }
            });
        });

        return valido[0] &&
                contador[0] == cantidadAristas;
    }

    // Elimina todos los vertices y conexiones.
    public void limpiar() {

        vertices.limpiar();
        ordenVertices.limpiar();

        cantidadAristas = 0;
    }

    // Valida un identificador.
    private void validarId(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador no puede estar vacio"
            );
        }
    }

    // Valida un monto de operacion.
    private void validarMonto(BigDecimal monto) {

        if (monto == null ||
                monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
            );
        }
    }

    // Representacion textual del grafo.
    @Override
    public String toString() {

        StringBuilder resultado =
                new StringBuilder("GRAFO DIRIGIDO\n");

        ordenVertices.recorrer(id -> {

            resultado.append(id).append(" -> ");

            Vertice vertice = vertices.obtener(id);

            if (vertice.adyacentes.estaVacia()) {

                resultado.append("SIN CONEXIONES");

            } else {

                vertice.adyacentes.recorrer(arista -> {

                    resultado.append("[")
                            .append(arista.getDestino())
                            .append(": $")
                            .append(arista.getPeso())
                            .append("] ");
                });
            }

            resultado.append("\n");
        });

        return resultado.toString();
    }
}
