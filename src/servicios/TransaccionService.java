
package servicios;

import estructuras.GrafoDirigido;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import estructuras.TablaHash;

import modelo.Cliente;
import modelo.Monedero;
import modelo.TipoTransaccion;
import modelo.Transaccion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Servicio principal de operaciones financieras.
 *
 * Permite:
 * - Depositos.
 * - Retiros.
 * - Transferencias.
 * - Gastos.
 * - Consultas de historial.
 * - Reversion de transacciones.
 * - Analisis de transferencias con grafos.
 *
 * Estructuras propias:
 * - ListaEnlazada: historial.
 * - Pila: reversiones LIFO.
 * - TablaHash: busqueda por ID.
 * - GrafoDirigido: transferencias.
 *
 * No utiliza colecciones de Java.
 *
 * Los puntos y notificaciones se integraran
 * posteriormente desde sus servicios.
 */
public class TransaccionService {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;
    private final MonederoService monederoService;

    private final ListaEnlazada<Transaccion> historial;

    private final Pila<Transaccion> pilaReversiones;

    private final TablaHash<String, Transaccion>
            indiceTransacciones;

    private BigDecimal totalComisiones;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public TransaccionService(
            ClienteService clienteService,
            MonederoService monederoService) {

        this.clienteService = Objects.requireNonNull(
                clienteService,
                "ClienteService no puede ser null"
        );

        this.monederoService = Objects.requireNonNull(
                monederoService,
                "MonederoService no puede ser null"
        );

        this.historial = new ListaEnlazada<>();

        this.pilaReversiones = new Pila<>();

        this.indiceTransacciones = new TablaHash<>();

        this.totalComisiones =
                new BigDecimal("0.00");
    }

    // ==========================================
    // DEPOSITOS
    // ==========================================

    /**
     * Deposita dinero en un monedero activo.
     */
    public Transaccion depositar(
            String idCliente,
            String idMonedero,
            BigDecimal monto) {

        BigDecimal cantidad = validarMonto(monto);

        Monedero monedero =
                monederoService.obtenerMonederoActivo(
                        idCliente,
                        idMonedero
                );

        Transaccion transaccion =
                Transaccion.crearDeposito(
                        idCliente,
                        idMonedero,
                        cantidad
                );

        // Ejecutar movimiento financiero.
        monedero.depositar(cantidad);

        // Registrar solamente operaciones exitosas.
        completarYRegistrar(transaccion, true);

        return transaccion;
    }

    // ==========================================
    // RETIROS
    // ==========================================

    /**
     * Retira dinero de un monedero.
     *
     * Rechaza retiros superiores al saldo.
     */
    public Transaccion retirar(
            String idCliente,
            String idMonedero,
            BigDecimal monto) {

        BigDecimal cantidad = validarMonto(monto);

        Monedero monedero =
                monederoService.obtenerMonederoActivo(
                        idCliente,
                        idMonedero
                );

        validarFondos(monedero, cantidad);

        Transaccion transaccion =
                Transaccion.crearRetiro(
                        idCliente,
                        idMonedero,
                        cantidad
                );

        monedero.retirar(cantidad);

        completarYRegistrar(transaccion, true);

        return transaccion;
    }

    // ==========================================
    // TRANSFERENCIAS
    // ==========================================

    /**
     * Transferencia sin comision.
     *
     * Util para operaciones basicas.
     */
    public Transaccion transferir(
            String idClienteOrigen,
            String idMonederoOrigen,
            String idClienteDestino,
            String idMonederoDestino,
            BigDecimal monto) {

        return transferir(
                idClienteOrigen,
                idMonederoOrigen,
                idClienteDestino,
                idMonederoDestino,
                monto,
                BigDecimal.ZERO
        );
    }

    /**
     * Transfiere dinero entre dos monederos.
     *
     * El origen paga:
     * monto + comision.
     *
     * El destino recibe:
     * monto.
     *
     * La comision se registra separadamente.
     */
    public Transaccion transferir(
            String idClienteOrigen,
            String idMonederoOrigen,
            String idClienteDestino,
            String idMonederoDestino,
            BigDecimal monto,
            BigDecimal comision) {

        BigDecimal cantidad = validarMonto(monto);

        BigDecimal tarifa = validarComision(comision);

        if (idMonederoOrigen != null
                && idMonederoOrigen.equals(
                idMonederoDestino)) {

            throw new IllegalArgumentException(
                    "No se puede transferir "
                            + "al mismo monedero"
            );
        }

        Monedero origen =
                monederoService.obtenerMonederoActivo(
                        idClienteOrigen,
                        idMonederoOrigen
                );

        Monedero destino =
                monederoService.obtenerMonederoActivo(
                        idClienteDestino,
                        idMonederoDestino
                );

        BigDecimal costoTotal =
                cantidad.add(tarifa);

        validarFondos(origen, costoTotal);

        Transaccion transaccion =
                Transaccion.crearTransferencia(
                        idClienteOrigen,
                        idClienteDestino,
                        idMonederoOrigen,
                        idMonederoDestino,
                        cantidad,
                        tarifa
                );

        // Primero retirar del origen.
        origen.retirar(costoTotal);

        // Si falla el deposito, restaurar el origen.
        try {

            destino.depositar(cantidad);

        } catch (RuntimeException error) {

            origen.depositar(costoTotal);

            throw error;
        }

        totalComisiones =
                totalComisiones.add(tarifa);

        completarYRegistrar(transaccion, true);

        return transaccion;
    }

    // ==========================================
    // GASTOS
    // ==========================================

    /**
     * Registra un gasto desde un monedero.
     *
     * Un gasto reduce el saldo, pero no
     * transfiere dinero a otro cliente.
     */
    public Transaccion registrarGasto(
            String idCliente,
            String idMonedero,
            BigDecimal monto,
            String descripcion) {

        BigDecimal cantidad = validarMonto(monto);

        if (descripcion == null
                || descripcion.isBlank()) {

            throw new IllegalArgumentException(
                    "La descripcion es obligatoria"
            );
        }

        Monedero monedero =
                monederoService.obtenerMonederoActivo(
                        idCliente,
                        idMonedero
                );

        validarFondos(monedero, cantidad);

        Transaccion transaccion =
                Transaccion.crearGasto(
                        idCliente,
                        idMonedero,
                        cantidad,
                        descripcion
                );

        monedero.retirar(cantidad);

        completarYRegistrar(transaccion, false);

        return transaccion;
    }

    // ==========================================
    // REGISTRO INTERNO
    // ==========================================

    /**
     * Finaliza y registra una transaccion.
     *
     * reversible = true:
     * Se incorpora a la pila.
     */
    private void completarYRegistrar(
            Transaccion transaccion,
            boolean reversible) {

        transaccion.marcarCompletada();

        historial.agregarFinal(transaccion);

        indiceTransacciones.insertar(
                transaccion.getId(),
                transaccion
        );

        if (reversible) {
            pilaReversiones.apilar(transaccion);
        }
    }

    // ==========================================
    // BUSQUEDA DE TRANSACCIONES
    // ==========================================

    /**
     * Busca una transaccion por ID
     * utilizando nuestra tabla hash.
     */
    public Transaccion buscarTransaccion(
            String idTransaccion) {

        validarId(
                idTransaccion,
                "ID de transaccion"
        );

        return indiceTransacciones.obtener(
                idTransaccion
        );
    }

    /**
     * Obtiene una transaccion obligatoria.
     */
    public Transaccion obtenerTransaccion(
            String idTransaccion) {

        Transaccion transaccion =
                buscarTransaccion(idTransaccion);

        if (transaccion == null) {

            throw new IllegalArgumentException(
                    "No existe la transaccion: "
                            + idTransaccion
            );
        }

        return transaccion;
    }

    // ==========================================
    // HISTORIAL
    // ==========================================

    /**
     * Devuelve una nueva lista con todas
     * las transacciones registradas.
     *
     * Conserva las referencias originales
     * de los objetos Transaccion.
     */
    public ListaEnlazada<Transaccion> obtenerHistorial() {

        ListaEnlazada<Transaccion> copia =
                new ListaEnlazada<>();

        historial.recorrer(copia::agregarFinal);

        return copia;
    }

    /**
     * Obtiene las transacciones donde el
     * cliente participa como origen o destino.
     */
    public ListaEnlazada<Transaccion>
    obtenerHistorialCliente(String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaEnlazada<Transaccion> resultado =
                new ListaEnlazada<>();

        historial.recorrer(transaccion -> {

            boolean esOrigen =
                    idCliente.equals(
                            transaccion.getIdClienteOrigen()
                    );

            boolean esDestino =
                    idCliente.equals(
                            transaccion.getIdClienteDestino()
                    );

            if (esOrigen || esDestino) {
                resultado.agregarFinal(transaccion);
            }
        });

        return resultado;
    }

    /**
     * Filtra transacciones por tipo.
     */
    public ListaEnlazada<Transaccion> obtenerPorTipo(
            TipoTransaccion tipo) {

        Objects.requireNonNull(
                tipo,
                "El tipo no puede ser null"
        );

        ListaEnlazada<Transaccion> resultado =
                new ListaEnlazada<>();

        historial.recorrer(transaccion -> {

            if (transaccion.getTipo() == tipo) {
                resultado.agregarFinal(transaccion);
            }
        });

        return resultado;
    }

    /**
     * Devuelve las ultimas N transacciones,
     * comenzando por la mas reciente.
     */
    public ListaEnlazada<Transaccion>
    obtenerUltimasTransacciones(int cantidad) {

        if (cantidad < 0) {

            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa"
            );
        }

        ListaEnlazada<Transaccion> resultado =
                new ListaEnlazada<>();

        historial.recorrer(resultado::agregarInicio);

        while (resultado.tamanio() > cantidad) {
            resultado.eliminarFinal();
        }

        return resultado;
    }

    // ==========================================
    // REVERSIONES
    // ==========================================

    /**
     * Consulta la ultima transaccion reversible.
     */
    public Transaccion consultarUltimaReversible() {

        if (pilaReversiones.estaVacia()) {
            return null;
        }

        return pilaReversiones.cima();
    }

    /**
     * Revierte la ultima transaccion reversible.
     *
     * Principio LIFO:
     * Last In, First Out.
     *
     * La transaccion original conserva
     * su historial y pasa a REVERTIDA.
     *
     * Se crea una nueva transaccion REVERSO.
     *
     * IMPORTANTE:
     * El ajuste de puntos se integrara
     * posteriormente con PuntosService.
     */
    public Transaccion revertirUltimaTransaccion() {

        if (pilaReversiones.estaVacia()) {

            throw new IllegalStateException(
                    "No existen transacciones reversibles"
            );
        }

        // Consultar sin eliminar todavia.
        Transaccion original =
                pilaReversiones.cima();

        if (!original.estaCompletada()) {

            throw new IllegalStateException(
                    "La transaccion no esta completada"
            );
        }

        // Crear registro antes de compensar.
        Transaccion reversion =
                Transaccion.crearReversion(original);

        switch (original.getTipo()) {

            case DEPOSITO ->
                    revertirDeposito(original);

            case RETIRO ->
                    revertirRetiro(original);

            case TRANSFERENCIA ->
                    revertirTransferencia(original);

            default -> throw new IllegalStateException(
                    "Tipo de transaccion no reversible"
            );
        }

        // Retirar de la pila solamente
        // despues de realizar la compensacion.
        pilaReversiones.desapilar();

        original.marcarRevertida();

        // La reversion no se agrega nuevamente
        // a la pila de operaciones reversibles.
        completarYRegistrar(reversion, false);

        return reversion;
    }

    /**
     * Compensa un deposito.
     *
     * Retira el dinero que se habia depositado.
     */
    private void revertirDeposito(
            Transaccion original) {

        Monedero destino =
                monederoService.obtenerMonederoActivo(
                        original.getIdClienteDestino(),
                        original.getIdMonederoDestino()
                );

        validarFondos(
                destino,
                original.getMonto()
        );

        destino.retirar(original.getMonto());
    }

    /**
     * Compensa un retiro.
     *
     * Devuelve el dinero al monedero original.
     */
    private void revertirRetiro(
            Transaccion original) {

        Monedero origen =
                monederoService.obtenerMonederoActivo(
                        original.getIdClienteOrigen(),
                        original.getIdMonederoOrigen()
                );

        origen.depositar(original.getMonto());
    }

    /**
     * Compensa una transferencia.
     *
     * El destino devuelve el monto.
     * El origen recupera monto y comision.
     */
    private void revertirTransferencia(
            Transaccion original) {

        Monedero origen =
                monederoService.obtenerMonederoActivo(
                        original.getIdClienteOrigen(),
                        original.getIdMonederoOrigen()
                );

        Monedero destino =
                monederoService.obtenerMonederoActivo(
                        original.getIdClienteDestino(),
                        original.getIdMonederoDestino()
                );

        BigDecimal monto = original.getMonto();

        BigDecimal devolucion =
                original.calcularCostoTotal();

        validarFondos(destino, monto);

        destino.retirar(monto);

        try {

            origen.depositar(devolucion);

        } catch (RuntimeException error) {

            destino.depositar(monto);

            throw error;
        }

        totalComisiones = totalComisiones.subtract(
                original.getComision()
        );
    }

    // ==========================================
    // GRAFO DE TRANSFERENCIAS
    // ==========================================

    /**
     * Reconstruye un grafo dirigido utilizando
     * solamente transferencias completadas.
     *
     * Se reconstruye a partir del historial
     * para excluir operaciones revertidas.
     *
     * Devuelve un grafo independiente.
     */
    public GrafoDirigido obtenerGrafoTransferencias() {

        GrafoDirigido grafo =
                new GrafoDirigido();

        // Incorporar clientes como vertices.
        clienteService.recorrerClientes(cliente -> {
            grafo.agregarVertice(cliente.getId());
        });

        // Incorporar transferencias vigentes.
        historial.recorrer(transaccion -> {

            if (transaccion.getTipo()
                    == TipoTransaccion.TRANSFERENCIA
                    && transaccion.estaCompletada()) {

                grafo.agregarArista(
                        transaccion.getIdClienteOrigen(),
                        transaccion.getIdClienteDestino(),
                        transaccion.getMonto()
                );
            }
        });

        return grafo;
    }

    /**
     * Obtiene el dinero transferido entre
     * dos clientes en operaciones vigentes.
     */
    public BigDecimal totalTransferidoEntre(
            String origen,
            String destino) {

        clienteService.obtenerCliente(origen);
        clienteService.obtenerCliente(destino);

        return obtenerGrafoTransferencias()
                .obtenerPeso(origen, destino);
    }

    // ==========================================
    // ESTADISTICAS
    // ==========================================

    /**
     * Cantidad de movimientos registrados,
     * incluyendo las reversiones.
     */
    public int cantidadTransacciones() {
        return historial.tamanio();
    }

    /**
     * Cantidad de operaciones disponibles
     * para revertirse mediante la pila.
     */
    public int cantidadReversibles() {
        return pilaReversiones.tamanio();
    }

    /**
     * Total neto de comisiones por
     * transferencias no revertidas.
     */
    public BigDecimal consultarTotalComisiones() {
        return totalComisiones;
    }

    /**
     * Cuenta las transacciones de un tipo.
     */
    public int contarPorTipo(TipoTransaccion tipo) {

        return obtenerPorTipo(tipo).tamanio();
    }

    /**
     * Suma los montos de las transacciones
     * completadas de un tipo determinado.
     *
     * No incluye operaciones revertidas.
     */
    public BigDecimal totalPorTipo(
            TipoTransaccion tipo) {

        Objects.requireNonNull(tipo);

        final BigDecimal[] total = {
                BigDecimal.ZERO
        };

        historial.recorrer(transaccion -> {

            if (transaccion.getTipo() == tipo
                    && transaccion.estaCompletada()) {

                total[0] = total[0].add(
                        transaccion.getMonto()
                );
            }
        });

        return total[0];
    }

    // ==========================================
    // VERIFICACION DE INTEGRIDAD
    // ==========================================

    /**
     * Comprueba la consistencia basica
     * entre historial, indice y pila.
     *
     * No sustituye una auditoria completa
     * de saldos ni de puntos.
     */
    public boolean verificarIntegridad() {

        if (historial.tamanio()
                != indiceTransacciones.tamanio()) {
            return false;
        }

        final boolean[] valido = {true};

        final TablaHash<String, Boolean> vistos =
                new TablaHash<>();

        final BigDecimal[] comisionesCalculadas = {
                BigDecimal.ZERO
        };

        historial.recorrer(transaccion -> {

            if (transaccion == null) {
                valido[0] = false;
                return;
            }

            String id = transaccion.getId();

            if (vistos.contieneClave(id)) {
                valido[0] = false;
                return;
            }

            vistos.insertar(id, true);

            if (indiceTransacciones.obtener(id)
                    != transaccion) {
                valido[0] = false;
            }

            if (transaccion.estaPendiente()
                    || transaccion.getEstado()
                    == Transaccion.Estado.FALLIDA
                    || transaccion.getEstado()
                    == Transaccion.Estado.CANCELADA) {

                valido[0] = false;
            }

            if (transaccion.getTipo()
                    == TipoTransaccion.TRANSFERENCIA
                    && transaccion.estaCompletada()) {

                comisionesCalculadas[0] =
                        comisionesCalculadas[0].add(
                                transaccion.getComision()
                        );
            }

            if (transaccion.esReversion()) {

                String idOriginal =
                        transaccion.getIdTransaccionOriginal();

                Transaccion original =
                        indiceTransacciones.obtener(idOriginal);

                if (original == null
                        || !original.estaRevertida()) {
                    valido[0] = false;
                }
            }
        });

        if (comisionesCalculadas[0].compareTo(
                totalComisiones) != 0) {

            valido[0] = false;
        }

        // Inspeccionar la pila sin modificarla.
        Pila<Transaccion> auxiliar = new Pila<>();

        while (!pilaReversiones.estaVacia()) {

            Transaccion transaccion =
                    pilaReversiones.desapilar();

            auxiliar.apilar(transaccion);

            if (!transaccion.estaCompletada()
                    || indiceTransacciones.obtener(
                    transaccion.getId()
            ) != transaccion) {

                valido[0] = false;
            }
        }

        // Restaurar la pila original.
        while (!auxiliar.estaVacia()) {

            pilaReversiones.apilar(
                    auxiliar.desapilar()
            );
        }

        return valido[0];
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private BigDecimal validarMonto(
            BigDecimal monto) {

        if (monto == null
                || monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
            );
        }

        return normalizar(monto);
    }

    private BigDecimal validarComision(
            BigDecimal comision) {

        if (comision == null
                || comision.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "La comision no puede ser negativa"
            );
        }

        return normalizar(comision);
    }

    private BigDecimal normalizar(
            BigDecimal monto) {

        try {

            return monto.setScale(
                    2,
                    RoundingMode.UNNECESSARY
            );

        } catch (ArithmeticException error) {

            throw new IllegalArgumentException(
                    "El monto admite maximo "
                            + "dos decimales significativos"
            );
        }
    }

    private void validarFondos(
            Monedero monedero,
            BigDecimal monto) {

        if (!monedero.tieneSaldoSuficiente(monto)) {

            throw new IllegalArgumentException(
                    "Saldo insuficiente en el monedero "
                            + monedero.getId()
            );
        }
    }

    private void validarId(
            String id,
            String campo) {

        if (id == null || id.isBlank()) {

            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        StringBuilder resultado =
                new StringBuilder(
                        "HISTORIAL DE TRANSACCIONES\n"
                );

        historial.recorrer(transaccion -> {

            resultado.append(transaccion)
                    .append("\n");
        });

        return resultado.toString();
    }
}
