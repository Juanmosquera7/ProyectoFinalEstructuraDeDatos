
package servicios;

import estructuras.ArbolAVL;
import estructuras.ArbolBinario;
import estructuras.ListaEnlazada;

import modelo.Cliente;
import modelo.Rango;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Servicio encargado de gestionar los rangos
 * y clasificaciones de los clientes.
 *
 * ESTRUCTURAS PROPIAS:
 *
 * ArbolBinario<Integer, Cliente>
 * - Indice ABB de clientes por puntos.
 *
 * ArbolAVL<Integer, Cliente>
 * - Indice balanceado de clientes por puntos.
 *
 * ListaEnlazada<Cliente>
 * - Resultados de consultas y rankings.
 *
 * Los arboles permiten almacenar varios
 * clientes con la misma puntuacion.
 *
 * IMPORTANTE:
 * Los puntos pueden cambiar desde Cliente
 * y PuntosService.
 *
 * Para evitar indices desactualizados,
 * este servicio reconstruye ambos arboles
 * antes de las consultas publicas.
 *
 * No utiliza colecciones de Java.
 */
public class RangosService {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;

    private final ArbolBinario<Integer, Cliente>
            indiceABB;

    private final ArbolAVL<Integer, Cliente>
            indiceAVL;

    private int cantidadIndexada;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public RangosService(
            ClienteService clienteService) {

        this.clienteService = Objects.requireNonNull(
                clienteService,
                "ClienteService no puede ser null"
        );

        this.indiceABB =
                new ArbolBinario<>();

        this.indiceAVL =
                new ArbolAVL<>();

        this.cantidadIndexada = 0;

        reconstruirIndices();
    }

    // ==========================================
    // SINCRONIZACION DE ARBOLES
    // ==========================================

    /**
     * Reconstruye los indices de puntuaciones
     * utilizando todos los clientes registrados.
     *
     * Los dos arboles contienen los mismos
     * clientes y las mismas claves.
     *
     * La clave corresponde a los puntos
     * actuales de cada cliente.
     */
    public void reconstruirIndices() {

        indiceABB.limpiar();
        indiceAVL.limpiar();

        cantidadIndexada = 0;

        clienteService.recorrerClientes(cliente -> {

            int puntos = cliente.getPuntos();

            indiceABB.insertar(
                    puntos,
                    cliente
            );

            indiceAVL.insertar(
                    puntos,
                    cliente
            );

            cantidadIndexada++;
        });
    }

    // ==========================================
    // CONSULTAS DE RANGO
    // ==========================================

    /**
     * Devuelve el rango actual de un cliente.
     */
    public Rango consultarRango(
            String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        return Rango.calcularRango(
                cliente.getPuntos()
        );
    }

    /**
     * Consulta los puntos de un cliente.
     */
    public int consultarPuntos(
            String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        return cliente.getPuntos();
    }

    /**
     * Consulta los puntos necesarios
     * para alcanzar el siguiente rango.
     */
    public int puntosParaSiguienteRango(
            String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        return cliente.puntosParaSiguienteRango();
    }

    /**
     * Obtiene el siguiente rango.
     *
     * Platino devuelve Platino porque
     * es el rango maximo disponible.
     */
    public Rango consultarSiguienteRango(
            String idCliente) {

        return consultarRango(
                idCliente
        ).siguienteRango();
    }

    /**
     * Devuelve el porcentaje de descuento
     * asociado al rango actual.
     *
     * Esta consulta no aplica descuentos
     * sobre las transacciones.
     */
    public int consultarDescuentoRango(
            String idCliente) {

        return consultarRango(
                idCliente
        ).getDescuentoComision();
    }

    // ==========================================
    // BUSQUEDA POR PUNTUACION
    // ==========================================

    /**
     * Busca clientes con una cantidad exacta
     * de puntos utilizando el ABB.
     *
     * Devuelve una lista independiente.
     */
    public ListaEnlazada<Cliente> buscarPorPuntosABB(
            int puntos) {

        validarPuntos(puntos);

        reconstruirIndices();

        return indiceABB.buscar(puntos);
    }

    /**
     * Busca clientes con una cantidad exacta
     * de puntos utilizando el arbol AVL.
     */
    public ListaEnlazada<Cliente> buscarPorPuntosAVL(
            int puntos) {

        validarPuntos(puntos);

        reconstruirIndices();

        return indiceAVL.buscar(puntos);
    }

    /**
     * Busca clientes por puntuacion usando
     * el AVL como indice principal.
     */
    public ListaEnlazada<Cliente> buscarPorPuntos(
            int puntos) {

        return buscarPorPuntosAVL(puntos);
    }

    /**
     * Comprueba si existe al menos un cliente
     * con la puntuacion indicada.
     */
    public boolean existePuntuacion(
            int puntos) {

        validarPuntos(puntos);

        reconstruirIndices();

        return indiceAVL.contieneClave(puntos);
    }

    // ==========================================
    // RECORRIDOS ORDENADOS
    // ==========================================

    /**
     * Obtiene los clientes desde la
     * menor hasta la mayor puntuacion.
     *
     * Utiliza el recorrido inorden del AVL.
     */
    public ListaEnlazada<Cliente>
    obtenerRankingAscendente() {

        reconstruirIndices();

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        indiceAVL.inorden((puntos, cliente) -> {

            resultado.agregarFinal(cliente);
        });

        return resultado;
    }

    /**
     * Obtiene los clientes desde la
     * mayor hasta la menor puntuacion.
     *
     * Recorre el AVL en orden ascendente
     * e inserta cada elemento al inicio
     * de la lista resultante.
     */
    public ListaEnlazada<Cliente>
    obtenerRankingDescendente() {

        reconstruirIndices();

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        indiceAVL.inorden((puntos, cliente) -> {

            resultado.agregarInicio(cliente);
        });

        return resultado;
    }

    /**
     * Devuelve los primeros N clientes
     * del ranking descendente.
     *
     * Si N supera la cantidad de clientes,
     * devuelve todos los disponibles.
     */
    public ListaEnlazada<Cliente> obtenerTop(
            int cantidad) {

        if (cantidad < 0) {

            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa"
            );
        }

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        if (cantidad == 0) {
            return resultado;
        }

        ListaEnlazada<Cliente> ranking =
                obtenerRankingDescendente();

        final int[] contador = {0};

        ranking.recorrer(cliente -> {

            if (contador[0] < cantidad) {

                resultado.agregarFinal(cliente);

                contador[0]++;
            }
        });

        return resultado;
    }

    // ==========================================
    // CONSULTAS POR RANGO
    // ==========================================

    /**
     * Obtiene todos los clientes que
     * pertenecen a un rango determinado.
     *
     * Los devuelve en orden descendente
     * de puntuacion.
     */
    public ListaEnlazada<Cliente> listarPorRango(
            Rango rango) {

        Objects.requireNonNull(
                rango,
                "El rango no puede ser null"
        );

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        obtenerRankingDescendente().recorrer(
                cliente -> {

                    if (rango.contienePuntos(
                            cliente.getPuntos())) {

                        resultado.agregarFinal(cliente);
                    }
                }
        );

        return resultado;
    }

    /**
     * Cuenta los clientes de un rango.
     */
    public int cantidadClientesPorRango(
            Rango rango) {

        return listarPorRango(rango).tamanio();
    }

    /**
     * Devuelve clientes cuya puntuacion
     * esta dentro de un intervalo cerrado.
     *
     * Incluye ambos limites.
     */
    public ListaEnlazada<Cliente> buscarEnIntervalo(
            int minimo,
            int maximo) {

        validarPuntos(minimo);
        validarPuntos(maximo);

        if (minimo > maximo) {

            throw new IllegalArgumentException(
                    "El minimo no puede superar "
                            + "el maximo"
            );
        }

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        obtenerRankingAscendente().recorrer(
                cliente -> {

                    int puntos = cliente.getPuntos();

                    if (puntos >= minimo
                            && puntos <= maximo) {

                        resultado.agregarFinal(cliente);
                    }
                }
        );

        return resultado;
    }

    // ==========================================
    // POSICIONES DEL RANKING
    // ==========================================

    /**
     * Devuelve la posicion de un cliente
     * dentro del ranking descendente.
     *
     * La primera posicion es 1.
     *
     * Si dos clientes tienen los mismos
     * puntos, ocupan la misma posicion.
     *
     * Devuelve -1 si no esta registrado.
     */
    public int obtenerPosicion(
            String idCliente) {

        Cliente objetivo =
                clienteService.obtenerCliente(idCliente);

        int puntosObjetivo = objetivo.getPuntos();

        ListaEnlazada<Cliente> ranking =
                obtenerRankingDescendente();

        final int[] mayores = {0};

        ranking.recorrer(cliente -> {

            if (cliente.getPuntos() > puntosObjetivo) {

                mayores[0]++;
            }
        });

        return mayores[0] + 1;
    }

    /**
     * Obtiene el cliente con mayor puntuacion.
     *
     * Cuando existen empates devuelve
     * uno de los clientes empatados.
     *
     * Devuelve null si no hay clientes.
     */
    public Cliente obtenerClienteMayorPuntuacion() {

        ListaEnlazada<Cliente> ranking =
                obtenerRankingDescendente();

        if (ranking.estaVacia()) {
            return null;
        }

        return ranking.obtenerPrimero();
    }

    /**
     * Obtiene el cliente con menor puntuacion.
     */
    public Cliente obtenerClienteMenorPuntuacion() {

        ListaEnlazada<Cliente> ranking =
                obtenerRankingAscendente();

        if (ranking.estaVacia()) {
            return null;
        }

        return ranking.obtenerPrimero();
    }

    // ==========================================
    // ESTADISTICAS DE PUNTOS
    // ==========================================

    /**
     * Suma los puntos de los clientes.
     *
     * Utiliza long para evitar desbordamiento
     * al acumular multiples enteros.
     */
    public long totalPuntosSistema() {

        final long[] total = {0L};

        clienteService.recorrerClientes(cliente -> {

            total[0] = Math.addExact(
                    total[0],
                    cliente.getPuntos()
            );
        });

        return total[0];
    }

    /**
     * Calcula el promedio de puntos
     * de los clientes registrados.
     */
    public double promedioPuntos() {

        int cantidad =
                clienteService.cantidadClientes();

        if (cantidad == 0) {
            return 0.0;
        }

        return (double) totalPuntosSistema()
                / cantidad;
    }

    /**
     * Consulta los puntos maximos
     * existentes en el sistema.
     *
     * Devuelve cero cuando no hay clientes.
     */
    public int maximaPuntuacion() {

        reconstruirIndices();

        if (indiceAVL.estaVacio()) {
            return 0;
        }

        return indiceAVL.maximo();
    }

    /**
     * Consulta los puntos minimos.
     */
    public int minimaPuntuacion() {

        reconstruirIndices();

        if (indiceAVL.estaVacio()) {
            return 0;
        }

        return indiceAVL.minimo();
    }

    // ==========================================
    // COMPARACION ABB VS AVL
    // ==========================================

    /**
     * Devuelve la altura del ABB.
     */
    public int obtenerAlturaABB() {

        reconstruirIndices();

        return indiceABB.altura();
    }

    /**
     * Devuelve la altura del AVL.
     */
    public int obtenerAlturaAVL() {

        reconstruirIndices();

        return indiceAVL.altura();
    }

    /**
     * Cuenta los nodos distintos del ABB.
     *
     * Varios clientes con igual puntuacion
     * comparten una misma clave.
     */
    public int cantidadNodosABB() {

        reconstruirIndices();

        return indiceABB.cantidadNodos();
    }

    /**
     * Cuenta los nodos distintos del AVL.
     */
    public int cantidadNodosAVL() {

        reconstruirIndices();

        return indiceAVL.cantidadNodos();
    }

    /**
     * Devuelve el numero total de clientes
     * indexados, incluyendo puntuaciones
     * repetidas.
     */
    public int cantidadClientesIndexados() {

        reconstruirIndices();

        return cantidadIndexada;
    }

    /**
     * Muestra las principales metricas
     * de ambos arboles.
     */
    public String generarComparacionArboles() {

        reconstruirIndices();

        StringBuilder resultado =
                new StringBuilder();

        resultado.append(
                "COMPARACION DE ARBOLES\n"
        );

        resultado.append(
                "============================\n"
        );

        resultado.append(
                "Clientes indexados: "
        ).append(cantidadIndexada).append("\n");

        resultado.append(
                "Nodos ABB: "
        ).append(indiceABB.cantidadNodos()).append("\n");

        resultado.append(
                "Nodos AVL: "
        ).append(indiceAVL.cantidadNodos()).append("\n");

        resultado.append(
                "Altura ABB: "
        ).append(indiceABB.altura()).append("\n");

        resultado.append(
                "Altura AVL: "
        ).append(indiceAVL.altura()).append("\n");

        resultado.append(
                "Integridad ABB: "
        ).append(indiceABB.verificarIntegridad()).append("\n");

        resultado.append(
                "Integridad AVL: "
        ).append(indiceAVL.verificarIntegridad()).append("\n");

        return resultado.toString();
    }

    // ==========================================
    // RECORRIDOS PERSONALIZADOS
    // ==========================================

    /**
     * Recorre los clientes ordenados
     * de menor a mayor puntuacion.
     */
    public void recorrerAscendente(
            Consumer<Cliente> accion) {

        Objects.requireNonNull(
                accion,
                "La accion no puede ser null"
        );

        obtenerRankingAscendente().recorrer(accion);
    }

    /**
     * Recorre los clientes ordenados
     * de mayor a menor puntuacion.
     */
    public void recorrerDescendente(
            Consumer<Cliente> accion) {

        Objects.requireNonNull(
                accion,
                "La accion no puede ser null"
        );

        obtenerRankingDescendente().recorrer(accion);
    }

    // ==========================================
    // VERIFICACION DE INTEGRIDAD
    // ==========================================

    /**
     * Comprueba:
     *
     * - Integridad de ClienteService.
     * - Integridad estructural de ABB y AVL.
     * - Igual cantidad de clientes indexados.
     * - Igual cantidad de claves distintas.
     * - Coincidencia entre claves y puntos.
     * - Rangos correspondientes.
     *
     * Reconstruye ambos indices antes
     * de realizar la verificacion.
     */
    public boolean verificarIntegridad() {

        if (!clienteService.verificarIntegridad()) {
            return false;
        }

        reconstruirIndices();

        if (!indiceABB.verificarIntegridad()
                || !indiceAVL.verificarIntegridad()) {

            return false;
        }

        int cantidadReal =
                clienteService.cantidadClientes();

        if (cantidadIndexada != cantidadReal) {
            return false;
        }

        if (indiceABB.tamanio() != cantidadReal
                || indiceAVL.tamanio() != cantidadReal) {

            return false;
        }

        if (indiceABB.cantidadNodos()
                != indiceAVL.cantidadNodos()) {

            return false;
        }

        final boolean[] valido = {true};

        final int[] cantidadABB = {0};
        final int[] cantidadAVL = {0};

        indiceABB.inorden((puntos, cliente) -> {

            cantidadABB[0]++;

            if (cliente == null
                    || cliente.getPuntos() != puntos
                    || cliente.getRango()
                    != Rango.calcularRango(puntos)
                    || clienteService.buscarCliente(
                    cliente.getId()) != cliente) {

                valido[0] = false;
            }

            if (!indiceAVL.contiene(
                    puntos,
                    cliente)) {

                valido[0] = false;
            }
        });

        indiceAVL.inorden((puntos, cliente) -> {

            cantidadAVL[0]++;

            if (cliente == null
                    || cliente.getPuntos() != puntos
                    || clienteService.buscarCliente(
                    cliente.getId()) != cliente) {

                valido[0] = false;
            }

            if (!indiceABB.contiene(
                    puntos,
                    cliente)) {

                valido[0] = false;
            }
        });

        if (cantidadABB[0] != cantidadReal
                || cantidadAVL[0] != cantidadReal) {

            valido[0] = false;
        }

        return valido[0];
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarPuntos(int puntos) {

        if (puntos < 0) {

            throw new IllegalArgumentException(
                    "Los puntos no pueden ser negativos"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        StringBuilder resultado =
                new StringBuilder();

        resultado.append(
                "RANKING DE CLIENTES\n"
        );

        resultado.append(
                "============================\n"
        );

        final int[] posicion = {1};

        recorrerDescendente(cliente -> {

            resultado.append(posicion[0]++)
                    .append(". ")
                    .append(cliente.getNombre())
                    .append(" - ")
                    .append(cliente.getPuntos())
                    .append(" puntos - ")
                    .append(cliente.getRango())
                    .append("\n");
        });

        return resultado.toString();
    }
}
