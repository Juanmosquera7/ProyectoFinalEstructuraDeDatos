
package pruebas;

import estructuras.ListaEnlazada;

import modelo.Beneficio;
import modelo.Cliente;
import modelo.Rango;
import modelo.TipoTransaccion;
import modelo.Transaccion;

import servicios.ClienteService;
import servicios.MonederoService;
import servicios.PuntosService;
import servicios.TransaccionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * PRUEBAS COMPLETAS DEL SISTEMA DE PUNTOS
 *
 * Proyecto:
 * Monedero Virtual con Sistema de Puntos.
 *
 * Comprueba:
 *
 * - Reglas y calculos de puntos.
 * - Acumulacion y descuento.
 * - Rangos.
 * - Catalogo de beneficios.
 * - Descuentos de comisiones.
 * - Estados de los beneficios.
 * - Validaciones.
 * - Integracion financiera basica.
 * - PuntosService.
 * - Prevencion de puntos duplicados.
 * - Canjes de beneficios.
 * - Historial de movimientos.
 * - Reversiones de puntos.
 * - Ajustes administrativos.
 * - Estadisticas.
 * - Integridad.
 *
 * No requiere JUnit.
 *
 * Cada grupo utiliza datos independientes.
 */
public class PruebaPuntos {

    // ==========================================
    // CONTADORES
    // ==========================================

    private static int aprobadas = 0;
    private static int fallidas = 0;

    private static int gruposAprobados = 0;
    private static int gruposFallidos = 0;

    // ==========================================
    // CONTEXTO DE PRUEBAS
    // ==========================================

    private static class Contexto {

        final ClienteService clientes;
        final MonederoService monederos;
        final TransaccionService transacciones;
        final PuntosService puntos;

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
                " PRUEBAS COMPLETAS DE PUNTOS"
        );

        System.out.println(
                "========================================"
        );

        // Grupos originales.

        ejecutarGrupo(
                "Reglas de puntos",
                PruebaPuntos::probarReglasPuntos
        );

        ejecutarGrupo(
                "Calculo de puntos por transaccion",
                PruebaPuntos::probarCalculoPuntos
        );

        ejecutarGrupo(
                "Acumulacion de puntos",
                PruebaPuntos::probarAcumulacion
        );

        ejecutarGrupo(
                "Descuento de puntos",
                PruebaPuntos::probarDescuentoPuntos
        );

        ejecutarGrupo(
                "Rangos y limites",
                PruebaPuntos::probarRangos
        );

        ejecutarGrupo(
                "Catalogo de beneficios",
                PruebaPuntos::probarCatalogoBeneficios
        );

        ejecutarGrupo(
                "Descuento de comisiones",
                PruebaPuntos::probarDescuentos
        );

        ejecutarGrupo(
                "Estados y utilizacion",
                PruebaPuntos::probarEstadosBeneficios
        );

        ejecutarGrupo(
                "Beneficios temporales",
                PruebaPuntos::probarBeneficiosTemporales
        );

        ejecutarGrupo(
                "Validaciones de puntos y beneficios",
                PruebaPuntos::probarValidaciones
        );

        ejecutarGrupo(
                "Integracion financiera basica",
                PruebaPuntos::probarIntegracionFinanciera
        );

        // Nuevos grupos de PuntosService.

        ejecutarGrupo(
                "Asignacion real de puntos",
                PruebaPuntos::probarAsignacionReal
        );

        ejecutarGrupo(
                "Prevencion de puntos duplicados",
                PruebaPuntos::probarDuplicados
        );

        ejecutarGrupo(
                "Canje real de beneficios",
                PruebaPuntos::probarCanjes
        );

        ejecutarGrupo(
                "Listado y utilizacion de beneficios",
                PruebaPuntos::probarGestionBeneficios
        );

        ejecutarGrupo(
                "Historial de movimientos de puntos",
                PruebaPuntos::probarHistorialPuntos
        );

        ejecutarGrupo(
                "Reversion de puntos",
                PruebaPuntos::probarReversionPuntos
        );

        ejecutarGrupo(
                "Proteccion de reversiones",
                PruebaPuntos::probarProteccionReversion
        );

        ejecutarGrupo(
                "Ajustes administrativos",
                PruebaPuntos::probarAjustesAdministrativos
        );

        ejecutarGrupo(
                "Estadisticas e integridad",
                PruebaPuntos::probarEstadisticasIntegridad
        );

        mostrarResumen();

        if (fallidas > 0 || gruposFallidos > 0) {

            throw new AssertionError(
                    "Se encontraron errores en PruebaPuntos"
            );
        }
    }

    // ==========================================
    // HERRAMIENTAS
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

        verificar(descripcion, correcto);

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

    private static Cliente nuevoCliente() {

        return new Cliente(
                "C001",
                "Juan",
                "juan@ejemplo.com"
        );
    }

    // ==========================================
    // 1. REGLAS DE PUNTOS
    // ==========================================

    private static void probarReglasPuntos() {

        verificarIgual(
                "Deposito: 1 punto por cada 100",
                1,
                TipoTransaccion.DEPOSITO
                        .getPuntosPorCada100()
        );

        verificarIgual(
                "Retiro: 2 puntos por cada 100",
                2,
                TipoTransaccion.RETIRO
                        .getPuntosPorCada100()
        );

        verificarIgual(
                "Transferencia: 3 puntos por cada 100",
                3,
                TipoTransaccion.TRANSFERENCIA
                        .getPuntosPorCada100()
        );

        verificarIgual(
                "Gasto: cero puntos",
                0,
                TipoTransaccion.GASTO
                        .getPuntosPorCada100()
        );

        verificar(
                "Deposito genera puntos",
                TipoTransaccion.DEPOSITO.generaPuntos()
        );

        verificar(
                "Retiro genera puntos",
                TipoTransaccion.RETIRO.generaPuntos()
        );

        verificar(
                "Transferencia genera puntos",
                TipoTransaccion.TRANSFERENCIA.generaPuntos()
        );

        verificar(
                "Gasto no genera puntos",
                !TipoTransaccion.GASTO.generaPuntos()
        );

        verificar(
                "Reversion no genera puntos",
                !TipoTransaccion.REVERSO.generaPuntos()
        );

        verificar(
                "Canje no genera puntos",
                !TipoTransaccion.CANJE_PUNTOS.generaPuntos()
        );
    }

    // ==========================================
    // 2. CALCULO DE PUNTOS
    // ==========================================

    private static void probarCalculoPuntos() {

        Transaccion deposito =
                Transaccion.crearDeposito(
                        "C001", "M001", dinero("10000")
                );

        verificarIgual(
                "Deposito de 10000 genera 100 puntos",
                100,
                deposito.calcularPuntosBase()
        );

        Transaccion retiro =
                Transaccion.crearRetiro(
                        "C001", "M001", dinero("1000")
                );

        verificarIgual(
                "Retiro de 1000 genera 20 puntos",
                20,
                retiro.calcularPuntosBase()
        );

        Transaccion transferencia =
                Transaccion.crearTransferencia(
                        "C001",
                        "C002",
                        "M001",
                        "M002",
                        dinero("5000"),
                        dinero("100")
                );

        verificarIgual(
                "Transferencia de 5000 genera 150 puntos",
                150,
                transferencia.calcularPuntosBase()
        );

        Transaccion pequenoDeposito =
                Transaccion.crearDeposito(
                        "C001", "M001", dinero("99.99")
                );

        verificarIgual(
                "Monto menor que 100 genera cero puntos",
                0,
                pequenoDeposito.calcularPuntosBase()
        );

        Transaccion exacto =
                Transaccion.crearDeposito(
                        "C001", "M001", dinero("100")
                );

        verificarIgual(
                "Deposito de 100 genera 1 punto",
                1,
                exacto.calcularPuntosBase()
        );

        Transaccion fraccionado =
                Transaccion.crearDeposito(
                        "C001", "M001", dinero("299.99")
                );

        verificarIgual(
                "Solo se cuentan bloques completos",
                2,
                fraccionado.calcularPuntosBase()
        );

        verificarIgual(
                "Transaccion inicia con cero puntos",
                0,
                deposito.getPuntosGenerados()
        );

        deposito.marcarCompletada();

        deposito.registrarPuntosGenerados(
                deposito.calcularPuntosBase()
        );

        verificarIgual(
                "Registrar puntos completados",
                100,
                deposito.getPuntosGenerados()
        );

        verificarExcepcion(
                "No otorgar puntos antes de completar",
                IllegalStateException.class,
                () -> retiro.registrarPuntosGenerados(20)
        );

        Transaccion gasto =
                Transaccion.crearGasto(
                        "C001",
                        "M001",
                        dinero("1000"),
                        "Compra"
                );

        verificarIgual(
                "Gasto genera cero puntos",
                0,
                gasto.calcularPuntosBase()
        );

        gasto.marcarCompletada();

        verificarExcepcion(
                "Gasto no puede otorgar puntos positivos",
                IllegalArgumentException.class,
                () -> gasto.registrarPuntosGenerados(10)
        );
    }

    // ==========================================
    // 3. ACUMULACION
    // ==========================================

    private static void probarAcumulacion() {

        Cliente cliente = nuevoCliente();

        verificarIgual(
                "Cliente inicia con cero puntos",
                0,
                cliente.getPuntos()
        );

        verificarIgual(
                "Rango inicial Bronce",
                Rango.BRONCE,
                cliente.getRango()
        );

        cliente.agregarPuntos(100);

        verificarIgual(
                "Acumular 100 puntos",
                100,
                cliente.getPuntos()
        );

        cliente.agregarPuntos(250);

        verificarIgual(
                "Acumular 350 puntos",
                350,
                cliente.getPuntos()
        );

        cliente.agregarPuntos(151);

        verificarIgual(
                "Acumular 501 puntos",
                501,
                cliente.getPuntos()
        );

        verificarIgual(
                "Ascenso a Plata",
                Rango.PLATA,
                cliente.getRango()
        );

        cliente.agregarPuntos(500);

        verificarIgual(
                "Acumular 1001 puntos",
                1001,
                cliente.getPuntos()
        );

        verificarIgual(
                "Ascenso a Oro",
                Rango.ORO,
                cliente.getRango()
        );

        cliente.agregarPuntos(4000);

        verificarIgual(
                "Acumular 5001 puntos",
                5001,
                cliente.getPuntos()
        );

        verificarIgual(
                "Ascenso a Platino",
                Rango.PLATINO,
                cliente.getRango()
        );
    }

    // ==========================================
    // 4. DESCUENTO DE PUNTOS
    // ==========================================

    private static void probarDescuentoPuntos() {

        Cliente cliente = nuevoCliente();

        cliente.agregarPuntos(1200);

        verificar(
                "Puede gastar 100 puntos",
                cliente.tienePuntosSuficientes(100)
        );

        verificar(
                "Puede gastar 1000 puntos",
                cliente.tienePuntosSuficientes(1000)
        );

        verificar(
                "No puede gastar 1500 puntos",
                !cliente.tienePuntosSuficientes(1500)
        );

        cliente.descontarPuntos(100);

        verificarIgual(
                "Descontar 100 puntos",
                1100,
                cliente.getPuntos()
        );

        cliente.descontarPuntos(500);

        verificarIgual(
                "Descontar 500 puntos",
                600,
                cliente.getPuntos()
        );

        verificarIgual(
                "Actualizar rango a Plata",
                Rango.PLATA,
                cliente.getRango()
        );

        verificarExcepcion(
                "Rechazar canje sin puntos",
                IllegalArgumentException.class,
                () -> cliente.descontarPuntos(1000)
        );

        verificarIgual(
                "Conservar puntos al rechazar",
                600,
                cliente.getPuntos()
        );

        cliente.descontarPuntos(600);

        verificarIgual(
                "Permitir gastar todos los puntos",
                0,
                cliente.getPuntos()
        );

        verificarIgual(
                "Regresar a Bronce",
                Rango.BRONCE,
                cliente.getRango()
        );
    }

    // ==========================================
    // 5. RANGOS
    // ==========================================

    private static void probarRangos() {

        verificarIgual(
                "0 corresponde a Bronce",
                Rango.BRONCE,
                Rango.calcularRango(0)
        );

        verificarIgual(
                "500 corresponde a Bronce",
                Rango.BRONCE,
                Rango.calcularRango(500)
        );

        verificarIgual(
                "501 corresponde a Plata",
                Rango.PLATA,
                Rango.calcularRango(501)
        );

        verificarIgual(
                "1000 corresponde a Plata",
                Rango.PLATA,
                Rango.calcularRango(1000)
        );

        verificarIgual(
                "1001 corresponde a Oro",
                Rango.ORO,
                Rango.calcularRango(1001)
        );

        verificarIgual(
                "5000 corresponde a Oro",
                Rango.ORO,
                Rango.calcularRango(5000)
        );

        verificarIgual(
                "5001 corresponde a Platino",
                Rango.PLATINO,
                Rango.calcularRango(5001)
        );

        verificarIgual(
                "Desde cero faltan 501 puntos",
                501,
                Rango.BRONCE.puntosParaSiguienteRango(0)
        );

        verificarIgual(
                "Con 500 falta 1 punto",
                1,
                Rango.BRONCE.puntosParaSiguienteRango(500)
        );

        verificarIgual(
                "Con 1000 falta 1 punto",
                1,
                Rango.PLATA.puntosParaSiguienteRango(1000)
        );

        verificarIgual(
                "Platino no tiene siguiente rango",
                0,
                Rango.PLATINO.puntosParaSiguienteRango(5001)
        );

        verificarIgual(
                "Siguiente de Bronce",
                Rango.PLATA,
                Rango.BRONCE.siguienteRango()
        );

        verificarIgual(
                "Siguiente de Plata",
                Rango.ORO,
                Rango.PLATA.siguienteRango()
        );

        verificarIgual(
                "Siguiente de Oro",
                Rango.PLATINO,
                Rango.ORO.siguienteRango()
        );

        verificarExcepcion(
                "Rechazar puntos negativos",
                IllegalArgumentException.class,
                () -> Rango.calcularRango(-1)
        );
    }

    // ==========================================
    // 6. CATALOGO DE BENEFICIOS
    // ==========================================

    private static void probarCatalogoBeneficios() {

        Beneficio descuento =
                Beneficio.crearDescuentoTransferencia(
                        "C001"
                );

        Beneficio retiros =
                Beneficio.crearRetirosSinComision(
                        "C001"
                );

        Beneficio bono =
                Beneficio.crearBonoSaldo(
                        "C001"
                );

        verificarIgual(
                "Descuento cuesta 100 puntos",
                100,
                descuento.getCostoPuntos()
        );

        verificarIgual(
                "Descuento de transferencia del 10%",
                10,
                descuento.getPorcentajeDescuento()
        );

        verificarIgual(
                "Retiros cuestan 500 puntos",
                500,
                retiros.getCostoPuntos()
        );

        verificarIgual(
                "Retiros tienen 100% de descuento",
                100,
                retiros.getPorcentajeDescuento()
        );

        verificarIgual(
                "Retiros duran 30 dias",
                30,
                retiros.getTipo().getDuracionDias()
        );

        verificarIgual(
                "Bono cuesta 1000 puntos",
                1000,
                bono.getCostoPuntos()
        );

        verificarMonto(
                "Bono acredita 50 unidades",
                "50",
                bono.getMontoBono()
        );

        verificarIgual(
                "Beneficio inicia disponible",
                Beneficio.Estado.DISPONIBLE,
                descuento.getEstado()
        );

        verificar(
                "Beneficio tiene ID",
                descuento.getId() != null
                        && !descuento.getId().isBlank()
        );

        verificarIgual(
                "Beneficio pertenece a C001",
                "C001",
                descuento.getIdCliente()
        );

        verificar(
                "Beneficios tienen IDs diferentes",
                !descuento.getId().equals(retiros.getId())
        );

        verificar(
                "100 puntos permiten descuento",
                descuento.puedeCanjear(100)
        );

        verificar(
                "99 puntos no permiten descuento",
                !descuento.puedeCanjear(99)
        );

        verificar(
                "1000 puntos permiten bono",
                bono.puedeCanjear(1000)
        );
    }

    // ==========================================
    // 7. DESCUENTOS
    // ==========================================

    private static void probarDescuentos() {

        Beneficio descuento =
                Beneficio.crearDescuentoTransferencia(
                        "C001"
                );

        descuento.activar();

        verificarMonto(
                "Comision 1000 queda en 900",
                "900",
                descuento.calcularComisionConDescuento(
                        dinero("1000")
                )
        );

        verificarMonto(
                "Comision 500 queda en 450",
                "450",
                descuento.calcularComisionConDescuento(
                        dinero("500")
                )
        );

        verificarMonto(
                "Comision cero permanece cero",
                "0",
                descuento.calcularComisionConDescuento(
                        dinero("0")
                )
        );

        verificarMonto(
                "Redondeo monetario correcto",
                "0.02",
                descuento.calcularComisionConDescuento(
                        dinero("0.02")
                )
        );

        verificarIgual(
                "Calcular no consume beneficio",
                0,
                descuento.getCantidadUsos()
        );

        Beneficio retiros =
                Beneficio.crearRetirosSinComision(
                        "C001"
                );

        retiros.activar();

        verificarMonto(
                "Retiro sin comision queda en cero",
                "0",
                retiros.calcularComisionConDescuento(
                        dinero("1500")
                )
        );

        Beneficio bono =
                Beneficio.crearBonoSaldo("C001");

        bono.activar();

        verificarExcepcion(
                "Bono no descuenta comisiones",
                IllegalStateException.class,
                () -> bono.calcularComisionConDescuento(
                        dinero("1000")
                )
        );
    }

    // ==========================================
    // 8. ESTADOS DE BENEFICIOS
    // ==========================================

    private static void probarEstadosBeneficios() {

        Beneficio descuento =
                Beneficio.crearDescuentoTransferencia(
                        "C001"
                );

        verificar(
                "Beneficio nuevo no puede usarse",
                !descuento.estaDisponibleParaUso()
        );

        verificarExcepcion(
                "Rechazar uso sin activar",
                IllegalStateException.class,
                descuento::utilizar
        );

        descuento.activar();

        verificarIgual(
                "Estado activo",
                Beneficio.Estado.ACTIVO,
                descuento.getEstado()
        );

        verificar(
                "Beneficio activo esta disponible",
                descuento.estaDisponibleParaUso()
        );

        descuento.utilizar();

        verificarIgual(
                "Registrar primer uso",
                1,
                descuento.getCantidadUsos()
        );

        verificarIgual(
                "Descuento queda utilizado",
                Beneficio.Estado.UTILIZADO,
                descuento.getEstado()
        );

        verificar(
                "Registrar fecha de uso",
                descuento.getFechaUtilizacion() != null
        );

        verificar(
                "Beneficio utilizado no disponible",
                !descuento.estaDisponibleParaUso()
        );

        verificarExcepcion(
                "Rechazar segundo uso",
                IllegalStateException.class,
                descuento::utilizar
        );

        Beneficio bono =
                Beneficio.crearBonoSaldo("C001");

        bono.activar();
        bono.utilizar();

        verificarIgual(
                "Bono utilizado una vez",
                Beneficio.Estado.UTILIZADO,
                bono.getEstado()
        );

        Beneficio cancelado =
                Beneficio.crearDescuentoTransferencia(
                        "C001"
                );

        cancelado.cancelar();

        verificarIgual(
                "Estado cancelado",
                Beneficio.Estado.CANCELADO,
                cancelado.getEstado()
        );

        verificarExcepcion(
                "No activar beneficio cancelado",
                IllegalStateException.class,
                cancelado::activar
        );
    }

    // ==========================================
    // 9. BENEFICIOS TEMPORALES
    // ==========================================

    private static void probarBeneficiosTemporales() {

        Beneficio retiros =
                Beneficio.crearRetirosSinComision(
                        "C001"
                );

        verificar(
                "Beneficio tiene vencimiento",
                retiros.tieneVencimiento()
        );

        verificarIgual(
                "Vence 30 dias despues del canje",
                retiros.getFechaCanje().plusDays(30),
                retiros.getFechaVencimiento()
        );

        verificar(
                "Fecha de vencimiento futura",
                retiros.getFechaVencimiento()
                        .isAfter(LocalDateTime.now())
        );

        retiros.activar();

        retiros.utilizar();
        retiros.utilizar();
        retiros.utilizar();

        verificarIgual(
                "Permitir tres usos",
                3,
                retiros.getCantidadUsos()
        );

        verificarIgual(
                "Beneficio temporal sigue activo",
                Beneficio.Estado.ACTIVO,
                retiros.getEstado()
        );

        verificar(
                "Disponible antes del vencimiento",
                retiros.estaDisponibleParaUso()
        );

        verificar(
                "No esta vencido",
                !retiros.estaVencido()
        );

        Beneficio descuento =
                Beneficio.crearDescuentoTransferencia(
                        "C001"
                );

        verificar(
                "Descuento no tiene vencimiento",
                !descuento.tieneVencimiento()
        );

        Beneficio bono =
                Beneficio.crearBonoSaldo("C001");

        verificar(
                "Bono no tiene vencimiento",
                !bono.tieneVencimiento()
        );
    }

    // ==========================================
    // 10. VALIDACIONES
    // ==========================================

    private static void probarValidaciones() {

        Cliente cliente = nuevoCliente();

        verificarExcepcion(
                "Rechazar acumulacion de cero",
                IllegalArgumentException.class,
                () -> cliente.agregarPuntos(0)
        );

        verificarExcepcion(
                "Rechazar acumulacion negativa",
                IllegalArgumentException.class,
                () -> cliente.agregarPuntos(-10)
        );

        verificarExcepcion(
                "Rechazar descuento de cero",
                IllegalArgumentException.class,
                () -> cliente.descontarPuntos(0)
        );

        verificarExcepcion(
                "Rechazar descuento negativo",
                IllegalArgumentException.class,
                () -> cliente.descontarPuntos(-10)
        );

        verificarExcepcion(
                "Rechazar descuento superior al saldo",
                IllegalArgumentException.class,
                () -> cliente.descontarPuntos(100)
        );

        verificarIgual(
                "Puntos no negativos",
                0,
                cliente.getPuntos()
        );

        verificarExcepcion(
                "Rechazar beneficio sin cliente",
                IllegalArgumentException.class,
                () -> new Beneficio(
                        "",
                        Beneficio.Tipo.BONO_SALDO
                )
        );

        verificarExcepcion(
                "Rechazar beneficio sin tipo",
                IllegalArgumentException.class,
                () -> new Beneficio("C001", null)
        );

        Beneficio descuento =
                Beneficio.crearDescuentoTransferencia(
                        "C001"
                );

        verificarExcepcion(
                "Rechazar comision negativa",
                IllegalArgumentException.class,
                () -> descuento.calcularComisionConDescuento(
                        dinero("-100")
                )
        );

        verificarExcepcion(
                "Rechazar descuento sin activar",
                IllegalStateException.class,
                () -> descuento.calcularComisionConDescuento(
                        dinero("100")
                )
        );

        Transaccion pendiente =
                Transaccion.crearDeposito(
                        "C001", "M001", dinero("1000")
                );

        verificarExcepcion(
                "No registrar puntos en transaccion pendiente",
                IllegalStateException.class,
                () -> pendiente.registrarPuntosGenerados(-1)
        );

        pendiente.marcarCompletada();

        verificarExcepcion(
                "Rechazar puntos negativos registrados",
                IllegalArgumentException.class,
                () -> pendiente.registrarPuntosGenerados(-1)
        );
    }

    // ==========================================
    // 11. INTEGRACION FINANCIERA BASICA
    // ==========================================

    private static void probarIntegracionFinanciera() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        verificar(
                "Deposito completado",
                deposito.estaCompletada()
        );

        verificarIgual(
                "Deposito genera 100 puntos base",
                100,
                deposito.calcularPuntosBase()
        );

        Transaccion retiro =
                c.transacciones.retirar(
                        "C001", "M001", dinero("1000")
                );

        verificarIgual(
                "Retiro genera 20 puntos base",
                20,
                retiro.calcularPuntosBase()
        );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001", "M001",
                        "C002", "M002",
                        dinero("2000")
                );

        verificarIgual(
                "Transferencia genera 60 puntos base",
                60,
                transferencia.calcularPuntosBase()
        );

        verificarIgual(
                "Puntos sin integracion automatica",
                0,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Destinatario sin puntos",
                0,
                c.ana.getPuntos()
        );

        verificarIgual(
                "Puntos de transaccion sin registrar",
                0,
                transferencia.getPuntosGenerados()
        );

        verificarMonto(
                "Saldo del origen",
                "7000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Saldo del destino",
                "2000",
                c.monederos.consultarSaldo(
                        "C002", "M002"
                )
        );

        verificar(
                "Integridad de clientes",
                c.clientes.verificarIntegridad()
        );

        verificar(
                "Integridad de monederos",
                c.monederos.verificarIntegridad()
        );

        verificar(
                "Integridad financiera",
                c.transacciones.verificarIntegridad()
        );

        verificarIgual(
                "Tres operaciones registradas",
                3,
                c.transacciones.cantidadTransacciones()
        );
    }

    // ==========================================
    // 12. ASIGNACION REAL DE PUNTOS
    // ==========================================

    private static void probarAsignacionReal() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        int ganadosDeposito =
                c.puntos.registrarPuntosPorTransaccion(
                        deposito
                );

        verificarIgual(
                "Deposito otorga 100 puntos",
                100,
                ganadosDeposito
        );

        verificarIgual(
                "Puntos registrados en cliente",
                100,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Puntos registrados en transaccion",
                100,
                deposito.getPuntosGenerados()
        );

        Transaccion retiro =
                c.transacciones.retirar(
                        "C001", "M001", dinero("1000")
                );

        verificarIgual(
                "Retiro otorga 20 puntos",
                20,
                c.puntos.registrarPuntosPorTransaccion(
                        retiro
                )
        );

        verificarIgual(
                "Cliente acumula 120 puntos",
                120,
                c.puntos.consultarPuntos("C001")
        );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001", "M001",
                        "C002", "M002",
                        dinero("2000")
                );

        verificarIgual(
                "Transferencia otorga 60 puntos",
                60,
                c.puntos.registrarPuntosPorTransaccion(
                        transferencia
                )
        );

        verificarIgual(
                "Cliente emisor acumula 180 puntos",
                180,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Destinatario no recibe puntos",
                0,
                c.puntos.consultarPuntos("C002")
        );

        verificarIgual(
                "Rango del cliente sigue Bronce",
                Rango.BRONCE,
                c.puntos.consultarRango("C001")
        );

        verificarIgual(
                "Tres operaciones procesadas",
                3,
                c.puntos.cantidadTransaccionesProcesadas()
        );

        verificarIgual(
                "Tres movimientos de acumulacion",
                3,
                c.puntos.cantidadMovimientosPuntos()
        );

        verificar(
                "Integridad de puntos correcta",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 13. PREVENCION DE DUPLICADOS
    // ==========================================

    private static void probarDuplicados() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("5000")
                );

        c.puntos.registrarPuntosPorTransaccion(
                deposito
        );

        verificar(
                "Transaccion marcada como procesada",
                c.puntos.transaccionProcesada(
                        deposito.getId()
                )
        );

        verificarIgual(
                "Puntos de transaccion almacenados",
                50,
                c.puntos.consultarPuntosTransaccion(
                        deposito.getId()
                )
        );

        verificarExcepcion(
                "Rechazar procesamiento duplicado",
                IllegalStateException.class,
                () -> c.puntos.registrarPuntosPorTransaccion(
                        deposito
                )
        );

        verificarIgual(
                "No duplicar puntos del cliente",
                50,
                c.juan.getPuntos()
        );

        verificarIgual(
                "No duplicar movimientos",
                1,
                c.puntos.cantidadMovimientosPuntos()
        );

        verificarIgual(
                "No duplicar transacciones procesadas",
                1,
                c.puntos.cantidadTransaccionesProcesadas()
        );

        Transaccion pendiente =
                Transaccion.crearDeposito(
                        "C001", "M001", dinero("1000")
                );

        verificarExcepcion(
                "Rechazar transaccion pendiente",
                IllegalStateException.class,
                () -> c.puntos.registrarPuntosPorTransaccion(
                        pendiente
                )
        );

        verificar(
                "Pendiente no queda procesada",
                !c.puntos.transaccionProcesada(
                        pendiente.getId()
                )
        );

        verificar(
                "Integridad despues de duplicados",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 14. CANJE REAL DE BENEFICIOS
    // ==========================================

    private static void probarCanjes() {

        Contexto c = new Contexto();

        c.puntos.agregarPuntosManuales(
                "C001",
                2000,
                "Saldo de prueba"
        );

        verificarIgual(
                "Cliente inicia con 2000 puntos",
                2000,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Rango inicial Oro",
                Rango.ORO,
                c.puntos.consultarRango("C001")
        );

        Beneficio descuento =
                c.puntos.canjearDescuentoTransferencia(
                        "C001"
                );

        verificarIgual(
                "Descuento cuesta 100 puntos",
                1900,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Descuento queda activo",
                Beneficio.Estado.ACTIVO,
                descuento.getEstado()
        );

        Beneficio retiros =
                c.puntos.canjearRetirosSinComision(
                        "C001"
                );

        verificarIgual(
                "Retiros descuentan 500 puntos",
                1400,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Retiros quedan activos",
                Beneficio.Estado.ACTIVO,
                retiros.getEstado()
        );

        Beneficio bono =
                c.puntos.canjearBonoSaldo("C001");

        verificarIgual(
                "Bono descuenta 1000 puntos",
                400,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Rango baja a Bronce",
                Rango.BRONCE,
                c.puntos.consultarRango("C001")
        );

        verificarIgual(
                "Tres beneficios canjeados",
                3,
                c.puntos.cantidadBeneficiosCanjeados()
        );

        verificar(
                "Buscar beneficio mediante tabla hash",
                c.puntos.buscarBeneficio(
                        bono.getId()
                ) == bono
        );

        verificarMonto(
                "Canjear bono aun no acredita dinero",
                "0",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificar(
                "Integridad despues de canjes",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 15. GESTION DE BENEFICIOS
    // ==========================================

    private static void probarGestionBeneficios() {

        Contexto c = new Contexto();

        c.puntos.agregarPuntosManuales(
                "C001", 1000, "Prueba"
        );

        Beneficio descuento =
                c.puntos.canjearDescuentoTransferencia(
                        "C001"
                );

        Beneficio retiros =
                c.puntos.canjearRetirosSinComision(
                        "C001"
                );

        verificarIgual(
                "Dos beneficios registrados",
                2,
                c.puntos.listarBeneficios(
                        "C001"
                ).tamanio()
        );

        verificarIgual(
                "Dos beneficios inicialmente activos",
                2,
                c.puntos.listarBeneficiosActivos(
                        "C001"
                ).tamanio()
        );

        verificar(
                "Buscar descuento activo",
                c.puntos.buscarBeneficioActivo(
                        "C001",
                        Beneficio.Tipo.DESCUENTO_TRANSFERENCIA
                ) == descuento
        );

        verificar(
                "Buscar retiros activos",
                c.puntos.buscarBeneficioActivo(
                        "C001",
                        Beneficio.Tipo.RETIROS_SIN_COMISION
                ) == retiros
        );

        c.puntos.registrarUsoBeneficio(
                "C001",
                descuento.getId()
        );

        verificarIgual(
                "Descuento consumido",
                Beneficio.Estado.UTILIZADO,
                descuento.getEstado()
        );

        verificarIgual(
                "Queda un beneficio activo",
                1,
                c.puntos.listarBeneficiosActivos(
                        "C001"
                ).tamanio()
        );

        c.puntos.registrarUsoBeneficio(
                "C001",
                retiros.getId()
        );

        c.puntos.registrarUsoBeneficio(
                "C001",
                retiros.getId()
        );

        verificarIgual(
                "Retiros permiten varios usos",
                2,
                retiros.getCantidadUsos()
        );

        verificarIgual(
                "Retiros siguen activos",
                Beneficio.Estado.ACTIVO,
                retiros.getEstado()
        );

        verificarExcepcion(
                "Otro cliente no puede consumir beneficio",
                IllegalArgumentException.class,
                () -> c.puntos.registrarUsoBeneficio(
                        "C002",
                        retiros.getId()
                )
        );

        verificar(
                "Integridad de beneficios correcta",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 16. HISTORIAL DE MOVIMIENTOS
    // ==========================================

    private static void probarHistorialPuntos() {

        Contexto c = new Contexto();

        c.puntos.agregarPuntosManuales(
                "C001", 1000, "Bono inicial"
        );

        c.puntos.canjearDescuentoTransferencia(
                "C001"
        );

        c.puntos.descontarPuntosManuales(
                "C001", 50, "Correccion"
        );

        verificarIgual(
                "Tres movimientos registrados",
                3,
                c.puntos.cantidadMovimientosPuntos()
        );

        ListaEnlazada<PuntosService.MovimientoPuntos>
                historial =
                c.puntos.obtenerHistorialCliente("C001");

        verificarIgual(
                "Tres movimientos del cliente",
                3,
                historial.tamanio()
        );

        verificarIgual(
                "Primer movimiento administrativo",
                PuntosService.TipoMovimiento.AJUSTE,
                historial.obtenerPrimero().getTipo()
        );

        verificarIgual(
                "Segundo movimiento es canje",
                PuntosService.TipoMovimiento.CANJE,
                historial.obtener(1).getTipo()
        );

        verificarIgual(
                "Ultimo movimiento administrativo",
                PuntosService.TipoMovimiento.AJUSTE,
                historial.obtenerUltimo().getTipo()
        );

        verificarIgual(
                "Saldo final del historial",
                850,
                historial.obtenerUltimo().getSaldoPosterior()
        );

        verificarIgual(
                "Cliente conserva 850 puntos",
                850,
                c.puntos.consultarPuntos("C001")
        );

        verificarIgual(
                "Otro cliente no tiene movimientos",
                0,
                c.puntos.obtenerHistorialCliente(
                        "C002"
                ).tamanio()
        );

        verificar(
                "Integridad del historial correcta",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 17. REVERSION DE PUNTOS
    // ==========================================

    private static void probarReversionPuntos() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        c.puntos.registrarPuntosPorTransaccion(
                deposito
        );

        verificarIgual(
                "Cliente obtiene 100 puntos",
                100,
                c.juan.getPuntos()
        );

        verificar(
                "Puntos pueden revertirse",
                c.puntos.puedeRevertirPuntos(
                        deposito
                )
        );

        // Primero revertimos el dinero.
        Transaccion reversion =
                c.transacciones.revertirUltimaTransaccion();

        verificar(
                "Reversion monetaria completada",
                reversion.estaCompletada()
        );

        // Despues compensamos los puntos.
        int compensados =
                c.puntos.revertirPuntosDeTransaccion(
                        deposito
                );

        verificarIgual(
                "Reversion descuenta 100 puntos",
                100,
                compensados
        );

        verificarIgual(
                "Puntos vuelven a cero",
                0,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Puntos netos de transaccion son cero",
                0,
                c.puntos.consultarPuntosTransaccion(
                        deposito.getId()
                )
        );

        verificar(
                "Transaccion permanece procesada",
                c.puntos.transaccionProcesada(
                        deposito.getId()
                )
        );

        verificarExcepcion(
                "Impedir otorgar puntos otra vez",
                IllegalStateException.class,
                () -> c.puntos.registrarPuntosPorTransaccion(
                        deposito
                )
        );

        verificarIgual(
                "Historial registra acumulacion y reversion",
                2,
                c.puntos.cantidadMovimientosPuntos()
        );

        verificar(
                "Integridad despues de reversion",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 18. PROTECCION DE REVERSIONES
    // ==========================================

    private static void probarProteccionReversion() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("10000")
                );

        c.puntos.registrarPuntosPorTransaccion(
                deposito
        );

        c.puntos.descontarPuntosManuales(
                "C001",
                80,
                "Consumo de prueba"
        );

        verificarIgual(
                "Cliente conserva solo 20 puntos",
                20,
                c.juan.getPuntos()
        );

        verificar(
                "No puede compensar 100 puntos",
                !c.puntos.puedeRevertirPuntos(
                        deposito
                )
        );

        verificarExcepcion(
                "Rechazar compensacion sin puntos",
                IllegalStateException.class,
                () -> c.puntos.revertirPuntosDeTransaccion(
                        deposito
                )
        );

        verificarIgual(
                "Puntos intactos tras rechazo",
                20,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Registro conserva puntos originales",
                100,
                c.puntos.consultarPuntosTransaccion(
                        deposito.getId()
                )
        );

        verificar(
                "Integridad despues del rechazo",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 19. AJUSTES ADMINISTRATIVOS
    // ==========================================

    private static void probarAjustesAdministrativos() {

        Contexto c = new Contexto();

        c.puntos.agregarPuntosManuales(
                "C001",
                1500,
                "Reconocimiento"
        );

        verificarIgual(
                "Agregar 1500 puntos",
                1500,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Rango cambia a Oro",
                Rango.ORO,
                c.juan.getRango()
        );

        c.puntos.descontarPuntosManuales(
                "C001",
                600,
                "Correccion"
        );

        verificarIgual(
                "Descontar 600 puntos",
                900,
                c.juan.getPuntos()
        );

        verificarIgual(
                "Rango cambia a Plata",
                Rango.PLATA,
                c.juan.getRango()
        );

        verificarExcepcion(
                "Rechazar ajuste negativo",
                IllegalArgumentException.class,
                () -> c.puntos.agregarPuntosManuales(
                        "C001", -5, "Invalido"
                )
        );

        verificarExcepcion(
                "Rechazar motivo vacio",
                IllegalArgumentException.class,
                () -> c.puntos.agregarPuntosManuales(
                        "C001", 10, ""
                )
        );

        verificarExcepcion(
                "Rechazar descuento excesivo",
                IllegalArgumentException.class,
                () -> c.puntos.descontarPuntosManuales(
                        "C001", 2000, "Invalido"
                )
        );

        verificarIgual(
                "Solo dos movimientos validos",
                2,
                c.puntos.cantidadMovimientosPuntos()
        );

        verificar(
                "Integridad de ajustes correcta",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // 20. ESTADISTICAS E INTEGRIDAD
    // ==========================================

    private static void probarEstadisticasIntegridad() {

        Contexto c = new Contexto();

        c.puntos.agregarPuntosManuales(
                "C001", 1200, "Inicial"
        );

        c.puntos.agregarPuntosManuales(
                "C002", 600, "Inicial"
        );

        c.puntos.agregarPuntosManuales(
                "C003", 100, "Inicial"
        );

        verificarIgual(
                "Total inicial de puntos",
                1900L,
                c.puntos.totalPuntosSistema()
        );

        verificarIgual(
                "Un cliente en Oro",
                1,
                c.puntos.cantidadClientesPorRango(
                        Rango.ORO
                )
        );

        verificarIgual(
                "Un cliente en Plata",
                1,
                c.puntos.cantidadClientesPorRango(
                        Rango.PLATA
                )
        );

        verificarIgual(
                "Un cliente en Bronce",
                1,
                c.puntos.cantidadClientesPorRango(
                        Rango.BRONCE
                )
        );

        c.puntos.canjearDescuentoTransferencia(
                "C001"
        );

        verificarIgual(
                "Puntos totales despues de canje",
                1800L,
                c.puntos.totalPuntosSistema()
        );

        verificarIgual(
                "Un beneficio registrado",
                1,
                c.puntos.cantidadBeneficiosCanjeados()
        );

        verificarIgual(
                "Cuatro movimientos de puntos",
                4,
                c.puntos.cantidadMovimientosPuntos()
        );

        verificar(
                "Integridad de clientes",
                c.clientes.verificarIntegridad()
        );

        verificar(
                "Integridad de monederos",
                c.monederos.verificarIntegridad()
        );

        verificar(
                "Integridad financiera",
                c.transacciones.verificarIntegridad()
        );

        verificar(
                "Integridad de puntos",
                c.puntos.verificarIntegridad()
        );
    }

    // ==========================================
    // RESUMEN GENERAL
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
                "Grupos aprobados: " + gruposAprobados
        );

        System.out.println(
                "Grupos fallidos: " + gruposFallidos
        );

        System.out.println(
                "Verificaciones aprobadas: " + aprobadas
        );

        System.out.println(
                "Verificaciones fallidas: " + fallidas
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
