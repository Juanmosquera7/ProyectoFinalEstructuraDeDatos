
package servicios;

import estructuras.GrafoDirigido;
import estructuras.ListaEnlazada;

import modelo.Beneficio;
import modelo.Cliente;
import modelo.Monedero;
import modelo.Notificacion;
import modelo.Rango;
import modelo.TipoTransaccion;
import modelo.Transaccion;
import modelo.TransaccionProgramada;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Servicio encargado de generar reportes
 * del sistema Monedero Virtual.
 *
 * REPORTES DISPONIBLES:
 *
 * - Resumen general del sistema.
 * - Clientes registrados.
 * - Informacion individual de clientes.
 * - Monederos y saldos.
 * - Historial financiero.
 * - Historial de un cliente.
 * - Puntos acumulados.
 * - Rangos y clasificaciones.
 * - Beneficios canjeados.
 * - Transferencias entre clientes.
 * - Transacciones programadas.
 * - Notificaciones.
 * - Comparacion de arboles ABB y AVL.
 * - Reporte completo.
 *
 * Utiliza los servicios existentes para
 * obtener la informacion actualizada.
 *
 * No modifica dinero, puntos ni operaciones.
 *
 * Los reportes se devuelven como String
 * y pueden mostrarse en Java Swing,
 * guardarse en archivos o imprimirse.
 *
 * No utiliza colecciones de Java.
 */
public class ReporteService {

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

    private static final String SEPARADOR =
            "========================================";

    private static final String LINEA =
            "----------------------------------------";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    // ==========================================
    // CONSTRUCTOR PRINCIPAL
    // ==========================================

    /**
     * Recibe todos los servicios existentes.
     *
     * Es importante que todos compartan
     * la misma instancia de ClienteService.
     */
    public ReporteService(
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

    /**
     * Permite generar reportes basicos
     * sin los servicios avanzados.
     */
    public ReporteService(
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
    // CONSTRUCTOR CON PUNTOS Y RANGOS
    // ==========================================

    public ReporteService(
            ClienteService clienteService,
            MonederoService monederoService,
            TransaccionService transaccionService,
            PuntosService puntosService,
            RangosService rangosService) {

        this(
                clienteService,
                monederoService,
                transaccionService,
                puntosService,
                rangosService,
                null,
                null
        );
    }

    // ==========================================
    // REPORTE GENERAL
    // ==========================================

    /**
     * Genera un resumen de los principales
     * indicadores del sistema.
     */
    public String generarReporteGeneral() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE GENERAL DEL SISTEMA"
        );

        linea(
                reporte,
                "Fecha: " + fechaActual()
        );

        separador(reporte);

        linea(
                reporte,
                "CLIENTES Y MONEDEROS"
        );

        linea(
                reporte,
                "Clientes registrados: "
                        + clienteService.cantidadClientes()
        );

        linea(
                reporte,
                "Clientes activos: "
                        + clienteService.cantidadClientesActivos()
        );

        linea(
                reporte,
                "Monederos registrados: "
                        + monederoService.cantidadMonederos()
        );

        linea(
                reporte,
                "Monederos activos: "
                        + monederoService.cantidadMonederosActivos()
        );

        linea(
                reporte,
                "Monederos bloqueados: "
                        + monederoService.cantidadMonederosBloqueados()
        );

        separador(reporte);

        linea(
                reporte,
                "INFORMACION FINANCIERA"
        );

        linea(
                reporte,
                "Saldo total del sistema: "
                        + dinero(
                        monederoService.saldoTotalSistema()
                )
        );

        linea(
                reporte,
                "Transacciones registradas: "
                        + transaccionService.cantidadTransacciones()
        );

        linea(
                reporte,
                "Depositos: "
                        + transaccionService.contarPorTipo(
                        TipoTransaccion.DEPOSITO
                )
        );

        linea(
                reporte,
                "Retiros: "
                        + transaccionService.contarPorTipo(
                        TipoTransaccion.RETIRO
                )
        );

        linea(
                reporte,
                "Transferencias: "
                        + transaccionService.contarPorTipo(
                        TipoTransaccion.TRANSFERENCIA
                )
        );

        linea(
                reporte,
                "Gastos: "
                        + transaccionService.contarPorTipo(
                        TipoTransaccion.GASTO
                )
        );

        linea(
                reporte,
                "Reversiones: "
                        + transaccionService.contarPorTipo(
                        TipoTransaccion.REVERSO
                )
        );

        linea(
                reporte,
                "Comisiones netas: "
                        + dinero(
                        transaccionService
                                .consultarTotalComisiones()
                )
        );

        separador(reporte);

        linea(
                reporte,
                "SISTEMA DE PUNTOS"
        );

        if (puntosService != null) {

            linea(
                    reporte,
                    "Puntos actuales del sistema: "
                            + puntosService.totalPuntosSistema()
            );

            linea(
                    reporte,
                    "Transacciones procesadas para puntos: "
                            + puntosService
                            .cantidadTransaccionesProcesadas()
            );

            linea(
                    reporte,
                    "Beneficios canjeados: "
                            + puntosService
                            .cantidadBeneficiosCanjeados()
            );

        } else {

            linea(
                    reporte,
                    "Modulo de puntos no configurado."
            );
        }

        separador(reporte);

        linea(
                reporte,
                "OPERACIONES PROGRAMADAS"
        );

        if (programacionService != null) {

            linea(
                    reporte,
                    "Programaciones totales: "
                            + programacionService
                            .cantidadProgramaciones()
            );

            linea(
                    reporte,
                    "Programaciones activas: "
                            + programacionService
                            .cantidadProgramacionesActivas()
            );

            linea(
                    reporte,
                    "Ejecuciones exitosas: "
                            + programacionService
                            .cantidadEjecucionesExitosas()
            );

            linea(
                    reporte,
                    "Ejecuciones fallidas: "
                            + programacionService
                            .cantidadEjecucionesFallidas()
            );

        } else {

            linea(
                    reporte,
                    "Modulo de programaciones no configurado."
            );
        }

        separador(reporte);

        linea(
                reporte,
                "NOTIFICACIONES"
        );

        if (notificacionService != null) {

            linea(
                    reporte,
                    "Total de notificaciones: "
                            + notificacionService
                            .cantidadNotificaciones()
            );

            linea(
                    reporte,
                    "Notificaciones no leidas: "
                            + notificacionService
                            .totalNoLeidasSistema()
            );

        } else {

            linea(
                    reporte,
                    "Modulo de notificaciones no configurado."
            );
        }

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE CLIENTES
    // ==========================================

    /**
     * Lista los clientes registrados
     * en orden de insercion.
     */
    public String generarReporteClientes() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE CLIENTES"
        );

        if (clienteService.cantidadClientes() == 0) {

            linea(reporte, "No hay clientes registrados.");

            return reporte.toString();
        }

        final int[] contador = {1};

        clienteService.recorrerClientes(cliente -> {

            linea(
                    reporte,
                    contador[0]++ + ". "
                            + cliente.getNombre()
            );

            linea(
                    reporte,
                    "   ID: " + cliente.getId()
            );

            linea(
                    reporte,
                    "   Correo: " + cliente.getCorreo()
            );

            linea(
                    reporte,
                    "   Estado: "
                            + (cliente.isActivo()
                            ? "ACTIVO"
                            : "INACTIVO")
            );

            linea(
                    reporte,
                    "   Monederos: "
                            + cliente.getCantidadMonederos()
            );

            linea(
                    reporte,
                    "   Puntos: " + cliente.getPuntos()
            );

            linea(
                    reporte,
                    "   Rango: " + cliente.getRango()
            );

            linea(
                    reporte,
                    "   Saldo total: "
                            + dinero(
                            cliente.consultarSaldoTotal()
                    )
            );

            separador(reporte);
        });

        linea(
                reporte,
                "TOTAL CLIENTES: "
                        + clienteService.cantidadClientes()
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE INDIVIDUAL DE CLIENTE
    // ==========================================

    /**
     * Consulta la informacion completa
     * de un cliente especifico.
     */
    public String generarReporteCliente(
            String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE INDIVIDUAL DE CLIENTE"
        );

        linea(
                reporte,
                "ID: " + cliente.getId()
        );

        linea(
                reporte,
                "Nombre: " + cliente.getNombre()
        );

        linea(
                reporte,
                "Correo: " + cliente.getCorreo()
        );

        linea(
                reporte,
                "Estado: "
                        + (cliente.isActivo()
                        ? "ACTIVO"
                        : "INACTIVO")
        );

        linea(
                reporte,
                "Puntos: " + cliente.getPuntos()
        );

        linea(
                reporte,
                "Rango: " + cliente.getRango()
        );

        linea(
                reporte,
                "Monederos: "
                        + cliente.getCantidadMonederos()
        );

        linea(
                reporte,
                "Saldo total: "
                        + dinero(
                        cliente.consultarSaldoTotal()
                )
        );

        separador(reporte);

        linea(
                reporte,
                "MONEDEROS DEL CLIENTE"
        );

        cliente.getMonederos().recorrer(monedero -> {

            linea(
                    reporte,
                    monedero.getId()
                            + " | "
                            + monedero.getNombre()
                            + " | "
                            + monedero.getTipo()
                            + " | "
                            + dinero(monedero.getSaldo())
            );
        });

        separador(reporte);

        linea(
                reporte,
                "TRANSACCIONES DEL CLIENTE"
        );

        ListaEnlazada<Transaccion> historial =
                transaccionService.obtenerHistorialCliente(
                        idCliente
                );

        if (historial.estaVacia()) {

            linea(reporte, "Sin transacciones.");

        } else {

            historial.recorrer(transaccion -> {

                agregarTransaccion(
                        reporte,
                        transaccion
                );
            });
        }

        if (puntosService != null) {

            separador(reporte);

            linea(
                    reporte,
                    "BENEFICIOS DEL CLIENTE"
            );

            ListaEnlazada<Beneficio> beneficios =
                    puntosService.listarBeneficios(
                            idCliente
                    );

            if (beneficios.estaVacia()) {

                linea(
                        reporte,
                        "Sin beneficios canjeados."
                );

            } else {

                beneficios.recorrer(beneficio -> {

                    linea(
                            reporte,
                            beneficio.getDescripcion()
                                    + " | "
                                    + beneficio.getEstado()
                                    + " | "
                                    + beneficio.getCostoPuntos()
                                    + " puntos"
                    );
                });
            }
        }

        if (notificacionService != null) {

            separador(reporte);

            linea(
                    reporte,
                    "NOTIFICACIONES DEL CLIENTE: "
                            + notificacionService
                            .cantidadNotificacionesCliente(
                                    idCliente
                            )
            );

            linea(
                    reporte,
                    "No leidas: "
                            + notificacionService
                            .cantidadNoLeidas(
                                    idCliente
                            )
            );
        }

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE MONEDEROS
    // ==========================================

    /**
     * Muestra los monederos del sistema.
     */
    public String generarReporteMonederos() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE MONEDEROS"
        );

        final int[] contador = {0};

        monederoService.recorrerMonederos(monedero -> {

            contador[0]++;

            linea(
                    reporte,
                    contador[0] + ". "
                            + monedero.getNombre()
            );

            linea(
                    reporte,
                    "   ID: " + monedero.getId()
            );

            linea(
                    reporte,
                    "   Cliente: "
                            + monedero.getIdCliente()
            );

            linea(
                    reporte,
                    "   Tipo: " + monedero.getTipo()
            );

            linea(
                    reporte,
                    "   Saldo: "
                            + dinero(monedero.getSaldo())
            );

            linea(
                    reporte,
                    "   Estado: "
                            + (monedero.isActivo()
                            ? "ACTIVO"
                            : "BLOQUEADO")
            );

            separador(reporte);
        });

        if (contador[0] == 0) {

            linea(
                    reporte,
                    "No hay monederos registrados."
            );
        }

        linea(
                reporte,
                "TOTAL MONEDEROS: " + contador[0]
        );

        linea(
                reporte,
                "SALDO TOTAL: "
                        + dinero(
                        monederoService.saldoTotalSistema()
                )
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE TRANSACCIONES
    // ==========================================

    /**
     * Muestra todas las transacciones
     * en orden de registro.
     */
    public String generarReporteTransacciones() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "HISTORIAL FINANCIERO GENERAL"
        );

        ListaEnlazada<Transaccion> historial =
                transaccionService.obtenerHistorial();

        if (historial.estaVacia()) {

            linea(
                    reporte,
                    "No existen transacciones."
            );

            return reporte.toString();
        }

        final int[] contador = {1};

        historial.recorrer(transaccion -> {

            linea(
                    reporte,
                    "OPERACION #" + contador[0]++
            );

            agregarTransaccion(
                    reporte,
                    transaccion
            );

            separador(reporte);
        });

        linea(
                reporte,
                "TOTAL TRANSACCIONES: "
                        + historial.tamanio()
        );

        return reporte.toString();
    }

    // ==========================================
    // HISTORIAL INDIVIDUAL
    // ==========================================

    /**
     * Muestra los movimientos donde
     * un cliente es origen o destino.
     */
    public String generarHistorialCliente(
            String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "HISTORIAL FINANCIERO DEL CLIENTE"
        );

        linea(
                reporte,
                "Cliente: " + cliente.getNombre()
        );

        linea(
                reporte,
                "ID: " + cliente.getId()
        );

        separador(reporte);

        ListaEnlazada<Transaccion> historial =
                transaccionService.obtenerHistorialCliente(
                        idCliente
                );

        if (historial.estaVacia()) {

            linea(
                    reporte,
                    "El cliente no tiene transacciones."
            );

        } else {

            historial.recorrer(transaccion -> {

                agregarTransaccion(
                        reporte,
                        transaccion
                );

                separador(reporte);
            });
        }

        linea(
                reporte,
                "TOTAL MOVIMIENTOS: "
                        + historial.tamanio()
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE MOVIMIENTOS POR TIPO
    // ==========================================

    /**
     * Resume las operaciones financieras
     * por tipo, considerando solamente
     * transacciones completadas para
     * los totales monetarios.
     */
    public String generarReporteResumenFinanciero() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "RESUMEN DE OPERACIONES FINANCIERAS"
        );

        for (TipoTransaccion tipo :
                TipoTransaccion.values()) {

            linea(
                    reporte,
                    tipo.getDescripcion()
            );

            linea(
                    reporte,
                    "  Registros: "
                            + transaccionService
                            .contarPorTipo(tipo)
            );

            linea(
                    reporte,
                    "  Total completado: "
                            + dinero(
                            transaccionService
                                    .totalPorTipo(tipo)
                    )
            );

            separador(reporte);
        }

        linea(
                reporte,
                "COMISIONES NETAS: "
                        + dinero(
                        transaccionService
                                .consultarTotalComisiones()
                )
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE PUNTOS
    // ==========================================

    /**
     * Muestra puntos y rangos actuales
     * de todos los clientes.
     */
    public String generarReportePuntos() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DEL SISTEMA DE PUNTOS"
        );

        if (puntosService == null) {

            linea(
                    reporte,
                    "Modulo de puntos no configurado."
            );

            return reporte.toString();
        }

        clienteService.recorrerClientes(cliente -> {

            linea(
                    reporte,
                    "Cliente: " + cliente.getNombre()
            );

            linea(
                    reporte,
                    "ID: " + cliente.getId()
            );

            linea(
                    reporte,
                    "Puntos actuales: "
                            + cliente.getPuntos()
            );

            linea(
                    reporte,
                    "Rango: " + cliente.getRango()
            );

            linea(
                    reporte,
                    "Para siguiente rango: "
                            + cliente.puntosParaSiguienteRango()
            );

            separador(reporte);
        });

        linea(
                reporte,
                "PUNTOS TOTALES DEL SISTEMA: "
                        + puntosService.totalPuntosSistema()
        );

        linea(
                reporte,
                "TRANSACCIONES PROCESADAS: "
                        + puntosService
                        .cantidadTransaccionesProcesadas()
        );

        linea(
                reporte,
                "MOVIMIENTOS DE PUNTOS: "
                        + puntosService
                        .cantidadMovimientosPuntos()
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE BENEFICIOS
    // ==========================================

    /**
     * Muestra los beneficios que han
     * canjeado los clientes.
     */
    public String generarReporteBeneficios() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE BENEFICIOS CANJEADOS"
        );

        if (puntosService == null) {

            linea(
                    reporte,
                    "Modulo de puntos no configurado."
            );

            return reporte.toString();
        }

        clienteService.recorrerClientes(cliente -> {

            ListaEnlazada<Beneficio> beneficios =
                    puntosService.listarBeneficios(
                            cliente.getId()
                    );

            if (beneficios.estaVacia()) {
                return;
            }

            linea(
                    reporte,
                    "CLIENTE: " + cliente.getNombre()
                            + " (" + cliente.getId() + ")"
            );

            beneficios.recorrer(beneficio -> {

                linea(
                        reporte,
                        "  Beneficio: "
                                + beneficio.getDescripcion()
                );

                linea(
                        reporte,
                        "  ID: " + beneficio.getId()
                );

                linea(
                        reporte,
                        "  Costo: "
                                + beneficio.getCostoPuntos()
                                + " puntos"
                );

                linea(
                        reporte,
                        "  Estado: "
                                + beneficio.getEstado()
                );

                linea(
                        reporte,
                        "  Usos: "
                                + beneficio.getCantidadUsos()
                );

                linea(
                        reporte,
                        "  Vencimiento: "
                                + beneficio
                                .getFechaVencimientoFormateada()
                );

                separador(reporte);
            });
        });

        linea(
                reporte,
                "TOTAL BENEFICIOS CANJEADOS: "
                        + puntosService
                        .cantidadBeneficiosCanjeados()
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE RANGOS
    // ==========================================

    /**
     * Muestra cuantos clientes pertenecen
     * a cada rango del sistema.
     */
    public String generarReporteRangos() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE RANGOS"
        );

        for (Rango rango : Rango.values()) {

            int cantidad;

            if (rangosService != null) {

                cantidad =
                        rangosService.cantidadClientesPorRango(
                                rango
                        );

            } else {

                final int[] contador = {0};

                clienteService.recorrerClientes(cliente -> {

                    if (cliente.getRango() == rango) {
                        contador[0]++;
                    }
                });

                cantidad = contador[0];
            }

            linea(
                    reporte,
                    rango.getNombre()
            );

            linea(
                    reporte,
                    "  Clientes: " + cantidad
            );

            linea(
                    reporte,
                    "  Puntos minimos: "
                            + rango.getPuntosMinimos()
            );

            linea(
                    reporte,
                    "  Puntos maximos: "
                            + (rango == Rango.PLATINO
                            ? "Sin limite practico"
                            : rango.getPuntosMaximos())
            );

            linea(
                    reporte,
                    "  Descuento de comision: "
                            + rango.getDescuentoComision()
                            + "%"
            );

            separador(reporte);
        }

        return reporte.toString();
    }

    // ==========================================
    // RANKING DE CLIENTES
    // ==========================================

    /**
     * Muestra los clientes de mayor
     * a menor puntuacion.
     */
    public String generarReporteRanking() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "RANKING DE CLIENTES"
        );

        if (rangosService == null) {

            linea(
                    reporte,
                    "Modulo de rangos no configurado."
            );

            return reporte.toString();
        }

        ListaEnlazada<Cliente> ranking =
                rangosService.obtenerRankingDescendente();

        if (ranking.estaVacia()) {

            linea(
                    reporte,
                    "No existen clientes registrados."
            );

            return reporte.toString();
        }

        final int[] posicion = {1};

        ranking.recorrer(cliente -> {

            int puesto =
                    rangosService.obtenerPosicion(
                            cliente.getId()
                    );

            linea(
                    reporte,
                    puesto + ". "
                            + cliente.getNombre()
                            + " | "
                            + cliente.getPuntos()
                            + " puntos | "
                            + cliente.getRango()
            );

            posicion[0]++;
        });

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE TRANSFERENCIAS
    // ==========================================

    /**
     * Genera un informe de las relaciones
     * financieras entre clientes.
     *
     * Utiliza nuestro GrafoDirigido.
     *
     * Solo considera transferencias
     * completadas y no revertidas.
     */
    public String generarReporteTransferencias() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE TRANSFERENCIAS"
        );

        GrafoDirigido grafo =
                transaccionService
                        .obtenerGrafoTransferencias();

        linea(
                reporte,
                "Clientes representados: "
                        + grafo.cantidadVertices()
        );

        linea(
                reporte,
                "Relaciones dirigidas: "
                        + grafo.cantidadAristas()
        );

        separador(reporte);

        linea(
                reporte,
                "RELACIONES ENTRE CLIENTES"
        );

        if (grafo.cantidadAristas() == 0) {

            linea(
                    reporte,
                    "No hay transferencias vigentes."
            );
        }

        grafo.recorrerAristas(arista -> {

            linea(
                    reporte,
                    arista.getOrigen()
                            + " -> "
                            + arista.getDestino()
            );

            linea(
                    reporte,
                    "  Monto total: "
                            + dinero(arista.getPeso())
            );

            linea(
                    reporte,
                    "  Operaciones: "
                            + arista.getCantidadOperaciones()
            );

            separador(reporte);
        });

        linea(
                reporte,
                "RESUMEN POR CLIENTE"
        );

        clienteService.recorrerClientes(cliente -> {

            String id = cliente.getId();

            linea(
                    reporte,
                    cliente.getNombre()
                            + " (" + id + ")"
            );

            linea(
                    reporte,
                    "  Total enviado: "
                            + dinero(
                            grafo.totalEnviado(id)
                    )
            );

            linea(
                    reporte,
                    "  Total recibido: "
                            + dinero(
                            grafo.totalRecibido(id)
                    )
            );

            linea(
                    reporte,
                    "  Destinos diferentes: "
                            + grafo.gradoSalida(id)
            );

            linea(
                    reporte,
                    "  Origenes diferentes: "
                            + grafo.gradoEntrada(id)
            );

            String destino =
                    grafo.destinoMasFrecuente(id);

            linea(
                    reporte,
                    "  Destino mas frecuente: "
                            + (destino == null
                            ? "Ninguno"
                            : destino)
            );

            separador(reporte);
        });

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE PROGRAMACIONES
    // ==========================================

    /**
     * Muestra las operaciones programadas.
     */
    public String generarReporteProgramaciones() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE TRANSACCIONES PROGRAMADAS"
        );

        if (programacionService == null) {

            linea(
                    reporte,
                    "Modulo de programaciones no configurado."
            );

            return reporte.toString();
        }

        ListaEnlazada<TransaccionProgramada> lista =
                programacionService
                        .listarProgramaciones();

        if (lista.estaVacia()) {

            linea(
                    reporte,
                    "No hay programaciones registradas."
            );

        } else {

            lista.recorrer(programacion -> {

                linea(
                        reporte,
                        "ID: " + programacion.getId()
                );

                linea(
                        reporte,
                        "Tipo: " + programacion.getTipo()
                );

                linea(
                        reporte,
                        "Monto: "
                                + dinero(
                                programacion.getMonto()
                        )
                );

                linea(
                        reporte,
                        "Proxima fecha: "
                                + programacion
                                .getFechaFormateada()
                );

                linea(
                        reporte,
                        "Frecuencia: "
                                + programacion.getFrecuencia()
                );

                linea(
                        reporte,
                        "Estado: "
                                + programacion.getEstado()
                );

                linea(
                        reporte,
                        "Ejecuciones realizadas: "
                                + programacion
                                .getEjecucionesRealizadas()
                );

                separador(reporte);
            });
        }

        linea(
                reporte,
                "TOTAL PROGRAMACIONES: "
                        + programacionService
                        .cantidadProgramaciones()
        );

        linea(
                reporte,
                "ACTIVAS: "
                        + programacionService
                        .cantidadProgramacionesActivas()
        );

        linea(
                reporte,
                "PAUSADAS: "
                        + programacionService
                        .cantidadProgramacionesPausadas()
        );

        linea(
                reporte,
                "COMPLETADAS: "
                        + programacionService
                        .cantidadProgramacionesCompletadas()
        );

        linea(
                reporte,
                "CANCELADAS: "
                        + programacionService
                        .cantidadProgramacionesCanceladas()
        );

        linea(
                reporte,
                "EJECUCIONES EXITOSAS: "
                        + programacionService
                        .cantidadEjecucionesExitosas()
        );

        linea(
                reporte,
                "EJECUCIONES FALLIDAS: "
                        + programacionService
                        .cantidadEjecucionesFallidas()
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE NOTIFICACIONES
    // ==========================================

    /**
     * Genera un informe de las alertas
     * correspondientes a cada cliente.
     */
    public String generarReporteNotificaciones() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE NOTIFICACIONES"
        );

        if (notificacionService == null) {

            linea(
                    reporte,
                    "Modulo de notificaciones no configurado."
            );

            return reporte.toString();
        }

        clienteService.recorrerClientes(cliente -> {

            linea(
                    reporte,
                    "CLIENTE: " + cliente.getNombre()
            );

            linea(
                    reporte,
                    "ID: " + cliente.getId()
            );

            ListaEnlazada<Notificacion> lista =
                    notificacionService
                            .listarNotificaciones(
                                    cliente.getId()
                            );

            if (lista.estaVacia()) {

                linea(
                        reporte,
                        "Sin notificaciones."
                );

            } else {

                lista.recorrer(notificacion -> {

                    linea(
                            reporte,
                            notificacion.getResumen()
                    );
                });
            }

            separador(reporte);
        });

        linea(
                reporte,
                "TOTAL NOTIFICACIONES: "
                        + notificacionService
                        .cantidadNotificaciones()
        );

        linea(
                reporte,
                "NO LEIDAS: "
                        + notificacionService
                        .totalNoLeidasSistema()
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE DE ESTRUCTURAS
    // ==========================================

    /**
     * Compara los arboles ABB y AVL
     * utilizados para indexar los clientes
     * por su puntuacion.
     */
    public String generarReporteEstructuras() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "REPORTE DE ESTRUCTURAS DE DATOS"
        );

        if (rangosService == null) {

            linea(
                    reporte,
                    "Modulo de rangos no configurado."
            );

            return reporte.toString();
        }

        reporte.append(
                rangosService.generarComparacionArboles()
        );

        separador(reporte);

        linea(
                reporte,
                "Integridad de ABB y AVL: "
                        + rangosService
                        .verificarIntegridad()
        );

        linea(
                reporte,
                "Altura ABB: "
                        + rangosService.obtenerAlturaABB()
        );

        linea(
                reporte,
                "Altura AVL: "
                        + rangosService.obtenerAlturaAVL()
        );

        return reporte.toString();
    }

    // ==========================================
    // REPORTE COMPLETO
    // ==========================================

    /**
     * Combina todos los informes
     * en un unico documento de texto.
     *
     * Puede mostrarse en PanelReportes
     * o exportarse posteriormente.
     */
    public String generarReporteCompleto() {

        StringBuilder reporte = new StringBuilder();

        encabezado(
                reporte,
                "MONEDERO VIRTUAL - REPORTE COMPLETO"
        );

        linea(
                reporte,
                "Generado: " + fechaActual()
        );

        agregarSeccion(
                reporte,
                generarReporteGeneral()
        );

        agregarSeccion(
                reporte,
                generarReporteClientes()
        );

        agregarSeccion(
                reporte,
                generarReporteMonederos()
        );

        agregarSeccion(
                reporte,
                generarReporteResumenFinanciero()
        );

        agregarSeccion(
                reporte,
                generarReporteTransacciones()
        );

        agregarSeccion(
                reporte,
                generarReportePuntos()
        );

        agregarSeccion(
                reporte,
                generarReporteRangos()
        );

        agregarSeccion(
                reporte,
                generarReporteRanking()
        );

        agregarSeccion(
                reporte,
                generarReporteBeneficios()
        );

        agregarSeccion(
                reporte,
                generarReporteTransferencias()
        );

        agregarSeccion(
                reporte,
                generarReporteProgramaciones()
        );

        agregarSeccion(
                reporte,
                generarReporteNotificaciones()
        );

        agregarSeccion(
                reporte,
                generarReporteEstructuras()
        );

        return reporte.toString();
    }

    // ==========================================
    // METODOS AUXILIARES
    // ==========================================

    /**
     * Agrega un encabezado al reporte.
     */
    private void encabezado(
            StringBuilder reporte,
            String titulo) {

        reporte.append(SEPARADOR)
                .append("\n");

        reporte.append(titulo)
                .append("\n");

        reporte.append(SEPARADOR)
                .append("\n");
    }

    /**
     * Agrega una linea de texto.
     */
    private void linea(
            StringBuilder reporte,
            String texto) {

        reporte.append(texto)
                .append("\n");
    }

    /**
     * Agrega un separador visual.
     */
    private void separador(
            StringBuilder reporte) {

        reporte.append(LINEA)
                .append("\n");
    }

    /**
     * Agrega una nueva seccion al
     * reporte completo.
     */
    private void agregarSeccion(
            StringBuilder reporte,
            String contenido) {

        reporte.append("\n\n");

        reporte.append(contenido);

        reporte.append("\n");
    }

    /**
     * Formatea un monto monetario.
     *
     * No modifica la cantidad original.
     */
    private String dinero(
            BigDecimal monto) {

        if (monto == null) {
            return "$0.00";
        }

        return "$" + monto.toPlainString();
    }

    /**
     * Devuelve la fecha actual formateada.
     */
    private String fechaActual() {

        return LocalDateTime.now().format(
                FORMATO_FECHA
        );
    }

    /**
     * Agrega los detalles de una
     * transaccion al reporte.
     */
    private void agregarTransaccion(
            StringBuilder reporte,
            Transaccion transaccion) {

        linea(
                reporte,
                "ID: " + transaccion.getId()
        );

        linea(
                reporte,
                "Tipo: " + transaccion.getTipo()
        );

        linea(
                reporte,
                "Monto: "
                        + dinero(transaccion.getMonto())
        );

        linea(
                reporte,
                "Comision: "
                        + dinero(transaccion.getComision())
        );

        linea(
                reporte,
                "Estado: " + transaccion.getEstado()
        );

        linea(
                reporte,
                "Fecha: "
                        + transaccion.getFechaFormateada()
        );

        linea(
                reporte,
                "Cliente origen: "
                        + textoOpcional(
                        transaccion.getIdClienteOrigen()
                )
        );

        linea(
                reporte,
                "Cliente destino: "
                        + textoOpcional(
                        transaccion.getIdClienteDestino()
                )
        );

        linea(
                reporte,
                "Monedero origen: "
                        + textoOpcional(
                        transaccion.getIdMonederoOrigen()
                )
        );

        linea(
                reporte,
                "Monedero destino: "
                        + textoOpcional(
                        transaccion.getIdMonederoDestino()
                )
        );

        linea(
                reporte,
                "Puntos generados: "
                        + transaccion.getPuntosGenerados()
        );

        if (transaccion.esReversion()) {

            linea(
                    reporte,
                    "Transaccion original: "
                            + textoOpcional(
                            transaccion
                                    .getIdTransaccionOriginal()
                    )
            );
        }

        if (transaccion.getDescripcion() != null
                && !transaccion.getDescripcion().isBlank()) {

            linea(
                    reporte,
                    "Descripcion: "
                            + transaccion.getDescripcion()
            );
        }
    }

    /**
     * Reemplaza identificadores ausentes
     * por un texto descriptivo.
     */
    private String textoOpcional(
            String valor) {

        return valor == null || valor.isBlank()
                ? "No aplica"
                : valor;
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return generarReporteGeneral();
    }
}
