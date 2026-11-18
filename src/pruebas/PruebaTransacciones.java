
package pruebas;

import estructuras.GrafoDirigido;
import estructuras.ListaEnlazada;

import modelo.Cliente;
import modelo.Monedero;
import modelo.TipoTransaccion;
import modelo.Transaccion;

import servicios.ClienteService;
import servicios.MonederoService;
import servicios.TransaccionService;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Pruebas de integracion del motor financiero.
 *
 * Verifica:
 * - Registro de clientes y monederos.
 * - Depositos.
 * - Retiros.
 * - Transferencias.
 * - Comisiones.
 * - Gastos.
 * - Historial.
 * - Reversiones LIFO.
 * - Grafos de transferencias.
 * - Validaciones e integridad.
 *
 * No utiliza JUnit ni librerias externas.
 */
public class PruebaTransacciones {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    private static int gruposAprobados = 0;
    private static int gruposFallidos = 0;

    // ==========================================
    // CONTEXTO DE PRUEBAS
    // ==========================================

    /**
     * Cada grupo crea su propio entorno.
     *
     * Esto evita que una prueba modifique
     * los datos utilizados por otra.
     */
    private static class Contexto {

        final ClienteService clientes;
        final MonederoService monederos;
        final TransaccionService transacciones;

        Contexto() {

            clientes = new ClienteService();

            monederos =
                    new MonederoService(clientes);

            transacciones =
                    new TransaccionService(
                            clientes,
                            monederos
                    );

            clientes.registrarCliente(
                    "C001",
                    "Juan",
                    "juan@ejemplo.com"
            );

            clientes.registrarCliente(
                    "C002",
                    "Ana",
                    "ana@ejemplo.com"
            );

            clientes.registrarCliente(
                    "C003",
                    "Luis",
                    "luis@ejemplo.com"
            );

            monederos.crearMonederoPrincipal(
                    "C001", "M001"
            );

            monederos.crearMonederoPrincipal(
                    "C002", "M002"
            );

            monederos.crearMonederoPrincipal(
                    "C003", "M003"
            );

            monederos.crearMonederoAhorros(
                    "C001", "M004"
            );
        }
    }

    // ==========================================
    // METODO PRINCIPAL
    // ==========================================

    public static void main(String[] args) {

        System.out.println(
                "======================================"
        );

        System.out.println(
                " MONEDERO VIRTUAL"
        );

        System.out.println(
                " PRUEBAS DE TRANSACCIONES"
        );

        System.out.println(
                "======================================"
        );

        ejecutarGrupo(
                "Clientes y monederos",
                PruebaTransacciones::probarClientes
        );

        ejecutarGrupo(
                "Depositos",
                PruebaTransacciones::probarDepositos
        );

        ejecutarGrupo(
                "Retiros",
                PruebaTransacciones::probarRetiros
        );

        ejecutarGrupo(
                "Transferencias",
                PruebaTransacciones::probarTransferencias
        );

        ejecutarGrupo(
                "Gastos",
                PruebaTransacciones::probarGastos
        );

        ejecutarGrupo(
                "Historial",
                PruebaTransacciones::probarHistorial
        );

        ejecutarGrupo(
                "Reversion de deposito",
                PruebaTransacciones::probarReversionDeposito
        );

        ejecutarGrupo(
                "Reversion de retiro",
                PruebaTransacciones::probarReversionRetiro
        );

        ejecutarGrupo(
                "Reversion de transferencia",
                PruebaTransacciones::probarReversionTransferencia
        );

        ejecutarGrupo(
                "Orden LIFO",
                PruebaTransacciones::probarLIFO
        );

        ejecutarGrupo(
                "Grafo de transferencias",
                PruebaTransacciones::probarGrafo
        );

        ejecutarGrupo(
                "Operaciones invalidas",
                PruebaTransacciones::probarOperacionesInvalidas
        );

        ejecutarGrupo(
                "Integridad financiera",
                PruebaTransacciones::probarIntegridad
        );

        mostrarResumen();

        if (fallidas > 0 || gruposFallidos > 0) {

            throw new AssertionError(
                    "Se encontraron errores en las pruebas"
            );
        }
    }

    // ==========================================
    // HERRAMIENTAS DE VERIFICACION
    // ==========================================

    private static void ejecutarGrupo(
            String nombre,
            Runnable prueba) {

        System.out.println();

        System.out.println(
                "--------------------------------------"
        );

        System.out.println(
                "GRUPO: " + nombre
        );

        System.out.println(
                "--------------------------------------"
        );

        int fallidasAntes = fallidas;

        try {

            prueba.run();

            if (fallidas == fallidasAntes) {

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

    /**
     * Compara montos monetarios ignorando
     * diferencias de escala decimal.
     *
     * Ejemplo:
     * 1000 y 1000.00 son equivalentes.
     */
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

    private static BigDecimal dinero(String monto) {
        return new BigDecimal(monto);
    }

    // ==========================================
    // 1. CLIENTES Y MONEDEROS
    // ==========================================

    private static void probarClientes() {

        Contexto c = new Contexto();

        verificarIgual(
                "Tres clientes registrados",
                3,
                c.clientes.cantidadClientes()
        );

        verificarIgual(
                "Cuatro monederos registrados",
                4,
                c.monederos.cantidadMonederos()
        );

        Cliente cliente =
                c.clientes.obtenerCliente("C001");

        verificarIgual(
                "Buscar cliente por ID",
                "Juan",
                cliente.getNombre()
        );

        verificarIgual(
                "Cliente con dos monederos",
                2,
                cliente.getCantidadMonederos()
        );

        verificar(
                "Cliente activo",
                c.clientes.clienteActivo("C001")
        );

        verificarMonto(
                "Saldo inicial principal",
                "0",
                c.monederos.consultarSaldo(
                        "C001", "M001"
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
    }

    // ==========================================
    // 2. DEPOSITOS
    // ==========================================

    private static void probarDepositos() {

        Contexto c = new Contexto();

        Transaccion t1 = c.transacciones.depositar(
                "C001",
                "M001",
                dinero("100000")
        );

        verificarMonto(
                "Saldo despues del deposito",
                "100000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarIgual(
                "Tipo deposito",
                TipoTransaccion.DEPOSITO,
                t1.getTipo()
        );

        verificarIgual(
                "Estado completado",
                Transaccion.Estado.COMPLETADA,
                t1.getEstado()
        );

        verificar(
                "ID generado",
                t1.getId() != null
                        && !t1.getId().isBlank()
        );

        verificarIgual(
                "Un movimiento registrado",
                1,
                c.transacciones.cantidadTransacciones()
        );

        verificarIgual(
                "Deposito reversible",
                1,
                c.transacciones.cantidadReversibles()
        );

        c.transacciones.depositar(
                "C001",
                "M001",
                dinero("25000")
        );

        verificarMonto(
                "Depositos acumulados",
                "125000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Total de depositos",
                "125000",
                c.transacciones.totalPorTipo(
                        TipoTransaccion.DEPOSITO
                )
        );

        verificar(
                "Integridad despues de depositar",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 3. RETIROS
    // ==========================================

    private static void probarRetiros() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("80000")
        );

        Transaccion retiro =
                c.transacciones.retirar(
                        "C001",
                        "M001",
                        dinero("30000")
                );

        verificarMonto(
                "Saldo despues del retiro",
                "50000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarIgual(
                "Tipo retiro",
                TipoTransaccion.RETIRO,
                retiro.getTipo()
        );

        verificar(
                "Retiro completado",
                retiro.estaCompletada()
        );

        verificarMonto(
                "Total retirado",
                "30000",
                c.transacciones.totalPorTipo(
                        TipoTransaccion.RETIRO
                )
        );

        verificarExcepcion(
                "Rechazar retiro sin fondos",
                IllegalArgumentException.class,
                () -> c.transacciones.retirar(
                        "C001",
                        "M001",
                        dinero("90000")
                )
        );

        verificarMonto(
                "Saldo intacto despues del rechazo",
                "50000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarIgual(
                "No registrar retiro rechazado",
                2,
                c.transacciones.cantidadTransacciones()
        );

        verificar(
                "Integridad del historial",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 4. TRANSFERENCIAS
    // ==========================================

    private static void probarTransferencias() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("100000")
        );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("50000"),
                        dinero("1000")
                );

        verificarMonto(
                "Saldo del origen",
                "49000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Saldo del destino",
                "50000",
                c.monederos.consultarSaldo(
                        "C002", "M002"
                )
        );

        verificarMonto(
                "Costo total de la operacion",
                "51000",
                transferencia.calcularCostoTotal()
        );

        verificarMonto(
                "Comision registrada",
                "1000",
                c.transacciones.consultarTotalComisiones()
        );

        verificarIgual(
                "Tipo transferencia",
                TipoTransaccion.TRANSFERENCIA,
                transferencia.getTipo()
        );

        verificar(
                "Transferencia completada",
                transferencia.estaCompletada()
        );

        verificarIgual(
                "Un registro por transferencia",
                2,
                c.transacciones.cantidadTransacciones()
        );

        verificar(
                "Integridad despues de transferir",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 5. GASTOS
    // ==========================================

    private static void probarGastos() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("60000")
        );

        Transaccion gasto =
                c.transacciones.registrarGasto(
                        "C001",
                        "M001",
                        dinero("15000"),
                        "Compra de alimentos"
                );

        verificarMonto(
                "Saldo despues del gasto",
                "45000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarIgual(
                "Tipo de gasto",
                TipoTransaccion.GASTO,
                gasto.getTipo()
        );

        verificarIgual(
                "Descripcion del gasto",
                "Compra de alimentos",
                gasto.getDescripcion()
        );

        verificarIgual(
                "Gasto no entra en pila reversible",
                1,
                c.transacciones.cantidadReversibles()
        );

        verificarMonto(
                "Total de gastos",
                "15000",
                c.transacciones.totalPorTipo(
                        TipoTransaccion.GASTO
                )
        );

        verificar(
                "Integridad despues del gasto",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 6. HISTORIAL
    // ==========================================

    private static void probarHistorial() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001", "M001", dinero("100000")
                );

        Transaccion retiro =
                c.transacciones.retirar(
                        "C001", "M001", dinero("10000")
                );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("20000")
                );

        ListaEnlazada<Transaccion> historial =
                c.transacciones.obtenerHistorial();

        verificarIgual(
                "Tres transacciones registradas",
                3,
                historial.tamanio()
        );

        verificarIgual(
                "Primer movimiento es deposito",
                deposito.getId(),
                historial.obtenerPrimero().getId()
        );

        verificarIgual(
                "Ultimo movimiento es transferencia",
                transferencia.getId(),
                historial.obtenerUltimo().getId()
        );

        verificar(
                "Buscar transaccion mediante hash",
                c.transacciones.buscarTransaccion(
                        retiro.getId()
                ) == retiro
        );

        verificarIgual(
                "Historial del cliente origen",
                3,
                c.transacciones.obtenerHistorialCliente(
                        "C001"
                ).tamanio()
        );

        verificarIgual(
                "Historial del destinatario",
                1,
                c.transacciones.obtenerHistorialCliente(
                        "C002"
                ).tamanio()
        );

        ListaEnlazada<Transaccion> ultimas =
                c.transacciones.obtenerUltimasTransacciones(2);

        verificarIgual(
                "Consultar dos movimientos recientes",
                2,
                ultimas.tamanio()
        );

        verificarIgual(
                "Movimiento mas reciente primero",
                transferencia.getId(),
                ultimas.obtenerPrimero().getId()
        );

        verificarIgual(
                "Filtrar transferencias",
                1,
                c.transacciones.obtenerPorTipo(
                        TipoTransaccion.TRANSFERENCIA
                ).tamanio()
        );

        verificar(
                "Integridad del indice e historial",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 7. REVERSION DE DEPOSITO
    // ==========================================

    private static void probarReversionDeposito() {

        Contexto c = new Contexto();

        Transaccion deposito =
                c.transacciones.depositar(
                        "C001",
                        "M001",
                        dinero("50000")
                );

        Transaccion reversion =
                c.transacciones.revertirUltimaTransaccion();

        verificarMonto(
                "Saldo restaurado a cero",
                "0",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificar(
                "Deposito marcado como revertido",
                deposito.estaRevertida()
        );

        verificarIgual(
                "Registro de tipo REVERSO",
                TipoTransaccion.REVERSO,
                reversion.getTipo()
        );

        verificarIgual(
                "Referencia a transaccion original",
                deposito.getId(),
                reversion.getIdTransaccionOriginal()
        );

        verificarIgual(
                "Dos registros en historial",
                2,
                c.transacciones.cantidadTransacciones()
        );

        verificarIgual(
                "Pila sin operaciones pendientes",
                0,
                c.transacciones.cantidadReversibles()
        );

        verificar(
                "Integridad despues del reverso",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 8. REVERSION DE RETIRO
    // ==========================================

    private static void probarReversionRetiro() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("100000")
        );

        Transaccion retiro =
                c.transacciones.retirar(
                        "C001", "M001", dinero("30000")
                );

        verificarMonto(
                "Saldo antes de revertir",
                "70000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        c.transacciones.revertirUltimaTransaccion();

        verificarMonto(
                "Dinero devuelto",
                "100000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificar(
                "Retiro marcado como revertido",
                retiro.estaRevertida()
        );

        verificarIgual(
                "Deposito sigue disponible para reversion",
                1,
                c.transacciones.cantidadReversibles()
        );

        verificar(
                "Integridad despues de revertir retiro",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 9. REVERSION DE TRANSFERENCIA
    // ==========================================

    private static void probarReversionTransferencia() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("100000")
        );

        Transaccion transferencia =
                c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("40000"),
                        dinero("1000")
                );

        verificarMonto(
                "Saldo origen antes de reversion",
                "59000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        c.transacciones.revertirUltimaTransaccion();

        verificarMonto(
                "Origen recupera monto y comision",
                "100000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Destino devuelve el dinero",
                "0",
                c.monederos.consultarSaldo(
                        "C002", "M002"
                )
        );

        verificarMonto(
                "Comision neta restaurada",
                "0",
                c.transacciones.consultarTotalComisiones()
        );

        verificar(
                "Transferencia marcada como revertida",
                transferencia.estaRevertida()
        );

        verificarMonto(
                "Grafo excluye transferencia revertida",
                "0",
                c.transacciones.totalTransferidoEntre(
                        "C001", "C002"
                )
        );

        verificar(
                "Integridad despues de reversion",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 10. ORDEN LIFO
    // ==========================================

    private static void probarLIFO() {

        Contexto c = new Contexto();

        Transaccion primera =
                c.transacciones.depositar(
                        "C001", "M001", dinero("50000")
                );

        Transaccion segunda =
                c.transacciones.depositar(
                        "C001", "M001", dinero("20000")
                );

        verificarIgual(
                "Ultima operacion en cima",
                segunda.getId(),
                c.transacciones
                        .consultarUltimaReversible()
                        .getId()
        );

        c.transacciones.revertirUltimaTransaccion();

        verificarMonto(
                "Primera reversion descuenta 20000",
                "50000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificar(
                "Segunda transaccion revertida",
                segunda.estaRevertida()
        );

        verificarIgual(
                "Primera transaccion ahora en cima",
                primera.getId(),
                c.transacciones
                        .consultarUltimaReversible()
                        .getId()
        );

        c.transacciones.revertirUltimaTransaccion();

        verificarMonto(
                "Segunda reversion deja saldo cero",
                "0",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificar(
                "Pila completamente vacia",
                c.transacciones.cantidadReversibles() == 0
        );

        verificarExcepcion(
                "Rechazar reversion sin operaciones",
                IllegalStateException.class,
                c.transacciones::revertirUltimaTransaccion
        );

        verificar(
                "Integridad despues de dos reversiones",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 11. GRAFO DE TRANSFERENCIAS
    // ==========================================

    private static void probarGrafo() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("200000")
        );

        c.transacciones.transferir(
                "C001", "M001",
                "C002", "M002",
                dinero("30000")
        );

        c.transacciones.transferir(
                "C001", "M001",
                "C002", "M002",
                dinero("20000")
        );

        c.transacciones.transferir(
                "C002", "M002",
                "C003", "M003",
                dinero("10000")
        );

        GrafoDirigido grafo =
                c.transacciones.obtenerGrafoTransferencias();

        verificarIgual(
                "Tres clientes como vertices",
                3,
                grafo.cantidadVertices()
        );

        verificarIgual(
                "Dos conexiones dirigidas",
                2,
                grafo.cantidadAristas()
        );

        verificarMonto(
                "Acumulado C001 hacia C002",
                "50000",
                grafo.obtenerPeso("C001", "C002")
        );

        verificarIgual(
                "Dos transferencias hacia C002",
                2,
                grafo.obtenerCantidadOperaciones(
                        "C001", "C002"
                )
        );

        verificar(
                "Existe camino C001 hacia C003",
                grafo.existeCamino("C001", "C003")
        );

        verificar(
                "No existe camino inverso",
                !grafo.existeCamino("C003", "C001")
        );

        verificar(
                "Integridad del grafo",
                grafo.verificarIntegridad()
        );

        verificar(
                "Integridad del historial financiero",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 12. OPERACIONES INVALIDAS
    // ==========================================

    private static void probarOperacionesInvalidas() {

        Contexto c = new Contexto();

        verificarExcepcion(
                "Rechazar deposito negativo",
                IllegalArgumentException.class,
                () -> c.transacciones.depositar(
                        "C001", "M001", dinero("-100")
                )
        );

        verificarExcepcion(
                "Rechazar deposito de cero",
                IllegalArgumentException.class,
                () -> c.transacciones.depositar(
                        "C001", "M001", dinero("0")
                )
        );

        verificarExcepcion(
                "Rechazar monto con decimales invalidos",
                IllegalArgumentException.class,
                () -> c.transacciones.depositar(
                        "C001", "M001", dinero("10.123")
                )
        );

        verificarExcepcion(
                "Rechazar cliente inexistente",
                IllegalArgumentException.class,
                () -> c.transacciones.depositar(
                        "C999", "M001", dinero("1000")
                )
        );

        verificarExcepcion(
                "Rechazar retiro sin fondos",
                IllegalArgumentException.class,
                () -> c.transacciones.retirar(
                        "C001", "M001", dinero("1000")
                )
        );

        c.monederos.bloquearMonedero(
                "C001", "M001"
        );

        verificarExcepcion(
                "Rechazar deposito a monedero bloqueado",
                IllegalStateException.class,
                () -> c.transacciones.depositar(
                        "C001", "M001", dinero("1000")
                )
        );

        c.monederos.desbloquearMonedero(
                "C001", "M001"
        );

        c.clientes.desactivarCliente("C001");

        verificarExcepcion(
                "Rechazar cliente inactivo",
                IllegalStateException.class,
                () -> c.transacciones.depositar(
                        "C001", "M001", dinero("1000")
                )
        );

        c.clientes.activarCliente("C001");

        c.transacciones.depositar(
                "C001", "M001", dinero("10000")
        );

        verificarExcepcion(
                "Rechazar transferencia sin fondos",
                IllegalArgumentException.class,
                () -> c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("20000")
                )
        );

        verificarExcepcion(
                "Rechazar transferencia al mismo monedero",
                IllegalArgumentException.class,
                () -> c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C001",
                        "M001",
                        dinero("1000")
                )
        );

        verificarExcepcion(
                "Rechazar comision negativa",
                IllegalArgumentException.class,
                () -> c.transacciones.transferir(
                        "C001",
                        "M001",
                        "C002",
                        "M002",
                        dinero("1000"),
                        dinero("-10")
                )
        );

        verificarMonto(
                "Saldo intacto tras rechazos",
                "10000",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarIgual(
                "No registrar movimientos rechazados",
                1,
                c.transacciones.cantidadTransacciones()
        );

        verificar(
                "Integridad despues de rechazos",
                c.transacciones.verificarIntegridad()
        );
    }

    // ==========================================
    // 13. INTEGRIDAD FINANCIERA
    // ==========================================

    private static void probarIntegridad() {

        Contexto c = new Contexto();

        c.transacciones.depositar(
                "C001", "M001", dinero("150000")
        );

        c.transacciones.depositar(
                "C002", "M002", dinero("50000")
        );

        c.transacciones.transferir(
                "C001",
                "M001",
                "C002",
                "M002",
                dinero("30000"),
                dinero("500")
        );

        c.transacciones.retirar(
                "C002", "M002", dinero("10000")
        );

        c.transacciones.registrarGasto(
                "C001",
                "M001",
                dinero("5000"),
                "Transporte"
        );

        verificarMonto(
                "Saldo final C001",
                "114500",
                c.monederos.consultarSaldo(
                        "C001", "M001"
                )
        );

        verificarMonto(
                "Saldo final C002",
                "70000",
                c.monederos.consultarSaldo(
                        "C002", "M002"
                )
        );

        verificarMonto(
                "Saldo total del sistema",
                "184500",
                c.monederos.saldoTotalSistema()
        );

        verificarMonto(
                "Comisiones acumuladas",
                "500",
                c.transacciones.consultarTotalComisiones()
        );

        verificarIgual(
                "Cinco operaciones registradas",
                5,
                c.transacciones.cantidadTransacciones()
        );

        verificarIgual(
                "Cuatro operaciones reversibles",
                4,
                c.transacciones.cantidadReversibles()
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
                "Integridad de transacciones",
                c.transacciones.verificarIntegridad()
        );

        verificar(
                "Integridad de relaciones dirigidas",
                c.transacciones
                        .obtenerGrafoTransferencias()
                        .verificarIntegridad()
        );
    }

    // ==========================================
    // RESUMEN FINAL
    // ==========================================

    private static void mostrarResumen() {

        System.out.println();

        System.out.println(
                "======================================"
        );

        System.out.println(
                " RESUMEN GENERAL"
        );

        System.out.println(
                "======================================"
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
                "--------------------------------------"
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
                "======================================"
        );
    }
}
