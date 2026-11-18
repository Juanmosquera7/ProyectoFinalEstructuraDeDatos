
package servicios;

import estructuras.ListaEnlazada;
import estructuras.TablaHash;

import modelo.Beneficio;
import modelo.Cliente;
import modelo.Rango;
import modelo.TipoTransaccion;
import modelo.Transaccion;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Servicio encargado de administrar el sistema
 * de puntos y beneficios del Monedero Virtual.
 *
 * REGLAS:
 *
 * DEPOSITO:
 * 1 punto por cada 100 unidades monetarias.
 *
 * RETIRO:
 * 2 puntos por cada 100 unidades monetarias.
 *
 * TRANSFERENCIA:
 * 3 puntos por cada 100 unidades monetarias.
 *
 * BENEFICIOS:
 * 100 puntos: descuento en transferencia.
 * 500 puntos: retiros sin comision.
 * 1000 puntos: bono de saldo.
 *
 * ESTRUCTURAS PROPIAS:
 *
 * TablaHash:
 * - Transacciones procesadas.
 * - Puntos otorgados por transaccion.
 * - Beneficios asociados a clientes.
 *
 * ListaEnlazada:
 * - Historial de movimientos de puntos.
 * - Listados de beneficios.
 *
 * No utiliza colecciones de Java.
 */
public class PuntosService {

    // ==========================================
    // TIPOS DE MOVIMIENTO DE PUNTOS
    // ==========================================

    public enum TipoMovimiento {

        ACUMULACION,
        CANJE,
        REVERSION,
        AJUSTE
    }

    // ==========================================
    // REGISTRO DE MOVIMIENTOS
    // ==========================================

    /**
     * Registro inmutable de un movimiento
     * realizado sobre los puntos de un cliente.
     */
    public static class MovimientoPuntos {

        private final String idCliente;
        private final TipoMovimiento tipo;
        private final int cantidad;
        private final int saldoPosterior;
        private final String referencia;
        private final LocalDateTime fecha;

        public MovimientoPuntos(
                String idCliente,
                TipoMovimiento tipo,
                int cantidad,
                int saldoPosterior,
                String referencia) {

            this.idCliente = idCliente;
            this.tipo = tipo;
            this.cantidad = cantidad;
            this.saldoPosterior = saldoPosterior;
            this.referencia = referencia;
            this.fecha = LocalDateTime.now();
        }

        public String getIdCliente() {
            return idCliente;
        }

        public TipoMovimiento getTipo() {
            return tipo;
        }

        public int getCantidad() {
            return cantidad;
        }

        public int getSaldoPosterior() {
            return saldoPosterior;
        }

        public String getReferencia() {
            return referencia;
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        @Override
        public String toString() {

            return "MovimientoPuntos{" +
                    "cliente='" + idCliente + '\'' +
                    ", tipo=" + tipo +
                    ", cantidad=" + cantidad +
                    ", saldoPosterior=" + saldoPosterior +
                    ", referencia='" + referencia + '\'' +
                    ", fecha=" + fecha +
                    '}';
        }
    }

    // ==========================================
    // ATRIBUTOS
    // ==========================================

    private final ClienteService clienteService;

    /**
     * Impide procesar dos veces la misma
     * transaccion financiera.
     *
     * Una transaccion revertida sigue
     * marcada como procesada.
     */
    private final TablaHash<String, Boolean>
            transaccionesProcesadas;

    /**
     * Guarda los puntos netos asociados
     * a cada transaccion.
     *
     * Tras una reversion exitosa, su
     * valor se actualiza a cero.
     */
    private final TablaHash<String, Integer>
            puntosPorTransaccion;

    /**
     * Relaciona cada cliente con una
     * lista propia de beneficios.
     */
    private final TablaHash<
            String,
            ListaEnlazada<Beneficio>
            > beneficiosPorCliente;

    /**
     * Permite localizar un beneficio
     * utilizando su identificador.
     */
    private final TablaHash<String, Beneficio>
            indiceBeneficios;

    /**
     * Conserva los movimientos de puntos
     * en orden cronologico.
     */
    private final ListaEnlazada<MovimientoPuntos>
            historialPuntos;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PuntosService(
            ClienteService clienteService) {

        this.clienteService = Objects.requireNonNull(
                clienteService,
                "ClienteService no puede ser null"
        );

        this.transaccionesProcesadas =
                new TablaHash<>();

        this.puntosPorTransaccion =
                new TablaHash<>();

        this.beneficiosPorCliente =
                new TablaHash<>();

        this.indiceBeneficios =
                new TablaHash<>();

        this.historialPuntos =
                new ListaEnlazada<>();
    }

    // ==========================================
    // CONSULTA DE PUNTOS
    // ==========================================

    /**
     * Consulta los puntos actuales de
     * un cliente registrado.
     */
    public int consultarPuntos(String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        return cliente.getPuntos();
    }

    /**
     * Consulta el rango actual.
     */
    public Rango consultarRango(String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        return cliente.getRango();
    }

    /**
     * Comprueba si un cliente dispone
     * de una cantidad de puntos.
     */
    public boolean tienePuntosSuficientes(
            String idCliente,
            int cantidad) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        return cliente.tienePuntosSuficientes(
                cantidad
        );
    }

    /**
     * Devuelve los puntos que faltan
     * para llegar al siguiente rango.
     */
    public int puntosParaSiguienteRango(
            String idCliente) {

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        return cliente.puntosParaSiguienteRango();
    }

    // ==========================================
    // CALCULO DE PUNTOS
    // ==========================================

    /**
     * Calcula los puntos base de una
     * transaccion sin otorgarlos.
     *
     * Puede utilizarse para mostrar una
     * vista previa antes de operar.
     */
    public int calcularPuntos(
            Transaccion transaccion) {

        Objects.requireNonNull(
                transaccion,
                "La transaccion no puede ser null"
        );

        return transaccion.calcularPuntosBase();
    }

    /**
     * Determina el cliente beneficiario.
     *
     * DEPOSITO:
     * Recibe puntos el propietario del
     * monedero de destino.
     *
     * RETIRO:
     * Recibe puntos quien retira dinero.
     *
     * TRANSFERENCIA:
     * Recibe puntos el cliente emisor.
     *
     * Otros tipos no otorgan puntos.
     */
    private String obtenerClienteBeneficiario(
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
    // OTORGAMIENTO DE PUNTOS
    // ==========================================

    /**
     * Otorga puntos por una transaccion
     * financiera completada.
     *
     * Verifica:
     * - Que la transaccion este completada.
     * - Que no haya sido procesada antes.
     * - Que exista el cliente beneficiario.
     * - Que los puntos sean validos.
     *
     * Devuelve los puntos otorgados.
     *
     * Una transaccion de menos de 100
     * unidades puede otorgar cero puntos,
     * pero igualmente queda procesada.
     */
    public int registrarPuntosPorTransaccion(
            Transaccion transaccion) {

        Objects.requireNonNull(
                transaccion,
                "La transaccion no puede ser null"
        );

        if (!transaccion.estaCompletada()) {

            throw new IllegalStateException(
                    "Solo se pueden otorgar puntos "
                            + "por transacciones completadas"
            );
        }

        String idTransaccion = transaccion.getId();

        if (transaccionesProcesadas.contieneClave(
                idTransaccion)) {

            throw new IllegalStateException(
                    "La transaccion ya fue procesada "
                            + "en el sistema de puntos"
            );
        }

        if (transaccion.getPuntosGenerados() != 0) {

            throw new IllegalStateException(
                    "La transaccion ya contiene "
                            + "puntos registrados"
            );
        }

        int puntos = transaccion.calcularPuntosBase();

        String idCliente =
                obtenerClienteBeneficiario(transaccion);

        // Las operaciones que no generan puntos
        // tambien deben marcarse como procesadas.
        if (!transaccion.getTipo().generaPuntos()) {

            transaccion.registrarPuntosGenerados(0);

            marcarTransaccionProcesada(
                    idTransaccion,
                    0
            );

            return 0;
        }

        if (idCliente == null
                || idCliente.isBlank()) {

            throw new IllegalStateException(
                    "La transaccion no tiene "
                            + "un cliente beneficiario"
            );
        }

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        if (puntos > 0) {

            // Validar desbordamiento antes
            // de modificar cualquier dato.
            Math.addExact(
                    cliente.getPuntos(),
                    puntos
            );
        }

        // Primero registrar los puntos en
        // el objeto Transaccion.
        transaccion.registrarPuntosGenerados(
                puntos
        );

        if (puntos > 0) {

            cliente.agregarPuntos(puntos);

            registrarMovimiento(
                    idCliente,
                    TipoMovimiento.ACUMULACION,
                    puntos,
                    cliente.getPuntos(),
                    idTransaccion
            );
        }

        marcarTransaccionProcesada(
                idTransaccion,
                puntos
        );

        return puntos;
    }

    /**
     * Marca una transaccion como procesada
     * en nuestras tablas hash.
     */
    private void marcarTransaccionProcesada(
            String idTransaccion,
            int puntos) {

        transaccionesProcesadas.insertar(
                idTransaccion,
                true
        );

        puntosPorTransaccion.insertar(
                idTransaccion,
                puntos
        );
    }

    /**
     * Verifica si ya se procesaron los
     * puntos de una transaccion.
     */
    public boolean transaccionProcesada(
            String idTransaccion) {

        validarTexto(
                idTransaccion,
                "ID de la transaccion"
        );

        return transaccionesProcesadas.contieneClave(
                idTransaccion
        );
    }

    /**
     * Consulta los puntos netos otorgados
     * por una transaccion.
     *
     * Devuelve cero si no existe registro.
     */
    public int consultarPuntosTransaccion(
            String idTransaccion) {

        validarTexto(
                idTransaccion,
                "ID de la transaccion"
        );

        Integer puntos =
                puntosPorTransaccion.obtener(
                        idTransaccion
                );

        return puntos == null ? 0 : puntos;
    }

    // ==========================================
    // REVERSION DE PUNTOS
    // ==========================================

    /**
     * Comprueba si los puntos de una
     * transaccion pueden compensarse.
     *
     * Si los puntos ya fueron gastados,
     * la compensacion no se permite.
     */
    public boolean puedeRevertirPuntos(
            Transaccion transaccion) {

        Objects.requireNonNull(
                transaccion,
                "La transaccion no puede ser null"
        );

        if (!transaccionesProcesadas.contieneClave(
                transaccion.getId())) {

            return true;
        }

        int puntos = consultarPuntosTransaccion(
                transaccion.getId()
        );

        if (puntos <= 0) {
            return true;
        }

        String idCliente =
                obtenerClienteBeneficiario(transaccion);

        if (idCliente == null) {
            return false;
        }

        Cliente cliente =
                clienteService.buscarCliente(idCliente);

        return cliente != null
                && cliente.getPuntos() >= puntos;
    }

    /**
     * Compensa los puntos de una
     * transaccion previamente procesada.
     *
     * El historial de procesamiento se
     * conserva para impedir duplicaciones.
     *
     * Puede utilizarse cuando la operacion
     * ya esta marcada como REVERTIDA.
     *
     * IMPORTANTE:
     * La coordinacion con la reversion
     * monetaria sera responsabilidad
     * de la integracion financiera.
     */
    public int revertirPuntosDeTransaccion(
            Transaccion transaccion) {

        Objects.requireNonNull(
                transaccion,
                "La transaccion no puede ser null"
        );

        if (!transaccion.estaCompletada()
                && !transaccion.estaRevertida()) {

            throw new IllegalStateException(
                    "La transaccion no admite "
                            + "compensacion de puntos"
            );
        }

        String idTransaccion = transaccion.getId();

        if (!transaccionesProcesadas.contieneClave(
                idTransaccion)) {

            throw new IllegalStateException(
                    "La transaccion no tiene "
                            + "puntos procesados"
            );
        }

        int puntos = consultarPuntosTransaccion(
                idTransaccion
        );

        if (puntos == 0) {
            return 0;
        }

        String idCliente =
                obtenerClienteBeneficiario(transaccion);

        Cliente cliente =
                clienteService.obtenerCliente(idCliente);

        if (cliente.getPuntos() < puntos) {

            throw new IllegalStateException(
                    "No es posible revertir "
                            + "los puntos porque ya fueron gastados"
            );
        }

        cliente.descontarPuntos(puntos);

        puntosPorTransaccion.insertar(
                idTransaccion,
                0
        );

        registrarMovimiento(
                idCliente,
                TipoMovimiento.REVERSION,
                -puntos,
                cliente.getPuntos(),
                idTransaccion
        );

        return puntos;
    }

    // ==========================================
    // AJUSTES ADMINISTRATIVOS
    // ==========================================

    /**
     * Permite agregar puntos manualmente
     * para ajustes administrativos.
     *
     * No debe usarse para reemplazar
     * el procesamiento de transacciones.
     */
    public void agregarPuntosManuales(
            String idCliente,
            int cantidad,
            String motivo) {

        validarCantidadPositiva(cantidad);
        validarTexto(motivo, "Motivo");

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        cliente.agregarPuntos(cantidad);

        registrarMovimiento(
                idCliente,
                TipoMovimiento.AJUSTE,
                cantidad,
                cliente.getPuntos(),
                motivo
        );
    }

    /**
     * Descuenta puntos por un ajuste
     * administrativo autorizado.
     */
    public void descontarPuntosManuales(
            String idCliente,
            int cantidad,
            String motivo) {

        validarCantidadPositiva(cantidad);
        validarTexto(motivo, "Motivo");

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        cliente.descontarPuntos(cantidad);

        registrarMovimiento(
                idCliente,
                TipoMovimiento.AJUSTE,
                -cantidad,
                cliente.getPuntos(),
                motivo
        );
    }

    // ==========================================
    // CANJE DE BENEFICIOS
    // ==========================================

    /**
     * Canjea puntos por un beneficio.
     *
     * Verifica:
     * - Cliente activo.
     * - Tipo valido.
     * - Puntos suficientes.
     *
     * Descuenta los puntos y activa
     * el beneficio adquirido.
     *
     * La aplicacion monetaria del beneficio
     * corresponde al servicio financiero.
     */
    public Beneficio canjearBeneficio(
            String idCliente,
            Beneficio.Tipo tipo) {

        if (tipo == null) {

            throw new IllegalArgumentException(
                    "El tipo de beneficio es obligatorio"
            );
        }

        Cliente cliente =
                clienteService.obtenerClienteActivo(
                        idCliente
                );

        int costo = tipo.getCostoPuntos();

        if (!cliente.tienePuntosSuficientes(costo)) {

            throw new IllegalArgumentException(
                    "Puntos insuficientes para canjear "
                            + tipo.getDescripcion()
            );
        }

        // Crear antes de descontar.
        Beneficio beneficio = new Beneficio(
                idCliente,
                tipo
        );

        beneficio.activar();

        ListaEnlazada<Beneficio> lista =
                beneficiosPorCliente.obtener(idCliente);

        if (lista == null) {

            lista = new ListaEnlazada<>();

            beneficiosPorCliente.insertar(
                    idCliente,
                    lista
            );
        }

        // Registrar beneficio adquirido.
        lista.agregarFinal(beneficio);

        indiceBeneficios.insertar(
                beneficio.getId(),
                beneficio
        );

        // Descontar despues de las validaciones.
        cliente.descontarPuntos(costo);

        registrarMovimiento(
                idCliente,
                TipoMovimiento.CANJE,
                -costo,
                cliente.getPuntos(),
                beneficio.getId()
        );

        return beneficio;
    }

    /**
     * Canjea el beneficio de descuento
     * del 10% en una transferencia.
     */
    public Beneficio canjearDescuentoTransferencia(
            String idCliente) {

        return canjearBeneficio(
                idCliente,
                Beneficio.Tipo.DESCUENTO_TRANSFERENCIA
        );
    }

    /**
     * Canjea retiros sin comision
     * durante 30 dias.
     */
    public Beneficio canjearRetirosSinComision(
            String idCliente) {

        return canjearBeneficio(
                idCliente,
                Beneficio.Tipo.RETIROS_SIN_COMISION
        );
    }

    /**
     * Canjea un bono de 50 unidades monetarias.
     *
     * El bono queda activo y pendiente
     * de aplicacion al monedero.
     *
     * Este metodo NO acredita dinero
     * directamente para evitar movimientos
     * financieros sin historial.
     */
    public Beneficio canjearBonoSaldo(
            String idCliente) {

        return canjearBeneficio(
                idCliente,
                Beneficio.Tipo.BONO_SALDO
        );
    }

    // ==========================================
    // CONSULTA DE BENEFICIOS
    // ==========================================

    /**
     * Busca un beneficio por su ID.
     *
     * Devuelve null si no existe.
     */
    public Beneficio buscarBeneficio(
            String idBeneficio) {

        validarTexto(
                idBeneficio,
                "ID del beneficio"
        );

        return indiceBeneficios.obtener(
                idBeneficio
        );
    }

    /**
     * Obtiene un beneficio obligatorio.
     */
    public Beneficio obtenerBeneficio(
            String idBeneficio) {

        Beneficio beneficio =
                buscarBeneficio(idBeneficio);

        if (beneficio == null) {

            throw new IllegalArgumentException(
                    "No existe el beneficio: "
                            + idBeneficio
            );
        }

        return beneficio;
    }

    /**
     * Devuelve una lista independiente
     * con los beneficios del cliente.
     *
     * Los objetos Beneficio conservan
     * sus referencias originales.
     */
    public ListaEnlazada<Beneficio> listarBeneficios(
            String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaEnlazada<Beneficio> resultado =
                new ListaEnlazada<>();

        ListaEnlazada<Beneficio> registrados =
                beneficiosPorCliente.obtener(idCliente);

        if (registrados != null) {

            registrados.recorrer(
                    resultado::agregarFinal
            );
        }

        return resultado;
    }

    /**
     * Devuelve los beneficios que se
     * encuentran activos.
     */
    public ListaEnlazada<Beneficio>
    listarBeneficiosActivos(String idCliente) {

        ListaEnlazada<Beneficio> resultado =
                new ListaEnlazada<>();

        listarBeneficios(idCliente).recorrer(
                beneficio -> {

                    if (beneficio.estaDisponibleParaUso()) {

                        resultado.agregarFinal(
                                beneficio
                        );
                    }
                }
        );

        return resultado;
    }

    /**
     * Busca el primer beneficio activo
     * de un tipo determinado.
     *
     * Devuelve null si no existe.
     */
    public Beneficio buscarBeneficioActivo(
            String idCliente,
            Beneficio.Tipo tipo) {

        Objects.requireNonNull(
                tipo,
                "El tipo no puede ser null"
        );

        final Beneficio[] resultado = {null};

        listarBeneficios(idCliente).recorrer(
                beneficio -> {

                    if (resultado[0] == null
                            && beneficio.getTipo() == tipo
                            && beneficio.estaDisponibleParaUso()) {

                        resultado[0] = beneficio;
                    }
                }
        );

        return resultado[0];
    }

    /**
     * Registra la utilizacion de un
     * beneficio previamente canjeado.
     *
     * Debe llamarse solamente despues
     * de aplicar exitosamente su efecto.
     */
    public void registrarUsoBeneficio(
            String idCliente,
            String idBeneficio) {

        clienteService.obtenerClienteActivo(
                idCliente
        );

        Beneficio beneficio =
                obtenerBeneficio(idBeneficio);

        if (!beneficio.getIdCliente().equals(idCliente)) {

            throw new IllegalArgumentException(
                    "El beneficio no pertenece "
                            + "al cliente indicado"
            );
        }

        beneficio.utilizar();
    }

    // ==========================================
    // HISTORIAL DE PUNTOS
    // ==========================================

    /**
     * Agrega un movimiento al historial.
     */
    private void registrarMovimiento(
            String idCliente,
            TipoMovimiento tipo,
            int cantidad,
            int saldoPosterior,
            String referencia) {

        MovimientoPuntos movimiento =
                new MovimientoPuntos(
                        idCliente,
                        tipo,
                        cantidad,
                        saldoPosterior,
                        referencia
                );

        historialPuntos.agregarFinal(movimiento);
    }

    /**
     * Devuelve una copia de la lista
     * completa de movimientos.
     */
    public ListaEnlazada<MovimientoPuntos>
    obtenerHistorialPuntos() {

        ListaEnlazada<MovimientoPuntos> resultado =
                new ListaEnlazada<>();

        historialPuntos.recorrer(
                resultado::agregarFinal
        );

        return resultado;
    }

    /**
     * Obtiene los movimientos de puntos
     * de un cliente especifico.
     */
    public ListaEnlazada<MovimientoPuntos>
    obtenerHistorialCliente(String idCliente) {

        clienteService.obtenerCliente(idCliente);

        ListaEnlazada<MovimientoPuntos> resultado =
                new ListaEnlazada<>();

        historialPuntos.recorrer(movimiento -> {

            if (movimiento.getIdCliente().equals(
                    idCliente)) {

                resultado.agregarFinal(movimiento);
            }
        });

        return resultado;
    }

    // ==========================================
    // ESTADISTICAS
    // ==========================================

    /**
     * Cantidad de transacciones procesadas
     * por el sistema de puntos.
     */
    public int cantidadTransaccionesProcesadas() {

        return transaccionesProcesadas.tamanio();
    }

    /**
     * Cantidad de beneficios canjeados.
     */
    public int cantidadBeneficiosCanjeados() {

        return indiceBeneficios.tamanio();
    }

    /**
     * Cantidad total de movimientos
     * registrados en el historial.
     */
    public int cantidadMovimientosPuntos() {

        return historialPuntos.tamanio();
    }

    /**
     * Suma los puntos actuales de
     * todos los clientes registrados.
     *
     * Utiliza long para evitar desbordamiento
     * al sumar cantidades de varios clientes.
     */
    public long totalPuntosSistema() {

        final long[] total = {0L};

        clienteService.recorrerClientes(cliente -> {

            total[0] = Math.addExact(
                    total[0],
                    cliente.getPuntos()
            );
        });

        return total[0];
    }

    /**
     * Cuenta los clientes que pertenecen
     * a un rango determinado.
     */
    public int cantidadClientesPorRango(
            Rango rango) {

        Objects.requireNonNull(
                rango,
                "El rango no puede ser null"
        );

        final int[] cantidad = {0};

        clienteService.recorrerClientes(cliente -> {

            if (cliente.getRango() == rango) {
                cantidad[0]++;
            }
        });

        return cantidad[0];
    }

    // ==========================================
    // VERIFICACION DE INTEGRIDAD
    // ==========================================

    /**
     * Verifica la consistencia basica
     * del sistema de puntos.
     *
     * Comprueba:
     * - Puntos no negativos.
     * - Rangos correspondientes.
     * - Indices de transacciones coherentes.
     * - Beneficios asociados al cliente correcto.
     * - Coincidencia entre listas e indice.
     *
     * No reconstruye todos los puntos
     * historicos ni audita saldos financieros.
     */
    public boolean verificarIntegridad() {

        if (!clienteService.verificarIntegridad()) {
            return false;
        }

        if (transaccionesProcesadas.tamanio()
                != puntosPorTransaccion.tamanio()) {

            return false;
        }

        final boolean[] valido = {true};

        clienteService.recorrerClientes(cliente -> {

            if (cliente.getPuntos() < 0) {
                valido[0] = false;
            }

            if (cliente.getRango()
                    != Rango.calcularRango(
                    cliente.getPuntos()
            )) {

                valido[0] = false;
            }
        });

        transaccionesProcesadas.recorrer(
                (id, procesada) -> {

                    if (!Boolean.TRUE.equals(procesada)) {
                        valido[0] = false;
                    }

                    Integer puntos =
                            puntosPorTransaccion.obtener(id);

                    if (puntos == null || puntos < 0) {
                        valido[0] = false;
                    }
                }
        );

        puntosPorTransaccion.recorrer(
                (id, puntos) -> {

                    if (!transaccionesProcesadas
                            .contieneClave(id)) {

                        valido[0] = false;
                    }
                }
        );

        final int[] beneficiosContados = {0};

        beneficiosPorCliente.recorrer(
                (idCliente, lista) -> {

                    if (clienteService.buscarCliente(
                            idCliente) == null) {

                        valido[0] = false;
                    }

                    if (lista == null) {
                        valido[0] = false;
                        return;
                    }

                    lista.recorrer(beneficio -> {

                        if (beneficio == null) {
                            valido[0] = false;
                            return;
                        }

                        beneficiosContados[0]++;

                        if (!beneficio.getIdCliente()
                                .equals(idCliente)) {

                            valido[0] = false;
                        }

                        if (indiceBeneficios.obtener(
                                beneficio.getId()
                        ) != beneficio) {

                            valido[0] = false;
                        }
                    });
                }
        );

        if (beneficiosContados[0]
                != indiceBeneficios.tamanio()) {

            valido[0] = false;
        }

        historialPuntos.recorrer(movimiento -> {

            if (movimiento == null) {
                valido[0] = false;
                return;
            }

            if (clienteService.buscarCliente(
                    movimiento.getIdCliente()) == null) {

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

    private void validarCantidadPositiva(int cantidad) {

        if (cantidad <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad de puntos "
                            + "debe ser positiva"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return "PuntosService{" +
                "transaccionesProcesadas="
                + cantidadTransaccionesProcesadas() +
                ", beneficiosCanjeados="
                + cantidadBeneficiosCanjeados() +
                ", movimientos="
                + cantidadMovimientosPuntos() +
                ", puntosSistema="
                + totalPuntosSistema() +
                '}';
    }
}
