
package pruebas;

import estructuras.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

/**
 * Pruebas automaticas de las estructuras propias
 * del proyecto MonederoVirtual.
 *
 * No utiliza JUnit ni bibliotecas externas.
 *
 * Cada grupo verifica operaciones fundamentales,
 * casos limite e integridad estructural.
 */
public class PruebaEstructuras {

    private static int aprobadas = 0;
    private static int fallidas = 0;
    private static int gruposAprobados = 0;
    private static int gruposFallidos = 0;

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println(" MONEDERO VIRTUAL");
        System.out.println(" PRUEBAS DE ESTRUCTURAS DE DATOS");
        System.out.println("====================================");

        ejecutarGrupo("Nodo", PruebaEstructuras::probarNodo);

        ejecutarGrupo("NodoDoble",
                PruebaEstructuras::probarNodoDoble);

        ejecutarGrupo("ListaEnlazada",
                PruebaEstructuras::probarListaEnlazada);

        ejecutarGrupo("Pila",
                PruebaEstructuras::probarPila);

        ejecutarGrupo("Cola",
                PruebaEstructuras::probarCola);

        ejecutarGrupo("ColaPrioridad",
                PruebaEstructuras::probarColaPrioridad);

        ejecutarGrupo("ListaCircular",
                PruebaEstructuras::probarListaCircular);

        ejecutarGrupo("TablaHash",
                PruebaEstructuras::probarTablaHash);

        ejecutarGrupo("ArbolBinario",
                PruebaEstructuras::probarArbolBinario);

        ejecutarGrupo("ArbolAVL",
                PruebaEstructuras::probarArbolAVL);

        ejecutarGrupo("GrafoDirigido",
                PruebaEstructuras::probarGrafoDirigido);

        mostrarResumen();

        if (fallidas > 0 || gruposFallidos > 0) {
            throw new AssertionError(
                    "Existen pruebas fallidas"
            );
        }
    }

    // ==========================================
    // SISTEMA GENERAL DE PRUEBAS
    // ==========================================

    private static void ejecutarGrupo(
            String nombre, Runnable prueba) {

        System.out.println("\n------------------------------------");
        System.out.println("ESTRUCTURA: " + nombre);
        System.out.println("------------------------------------");

        int fallidasAntes = fallidas;

        try {
            prueba.run();

            if (fallidas == fallidasAntes) {
                gruposAprobados++;
                System.out.println("RESULTADO: APROBADO");
            } else {
                gruposFallidos++;
                System.out.println("RESULTADO: FALLIDO");
            }

        } catch (Throwable error) {

            gruposFallidos++;
            fallidas++;

            System.out.println(
                    "[ERROR] " + error.getClass().getSimpleName()
                            + ": " + error.getMessage()
            );

            error.printStackTrace(System.out);
        }
    }

    private static void verificar(
            String descripcion, boolean condicion) {

        if (condicion) {
            aprobadas++;
            System.out.println("[OK] " + descripcion);
        } else {
            fallidas++;
            System.out.println("[FALLO] " + descripcion);
        }
    }

    private static void verificarIgual(
            String descripcion,
            Object esperado,
            Object obtenido) {

        boolean correcto = Objects.equals(
                esperado, obtenido
        );

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
                        "   Excepcion inesperada: " + error
                );
            }
        }
    }

    // ==========================================
    // 1. NODO
    // ==========================================

    private static void probarNodo() {

        Nodo<Integer> primero = new Nodo<>(10);
        Nodo<Integer> segundo = new Nodo<>(20);

        verificarIgual(
                "Dato inicial", 10, primero.getDato()
        );

        verificar(
                "Siguiente inicialmente null",
                primero.getSiguiente() == null
        );

        primero.setSiguiente(segundo);

        verificar(
                "Enlace entre nodos",
                primero.getSiguiente() == segundo
        );

        primero.setDato(15);

        verificarIgual(
                "Modificar dato", 15, primero.getDato()
        );
    }

    // ==========================================
    // 2. NODO DOBLE
    // ==========================================

    private static void probarNodoDoble() {

        NodoDoble<String> a = new NodoDoble<>("A");
        NodoDoble<String> b = new NodoDoble<>("B");

        a.setSiguiente(b);
        b.setAnterior(a);

        verificar("A es primero", a.esPrimero());
        verificar("B es ultimo", b.esUltimo());

        verificar(
                "Enlace hacia adelante",
                a.getSiguiente() == b
        );

        verificar(
                "Enlace hacia atras",
                b.getAnterior() == a
        );

        b.setDato("C");

        verificarIgual(
                "Modificar dato", "C", b.getDato()
        );

        b.desvincular();

        verificar(
                "Desvincular referencias de B",
                b.getAnterior() == null
                        && b.getSiguiente() == null
        );
    }

    // ==========================================
    // 3. LISTA ENLAZADA
    // ==========================================

    private static void probarListaEnlazada() {

        ListaEnlazada<Integer> lista =
                new ListaEnlazada<>();

        verificar("Lista inicialmente vacia",
                lista.estaVacia());

        lista.agregarFinal(10);
        lista.agregarFinal(20);
        lista.agregarInicio(5);

        verificarIgual(
                "Orden de insercion",
                "[5, 10, 20]",
                lista.toString()
        );

        verificarIgual(
                "Tamanio", 3, lista.tamanio()
        );

        lista.insertar(2, 15);

        verificarIgual(
                "Insercion intermedia",
                "[5, 10, 15, 20]",
                lista.toString()
        );

        verificarIgual(
                "Buscar elemento", 2, lista.buscar(15)
        );

        verificar(
                "Contiene elemento", lista.contiene(20)
        );

        lista.modificar(1, 12);

        verificarIgual(
                "Modificar elemento", 12, lista.obtener(1)
        );

        verificarIgual(
                "Eliminar inicio", 5, lista.eliminarInicio()
        );

        verificarIgual(
                "Eliminar final", 20, lista.eliminarFinal()
        );

        verificarIgual(
                "Eliminar por indice", 15, lista.eliminar(1)
        );

        lista.agregarFinal(30);
        lista.agregarFinal(40);

        lista.invertir();

        verificarIgual(
                "Invertir lista",
                "[40, 30, 12]",
                lista.toString()
        );

        verificarIgual(
                "Primer elemento", 40,
                lista.obtenerPrimero()
        );

        verificarIgual(
                "Ultimo elemento", 12,
                lista.obtenerUltimo()
        );

        verificarExcepcion(
                "Indice fuera de rango",
                IndexOutOfBoundsException.class,
                () -> lista.obtener(100)
        );

        lista.limpiar();

        verificar("Limpiar lista", lista.estaVacia());
    }

    // ==========================================
    // 4. PILA
    // ==========================================

    private static void probarPila() {

        Pila<String> pila = new Pila<>();

        verificar(
                "Pila inicialmente vacia",
                pila.estaVacia()
        );

        pila.apilar("Deposito");
        pila.apilar("Retiro");
        pila.apilar("Transferencia");

        verificarIgual(
                "Tamanio", 3, pila.tamanio()
        );

        verificarIgual(
                "Consultar cima",
                "Transferencia", pila.cima()
        );

        verificarIgual(
                "Buscar desde la cima",
                2, pila.buscar("Retiro")
        );

        verificarIgual(
                "Desapilar transferencia",
                "Transferencia", pila.desapilar()
        );

        verificarIgual(
                "Desapilar retiro",
                "Retiro", pila.desapilar()
        );

        verificarIgual(
                "Desapilar deposito",
                "Deposito", pila.desapilar()
        );

        verificar(
                "Pila vacia despues de retirar todo",
                pila.estaVacia()
        );

        verificarExcepcion(
                "No permite desapilar vacia",
                IllegalStateException.class,
                pila::desapilar
        );

        pila.apilar("Nueva");
        pila.limpiar();

        verificar("Limpiar pila", pila.estaVacia());
    }

    // ==========================================
    // 5. COLA
    // ==========================================

    private static void probarCola() {

        Cola<Integer> cola = new Cola<>();

        verificar(
                "Cola inicialmente vacia",
                cola.estaVacia()
        );

        cola.encolar(100);
        cola.encolar(200);
        cola.encolar(300);

        verificarIgual(
                "Tamanio", 3, cola.tamanio()
        );

        verificarIgual(
                "Frente", 100, cola.frente()
        );

        verificarIgual(
                "Ultimo", 300, cola.ultimo()
        );

        verificarIgual(
                "Buscar elemento", 2, cola.buscar(200)
        );

        verificarIgual(
                "Desencolar primero", 100, cola.desencolar()
        );

        verificarIgual(
                "Desencolar segundo", 200, cola.desencolar()
        );

        verificarIgual(
                "Desencolar tercero", 300, cola.desencolar()
        );

        verificar(
                "Cola vacia despues de retirar todo",
                cola.estaVacia()
        );

        verificarExcepcion(
                "No permite desencolar vacia",
                IllegalStateException.class,
                cola::desencolar
        );

        cola.encolar(400);

        verificarIgual(
                "Reutilizar cola vacia",
                400, cola.frente()
        );

        cola.limpiar();

        verificar("Limpiar cola", cola.estaVacia());
    }

    // ==========================================
    // 6. COLA DE PRIORIDAD
    // ==========================================

    private static void probarColaPrioridad() {

        ColaPrioridad<Integer> cola =
                new ColaPrioridad<>(
                        Comparator.naturalOrder()
                );

        verificar(
                "Inicialmente vacia", cola.estaVacia()
        );

        cola.encolar(50);
        cola.encolar(10);
        cola.encolar(30);
        cola.encolar(5);
        cola.encolar(20);

        verificarIgual(
                "Tamanio", 5, cola.tamanio()
        );

        verificarIgual(
                "Menor prioridad numerica primero",
                5, cola.frente()
        );

        int[] esperado = {5, 10, 20, 30, 50};

        boolean ordenCorrecto = true;

        for (int valor : esperado) {

            if (!Objects.equals(
                    valor, cola.desencolar())) {
                ordenCorrecto = false;
            }
        }

        verificar(
                "Extraccion en orden ascendente",
                ordenCorrecto
        );

        verificar(
                "Vacia despues de extracciones",
                cola.estaVacia()
        );

        verificarExcepcion(
                "No permite desencolar vacia",
                java.util.NoSuchElementException.class,
                cola::desencolar
        );

        // Verificar crecimiento del arreglo.
        for (int i = 100; i >= 0; i--) {
            cola.encolar(i);
        }

        verificarIgual(
                "Redimensionamiento automatico",
                101, cola.tamanio()
        );

        verificarIgual(
                "Prioridad despues de ampliar",
                0, cola.frente()
        );

        cola.limpiar();

        verificar("Limpiar cola", cola.estaVacia());
    }

    // ==========================================
    // 7. LISTA CIRCULAR
    // ==========================================

    private static void probarListaCircular() {

        ListaCircular<String> lista =
                new ListaCircular<>();

        verificar(
                "Circular vacia valida",
                lista.verificarCircularidad()
        );

        lista.agregarFinal("A");
        lista.agregarFinal("B");
        lista.agregarFinal("C");

        verificarIgual(
                "Tamanio", 3, lista.tamanio()
        );

        verificar(
                "Circularidad correcta",
                lista.verificarCircularidad()
        );

        verificarIgual(
                "Primer elemento",
                "A", lista.obtenerPrimero()
        );

        verificarIgual(
                "Ultimo elemento",
                "C", lista.obtenerUltimo()
        );

        lista.rotar();

        verificarIgual(
                "Rotar cabeza",
                "B", lista.obtenerPrimero()
        );

        verificarIgual(
                "Rotar cola",
                "A", lista.obtenerUltimo()
        );

        verificar(
                "Circularidad despues de rotar",
                lista.verificarCircularidad()
        );

        verificarIgual(
                "Eliminar inicio",
                "B", lista.eliminarInicio()
        );

        verificarIgual(
                "Eliminar final",
                "A", lista.eliminarFinal()
        );

        verificarIgual(
                "Elemento restante",
                "C", lista.obtenerPrimero()
        );

        verificar(
                "Circularidad con un nodo",
                lista.verificarCircularidad()
        );

        lista.agregarInicio("Z");

        verificarIgual(
                "Agregar al inicio",
                "Z", lista.obtenerPrimero()
        );

        verificar(
                "Circularidad despues de insertar",
                lista.verificarCircularidad()
        );

        lista.limpiar();

        verificar(
                "Circularidad despues de limpiar",
                lista.verificarCircularidad()
        );
    }

    // ==========================================
    // 8. TABLA HASH
    // ==========================================

    private static void probarTablaHash() {

        TablaHash<String, Integer> tabla =
                new TablaHash<>(2);

        verificar(
                "Tabla inicialmente vacia",
                tabla.estaVacia()
        );

        tabla.insertar("C001", 100);
        tabla.insertar("C002", 200);
        tabla.insertar("C003", 300);

        verificarIgual(
                "Tamanio", 3, tabla.tamanio()
        );

        verificarIgual(
                "Buscar cliente C002",
                200, tabla.obtener("C002")
        );

        verificar(
                "Contiene clave C001",
                tabla.contieneClave("C001")
        );

        verificar(
                "Clave inexistente",
                !tabla.contieneClave("C999")
        );

        verificar(
                "Redimensionamiento automatico",
                tabla.capacidad() > 2
        );

        tabla.insertar("C002", 500);

        verificarIgual(
                "Actualizar valor",
                500, tabla.obtener("C002")
        );

        verificarIgual(
                "No duplicar clave",
                3, tabla.tamanio()
        );

        verificarIgual(
                "Eliminar clave",
                100, tabla.eliminar("C001")
        );

        verificar(
                "Clave eliminada",
                !tabla.contieneClave("C001")
        );

        // Colisiones forzadas:
        // Ambas claves tienen el mismo hash.
        TablaHash<Integer, String> colisiones =
                new TablaHash<>(1);

        colisiones.insertar(1, "Uno");
        colisiones.insertar(2, "Dos");

        verificarIgual(
                "Recuperar primer valor",
                "Uno", colisiones.obtener(1)
        );

        verificarIgual(
                "Recuperar segundo valor",
                "Dos", colisiones.obtener(2)
        );

        tabla.limpiar();

        verificar("Limpiar tabla", tabla.estaVacia());
    }

    // ==========================================
    // 9. ARBOL BINARIO DE BUSQUEDA
    // ==========================================

    private static void probarArbolBinario() {

        ArbolBinario<Integer, String> arbol =
                new ArbolBinario<>();

        arbol.insertar(50, "Juan");
        arbol.insertar(30, "Ana");
        arbol.insertar(70, "Luis");
        arbol.insertar(20, "Sofia");
        arbol.insertar(40, "Carlos");
        arbol.insertar(60, "Maria");
        arbol.insertar(80, "Pedro");

        verificarIgual(
                "Cantidad de entradas",
                7, arbol.tamanio()
        );

        verificarIgual(
                "Cantidad de nodos",
                7, arbol.cantidadNodos()
        );

        verificar(
                "Buscar clave 40",
                arbol.contieneClave(40)
        );

        verificarIgual(
                "Clave minima", 20, arbol.minimo()
        );

        verificarIgual(
                "Clave maxima", 80, arbol.maximo()
        );

        verificar(
                "Integridad inicial",
                arbol.verificarIntegridad()
        );

        arbol.insertar(50, "Camila");

        verificarIgual(
                "Admitir claves repetidas",
                8, arbol.tamanio()
        );

        verificarIgual(
                "Dos valores en clave 50",
                2, arbol.buscar(50).tamanio()
        );

        verificar(
                "Eliminar un valor repetido",
                arbol.eliminar(50, "Camila")
        );

        verificarIgual(
                "Conservar nodo con otro valor",
                1, arbol.buscar(50).tamanio()
        );

        verificar(
                "Eliminar nodo con dos hijos",
                arbol.eliminar(50, "Juan")
        );

        verificar(
                "Integridad tras eliminacion",
                arbol.verificarIntegridad()
        );

        verificarIgual(
                "Cantidad final",
                6, arbol.tamanio()
        );

        StringBuilder recorrido = new StringBuilder();

        arbol.inorden((clave, valor) ->
                recorrido.append(clave).append(" ")
        );

        verificarIgual(
                "Recorrido inorden",
                "20 30 40 60 70 80 ",
                recorrido.toString()
        );

        arbol.limpiar();

        verificar("Limpiar arbol", arbol.estaVacio());
    }

    // ==========================================
    // 10. ARBOL AVL
    // ==========================================

    private static void probarArbolAVL() {

        // Caso LL.
        ArbolAVL<Integer, String> ll =
                new ArbolAVL<>();

        ll.insertar(30, "A");
        ll.insertar(20, "B");
        ll.insertar(10, "C");

        verificar(
                "Rotacion LL",
                ll.verificarIntegridad()
                        && ll.altura() == 2
        );

        // Caso RR.
        ArbolAVL<Integer, String> rr =
                new ArbolAVL<>();

        rr.insertar(10, "A");
        rr.insertar(20, "B");
        rr.insertar(30, "C");

        verificar(
                "Rotacion RR",
                rr.verificarIntegridad()
                        && rr.altura() == 2
        );

        // Caso LR.
        ArbolAVL<Integer, String> lr =
                new ArbolAVL<>();

        lr.insertar(30, "A");
        lr.insertar(10, "B");
        lr.insertar(20, "C");

        verificar(
                "Rotacion LR",
                lr.verificarIntegridad()
                        && lr.altura() == 2
        );

        // Caso RL.
        ArbolAVL<Integer, String> rl =
                new ArbolAVL<>();

        rl.insertar(10, "A");
        rl.insertar(30, "B");
        rl.insertar(20, "C");

        verificar(
                "Rotacion RL",
                rl.verificarIntegridad()
                        && rl.altura() == 2
        );

        // Prueba general de insercion.
        ArbolAVL<Integer, String> arbol =
                new ArbolAVL<>();

        int[] valores = {
                45, 33, 29, 28, 22, 32,
                34, 39, 36, 43, 60, 47,
                46, 50, 49, 55, 70, 69,
                65, 86, 79, 88
        };

        for (int valor : valores) {
            arbol.insertar(valor, "C" + valor);
        }

        verificarIgual(
                "Cantidad de entradas",
                valores.length,
                arbol.tamanio()
        );

        verificar(
                "Balance despues de inserciones",
                arbol.verificarIntegridad()
        );

        verificar(
                "Altura controlada",
                arbol.altura() <= 6
        );

        verificar(
                "Buscar clave existente",
                arbol.contieneClave(49)
        );

        arbol.insertar(49, "OTRO");

        verificarIgual(
                "Claves repetidas",
                2, arbol.buscar(49).tamanio()
        );

        verificar(
                "Eliminar valor repetido",
                arbol.eliminar(49, "OTRO")
        );

        verificar(
                "Eliminar raiz",
                arbol.eliminar(45, "C45")
        );

        verificar(
                "Balance despues de eliminar raiz",
                arbol.verificarIntegridad()
        );

        verificar(
                "Eliminar otro nodo",
                arbol.eliminar(33, "C33")
        );

        verificar(
                "Balance despues de eliminaciones",
                arbol.verificarIntegridad()
        );

        arbol.limpiar();

        verificar(
                "Limpiar AVL",
                arbol.estaVacio()
        );
    }

    // ==========================================
    // 11. GRAFO DIRIGIDO
    // ==========================================

    private static void probarGrafoDirigido() {

        GrafoDirigido grafo = new GrafoDirigido();

        verificar(
                "Grafo inicialmente vacio",
                grafo.estaVacio()
        );

        grafo.agregarVertice("C001");
        grafo.agregarVertice("C002");
        grafo.agregarVertice("C003");

        verificarIgual(
                "Cantidad de vertices",
                3, grafo.cantidadVertices()
        );

        grafo.agregarArista(
                "C001", "C002",
                new BigDecimal("50000")
        );

        grafo.agregarArista(
                "C001", "C002",
                new BigDecimal("20000")
        );

        grafo.agregarArista(
                "C002", "C003",
                new BigDecimal("35000")
        );

        verificarIgual(
                "Cantidad de aristas distintas",
                2, grafo.cantidadAristas()
        );

        verificarIgual(
                "Acumulacion de transferencias",
                new BigDecimal("70000"),
                grafo.obtenerPeso("C001", "C002")
        );

        verificarIgual(
                "Cantidad de operaciones",
                2,
                grafo.obtenerCantidadOperaciones(
                        "C001", "C002"
                )
        );

        verificar(
                "Existe arista dirigida",
                grafo.existeArista("C001", "C002")
        );

        verificar(
                "No existe arista inversa",
                !grafo.existeArista("C002", "C001")
        );

        verificarIgual(
                "Total enviado C001",
                new BigDecimal("70000"),
                grafo.totalEnviado("C001")
        );

        verificarIgual(
                "Total recibido C002",
                new BigDecimal("70000"),
                grafo.totalRecibido("C002")
        );

        verificar(
                "Camino dirigido C001 a C003",
                grafo.existeCamino("C001", "C003")
        );

        verificar(
                "No existe camino inverso",
                !grafo.existeCamino("C003", "C001")
        );

        verificarIgual(
                "Grado de salida C001",
                1, grafo.gradoSalida("C001")
        );

        verificar(
                "Integridad del grafo",
                grafo.verificarIntegridad()
        );

        verificar(
                "Eliminar arista",
                grafo.eliminarArista("C001", "C002")
        );

        verificar(
                "Arista eliminada",
                !grafo.existeArista("C001", "C002")
        );

        verificar(
                "Eliminar vertice",
                grafo.eliminarVertice("C002")
        );

        verificar(
                "Integridad despues de eliminar",
                grafo.verificarIntegridad()
        );

        grafo.limpiar();

        verificar(
                "Limpiar grafo",
                grafo.estaVacio()
        );
    }

    // ==========================================
    // RESUMEN GENERAL
    // ==========================================

    private static void mostrarResumen() {

        System.out.println();
        System.out.println("====================================");
        System.out.println(" RESUMEN GENERAL");
        System.out.println("====================================");

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

        System.out.println("------------------------------------");

        if (fallidas == 0 && gruposFallidos == 0) {

            System.out.println(
                    "RESULTADO: TODAS LAS PRUEBAS APROBADAS"
            );

        } else {

            System.out.println(
                    "RESULTADO: SE ENCONTRARON ERRORES"
            );
        }

        System.out.println("====================================");
    }
}
