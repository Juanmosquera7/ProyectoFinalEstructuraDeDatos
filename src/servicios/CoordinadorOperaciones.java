
package servicios;

import estructuras.ListaEnlazada;

import modelo.Beneficio;
import modelo.Cliente;
import modelo.Rango;
import modelo.TipoTransaccion;
import modelo.Transaccion;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Coordinador de operaciones del Monedero Virtual.
 *
 * Integra:
 *
 * - ClienteService.
 * - MonederoService.
 * - TransaccionService.
 * - PuntosService.
 * - NotificacionService.
 *
 * FUNCIONALIDADES:
 *
 * 1. Depositos con puntos y notificaciones.
 * 2. Retiros con puntos y notificaciones.
 * 3. Transferencias con puntos y avisos.
 * 4. Transferencias con beneficios.
 * 5. Gastos.
 * 6. Canjes de beneficios.
 * 7. Reversiones financieras y de puntos.
 * 8. Registro de incidencias de integracion.
 *
 * El movimiento financiero es ejecutado
 * exclusivamente por TransaccionService.
 *
 * IMPORTANTE:
 *
 * Este coordinador NO implementa atomicidad
 * distribuida ni persistencia.
 *
 * Si el movimiento monetario se completa
 * pero falla una operacion secundaria,
 * se devuelve el resultado financiero junto
 * con la incidencia correspondiente.
 *
 * Nunca se repite automaticamente una
 * transferencia o deposito por un fallo
 * posterior de puntos o notificaciones.
 */
public class CoordinadorOperaciones {

    // ==========================================
    // RESULTADO DE OPERACION
    // ==========================================

    /**
     * Representa una operacion financiera
     * y el resultado de sus integraciones.
     *
     * Distingue el exito financiero de
     * los posibles errores secundarios.
     */
    public static class ResultadoOperacion {

        private final Transaccion transaccion;

        private final ListaEnlazada<String> incidencias;

        private int puntosOtorgados;

        private int puntosCompensados;

        private int notificacionesEnviadas;

        private boolean puntosProcesados;

        private boolean beneficioAplicado;


        private ResultadoOperacion(
                Transaccion transaccion) {

            this.transaccion = Objects.requireNonNull(
                    transaccion,
                    "La transaccion no puede ser null"
            );

            this.incidencias = new ListaEnlazada<>();

            this.puntosOtorgados = 0;
            this.puntosCompensados = 0;
            this.notificacionesEnviadas = 0;

            this.puntosProcesados = false;
            this.beneficioAplicado = false;
        }

        public Transaccion getTransaccion() {
            return transaccion;
        }

        public String getIdTransaccion() {
            return transaccion.getId();
        }

        public boolean esExitosaFinancieramente() {
            return transaccion.estaCompletada();
        }

        public int getPuntosOtorgados() {
            return puntosOtorgados;
        }

        public int getPuntosCompensados() {
            return puntosCompensados;
        }

        public int getNotificacionesEnviadas() {
            return notificacionesEnviadas;
        }

        public boolean isPuntosProcesados() {
            return puntosProcesados;
        }

        public boolean isBeneficioAplicado() {
            return beneficioAplicado;
        }

        public boolean tieneIncidencias() {
            return !incidencias.estaVacia();
        }

        public int getCantidadIncidencias() {
            return incidencias.tamanio();
        }

        public ListaEnlazada<String> getIncidencias() {

            ListaEnlazada<String> copia =
                    new ListaEnlazada<>();

            incidencias.recorrer(copia::agregarFinal);

            return copia;
        }

        private void registrarIncidencia(String mensaje) {

            incidencias.agregarFinal(mensaje);
        }

        private void registrarNotificacion() {

            notificacionesEnviadas++;
        }

        public String generarResumen() {

            StringBuilder resultado =
                    new StringBuilder();

            resultado.append(
                    "RESULTADO DE OPERACION\n"
            );

            resultado.append(
                    "==============================\n"
            );

            resultado.append(
                    "ID: "
            ).append(getIdTransaccion()).append("\n");

            resultado.append(
                    "Tipo: "
            ).append(transaccion.getTipo()).append("\n");

            resultado.append(
                    "Estado financiero: "
            ).append(transaccion.getEstado()).append("\n");

            resultado.append(
                    "Monto: "
            ).append(transaccion.getMonto()).append("\n");

            resultado.append(
                    "Comision: "
            ).append(transaccion.getComision()).append("\n");

            resultado.append(
                    "Puntos procesados: "
            ).append(puntosProcesados).append("\n");

            resultado.append(
                    "Puntos otorgados: "
            ).append(puntosOtorgados).append("\n");

            resultado.append(
                    "Puntos compensados: "
            ).append(puntosCompensados).append("\n");

            resultado.append(
                    "Notificaciones enviadas: "
            ).append(notificacionesEnviadas).append("\n");

            resultado.append(
                    "Beneficio aplicado: "
            ).append(beneficioAplicado).append("\n");

            resultado.append(
                    "Incidencias: "
            ).append(incidencias.tamanio()).append("\n");

            incidencias.recorrer(incidencia -> {

                resultado.append(
                        " - "
                ).append(incidencia).append("\n");
            });

            return resultado.toString();
        }

        @Override
        public String toString() {

            return generarResumen();
        }
    }

    // ==========================================
    // INTERFAZ PARA NOTIFICACIONES
    // ==========================================

    @FunctionalInterface
    private interface AccionNotificacion {

        void ejecutar();
    }

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;

    private final MonederoService monederoService;

    private final TransaccionService transaccionService;

    private final PuntosService puntosService;

    private final NotificacionService notificacionService;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public CoordinadorOperaciones(
            ClienteService clienteService,
            MonederoService monederoService,
            TransaccionService transaccionService,
            PuntosService puntosService,
            NotificacionService notificacionService) {

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

        this.puntosService = Objects.requireNonNull(
                puntosService,
                "PuntosService no puede ser null"
        );

        this.notificacionService = Objects.requireNonNull(
                notificacionService,
                "NotificacionService no puede ser null"
        );
    }

    // ==========================================
    // DEPOSITOS
    // ==========================================

    /**
     * Ejecuta un deposito y coordina
     * los puntos y las notificaciones.
     *
     * Si el deposito financiero falla,
     * se propaga la excepcion.
     *
     * Si falla un proceso secundario,
     * se conserva el deposito y se registra
     * la incidencia.
     */
    public ResultadoOperacion depositar(
            String idCliente,
            String idMonedero,
            BigDecimal monto) {

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        Rango rangoAnterior =
                cliente.getRango();

        Transaccion transaccion =
                transaccionService.depositar(
                        idCliente,
                        idMonedero,
                        monto
                );

        ResultadoOperacion resultado =
                new ResultadoOperacion(transaccion);

        procesarPuntos(
                resultado,
                rangoAnterior
        );

        enviarNotificacionSegura(
                resultado,
                "Notificacion de deposito",
                () -> notificacionService.notificarDeposito(
                        idCliente,
                        monto,
                        transaccion.getId()
                )
        );

        return resultado;
    }

    // ==========================================
    // RETIROS
    // ==========================================

    /**
     * Ejecuta un retiro.
     *
     * El motor financiero actual no
     * aplica comisiones a los retiros.
     *
     * Por eso el beneficio temporal de
     * retiros sin comision no se consume
     * automaticamente aqui.
     */
    public ResultadoOperacion retirar(
            String idCliente,
            String idMonedero,
            BigDecimal monto) {

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        Rango rangoAnterior =
                cliente.getRango();

        Transaccion transaccion =
                transaccionService.retirar(
                        idCliente,
                        idMonedero,
                        monto
                );

        ResultadoOperacion resultado =
                new ResultadoOperacion(transaccion);

        procesarPuntos(
                resultado,
                rangoAnterior
        );

        enviarNotificacionSegura(
                resultado,
                "Notificacion de retiro",
                () -> notificacionService.notificarRetiro(
                        idCliente,
                        monto,
                        transaccion.getId()
                )
        );

        return resultado;
    }

    // ==========================================
    // TRANSFERENCIAS SIN COMISION
    // ==========================================

    /**
     * Ejecuta una transferencia
     * sin comision explicita.
     */
    public ResultadoOperacion transferir(
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

    // ==========================================
    // TRANSFERENCIAS CON COMISION
    // ==========================================

    /**
     * Ejecuta una transferencia con
     * una comision especifica.
     *
     * Los puntos pertenecen al emisor.
     *
     * Se generan notificaciones para
     * el origen y el destino.
     */
    public ResultadoOperacion transferir(
            String idClienteOrigen,
            String idMonederoOrigen,
            String idClienteDestino,
            String idMonederoDestino,
            BigDecimal monto,
            BigDecimal comision) {

        Cliente emisor =
                clienteService.obtenerClienteActivo(
                        idClienteOrigen
                );

        Rango rangoAnterior =
                emisor.getRango();

        Transaccion transaccion =
                transaccionService.transferir(
                        idClienteOrigen,
                        idMonederoOrigen,
                        idClienteDestino,
                        idMonederoDestino,
                        monto,
                        comision
                );

        ResultadoOperacion resultado =
                new ResultadoOperacion(transaccion);

        procesarPuntos(
                resultado,
                rangoAnterior
        );

        notificarTransferencia(
                resultado
        );

        return resultado;
    }

    // ==========================================
    // TRANSFERENCIAS CON BENEFICIO
    // ==========================================

    /**
     * Ejecuta una transferencia aplicando
     * un beneficio activo de descuento.
     *
     * El beneficio debe:
     * - Existir.
     * - Pertenecer al cliente emisor.
     * - Ser de descuento de transferencia.
     * - Encontrarse activo.
     *
     * Primero se calcula la comision.
     * Despues se realiza el movimiento
     * financiero y se consume el beneficio.
     *
     * Si falla el registro del uso tras
     * mover dinero, se informa la incidencia
     * sin repetir la transferencia.
     */
    public ResultadoOperacion transferirConBeneficio(
            String idClienteOrigen,
            String idMonederoOrigen,
            String idClienteDestino,
            String idMonederoDestino,
            BigDecimal monto,
            BigDecimal comisionOriginal,
            String idBeneficio) {

        validarComision(comisionOriginal);

        clienteService.obtenerClienteActivo(
                idClienteOrigen
        );

        Beneficio beneficio =
                puntosService.obtenerBeneficio(
                        idBeneficio
                );

        if (!beneficio.getIdCliente().equals(
                idClienteOrigen)) {

            throw new IllegalArgumentException(
                    "El beneficio no pertenece "
                            + "al cliente emisor"
            );
        }

        if (beneficio.getTipo()
                != Beneficio.Tipo.DESCUENTO_TRANSFERENCIA) {

            throw new IllegalArgumentException(
                    "El beneficio no es un descuento "
                            + "de transferencia"
            );
        }

        if (!beneficio.estaDisponibleParaUso()) {

            throw new IllegalStateException(
                    "El beneficio no esta disponible"
            );
        }

        BigDecimal comisionFinal =
                beneficio.calcularComisionConDescuento(
                        comisionOriginal
                );

        // La transferencia solo se ejecuta
        // despues de validar el beneficio.
        ResultadoOperacion resultado =
                transferir(
                        idClienteOrigen,
                        idMonederoOrigen,
                        idClienteDestino,
                        idMonederoDestino,
                        monto,
                        comisionFinal
                );

        try {

            puntosService.registrarUsoBeneficio(
                    idClienteOrigen,
                    idBeneficio
            );

            resultado.beneficioAplicado = true;

        } catch (RuntimeException error) {

            resultado.registrarIncidencia(
                    "La transferencia fue completada, "
                            + "pero no pudo registrarse "
                            + "el uso del beneficio: "
                            + mensajeError(error)
            );
        }

        return resultado;
    }

    // ==========================================
    // TRANSFERENCIA CON DESCUENTO DISPONIBLE
    // ==========================================

    /**
     * Busca un descuento activo
     * del cliente emisor.
     *
     * Si existe, lo utiliza.
     *
     * Si no existe, ejecuta la
     * transferencia con la comision
     * original.
     */
    public ResultadoOperacion transferirConDescuentoDisponible(
            String idClienteOrigen,
            String idMonederoOrigen,
            String idClienteDestino,
            String idMonederoDestino,
            BigDecimal monto,
            BigDecimal comisionOriginal) {

        validarComision(comisionOriginal);

        Beneficio beneficio =
                puntosService.buscarBeneficioActivo(
                        idClienteOrigen,
                        Beneficio.Tipo.DESCUENTO_TRANSFERENCIA
                );

        if (beneficio == null) {

            return transferir(
                    idClienteOrigen,
                    idMonederoOrigen,
                    idClienteDestino,
                    idMonederoDestino,
                    monto,
                    comisionOriginal
            );
        }

        return transferirConBeneficio(
                idClienteOrigen,
                idMonederoOrigen,
                idClienteDestino,
                idMonederoDestino,
                monto,
                comisionOriginal,
                beneficio.getId()
        );
    }

    // ==========================================
    // GASTOS
    // ==========================================

    /**
     * Registra un gasto financiero.
     *
     * Los gastos no otorgan puntos
     * segun las reglas actuales.
     */
    public ResultadoOperacion registrarGasto(
            String idCliente,
            String idMonedero,
            BigDecimal monto,
            String descripcion) {

        Transaccion transaccion =
                transaccionService.registrarGasto(
                        idCliente,
                        idMonedero,
                        monto,
                        descripcion
                );

        ResultadoOperacion resultado =
                new ResultadoOperacion(transaccion);

        // Los gastos no generan puntos.
        // No se necesita procesar puntos.

        enviarNotificacionSegura(
                resultado,
                "Notificacion de gasto",
                () -> notificacionService.enviarNotificacion(
                        idCliente,
                        modelo.Notificacion.Tipo.SISTEMA,
                        "Gasto registrado",
                        "Se registro un gasto de $"
                                + monto.toPlainString()
                                + ": " + descripcion
                )
        );

        return resultado;
    }

    // ==========================================
    // CANJE DE BENEFICIOS
    // ==========================================

    /**
     * Canjea puntos por un beneficio.
     *
     * Los puntos se descuentan mediante
     * PuntosService.
     *
     * El efecto financiero de bonos de
     * saldo todavia debe implementarse
     * en el motor financiero.
     */
    public Beneficio canjearBeneficio(
            String idCliente,
            Beneficio.Tipo tipo) {

        Beneficio beneficio =
                puntosService.canjearBeneficio(
                        idCliente,
                        tipo
                );

        try {

            notificacionService.notificarCanje(
                    idCliente,
                    beneficio.getDescripcion()
            );

        } catch (RuntimeException error) {

            // El canje ya fue realizado.
            // No debe repetirse para intentar
            // enviar una notificacion.
            System.err.println(
                    "[ADVERTENCIA] Canje completado, "
                            + "pero fallo la notificacion: "
                            + mensajeError(error)
            );
        }

        return beneficio;
    }

    // ==========================================
    // REVERSION DE ULTIMA OPERACION
    // ==========================================

    /**
     * Revierte la ultima operacion financiera
     * disponible mediante la pila LIFO.
     *
     * Antes de mover dinero, comprueba
     * que se puedan compensar los puntos.
     *
     * Si los puntos ya fueron gastados,
     * se rechaza la operacion.
     *
     * La compensacion de puntos se ejecuta
     * despues del reverso monetario.
     *
     * IMPORTANTE:
     * Todavia no existe atomicidad real
     * entre ambos servicios.
     */
    public ResultadoOperacion revertirUltimaTransaccion() {

        Transaccion original =
                transaccionService.consultarUltimaReversible();

        if (original == null) {

            throw new IllegalStateException(
                    "No existen transacciones "
                            + "disponibles para revertir"
            );
        }

        if (!puntosService.puedeRevertirPuntos(
                original)) {

            throw new IllegalStateException(
                    "No se puede revertir la operacion: "
                            + "los puntos obtenidos "
                            + "ya fueron gastados"
            );
        }

        // Primero revertir el movimiento
        // financiero mediante el servicio.
        Transaccion reversion =
                transaccionService.revertirUltimaTransaccion();

        ResultadoOperacion resultado =
                new ResultadoOperacion(reversion);

        // La transaccion original queda
        // marcada como REVERTIDA.
        if (puntosService.transaccionProcesada(
                original.getId())) {

            try {

                resultado.puntosCompensados =
                        puntosService
                                .revertirPuntosDeTransaccion(
                                        original
                                );

                resultado.puntosProcesados = true;

            } catch (RuntimeException error) {

                resultado.registrarIncidencia(
                        "La reversion monetaria se completo, "
                                + "pero fallo la compensacion "
                                + "de puntos: "
                                + mensajeError(error)
                );
            }
        }

        // Avisar a los clientes involucrados.
        notificarReversion(
                resultado,
                original
        );

        return resultado;
    }

    // ==========================================
    // ASIGNACION AUTOMATICA DE PUNTOS
    // ==========================================

    /**
     * Procesa los puntos de una transaccion
     * financiera completada.
     *
     * Tambien detecta un cambio de rango
     * y envia las notificaciones asociadas.
     *
     * Un fallo en este paso se registra
     * como incidencia de integracion.
     */
    private void procesarPuntos(
            ResultadoOperacion resultado,
            Rango rangoAnterior) {

        Transaccion transaccion =
                resultado.getTransaccion();

        String idCliente =
                obtenerClienteQueGanaPuntos(
                        transaccion
                );

        if (idCliente == null) {
            return;
        }

        try {

            int puntosGanados =
                    puntosService
                            .registrarPuntosPorTransaccion(
                                    transaccion
                            );

            resultado.puntosOtorgados =
                    puntosGanados;

            resultado.puntosProcesados = true;

        } catch (RuntimeException error) {

            resultado.registrarIncidencia(
                    "El movimiento financiero se completo, "
                            + "pero fallo la asignacion "
                            + "de puntos: "
                            + mensajeError(error)
            );

            return;
        }

        if (resultado.puntosOtorgados > 0) {

            enviarNotificacionSegura(
                    resultado,
                    "Notificacion de puntos",
                    () -> notificacionService
                            .notificarPuntosGanados(
                                    idCliente,
                                    resultado.puntosOtorgados,
                                    transaccion.getId()
                            )
            );
        }

        Rango rangoActual =
                clienteService.obtenerCliente(
                        idCliente
                ).getRango();

        if (rangoActual != rangoAnterior) {

            enviarNotificacionSegura(
                    resultado,
                    "Notificacion de cambio de rango",
                    () -> notificacionService
                            .notificarCambioRango(
                                    idCliente,
                                    rangoActual
                            )
            );
        }
    }

    // ==========================================
    // IDENTIFICAR BENEFICIARIO DE PUNTOS
    // ==========================================

    private String obtenerClienteQueGanaPuntos(
            Transaccion transaccion) {

        return switch (transaccion.getTipo()) {

            case DEPOSITO ->
                    transaccion.getIdClienteDestino();

            case RETIRO, TRANSFERENCIA ->
                    transaccion.getIdClienteOrigen();

            default -> null;
        };
    }

    // ==========================================
    // NOTIFICACIONES DE TRANSFERENCIAS
    // ==========================================

    /**
     * Notifica por separado a emisor
     * y destinatario.
     *
     * Si uno de los avisos falla,
     * el otro aun puede procesarse.
     */
    private void notificarTransferencia(
            ResultadoOperacion resultado) {

        Transaccion transaccion =
                resultado.getTransaccion();

        enviarNotificacionSegura(
                resultado,
                "Aviso al emisor",
                () -> notificacionService
                        .notificarTransferenciaEnviada(
                                transaccion.getIdClienteOrigen(),
                                transaccion.getMonto(),
                                transaccion.getId()
                        )
        );

        enviarNotificacionSegura(
                resultado,
                "Aviso al destinatario",
                () -> notificacionService
                        .notificarTransferenciaRecibida(
                                transaccion.getIdClienteDestino(),
                                transaccion.getMonto(),
                                transaccion.getId()
                        )
        );
    }

    // ==========================================
    // NOTIFICACIONES DE REVERSION
    // ==========================================

    private void notificarReversion(
            ResultadoOperacion resultado,
            Transaccion original) {

        String origen =
                original.getIdClienteOrigen();

        String destino =
                original.getIdClienteDestino();

        if (origen != null) {

            enviarNotificacionSegura(
                    resultado,
                    "Aviso de reversion al origen",
                    () -> notificacionService
                            .notificarReversion(
                                    origen,
                                    original.getId()
                            )
            );
        }

        if (destino != null
                && !destino.equals(origen)) {

            enviarNotificacionSegura(
                    resultado,
                    "Aviso de reversion al destino",
                    () -> notificacionService
                            .notificarReversion(
                                    destino,
                                    original.getId()
                            )
            );
        }
    }

    // ==========================================
    // EJECUCION SEGURA DE NOTIFICACIONES
    // ==========================================

    /**
     * Ejecuta una notificacion sin convertir
     * un fallo secundario en una segunda
     * operacion financiera.
     */
    private void enviarNotificacionSegura(
            ResultadoOperacion resultado,
            String descripcion,
            AccionNotificacion accion) {

        try {

            accion.ejecutar();

            resultado.registrarNotificacion();

        } catch (RuntimeException error) {

            resultado.registrarIncidencia(
                    descripcion
                            + " no pudo registrarse: "
                            + mensajeError(error)
            );
        }
    }

    // ==========================================
    // CONSULTAS AUXILIARES
    // ==========================================

    public int consultarPuntos(
            String idCliente) {

        return puntosService.consultarPuntos(
                idCliente
        );
    }

    public Rango consultarRango(
            String idCliente) {

        return clienteService.obtenerCliente(
                idCliente
        ).getRango();
    }

    public BigDecimal consultarSaldo(
            String idCliente,
            String idMonedero) {

        return monederoService.consultarSaldo(
                idCliente,
                idMonedero
        );
    }

    public Transaccion buscarTransaccion(
            String idTransaccion) {

        return transaccionService.buscarTransaccion(
                idTransaccion
        );
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarComision(
            BigDecimal comision) {

        if (comision == null
                || comision.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "La comision no puede ser negativa"
            );
        }

        try {

            comision.setScale(
                    2,
                    java.math.RoundingMode.UNNECESSARY
            );

        } catch (ArithmeticException error) {

            throw new IllegalArgumentException(
                    "La comision no admite "
                            + "mas de dos decimales"
            );
        }
    }

    private String mensajeError(
            RuntimeException error) {

        if (error.getMessage() == null
                || error.getMessage().isBlank()) {

            return error.getClass().getSimpleName();
        }

        return error.getMessage();
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return "CoordinadorOperaciones{" +
                "clientes="
                + clienteService.cantidadClientes() +
                ", monederos="
                + monederoService.cantidadMonederos() +
                ", transacciones="
                + transaccionService.cantidadTransacciones() +
                ", puntos="
                + puntosService.totalPuntosSistema() +
                ", notificaciones="
                + notificacionService
                .cantidadNotificaciones() +
                '}';
    }
}
