
package modelo;

import estructuras.ListaEnlazada;

import java.math.BigDecimal;

/**
 * Representa un cliente del sistema Monedero Virtual.
 *
 * Cada cliente puede tener multiples monederos,
 * acumular puntos y pertenecer a un rango.
 *
 * Utiliza nuestra ListaEnlazada propia para
 * almacenar los monederos del cliente.
 *
 * La gestion de las transacciones financieras
 * sera responsabilidad de los servicios.
 */
public class Cliente {

    private final String id;

    private String nombre;
    private String correo;

    private final ListaEnlazada<Monedero> monederos;

    private int puntos;
    private Rango rango;

    private boolean activo;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    /**
     * Constructor principal del cliente.
     */
    public Cliente(
            String id,
            String nombre,
            String correo) {

        validarTexto(id, "ID del cliente");
        validarTexto(nombre, "Nombre del cliente");
        validarCorreo(correo);

        this.id = id;
        this.nombre = nombre;
        this.correo = correo;

        this.monederos = new ListaEnlazada<>();

        this.puntos = 0;
        this.rango = Rango.BRONCE;

        this.activo = true;
    }

    /**
     * Constructor simplificado.
     */
    public Cliente(String id, String nombre) {

        this(
                id,
                nombre,
                "sin-correo@monedero.local"
        );
    }

    // ==========================================
    // METODOS GET
    // ==========================================

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public int getPuntos() {
        return puntos;
    }

    public Rango getRango() {
        return rango;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getCantidadMonederos() {
        return monederos.tamanio();
    }

    // ==========================================
    // MODIFICACION DE INFORMACION
    // ==========================================

    public void setNombre(String nombre) {

        validarTexto(nombre, "Nombre del cliente");

        this.nombre = nombre;
    }

    public void setCorreo(String correo) {

        validarCorreo(correo);

        this.correo = correo;
    }

    // ==========================================
    // GESTION DE MONEDEROS
    // ==========================================

    /**
     * Agrega un monedero al cliente.
     *
     * Verifica que:
     * - El cliente este activo.
     * - El monedero no sea null.
     * - El monedero pertenezca al cliente.
     * - No exista otro con el mismo ID.
     */
    public void agregarMonedero(Monedero monedero) {

        validarActivo();

        if (monedero == null) {
            throw new IllegalArgumentException(
                    "El monedero no puede ser null"
            );
        }

        if (!monedero.getIdCliente().equals(id)) {
            throw new IllegalArgumentException(
                    "El monedero no pertenece al cliente"
            );
        }

        if (buscarMonedero(monedero.getId()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un monedero con ID: "
                            + monedero.getId()
            );
        }

        monederos.agregarFinal(monedero);
    }

    /**
     * Busca un monedero por identificador.
     *
     * Devuelve null si no existe.
     */
    public Monedero buscarMonedero(String idMonedero) {

        validarTexto(
                idMonedero,
                "ID del monedero"
        );

        for (int i = 0; i < monederos.tamanio(); i++) {

            Monedero actual = monederos.obtener(i);

            if (actual.getId().equals(idMonedero)) {
                return actual;
            }
        }

        return null;
    }

    /**
     * Verifica si existe un monedero.
     */
    public boolean contieneMonedero(String idMonedero) {

        return buscarMonedero(idMonedero) != null;
    }

    /**
     * Elimina un monedero por su identificador.
     *
     * Por seguridad solamente permite eliminar
     * monederos con saldo igual a cero.
     *
     * Devuelve true si fue eliminado.
     */
    public boolean eliminarMonedero(String idMonedero) {

        validarActivo();

        validarTexto(
                idMonedero,
                "ID del monedero"
        );

        for (int i = 0; i < monederos.tamanio(); i++) {

            Monedero actual = monederos.obtener(i);

            if (actual.getId().equals(idMonedero)) {

                if (actual.getSaldo().compareTo(
                        BigDecimal.ZERO) != 0) {

                    throw new IllegalStateException(
                            "No se puede eliminar un monedero "
                                    + "con saldo disponible"
                    );
                }

                monederos.eliminar(i);

                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve el monedero principal.
     *
     * Identifica el primero cuyo tipo
     * sea PRINCIPAL.
     */
    public Monedero getMonederoPrincipal() {

        for (int i = 0; i < monederos.tamanio(); i++) {

            Monedero actual = monederos.obtener(i);

            if (actual.getTipo().equalsIgnoreCase(
                    "PRINCIPAL")) {

                return actual;
            }
        }

        return null;
    }

    /**
     * Devuelve el saldo total de todos
     * los monederos del cliente.
     */
    public BigDecimal consultarSaldoTotal() {

        final BigDecimal[] total = {
                BigDecimal.ZERO
        };

        monederos.recorrer(monedero -> {

            total[0] = total[0].add(
                    monedero.getSaldo()
            );
        });

        return total[0].setScale(2);
    }

    /**
     * Devuelve una copia de la lista
     * de referencias a los monederos.
     *
     * La lista es independiente, pero
     * los objetos Monedero son compartidos.
     */
    public ListaEnlazada<Monedero> getMonederos() {

        ListaEnlazada<Monedero> copia =
                new ListaEnlazada<>();

        monederos.recorrer(copia::agregarFinal);

        return copia;
    }

    // ==========================================
    // SISTEMA DE PUNTOS
    // ==========================================

    /**
     * Agrega puntos al cliente y actualiza
     * automaticamente su rango.
     */
    public void agregarPuntos(int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "Los puntos a agregar deben ser positivos"
            );
        }

        puntos = Math.addExact(puntos, cantidad);

        actualizarRango();
    }

    /**
     * Descuenta puntos del cliente.
     *
     * Se utilizara para canjes y reversiones.
     */
    public void descontarPuntos(int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "Los puntos a descontar deben ser positivos"
            );
        }

        if (puntos < cantidad) {
            throw new IllegalArgumentException(
                    "El cliente no tiene puntos suficientes"
            );
        }

        puntos -= cantidad;

        actualizarRango();
    }

    /**
     * Verifica si el cliente tiene puntos
     * suficientes para un canje.
     */
    public boolean tienePuntosSuficientes(int cantidad) {

        return cantidad > 0 && puntos >= cantidad;
    }

    /**
     * Calcula el rango de acuerdo con
     * los puntos acumulados.
     */
    private void actualizarRango() {

        rango = Rango.calcularRango(puntos);
    }

    /**
     * Devuelve los puntos necesarios
     * para alcanzar el siguiente rango.
     */
    public int puntosParaSiguienteRango() {

        return rango.puntosParaSiguienteRango(puntos);
    }

    // ==========================================
    // ESTADO DEL CLIENTE
    // ==========================================

    /**
     * Desactiva al cliente.
     *
     * Los servicios deberan comprobar este
     * estado antes de ejecutar operaciones.
     */
    public void desactivar() {
        activo = false;
    }

    /**
     * Reactiva al cliente.
     */
    public void activar() {
        activo = true;
    }

    private void validarActivo() {

        if (!activo) {
            throw new IllegalStateException(
                    "El cliente " + id
                            + " se encuentra inactivo"
            );
        }
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

    /**
     * Validacion basica del correo.
     * No pretende validar todos los formatos
     * posibles de direcciones electronicas.
     */
    private void validarCorreo(String correo) {

        if (correo == null ||
                correo.isBlank() ||
                !correo.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
                )) {

            throw new IllegalArgumentException(
                    "El correo electronico no es valido"
            );
        }
    }

    // ==========================================
    // REPRESENTACION TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return "Cliente{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", monederos=" + monederos.tamanio() +
                ", puntos=" + puntos +
                ", rango=" + rango +
                ", saldoTotal=" + consultarSaldoTotal() +
                ", activo=" + activo +
                '}';
    }
}
