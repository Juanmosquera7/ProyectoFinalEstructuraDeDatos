
package servicios;

import estructuras.ListaCircular;
import estructuras.ListaEnlazada;
import estructuras.TablaHash;

import modelo.Cliente;
import modelo.Notificacion;
import modelo.Rango;
import modelo.Transaccion;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Servicio de notificaciones del Monedero Virtual.
 *
 * ESTRUCTURAS PROPIAS:
 *
 * ListaCircular<Notificacion>:
 * Almacena las notificaciones de cada cliente.
 *
 * TablaHash<String, ListaCircular<Notificacion>>:
 * Relaciona cada cliente con su lista circular.
 *
 * TablaHash<String, Notificacion>:
 * Permite buscar notificaciones por ID.
 *
 * FUNCIONALIDADES:
 *
 * - Enviar notificaciones.
 * - Alertar sobre saldo bajo.
 * - Informar depositos y retiros.
 * - Notificar transferencias.
 * - Informar puntos ganados.
 * - Notificar cambios de rango.
 * - Generar recordatorios.
 * - Informar operaciones fallidas.
 * - Marcar como leidas.
 * - Eliminar notificaciones.
 * - Recorrido circular.
 * - Estadisticas e integridad.
 *
 * No utiliza colecciones de Java.
 */
public class NotificacionService {

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;

    private final TablaHash<
            String,
            ListaCircular<Notificacion>
            > notificacionesPorCliente;

    private final TablaHash<String, Notificacion>
            indiceNotificaciones;

    private int totalNotificaciones;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public NotificacionService(
            ClienteService clienteService) {

        this.clienteService = Objects.requireNonNull(
                clienteService,
                "ClienteService no puede ser null"
        );

        this.notificacionesPorCliente =
                new TablaHash<>();

        this.indiceNotificaciones =
                new TablaHash<>();

        this.totalNotificaciones = 0;
    }

    // ==========================================
    // REGISTRO DE NOTIFICACIONES
    // ==========================================

    /**
     * Registra una notificacion existente.
     *
     * Comprueba que:
     * - La notificacion no sea null.
     * - El cliente exista.
     * - El ID no se encuentre registrado.
     *
     * Devuelve la notificacion registrada.
     */
    public Notificacion enviarNotificacion(
            Notificacion notificacion) {

        Objects.requireNonNull(
                notificacion,
                "La notificacion no puede ser null"
        );

        String idCliente =
                notificacion.getIdCliente();

        clienteService.obtenerCliente(idCliente);

        if (indiceNotificaciones.contieneClave(
                notificacion.getId())) {

            throw new IllegalArgumentException(
                    "La notificacion ya esta registrada: "
                            + notificacion.getId()
            );
        }

        ListaCircular<Notificacion> lista =
                obtenerOCrearLista(idCliente);

        lista.agregarFinal(notificacion);

        indiceNotificaciones.insertar(
                notificacion.getId(),
                notificacion
        );

        totalNotificaciones++;

        return notificacion;
    }

    /**
     * Crea y registra una notificacion general.
     */
    public Notificacion enviarNotificacion(
            String idCliente,
            Notificacion.Tipo tipo,
            String titulo,
            String mensaje,
            Notificacion.Prioridad prioridad) {

        Notificacion nueva = new Notificacion(
                idCliente,
                tipo,
                titulo,
                mensaje,
                prioridad,
                null
        );

        return enviarNotificacion(nueva);
    }

    /**
     * Crea una notificacion general
     * con prioridad media.
     */
    public Notificacion enviarNotificacion(
            String idCliente,
            Notificacion.Tipo tipo,
            String titulo,
            String mensaje) {

        return enviarNotificacion(
                new Notificacion(
                        idCliente,
                        tipo,
                        titulo,
                        mensaje
                )
        );
    }

    // ==========================================
    // LISTA CIRCULAR POR CLIENTE
    // ==========================================

    /**
     * Obtiene la lista circular del cliente.
     *
     * Si todavia no existe, la crea.
     */
    private ListaCircular<Notificacion>
    obtenerOCrearLista(String idCliente) {

        ListaCircular<Notificacion> lista =
                notificacionesPorCliente.obtener(
                        idCliente
                );

        if (lista == null) {

            lista = new ListaCircular<>();

            notificacionesPorCliente.insertar(
                    idCliente,
                    lista
            );
        }

        return lista;
    }

    /**
     * Obtiene la lista del cliente.
     *
     * Devuelve null cuando todavia
     * no tiene notificaciones.
     */
    private ListaCircular<Notificacion>
    obtenerLista(String idCliente) {

        return notificacionesPorCliente.obtener(
                idCliente
        );
    }

    // ==========================================
    // NOTIFICACIONES FINANCIERAS
    // ==========================================

    /**
     * Notifica un deposito completado.
     */
    public Notificacion notificarDeposito(
            String idCliente,
            BigDecimal monto,
            String idTransaccion) {

        validarMonto(monto);

        Notificacion nueva = new Notificacion(
                idCliente,
                Notificacion.Tipo.DEPOSITO,
                "Deposito realizado",
                "Se han depositado $"
                        + monto.toPlainString()
                        + " en tu monedero.",
                Notificacion.Prioridad.MEDIA,
                idTransaccion
        );

        return enviarNotificacion(nueva);
    }

    /**
     * Notifica un retiro completado.
     */
    public Notificacion notificarRetiro(
            String idCliente,
            BigDecimal monto,
            String idTransaccion) {

        validarMonto(monto);

        Notificacion nueva = new Notificacion(
                idCliente,
                Notificacion.Tipo.RETIRO,
                "Retiro realizado",
                "Has retirado $"
                        + monto.toPlainString()
                        + " de tu monedero.",
                Notificacion.Prioridad.MEDIA,
                idTransaccion
        );

        return enviarNotificacion(nueva);
    }

    /**
     * Notifica una transferencia enviada.
     */
    public Notificacion notificarTransferenciaEnviada(
            String idCliente,
            BigDecimal monto,
            String idTransaccion) {

        validarMonto(monto);

        Notificacion nueva =
                Notificacion.crearTransferenciaEnviada(
                        idCliente,
                        monto.toPlainString(),
                        idTransaccion
                );

        return enviarNotificacion(nueva);
    }

    /**
     * Notifica una transferencia recibida.
     */
    public Notificacion notificarTransferenciaRecibida(
            String idCliente,
            BigDecimal monto,
            String idTransaccion) {

        validarMonto(monto);

        Notificacion nueva =
                Notificacion.crearTransferenciaRecibida(
                        idCliente,
                        monto.toPlainString(),
                        idTransaccion
                );

        return enviarNotificacion(nueva);
    }

    /**
     * Genera las notificaciones correspondientes
     * a una transferencia completada.
     *
     * Envia un aviso al origen y otro
     * al destino.
     */
    public void notificarTransferencia(
            Transaccion transaccion) {

        Objects.requireNonNull(
                transaccion,
                "La transaccion no puede ser null"
        );

        if (transaccion.getTipo()
                != modelo.TipoTransaccion.TRANSFERENCIA) {

            throw new IllegalArgumentException(
                    "La transaccion no es una transferencia"
            );
        }

        if (!transaccion.estaCompletada()) {

            throw new IllegalStateException(
                    "Solo se notifican transferencias completadas"
            );
        }

        clienteService.obtenerCliente(
                transaccion.getIdClienteOrigen()
        );

        clienteService.obtenerCliente(
                transaccion.getIdClienteDestino()
        );

        notificarTransferenciaEnviada(
                transaccion.getIdClienteOrigen(),
                transaccion.getMonto(),
                transaccion.getId()
        );

        notificarTransferenciaRecibida(
                transaccion.getIdClienteDestino(),
                transaccion.getMonto(),
                transaccion.getId()
        );
    }

    // ==========================================
    // ALERTAS DE SALDO
    // ==========================================

    /**
     * Genera una alerta de saldo bajo.
     */
    public Notificacion notificarSaldoBajo(
            String idCliente,
            String nombreMonedero) {

        Notificacion nueva =
                Notificacion.crearSaldoBajo(
                        idCliente,
                        nombreMonedero
                );

        return enviarNotificacion(nueva);
    }

    /**
     * Revisa si un monedero tiene saldo
     * inferior al limite establecido.
     *
     * Si es asi, genera una notificacion.
     *
     * Devuelve true cuando crea una alerta.
     */
    public boolean comprobarSaldoBajo(
            String idCliente,
            String idMonedero,
            BigDecimal limite) {

        if (limite == null
                || limite.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El limite no puede ser negativo"
            );
        }

        modelo.Monedero monedero =
                clienteService.obtenerMonedero(
                        idCliente,
                        idMonedero
                );

        if (!monedero.tieneSaldoBajo(limite)) {
            return false;
        }

        notificarSaldoBajo(
                idCliente,
                monedero.getNombre()
        );

        return true;
    }

    // ==========================================
    // SISTEMA DE PUNTOS Y RANGOS
    // ==========================================

    /**
     * Informa los puntos obtenidos
     * por una transaccion.
     */
    public Notificacion notificarPuntosGanados(
            String idCliente,
            int puntos,
            String idTransaccion) {

        Notificacion nueva =
                Notificacion.crearPuntosGanados(
                        idCliente,
                        puntos,
                        idTransaccion
                );

        return enviarNotificacion(nueva);
    }

    /**
     * Informa un cambio de rango.
     */
    public Notificacion notificarCambioRango(
            String idCliente,
            Rango nuevoRango) {

        Notificacion nueva =
                Notificacion.crearCambioRango(
                        idCliente,
                        nuevoRango
                );

        return enviarNotificacion(nueva);
    }

    /**
     * Informa el canje de una recompensa.
     */
    public Notificacion notificarCanje(
            String idCliente,
            String descripcionBeneficio) {

        validarTexto(
                descripcionBeneficio,
                "Descripcion del beneficio"
        );

        return enviarNotificacion(
                idCliente,
                Notificacion.Tipo.CANJE_REALIZADO,
                "Beneficio canjeado",
                "Has canjeado tus puntos por: "
                        + descripcionBeneficio,
                Notificacion.Prioridad.MEDIA
        );
    }

    // ==========================================
    // RECORDATORIOS Y PROGRAMACIONES
    // ==========================================

    /**
     * Crea un recordatorio de una
     * transaccion programada.
     */
    public Notificacion notificarRecordatorio(
            String idCliente,
            String descripcion) {

        Notificacion nueva =
                Notificacion.crearRecordatorio(
                        idCliente,
                        descripcion
                );

        return enviarNotificacion(nueva);
    }

    /**
     * Informa que una transaccion
     * ha sido programada.
     */
    public Notificacion notificarProgramacion(
            String idCliente,
            String descripcion) {

        return enviarNotificacion(
                idCliente,
                Notificacion.Tipo.TRANSACCION_PROGRAMADA,
                "Transaccion programada",
                descripcion,
                Notificacion.Prioridad.MEDIA
        );
    }

    // ==========================================
    // ERRORES Y REVERSIONES
    // ==========================================

    /**
     * Notifica una transaccion fallida.
     */
    public Notificacion notificarTransaccionFallida(
            String idCliente,
            String motivo,
            String idTransaccion) {

        Notificacion nueva =
                Notificacion.crearTransaccionFallida(
                        idCliente,
                        motivo,
                        idTransaccion
                );

        return enviarNotificacion(nueva);
    }

    /**
     * Notifica la reversion de una transaccion.
     */
    public Notificacion notificarReversion(
            String idCliente,
            String idTransaccion) {

        Notificacion nueva =
                Notificacion.crearReversion(
                        idCliente,
                        idTransaccion
                );

        return enviarNotificacion(nueva);
    }

    // ==========================================
    // BUSQUEDA POR IDENTIFICADOR
    // ==========================================

    /**
     * Busca una notificacion mediante
     * nuestra tabla hash propia.
     *
     * Devuelve null si no existe.
     */
    public Notificacion buscarNotificacion(
            String idNotificacion) {

        validarTexto(
                idNotificacion,
                "ID de la notificacion"
        );

        return indiceNotificaciones.obtener(
                idNotificacion
        );
    }

    /**
     * Obtiene una notificacion obligatoria.
     */
    public Notificacion obtenerNotificacion(
            String idNotificacion) {

        Notificacion notificacion =
                buscarNotificacion(idNotificacion);

        if (notificacion == null) {

            throw new IllegalArgumentException(
                    "No existe la notificacion: "
                            + idNotificacion
            );
        }

        return notificacion;
    }

    // ==========================================
    // CONSULTAS POR CLIENTE
    // ==========================================

    /**
     * Devuelve una copia de las notificaciones
     * de un cliente en una lista enlazada.
     *
     * La lista es independiente, pero contiene
     * referencias a los objetos originales.
     */
    public ListaEnlazada<Notificacion>
    listarNotificaciones(String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaEnlazada<Notificacion> resultado =
                new ListaEnlazada<>();

        ListaCircular<Notificacion> lista =
                obtenerLista(idCliente);

        if (lista != null) {

            lista.recorrer(resultado::agregarFinal);
        }

        return resultado;
    }

    /**
     * Devuelve solamente las
     * notificaciones no leidas.
     */
    public ListaEnlazada<Notificacion>
    listarNoLeidas(String idCliente) {

        ListaEnlazada<Notificacion> resultado =
                new ListaEnlazada<>();

        listarNotificaciones(idCliente).recorrer(
                notificacion -> {

                    if (!notificacion.isLeida()) {

                        resultado.agregarFinal(
                                notificacion
                        );
                    }
                }
        );

        return resultado;
    }

    /**
     * Devuelve las notificaciones urgentes.
     */
    public ListaEnlazada<Notificacion>
    listarUrgentes(String idCliente) {

        ListaEnlazada<Notificacion> resultado =
                new ListaEnlazada<>();

        listarNotificaciones(idCliente).recorrer(
                notificacion -> {

                    if (notificacion.esUrgente()) {

                        resultado.agregarFinal(
                                notificacion
                        );
                    }
                }
        );

        return resultado;
    }

    /**
     * Filtra notificaciones segun su tipo.
     */
    public ListaEnlazada<Notificacion> listarPorTipo(
            String idCliente,
            Notificacion.Tipo tipo) {

        Objects.requireNonNull(
                tipo,
                "El tipo no puede ser null"
        );

        ListaEnlazada<Notificacion> resultado =
                new ListaEnlazada<>();

        listarNotificaciones(idCliente).recorrer(
                notificacion -> {

                    if (notificacion.getTipo() == tipo) {

                        resultado.agregarFinal(
                                notificacion
                        );
                    }
                }
        );

        return resultado;
    }

    /**
     * Filtra notificaciones asociadas
     * a una transaccion especifica.
     */
    public ListaEnlazada<Notificacion>
    listarPorTransaccion(String idTransaccion) {

        validarTexto(
                idTransaccion,
                "ID de la transaccion"
        );

        ListaEnlazada<Notificacion> resultado =
                new ListaEnlazada<>();

        indiceNotificaciones.recorrer(
                (id, notificacion) -> {

                    if (idTransaccion.equals(
                            notificacion.getIdTransaccion())) {

                        resultado.agregarFinal(
                                notificacion
                        );
                    }
                }
        );

        return resultado;
    }

    // ==========================================
    // RECORRIDO CIRCULAR
    // ==========================================

    /**
     * Consulta la notificacion situada
     * al frente de la lista circular.
     *
     * No modifica el orden.
     *
     * Devuelve null si no hay notificaciones.
     */
    public Notificacion consultarActual(
            String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaCircular<Notificacion> lista =
                obtenerLista(idCliente);

        if (lista == null || lista.estaVacia()) {
            return null;
        }

        return lista.obtenerPrimero();
    }

    /**
     * Avanza una posicion en la lista circular.
     *
     * Permite mostrar las notificaciones
     * de manera ciclica.
     *
     * Devuelve la nueva notificacion actual.
     */
    public Notificacion avanzarCircular(
            String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaCircular<Notificacion> lista =
                obtenerLista(idCliente);

        if (lista == null || lista.estaVacia()) {
            return null;
        }

        lista.rotar();

        return lista.obtenerPrimero();
    }

    /**
     * Reinicia el recorrido al primer
     * elemento de la orientacion actual.
     *
     * No existe una posicion inicial fija
     * despues de rotar la lista.
     */
    public Notificacion obtenerActual(
            String idCliente) {

        return consultarActual(idCliente);
    }

    // ==========================================
    // CONTROL DE LECTURA
    // ==========================================

    /**
     * Marca una notificacion como leida.
     */
    public void marcarLeida(
            String idNotificacion) {

        Notificacion notificacion =
                obtenerNotificacion(idNotificacion);

        notificacion.marcarLeida();
    }

    /**
     * Marca una notificacion como no leida.
     */
    public void marcarNoLeida(
            String idNotificacion) {

        Notificacion notificacion =
                obtenerNotificacion(idNotificacion);

        notificacion.marcarNoLeida();
    }

    /**
     * Marca todas las notificaciones
     * de un cliente como leidas.
     *
     * Devuelve la cantidad que
     * estaba pendiente de lectura.
     */
    public int marcarTodasLeidas(
            String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaCircular<Notificacion> lista =
                obtenerLista(idCliente);

        if (lista == null) {
            return 0;
        }

        final int[] cantidad = {0};

        lista.recorrer(notificacion -> {

            if (!notificacion.isLeida()) {

                notificacion.marcarLeida();

                cantidad[0]++;
            }
        });

        return cantidad[0];
    }

    // ==========================================
    // ELIMINACION DE NOTIFICACIONES
    // ==========================================

    /**
     * Elimina una notificacion por ID.
     *
     * La elimina de la lista circular
     * y tambien del indice hash.
     *
     * Devuelve false si no existe.
     */
    public boolean eliminarNotificacion(
            String idNotificacion) {

        validarTexto(
                idNotificacion,
                "ID de la notificacion"
        );

        Notificacion notificacion =
                indiceNotificaciones.obtener(
                        idNotificacion
                );

        if (notificacion == null) {
            return false;
        }

        ListaCircular<Notificacion> lista =
                obtenerLista(
                        notificacion.getIdCliente()
                );

        if (lista == null) {
            throw new IllegalStateException(
                    "La notificacion no tiene lista asociada"
            );
        }

        boolean eliminada =
                lista.eliminarDato(notificacion);

        if (!eliminada) {
            throw new IllegalStateException(
                    "La notificacion no existe "
                            + "en su lista circular"
            );
        }

        indiceNotificaciones.eliminar(
                idNotificacion
        );

        totalNotificaciones--;

        if (lista.estaVacia()) {

            notificacionesPorCliente.eliminar(
                    notificacion.getIdCliente()
            );
        }

        return true;
    }

    /**
     * Elimina todas las notificaciones
     * de un cliente.
     *
     * Devuelve la cantidad eliminada.
     */
    public int eliminarTodas(
            String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaCircular<Notificacion> lista =
                obtenerLista(idCliente);

        if (lista == null) {
            return 0;
        }

        int cantidad = lista.tamanio();

        lista.recorrer(notificacion -> {

            indiceNotificaciones.eliminar(
                    notificacion.getId()
            );
        });

        notificacionesPorCliente.eliminar(
                idCliente
        );

        totalNotificaciones -= cantidad;

        return cantidad;
    }

    /**
     * Elimina solamente las
     * notificaciones leidas.
     *
     * Devuelve la cantidad eliminada.
     */
    public int eliminarLeidas(
            String idCliente) {

        ListaEnlazada<Notificacion> lista =
                listarNotificaciones(idCliente);

        final int[] cantidad = {0};

        lista.recorrer(notificacion -> {

            if (notificacion.isLeida()) {

                if (eliminarNotificacion(
                        notificacion.getId())) {

                    cantidad[0]++;
                }
            }
        });

        return cantidad[0];
    }

    // ==========================================
    // ESTADISTICAS
    // ==========================================

    /**
     * Total de notificaciones almacenadas
     * en el sistema.
     */
    public int cantidadNotificaciones() {

        return totalNotificaciones;
    }

    /**
     * Cantidad de notificaciones
     * de un cliente.
     */
    public int cantidadNotificacionesCliente(
            String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaCircular<Notificacion> lista =
                obtenerLista(idCliente);

        return lista == null
                ? 0
                : lista.tamanio();
    }

    /**
     * Cantidad de notificaciones
     * pendientes de lectura.
     */
    public int cantidadNoLeidas(
            String idCliente) {

        return listarNoLeidas(idCliente).tamanio();
    }

    /**
     * Cantidad de notificaciones urgentes.
     */
    public int cantidadUrgentes(
            String idCliente) {

        return listarUrgentes(idCliente).tamanio();
    }

    /**
     * Cantidad total de notificaciones
     * no leidas en todo el sistema.
     */
    public int totalNoLeidasSistema() {

        final int[] cantidad = {0};

        indiceNotificaciones.recorrer(
                (id, notificacion) -> {

                    if (!notificacion.isLeida()) {
                        cantidad[0]++;
                    }
                }
        );

        return cantidad[0];
    }

    /**
     * Cantidad de clientes que tienen
     * al menos una notificacion.
     */
    public int clientesConNotificaciones() {

        return notificacionesPorCliente.tamanio();
    }

    // ==========================================
    // VERIFICACION DE INTEGRIDAD
    // ==========================================

    /**
     * Comprueba:
     *
     * - Integridad basica de ClienteService.
     * - Correspondencia entre listas e indice.
     * - IDs unicos de notificaciones.
     * - Pertenencia al cliente correcto.
     * - Circularidad de las listas.
     * - Coincidencia de contadores.
     *
     * No comprueba que todas las operaciones
     * financieras tengan una notificacion.
     */
    public boolean verificarIntegridad() {

        if (!clienteService.verificarIntegridad()) {
            return false;
        }

        if (totalNotificaciones
                != indiceNotificaciones.tamanio()) {

            return false;
        }

        final boolean[] valido = {true};

        final int[] contadas = {0};

        TablaHash<String, Boolean> ids =
                new TablaHash<>();

        notificacionesPorCliente.recorrer(
                (idCliente, lista) -> {

                    if (clienteService.buscarCliente(
                            idCliente) == null) {

                        valido[0] = false;
                    }

                    if (lista == null
                            || lista.estaVacia()) {

                        valido[0] = false;
                        return;
                    }

                    if (!lista.verificarCircularidad()) {

                        valido[0] = false;
                    }

                    lista.recorrer(notificacion -> {

                        if (notificacion == null) {

                            valido[0] = false;
                            return;
                        }

                        contadas[0]++;

                        if (!idCliente.equals(
                                notificacion.getIdCliente())) {

                            valido[0] = false;
                        }

                        if (ids.contieneClave(
                                notificacion.getId())) {

                            valido[0] = false;

                        } else {

                            ids.insertar(
                                    notificacion.getId(),
                                    true
                            );
                        }

                        if (indiceNotificaciones.obtener(
                                notificacion.getId()
                        ) != notificacion) {

                            valido[0] = false;
                        }
                    });
                }
        );

        if (contadas[0] != totalNotificaciones) {
            valido[0] = false;
        }

        indiceNotificaciones.recorrer(
                (id, notificacion) -> {

                    if (notificacion == null
                            || !ids.contieneClave(id)) {

                        valido[0] = false;
                    }
                }
        );

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

    private void validarMonto(BigDecimal monto) {

        if (monto == null
                || monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser positivo"
            );
        }

        try {

            monto.setScale(
                    2,
                    java.math.RoundingMode.UNNECESSARY
            );

        } catch (ArithmeticException error) {

            throw new IllegalArgumentException(
                    "El monto tiene demasiados decimales"
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
                "CENTRO DE NOTIFICACIONES\n"
        );

        resultado.append(
                "==============================\n"
        );

        clienteService.recorrerClientes(cliente -> {

            resultado.append(
                            "\nCliente: "
                    ).append(cliente.getNombre())
                    .append(" (")
                    .append(cliente.getId())
                    .append(")\n");

            ListaCircular<Notificacion> lista =
                    obtenerLista(cliente.getId());

            if (lista == null || lista.estaVacia()) {

                resultado.append(
                        "Sin notificaciones\n"
                );

            } else {

                lista.recorrer(notificacion -> {

                    resultado.append(
                            notificacion.getResumen()
                    ).append("\n");
                });
            }
        });

        resultado.append(
                "\nTotal de notificaciones: "
        ).append(totalNotificaciones);

        return resultado.toString();
    }
}
