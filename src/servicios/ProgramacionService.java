
package servicios;

import estructuras.ColaPrioridad;
import estructuras.ListaEnlazada;
import estructuras.TablaHash;

import modelo.Transaccion;
import modelo.TransaccionProgramada;
import modelo.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Servicio de transacciones programadas.
 *
 * PROYECTO:
 * Monedero Virtual con Sistema de Puntos.
 *
 * Funcionalidades:
 * - Programar depositos.
 * - Programar transferencias.
 * - Ejecutar operaciones vencidas.
 * - Gestionar operaciones recurrentes.
 * - Pausar y reanudar.
 * - Cancelar programaciones.
 * - Modificar fechas.
 * - Consultar historiales.
 * - Reintentar operaciones fallidas.
 *
 * ESTRUCTURAS PROPIAS:
 *
 * ColaPrioridad:
 * Ordena operaciones por fecha de ejecucion.
 *
 * TablaHash:
 * Permite buscar programaciones por ID.
 *
 * ListaEnlazada:
 * Conserva el registro de programaciones
 * y el historial de ejecuciones.
 *
 * IMPORTANTE:
 * Este servicio ejecuta las transacciones
 * financieras mediante TransaccionService.
 *
 * No utiliza colecciones de Java.
 */
public class ProgramacionService {

    // ==========================================
    // RESULTADO DE UNA EJECUCION
    // ==========================================

    /**
     * Representa el resultado de un intento
     * de ejecutar una programacion.
     *
     * Permite consultar si fue exitoso
     * y obtener la transaccion realizada.
     */
    public static class ResultadoEjecucion {

        private final String idProgramacion;

        private final boolean exitosa;

        private final Transaccion transaccion;

        private final String mensaje;

        private final LocalDateTime fechaIntento;

        public ResultadoEjecucion(
                String idProgramacion,
                boolean exitosa,
                Transaccion transaccion,
                String mensaje) {

            this.idProgramacion = idProgramacion;
            this.exitosa = exitosa;
            this.transaccion = transaccion;
            this.mensaje = mensaje;
            this.fechaIntento = LocalDateTime.now();
        }

        public String getIdProgramacion() {
            return idProgramacion;
        }

        public boolean isExitosa() {
            return exitosa;
        }

        public Transaccion getTransaccion() {
            return transaccion;
        }

        public String getMensaje() {
            return mensaje;
        }

        public LocalDateTime getFechaIntento() {
            return fechaIntento;
        }

        @Override
        public String toString() {

            return "ResultadoEjecucion{" +
                    "programacion='" + idProgramacion + '\'' +
                    ", exitosa=" + exitosa +
                    ", transaccion=" +
                    (transaccion == null
                            ? "null"
                            : transaccion.getId()) +
                    ", mensaje='" + mensaje + '\'' +
                    ", fecha=" + fechaIntento +
                    '}';
        }
    }

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;

    private final MonederoService monederoService;

    private final TransaccionService transaccionService;

    private final TablaHash<
            String,
            TransaccionProgramada
            > indiceProgramaciones;

    private final ListaEnlazada<TransaccionProgramada>
            programaciones;

    private final ColaPrioridad<TransaccionProgramada>
            colaPrioridad;

    private final ListaEnlazada<ResultadoEjecucion>
            historialEjecuciones;

    private int ejecucionesExitosas;

    private int ejecucionesFallidas;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ProgramacionService(
            ClienteService clienteService,
            MonederoService monederoService,
            TransaccionService transaccionService) {

        this.clienteService = Objects.requireNonNull(
                clienteService,
                "ClienteService no puede ser null"
        );

        this.monederoService = Objects.requireNonNull(
                monederoService,
                "MonederoService no puede ser null"
        );

        this.transaccionService = Objects.requireNonNull(
                transaccionService,
                "TransaccionService no puede ser null"
        );

        this.indiceProgramaciones =
                new TablaHash<>();

        this.programaciones =
                new ListaEnlazada<>();

        this.colaPrioridad = new ColaPrioridad<>(
                TransaccionProgramada::compareTo
        );

        this.historialEjecuciones =
                new ListaEnlazada<>();

        this.ejecucionesExitosas = 0;

        this.ejecucionesFallidas = 0;
    }

    // ==========================================
    // REGISTRO DE PROGRAMACIONES
    // ==========================================

    /**
     * Registra una programacion existente.
     *
     * Valida los clientes y monederos
     * antes de incorporarla al sistema.
     */
    public void registrarProgramacion(
            TransaccionProgramada programacion) {

        Objects.requireNonNull(
                programacion,
                "La programacion no puede ser null"
        );

        if (indiceProgramaciones.contieneClave(
                programacion.getId())) {

            throw new IllegalArgumentException(
                    "La programacion ya esta registrada"
            );
        }

        if (!programacion.estaActiva()) {

            throw new IllegalStateException(
                    "Solo se pueden registrar "
                            + "programaciones activas"
            );
        }

        if (!programacion.getFechaEjecucion()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "La fecha inicial debe ser futura"
            );
        }

        validarCuentas(programacion);

        indiceProgramaciones.insertar(
                programacion.getId(),
                programacion
        );

        programaciones.agregarFinal(programacion);

        reconstruirCola();
    }

    // ==========================================
    // PROGRAMAR DEPOSITOS
    // ==========================================

    /**
     * Programa un deposito futuro.
     *
     * Puede ser unico o recurrente.
     */
    public TransaccionProgramada programarDeposito(
            String idCliente,
            String idMonedero,
            BigDecimal monto,
            LocalDateTime fecha,
            TransaccionProgramada.Frecuencia frecuencia) {

        TransaccionProgramada nueva =
                TransaccionProgramada.crearDeposito(
                        idCliente,
                        idMonedero,
                        monto,
                        fecha,
                        frecuencia
                );

        registrarProgramacion(nueva);

        return nueva;
    }

    /**
     * Programa un deposito de una sola vez.
     */
    public TransaccionProgramada programarDeposito(
            String idCliente,
            String idMonedero,
            BigDecimal monto,
            LocalDateTime fecha) {

        return programarDeposito(
                idCliente,
                idMonedero,
                monto,
                fecha,
                TransaccionProgramada.Frecuencia.UNICA
        );
    }

    // ==========================================
    // PROGRAMAR TRANSFERENCIAS
    // ==========================================

    /**
     * Programa una transferencia futura.
     *
     * La transferencia se ejecutara
     * utilizando TransaccionService.
     */
    public TransaccionProgramada programarTransferencia(
            String idClienteOrigen,
            String idMonederoOrigen,
            String idClienteDestino,
            String idMonederoDestino,
            BigDecimal monto,
            LocalDateTime fecha,
            TransaccionProgramada.Frecuencia frecuencia) {

        TransaccionProgramada nueva =
                TransaccionProgramada.crearTransferencia(
                        idClienteOrigen,
                        idClienteDestino,
                        idMonederoOrigen,
                        idMonederoDestino,
                        monto,
                        fecha,
                        frecuencia
                );

        registrarProgramacion(nueva);

        return nueva;
    }

    /**
     * Programa una transferencia unica.
     */
    public TransaccionProgramada programarTransferencia(
            String idClienteOrigen,
            String idMonederoOrigen,
            String idClienteDestino,
            String idMonederoDestino,
            BigDecimal monto,
            LocalDateTime fecha) {

        return programarTransferencia(
                idClienteOrigen,
                idMonederoOrigen,
                idClienteDestino,
                idMonederoDestino,
                monto,
                fecha,
                TransaccionProgramada.Frecuencia.UNICA
        );
    }

    // ==========================================
    // VALIDACION DE CUENTAS
    // ==========================================

    /**
     * Comprueba que los participantes y
     * monederos existan y esten activos.
     *
     * Esta validacion se ejecuta tambien
     * al momento de realizar la operacion,
     * porque las cuentas pueden cambiar.
     */
    private void validarCuentas(
            TransaccionProgramada programacion) {

        if (programacion.getTipo()
                == TipoTransaccion.DEPOSITO) {

            monederoService.obtenerMonederoActivo(
                    programacion.getIdClienteDestino(),
                    programacion.getIdMonederoDestino()
            );

        } else if (programacion.getTipo()
                == TipoTransaccion.TRANSFERENCIA) {

            monederoService.obtenerMonederoActivo(
                    programacion.getIdClienteOrigen(),
                    programacion.getIdMonederoOrigen()
            );

            monederoService.obtenerMonederoActivo(
                    programacion.getIdClienteDestino(),
                    programacion.getIdMonederoDestino()
            );

        } else {

            throw new IllegalArgumentException(
                    "Tipo de programacion no permitido"
            );
        }
    }

    // ==========================================
    // COLA DE PRIORIDAD
    // ==========================================

    /**
     * Reconstruye la cola con las
     * programaciones activas.
     *
     * Esto es importante porque las fechas
     * pueden cambiar al reprogramar, pausar
     * o ejecutar operaciones recurrentes.
     *
     * Evita modificar claves de prioridad
     * mientras los objetos se encuentran
     * organizados dentro del heap.
     */
    public void reconstruirCola() {

        colaPrioridad.limpiar();

        programaciones.recorrer(programacion -> {

            if (programacion.estaActiva()) {

                colaPrioridad.encolar(
                        programacion
                );
            }
        });
    }

    /**
     * Consulta la siguiente programacion
     * activa segun su fecha.
     *
     * Devuelve null cuando no hay pendientes.
     */
    public TransaccionProgramada
    consultarProximaProgramacion() {

        reconstruirCola();

        if (colaPrioridad.estaVacia()) {
            return null;
        }

        return colaPrioridad.frente();
    }

    /**
     * Consulta cuantas programaciones
     * activas contiene la cola.
     */
    public int cantidadEnCola() {

        reconstruirCola();

        return colaPrioridad.tamanio();
    }

    // ==========================================
    // EJECUCION INTERNA
    // ==========================================

    /**
     * Ejecuta una programacion que ya
     * fue retirada de la cola.
     *
     * Solo registra una ejecucion exitosa
     * despues de confirmar que la operacion
     * financiera fue completada.
     *
     * En caso de error, conserva la
     * programacion activa para reintentarla
     * durante una llamada posterior.
     */
    private ResultadoEjecucion ejecutarInternamente(
            TransaccionProgramada programacion) {

        try {

            if (!programacion.estaListaParaEjecutar()) {

                throw new IllegalStateException(
                        "La programacion no esta lista"
                );
            }

            validarCuentas(programacion);

            Transaccion transaccion;

            if (programacion.getTipo()
                    == TipoTransaccion.DEPOSITO) {

                transaccion = transaccionService.depositar(
                        programacion.getIdClienteDestino(),
                        programacion.getIdMonederoDestino(),
                        programacion.getMonto()
                );

            } else if (programacion.getTipo()
                    == TipoTransaccion.TRANSFERENCIA) {

                transaccion = transaccionService.transferir(
                        programacion.getIdClienteOrigen(),
                        programacion.getIdMonederoOrigen(),
                        programacion.getIdClienteDestino(),
                        programacion.getIdMonederoDestino(),
                        programacion.getMonto()
                );

            } else {

                throw new IllegalStateException(
                        "Tipo de transaccion no soportado"
                );
            }

            // Actualizar la fecha y el estado
            // despues de la operacion financiera.
            programacion.registrarEjecucionExitosa();

            ResultadoEjecucion resultado =
                    new ResultadoEjecucion(
                            programacion.getId(),
                            true,
                            transaccion,
                            "Operacion ejecutada correctamente"
                    );

            ejecucionesExitosas++;

            historialEjecuciones.agregarFinal(resultado);

            return resultado;

        } catch (RuntimeException error) {

            ejecucionesFallidas++;

            ResultadoEjecucion resultado =
                    new ResultadoEjecucion(
                            programacion.getId(),
                            false,
                            null,
                            error.getMessage() == null
                                    ? "Error desconocido"
                                    : error.getMessage()
                    );

            historialEjecuciones.agregarFinal(resultado);

            return resultado;
        }
    }

    // ==========================================
    // EJECUTAR PROXIMA OPERACION
    // ==========================================

    /**
     * Intenta ejecutar la operacion activa
     * con la fecha mas cercana.
     *
     * Devuelve null si la cola esta vacia
     * o la proxima fecha aun no ha llegado.
     *
     * Si una operacion falla, queda activa
     * para un reintento posterior.
     */
    public ResultadoEjecucion ejecutarProxima() {

        reconstruirCola();

        if (colaPrioridad.estaVacia()) {
            return null;
        }

        TransaccionProgramada siguiente =
                colaPrioridad.frente();

        if (!siguiente.estaListaParaEjecutar()) {
            return null;
        }

        // Retirar del heap antes de modificar
        // su fecha en caso de recurrencia.
        siguiente = colaPrioridad.desencolar();

        ResultadoEjecucion resultado =
                ejecutarInternamente(siguiente);

        reconstruirCola();

        return resultado;
    }

    // ==========================================
    // EJECUTAR TODAS LAS VENCIDAS
    // ==========================================

    /**
     * Ejecuta las operaciones que estan
     * vencidas en el momento de la llamada.
     *
     * Cada programacion se intenta como
     * maximo una vez por llamada.
     *
     * Si falla una operacion, se registra
     * el error y se continua con las demas.
     *
     * Devuelve la lista de resultados.
     */
    public ListaEnlazada<ResultadoEjecucion>
    ejecutarPendientes() {

        return ejecutarPendientes(
                Integer.MAX_VALUE
        );
    }

    /**
     * Ejecuta como maximo una cantidad
     * determinada de operaciones vencidas.
     *
     * El limite evita procesar demasiadas
     * operaciones en una sola llamada.
     */
    public ListaEnlazada<ResultadoEjecucion>
    ejecutarPendientes(int limite) {

        if (limite < 0) {

            throw new IllegalArgumentException(
                    "El limite no puede ser negativo"
            );
        }

        ListaEnlazada<ResultadoEjecucion> resultados =
                new ListaEnlazada<>();

        if (limite == 0) {
            return resultados;
        }

        reconstruirCola();

        // Capturar las operaciones vencidas
        // antes de comenzar las ejecuciones.
        //
        // Asi, una programacion fallida no
        // bloquea las otras operaciones.
        ListaEnlazada<TransaccionProgramada> vencidas =
                new ListaEnlazada<>();

        LocalDateTime referencia =
                LocalDateTime.now();

        while (!colaPrioridad.estaVacia()
                && vencidas.tamanio() < limite) {

            TransaccionProgramada siguiente =
                    colaPrioridad.frente();

            if (siguiente.getFechaEjecucion()
                    .isAfter(referencia)) {

                break;
            }

            vencidas.agregarFinal(
                    colaPrioridad.desencolar()
            );
        }

        vencidas.recorrer(programacion -> {

            if (programacion.estaListaParaEjecutar()) {

                ResultadoEjecucion resultado =
                        ejecutarInternamente(programacion);

                resultados.agregarFinal(resultado);
            }
        });

        reconstruirCola();

        return resultados;
    }

    // ==========================================
    // BUSQUEDA DE PROGRAMACIONES
    // ==========================================

    /**
     * Busca una programacion por ID
     * mediante nuestra tabla hash.
     *
     * Devuelve null si no existe.
     */
    public TransaccionProgramada buscarProgramacion(
            String idProgramacion) {

        validarId(idProgramacion);

        return indiceProgramaciones.obtener(
                idProgramacion
        );
    }

    /**
     * Obtiene una programacion obligatoria.
     */
    public TransaccionProgramada obtenerProgramacion(
            String idProgramacion) {

        TransaccionProgramada programacion =
                buscarProgramacion(idProgramacion);

        if (programacion == null) {

            throw new IllegalArgumentException(
                    "No existe la programacion: "
                            + idProgramacion
            );
        }

        return programacion;
    }

    // ==========================================
    // PAUSAR, REANUDAR Y CANCELAR
    // ==========================================

    /**
     * Pausa una programacion activa.
     */
    public void pausarProgramacion(
            String idProgramacion) {

        TransaccionProgramada programacion =
                obtenerProgramacion(idProgramacion);

        programacion.pausar();

        reconstruirCola();
    }

    /**
     * Reactiva una programacion pausada.
     *
     * La entidad recalcula la fecha
     * cuando corresponde.
     */
    public void reanudarProgramacion(
            String idProgramacion) {

        TransaccionProgramada programacion =
                obtenerProgramacion(idProgramacion);

        validarCuentas(programacion);

        programacion.reanudar();

        reconstruirCola();
    }

    /**
     * Cancela una programacion.
     *
     * Conserva el registro historico,
     * pero la elimina de la cola activa.
     */
    public void cancelarProgramacion(
            String idProgramacion) {

        TransaccionProgramada programacion =
                obtenerProgramacion(idProgramacion);

        programacion.cancelar();

        reconstruirCola();
    }

    // ==========================================
    // REPROGRAMACION
    // ==========================================

    /**
     * Modifica la fecha de ejecucion.
     *
     * Reconstruye la cola despues
     * del cambio de prioridad.
     */
    public void reprogramar(
            String idProgramacion,
            LocalDateTime nuevaFecha) {

        TransaccionProgramada programacion =
                obtenerProgramacion(idProgramacion);

        programacion.reprogramar(nuevaFecha);

        reconstruirCola();
    }

    // ==========================================
    // LISTADOS
    // ==========================================

    /**
     * Devuelve una nueva lista con todas
     * las programaciones registradas.
     *
     * Los objetos internos son compartidos.
     */
    public ListaEnlazada<TransaccionProgramada>
    listarProgramaciones() {

        ListaEnlazada<TransaccionProgramada> resultado =
                new ListaEnlazada<>();

        programaciones.recorrer(
                resultado::agregarFinal
        );

        return resultado;
    }

    /**
     * Devuelve las programaciones
     * activas ordenadas por fecha.
     */
    public ListaEnlazada<TransaccionProgramada>
    listarProgramacionesActivas() {

        reconstruirCola();

        ListaEnlazada<TransaccionProgramada> resultado =
                new ListaEnlazada<>();

        // Cola temporal: no destruimos la cola
        // principal mientras la consultamos.
        ColaPrioridad<TransaccionProgramada> auxiliar =
                new ColaPrioridad<>(
                        TransaccionProgramada::compareTo
                );

        programaciones.recorrer(programacion -> {

            if (programacion.estaActiva()) {
                auxiliar.encolar(programacion);
            }
        });

        while (!auxiliar.estaVacia()) {

            resultado.agregarFinal(
                    auxiliar.desencolar()
            );
        }

        return resultado;
    }

    /**
     * Lista las programaciones donde un
     * cliente participa como origen o destino.
     */
    public ListaEnlazada<TransaccionProgramada>
    listarProgramacionesCliente(String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaEnlazada<TransaccionProgramada> resultado =
                new ListaEnlazada<>();

        programaciones.recorrer(programacion -> {

            boolean origen = idCliente.equals(
                    programacion.getIdClienteOrigen()
            );

            boolean destino = idCliente.equals(
                    programacion.getIdClienteDestino()
            );

            if (origen || destino) {

                resultado.agregarFinal(
                        programacion
                );
            }
        });

        return resultado;
    }

    /**
     * Obtiene programaciones de un
     * estado determinado.
     */
    public ListaEnlazada<TransaccionProgramada>
    listarPorEstado(
            TransaccionProgramada.Estado estado) {

        Objects.requireNonNull(
                estado,
                "El estado no puede ser null"
        );

        ListaEnlazada<TransaccionProgramada> resultado =
                new ListaEnlazada<>();

        programaciones.recorrer(programacion -> {

            if (programacion.getEstado() == estado) {

                resultado.agregarFinal(
                        programacion
                );
            }
        });

        return resultado;
    }

    // ==========================================
    // HISTORIAL DE EJECUCIONES
    // ==========================================

    /**
     * Devuelve una copia del historial
     * de intentos de ejecucion.
     *
     * Incluye resultados exitosos
     * y fallidos.
     */
    public ListaEnlazada<ResultadoEjecucion>
    obtenerHistorialEjecuciones() {

        ListaEnlazada<ResultadoEjecucion> resultado =
                new ListaEnlazada<>();

        historialEjecuciones.recorrer(
                resultado::agregarFinal
        );

        return resultado;
    }

    /**
     * Filtra los resultados de una
     * programacion especifica.
     */
    public ListaEnlazada<ResultadoEjecucion>
    obtenerHistorialProgramacion(
            String idProgramacion) {

        obtenerProgramacion(idProgramacion);

        ListaEnlazada<ResultadoEjecucion> resultado =
                new ListaEnlazada<>();

        historialEjecuciones.recorrer(ejecucion -> {

            if (ejecucion.getIdProgramacion()
                    .equals(idProgramacion)) {

                resultado.agregarFinal(ejecucion);
            }
        });

        return resultado;
    }

    // ==========================================
    // ESTADISTICAS
    // ==========================================

    public int cantidadProgramaciones() {

        return indiceProgramaciones.tamanio();
    }

    public int cantidadProgramacionesActivas() {

        return listarPorEstado(
                TransaccionProgramada.Estado.ACTIVA
        ).tamanio();
    }

    public int cantidadProgramacionesPausadas() {

        return listarPorEstado(
                TransaccionProgramada.Estado.PAUSADA
        ).tamanio();
    }

    public int cantidadProgramacionesCompletadas() {

        return listarPorEstado(
                TransaccionProgramada.Estado.COMPLETADA
        ).tamanio();
    }

    public int cantidadProgramacionesCanceladas() {

        return listarPorEstado(
                TransaccionProgramada.Estado.CANCELADA
        ).tamanio();
    }

    public int cantidadEjecucionesExitosas() {
        return ejecucionesExitosas;
    }

    public int cantidadEjecucionesFallidas() {
        return ejecucionesFallidas;
    }

    /**
     * Cuenta las programaciones activas
     * cuya fecha ya llego.
     */
    public int cantidadVencidas() {

        final int[] cantidad = {0};

        programaciones.recorrer(programacion -> {

            if (programacion.estaListaParaEjecutar()) {
                cantidad[0]++;
            }
        });

        return cantidad[0];
    }

    // ==========================================
    // VERIFICACION DE INTEGRIDAD
    // ==========================================

    /**
     * Comprueba:
     *
     * - Coincidencia entre tabla hash y lista.
     * - IDs unicos.
     * - Programaciones validas.
     * - Estados coherentes.
     * - Coincidencia con la cola activa.
     * - Historial de ejecuciones consistente.
     *
     * No audita saldos, puntos ni la
     * atomicidad de las operaciones.
     */
    public boolean verificarIntegridad() {

        if (programaciones.tamanio()
                != indiceProgramaciones.tamanio()) {

            return false;
        }

        final boolean[] valido = {true};

        TablaHash<String, Boolean> ids =
                new TablaHash<>();

        final int[] activas = {0};

        programaciones.recorrer(programacion -> {

            if (programacion == null) {
                valido[0] = false;
                return;
            }

            String id = programacion.getId();

            if (ids.contieneClave(id)) {
                valido[0] = false;
                return;
            }

            ids.insertar(id, true);

            if (indiceProgramaciones.obtener(id)
                    != programacion) {

                valido[0] = false;
            }

            if (programacion.getMonto() == null
                    || programacion.getMonto().compareTo(
                    BigDecimal.ZERO) <= 0) {

                valido[0] = false;
            }

            if (programacion.getFechaEjecucion()
                    == null) {

                valido[0] = false;
            }

            if (programacion.getTipo()
                    != TipoTransaccion.DEPOSITO
                    && programacion.getTipo()
                    != TipoTransaccion.TRANSFERENCIA) {

                valido[0] = false;
            }

            if (programacion.estaActiva()) {
                activas[0]++;
            }

            if (programacion.getEstado()
                    == TransaccionProgramada.Estado.COMPLETADA
                    && programacion.esRecurrente()) {

                valido[0] = false;
            }

            if (programacion.getEjecucionesRealizadas() < 0) {

                valido[0] = false;
            }

            if (programacion.getEjecucionesRealizadas() == 0
                    && programacion.getUltimaEjecucion()
                    != null) {

                valido[0] = false;
            }
        });

        reconstruirCola();

        if (colaPrioridad.tamanio() != activas[0]) {
            valido[0] = false;
        }

        final int[] exitosas = {0};
        final int[] fallidas = {0};

        historialEjecuciones.recorrer(resultado -> {

            if (resultado == null
                    || !indiceProgramaciones.contieneClave(
                    resultado.getIdProgramacion())) {

                valido[0] = false;
                return;
            }

            if (resultado.isExitosa()) {

                exitosas[0]++;

                if (resultado.getTransaccion() == null
                        || !resultado.getTransaccion()
                        .estaCompletada()) {

                    // Una transaccion puede haber sido
                    // revertida posteriormente.
                    if (resultado.getTransaccion() == null
                            || !resultado.getTransaccion()
                            .estaRevertida()) {

                        valido[0] = false;
                    }
                }

            } else {

                fallidas[0]++;

                if (resultado.getTransaccion() != null) {
                    valido[0] = false;
                }
            }
        });

        if (exitosas[0] != ejecucionesExitosas
                || fallidas[0] != ejecucionesFallidas) {

            valido[0] = false;
        }

        return valido[0];
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarId(String id) {

        if (id == null || id.isBlank()) {

            throw new IllegalArgumentException(
                    "El ID de la programacion "
                            + "es obligatorio"
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
                "TRANSACCIONES PROGRAMADAS\n"
        );

        resultado.append(
                "================================\n"
        );

        listarProgramaciones().recorrer(programacion -> {

            resultado.append(programacion)
                    .append("\n");
        });

        resultado.append(
                "--------------------------------\n"
        );

        resultado.append(
                "Programaciones: "
        ).append(cantidadProgramaciones()).append("\n");

        resultado.append(
                "Activas: "
        ).append(cantidadProgramacionesActivas()).append("\n");

        resultado.append(
                "Completadas: "
        ).append(cantidadProgramacionesCompletadas()).append("\n");

        resultado.append(
                "Ejecuciones exitosas: "
        ).append(ejecucionesExitosas).append("\n");

        resultado.append(
                "Ejecuciones fallidas: "
        ).append(ejecucionesFallidas).append("\n");

        return resultado.toString();
    }
}
