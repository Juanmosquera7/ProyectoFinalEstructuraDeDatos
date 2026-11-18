
package servicios;

import estructuras.ListaEnlazada;
import estructuras.TablaHash;

import modelo.Cliente;
import modelo.Monedero;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Servicio encargado de administrar los clientes
 * registrados en el sistema Monedero Virtual.
 *
 * Estructuras propias utilizadas:
 *
 * TablaHash<String, Cliente>
 * - Busqueda eficiente de clientes por ID.
 *
 * ListaEnlazada<String>
 * - Conserva el orden de registro.
 *
 * Responsabilidades:
 * - Registrar clientes.
 * - Buscar clientes.
 * - Actualizar informacion.
 * - Activar y desactivar cuentas.
 * - Administrar monederos.
 * - Consultar saldos.
 *
 * Las operaciones financieras corresponden
 * a TransaccionService.
 */
public class ClienteService {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final TablaHash<String, Cliente> clientes;

    private final ListaEnlazada<String> ordenClientes;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ClienteService() {

        this.clientes = new TablaHash<>();

        this.ordenClientes = new ListaEnlazada<>();
    }

    // ==========================================
    // REGISTRO DE CLIENTES
    // ==========================================

    /**
     * Registra un cliente con ID, nombre y correo.
     *
     * Rechaza identificadores duplicados.
     *
     * @return Cliente registrado.
     */
    public Cliente registrarCliente(
            String id,
            String nombre,
            String correo) {

        validarId(id);

        if (clientes.contieneClave(id)) {

            throw new IllegalArgumentException(
                    "Ya existe un cliente con ID: " + id
            );
        }

        Cliente nuevo = new Cliente(
                id,
                nombre,
                correo
        );

        clientes.insertar(id, nuevo);

        ordenClientes.agregarFinal(id);

        return nuevo;
    }

    /**
     * Registra un cliente utilizando
     * el constructor simplificado.
     */
    public Cliente registrarCliente(
            String id,
            String nombre) {

        validarId(id);

        if (clientes.contieneClave(id)) {

            throw new IllegalArgumentException(
                    "Ya existe un cliente con ID: " + id
            );
        }

        Cliente nuevo = new Cliente(id, nombre);

        clientes.insertar(id, nuevo);

        ordenClientes.agregarFinal(id);

        return nuevo;
    }

    /**
     * Registra un objeto Cliente existente.
     *
     * Permite incorporar clientes creados
     * previamente sin duplicar sus IDs.
     */
    public void registrarCliente(Cliente cliente) {

        Objects.requireNonNull(
                cliente,
                "El cliente no puede ser null"
        );

        String id = cliente.getId();

        if (clientes.contieneClave(id)) {

            throw new IllegalArgumentException(
                    "El cliente ya esta registrado: " + id
            );
        }

        clientes.insertar(id, cliente);

        ordenClientes.agregarFinal(id);
    }

    // ==========================================
    // BUSQUEDA DE CLIENTES
    // ==========================================

    /**
     * Busca un cliente mediante su ID.
     *
     * Utiliza nuestra tabla hash propia.
     *
     * Devuelve null si no existe.
     */
    public Cliente buscarCliente(String id) {

        validarId(id);

        return clientes.obtener(id);
    }

    /**
     * Busca un cliente y lanza una excepcion
     * cuando no existe.
     *
     * Util para operaciones obligatorias.
     */
    public Cliente obtenerCliente(String id) {

        Cliente cliente = buscarCliente(id);

        if (cliente == null) {

            throw new IllegalArgumentException(
                    "No existe un cliente con ID: " + id
            );
        }

        return cliente;
    }

    /**
     * Verifica si existe un cliente registrado.
     */
    public boolean existeCliente(String id) {

        validarId(id);

        return clientes.contieneClave(id);
    }

    /**
     * Busca clientes cuyo nombre contenga
     * el texto indicado.
     *
     * La busqueda no distingue mayusculas.
     */
    public ListaEnlazada<Cliente> buscarPorNombre(
            String texto) {

        validarTexto(texto, "Texto de busqueda");

        String criterio = texto.toLowerCase();

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        recorrerClientes(cliente -> {

            if (cliente.getNombre()
                    .toLowerCase()
                    .contains(criterio)) {

                resultado.agregarFinal(cliente);
            }
        });

        return resultado;
    }

    // ==========================================
    // ACTUALIZACION DE CLIENTES
    // ==========================================

    /**
     * Actualiza el nombre de un cliente.
     */
    public void actualizarNombre(
            String id,
            String nuevoNombre) {

        validarTexto(
                nuevoNombre,
                "Nuevo nombre"
        );

        Cliente cliente = obtenerCliente(id);

        cliente.setNombre(nuevoNombre);
    }

    /**
     * Actualiza el correo electronico.
     */
    public void actualizarCorreo(
            String id,
            String nuevoCorreo) {

        Cliente cliente = obtenerCliente(id);

        cliente.setCorreo(nuevoCorreo);
    }

    // ==========================================
    // ESTADO DE LOS CLIENTES
    // ==========================================

    /**
     * Desactiva una cuenta de cliente.
     *
     * El cliente permanece registrado,
     * pero los servicios financieros
     * deberan rechazar sus operaciones.
     */
    public void desactivarCliente(String id) {

        Cliente cliente = obtenerCliente(id);

        cliente.desactivar();
    }

    /**
     * Reactiva una cuenta de cliente.
     */
    public void activarCliente(String id) {

        Cliente cliente = obtenerCliente(id);

        cliente.activar();
    }

    /**
     * Comprueba si el cliente existe
     * y se encuentra activo.
     */
    public boolean clienteActivo(String id) {

        Cliente cliente = buscarCliente(id);

        return cliente != null && cliente.isActivo();
    }

    /**
     * Obtiene un cliente activo.
     *
     * Este metodo sera utilizado por
     * los servicios financieros.
     */
    public Cliente obtenerClienteActivo(String id) {

        Cliente cliente = obtenerCliente(id);

        if (!cliente.isActivo()) {

            throw new IllegalStateException(
                    "El cliente " + id
                            + " se encuentra inactivo"
            );
        }

        return cliente;
    }

    // ==========================================
    // GESTION DE MONEDEROS
    // ==========================================

    /**
     * Crea un monedero para un cliente.
     *
     * Valida la pertenencia del monedero
     * y evita IDs duplicados en el cliente.
     */
    public Monedero crearMonedero(
            String idCliente,
            String idMonedero,
            String nombre,
            String tipo) {

        Cliente cliente = obtenerClienteActivo(
                idCliente
        );

        // Impedir identificadores duplicados
        // entre diferentes clientes.
        if (existeMonederoGlobal(idMonedero)) {

            throw new IllegalArgumentException(
                    "Ya existe un monedero con ID: "
                            + idMonedero
            );
        }

        Monedero monedero = new Monedero(
                idMonedero,
                idCliente,
                nombre,
                tipo
        );

        cliente.agregarMonedero(monedero);

        return monedero;
    }

    /**
     * Crea un monedero principal.
     */
    public Monedero crearMonederoPrincipal(
            String idCliente,
            String idMonedero) {

        Cliente cliente = obtenerClienteActivo(
                idCliente
        );

        if (cliente.getMonederoPrincipal() != null) {

            throw new IllegalStateException(
                    "El cliente ya tiene "
                            + "un monedero principal"
            );
        }

        return crearMonedero(
                idCliente,
                idMonedero,
                "Monedero principal",
                "PRINCIPAL"
        );
    }

    /**
     * Busca un monedero perteneciente
     * a un cliente determinado.
     *
     * Devuelve null si no existe.
     */
    public Monedero buscarMonedero(
            String idCliente,
            String idMonedero) {

        Cliente cliente = obtenerCliente(idCliente);

        return cliente.buscarMonedero(idMonedero);
    }

    /**
     * Obtiene un monedero obligatorio.
     *
     * Lanza excepcion si no existe.
     */
    public Monedero obtenerMonedero(
            String idCliente,
            String idMonedero) {

        Monedero monedero = buscarMonedero(
                idCliente,
                idMonedero
        );

        if (monedero == null) {

            throw new IllegalArgumentException(
                    "No existe el monedero "
                            + idMonedero
                            + " para el cliente "
                            + idCliente
            );
        }

        return monedero;
    }

    /**
     * Obtiene un monedero activo
     * perteneciente a un cliente activo.
     */
    public Monedero obtenerMonederoActivo(
            String idCliente,
            String idMonedero) {

        obtenerClienteActivo(idCliente);

        Monedero monedero = obtenerMonedero(
                idCliente,
                idMonedero
        );

        if (!monedero.isActivo()) {

            throw new IllegalStateException(
                    "El monedero " + idMonedero
                            + " esta bloqueado"
            );
        }

        return monedero;
    }

    /**
     * Verifica si un ID de monedero
     * ya esta registrado en el sistema.
     *
     * Recorre todos los clientes registrados.
     */
    public boolean existeMonederoGlobal(
            String idMonedero) {

        validarTexto(
                idMonedero,
                "ID del monedero"
        );

        final boolean[] encontrado = {false};

        recorrerClientes(cliente -> {

            if (cliente.contieneMonedero(idMonedero)) {
                encontrado[0] = true;
            }
        });

        return encontrado[0];
    }

    /**
     * Elimina un monedero cuando su saldo es cero.
     *
     * La validacion financiera se realiza
     * dentro de Cliente.java.
     */
    public boolean eliminarMonedero(
            String idCliente,
            String idMonedero) {

        Cliente cliente = obtenerClienteActivo(
                idCliente
        );

        return cliente.eliminarMonedero(idMonedero);
    }

    /**
     * Bloquea un monedero del cliente.
     */
    public void bloquearMonedero(
            String idCliente,
            String idMonedero) {

        obtenerClienteActivo(idCliente);

        Monedero monedero = obtenerMonedero(
                idCliente,
                idMonedero
        );

        monedero.bloquear();
    }

    /**
     * Desbloquea un monedero del cliente.
     */
    public void desbloquearMonedero(
            String idCliente,
            String idMonedero) {

        obtenerClienteActivo(idCliente);

        Monedero monedero = obtenerMonedero(
                idCliente,
                idMonedero
        );

        monedero.desbloquear();
    }

    // ==========================================
    // CONSULTAS FINANCIERAS
    // ==========================================

    /**
     * Consulta el saldo de un monedero.
     *
     * No modifica el dinero.
     */
    public BigDecimal consultarSaldoMonedero(
            String idCliente,
            String idMonedero) {

        Monedero monedero = obtenerMonedero(
                idCliente,
                idMonedero
        );

        return monedero.consultarSaldo();
    }

    /**
     * Consulta el saldo total de los
     * monederos de un cliente.
     */
    public BigDecimal consultarSaldoTotal(
            String idCliente) {

        Cliente cliente = obtenerCliente(idCliente);

        return cliente.consultarSaldoTotal();
    }

    /**
     * Obtiene los monederos de un cliente.
     *
     * Devuelve una copia de la lista.
     */
    public ListaEnlazada<Monedero> listarMonederos(
            String idCliente) {

        Cliente cliente = obtenerCliente(idCliente);

        return cliente.getMonederos();
    }

    // ==========================================
    // LISTADO DE CLIENTES
    // ==========================================

    /**
     * Obtiene todos los clientes
     * en el orden en que fueron registrados.
     *
     * Devuelve una nueva lista.
     */
    public ListaEnlazada<Cliente> listarClientes() {

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        recorrerClientes(resultado::agregarFinal);

        return resultado;
    }

    /**
     * Obtiene solamente los clientes activos.
     */
    public ListaEnlazada<Cliente> listarClientesActivos() {

        ListaEnlazada<Cliente> resultado =
                new ListaEnlazada<>();

        recorrerClientes(cliente -> {

            if (cliente.isActivo()) {
                resultado.agregarFinal(cliente);
            }
        });

        return resultado;
    }

    /**
     * Recorre todos los clientes registrados.
     */
    public void recorrerClientes(
            Consumer<Cliente> accion) {

        Objects.requireNonNull(
                accion,
                "La accion no puede ser null"
        );

        ordenClientes.recorrer(id -> {

            Cliente cliente = clientes.obtener(id);

            if (cliente != null) {
                accion.accept(cliente);
            }
        });
    }

    // ==========================================
    // ESTADISTICAS
    // ==========================================

    /**
     * Obtiene el numero total de clientes.
     */
    public int cantidadClientes() {

        return clientes.tamanio();
    }

    /**
     * Cuenta los clientes activos.
     */
    public int cantidadClientesActivos() {

        final int[] contador = {0};

        recorrerClientes(cliente -> {

            if (cliente.isActivo()) {
                contador[0]++;
            }
        });

        return contador[0];
    }

    /**
     * Cuenta todos los monederos del sistema.
     */
    public int cantidadMonederos() {

        final int[] contador = {0};

        recorrerClientes(cliente -> {

            contador[0] +=
                    cliente.getCantidadMonederos();
        });

        return contador[0];
    }

    /**
     * Calcula el dinero total almacenado
     * en todos los monederos registrados.
     */
    public BigDecimal saldoTotalSistema() {

        final BigDecimal[] total = {
                BigDecimal.ZERO
        };

        recorrerClientes(cliente -> {

            total[0] = total[0].add(
                    cliente.consultarSaldoTotal()
            );
        });

        return total[0].setScale(2);
    }

    // ==========================================
    // VERIFICACION DE INTEGRIDAD
    // ==========================================

    /**
     * Comprueba:
     *
     * - Coincidencia entre lista y tabla hash.
     * - Identificadores correctos.
     * - Propiedad de los monederos.
     * - Puntos no negativos.
     * - Rangos actualizados.
     * - Saldos no negativos.
     */
    public boolean verificarIntegridad() {

        if (ordenClientes.tamanio()
                != clientes.tamanio()) {

            return false;
        }

        final boolean[] valido = {true};
        final TablaHash<String, Boolean> idsVistos =
                new TablaHash<>();

        ordenClientes.recorrer(id -> {

            if (!valido[0]) {
                return;
            }

            if (idsVistos.contieneClave(id)) {
                valido[0] = false;
                return;
            }

            idsVistos.insertar(id, true);

            Cliente cliente = clientes.obtener(id);

            if (cliente == null
                    || !cliente.getId().equals(id)) {

                valido[0] = false;
                return;
            }

            if (cliente.getPuntos() < 0
                    || cliente.getRango()
                    != modelo.Rango.calcularRango(
                    cliente.getPuntos()
            )) {

                valido[0] = false;
                return;
            }

            cliente.getMonederos().recorrer(monedero -> {

                if (monedero == null
                        || !monedero.getIdCliente()
                        .equals(cliente.getId())
                        || monedero.getSaldo().compareTo(
                        BigDecimal.ZERO) < 0) {

                    valido[0] = false;
                }
            });
        });

        return valido[0];
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarId(String id) {

        validarTexto(id, "ID del cliente");
    }

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
                new StringBuilder("CLIENTES REGISTRADOS\n");

        recorrerClientes(cliente -> {

            resultado.append(cliente)
                    .append("\n");
        });

        return resultado.toString();
    }
}
