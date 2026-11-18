
package pruebas;

import estructuras.GrafoDirigido;
import estructuras.ListaEnlazada;

import modelo.Beneficio;
import modelo.Cliente;
import modelo.Notificacion;
import modelo.Rango;
import modelo.TipoTransaccion;
import modelo.Transaccion;
import modelo.TransaccionProgramada;

import servicios.ClienteService;
import servicios.MonederoService;
import servicios.TransaccionService;
import servicios.PuntosService;
import servicios.RangosService;
import servicios.ProgramacionService;
import servicios.NotificacionService;
import servicios.ReporteService;
import servicios.IntegridadService;
import servicios.CoordinadorOperaciones;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * PRUEBAS DE INTEGRACION GENERAL
 *
 * PROYECTO:
 * Simulador de Monedero Virtual
 * con Sistema de Puntos.
 *
 * Evalua la comunicacion entre los
 * nueve servicios del proyecto.
 *
 * GRUPOS:
 *
 * 1. Inicializacion del sistema.
 * 2. Operaciones financieras.
 * 3. Puntos asociados a transacciones.
 * 4. Rangos y clasificaciones.
 * 5. Arboles ABB y AVL.
 * 6. Canje y gestion de beneficios.
 * 7. Notificaciones financieras.
 * 8. Listas circulares y lectura.
 * 9. Transacciones programadas.
 * 10. Grafos de transferencias.
 * 11. Reportes generales.
 * 12. Conciliacion financiera.
 * 13. Reversiones financieras y puntos.
 * 14. Auditoria completa.
 * 15. Validaciones entre servicios.
 * 16. Escenario integral del sistema.
 *
 * No utiliza JUnit.
 *
 * Cada grupo crea un contexto independiente.
 *
 * IMPORTANTE:
 * La asignacion de puntos y el envio
 * de notificaciones se invocan manualmente
 * mientras no exista el coordinador
 * automatico de operaciones.
 */
public class PruebaIntegracion {

    // ==========================================
    // CONTADORES
    // ==========================================

    private static int aprobadas = 0;
    private static int fallidas = 0;

    private static int gruposAprobados = 0;
    private static int gruposFallidos = 0;

    // ==========================================
    // CONTEXTO DEL SISTEMA
    // ==========================================

    /**
     * Inicializa todos los servicios
     * utilizando las mismas referencias.
     *
     * Registra tres clientes y crea
     * un monedero principal para cada uno.
     */
    private static class Contexto {

        final ClienteService clientes;
        final MonederoService monederos;
        final TransaccionService transacciones;

        final PuntosService puntos;
        final RangosService rangos;
        final ProgramacionService programaciones;
        final NotificacionService notificaciones;

        final ReporteService reportes;
        final IntegridadService integridad;

        final CoordinadorOperaciones coordinador;

        final Cliente juan;
        final Cliente ana;
        final Cliente luis;

        Contexto() {

            clientes = new ClienteService();

            monederos = new MonederoService(
                    clientes
            );

            transacciones = new TransaccionService(
                    clientes,
                    monederos
            );

            puntos = new PuntosService(
                    clientes
            );

            rangos = new RangosService(
                    clientes
            );

            programaciones = new ProgramacionService(
                    clientes,
                    monederos,
                    transacciones
            );

            notificaciones = new NotificacionService(
                    clientes
            );

            coordinador = new CoordinadorOperaciones(
                    clientes,
                    monederos,
                    transacciones,
                    puntos,
                    notificaciones
            );

            reportes = new ReporteService(
                    clientes,
                    monederos,
                    transacciones,
                    puntos,
                    rangos,
                    programaciones,
                    notificaciones
            );

            integridad = new IntegridadService(
                    clientes,
                    monederos,
                    transacciones,
                    puntos,
                    rangos,
                    programaciones,
                    notificaciones
            );

            juan = clientes.registrarCliente(
                    "C001",
                    "Juan",
                    "juan@ejemplo.com"
            );

            ana = clientes.registrarCliente(
                    "C002",
                    "Ana",
                    "ana@ejemplo.com"
            );

            luis = clientes.registrarCliente(
                    "C003",
                    "Luis",
                    "luis@ejemplo.com"
            );

            monederos.crearMonederoPrincipal(
                    "C001",
                    "M001"
            );

            monederos.crearMonederoPrincipal(
                    "C002",
                    "M002"
            );

            monederos.crearMonederoPrincipal(
                    "C003",
                    "M003"
            );
        }
    }

    // ==========================================
    // MAIN
    // ==========================================

    public static void main(String[] args) {

        System.out.println(
                "========================================"
        );

        System.out.println(
                " MONEDERO VIRTUAL"
        );

        System.out.println(
                " PRUEBAS DE INTEGRACION GENERAL"
        );

        System.out.println(
                "========================================"
        );

        ejecutarGrupo(
                "Inicializacion del sistema",
                PruebaIntegracion::probarInicializacion
        );

        ejecutarGrupo(
                "Operaciones financieras",
                PruebaIntegracion::probarFinanzas
        );

        ejecutarGrupo(
                "Puntos asociados a transacciones",
                PruebaIntegracion::probarPuntos
        );

        ejecutarGrupo(
                "Rangos y clasificaciones",
                PruebaIntegracion::probarRangos
        );

        ejecutarGrupo(
                "Arboles ABB y AVL",
                PruebaIntegracion::probarArboles
        );

        ejecutarGrupo(
                "Canje y gestion de beneficios",
                PruebaIntegracion::probarBeneficios
        );

        ejecutarGrupo(
                "Notificaciones financieras",
                PruebaIntegracion::probarNotificaciones
        );

        ejecutarGrupo(
                "Listas circulares y lectura",
                PruebaIntegracion::probarCircularidad
        );

        ejecutarGrupo(
                "Transacciones programadas",
                PruebaIntegracion::probarProgramaciones
        );

        ejecutarGrupo(
                "Grafos de transferencias",
                PruebaIntegracion::probarGrafos
        );

        ejecutarGrupo(
                "Reportes generales",
                PruebaIntegracion::probarReportes
        );

        ejecutarGrupo(
                "Conciliacion financiera",
                PruebaIntegracion::probarConciliacion
        );

        ejecutarGrupo(
                "Reversiones financieras y puntos",
                PruebaIntegracion::probarReversiones
        );

        ejecutarGrupo(
                "Auditoria completa",
                PruebaIntegracion::probarAuditoria
        );

        ejecutarGrupo(
                "Validaciones entre servicios",
                PruebaIntegracion::probarValidaciones
        );

        ejecutarGrupo(
                "Escenario integral del sistema",
                PruebaIntegracion::probarEscenarioIntegral
        );

        // ==========================================
// PRUEBAS DEL COORDINADOR DE OPERACIONES
// ==========================================

        ejecutarGrupo(
                "Coordinador: depositos automaticos",
                PruebaIntegracion::probarCoordinadorDepositos
        );

        ejecutarGrupo(
                "Coordinador: retiros automaticos",
                PruebaIntegracion::probarCoordinadorRetiros
        );

        ejecutarGrupo(
                "Coordinador: transferencias y rangos",
                PruebaIntegracion::probarCoordinadorTransferencias
        );

        ejecutarGrupo(
                "Coordinador: descuentos y beneficios",
                PruebaIntegracion::probarCoordinadorBeneficios
        );

        ejecutarGrupo(
                "Coordinador: reversiones completas",
                PruebaIntegracion::probarCoordinadorReversiones
        );

        ejecutarGrupo(
                "Coordinador: validaciones y seguridad",
                PruebaIntegracion::probarCoordinadorValidaciones
        );

        mostrarResumen();

        if (fallidas > 0 || gruposFallidos > 0) {

            throw new AssertionError(
                    "Existen errores en la integracion"
            );
        }
    }

    // ==========================================
    // HERRAMIENTAS DE PRUEBA
    // ==========================================

    private static void ejecutarGrupo(
            String nombre,
            Runnable prueba) {

        System.out.println();

        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                "GRUPO: " + nombre
        );

        System.out.println(
                "----------------------------------------"
        );

        int erroresAntes = fallidas;

        try {

            prueba.run();

            if (fallidas == erroresAntes) {

                gruposAprobados++;

                System.out.println(
                        "RESULTADO: APROBADO"
                );

            } else {

                gruposFallidos++;

                System.out.println(
                        "RESULTADO: FALLIDO"
                );
            }

        } catch (Throwable error) {

            fallidas++;
            gruposFallidos++;

            System.out.println(
                    "[ERROR] "
                            + error.getClass().getSimpleName()
                            + ": "
                            + error.getMessage()
            );

            error.printStackTrace(System.out);
        }
    }

    private static void verificar(
            String descripcion,
            boolean condicion) {

        if (condicion) {

            aprobadas++;

            System.out.println(
                    "[OK] " + descripcion
            );

        } else {

            fallidas++;

            System.out.println(
                    "[FALLO] " + descripcion
            );
        }
    }

    private static void verificarIgual(
            String descripcion,
            Object esperado,
            Object obtenido) {

        boolean correcto =
                Objects.equals(esperado, obtenido);

        verificar(
                descripcion,
                correcto
        );

        if (!correcto) {

            System.out.println(
                    "   Esperado: " + esperado
            );

            System.out.println(
                    "   Obtenido: " + obtenido
            );
        }
    }

    private static void verificarMonto(
            String descripcion,
            String esperado,
            BigDecimal obtenido) {

        BigDecimal valorEsperado =
                new BigDecimal(esperado);

        verificar(
                descripcion,
                obtenido != null
                        && valorEsperado.compareTo(
                        obtenido
                ) == 0
        );

        if (obtenido == null
                || valorEsperado.compareTo(obtenido) != 0) {

            System.out.println(
                    "   Esperado: " + valorEsperado
            );

            System.out.println(
                    "   Obtenido: " + obtenido
            );
        }
    }

    private static void verificarExcepcion(
            String descripcion,
            Class<? extends Throwable> tipo,
            Runnable operacion) {

        try {

            operacion.run();

            verificar(descripcion, false);

        } catch (Throwable error) {

            verificar(
                    descripcion,
                    tipo.isInstance(error)
            );

            if (!tipo.isInstance(error)) {

                System.out.println(
                        "   Excepcion inesperada: "
                                + error
                );
            }
        }
    }

    private static BigDecimal dinero(String valor) {

        return new BigDecimal(valor);
    }

    // ==========================================
    // 1. INICIALIZACION
    // ==========================================

    private static void probarInicializacion() {

        Contexto c = new Contexto();

        verificarIgual(
                "Tres clientes registrados",
                3,
                c.clientes.cantidadClientes()
        );

        verificarIgual(
                "Tres monederos principales",
                3,
                c.monederos.cantidadMonederos()
        );

        verificarIgual(
                "Tres clientes activos",
                3,
                c.clientes.cantidadClientesActivos()
        );

        verificarMonto(
                "Saldo total inicial",
                "0",
                c.monederos.saldoTotalSistema()
        );

        verificarIgual(
                "Cero transacciones iniciales",
                0,
                c.transacciones.cantidadTransacciones()
        );

        verificarIgual(
                "Cero puntos iniciales",
                0L,
                c.puntos.totalPuntosSistema()
        );

        verificarIgual(
                "Tres clientes indexados en arboles",
                3,
                c.rangos.cantidadClientesIndexados()
        );

        verificarIgual(
                "Una puntuacion distinta en los arboles",
                1,
                c.rangos.cantidadNodosAVL()
        );

        verificarIgual(
                "Cero programaciones iniciales",
                0,
                c.programaciones.cantidadProgramaciones()
        );

        verificarIgual(
                "Cero notificaciones iniciales",
                0,
                c.notificaciones.cantidadNotificaciones()
        );

        verificar(
                "Auditoria inicial sin errores",
                c.integridad.sistemaIntegro()
        );
    }

    // ==========================================
    // 2. OPERACIONES FINANCIERAS
    // ==========================================

    private static void probarFinanzas() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001",
                        "M001",
                        dinero("10000")
                );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("2000"),
                        dinero("100")
                );

        Transaccion retiro =
                c.transacciones.retirar(
                        "C002",
                        "M002",
                        dinero("500")
                );

        verificar(
                "Deposito completado",
                deposito.estaCompletada()
        );

        verificar(
                "Transferencia completada",
                transferencia.estaCompletada()
        );

        verificar(
                "Retiro completado",
                retiro.estaCompletada()
        );

        verificarMonto(
                "Saldo de Juan",
                "7900",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Saldo de Ana",
                "1500",
                c.monederos.consultarSaldo(
                        "C002", "M002"
                )
        );

        verificarMonto(
                "Saldo total financiero",
                "9400",
                c.monederos.saldoTotalSistema()
        );

        verificarMonto(
                "Comision total",
                "100",
                c.transacciones.consultarTotalComisiones()
        );

        verificarIgual(
                "Tres movimientos registrados",
                3,
                c.transacciones.cantidadTransacciones()
        );

        verificar(
                "Integridad financiera",
                c.transacciones.verificarIntegridad()
        );

        verificar(
                "Conciliacion de saldos",
                c.integridad.verificarConciliacionFinanciera()
        );
    }

    // ==========================================
    // 3. PUNTOS POR TRANSACCIONES
    // ==========================================

    private static void probarPuntos() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        verificarIgual(
                "Registrar 100 puntos de deposito",
                100,
                c.puntos.registrarPuntosPorTransaccion(
                        deposito
                )
        );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001", "M001",
                        "C002", "M002",
                        dinero("2000")
                );

        verificarIgual(
                "Registrar 60 puntos de transferencia",
                60,
                c.puntos.registrarPuntosPorTransaccion(
                        transferencia
                )
        );

        Transaccion retiro =
                c.transacciones.retirar(
                        "C002", "M002", dinero("500")
                );

        verificarIgual(
                "Registrar 10 puntos de retiro",
                10,
                c.puntos.registrarPuntosPorTransaccion(
                        retiro
                )
        );

        verificarIgual(
                "Juan acumula 160 puntos",
                160,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Ana acumula 10 puntos",
                10,
                c.puntos.consultarPuntos("C002")
        );

        verificarIgual(
                "Total de 170 puntos",
                170L,
                c.puntos.totalPuntosSistema()
        );

        verificarIgual(
                "Tres transacciones procesadas",
                3,
                c.puntos.cantidadTransaccionesProcesadas()
        );

        verificarExcepcion(
                "No duplicar puntos de deposito",
                IllegalStateException.class,
                () -> c.puntos.registrarPuntosPorTransaccion(
                        deposito
                )
        );

        verificar(
                "Integridad del sistema de puntos",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 4. RANGOS Y CLASIFICACIONES
    // ==========================================

    private static void probarRangos() {

        Contexto c = new Contexto();

        c.puntos.agregarPuntosManuales(
                "C001", 1500, "Prueba"
        );

        c.puntos.agregarPuntosManuales(
                "C002", 600, "Prueba"
        );

        c.puntos.agregarPuntosManuales(
                "C003", 5200, "Prueba"
        );

        verificarIgual(
                "Juan pertenece a Oro",
                Rango.ORO,
                c.rangos.consultarRango("C001")
        );

        verificarIgual(
                "Ana pertenece a Plata",
                Rango.PLATA,
                c.rangos.consultarRango("C002")
        );

        verificarIgual(
                "Luis pertenece a Platino",
                Rango.PLATINO,
                c.rangos.consultarRango("C003")
        );

        verificarIgual(
                "Luis ocupa el primer lugar",
                1,
                c.rangos.obtenerPosicion("C003")
        );

        verificarIgual(
                "Juan ocupa el segundo lugar",
                2,
                c.rangos.obtenerPosicion("C001")
        );

        verificarIgual(
                "Ana ocupa el tercer lugar",
                3,
                c.rangos.obtenerPosicion("C002")
        );

        verificarIgual(
                "Top 2 contiene dos clientes",
                2,
                c.rangos.obtenerTop(2).tamanio()
        );

        verificarIgual(
                "Mayor puntuacion",
                5200,
                c.rangos.maximaPuntuacion()
        );

        verificarIgual(
                "Menor puntuacion",
                600,
                c.rangos.minimaPuntuacion()
        );

        verificar(
                "Integridad de rangos",
                c.rangos.verificarIntegridad()
        );
    }

    // ==========================================
    // 5. ARBOLES ABB Y AVL
    // ==========================================

    private static void probarArboles() {

        Contexto c = new Contexto();

        c.juan.agregarPuntos(100);
        c.ana.agregarPuntos(200);
        c.luis.agregarPuntos(300);

        verificarIgual(
                "ABB contiene tres claves",
                3,
                c.rangos.cantidadNodosABB()
        );

        verificarIgual(
                "AVL contiene tres claves",
                3,
                c.rangos.cantidadNodosAVL()
        );

        verificarIgual(
                "ABB encuentra puntuacion 200",
                1,
                c.rangos.buscarPorPuntosABB(
                        200
                ).tamanio()
        );

        verificarIgual(
                "AVL encuentra puntuacion 200",
                1,
                c.rangos.buscarPorPuntosAVL(
                        200
                ).tamanio()
        );

        verificar(
                "Existe puntuacion 300",
                c.rangos.existePuntuacion(300)
        );

        verificar(
                "No existe puntuacion 500",
                !c.rangos.existePuntuacion(500)
        );

        verificar(
                "Altura ABB positiva",
                c.rangos.obtenerAlturaABB() > 0
        );

        verificar(
                "Altura AVL positiva",
                c.rangos.obtenerAlturaAVL() > 0
        );

        verificar(
                "AVL no supera altura ABB en este escenario",
                c.rangos.obtenerAlturaAVL()
                        <= c.rangos.obtenerAlturaABB()
        );

        // Cambiar puntuaciones despues
        // de construir los indices.

        c.juan.agregarPuntos(200);

        verificarIgual(
                "ABB se actualiza tras cambio de puntos",
                0,
                c.rangos.buscarPorPuntosABB(
                        100
                ).tamanio()
        );

        verificarIgual(
                "AVL refleja la nueva puntuacion",
                2,
                c.rangos.buscarPorPuntosAVL(
                        300
                ).tamanio()
        );

        verificar(
                "Integridad de ambos arboles",
                c.rangos.verificarIntegridad()
        );
    }

    // ==========================================
    // 6. BENEFICIOS
    // ==========================================

    private static void probarBeneficios() {

        Contexto c = new Contexto();

        c.puntos.agregarPuntosManuales(
                "C001", 2000, "Prueba"
        );

        Beneficio descuento =
                c.puntos.canjearDescuentoTransferencia(
                        "C001"
                );

        verificarIgual(
                "Descuento cuesta 100 puntos",
                1900,
                c.juan.getPuntos()
        );

        verificarMonto(
                "Comision 1000 queda en 900",
                "900",
                descuento.calcularComisionConDescuento(
                        dinero("1000")
                )
        );

        Beneficio retiros =
                c.puntos.canjearRetirosSinComision(
                        "C001"
                );

        verificarIgual(
                "Retiros cuestan 500 puntos",
                1400,
                c.juan.getPuntos()
        );

        verificarMonto(
                "Retiros pueden eliminar comision",
                "0",
                retiros.calcularComisionConDescuento(
                        dinero("500")
                )
        );

        Beneficio bono =
                c.puntos.canjearBonoSaldo("C001");

        verificarIgual(
                "Bono cuesta 1000 puntos",
                400,
                c.juan.getPuntos()
        );

        verificarMonto(
                "Bono representa 50 unidades",
                "50",
                bono.getMontoBono()
        );

        verificarMonto(
                "Bono aun no modifica saldo financiero",
                "0",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarIgual(
                "Tres beneficios canjeados",
                3,
                c.puntos.cantidadBeneficiosCanjeados()
        );

        verificarIgual(
                "Cliente tiene tres beneficios",
                3,
                c.puntos.listarBeneficios(
                        "C001"
                ).tamanio()
        );

        verificar(
                "Integridad de beneficios",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 7. NOTIFICACIONES FINANCIERAS
    // ==========================================

    private static void probarNotificaciones() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        c.notificaciones.notificarDeposito(
                "C001",
                deposito.getMonto(),
                deposito.getId()
        );

        int puntos =
                c.puntos.registrarPuntosPorTransaccion(
                        deposito
                );

        c.notificaciones.notificarPuntosGanados(
                "C001",
                puntos,
                deposito.getId()
        );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001", "M001",
                        "C002", "M002",
                        dinero("2000")
                );

        c.notificaciones.notificarTransferencia(
                transferencia
        );

        verificarIgual(
                "Cuatro notificaciones generadas",
                4,
                c.notificaciones.cantidadNotificaciones()
        );

        verificarIgual(
                "Juan recibe tres notificaciones",
                3,
                c.notificaciones.cantidadNotificacionesCliente(
                        "C001"
                )
        );

        verificarIgual(
                "Ana recibe una notificacion",
                1,
                c.notificaciones.cantidadNotificacionesCliente(
                        "C002"
                )
        );

        verificarIgual(
                "Cuatro notificaciones no leidas",
                4,
                c.notificaciones.totalNoLeidasSistema()
        );

        verificarIgual(
                "Transferencia produce dos avisos",
                2,
                c.notificaciones.listarPorTransaccion(
                        transferencia.getId()
                ).tamanio()
        );

        verificarIgual(
                "Un aviso de puntos para Juan",
                1,
                c.notificaciones.listarPorTipo(
                        "C001",
                        Notificacion.Tipo.PUNTOS_GANADOS
                ).tamanio()
        );

        verificar(
                "Integridad de notificaciones",
                c.notificaciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 8. CIRCULARIDAD Y LECTURA
    // ==========================================

    private static void probarCircularidad() {

        Contexto c = new Contexto();

        Notificacion primera =
                c.notificaciones.notificarRecordatorio(
                        "C001",
                        "Primera alerta"
                );

        Notificacion segunda =
                c.notificaciones.notificarRecordatorio(
                        "C001",
                        "Segunda alerta"
                );

        Notificacion tercera =
                c.notificaciones.notificarRecordatorio(
                        "C001",
                        "Tercera alerta"
                );

        verificarIgual(
                "Primera notificacion actual",
                primera.getId(),
                c.notificaciones.consultarActual(
                        "C001"
                ).getId()
        );

        verificarIgual(
                "Avanzar a segunda",
                segunda.getId(),
                c.notificaciones.avanzarCircular(
                        "C001"
                ).getId()
        );

        verificarIgual(
                "Avanzar a tercera",
                tercera.getId(),
                c.notificaciones.avanzarCircular(
                        "C001"
                ).getId()
        );

        verificarIgual(
                "Recorrido circular vuelve a primera",
                primera.getId(),
                c.notificaciones.avanzarCircular(
                        "C001"
                ).getId()
        );

        c.notificaciones.marcarLeida(
                primera.getId()
        );

        verificar(
                "Primera notificacion leida",
                primera.isLeida()
        );

        verificarIgual(
                "Quedan dos notificaciones pendientes",
                2,
                c.notificaciones.cantidadNoLeidas(
                        "C001"
                )
        );

        verificarIgual(
                "Marcar dos notificaciones restantes",
                2,
                c.notificaciones.marcarTodasLeidas(
                        "C001"
                )
        );

        verificarIgual(
                "Ninguna notificacion pendiente",
                0,
                c.notificaciones.cantidadNoLeidas(
                        "C001"
                )
        );

        verificar(
                "Eliminar notificacion existente",
                c.notificaciones.eliminarNotificacion(
                        segunda.getId()
                )
        );

        verificarIgual(
                "Quedan dos notificaciones",
                2,
                c.notificaciones.cantidadNotificaciones()
        );

        verificar(
                "Circularidad e indices consistentes",
                c.notificaciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 9. PROGRAMACIONES
    // ==========================================

    private static void probarProgramaciones() {

        Contexto c = new Contexto();

        LocalDateTime fechaBase =
                LocalDateTime.now();

        TransaccionProgramada deposito =
                c.programaciones.programarDeposito(
                        "C001",
                        "M001",
                        dinero("5000"),
                        fechaBase.plusDays(2)
                );

        TransaccionProgramada transferencia =
                c.programaciones.programarTransferencia(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("1000"),
                        fechaBase.plusDays(1),
                        TransaccionProgramada.Frecuencia.MENSUAL
                );

        verificarIgual(
                "Dos programaciones registradas",
                2,
                c.programaciones.cantidadProgramaciones()
        );

        verificarIgual(
                "Dos programaciones activas",
                2,
                c.programaciones.cantidadProgramacionesActivas()
        );

        verificarIgual(
                "Transferencia tiene prioridad temporal",
                transferencia.getId(),
                c.programaciones.consultarProximaProgramacion()
                        .getId()
        );

        verificarIgual(
                "Dos elementos en cola de prioridad",
                2,
                c.programaciones.cantidadEnCola()
        );

        verificarIgual(
                "Ninguna ejecucion antes de la fecha",
                0,
                c.programaciones.ejecutarPendientes()
                        .tamanio()
        );

        verificarIgual(
                "No se modifica historial financiero",
                0,
                c.transacciones.cantidadTransacciones()
        );

        c.programaciones.pausarProgramacion(
                transferencia.getId()
        );

        verificarIgual(
                "Una programacion pausada",
                1,
                c.programaciones.cantidadProgramacionesPausadas()
        );

        verificarIgual(
                "Deposito queda primero en la cola",
                deposito.getId(),
                c.programaciones.consultarProximaProgramacion()
                        .getId()
        );

        c.programaciones.reanudarProgramacion(
                transferencia.getId()
        );

        verificarIgual(
                "Dos programaciones reactivadas",
                2,
                c.programaciones.cantidadProgramacionesActivas()
        );

        c.programaciones.cancelarProgramacion(
                deposito.getId()
        );

        verificarIgual(
                "Una programacion cancelada",
                1,
                c.programaciones.cantidadProgramacionesCanceladas()
        );

        verificarIgual(
                "Una programacion activa restante",
                1,
                c.programaciones.cantidadProgramacionesActivas()
        );

        verificarIgual(
                "Programacion buscada mediante hash",
                transferencia.getId(),
                c.programaciones.obtenerProgramacion(
                        transferencia.getId()
                ).getId()
        );

        verificar(
                "Integridad de programaciones",
                c.programaciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 10. GRAFOS
    // ==========================================

    private static void probarGrafos() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("30000")
        );

        c.transacciones.transferir(
                "C001", "M001",
                "C002", "M002",
                dinero("6000")
        );

        c.transacciones.transferir(
                "C002", "M002",
                "C003", "M003",
                dinero("2000")
        );

        GrafoDirigido grafo =
                c.transacciones.obtenerGrafoTransferencias();

        verificarIgual(
                "Tres vertices en el grafo",
                3,
                grafo.cantidadVertices()
        );

        verificarIgual(
                "Dos relaciones dirigidas",
                2,
                grafo.cantidadAristas()
        );

        verificar(
                "Existe arista Juan hacia Ana",
                grafo.existeArista("C001", "C002")
        );

        verificar(
                "Existe arista Ana hacia Luis",
                grafo.existeArista("C002", "C003")
        );

        verificar(
                "Existe camino Juan hacia Luis",
                grafo.existeCamino("C001", "C003")
        );

        verificar(
                "No existe camino Luis hacia Juan",
                !grafo.existeCamino("C003", "C001")
        );

        verificarMonto(
                "Juan envio 6000",
                "6000",
                grafo.totalEnviado("C001")
        );

        verificarMonto(
                "Luis recibio 2000",
                "2000",
                grafo.totalRecibido("C003")
        );

        verificar(
                "Integridad del grafo",
                c.integridad.verificarGrafoTransferencias()
        );
    }

    // ==========================================
    // 11. REPORTES
    // ==========================================

    private static void probarReportes() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        c.puntos.registrarPuntosPorTransaccion(
                deposito
        );

        c.notificaciones.notificarDeposito(
                "C001",
                dinero("10000"),
                deposito.getId()
        );

        String general =
                c.reportes.generarReporteGeneral();

        String clientes =
                c.reportes.generarReporteClientes();

        String financiero =
                c.reportes.generarReporteTransacciones();

        String puntos =
                c.reportes.generarReportePuntos();

        String ranking =
                c.reportes.generarReporteRanking();

        String transferencias =
                c.reportes.generarReporteTransferencias();

        String estructuras =
                c.reportes.generarReporteEstructuras();

        String completo =
                c.reportes.generarReporteCompleto();

        verificar(
                "Reporte general generado",
                general != null
                        && general.contains(
                        "REPORTE GENERAL"
                )
        );

        verificar(
                "Reporte de clientes incluye a Juan",
                clientes.contains("Juan")
        );

        verificar(
                "Historial incluye deposito",
                financiero.contains(deposito.getId())
        );

        verificar(
                "Reporte de puntos incluye puntuaciones",
                puntos.contains("Puntos")
                        || puntos.contains("puntos")
        );

        verificar(
                "Ranking incluye cliente",
                ranking.contains("Juan")
        );

        verificar(
                "Reporte de transferencias generado",
                transferencias.contains(
                        "REPORTE DE TRANSFERENCIAS"
                )
        );

        verificar(
                "Reporte de estructuras compara arboles",
                estructuras.contains("ABB")
                        && estructuras.contains("AVL")
        );

        verificar(
                "Reporte completo contiene varias secciones",
                completo.contains("REPORTE DE CLIENTES")
                        && completo.contains(
                        "REPORTE DE MONEDEROS"
                )
        );

        verificar(
                "Generar reportes no altera el saldo",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                ).compareTo(dinero("10000")) == 0
        );

        verificarIgual(
                "Generar reportes no altera puntos",
                100,
                c.juan.getPuntos()
        );
    }

    // ==========================================
    // 12. CONCILIACION FINANCIERA
    // ==========================================

    private static void probarConciliacion() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("100000")
        );

        c.transacciones.transferir(
                "C001", "M001",
                "C002", "M002",
                dinero("20000"),
                dinero("500")
        );

        c.transacciones.retirar(
                "C001", "M001", dinero("10000")
        );

        c.transacciones.registrarGasto(
                "C002",
                "M002",
                dinero("3000"),
                "Alimentacion"
        );

        verificarMonto(
                "Saldo final de Juan",
                "69500",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Saldo final de Ana",
                "17000",
                c.monederos.consultarSaldo(
                        "C002", "M002"
                )
        );

        verificarMonto(
                "Saldo total del sistema",
                "86500",
                c.monederos.saldoTotalSistema()
        );

        verificarMonto(
                "Comision neta",
                "500",
                c.transacciones.consultarTotalComisiones()
        );

        verificar(
                "Coincidencia entre saldos totales",
                c.integridad.verificarSaldosTotales()
        );

        verificar(
                "Conciliacion con historial",
                c.integridad.verificarConciliacionFinanciera()
        );

        verificar(
                "Referencias de transacciones validas",
                c.integridad.verificarReferenciasTransacciones()
        );

        verificar(
                "Pertenencia de monederos valida",
                c.integridad.verificarPertenenciaMonederos()
        );

        verificar(
                "Auditoria basica correcta",
                c.integridad.ejecutarAuditoriaBasica()
                        .esCorrecta()
        );
    }

    // ==========================================
    // 13. REVERSIONES
    // ==========================================

    private static void probarReversiones() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        c.puntos.registrarPuntosPorTransaccion(
                deposito
        );

        verificarIgual(
                "Deposito otorga 100 puntos",
                100,
                c.juan.getPuntos()
        );

        verificar(
                "Reversion de puntos permitida",
                c.puntos.puedeRevertirPuntos(
                        deposito
                )
        );

        Transaccion reversion =
                c.transacciones.revertirUltimaTransaccion();

        verificar(
                "Reversion monetaria completada",
                reversion.estaCompletada()
        );

        verificarIgual(
                "Compensar 100 puntos",
                100,
                c.puntos.revertirPuntosDeTransaccion(
                        deposito
                )
        );

        verificarMonto(
                "Saldo monetario vuelve a cero",
                "0",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarIgual(
                "Puntos vuelven a cero",
                0,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Puntos netos de operacion revertida",
                0,
                c.puntos.consultarPuntosTransaccion(
                        deposito.getId()
                )
        );

        verificar(
                "Conciliacion despues de reversion",
                c.integridad.verificarConciliacionFinanciera()
        );

        verificar(
                "Integridad financiera despues de reversion",
                c.transacciones.verificarIntegridad()
        );

        verificar(
                "Integridad de puntos despues de reversion",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 14. AUDITORIA COMPLETA
    // ==========================================

    private static void probarAuditoria() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("20000")
                );

        c.puntos.registrarPuntosPorTransaccion(
                deposito
        );

        c.notificaciones.notificarDeposito(
                "C001",
                deposito.getMonto(),
                deposito.getId()
        );

        c.programaciones.programarDeposito(
                "C002",
                "M002",
                dinero("1000"),
                LocalDateTime.now().plusDays(3)
        );

        IntegridadService.ResultadoAuditoria auditoria =
                c.integridad.ejecutarAuditoriaCompleta();

        verificar(
                "Auditoria completa sin errores",
                auditoria.esCorrecta()
        );

        verificarIgual(
                "Cero errores de integridad",
                0,
                auditoria.getCantidadErrores()
        );

        verificar(
                "Auditoria realiza comprobaciones",
                auditoria.getComprobacionesRealizadas() > 0
        );

        verificarIgual(
                "Todas las comprobaciones aprobadas",
                auditoria.getComprobacionesRealizadas(),
                auditoria.getComprobacionesAprobadas()
        );

        verificar(
                "Advertencias de integracion registradas",
                auditoria.tieneAdvertencias()
        );

        verificar(
                "Informe contiene resumen",
                auditoria.generarInforme()
                        .contains("RESUMEN DE AUDITORIA")
        );

        verificar(
                "Informe contiene resultado",
                auditoria.generarInforme()
                        .contains("SIN ERRORES DETECTADOS")
        );

        verificar(
                "Consulta rapida de integridad",
                c.integridad.sistemaIntegro()
        );
    }

    // ==========================================
    // 15. VALIDACIONES ENTRE SERVICIOS
    // ==========================================

    private static void probarValidaciones() {

        Contexto c = new Contexto();

        verificarExcepcion(
                "No depositar a cliente inexistente",
                IllegalArgumentException.class,
                () -> c.transacciones.depositar(
                        "C999", "M001", dinero("100")
                )
        );

        verificarExcepcion(
                "No retirar fondos inexistentes",
                IllegalArgumentException.class,
                () -> c.transacciones.retirar(
                        "C001", "M001", dinero("1000")
                )
        );

        verificarExcepcion(
                "No canjear sin puntos",
                IllegalArgumentException.class,
                () -> c.puntos.canjearBonoSaldo(
                        "C001"
                )
        );

        verificarExcepcion(
                "Rechazar busqueda por puntos negativos",
                IllegalArgumentException.class,
                () -> c.rangos.buscarPorPuntosAVL(-1)
        );

        verificarExcepcion(
                "Rechazar top negativo",
                IllegalArgumentException.class,
                () -> c.rangos.obtenerTop(-1)
        );

        verificarExcepcion(
                "No consultar notificacion inexistente",
                IllegalArgumentException.class,
                () -> c.notificaciones.obtenerNotificacion(
                        "N999"
                )
        );

        verificarExcepcion(
                "No consultar programacion inexistente",
                IllegalArgumentException.class,
                () -> c.programaciones.obtenerProgramacion(
                        "P999"
                )
        );

        verificarIgual(
                "Rechazos no crean movimientos",
                0,
                c.transacciones.cantidadTransacciones()
        );

        verificarIgual(
                "Rechazos no alteran puntos",
                0L,
                c.puntos.totalPuntosSistema()
        );

        verificar(
                "Sistema integro despues de rechazos",
                c.integridad.sistemaIntegro()
        );
    }

    // ==========================================
    // 16. ESCENARIO INTEGRAL
    // ==========================================

    private static void probarEscenarioIntegral() {

        Contexto c = new Contexto();

        // --------------------------------------
        // 1. DEPOSITO
        // --------------------------------------

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("100000")
                );

        int puntosDeposito =
                c.puntos.registrarPuntosPorTransaccion(
                        deposito
                );

        c.notificaciones.notificarDeposito(
                "C001",
                deposito.getMonto(),
                deposito.getId()
        );

        c.notificaciones.notificarPuntosGanados(
                "C001",
                puntosDeposito,
                deposito.getId()
        );

        verificarIgual(
                "Deposito genera 1000 puntos",
                1000,
                puntosDeposito
        );

        verificarIgual(
                "Juan alcanza rango Plata",
                Rango.PLATA,
                c.rangos.consultarRango("C001")
        );

        // --------------------------------------
        // 2. CANJE DE BENEFICIO
        // --------------------------------------

        Beneficio beneficio =
                c.puntos.canjearDescuentoTransferencia(
                        "C001"
                );

        c.notificaciones.notificarCanje(
                "C001",
                beneficio.getDescripcion()
        );

        verificarIgual(
                "Canje descuenta 100 puntos",
                900,
                c.juan.getPuntos()
        );

        verificar(
                "Beneficio queda activo",
                beneficio.estaDisponibleParaUso()
        );

        // --------------------------------------
        // 3. TRANSFERENCIA
        // --------------------------------------

        // El descuento del beneficio se
        // calcula aqui explicitamente.
        // Aun no esta integrado automaticamente
        // en TransaccionService.

        BigDecimal comisionConDescuento =
                beneficio.calcularComisionConDescuento(
                        dinero("1000")
                );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("20000"),
                        comisionConDescuento
                );

        beneficio.utilizar();

        int puntosTransferencia =
                c.puntos.registrarPuntosPorTransaccion(
                        transferencia
                );

        c.notificaciones.notificarTransferencia(
                transferencia
        );

        verificarMonto(
                "Comision con descuento del 10%",
                "900",
                comisionConDescuento
        );

        verificarMonto(
                "Saldo Juan tras transferencia",
                "79100",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Saldo Ana tras transferencia",
                "20000",
                c.monederos.consultarSaldo(
                        "C002", "M002"
                )
        );

        verificarIgual(
                "Transferencia otorga 600 puntos",
                600,
                puntosTransferencia
        );

        verificarIgual(
                "Juan acumula 1500 puntos",
                1500,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Juan asciende a Oro",
                Rango.ORO,
                c.rangos.consultarRango("C001")
        );

        verificarIgual(
                "Beneficio utilizado",
                Beneficio.Estado.UTILIZADO,
                beneficio.getEstado()
        );

        // --------------------------------------
        // 4. GRAFO
        // --------------------------------------

        GrafoDirigido grafo =
                c.transacciones.obtenerGrafoTransferencias();

        verificarMonto(
                "Grafo registra transferencia",
                "20000",
                grafo.obtenerPeso(
                        "C001", "C002"
                )
        );

        // --------------------------------------
        // 5. PROGRAMACION FUTURA
        // --------------------------------------

        c.programaciones.programarDeposito(
                "C003",
                "M003",
                dinero("5000"),
                LocalDateTime.now().plusDays(5)
        );

        verificarIgual(
                "Programacion futura registrada",
                1,
                c.programaciones.cantidadProgramaciones()
        );

        verificarIgual(
                "Programacion no ejecutada antes de fecha",
                0,
                c.programaciones.ejecutarPendientes()
                        .tamanio()
        );

        // --------------------------------------
        // 6. REPORTE
        // --------------------------------------

        String reporte =
                c.reportes.generarReporteCompleto();

        verificar(
                "Reporte completo generado",
                reporte != null
                        && reporte.contains(
                        "REPORTE GENERAL"
                )
        );

        verificar(
                "Ranking contiene a Juan",
                c.reportes.generarReporteRanking()
                        .contains("Juan")
        );

        // --------------------------------------
        // 7. AUDITORIA
        // --------------------------------------

        IntegridadService.ResultadoAuditoria auditoria =
                c.integridad.ejecutarAuditoriaCompleta();

        verificar(
                "Auditoria final sin errores",
                auditoria.esCorrecta()
        );

        verificar(
                "Conciliacion monetaria final",
                c.integridad.verificarConciliacionFinanciera()
        );

        verificar(
                "Integridad de puntos final",
                c.puntos.verificarIntegridad()
        );

        verificar(
                "Integridad de notificaciones final",
                c.notificaciones.verificarIntegridad()
        );

        verificar(
                "Integridad de programaciones final",
                c.programaciones.verificarIntegridad()
        );

        verificarIgual(
                "Dos transacciones financieras",
                2,
                c.transacciones.cantidadTransacciones()
        );

        verificarIgual(
                "Dos transacciones con puntos",
                2,
                c.puntos.cantidadTransaccionesProcesadas()
        );

        verificarIgual(
                "Un beneficio canjeado",
                1,
                c.puntos.cantidadBeneficiosCanjeados()
        );

        verificarIgual(
                "Cinco notificaciones generadas",
                5,
                c.notificaciones.cantidadNotificaciones()
        );
    }


    // ==========================================
    // 17. COORDINADOR: DEPOSITOS
    // ==========================================

    /**
     * Comprueba que el coordinador:
     *
     * - Ejecute el deposito financiero.
     * - Otorgue puntos automaticamente.
     * - Registre los puntos en la transaccion.
     * - Genere notificaciones.
     * - Conserve la integridad financiera.
     *
     * No se invoca manualmente PuntosService
     * ni NotificacionService.
     */
    private static void probarCoordinadorDepositos() {

        Contexto c = new Contexto();

        CoordinadorOperaciones.ResultadoOperacion resultado =
                c.coordinador.depositar(
                        "C001",
                        "M001",
                        dinero("10000")
                );

        verificar(
                "Deposito financiero completado",
                resultado.esExitosaFinancieramente()
        );

        verificarIgual(
                "Deposito otorga 100 puntos automaticamente",
                100,
                resultado.getPuntosOtorgados()
        );

        verificarIgual(
                "Cliente recibe los 100 puntos",
                100,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Transaccion registra puntos generados",
                100,
                resultado.getTransaccion()
                        .getPuntosGenerados()
        );

        verificarMonto(
                "Monedero recibe 10000",
                "10000",
                c.monederos.consultarSaldo(
                        "C001",
                        "M001"
                )
        );

        verificarIgual(
                "Dos notificaciones generadas",
                2,
                resultado.getNotificacionesEnviadas()
        );

        verificarIgual(
                "Cliente recibe dos notificaciones",
                2,
                c.notificaciones
                        .cantidadNotificacionesCliente("C001")
        );

        verificar(
                "Asignacion de puntos procesada",
                resultado.isPuntosProcesados()
        );

        verificar(
                "Operacion sin incidencias",
                !resultado.tieneIncidencias()
        );

        verificarIgual(
                "Una transaccion financiera registrada",
                1,
                c.transacciones.cantidadTransacciones()
        );

        verificar(
                "Conciliacion financiera correcta",
                c.integridad.verificarConciliacionFinanciera()
        );

        verificar(
                "Auditoria completa correcta",
                c.integridad.sistemaIntegro()
        );
    }

    // ==========================================
    // 18. COORDINADOR: RETIROS
    // ==========================================

    /**
     * Verifica que retirar dinero mediante
     * el coordinador produzca puntos y
     * notificaciones automaticamente.
     */
    private static void probarCoordinadorRetiros() {

        Contexto c = new Contexto();

        c.coordinador.depositar(
                "C001",
                "M001",
                dinero("10000")
        );

        CoordinadorOperaciones.ResultadoOperacion resultado =
                c.coordinador.retirar(
                        "C001",
                        "M001",
                        dinero("1000")
                );

        verificar(
                "Retiro completado",
                resultado.esExitosaFinancieramente()
        );

        verificarIgual(
                "Retiro genera 20 puntos",
                20,
                resultado.getPuntosOtorgados()
        );

        verificarIgual(
                "Cliente acumula 120 puntos",
                120,
                c.juan.getPuntos()
        );

        verificarMonto(
                "Saldo despues del retiro",
                "9000",
                c.monederos.consultarSaldo(
                        "C001",
                        "M001"
                )
        );

        verificarIgual(
                "Retiro genera dos avisos",
                2,
                resultado.getNotificacionesEnviadas()
        );

        verificarIgual(
                "Cuatro notificaciones acumuladas",
                4,
                c.notificaciones.cantidadNotificaciones()
        );

        verificarIgual(
                "Dos transacciones financieras",
                2,
                c.transacciones.cantidadTransacciones()
        );

        verificar(
                "Retiro sin incidencias",
                !resultado.tieneIncidencias()
        );

        verificar(
                "Conciliacion correcta",
                c.integridad.verificarConciliacionFinanciera()
        );

        verificar(
                "Integridad de puntos",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 19. COORDINADOR: TRANSFERENCIAS
    // ==========================================

    /**
     * Comprueba:
     *
     * - Transferencia monetaria.
     * - Puntos automaticos del emisor.
     * - Cambio automatico de rango.
     * - Notificaciones de rango y transferencia.
     * - Actualizacion del grafo dirigido.
     */
    private static void probarCoordinadorTransferencias() {

        Contexto c = new Contexto();

        // Deposito de 50100.
        //
        // Genera 501 puntos, por lo
        // que Juan asciende a Plata.

        CoordinadorOperaciones.ResultadoOperacion deposito =
                c.coordinador.depositar(
                        "C001",
                        "M001",
                        dinero("50100")
                );

        verificarIgual(
                "Deposito genera 501 puntos",
                501,
                deposito.getPuntosOtorgados()
        );

        verificarIgual(
                "Juan asciende a Plata",
                Rango.PLATA,
                c.juan.getRango()
        );

        verificarIgual(
                "Deposito con cambio de rango genera tres avisos",
                3,
                deposito.getNotificacionesEnviadas()
        );

        // Transferir 20000.
        //
        // 200 bloques de 100 * 3 puntos
        // = 600 puntos.
        //
        // Total de Juan: 1101.
        // Nuevo rango: Oro.

        CoordinadorOperaciones.ResultadoOperacion transferencia =
                c.coordinador.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("20000")
                );

        verificar(
                "Transferencia completada",
                transferencia.esExitosaFinancieramente()
        );

        verificarIgual(
                "Transferencia otorga 600 puntos",
                600,
                transferencia.getPuntosOtorgados()
        );

        verificarIgual(
                "Juan acumula 1101 puntos",
                1101,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Juan asciende a Oro",
                Rango.ORO,
                c.juan.getRango()
        );

        verificarIgual(
                "Ana no obtiene puntos por recibir",
                0,
                c.ana.getPuntos()
        );

        verificarMonto(
                "Saldo restante de Juan",
                "30100",
                c.monederos.consultarSaldo(
                        "C001",
                        "M001"
                )
        );

        verificarMonto(
                "Saldo recibido por Ana",
                "20000",
                c.monederos.consultarSaldo(
                        "C002",
                        "M002"
                )
        );

        verificarIgual(
                "Transferencia genera cuatro avisos",
                4,
                transferencia.getNotificacionesEnviadas()
        );

        verificarIgual(
                "Siete notificaciones en el sistema",
                7,
                c.notificaciones.cantidadNotificaciones()
        );

        verificarIgual(
                "Ana recibe aviso de transferencia",
                1,
                c.notificaciones
                        .cantidadNotificacionesCliente("C002")
        );

        verificarMonto(
                "Grafo registra transferencia",
                "20000",
                c.transacciones
                        .obtenerGrafoTransferencias()
                        .obtenerPeso("C001", "C002")
        );

        verificar(
                "Transferencia sin incidencias",
                !transferencia.tieneIncidencias()
        );

        verificar(
                "Auditoria completa sin errores",
                c.integridad.sistemaIntegro()
        );
    }

    // ==========================================
    // 20. COORDINADOR: BENEFICIOS
    // ==========================================

    /**
     * Comprueba el flujo:
     *
     * Deposito -> Puntos -> Canje ->
     * Transferencia con descuento ->
     * Consumo del beneficio.
     *
     * El descuento solo se aplica sobre
     * la comision de transferencia.
     */
    private static void probarCoordinadorBeneficios() {

        Contexto c = new Contexto();

        // Deposito: 2000 puntos.

        c.coordinador.depositar(
                "C001",
                "M001",
                dinero("200000")
        );

        verificarIgual(
                "Deposito genera 2000 puntos",
                2000,
                c.juan.getPuntos()
        );

        // Canjear descuento: -100 puntos.

        Beneficio beneficio =
                c.coordinador.canjearBeneficio(
                        "C001",
                        Beneficio.Tipo.DESCUENTO_TRANSFERENCIA
                );

        verificarIgual(
                "Canje descuenta 100 puntos",
                1900,
                c.juan.getPuntos()
        );

        verificar(
                "Beneficio disponible para transferencia",
                beneficio.estaDisponibleParaUso()
        );

        verificarIgual(
                "Beneficio almacenado",
                1,
                c.puntos.cantidadBeneficiosCanjeados()
        );

        // Transferencia de 20000.
        //
        // Comision original: 1000.
        // Descuento: 10%.
        // Comision final: 900.

        CoordinadorOperaciones.ResultadoOperacion resultado =
                c.coordinador.transferirConBeneficio(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("20000"),
                        dinero("1000"),
                        beneficio.getId()
                );

        verificar(
                "Transferencia con descuento completada",
                resultado.esExitosaFinancieramente()
        );

        verificarMonto(
                "Comision reducida a 900",
                "900",
                resultado.getTransaccion().getComision()
        );

        verificarMonto(
                "Saldo Juan con descuento aplicado",
                "179100",
                c.monederos.consultarSaldo(
                        "C001",
                        "M001"
                )
        );

        verificarMonto(
                "Ana recibe 20000 completos",
                "20000",
                c.monederos.consultarSaldo(
                        "C002",
                        "M002"
                )
        );

        verificarIgual(
                "Transferencia genera 600 puntos",
                600,
                resultado.getPuntosOtorgados()
        );

        verificarIgual(
                "Juan termina con 2500 puntos",
                2500,
                c.juan.getPuntos()
        );

        verificar(
                "El coordinador registra el uso del beneficio",
                resultado.isBeneficioAplicado()
        );

        verificarIgual(
                "Beneficio queda utilizado",
                Beneficio.Estado.UTILIZADO,
                beneficio.getEstado()
        );

        verificarIgual(
                "Beneficio tiene un uso registrado",
                1,
                beneficio.getCantidadUsos()
        );

        verificarExcepcion(
                "Beneficio no puede utilizarse nuevamente",
                IllegalStateException.class,
                () -> c.coordinador.transferirConBeneficio(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("1000"),
                        dinero("100"),
                        beneficio.getId()
                )
        );

        verificar(
                "Transferencia sin incidencias",
                !resultado.tieneIncidencias()
        );

        verificar(
                "Conciliacion con comision reducida",
                c.integridad.verificarConciliacionFinanciera()
        );

        verificar(
                "Integridad del sistema de puntos",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 21. COORDINADOR: REVERSIONES
    // ==========================================

    /**
     * Verifica que el coordinador:
     *
     * - Consulte la operacion LIFO.
     * - Revise disponibilidad de puntos.
     * - Revierta el dinero.
     * - Compense los puntos.
     * - Notifique la reversion.
     */
    private static void probarCoordinadorReversiones() {

        Contexto c = new Contexto();

        CoordinadorOperaciones.ResultadoOperacion deposito =
                c.coordinador.depositar(
                        "C001",
                        "M001",
                        dinero("10000")
                );

        verificarIgual(
                "Deposito genera 100 puntos",
                100,
                c.juan.getPuntos()
        );

        CoordinadorOperaciones.ResultadoOperacion reversion =
                c.coordinador.revertirUltimaTransaccion();

        verificar(
                "Reversion financiera completada",
                reversion.esExitosaFinancieramente()
        );

        verificarIgual(
                "Reversion compensa 100 puntos",
                100,
                reversion.getPuntosCompensados()
        );

        verificarIgual(
                "Juan vuelve a cero puntos",
                0,
                c.juan.getPuntos()
        );

        verificarMonto(
                "Monedero vuelve a saldo cero",
                "0",
                c.monederos.consultarSaldo(
                        "C001",
                        "M001"
                )
        );

        verificar(
                "Operacion original marcada como revertida",
                deposito.getTransaccion().estaRevertida()
        );

        verificarIgual(
                "Puntos netos de operacion revertida",
                0,
                c.puntos.consultarPuntosTransaccion(
                        deposito.getIdTransaccion()
                )
        );

        verificarIgual(
                "Una notificacion de reversion",
                1,
                reversion.getNotificacionesEnviadas()
        );

        verificarIgual(
                "Tres notificaciones acumuladas",
                3,
                c.notificaciones.cantidadNotificaciones()
        );

        verificar(
                "Reversion sin incidencias",
                !reversion.tieneIncidencias()
        );

        verificar(
                "Conciliacion despues de reversion",
                c.integridad.verificarConciliacionFinanciera()
        );

        verificar(
                "Auditoria completa despues de reversion",
                c.integridad.sistemaIntegro()
        );
    }

    // ==========================================
    // 22. COORDINADOR: VALIDACIONES
    // ==========================================

    /**
     * Comprueba que las operaciones
     * rechazadas no modifiquen saldos
     * ni generen puntos o notificaciones.
     *
     * Tambien comprueba el rechazo de una
     * reversion cuando los puntos obtenidos
     * ya se han utilizado.
     */
    private static void probarCoordinadorValidaciones() {

        Contexto c = new Contexto();

        verificarExcepcion(
                "Rechazar retiro sin fondos",
                IllegalArgumentException.class,
                () -> c.coordinador.retirar(
                        "C001",
                        "M001",
                        dinero("1000")
                )
        );

        verificarExcepcion(
                "Rechazar deposito a cliente inexistente",
                IllegalArgumentException.class,
                () -> c.coordinador.depositar(
                        "C999",
                        "M001",
                        dinero("1000")
                )
        );

        verificarIgual(
                "Operaciones fallidas no crean transacciones",
                0,
                c.transacciones.cantidadTransacciones()
        );

        verificarIgual(
                "Operaciones fallidas no generan puntos",
                0L,
                c.puntos.totalPuntosSistema()
        );

        verificarIgual(
                "Operaciones fallidas no generan avisos",
                0,
                c.notificaciones.cantidadNotificaciones()
        );

        // Crear una operacion valida que
        // genere exactamente 100 puntos.

        c.coordinador.depositar(
                "C001",
                "M001",
                dinero("10000")
        );

        // Gastar los 100 puntos
        // en un beneficio.

        Beneficio beneficio =
                c.coordinador.canjearBeneficio(
                        "C001",
                        Beneficio.Tipo.DESCUENTO_TRANSFERENCIA
                );

        verificarIgual(
                "Cliente gasto los puntos generados",
                0,
                c.juan.getPuntos()
        );

        verificar(
                "Beneficio adquirido correctamente",
                beneficio.estaDisponibleParaUso()
        );

        verificarExcepcion(
                "Rechazar reversion con puntos gastados",
                IllegalStateException.class,
                c.coordinador::revertirUltimaTransaccion
        );

        verificarMonto(
                "El rechazo conserva el saldo monetario",
                "10000",
                c.monederos.consultarSaldo(
                        "C001",
                        "M001"
                )
        );

        verificarIgual(
                "El rechazo conserva la transaccion original",
                1,
                c.transacciones.cantidadTransacciones()
        );

        verificar(
                "Operacion original sigue completada",
                c.transacciones
                        .consultarUltimaReversible()
                        .estaCompletada()
        );

        verificar(
                "Integridad despues de los rechazos",
                c.integridad.sistemaIntegro()
        );
    }


    // ==========================================
    // RESUMEN
    // ==========================================

    private static void mostrarResumen() {

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                " RESUMEN GENERAL"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Grupos aprobados: "
                        + gruposAprobados
        );

        System.out.println(
                "Grupos fallidos: "
                        + gruposFallidos
        );

        System.out.println(
                "Verificaciones aprobadas: "
                        + aprobadas
        );

        System.out.println(
                "Verificaciones fallidas: "
                        + fallidas
        );

        System.out.println(
                "----------------------------------------"
        );

        if (fallidas == 0 && gruposFallidos == 0) {

            System.out.println(
                    "RESULTADO: TODAS LAS PRUEBAS APROBADAS"
            );

        } else {

            System.out.println(
                    "RESULTADO: SE ENCONTRARON ERRORES"
            );
        }

        System.out.println(
                "========================================"
        );
    }
}
