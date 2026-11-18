package vista.tema;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ==================================================
 *                 MONEDERO VIRTUAL
 * ==================================================
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * CLASE:
 * Tipografia.java
 *
 * RESPONSABILIDADES:
 *
 * - Centralizar las fuentes de la interfaz.
 * - Definir las escalas tipograficas.
 * - Cargar fuentes personalizadas.
 * - Proporcionar fuentes de respaldo.
 * - Mantener coherencia entre pantallas.
 *
 * FAMILIAS:
 *
 * INTER:
 * Interfaz, navegacion y formularios.
 *
 * IBM PLEX MONO:
 * Valores monetarios, puntos y codigos.
 *
 * INSTRUMENT SERIF:
 * Titulos y elementos editoriales.
 *
 * RECURSOS ESPERADOS:
 *
 * src/recursos/fuentes/
 *
 * - Inter-Regular.ttf
 * - Inter-Medium.ttf
 * - Inter-SemiBold.ttf
 * - Inter-Bold.ttf
 * - IBMPlexMono-Regular.ttf
 * - IBMPlexMono-Medium.ttf
 * - IBMPlexMono-Bold.ttf
 * - InstrumentSerif-Regular.ttf
 * - InstrumentSerif-Italic.ttf
 *
 * Si los archivos no existen, se utilizan
 * fuentes de respaldo del sistema.
 *
 * No requiere bibliotecas externas.
 *
 * Compatible con Java 21.
 */
public final class Tipografia {

    // ==========================================
    // CONSTRUCTOR PRIVADO
    // ==========================================

    private Tipografia() {

        throw new UnsupportedOperationException(
                "Tipografia es una clase de utilidades"
        );
    }

    // ==========================================
    // NOMBRES DE FAMILIAS
    // ==========================================

    public static final String FAMILIA_INTER =
            "Inter";

    public static final String FAMILIA_MONO =
            "IBM Plex Mono";

    public static final String FAMILIA_EDITORIAL =
            "Instrument Serif";

    public static final String RESPALDO_INTER =
            Font.SANS_SERIF;

    public static final String RESPALDO_MONO =
            Font.MONOSPACED;

    public static final String RESPALDO_EDITORIAL =
            Font.SERIF;

    // ==========================================
    // ARCHIVOS DE FUENTES
    // ==========================================

    private static final String RUTA_RECURSOS =
            "/recursos/fuentes/";

    private static final String RUTA_DESARROLLO =
            "src/recursos/fuentes/";

    private static final String ARCHIVO_INTER_REGULAR =
            "Inter-Regular.ttf";

    private static final String ARCHIVO_INTER_MEDIUM =
            "Inter-Medium.ttf";

    private static final String ARCHIVO_INTER_SEMIBOLD =
            "Inter-SemiBold.ttf";

    private static final String ARCHIVO_INTER_BOLD =
            "Inter-Bold.ttf";

    private static final String ARCHIVO_MONO_REGULAR =
            "IBMPlexMono-Regular.ttf";

    private static final String ARCHIVO_MONO_MEDIUM =
            "IBMPlexMono-Medium.ttf";

    // Se conserva la API monoSemiBold(), pero el archivo disponible es Bold.
    private static final String ARCHIVO_MONO_SEMIBOLD =
            "IBMPlexMono-Bold.ttf";

    private static final String ARCHIVO_EDITORIAL_REGULAR =
            "InstrumentSerif-Regular.ttf";

    private static final String ARCHIVO_EDITORIAL_ITALIC =
            "InstrumentSerif-Italic.ttf";

    // ==========================================
    // ESCALA TIPOGRAFICA
    // ==========================================

    public static final float TAMANO_MICRO = 10f;

    public static final float TAMANO_CAPTION = 11f;

    public static final float TAMANO_ETIQUETA = 12f;

    public static final float TAMANO_PEQUENO = 13f;

    public static final float TAMANO_CUERPO = 14f;

    public static final float TAMANO_CUERPO_GRANDE = 15f;

    public static final float TAMANO_BOTON = 14f;

    public static final float TAMANO_SUBTITULO = 17f;

    public static final float TAMANO_TARJETA = 20f;

    public static final float TAMANO_SECCION = 28f;

    public static final float TAMANO_TITULO = 36f;

    public static final float TAMANO_DISPLAY = 48f;

    public static final float TAMANO_BALANCE = 58f;

    public static final float TAMANO_HERO = 64f;

    // ==========================================
    // PESOS LOGICOS
    // ==========================================

    public static final int PESO_REGULAR =
            Font.PLAIN;

    public static final int PESO_NEGRITA =
            Font.BOLD;

    public static final int ESTILO_ITALICA =
            Font.ITALIC;

    // ==========================================
    // FUENTES CARGADAS
    // ==========================================

    private static Font interRegular;

    private static Font interMedium;

    private static Font interSemiBold;

    private static Font interBold;

    private static Font monoRegular;

    private static Font monoMedium;

    private static Font monoSemiBold;

    private static Font editorialRegular;

    private static Font editorialItalic;

    // ==========================================
    // ESTADO DE INICIALIZACION
    // ==========================================

    private static boolean inicializada = false;

    private static boolean interPersonalizada = false;

    private static boolean monoPersonalizada = false;

    private static boolean editorialPersonalizada = false;

    // ==========================================
    // CACHE DE FUENTES
    // ==========================================

    /**
     * Evita reconstruir objetos Font
     * constantemente.
     *
     * Se utiliza HashMap solamente para
     * almacenar recursos graficos.
     *
     * No reemplaza ninguna estructura
     * de datos del proyecto academico.
     */
    private static final Map<String, Font> cache =
            new HashMap<>();

    // Registra si cada TTF se cargo realmente o requirio respaldo.
    private static final Map<String, Boolean> fuentesCargadas =
            new HashMap<>();

    // ==========================================
    // INICIALIZACION PUBLICA
    // ==========================================

    /**
     * Carga las fuentes de la aplicacion.
     *
     * Puede llamarse varias veces.
     * La inicializacion se realiza una sola vez.
     */
    public static synchronized void inicializar() {

        if (inicializada) {
            return;
        }

        // --------------------------------------
        // INTER
        // --------------------------------------

        interRegular = cargarFuente(
                ARCHIVO_INTER_REGULAR,
                RESPALDO_INTER,
                Font.PLAIN
        );

        interMedium = cargarFuente(
                ARCHIVO_INTER_MEDIUM,
                RESPALDO_INTER,
                Font.PLAIN
        );

        interSemiBold = cargarFuente(
                ARCHIVO_INTER_SEMIBOLD,
                RESPALDO_INTER,
                Font.BOLD
        );

        interBold = cargarFuente(
                ARCHIVO_INTER_BOLD,
                RESPALDO_INTER,
                Font.BOLD
        );

        // --------------------------------------
        // IBM PLEX MONO
        // --------------------------------------

        monoRegular = cargarFuente(
                ARCHIVO_MONO_REGULAR,
                RESPALDO_MONO,
                Font.PLAIN
        );

        monoMedium = cargarFuente(
                ARCHIVO_MONO_MEDIUM,
                RESPALDO_MONO,
                Font.PLAIN
        );

        monoSemiBold = cargarFuente(
                ARCHIVO_MONO_SEMIBOLD,
                RESPALDO_MONO,
                Font.BOLD
        );

        // --------------------------------------
        // INSTRUMENT SERIF
        // --------------------------------------

        editorialRegular = cargarFuente(
                ARCHIVO_EDITORIAL_REGULAR,
                RESPALDO_EDITORIAL,
                Font.PLAIN
        );

        editorialItalic = cargarFuente(
                ARCHIVO_EDITORIAL_ITALIC,
                RESPALDO_EDITORIAL,
                Font.ITALIC
        );

        // --------------------------------------
        // VERIFICAR FUENTES PERSONALIZADAS
        // --------------------------------------

        interPersonalizada =
                estaCargada(ARCHIVO_INTER_REGULAR)
                        && estaCargada(ARCHIVO_INTER_MEDIUM)
                        && estaCargada(ARCHIVO_INTER_SEMIBOLD)
                        && estaCargada(ARCHIVO_INTER_BOLD);

        monoPersonalizada =
                estaCargada(ARCHIVO_MONO_REGULAR)
                        && estaCargada(ARCHIVO_MONO_MEDIUM)
                        && estaCargada(ARCHIVO_MONO_SEMIBOLD);

        editorialPersonalizada =
                estaCargada(ARCHIVO_EDITORIAL_REGULAR)
                        && estaCargada(ARCHIVO_EDITORIAL_ITALIC);

        inicializada = true;
    }

    // ==========================================
    // CARGAR FUENTE DESDE RECURSOS
    // ==========================================

    /**
     * Intenta cargar una fuente desde:
     *
     * 1. Classpath del proyecto.
     * 2. Carpeta de desarrollo local.
     * 3. Fuente de respaldo de Java.
     *
     * Nunca devuelve null.
     */
    private static Font cargarFuente(
            String archivo,
            String familiaRespaldo,
            int estiloRespaldo) {

        // --------------------------------------
        // OPCION 1: CLASSPATH
        // --------------------------------------

        try (InputStream entrada =
                     Tipografia.class.getResourceAsStream(
                             RUTA_RECURSOS + archivo
                     )) {

            if (entrada != null) {

                Font fuente = Font.createFont(
                        Font.TRUETYPE_FONT,
                        entrada
                );

                registrarFuente(fuente);
                fuentesCargadas.put(archivo, true);
                return fuente.deriveFont(14f);
            }

        } catch (IOException
                 | FontFormatException error) {

            System.err.println(
                    "[TIPOGRAFIA] Error cargando recurso "
                            + archivo
                            + ": "
                            + error.getMessage()
            );
        }

        // --------------------------------------
        // OPCION 2: ARCHIVO LOCAL
        // --------------------------------------

        File archivoLocal = new File(
                RUTA_DESARROLLO + archivo
        );

        if (archivoLocal.isFile()) {

            try {

                Font fuente = Font.createFont(
                        Font.TRUETYPE_FONT,
                        archivoLocal
                );

                registrarFuente(fuente);
                fuentesCargadas.put(archivo, true);
                return fuente.deriveFont(14f);

            } catch (IOException
                     | FontFormatException error) {

                System.err.println(
                        "[TIPOGRAFIA] Error cargando archivo "
                                + archivo
                                + ": "
                                + error.getMessage()
                );
            }
        }

        // --------------------------------------
        // OPCION 3: RESPALDO
        // --------------------------------------

        fuentesCargadas.put(archivo, false);
        System.err.println(
                "[TIPOGRAFIA] No se encontro " + archivo
                        + "; se usara fuente de respaldo."
        );
        return new Font(
                familiaRespaldo,
                estiloRespaldo,
                14
        );
    }

    // ==========================================
    // REGISTRAR FUENTE EN JAVA
    // ==========================================

    private static void registrarFuente(
            Font fuente) {

        try {

            if (!GraphicsEnvironment.isHeadless()) {

                GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .registerFont(fuente);
            }

        } catch (RuntimeException error) {

            System.err.println(
                    "[TIPOGRAFIA] No se pudo registrar "
                            + fuente.getFontName()
            );
        }
    }

    // ==========================================
    // VERIFICACION EXACTA DE RECURSOS
    // ==========================================

    private static boolean estaCargada(String archivo) {
        return Boolean.TRUE.equals(fuentesCargadas.get(archivo));
    }

    public static int cantidadArchivosCargados() {
        inicializar();
        int cantidad = 0;
        for (boolean cargada : fuentesCargadas.values()) {
            if (cargada) cantidad++;
        }
        return cantidad;
    }

    // ==========================================
    // CREACION DE FUENTES
    // ==========================================

    /**
     * Devuelve una fuente escalada.
     *
     * Usa un cache para evitar derivaciones
     * repetidas del mismo objeto.
     */
    private static synchronized Font obtenerFuente(
            String identificador,
            Font base,
            float tamano) {

        validarTamano(tamano);

        String clave =
                identificador + ":" + tamano;

        Font existente = cache.get(clave);

        if (existente != null) {
            return existente;
        }

        Font fuente = base.deriveFont(
                tamano
        );

        cache.put(clave, fuente);

        return fuente;
    }

    // ==========================================
    // INTER REGULAR
    // ==========================================

    public static Font regular(float tamano) {

        inicializar();

        return obtenerFuente(
                "inter-regular",
                interRegular,
                tamano
        );
    }

    public static Font regular() {

        return regular(TAMANO_CUERPO);
    }

    // ==========================================
    // INTER MEDIUM
    // ==========================================

    public static Font medium(float tamano) {

        inicializar();

        return obtenerFuente(
                "inter-medium",
                interMedium,
                tamano
        );
    }

    public static Font medium() {

        return medium(TAMANO_CUERPO);
    }

    // ==========================================
    // INTER SEMIBOLD
    // ==========================================

    public static Font semiBold(float tamano) {

        inicializar();

        return obtenerFuente(
                "inter-semibold",
                interSemiBold,
                tamano
        );
    }

    public static Font semiBold() {

        return semiBold(TAMANO_CUERPO);
    }

    // ==========================================
    // INTER BOLD
    // ==========================================

    public static Font bold(float tamano) {

        inicializar();

        return obtenerFuente(
                "inter-bold",
                interBold,
                tamano
        );
    }

    public static Font bold() {

        return bold(TAMANO_CUERPO);
    }

    // ==========================================
    // IBM PLEX MONO REGULAR
    // ==========================================

    public static Font mono(float tamano) {

        inicializar();

        return obtenerFuente(
                "mono-regular",
                monoRegular,
                tamano
        );
    }

    public static Font mono() {

        return mono(TAMANO_CUERPO);
    }

    // ==========================================
    // IBM PLEX MONO MEDIUM
    // ==========================================

    public static Font monoMedium(float tamano) {

        inicializar();

        return obtenerFuente(
                "mono-medium",
                monoMedium,
                tamano
        );
    }

    // ==========================================
    // IBM PLEX MONO SEMIBOLD
    // ==========================================

    public static Font monoSemiBold(float tamano) {

        inicializar();

        return obtenerFuente(
                "mono-semibold",
                monoSemiBold,
                tamano
        );
    }

    // ==========================================
    // INSTRUMENT SERIF
    // ==========================================

    public static Font editorial(float tamano) {

        inicializar();

        return obtenerFuente(
                "editorial-regular",
                editorialRegular,
                tamano
        );
    }

    public static Font editorial() {

        return editorial(TAMANO_TITULO);
    }

    // ==========================================
    // INSTRUMENT SERIF ITALIC
    // ==========================================

    public static Font editorialItalic(float tamano) {

        inicializar();

        return obtenerFuente(
                "editorial-italic",
                editorialItalic,
                tamano
        );
    }

    // ==========================================
    // FUENTES SEMANTICAS
    // ==========================================

    /**
     * Las vistas deben preferir estos metodos
     * en lugar de seleccionar tamanos
     * arbitrarios para cada componente.
     */

    public static Font micro() {
        return medium(TAMANO_MICRO);
    }

    public static Font caption() {
        return regular(TAMANO_CAPTION);
    }

    public static Font etiqueta() {
        return semiBold(TAMANO_ETIQUETA);
    }

    public static Font cuerpoPequeno() {
        return regular(TAMANO_PEQUENO);
    }

    public static Font cuerpo() {
        return regular(TAMANO_CUERPO);
    }

    public static Font cuerpoGrande() {
        return regular(TAMANO_CUERPO_GRANDE);
    }

    public static Font boton() {
        return semiBold(TAMANO_BOTON);
    }

    public static Font navegacion() {
        return medium(TAMANO_CUERPO);
    }

    public static Font navegacionActiva() {
        return semiBold(TAMANO_CUERPO);
    }

    public static Font subtitulo() {
        return semiBold(TAMANO_SUBTITULO);
    }

    public static Font tituloTarjeta() {
        return semiBold(TAMANO_TARJETA);
    }

    public static Font tituloSeccion() {
        return semiBold(TAMANO_SECCION);
    }

    public static Font tituloPagina() {
        return semiBold(TAMANO_TITULO);
    }

    public static Font tituloEditorial() {
        return editorial(TAMANO_DISPLAY);
    }

    public static Font heroEditorial() {
        return editorial(TAMANO_HERO);
    }

    public static Font balancePrincipal() {
        return monoMedium(TAMANO_BALANCE);
    }

    public static Font balanceSecundario() {
        return monoMedium(32f);
    }

    public static Font montoTarjeta() {
        return monoMedium(26f);
    }

    public static Font montoTabla() {
        return monoMedium(13f);
    }

    public static Font numeroEstadistica() {
        return monoMedium(30f);
    }

    public static Font puntos() {
        return monoSemiBold(22f);
    }

    public static Font codigoTransaccion() {
        return mono(TAMANO_ETIQUETA);
    }

    public static Font fecha() {
        return mono(TAMANO_ETIQUETA);
    }

    public static Font encabezadoTabla() {
        return semiBold(TAMANO_ETIQUETA);
    }

    public static Font celdaTabla() {
        return regular(TAMANO_PEQUENO);
    }

    public static Font textoFormulario() {
        return regular(TAMANO_CUERPO);
    }

    public static Font tituloDialogo() {
        return semiBold(24f);
    }

    public static Font mensajeEstado() {
        return medium(TAMANO_PEQUENO);
    }

    // ==========================================
    // CREAR FUENTE PERSONALIZADA
    // ==========================================

    /**
     * Permite seleccionar una familia,
     * estilo y tamano.
     *
     * Estilos soportados:
     * Font.PLAIN, Font.BOLD,
     * Font.ITALIC y sus combinaciones.
     */
    public static Font personalizada(
            String familia,
            int estilo,
            float tamano) {

        inicializar();

        validarTamano(tamano);

        if (familia == null || familia.isBlank()) {

            throw new IllegalArgumentException(
                    "La familia es obligatoria"
            );
        }

        if (estilo < Font.PLAIN
                || estilo > (Font.BOLD | Font.ITALIC)) {

            throw new IllegalArgumentException(
                    "Estilo de fuente no valido"
            );
        }

        Font base;
        boolean negrita = (estilo & Font.BOLD) != 0;
        boolean cursiva = (estilo & Font.ITALIC) != 0;

        if (familia.equalsIgnoreCase(FAMILIA_INTER)) {
            base = negrita ? interBold : interRegular;
        } else if (familia.equalsIgnoreCase(FAMILIA_MONO)) {
            base = negrita ? monoSemiBold : monoRegular;
        } else if (familia.equalsIgnoreCase(FAMILIA_EDITORIAL)) {
            base = cursiva ? editorialItalic : editorialRegular;
        } else {
            return new Font(
                    familia,
                    estilo,
                    Math.round(tamano)
            ).deriveFont(tamano);
        }

        // El estilo ya viene en la variante TTF (Bold/Italic).
        // Solo derivamos la combinacion no cubierta por ese archivo.
        if (familia.equalsIgnoreCase(FAMILIA_EDITORIAL) && negrita) {
            return base.deriveFont(Font.BOLD | (cursiva ? Font.ITALIC : 0), tamano);
        }
        if (!familia.equalsIgnoreCase(FAMILIA_EDITORIAL) && cursiva) {
            return base.deriveFont((negrita ? Font.BOLD : 0) | Font.ITALIC, tamano);
        }
        return base.deriveFont(tamano);
    }

    // ==========================================
    // ESCALAR FUENTE EXISTENTE
    // ==========================================

    /**
     * Devuelve una fuente con otro tamano.
     *
     * Conserva la familia y el estilo.
     */
    public static Font escalar(
            Font fuente,
            float tamano) {

        if (fuente == null) {

            throw new IllegalArgumentException(
                    "La fuente no puede ser null"
            );
        }

        validarTamano(tamano);

        return fuente.deriveFont(tamano);
    }

    // ==========================================
    // CAMBIAR ESTILO
    // ==========================================

    public static Font cambiarEstilo(
            Font fuente,
            int estilo) {

        if (fuente == null) {

            throw new IllegalArgumentException(
                    "La fuente no puede ser null"
            );
        }

        if (estilo < Font.PLAIN
                || estilo > (Font.BOLD | Font.ITALIC)) {

            throw new IllegalArgumentException(
                    "Estilo de fuente no valido"
            );
        }

        return fuente.deriveFont(
                estilo,
                fuente.getSize2D()
        );
    }

    // ==========================================
    // ESTADO DE LAS FUENTES
    // ==========================================

    public static boolean estaInicializada() {

        return inicializada;
    }

    public static boolean tieneInterPersonalizada() {

        inicializar();

        return interPersonalizada;
    }

    public static boolean tieneMonoPersonalizada() {

        inicializar();

        return monoPersonalizada;
    }

    public static boolean tieneEditorialPersonalizada() {

        inicializar();

        return editorialPersonalizada;
    }

    public static boolean todasPersonalizadas() {

        inicializar();

        return interPersonalizada
                && monoPersonalizada
                && editorialPersonalizada;
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private static void validarTamano(
            float tamano) {

        if (!Float.isFinite(tamano)
                || tamano <= 0f
                || tamano > 256f) {

            throw new IllegalArgumentException(
                    "El tamano debe estar "
                            + "entre 0 y 256"
            );
        }
    }

    // ==========================================
    // LIMPIEZA DEL CACHE
    // ==========================================

    /**
     * Elimina fuentes derivadas del cache.
     *
     * No elimina las fuentes base cargadas.
     *
     * No debe llamarse durante una operacion
     * de pintado de Swing.
     */
    public static synchronized void limpiarCache() {

        cache.clear();
    }

    // ==========================================
    // INFORME DE TIPOGRAFIA
    // ==========================================

    public static String generarInforme() {

        inicializar();

        StringBuilder resultado =
                new StringBuilder();

        resultado.append(
                "========================================\n"
        );

        resultado.append(
                "MONEDERO VIRTUAL - TIPOGRAFIA\n"
        );

        resultado.append(
                "========================================\n"
        );

        resultado.append(
                "Sistema visual: Institutional Noir\n\n"
        );

        resultado.append(
                "INTER\n"
        );

        resultado.append(
                "Familia activa: "
        ).append(
                interRegular.getFamily()
        ).append("\n");

        resultado.append(
                "Personalizada: "
        ).append(
                interPersonalizada
        ).append("\n\n");

        resultado.append(
                "IBM PLEX MONO\n"
        );

        resultado.append(
                "Familia activa: "
        ).append(
                monoRegular.getFamily()
        ).append("\n");

        resultado.append(
                "Personalizada: "
        ).append(
                monoPersonalizada
        ).append("\n\n");

        resultado.append(
                "INSTRUMENT SERIF\n"
        );

        resultado.append(
                "Familia activa: "
        ).append(
                editorialRegular.getFamily()
        ).append("\n");

        resultado.append(
                "Personalizada: "
        ).append(
                editorialPersonalizada
        ).append("\n\n");

        resultado.append(
                "Fuentes TTF cargadas: "
        ).append(cantidadArchivosCargados()).append("/9\n");

        resultado.append(
                "Todas las familias completas: "
        ).append(todasPersonalizadas()).append("\n");

        resultado.append(
                "Fuentes en cache: "
        ).append(cache.size()).append("\n");

        return resultado.toString();
    }
}
