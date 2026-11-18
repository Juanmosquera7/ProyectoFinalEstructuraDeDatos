
package servicios;

import estructuras.ListaEnlazada;
import modelo.Cliente;
import modelo.Monedero;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Servicio encargado de administrar los monederos
 * del sistema Monedero Virtual.
 *
 * Utiliza ClienteService para acceder a los
 * clientes y verificar sus monederos.
 *
 * Responsabilidades:
 * - Crear monederos.
 * - Buscar monederos.
 * - Modificar nombres y tipos.
 * - Bloquear y desbloquear monederos.
 * - Consultar saldos.
 * - Obtener estadisticas.
 * - Verificar integridad.
 *
 * IMPORTANTE:
 * Este servicio NO realiza depositos,
 * retiros ni transferencias.
 *
 * Las operaciones financieras corresponden
 * a TransaccionService para garantizar
 * el registro correcto de movimientos.
 */
public class MonederoService {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    /**
     * Recibe el servicio de clientes existente.
     *
     * Todos los servicios deben trabajar con
     * la misma instancia de ClienteService.
     */
    public MonederoService(
            ClienteService clienteService) {

        this.clienteService = Objects.requireNonNull(
                clienteService,
                "ClienteService no puede ser null"
        );
    }

    // ==========================================
    // CREACION DE MONEDEROS
    // ==========================================

    /**
     * Crea un monedero con tipo personalizado.
     */
    public Monedero crearMonedero(
            String idCliente,
            String idMonedero,
            String nombre,
            String tipo) {

        validarTexto(idCliente, "ID del cliente");
        validarTexto(idMonedero, "ID del monedero");
        validarTexto(nombre, "Nombre");
        validarTexto(tipo, "Tipo");

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        // Evitar un segundo monedero principal.
        if (tipo.equalsIgnoreCase("PRINCIPAL")
                && cliente.getMonederoPrincipal() != null) {

            throw new IllegalStateException(
                    "El cliente ya tiene "
                            + "un monedero principal"
            );
        }

        return clienteService.crearMonedero(
                idCliente,
                idMonedero,
                nombre,
                tipo.toUpperCase()
        );
    }

    /**
     * Crea un monedero principal.
     */
    public Monedero crearMonederoPrincipal(
            String idCliente,
            String idMonedero) {

        return clienteService.crearMonederoPrincipal(
                idCliente,
                idMonedero
        );
    }

    /**
     * Crea un monedero de ahorros.
     */
    public Monedero crearMonederoAhorros(
            String idCliente,
            String idMonedero) {

        return crearMonedero(
                idCliente,
                idMonedero,
                "Monedero de ahorros",
                "AHORROS"
        );
    }

    /**
     * Crea un monedero de gastos diarios.
     */
    public Monedero crearMonederoGastos(
            String idCliente,
            String idMonedero) {

        return crearMonedero(
                idCliente,
                idMonedero,
                "Monedero de gastos",
                "GASTOS"
        );
    }

    // ==========================================
    // BUSQUEDA DE MONEDEROS
    // ==========================================

    /**
     * Busca un monedero de un cliente.
     *
     * Devuelve null cuando no existe.
     */
    public Monedero buscarMonedero(
            String idCliente,
            String idMonedero) {

        return clienteService.buscarMonedero(
                idCliente,
                idMonedero
        );
    }

    /**
     * Obtiene un monedero obligatorio.
     *
     * Lanza excepcion cuando no existe.
     */
    public Monedero obtenerMonedero(
            String idCliente,
            String idMonedero) {

        return clienteService.obtenerMonedero(
                idCliente,
                idMonedero
        );
    }

    /**
     * Obtiene un monedero habilitado
     * perteneciente a un cliente activo.
     */
    public Monedero obtenerMonederoActivo(
            String idCliente,
            String idMonedero) {

        return clienteService.obtenerMonederoActivo(
                idCliente,
                idMonedero
        );
    }

    /**
     * Verifica si un monedero pertenece
     * a un cliente especifico.
     */
    public boolean perteneceACliente(
            String idCliente,
            String idMonedero) {

        return buscarMonedero(
                idCliente,
                idMonedero
        ) != null;
    }

    /**
     * Busca un monedero globalmente por su ID.
     *
     * Recorre todos los clientes registrados.
     * Devuelve null si no existe.
     */
    public Monedero buscarMonederoGlobal(
            String idMonedero) {

        validarTexto(
                idMonedero,
                "ID del monedero"
        );

        final Monedero[] resultado = {null};

        clienteService.recorrerClientes(cliente -> {

            if (resultado[0] == null) {

                Monedero encontrado =
                        cliente.buscarMonedero(idMonedero);

                if (encontrado != null) {
                    resultado[0] = encontrado;
                }
            }
        });

        return resultado[0];
    }

    // ==========================================
    // MODIFICACION DE MONEDEROS
    // ==========================================

    /**
     * Cambia el nombre de un monedero.
     */
    public void cambiarNombre(
            String idCliente,
            String idMonedero,
            String nuevoNombre) {

        validarTexto(
                nuevoNombre,
                "Nuevo nombre"
        );

        Monedero monedero = obtenerMonederoActivo(
                idCliente,
                idMonedero
        );

        monedero.setNombre(nuevoNombre);
    }

    /**
     * Cambia el tipo de monedero.
     *
     * Solo puede existir un monedero principal
     * por cliente.
     */
    public void cambiarTipo(
            String idCliente,
            String idMonedero,
            String nuevoTipo) {

        validarTexto(nuevoTipo, "Nuevo tipo");

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        Monedero monedero = obtenerMonederoActivo(
                idCliente,
                idMonedero
        );

        String tipoNormalizado =
                nuevoTipo.toUpperCase();

        if (tipoNormalizado.equals("PRINCIPAL")) {

            Monedero principal =
                    cliente.getMonederoPrincipal();

            if (principal != null
                    && !principal.getId().equals(
                    idMonedero)) {

                throw new IllegalStateException(
                        "El cliente ya tiene "
                                + "un monedero principal"
                );
            }
        }

        monedero.setTipo(tipoNormalizado);
    }

    // ==========================================
    // BLOQUEO Y DESBLOQUEO
    // ==========================================

    /**
     * Bloquea un monedero.
     *
     * No elimina el saldo ni el monedero.
     */
    public void bloquearMonedero(
            String idCliente,
            String idMonedero) {

        clienteService.bloquearMonedero(
                idCliente,
                idMonedero
        );
    }

    /**
     * Desbloquea un monedero.
     */
    public void desbloquearMonedero(
            String idCliente,
            String idMonedero) {

        clienteService.desbloquearMonedero(
                idCliente,
                idMonedero
        );
    }

    /**
     * Consulta si un monedero esta activo.
     */
    public boolean estaActivo(
            String idCliente,
            String idMonedero) {

        Monedero monedero = obtenerMonedero(
                idCliente,
                idMonedero
        );

        return monedero.isActivo();
    }

    // ==========================================
    // ELIMINACION DE MONEDEROS
    // ==========================================

    /**
     * Elimina un monedero cuyo saldo sea cero.
     *
     * No permite eliminar un monedero
     * que todavia tenga dinero.
     */
    public boolean eliminarMonedero(
            String idCliente,
            String idMonedero) {

        return clienteService.eliminarMonedero(
                idCliente,
                idMonedero
        );
    }

    // ==========================================
    // CONSULTAS DE SALDOS
    // ==========================================

    /**
     * Consulta el saldo de un monedero.
     */
    public BigDecimal consultarSaldo(
            String idCliente,
            String idMonedero) {

        return clienteService.consultarSaldoMonedero(
                idCliente,
                idMonedero
        );
    }

    /**
     * Consulta el saldo total de un cliente.
     */
    public BigDecimal consultarSaldoTotalCliente(
            String idCliente) {

        return clienteService.consultarSaldoTotal(
                idCliente
        );
    }

    /**
     * Comprueba si un monedero tiene fondos
     * suficientes para una operacion.
     *
     * No descuenta dinero.
     */
    public boolean tieneSaldoSuficiente(
            String idCliente,
            String idMonedero,
            BigDecimal monto) {

        Monedero monedero = obtenerMonederoActivo(
                idCliente,
                idMonedero
        );

        return monedero.tieneSaldoSuficiente(monto);
    }

    /**
     * Comprueba si el saldo esta por debajo
     * de un limite determinado.
     */
    public boolean tieneSaldoBajo(
            String idCliente,
            String idMonedero,
            BigDecimal limite) {

        Monedero monedero = obtenerMonedero(
                idCliente,
                idMonedero
        );

        return monedero.tieneSaldoBajo(limite);
    }

    // ==========================================
    // LISTADOS Y RECORRIDOS
    // ==========================================

    /**
     * Obtiene los monederos de un cliente.
     *
     * Devuelve una lista nueva que contiene
     * referencias a los mismos monederos.
     */
    public ListaEnlazada<Monedero> listarMonederos(
            String idCliente) {

        return clienteService.listarMonederos(
                idCliente
        );
    }

    /**
     * Devuelve los monederos de un tipo
     * especifico pertenecientes a un cliente.
     */
    public ListaEnlazada<Monedero> listarPorTipo(
            String idCliente,
            String tipo) {

        validarTexto(tipo, "Tipo");

        ListaEnlazada<Monedero> resultado =
                new ListaEnlazada<>();

        listarMonederos(idCliente).recorrer(monedero -> {

            if (monedero.getTipo().equalsIgnoreCase(tipo)) {
                resultado.agregarFinal(monedero);
            }
        });

        return resultado;
    }

    /**
     * Recorre todos los monederos registrados
     * en el sistema.
     */
    public void recorrerMonederos(
            Consumer<Monedero> accion) {

        Objects.requireNonNull(
                accion,
                "La accion no puede ser null"
        );

        clienteService.recorrerClientes(cliente -> {

            cliente.getMonederos().recorrer(accion);
        });
    }

    /**
     * Obtiene todos los monederos del sistema.
     */
    public ListaEnlazada<Monedero> listarTodos() {

        ListaEnlazada<Monedero> resultado =
                new ListaEnlazada<>();

        recorrerMonederos(resultado::agregarFinal);

        return resultado;
    }

    // ==========================================
    // ESTADISTICAS
    // ==========================================

    /**
     * Cantidad total de monederos registrados.
     */
    public int cantidadMonederos() {

        return clienteService.cantidadMonederos();
    }

    /**
     * Cantidad de monederos activos.
     */
    public int cantidadMonederosActivos() {

        final int[] cantidad = {0};

        recorrerMonederos(monedero -> {

            if (monedero.isActivo()) {
                cantidad[0]++;
            }
        });

        return cantidad[0];
    }

    /**
     * Cantidad de monederos bloqueados.
     */
    public int cantidadMonederosBloqueados() {

        final int[] cantidad = {0};

        recorrerMonederos(monedero -> {

            if (!monedero.isActivo()) {
                cantidad[0]++;
            }
        });

        return cantidad[0];
    }

    /**
     * Calcula el saldo total almacenado
     * en todos los monederos del sistema.
     */
    public BigDecimal saldoTotalSistema() {

        return clienteService.saldoTotalSistema();
    }

    /**
     * Calcula el saldo total de los monederos
     * que pertenecen a un tipo determinado.
     */
    public BigDecimal saldoTotalPorTipo(String tipo) {

        validarTexto(tipo, "Tipo");

        final BigDecimal[] total = {
                BigDecimal.ZERO
        };

        recorrerMonederos(monedero -> {

            if (monedero.getTipo().equalsIgnoreCase(tipo)) {

                total[0] = total[0].add(
                        monedero.getSaldo()
                );
            }
        });

        return total[0].setScale(2);
    }

    // ==========================================
    // VERIFICACION DE INTEGRIDAD
    // ==========================================

    /**
     * Verifica:
     *
     * - Que todos los monederos tengan dueño.
     * - Que sus IDs sean unicos globalmente.
     * - Que los saldos no sean negativos.
     * - Que no existan varios principales
     *   para un mismo cliente.
     */
    public boolean verificarIntegridad() {

        if (!clienteService.verificarIntegridad()) {
            return false;
        }

        final boolean[] valido = {true};

        final estructuras.TablaHash<String, Boolean> ids =
                new estructuras.TablaHash<>();

        clienteService.recorrerClientes(cliente -> {

            final int[] principales = {0};

            cliente.getMonederos().recorrer(monedero -> {

                if (monedero == null) {
                    valido[0] = false;
                    return;
                }

                if (!monedero.getIdCliente().equals(
                        cliente.getId())) {

                    valido[0] = false;
                }

                if (ids.contieneClave(monedero.getId())) {

                    valido[0] = false;

                } else {

                    ids.insertar(
                            monedero.getId(),
                            true
                    );
                }

                if (monedero.getSaldo().compareTo(
                        BigDecimal.ZERO) < 0) {

                    valido[0] = false;
                }

                if (monedero.getTipo().equalsIgnoreCase(
                        "PRINCIPAL")) {

                    principales[0]++;
                }
            });

            if (principales[0] > 1) {
                valido[0] = false;
            }
        });

        return valido[0];
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarTexto(
            String texto,
            String campo) {

        if (texto == null || texto.isBlank()) {

            throw new IllegalArgumentException(
                    campo + " no puede estar vacio"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        StringBuilder resultado =
                new StringBuilder("MONEDEROS REGISTRADOS\n");

        recorrerMonederos(monedero -> {

            resultado.append(monedero)
                    .append("\n");
        });

        return resultado.toString();
    }
}
