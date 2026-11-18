
package vista.tema;

import java.awt.Color;

/**
 * ==================================================
 *                 MONEDERO VIRTUAL
 * ==================================================
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * CLASE:
 * Paleta.java
 *
 * RESPONSABILIDAD:
 *
 * Centralizar todos los colores utilizados
 * en la interfaz grafica Java Swing.
 *
 * DIRECCION ARTISTICA:
 *
 * - Minimalismo editorial.
 * - Negro carbon y grafito.
 * - Blanco hueso.
 * - Champagne como acento principal.
 * - Verde salvia para estados positivos.
 * - Terracota para alertas y errores.
 * - Contrastes discretos.
 * - Superficies sin degradados excesivos.
 *
 * PRINCIPIOS:
 *
 * 1. Evitar colores arbitrarios.
 * 2. Mantener consistencia visual.
 * 3. Priorizar legibilidad.
 * 4. Utilizar acentos con moderacion.
 * 5. Separar colores visuales y semanticos.
 *
 * Todos los colores son constantes
 * compartidas e inmutables.
 *
 * Java 21.
 */
public final class Paleta {

    // ==========================================
    // CONSTRUCTOR PRIVADO
    // ==========================================

    private Paleta() {

        throw new UnsupportedOperationException(
                "Paleta es una clase de utilidades"
        );
    }

    // ==========================================
    // 1. COLORES FUNDAMENTALES
    // ==========================================

    /**
     * Fondo principal de la aplicacion.
     *
     * Negro carbon ligeramente calido.
     */
    public static final Color OBSIDIAN =
            new Color(0x101113);

    /**
     * Superficie de navegacion lateral.
     */
    public static final Color GRAPHITE =
            new Color(0x17181B);

    /**
     * Superficies elevadas.
     */
    public static final Color ELEVATED =
            new Color(0x202125);

    /**
     * Separadores y bordes.
     */
    public static final Color DIVIDER =
            new Color(0x353638);

    /**
     * Blanco hueso.
     *
     * Color principal de texto.
     */
    public static final Color BONE =
            new Color(0xF1EFE9);

    /**
     * Texto secundario.
     */
    public static final Color ASH =
            new Color(0xA2A3A6);

    /**
     * Acento principal de marca.
     *
     * Utilizar con moderacion.
     */
    public static final Color CHAMPAGNE =
            new Color(0xC7A77A);

    /**
     * Verde salvia.
     *
     * Operaciones positivas.
     */
    public static final Color SAGE =
            new Color(0xAFC6B0);

    /**
     * Terracota.
     *
     * Operaciones negativas y advertencias.
     */
    public static final Color CLAY =
            new Color(0xD5A095);

    // ==========================================
    // 2. FONDOS Y SUPERFICIES
    // ==========================================

    public static final Color FONDO_PRINCIPAL =
            OBSIDIAN;

    public static final Color FONDO_LATERAL =
            GRAPHITE;

    public static final Color FONDO_TARJETA =
            ELEVATED;

    public static final Color FONDO_SECUNDARIO =
            new Color(0x1C1D20);

    public static final Color FONDO_TERCIARIO =
            new Color(0x25262A);

    public static final Color FONDO_CAMPO =
            new Color(0x1A1B1E);

    public static final Color FONDO_TABLA =
            OBSIDIAN;

    public static final Color FONDO_TABLA_ALTERNO =
            new Color(0x18191C);

    public static final Color FONDO_TABLA_HOVER =
            new Color(0x292A2E);

    public static final Color FONDO_TABLA_SELECCION =
            new Color(0x302C27);

    public static final Color FONDO_OVERLAY =
            new Color(0, 0, 0, 165);

    // ==========================================
    // 3. TEXTOS
    // ==========================================

    public static final Color TEXTO_PRINCIPAL =
            BONE;

    public static final Color TEXTO_SECUNDARIO =
            ASH;

    public static final Color TEXTO_TERCIARIO =
            new Color(0x828488);

    public static final Color TEXTO_DESHABILITADO =
            new Color(0x64666A);

    public static final Color TEXTO_SOBRE_ACENTO =
            OBSIDIAN;

    public static final Color TEXTO_ENLACE =
            CHAMPAGNE;

    public static final Color TEXTO_NUMERICO =
            new Color(0xF6F3EE);

    public static final Color TEXTO_ETIQUETA =
            new Color(0xB7B6B2);

    public static final Color TEXTO_PLACEHOLDER =
            new Color(0x85868A);

    // ==========================================
    // 4. ACENTOS DE MARCA
    // ==========================================

    public static final Color ACENTO =
            CHAMPAGNE;

    public static final Color ACENTO_HOVER =
            new Color(0xD5B98E);

    public static final Color ACENTO_PRESIONADO =
            new Color(0xAE8B5C);

    public static final Color ACENTO_SUAVE =
            new Color(0x302A23);

    public static final Color ACENTO_BORDE =
            new Color(0x65543F);

    public static final Color ACENTO_DESHABILITADO =
            new Color(0x625849);

    // ==========================================
    // 5. BORDES Y DIVISIONES
    // ==========================================

    public static final Color BORDE =
            DIVIDER;

    public static final Color BORDE_SUAVE =
            new Color(0x2B2C2F);

    public static final Color BORDE_FUERTE =
            new Color(0x4D4F52);

    public static final Color BORDE_FOCO =
            CHAMPAGNE;

    public static final Color BORDE_ERROR =
            CLAY;

    public static final Color BORDE_EXITO =
            SAGE;

    // ==========================================
    // 6. BOTONES
    // ==========================================

    public static final Color BOTON_PRIMARIO =
            CHAMPAGNE;

    public static final Color BOTON_PRIMARIO_HOVER =
            ACENTO_HOVER;

    public static final Color BOTON_PRIMARIO_PRESIONADO =
            ACENTO_PRESIONADO;

    public static final Color BOTON_PRIMARIO_TEXTO =
            OBSIDIAN;

    public static final Color BOTON_SECUNDARIO =
            new Color(0x292A2D);

    public static final Color BOTON_SECUNDARIO_HOVER =
            new Color(0x35363A);

    public static final Color BOTON_SECUNDARIO_TEXTO =
            BONE;

    public static final Color BOTON_FANTASMA_HOVER =
            new Color(0x25262A);

    public static final Color BOTON_PELIGRO =
            new Color(0xA9665D);

    public static final Color BOTON_PELIGRO_HOVER =
            new Color(0xBC786E);

    // ==========================================
    // 7. NAVEGACION LATERAL
    // ==========================================

    public static final Color SIDEBAR_FONDO =
            GRAPHITE;

    public static final Color SIDEBAR_TEXTO =
            new Color(0xA7A8AB);

    public static final Color SIDEBAR_TEXTO_ACTIVO =
            BONE;

    public static final Color SIDEBAR_ITEM_HOVER =
            new Color(0x242529);

    public static final Color SIDEBAR_ITEM_ACTIVO =
            new Color(0x302D29);

    public static final Color SIDEBAR_INDICADOR =
            CHAMPAGNE;

    public static final Color SIDEBAR_DIVISOR =
            new Color(0x303135);

    // ==========================================
    // 8. ESTADOS FINANCIEROS
    // ==========================================

    /**
     * Entrada de dinero.
     */
    public static final Color FINANZAS_ENTRADA =
            SAGE;

    /**
     * Salida de dinero.
     */
    public static final Color FINANZAS_SALIDA =
            CLAY;

    /**
     * Valores neutros.
     */
    public static final Color FINANZAS_NEUTRO =
            BONE;

    /**
     * Comisiones.
     */
    public static final Color FINANZAS_COMISION =
            new Color(0xD2BD9C);

    /**
     * Saldo bajo.
     */
    public static final Color FINANZAS_SALDO_BAJO =
            new Color(0xDDBE83);

    // ==========================================
    // 9. ESTADOS DEL SISTEMA
    // ==========================================

    public static final Color EXITO =
            new Color(0xB5D7BD);

    public static final Color EXITO_FONDO =
            new Color(0x243129);

    public static final Color ADVERTENCIA =
            new Color(0xDFC79B);

    public static final Color ADVERTENCIA_FONDO =
            new Color(0x353125);

    public static final Color ERROR =
            new Color(0xE2A7A2);

    public static final Color ERROR_FONDO =
            new Color(0x392A2A);

    public static final Color INFORMACION =
            new Color(0xA8B8D0);

    public static final Color INFORMACION_FONDO =
            new Color(0x252E39);

    public static final Color PENDIENTE =
            ADVERTENCIA;

    public static final Color COMPLETADA =
            EXITO;

    public static final Color CANCELADA =
            TEXTO_TERCIARIO;

    public static final Color REVERTIDA =
            new Color(0xB9A6CF);

    // ==========================================
    // 10. RANGOS DE CLIENTES
    // ==========================================

    /**
     * BRONCE:
     * Cobre oscuro y sobrio.
     */
    public static final Color RANGO_BRONCE =
            new Color(0xB48B72);

    public static final Color RANGO_BRONCE_FONDO =
            new Color(0x302823);

    /**
     * PLATA:
     * Gris metalico frio.
     */
    public static final Color RANGO_PLATA =
            new Color(0xC2C8D0);

    public static final Color RANGO_PLATA_FONDO =
            new Color(0x2A2D33);

    /**
     * ORO:
     * Champagne ligeramente mas intenso.
     */
    public static final Color RANGO_ORO =
            CHAMPAGNE;

    public static final Color RANGO_ORO_FONDO =
            new Color(0x332D25);

    /**
     * PLATINO:
     * Blanco metalico.
     */
    public static final Color RANGO_PLATINO =
            new Color(0xE1E3E7);

    public static final Color RANGO_PLATINO_FONDO =
            new Color(0x34373B);

    // ==========================================
    // 11. GRAFICOS Y VISUALIZACIONES
    // ==========================================

    public static final Color GRAFICO_PRINCIPAL =
            CHAMPAGNE;

    public static final Color GRAFICO_SECUNDARIO =
            SAGE;

    public static final Color GRAFICO_TERCIARIO =
            new Color(0x9BA9B8);

    public static final Color GRAFICO_CUATERNARIO =
            new Color(0xB59EBE);

    public static final Color GRAFICO_REJILLA =
            new Color(0x303135);

    public static final Color GRAFICO_EJE =
            new Color(0x77797C);

    public static final Color GRAFICO_ETIQUETA =
            ASH;

    public static final Color GRAFICO_FONDO =
            OBSIDIAN;

    // ==========================================
    // 12. TABLAS
    // ==========================================

    public static final Color TABLA_CABECERA =
            new Color(0x1C1D20);

    public static final Color TABLA_FILA =
            OBSIDIAN;

    public static final Color TABLA_FILA_ALTERNA =
            FONDO_TABLA_ALTERNO;

    public static final Color TABLA_FILA_HOVER =
            FONDO_TABLA_HOVER;

    public static final Color TABLA_SELECCION =
            FONDO_TABLA_SELECCION;

    public static final Color TABLA_LINEAS =
            BORDE_SUAVE;

    public static final Color TABLA_TEXTO =
            BONE;

    public static final Color TABLA_TEXTO_SECUNDARIO =
            ASH;

    // ==========================================
    // 13. CAMPOS DE FORMULARIO
    // ==========================================

    public static final Color INPUT_FONDO =
            FONDO_CAMPO;

    public static final Color INPUT_BORDE =
            BORDE;

    public static final Color INPUT_BORDE_FOCO =
            CHAMPAGNE;

    public static final Color INPUT_TEXTO =
            BONE;

    public static final Color INPUT_PLACEHOLDER =
            TEXTO_PLACEHOLDER;

    public static final Color INPUT_DESHABILITADO =
            new Color(0x232427);

    public static final Color INPUT_ERROR =
            ERROR;

    // ==========================================
    // 14. OVERLAYS Y SOMBRAS
    // ==========================================

    public static final Color SOMBRA_SUAVE =
            new Color(0, 0, 0, 45);

    public static final Color SOMBRA_MEDIA =
            new Color(0, 0, 0, 80);

    public static final Color SOMBRA_FUERTE =
            new Color(0, 0, 0, 125);

    public static final Color OVERLAY_OSCURO =
            new Color(0, 0, 0, 180);

    public static final Color OVERLAY_SUAVE =
            new Color(0, 0, 0, 90);

    // ==========================================
    // 15. COLORES SEGUN RANGO
    // ==========================================

    /**
     * Devuelve el color principal
     * correspondiente a un rango.
     *
     * Se utiliza Object como parametro
     * para permitir uso independiente.
     *
     * Preferimos que el sistema visual
     * no dependa del modelo financiero.
     */
    public static Color colorRango(
            Object rango) {

        if (rango == null) {
            return TEXTO_SECUNDARIO;
        }

        String nombre =
                rango.toString().toUpperCase();

        return switch (nombre) {

            case "BRONCE" -> RANGO_BRONCE;

            case "PLATA" -> RANGO_PLATA;

            case "ORO" -> RANGO_ORO;

            case "PLATINO" -> RANGO_PLATINO;

            default -> TEXTO_SECUNDARIO;
        };
    }

    /**
     * Devuelve el fondo correspondiente
     * al rango indicado.
     */
    public static Color fondoRango(
            Object rango) {

        if (rango == null) {
            return FONDO_SECUNDARIO;
        }

        String nombre =
                rango.toString().toUpperCase();

        return switch (nombre) {

            case "BRONCE" -> RANGO_BRONCE_FONDO;

            case "PLATA" -> RANGO_PLATA_FONDO;

            case "ORO" -> RANGO_ORO_FONDO;

            case "PLATINO" -> RANGO_PLATINO_FONDO;

            default -> FONDO_SECUNDARIO;
        };
    }

    // ==========================================
    // 16. TRANSPARENCIA
    // ==========================================

    /**
     * Aplica un nivel de transparencia
     * a un color existente.
     *
     * Alpha:
     * 0   = completamente transparente.
     * 255 = completamente opaco.
     */
    public static Color conAlpha(
            Color color,
            int alpha) {

        if (color == null) {

            throw new IllegalArgumentException(
                    "El color no puede ser null"
            );
        }

        if (alpha < 0 || alpha > 255) {

            throw new IllegalArgumentException(
                    "Alpha debe estar entre 0 y 255"
            );
        }

        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                alpha
        );
    }

    // ==========================================
    // 17. MEZCLA DE COLORES
    // ==========================================

    /**
     * Mezcla dos colores.
     *
     * factor = 0.0 -> primer color.
     * factor = 1.0 -> segundo color.
     *
     * Util para hover y transiciones.
     */
    public static Color mezclar(
            Color primero,
            Color segundo,
            double factor) {

        if (primero == null || segundo == null) {

            throw new IllegalArgumentException(
                    "Los colores no pueden ser null"
            );
        }

        if (!Double.isFinite(factor)
                || factor < 0.0
                || factor > 1.0) {

            throw new IllegalArgumentException(
                    "El factor debe estar entre 0 y 1"
            );
        }

        int rojo = interpolar(
                primero.getRed(),
                segundo.getRed(),
                factor
        );

        int verde = interpolar(
                primero.getGreen(),
                segundo.getGreen(),
                factor
        );

        int azul = interpolar(
                primero.getBlue(),
                segundo.getBlue(),
                factor
        );

        int alpha = interpolar(
                primero.getAlpha(),
                segundo.getAlpha(),
                factor
        );

        return new Color(
                rojo,
                verde,
                azul,
                alpha
        );
    }

    private static int interpolar(
            int inicio,
            int fin,
            double factor) {

        return (int) Math.round(
                inicio + (fin - inicio) * factor
        );
    }

    // ==========================================
    // 18. ACLARAR COLOR
    // ==========================================

    /**
     * Mezcla un color con blanco.
     *
     * factor debe estar entre 0 y 1.
     */
    public static Color aclarar(
            Color color,
            double factor) {

        return mezclar(
                color,
                Color.WHITE,
                factor
        );
    }

    // ==========================================
    // 19. OSCURECER COLOR
    // ==========================================

    /**
     * Mezcla un color con negro.
     *
     * factor debe estar entre 0 y 1.
     */
    public static Color oscurecer(
            Color color,
            double factor) {

        return mezclar(
                color,
                Color.BLACK,
                factor
        );
    }

    // ==========================================
    // 20. CONVERTIR COLOR A HEXADECIMAL
    // ==========================================

    /**
     * Convierte un Color de Java
     * a su representacion hexadecimal.
     *
     * Ejemplo: #101113
     *
     * No incluye el canal alpha.
     */
    public static String aHex(
            Color color) {

        if (color == null) {

            throw new IllegalArgumentException(
                    "El color no puede ser null"
            );
        }

        return String.format(
                "#%02X%02X%02X",
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );
    }

    // ==========================================
    // 21. CONVERTIR HEXADECIMAL A COLOR
    // ==========================================

    /**
     * Convierte una cadena hexadecimal
     * a java.awt.Color.
     *
     * Acepta:
     *
     * #101113
     * 101113
     *
     * No acepta alpha en el texto.
     */
    public static Color desdeHex(
            String hexadecimal) {

        if (hexadecimal == null
                || hexadecimal.isBlank()) {

            throw new IllegalArgumentException(
                    "El hexadecimal es obligatorio"
            );
        }

        String valor =
                hexadecimal.trim();

        if (valor.startsWith("#")) {

            valor = valor.substring(1);
        }

        if (!valor.matches("[0-9a-fA-F]{6}")) {

            throw new IllegalArgumentException(
                    "El color debe tener seis "
                            + "digitos hexadecimales"
            );
        }

        return new Color(
                Integer.parseInt(valor, 16)
        );
    }

    // ==========================================
    // 22. CONTRASTE Y LEGIBILIDAD
    // ==========================================

    /**
     * Calcula la luminancia relativa
     * de un color segun sus componentes RGB.
     *
     * El resultado esta entre 0 y 1.
     *
     * No considera composicion alpha.
     */
    public static double luminancia(
            Color color) {

        if (color == null) {

            throw new IllegalArgumentException(
                    "El color no puede ser null"
            );
        }

        double rojo =
                canalLineal(color.getRed());

        double verde =
                canalLineal(color.getGreen());

        double azul =
                canalLineal(color.getBlue());

        return 0.2126 * rojo
                + 0.7152 * verde
                + 0.0722 * azul;
    }

    private static double canalLineal(
            int canal) {

        double valor =
                canal / 255.0;

        if (valor <= 0.04045) {

            return valor / 12.92;
        }

        return Math.pow(
                (valor + 0.055) / 1.055,
                2.4
        );
    }

    /**
     * Calcula la relacion de contraste
     * entre dos colores opacos.
     *
     * Resultado:
     * 1.0 = mismo brillo relativo.
     * 21.0 = maximo contraste teorico.
     */
    public static double relacionContraste(
            Color primero,
            Color segundo) {

        if (primero == null || segundo == null) {

            throw new IllegalArgumentException(
                    "Los colores no pueden ser null"
            );
        }

        if (primero.getAlpha() != 255
                || segundo.getAlpha() != 255) {

            throw new IllegalArgumentException(
                    "Los colores deben ser opacos "
                            + "para calcular el contraste"
            );
        }

        double a =
                luminancia(primero);

        double b =
                luminancia(segundo);

        double mayor =
                Math.max(a, b);

        double menor =
                Math.min(a, b);

        return (mayor + 0.05)
                / (menor + 0.05);
    }

    /**
     * Comprueba contraste minimo de 4.5:1.
     *
     * Util para texto normal.
     *
     * La legibilidad final tambien depende
     * del tamano y peso de la tipografia.
     */
    public static boolean contrasteTextoNormal(
            Color texto,
            Color fondo) {

        return relacionContraste(
                texto,
                fondo
        ) >= 4.5;
    }

    /**
     * Comprueba contraste minimo de 3:1.
     *
     * Util para texto grande.
     */
    public static boolean contrasteTextoGrande(
            Color texto,
            Color fondo) {

        return relacionContraste(
                texto,
                fondo
        ) >= 3.0;
    }

    // ==========================================
    // 23. COLOR DE TEXTO AUTOMATICO
    // ==========================================

    /**
     * Selecciona entre blanco hueso
     * y negro carbon dependiendo del
     * contraste sobre un fondo.
     *
     * Ideal para etiquetas y botones.
     */
    public static Color textoSobre(
            Color fondo) {

        if (fondo == null) {

            throw new IllegalArgumentException(
                    "El fondo no puede ser null"
            );
        }

        if (fondo.getAlpha() != 255) {

            throw new IllegalArgumentException(
                    "El fondo debe ser opaco"
            );
        }

        double contrasteClaro =
                relacionContraste(
                        BONE,
                        fondo
                );

        double contrasteOscuro =
                relacionContraste(
                        OBSIDIAN,
                        fondo
                );

        return contrasteClaro >= contrasteOscuro
                ? BONE
                : OBSIDIAN;
    }

    // ==========================================
    // 24. DESCRIPCION DEL SISTEMA VISUAL
    // ==========================================

    public static String obtenerDescripcion() {

        return """
                MONEDERO VIRTUAL
                PRIVATE LEDGER

                SISTEMA VISUAL:
                INSTITUTIONAL NOIR

                Paleta:
                Obsidian  #101113
                Graphite  #17181B
                Elevated  #202125
                Divider   #353638
                Bone      #F1EFE9
                Ash       #A2A3A6
                Champagne #C7A77A
                Sage      #AFC6B0
                Clay      #D5A095

                Principios:
                - Minimalismo editorial
                - Precision financiera
                - Tipografia protagonista
                - Contraste controlado
                - Ornamentacion minima
                - Elegancia funcional
                """;
    }
}
