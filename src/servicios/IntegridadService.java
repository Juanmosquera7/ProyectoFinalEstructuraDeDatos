
package servicios;

import estructuras.ListaEnlazada;
import estructuras.TablaHash;

import modelo.Cliente;
import modelo.Monedero;
import modelo.Rango;
import modelo.TipoTransaccion;
import modelo.Transaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Servicio de verificacion de integridad.
 *
 * PROYECTO:
 * Monedero Virtual con Sistema de Puntos.
 *
 * RESPONSABILIDADES:
 *
 * - Verificar clientes.
 * - Verificar monederos.
 * - Verificar transacciones.
 * - Verificar puntos y beneficios.
 * - Verificar rangos y arboles.
 * - Verificar programaciones.
 * - Verificar notificaciones.
 * - Comprobar saldos contra historial.
 * - Comprobar relaciones entre servicios.
 * - Generar un reporte de auditoria.
 *
 * ESTRUCTURAS PROPIAS:
 *
 * TablaHash:
 * Para calcular y consultar saldos esperados.
 *
 * ListaEnlazada:
 * Para registrar errores y advertencias.
 *
 * No utiliza colecciones de Java.
 *
 * IMPORTANTE:
 * La auditoria informa los problemas.
 * No modifica saldos ni puntos.
 */
public class IntegridadService {

    // ==========================================
    // RESULTADO DE AUDITORIA
    // ==========================================

    /**
     * Contiene el resultado de una auditoria.
     *
     * Separa los errores de integridad
     * de las advertencias de integracion.
     */
    public static class ResultadoAuditoria {

        private final LocalDateTime fecha;

        private final ListaEnlazada<String> errores;

        private final ListaEnlazada<String> advertencias;

        private final ListaEnlazada<String> verificaciones;

        private int comprobacionesRealizadas;

        private int comprobacionesAprobadas;

        public ResultadoAuditoria() {

            fecha = LocalDateTime.now();

            errores = new ListaEnlazada<>();

            advertencias = new ListaEnlazada<>();

            verificaciones = new ListaEnlazada<>();

            comprobacionesRealizadas = 0;

            comprobacionesAprobadas = 0;
        }

        private void comprobar(
                String descripcion,
                boolean correcta) {

            comprobacionesRealizadas++;

            if (correcta) {

                comprobacionesAprobadas++;

                verificaciones.agregarFinal(
                        "[OK] " + descripcion
                );

            } else {

                errores.agregarFinal(
                        "[ERROR] " + descripcion
                );

                verificaciones.agregarFinal(
                        "[FALLO] " + descripcion
                );
            }
        }

        private void registrarError(String mensaje) {

            comprobacionesRealizadas++;

            errores.agregarFinal(
                    "[ERROR] " + mensaje
            );

            verificaciones.agregarFinal(
                    "[FALLO] " + mensaje
            );
        }

        private void registrarAdvertencia(String mensaje) {

            advertencias.agregarFinal(
                    "[ADVERTENCIA] " + mensaje
            );
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public boolean esCorrecta() {
            return errores.estaVacia();
        }

        public boolean tieneAdvertencias() {
            return !advertencias.estaVacia();
        }

        public int getComprobacionesRealizadas() {
            return comprobacionesRealizadas;
        }

        public int getComprobacionesAprobadas() {
            return comprobacionesAprobadas;
        }

        public int getCantidadErrores() {
            return errores.tamanio();
        }

        public int getCantidadAdvertencias() {
            return advertencias.tamanio();
        }

        public ListaEnlazada<String> getErrores() {

            ListaEnlazada<String> copia =
                    new ListaEnlazada<>();

            errores.recorrer(copia::agregarFinal);

            return copia;
        }

        public ListaEnlazada<String> getAdvertencias() {

            ListaEnlazada<String> copia =
                    new ListaEnlazada<>();

            advertencias.recorrer(
                    copia::agregarFinal
            );

            return copia;
        }

        public ListaEnlazada<String> getVerificaciones() {

            ListaEnlazada<String> copia =
                    new ListaEnlazada<>();

            verificaciones.recorrer(
                    copia::agregarFinal
            );

            return copia;
        }

        public String generarInforme() {

            StringBuilder resultado =
                    new StringBuilder();

            DateTimeFormatter formato =
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy HH:mm:ss"
                    );

            resultado.append(
                    "========================================\n"
            );

            resultado.append(
                    " AUDITORIA DE INTEGRIDAD\n"
            );

            resultado.append(
                    "========================================\n"
            );

            resultado.append(
                            "Fecha: "
                    ).append(fecha.format(formato))
                    .append("\n\n");

            verificaciones.recorrer(linea -> {

                resultado.append(linea)
                        .append("\n");
            });

            resultado.append(
                    "\n----------------------------------------\n"
            );

            resultado.append(
                    "RESUMEN DE AUDITORIA\n"
            );

            resultado.append(
                            "Comprobaciones: "
                    ).append(comprobacionesRealizadas)
                    .append("\n");

            resultado.append(
                            "Aprobadas: "
                    ).append(comprobacionesAprobadas)
                    .append("\n");

            resultado.append(
                            "Errores: "
                    ).append(getCantidadErrores())
                    .append("\n");

            resultado.append(
                            "Advertencias: "
                    ).append(getCantidadAdvertencias())
                    .append("\n");

            if (!errores.estaVacia()) {

                resultado.append(
                        "\nERRORES DETECTADOS\n"
                );

                errores.recorrer(error -> {

                    resultado.append(error)
                            .append("\n");
                });
            }

            if (!advertencias.estaVacia()) {

                resultado.append(
                        "\nADVERTENCIAS\n"
                );

                advertencias.recorrer(advertencia -> {

                    resultado.append(advertencia)
                            .append("\n");
                });
            }

            resultado.append(
                    "\nRESULTADO: "
            );

            resultado.append(
                    esCorrecta()
                            ? "SIN ERRORES DETECTADOS"
                            : "SE DETECTARON ERRORES"
            );

            resultado.append("\n");

            return resultado.toString();
        }

        @Override
        public String toString() {

            return generarInforme();
        }
    }

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;

    private final MonederoService monederoService;

    private final TransaccionService transaccionService;

    private final PuntosService puntosService;

    private final RangosService rangosService;

    private final ProgramacionService programacionService;

    private final NotificacionService notificacionService;

    // ==========================================
    // CONSTRUCTOR COMPLETO
    // ==========================================

    public IntegridadService(
            ClienteService clienteService,
            MonederoService monederoService,
            TransaccionService transaccionService,
            PuntosService puntosService,
            RangosService rangosService,
            ProgramacionService programacionService,
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

        this.puntosService = puntosService;

        this.rangosService = rangosService;

        this.programacionService = programacionService;

        this.notificacionService = notificacionService;
    }

    // ==========================================
    // CONSTRUCTOR FINANCIERO
    // ==========================================

    public IntegridadService(
            ClienteService clienteService,
            MonederoService monederoService,
            TransaccionService transaccionService) {

        this(
                clienteService,
                monederoService,
                transaccionService,
                null,
                null,
                null,
                null
        );
    }

    // ==========================================
    // AUDITORIA GENERAL
    // ==========================================

    /**
     * Ejecuta todas las verificaciones.
     *
     * Un error en un modulo se registra
     * sin impedir comprobar los demas.
     */
    public ResultadoAuditoria ejecutarAuditoriaCompleta() {

        ResultadoAuditoria resultado =
                new ResultadoAuditoria();

        ejecutarComprobacion(
                resultado,
                "Integridad de clientes",
                clienteService::verificarIntegridad
        );

        ejecutarComprobacion(
                resultado,
                "Integridad de monederos",
                monederoService::verificarIntegridad
        );

        ejecutarComprobacion(
                resultado,
                "Integridad de transacciones",
                transaccionService::verificarIntegridad
        );

        if (puntosService != null) {

            ejecutarComprobacion(
                    resultado,
                    "Integridad del sistema de puntos",
                    puntosService::verificarIntegridad
            );

        } else {

            resultado.registrarAdvertencia(
                    "PuntosService no configurado"
            );
        }

        if (rangosService != null) {

            ejecutarComprobacion(
                    resultado,
                    "Integridad de ABB y AVL",
                    rangosService::verificarIntegridad
            );

        } else {

            resultado.registrarAdvertencia(
                    "RangosService no configurado"
            );
        }

        if (programacionService != null) {

            ejecutarComprobacion(
                    resultado,
                    "Integridad de programaciones",
                    programacionService::verificarIntegridad
            );

        } else {

            resultado.registrarAdvertencia(
                    "ProgramacionService no configurado"
            );
        }

        if (notificacionService != null) {

            ejecutarComprobacion(
                    resultado,
                    "Integridad de notificaciones",
                    notificacionService::verificarIntegridad
            );

        } else {

            resultado.registrarAdvertencia(
                    "NotificacionService no configurado"
            );
        }

        // Comprobaciones entre modulos.

        ejecutarComprobacion(
                resultado,
                "Monederos pertenecen a clientes existentes",
                this::verificarPertenenciaMonederos
        );

        ejecutarComprobacion(
                resultado,
                "Saldos totales coinciden",
                this::verificarSaldosTotales
        );

        ejecutarComprobacion(
                resultado,
                "Historial financiero y saldos coinciden",
                this::verificarConciliacionFinanciera
        );

        ejecutarComprobacion(
                resultado,
                "Transacciones referencian cuentas validas",
                this::verificarReferenciasTransacciones
        );

        ejecutarComprobacion(
                resultado,
                "Grafo de transferencias consistente",
                this::verificarGrafoTransferencias
        );

        if (puntosService != null) {

            ejecutarComprobacion(
                    resultado,
                    "Puntos totales coinciden",
                    this::verificarPuntosTotales
            );
        }

        if (rangosService != null) {

            ejecutarComprobacion(
                    resultado,
                    "Clasificacion de rangos consistente",
                    this::verificarClasificacionRangos
            );
        }

        registrarAdvertenciasIntegracion(
                resultado
        );

        return resultado;
    }

    // ==========================================
    // AUDITORIA BASICA
    // ==========================================

    /**
     * Verifica solamente los tres
     * servicios financieros principales.
     */
    public ResultadoAuditoria ejecutarAuditoriaBasica() {

        ResultadoAuditoria resultado =
                new ResultadoAuditoria();

        ejecutarComprobacion(
                resultado,
                "Integridad de clientes",
                clienteService::verificarIntegridad
        );

        ejecutarComprobacion(
                resultado,
                "Integridad de monederos",
                monederoService::verificarIntegridad
        );

        ejecutarComprobacion(
                resultado,
                "Integridad de transacciones",
                transaccionService::verificarIntegridad
        );

        ejecutarComprobacion(
                resultado,
                "Saldos totales coinciden",
                this::verificarSaldosTotales
        );

        ejecutarComprobacion(
                resultado,
                "Conciliacion financiera",
                this::verificarConciliacionFinanciera
        );

        return resultado;
    }

    // ==========================================
    // EJECUCION SEGURA DE COMPROBACIONES
    // ==========================================

    @FunctionalInterface
    private interface Comprobacion {

        boolean verificar();
    }

    private void ejecutarComprobacion(
            ResultadoAuditoria resultado,
            String descripcion,
            Comprobacion comprobacion) {

        try {

            boolean correcta =
                    comprobacion.verificar();

            resultado.comprobar(
                    descripcion,
                    correcta
            );

        } catch (RuntimeException error) {

            resultado.registrarError(
                    descripcion
                            + ": "
                            + error.getClass().getSimpleName()
                            + " - "
                            + error.getMessage()
            );
        }
    }

    // ==========================================
    // VERIFICAR PERTENENCIA DE MONEDEROS
    // ==========================================

    /**
     * Comprueba que cada monedero
     * pertenezca a un cliente existente.
     *
     * Tambien verifica que el monedero
     * se encuentre registrado en la
     * lista del cliente correspondiente.
     */
    public boolean verificarPertenenciaMonederos() {

        final boolean[] valido = {true};

        monederoService.recorrerMonederos(monedero -> {

            if (monedero == null) {

                valido[0] = false;
                return;
            }

            Cliente propietario =
                    clienteService.buscarCliente(
                            monedero.getIdCliente()
                    );

            if (propietario == null) {

                valido[0] = false;
                return;
            }

            Monedero registrado =
                    propietario.buscarMonedero(
                            monedero.getId()
                    );

            if (registrado != monedero) {

                valido[0] = false;
            }
        });

        return valido[0];
    }

    // ==========================================
    // VERIFICACION DE SALDOS TOTALES
    // ==========================================

    /**
     * Compara el saldo total informado
     * por ClienteService y MonederoService.
     */
    public boolean verificarSaldosTotales() {

        BigDecimal saldoClientes =
                clienteService.saldoTotalSistema();

        BigDecimal saldoMonederos =
                monederoService.saldoTotalSistema();

        return saldoClientes.compareTo(
                saldoMonederos
        ) == 0;
    }

    // ==========================================
    // CONCILIACION FINANCIERA
    // ==========================================

    /**
     * Reconstruye los saldos esperados
     * mediante el historial financiero.
     *
     * Operaciones consideradas:
     *
     * DEPOSITO:
     * Suma al monedero destino.
     *
     * RETIRO:
     * Resta del monedero origen.
     *
     * TRANSFERENCIA:
     * Resta monto y comision del origen.
     * Suma monto al destino.
     *
     * GASTO:
     * Resta del monedero origen.
     *
     * REVERSO:
     * No se aplica por separado porque
     * el movimiento original revertido
     * ya se excluye del calculo.
     *
     * Solo se aplican operaciones
     * cuyo estado es COMPLETADA.
     *
     * Las operaciones REVERTIDAS
     * no contribuyen al saldo neto.
     *
     * Esta conciliacion asume saldos
     * iniciales de cero.
     *
     * Si en el futuro se incorporan
     * nuevas operaciones monetarias,
     * este metodo debera ampliarse.
     */
    public boolean verificarConciliacionFinanciera() {

        TablaHash<String, BigDecimal> saldosEsperados =
                new TablaHash<>();

        // Inicializar los saldos esperados.

        monederoService.recorrerMonederos(monedero -> {

            saldosEsperados.insertar(
                    monedero.getId(),
                    BigDecimal.ZERO
            );
        });

        final boolean[] valido = {true};

        ListaEnlazada<Transaccion> historial =
                transaccionService.obtenerHistorial();

        historial.recorrer(transaccion -> {

            if (!valido[0]) {
                return;
            }

            if (transaccion == null) {

                valido[0] = false;
                return;
            }

            if (transaccion.getEstado()
                    == Transaccion.Estado.REVERTIDA) {

                return;
            }

            if (transaccion.getEstado()
                    != Transaccion.Estado.COMPLETADA) {

                return;
            }

            TipoTransaccion tipo =
                    transaccion.getTipo();

            BigDecimal monto =
                    transaccion.getMonto();

            BigDecimal comision =
                    transaccion.getComision();

            switch (tipo) {

                case DEPOSITO -> {

                    valido[0] = actualizarSaldoEsperado(
                            saldosEsperados,
                            transaccion.getIdMonederoDestino(),
                            monto
                    );
                }

                case RETIRO -> {

                    valido[0] = actualizarSaldoEsperado(
                            saldosEsperados,
                            transaccion.getIdMonederoOrigen(),
                            monto.negate()
                    );
                }

                case TRANSFERENCIA -> {

                    boolean origenCorrecto =
                            actualizarSaldoEsperado(
                                    saldosEsperados,
                                    transaccion.getIdMonederoOrigen(),
                                    monto.add(comision).negate()
                            );

                    boolean destinoCorrecto =
                            actualizarSaldoEsperado(
                                    saldosEsperados,
                                    transaccion.getIdMonederoDestino(),
                                    monto
                            );

                    valido[0] =
                            origenCorrecto && destinoCorrecto;
                }

                case GASTO -> {

                    valido[0] = actualizarSaldoEsperado(
                            saldosEsperados,
                            transaccion.getIdMonederoOrigen(),
                            monto.negate()
                    );
                }

                case REVERSO -> {

                    // El original revertido
                    // ya esta excluido.
                }

                case BONIFICACION,
                     CANJE_PUNTOS -> {

                    // Estas operaciones todavia
                    // no forman parte del motor
                    // financiero integrado.
                    //
                    // No es seguro conciliarlas
                    // sin conocer su efecto real.
                    valido[0] = false;
                }
            }
        });

        if (!valido[0]) {
            return false;
        }

        monederoService.recorrerMonederos(monedero -> {

            BigDecimal esperado =
                    saldosEsperados.obtener(
                            monedero.getId()
                    );

            if (esperado == null
                    || esperado.compareTo(
                    monedero.getSaldo()
            ) != 0) {

                valido[0] = false;
            }
        });

        return valido[0];
    }

    /**
     * Actualiza el saldo esperado
     * de un monedero registrado.
     */
    private boolean actualizarSaldoEsperado(
            TablaHash<String, BigDecimal> saldos,
            String idMonedero,
            BigDecimal diferencia) {

        if (idMonedero == null
                || diferencia == null) {

            return false;
        }

        BigDecimal saldo =
                saldos.obtener(idMonedero);

        if (saldo == null) {
            return false;
        }

        saldos.insertar(
                idMonedero,
                saldo.add(diferencia)
        );

        return true;
    }

    // ==========================================
    // REFERENCIAS DE TRANSACCIONES
    // ==========================================

    /**
     * Verifica que los clientes y monederos
     * mencionados por las transacciones
     * sigan registrados en el sistema.
     *
     * Los identificadores nulos son validos
     * cuando no corresponden al tipo de
     * movimiento realizado.
     */
    public boolean verificarReferenciasTransacciones() {

        final boolean[] valido = {true};

        transaccionService.obtenerHistorial()
                .recorrer(transaccion -> {

                    if (transaccion == null) {

                        valido[0] = false;
                        return;
                    }

                    String clienteOrigen =
                            transaccion.getIdClienteOrigen();

                    String clienteDestino =
                            transaccion.getIdClienteDestino();

                    String monederoOrigen =
                            transaccion.getIdMonederoOrigen();

                    String monederoDestino =
                            transaccion.getIdMonederoDestino();

                    if (clienteOrigen != null) {

                        Cliente cliente =
                                clienteService.buscarCliente(
                                        clienteOrigen
                                );

                        if (cliente == null) {

                            valido[0] = false;

                        } else if (monederoOrigen != null
                                && cliente.buscarMonedero(
                                monederoOrigen
                        ) == null) {

                            valido[0] = false;
                        }

                    } else if (monederoOrigen != null) {

                        valido[0] = false;
                    }

                    if (clienteDestino != null) {

                        Cliente cliente =
                                clienteService.buscarCliente(
                                        clienteDestino
                                );

                        if (cliente == null) {

                            valido[0] = false;

                        } else if (monederoDestino != null
                                && cliente.buscarMonedero(
                                monederoDestino
                        ) == null) {

                            valido[0] = false;
                        }

                    } else if (monederoDestino != null) {

                        valido[0] = false;
                    }
                });

        return valido[0];
    }

    // ==========================================
    // VERIFICACION DEL GRAFO
    // ==========================================

    /**
     * Comprueba la integridad estructural
     * del grafo de transferencias.
     *
     * Tambien verifica que las aristas
     * correspondan a relaciones existentes
     * entre clientes registrados.
     */
    public boolean verificarGrafoTransferencias() {

        estructuras.GrafoDirigido grafo =
                transaccionService
                        .obtenerGrafoTransferencias();

        if (!grafo.verificarIntegridad()) {
            return false;
        }

        if (grafo.cantidadVertices()
                != clienteService.cantidadClientes()) {

            return false;
        }

        final boolean[] valido = {true};

        grafo.recorrerAristas(arista -> {

            if (clienteService.buscarCliente(
                    arista.getOrigen()) == null) {

                valido[0] = false;
            }

            if (clienteService.buscarCliente(
                    arista.getDestino()) == null) {

                valido[0] = false;
            }

            if (arista.getPeso().compareTo(
                    BigDecimal.ZERO) <= 0) {

                valido[0] = false;
            }

            if (arista.getCantidadOperaciones() <= 0) {

                valido[0] = false;
            }
        });

        return valido[0];
    }

    // ==========================================
    // VERIFICACION DE PUNTOS
    // ==========================================

    /**
     * Comprueba que los puntos totales
     * de PuntosService coincidan con
     * la suma de puntos de los clientes.
     */
    public boolean verificarPuntosTotales() {

        if (puntosService == null) {

            throw new IllegalStateException(
                    "PuntosService no esta configurado"
            );
        }

        final long[] total = {0L};

        clienteService.recorrerClientes(cliente -> {

            total[0] = Math.addExact(
                    total[0],
                    cliente.getPuntos()
            );
        });

        return total[0]
                == puntosService.totalPuntosSistema();
    }

    // ==========================================
    // VERIFICACION DE RANGOS
    // ==========================================

    /**
     * Comprueba que los rangos de
     * los clientes correspondan a
     * sus puntuaciones actuales.
     *
     * Tambien valida los indices ABB y AVL.
     */
    public boolean verificarClasificacionRangos() {

        if (rangosService == null) {

            throw new IllegalStateException(
                    "RangosService no esta configurado"
            );
        }

        if (!rangosService.verificarIntegridad()) {
            return false;
        }

        final boolean[] valido = {true};

        final int[] clientesContados = {0};

        for (Rango rango : Rango.values()) {

            clientesContados[0] +=
                    rangosService.cantidadClientesPorRango(
                            rango
                    );
        }

        if (clientesContados[0]
                != clienteService.cantidadClientes()) {

            valido[0] = false;
        }

        clienteService.recorrerClientes(cliente -> {

            if (cliente.getRango()
                    != Rango.calcularRango(
                    cliente.getPuntos()
            )) {

                valido[0] = false;
            }

            if (rangosService.consultarRango(
                    cliente.getId()
            ) != cliente.getRango()) {

                valido[0] = false;
            }
        });

        return valido[0];
    }

    // ==========================================
    // ADVERTENCIAS DE INTEGRACION
    // ==========================================

    /**
     * Informa las limitaciones que aun
     * existen en la integracion actual.
     *
     * No las considera errores de datos:
     * son funcionalidades pendientes.
     */
    private void registrarAdvertenciasIntegracion(
            ResultadoAuditoria resultado) {

        if (puntosService != null) {

            resultado.registrarAdvertencia(
                    "La asignacion automatica de puntos "
                            + "desde TransaccionService "
                            + "aun requiere integracion"
            );

            resultado.registrarAdvertencia(
                    "Las reversiones monetarias y de puntos "
                            + "todavia no son atomicas"
            );
        }

        if (notificacionService != null) {

            resultado.registrarAdvertencia(
                    "Las notificaciones no se generan "
                            + "automaticamente en todos "
                            + "los servicios financieros"
            );
        }

        if (programacionService != null) {

            resultado.registrarAdvertencia(
                    "Las programaciones requieren un "
                            + "temporizador o una llamada "
                            + "explicita para ejecutarse"
            );
        }

        resultado.registrarAdvertencia(
                "Los datos permanecen en memoria "
                        + "y no sobreviven al cierre "
                        + "de la aplicacion"
        );
    }

    // ==========================================
    // CONSULTAS RAPIDAS
    // ==========================================

    /**
     * Devuelve true si la auditoria
     * completa no encontro errores.
     *
     * Las advertencias no provocan
     * un resultado false.
     */
    public boolean sistemaIntegro() {

        return ejecutarAuditoriaCompleta()
                .esCorrecta();
    }

    /**
     * Genera el informe completo
     * de auditoria como String.
     */
    public String generarReporteIntegridad() {

        return ejecutarAuditoriaCompleta()
                .generarInforme();
    }

    /**
     * Genera un resumen breve.
     */
    public String generarResumenIntegridad() {

        ResultadoAuditoria auditoria =
                ejecutarAuditoriaCompleta();

        StringBuilder resumen =
                new StringBuilder();

        resumen.append(
                "INTEGRIDAD DEL SISTEMA\n"
        );

        resumen.append(
                "============================\n"
        );

        resumen.append(
                "Comprobaciones: "
        ).append(
                auditoria.getComprobacionesRealizadas()
        ).append("\n");

        resumen.append(
                "Aprobadas: "
        ).append(
                auditoria.getComprobacionesAprobadas()
        ).append("\n");

        resumen.append(
                "Errores: "
        ).append(
                auditoria.getCantidadErrores()
        ).append("\n");

        resumen.append(
                "Advertencias: "
        ).append(
                auditoria.getCantidadAdvertencias()
        ).append("\n");

        resumen.append(
                "Estado: "
        ).append(
                auditoria.esCorrecta()
                        ? "SIN ERRORES DETECTADOS"
                        : "CON ERRORES"
        );

        return resumen.toString();
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return generarResumenIntegridad();
    }
}
