
package vista.tema;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * ============================================================
 *                       MONEDERO VIRTUAL
 * ============================================================
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * CLASE:
 * Espaciado.java
 *
 * RESPONSABILIDADES:
 *
 * 1. Centralizar medidas y proporciones.
 * 2. Definir la reticula de la aplicacion.
 * 3. Normalizar margenes y separaciones.
 * 4. Establecer dimensiones de componentes.
 * 5. Proporcionar utilidades de distribucion.
 * 6. Facilitar interfaces adaptables.
 *
 * UNIDADES:
 *
 * Todas las dimensiones estan expresadas
 * en unidades logicas de Java Swing.
 *
 * No representan necesariamente pixeles
 * fisicos del monitor.
 *
 * DIRECCION ARTISTICA:
 *
 * - Composicion editorial.
 * - Espacios negativos amplios.
 * - Reticula consistente.
 * - Navegacion lateral equilibrada.
 * - Tarjetas sobrias.
 * - Componentes de altura uniforme.
 * - Proporciones controladas.
 *
 * REGLA FUNDAMENTAL:
 *
 * Las vistas deben utilizar las constantes
 * de esta clase en lugar de introducir
 * valores arbitrarios.
 *
 * Java 21.
 */
public final class Espaciado {

    // ========================================================
    // CONSTRUCTOR PRIVADO
    // ========================================================

    private Espaciado() {

        throw new UnsupportedOperationException(
                "Espaciado es una clase de utilidades"
        );
    }

    // ========================================================
    // 1. ESCALA BASE
    // ========================================================

    /**
     * Sistema basado en una escala de 4 unidades.
     *
     * Permite construir separaciones consistentes
     * sin depender de valores improvisados.
     */

    public static final int NINGUNO = 0;

    public static final int XXS = 2;

    public static final int XS = 4;

    public static final int SM = 8;

    public static final int MD = 12;

    public static final int LG = 16;

    public static final int XL = 20;

    public static final int XXL = 24;

    public static final int XXXL = 32;

    public static final int ESPACIO_40 = 40;

    public static final int ESPACIO_48 = 48;

    public static final int ESPACIO_56 = 56;

    public static final int ESPACIO_64 = 64;

    public static final int ESPACIO_80 = 80;

    public static final int ESPACIO_96 = 96;

    // ========================================================
    // 2. VENTANA PRINCIPAL
    // ========================================================

    /**
     * Resolucion de referencia del dashboard.
     *
     * La ventana real debera adaptarse
     * al area disponible del monitor.
     */

    public static final int VENTANA_ANCHO_REFERENCIA =
            1440;

    public static final int VENTANA_ALTO_REFERENCIA =
            900;

    /**
     * Resolucion minima recomendada.
     *
     * En ventanas mas pequenas, los paneles
     * deberan permitir desplazamiento.
     */

    public static final int VENTANA_ANCHO_MINIMO =
            1024;

    public static final int VENTANA_ALTO_MINIMO =
            640;

    public static final int VENTANA_ANCHO_INICIAL =
            1360;

    public static final int VENTANA_ALTO_INICIAL =
            820;

    // ========================================================
    // 3. SIDEBAR
    // ========================================================

    /**
     * Barra lateral principal.
     */

    public static final int SIDEBAR_ANCHO =
            248;

    public static final int SIDEBAR_ANCHO_COMPACTO =
            216;

    public static final int SIDEBAR_ANCHO_COLAPSADO =
            72;

    public static final int SIDEBAR_PADDING_HORIZONTAL =
            20;

    public static final int SIDEBAR_PADDING_VERTICAL =
            28;

    public static final int SIDEBAR_LOGO_ALTURA =
            56;

    public static final int SIDEBAR_LOGO_SEPARACION =
            40;

    public static final int SIDEBAR_ITEM_ALTURA =
            46;

    public static final int SIDEBAR_ITEM_SEPARACION =
            6;

    public static final int SIDEBAR_ITEM_PADDING =
            14;

    public static final int SIDEBAR_ICONO =
            18;

    public static final int SIDEBAR_ICONO_TEXTO =
            14;

    public static final int SIDEBAR_INDICADOR_ANCHO =
            3;

    public static final int SIDEBAR_GRUPO_SEPARACION =
            28;

    public static final int SIDEBAR_FOOTER_ALTURA =
            64;

    // ========================================================
    // 4. CONTENIDO PRINCIPAL
    // ========================================================

    /**
     * Margenes editoriales del dashboard.
     */

    public static final int CONTENIDO_PADDING_HORIZONTAL =
            40;

    public static final int CONTENIDO_PADDING_VERTICAL =
            36;

    public static final int CONTENIDO_PADDING_COMPACTO =
            24;

    public static final int CONTENIDO_PADDING_MINIMO =
            16;

    public static final int CONTENIDO_ANCHO_MINIMO =
            640;

    public static final int CONTENIDO_ANCHO_MAXIMO =
            1600;

    public static final int CONTENIDO_SEPARACION_SECCIONES =
            40;

    public static final int CONTENIDO_SEPARACION_BLOQUES =
            24;

    public static final int CONTENIDO_SEPARACION_ELEMENTOS =
            16;

    // ========================================================
    // 5. RETICULA EDITORIAL
    // ========================================================

    /**
     * Reticula conceptual de doce columnas.
     *
     * Swing no incluye CSS Grid.
     *
     * Estos valores seran utilizados por
     * nuestros gestores de distribucion
     * y paneles personalizados.
     */

    public static final int GRID_COLUMNAS =
            12;

    public static final int GRID_SEPARACION_HORIZONTAL =
            24;

    public static final int GRID_SEPARACION_VERTICAL =
            24;

    public static final int GRID_COLUMNAS_COMPACTO =
            6;

    public static final int GRID_COLUMNAS_MOVIL =
            1;

    // ========================================================
    // 6. BREAKPOINTS
    // ========================================================

    /**
     * Los breakpoints se evaluan utilizando
     * el ancho disponible del CONTENIDO.
     *
     * No el ancho total de la pantalla.
     */

    public static final int BREAKPOINT_COMPACTO =
            760;

    public static final int BREAKPOINT_MEDIO =
            1080;

    public static final int BREAKPOINT_AMPLIO =
            1440;

    public enum ModoDiseno {

        ESTRECHO,
        COMPACTO,
        NORMAL,
        AMPLIO
    }

    /**
     * Determina la distribucion recomendada
     * segun el ancho util del contenido.
     */
    public static ModoDiseno obtenerModo(
            int anchoDisponible) {

        if (anchoDisponible < 0) {

            throw new IllegalArgumentException(
                    "El ancho no puede ser negativo"
            );
        }

        if (anchoDisponible < BREAKPOINT_COMPACTO) {

            return ModoDiseno.ESTRECHO;
        }

        if (anchoDisponible < BREAKPOINT_MEDIO) {

            return ModoDiseno.COMPACTO;
        }

        if (anchoDisponible < BREAKPOINT_AMPLIO) {

            return ModoDiseno.NORMAL;
        }

        return ModoDiseno.AMPLIO;
    }

    /**
     * Cantidad recomendada de columnas
     * para tarjetas estadisticas.
     */
    public static int columnasEstadisticas(
            int anchoDisponible) {

        ModoDiseno modo =
                obtenerModo(anchoDisponible);

        return switch (modo) {

            case ESTRECHO -> 1;

            case COMPACTO -> 2;

            case NORMAL -> 3;

            case AMPLIO -> 4;
        };
    }

    /**
     * Columnas disponibles para la reticula.
     */
    public static int columnasReticula(
            int anchoDisponible) {

        ModoDiseno modo =
                obtenerModo(anchoDisponible);

        return switch (modo) {

            case ESTRECHO -> GRID_COLUMNAS_MOVIL;

            case COMPACTO -> GRID_COLUMNAS_COMPACTO;

            case NORMAL, AMPLIO -> GRID_COLUMNAS;
        };
    }

    // ========================================================
    // 7. ENCABEZADOS
    // ========================================================

    public static final int HEADER_ALTURA =
            72;

    public static final int HEADER_ALTURA_COMPACTA =
            60;

    public static final int HEADER_TITULO_SEPARACION =
            8;

    public static final int HEADER_ACCIONES_SEPARACION =
            12;

    public static final int HEADER_SEPARACION_INFERIOR =
            36;

    // ========================================================
    // 8. DASHBOARD
    // ========================================================

    /**
     * Secciones principales del PanelInicio.
     */

    public static final int DASHBOARD_BALANCE_ALTURA =
            232;

    public static final int DASHBOARD_BALANCE_ALTURA_COMPACTA =
            190;

    public static final int DASHBOARD_RESUMEN_ALTURA =
            130;

    public static final int DASHBOARD_TARJETA_ALTURA =
            136;

    public static final int DASHBOARD_ACTIVIDAD_ALTURA =
            340;

    public static final int DASHBOARD_GRAFICO_ALTURA =
            280;

    public static final int DASHBOARD_RANGO_ALTURA =
            220;

    public static final int DASHBOARD_ACCIONES_ALTURA =
            64;

    public static final int DASHBOARD_SEPARACION =
            24;

    /**
     * Distribucion conceptual:
     *
     * 8 columnas para informacion principal.
     * 4 columnas para panel secundario.
     */

    public static final int DASHBOARD_COLUMNAS_PRINCIPALES =
            8;

    public static final int DASHBOARD_COLUMNAS_LATERALES =
            4;

    // ========================================================
    // 9. TARJETAS
    // ========================================================

    public static final int TARJETA_PADDING =
            24;

    public static final int TARJETA_PADDING_COMPACTO =
            18;

    public static final int TARJETA_PADDING_GRANDE =
            32;

    public static final int TARJETA_SEPARACION_INTERNA =
            16;

    public static final int TARJETA_SEPARACION_TITULO =
            10;

    public static final int TARJETA_ALTURA_MINIMA =
            112;

    public static final int TARJETA_ALTURA_MEDIA =
            160;

    public static final int TARJETA_ALTURA_GRANDE =
            240;

    // ========================================================
    // 10. RADIOS
    // ========================================================

    /**
     * Institutional Noir utiliza
     * esquinas discretamente redondeadas.
     *
     * Valores expresados como radio.
     */

    public static final int RADIO_NINGUNO =
            0;

    public static final int RADIO_PEQUENO =
            4;

    public static final int RADIO_MEDIO =
            6;

    public static final int RADIO_GRANDE =
            8;

    public static final int RADIO_EXTRA =
            12;

    public static final int RADIO_PILDORA =
            999;

    public static final int RADIO_BOTON =
            RADIO_MEDIO;

    public static final int RADIO_TARJETA =
            RADIO_GRANDE;

    public static final int RADIO_CAMPO =
            RADIO_MEDIO;

    public static final int RADIO_DIALOGO =
            RADIO_EXTRA;

    // ========================================================
    // 11. BOTONES
    // ========================================================

    public static final int BOTON_ALTURA =
            44;

    public static final int BOTON_ALTURA_PEQUENO =
            36;

    public static final int BOTON_ALTURA_GRANDE =
            52;

    public static final int BOTON_ANCHO_MINIMO =
            112;

    public static final int BOTON_ANCHO_ACCION =
            148;

    public static final int BOTON_PADDING_HORIZONTAL =
            20;

    public static final int BOTON_PADDING_VERTICAL =
            10;

    public static final int BOTON_ICONO =
            18;

    public static final int BOTON_SEPARACION_ICONO =
            10;

    // ========================================================
    // 12. CAMPOS Y FORMULARIOS
    // ========================================================

    public static final int INPUT_ALTURA =
            48;

    public static final int INPUT_ALTURA_COMPACTA =
            40;

    public static final int INPUT_ALTURA_GRANDE =
            56;

    public static final int INPUT_PADDING_HORIZONTAL =
            14;

    public static final int INPUT_PADDING_VERTICAL =
            10;

    public static final int FORMULARIO_SEPARACION_CAMPOS =
            20;

    public static final int FORMULARIO_SEPARACION_GRUPOS =
            32;

    public static final int FORMULARIO_ETIQUETA_SEPARACION =
            8;

    public static final int FORMULARIO_ANCHO_RECOMENDADO =
            520;

    public static final int FORMULARIO_ANCHO_MAXIMO =
            680;

    // ========================================================
    // 13. TABLAS
    // ========================================================

    public static final int TABLA_CABECERA_ALTURA =
            48;

    public static final int TABLA_FILA_ALTURA =
            52;

    public static final int TABLA_FILA_COMPACTA =
            44;

    public static final int TABLA_PADDING_HORIZONTAL =
            18;

    public static final int TABLA_PADDING_VERTICAL =
            12;

    public static final int TABLA_ICONO =
            18;

    public static final int TABLA_ICONO_FONDO =
            36;

    public static final int TABLA_SEPARACION_COLUMNAS =
            20;

    // ========================================================
    // 14. ESTADOS Y ETIQUETAS
    // ========================================================

    public static final int BADGE_ALTURA =
            26;

    public static final int BADGE_PADDING_HORIZONTAL =
            10;

    public static final int BADGE_PADDING_VERTICAL =
            5;

    public static final int BADGE_RADIO =
            RADIO_PEQUENO;

    public static final int INDICADOR_ESTADO =
            7;

    public static final int INDICADOR_SEPARACION =
            8;

    // ========================================================
    // 15. ICONOGRAFIA
    // ========================================================

    public static final int ICONO_MICRO =
            12;

    public static final int ICONO_PEQUENO =
            16;

    public static final int ICONO_NORMAL =
            20;

    public static final int ICONO_GRANDE =
            24;

    public static final int ICONO_DESTACADO =
            32;

    public static final float ICONO_GROSOR =
            1.75f;

    // ========================================================
    // 16. DIALOGOS
    // ========================================================

    public static final int DIALOGO_ANCHO_PEQUENO =
            420;

    public static final int DIALOGO_ANCHO_MEDIO =
            560;

    public static final int DIALOGO_ANCHO_GRANDE =
            760;

    public static final int DIALOGO_PADDING =
            32;

    public static final int DIALOGO_SEPARACION =
            24;

    public static final int DIALOGO_ACCIONES_SEPARACION =
            12;

    // ========================================================
    // 17. NOTIFICACIONES
    // ========================================================

    public static final int NOTIFICACION_ANCHO =
            360;

    public static final int NOTIFICACION_ALTURA_MINIMA =
            76;

    public static final int NOTIFICACION_PADDING =
            16;

    public static final int NOTIFICACION_SEPARACION =
            12;

    // ========================================================
    // 18. ANIMACIONES
    // ========================================================

    /**
     * Duraciones expresadas en milisegundos.
     *
     * Estos valores seran utilizados
     * por componentes Swing personalizados.
     */

    public static final int ANIMACION_INSTANTANEA =
            90;

    public static final int ANIMACION_RAPIDA =
            160;

    public static final int ANIMACION_NORMAL =
            220;

    public static final int ANIMACION_LENTA =
            280;

    public static final int ANIMACION_SIDEBAR =
            220;

    public static final int ANIMACION_CAMBIO_PANEL =
            180;

    public static final int ANIMACION_NOTIFICACION =
            240;

    public static final int ANIMACION_HOVER =
            140;

    /**
     * Intervalo orientativo para animaciones
     * de aproximadamente 60 FPS.
     */
    public static final int ANIMACION_FRAME_MS =
            16;

    // ========================================================
    // 19. BORDES Y GROSORES
    // ========================================================

    public static final int BORDE_FIN0 =
            0;

    public static final int BORDE_FINO =
            1;

    public static final int BORDE_MEDIO =
            2;

    public static final int BORDE_FUERTE =
            3;

    public static final int DIVISOR_GROSOR =
            1;

    // ========================================================
    // 20. TAMAÑOS PREDEFINIDOS
    // ========================================================

    /**
     * Devuelve una dimension nueva
     * para evitar que los componentes
     * compartan objetos mutables.
     */

    public static Dimension ventanaInicial() {

        return new Dimension(
                VENTANA_ANCHO_INICIAL,
                VENTANA_ALTO_INICIAL
        );
    }

    public static Dimension ventanaMinima() {

        return new Dimension(
                VENTANA_ANCHO_MINIMO,
                VENTANA_ALTO_MINIMO
        );
    }

    public static Dimension ventanaReferencia() {

        return new Dimension(
                VENTANA_ANCHO_REFERENCIA,
                VENTANA_ALTO_REFERENCIA
        );
    }

    public static Dimension botonNormal() {

        return new Dimension(
                BOTON_ANCHO_MINIMO,
                BOTON_ALTURA
        );
    }

    public static Dimension botonGrande() {

        return new Dimension(
                BOTON_ANCHO_ACCION,
                BOTON_ALTURA_GRANDE
        );
    }

    public static Dimension campoNormal(
            int ancho) {

        validarNoNegativo(
                ancho,
                "El ancho"
        );

        return new Dimension(
                ancho,
                INPUT_ALTURA
        );
    }

    public static Dimension sidebar(
            int alto) {

        validarNoNegativo(
                alto,
                "El alto"
        );

        return new Dimension(
                SIDEBAR_ANCHO,
                alto
        );
    }

    public static Dimension tarjetaEstadistica(
            int ancho) {

        validarNoNegativo(
                ancho,
                "El ancho"
        );

        return new Dimension(
                ancho,
                DASHBOARD_TARJETA_ALTURA
        );
    }

    // ========================================================
    // 21. INSETS
    // ========================================================

    /**
     * Crea margenes internos uniformes.
     */
    public static Insets insets(
            int valor) {

        validarNoNegativo(
                valor,
                "El espaciado"
        );

        return new Insets(
                valor,
                valor,
                valor,
                valor
        );
    }

    /**
     * Crea margenes internos personalizados.
     *
     * Orden:
     * arriba, izquierda, abajo, derecha.
     */
    public static Insets insets(
            int arriba,
            int izquierda,
            int abajo,
            int derecha) {

        validarNoNegativo(arriba, "Arriba");
        validarNoNegativo(izquierda, "Izquierda");
        validarNoNegativo(abajo, "Abajo");
        validarNoNegativo(derecha, "Derecha");

        return new Insets(
                arriba,
                izquierda,
                abajo,
                derecha
        );
    }

    /**
     * Margen interno estandar de tarjetas.
     */
    public static Insets insetsTarjeta() {

        return insets(
                TARJETA_PADDING
        );
    }

    /**
     * Margen del contenido principal.
     */
    public static Insets insetsContenido() {

        return insets(
                CONTENIDO_PADDING_VERTICAL,
                CONTENIDO_PADDING_HORIZONTAL,
                CONTENIDO_PADDING_VERTICAL,
                CONTENIDO_PADDING_HORIZONTAL
        );
    }

    /**
     * Margen de la barra lateral.
     */
    public static Insets insetsSidebar() {

        return insets(
                SIDEBAR_PADDING_VERTICAL,
                SIDEBAR_PADDING_HORIZONTAL,
                SIDEBAR_PADDING_VERTICAL,
                SIDEBAR_PADDING_HORIZONTAL
        );
    }

    /**
     * Margen de campos de texto.
     */
    public static Insets insetsCampo() {

        return insets(
                INPUT_PADDING_VERTICAL,
                INPUT_PADDING_HORIZONTAL,
                INPUT_PADDING_VERTICAL,
                INPUT_PADDING_HORIZONTAL
        );
    }

    // ========================================================
    // 22. BORDES VACIOS
    // ========================================================

    /**
     * Construye un borde vacio uniforme.
     */
    public static Border bordeVacio(
            int valor) {

        validarNoNegativo(
                valor,
                "El espaciado"
        );

        return BorderFactory.createEmptyBorder(
                valor,
                valor,
                valor,
                valor
        );
    }

    /**
     * Construye un borde vacio personalizado.
     */
    public static Border bordeVacio(
            int arriba,
            int izquierda,
            int abajo,
            int derecha) {

        validarNoNegativo(arriba, "Arriba");
        validarNoNegativo(izquierda, "Izquierda");
        validarNoNegativo(abajo, "Abajo");
        validarNoNegativo(derecha, "Derecha");

        return BorderFactory.createEmptyBorder(
                arriba,
                izquierda,
                abajo,
                derecha
        );
    }

    public static Border bordeTarjeta() {

        return bordeVacio(
                TARJETA_PADDING
        );
    }

    public static Border bordeContenido() {

        return bordeVacio(
                CONTENIDO_PADDING_VERTICAL,
                CONTENIDO_PADDING_HORIZONTAL,
                CONTENIDO_PADDING_VERTICAL,
                CONTENIDO_PADDING_HORIZONTAL
        );
    }

    public static Border bordeSidebar() {

        return bordeVacio(
                SIDEBAR_PADDING_VERTICAL,
                SIDEBAR_PADDING_HORIZONTAL,
                SIDEBAR_PADDING_VERTICAL,
                SIDEBAR_PADDING_HORIZONTAL
        );
    }

    // ========================================================
    // 23. APLICAR ESPACIADO A UN COMPONENTE
    // ========================================================

    /**
     * Aplica un borde interno vacio
     * a un componente Swing.
     *
     * Sustituye el borde anterior.
     *
     * No debe utilizarse sobre componentes
     * cuyo borde deba conservarse.
     */
    public static void aplicarPadding(
            JComponent componente,
            int valor) {

        if (componente == null) {

            throw new IllegalArgumentException(
                    "El componente no puede ser null"
            );
        }

        componente.setBorder(
                bordeVacio(valor)
        );
    }

    /**
     * Aplica margenes personalizados.
     */
    public static void aplicarPadding(
            JComponent componente,
            int arriba,
            int izquierda,
            int abajo,
            int derecha) {

        if (componente == null) {

            throw new IllegalArgumentException(
                    "El componente no puede ser null"
            );
        }

        componente.setBorder(
                bordeVacio(
                        arriba,
                        izquierda,
                        abajo,
                        derecha
                )
        );
    }

    // ========================================================
    // 24. CALCULAR COLUMNAS DE GRID
    // ========================================================

    /**
     * Calcula el ancho individual de una
     * columna utilizando una reticula.
     *
     * Parametros:
     *
     * anchoDisponible:
     * Espacio total de la reticula.
     *
     * columnas:
     * Numero de columnas.
     *
     * separacion:
     * Espacio entre columnas.
     */
    public static int anchoColumna(
            int anchoDisponible,
            int columnas,
            int separacion) {

        validarNoNegativo(
                anchoDisponible,
                "El ancho disponible"
        );

        if (columnas <= 0) {

            throw new IllegalArgumentException(
                    "Las columnas deben ser mayores que cero"
            );
        }

        validarNoNegativo(
                separacion,
                "La separacion"
        );

        long espacios =
                (long) (columnas - 1) * separacion;

        long util =
                (long) anchoDisponible - espacios;

        if (util <= 0) {
            return 0;
        }

        return (int) (util / columnas);
    }

    /**
     * Calcula el ancho de un bloque que
     * ocupa varias columnas.
     *
     * Ejemplo:
     * Un bloque de ocho columnas dentro
     * de una reticula de doce.
     */
    public static int anchoBloque(
            int anchoDisponible,
            int columnasTotales,
            int columnasOcupadas,
            int separacion) {

        if (columnasTotales <= 0) {

            throw new IllegalArgumentException(
                    "El total de columnas debe ser positivo"
            );
        }

        if (columnasOcupadas <= 0
                || columnasOcupadas > columnasTotales) {

            throw new IllegalArgumentException(
                    "Cantidad de columnas ocupadas invalida"
            );
        }

        int columna =
                anchoColumna(
                        anchoDisponible,
                        columnasTotales,
                        separacion
                );

        long resultado =
                (long) columna * columnasOcupadas
                        + (long) separacion
                        * (columnasOcupadas - 1);

        return (int) Math.min(
                Integer.MAX_VALUE,
                resultado
        );
    }

    /**
     * Calcula una distribucion en doce columnas.
     */
    public static int anchoGrid12(
            int anchoDisponible,
            int columnasOcupadas) {

        return anchoBloque(
                anchoDisponible,
                GRID_COLUMNAS,
                columnasOcupadas,
                GRID_SEPARACION_HORIZONTAL
        );
    }

    // ========================================================
    // 25. DISTRIBUCION DE TARJETAS
    // ========================================================

    /**
     * Devuelve el ancho recomendado para
     * tarjetas distribuidas uniformemente.
     */
    public static int anchoTarjeta(
            int anchoDisponible,
            int cantidadColumnas) {

        return anchoColumna(
                anchoDisponible,
                cantidadColumnas,
                GRID_SEPARACION_HORIZONTAL
        );
    }

    // ========================================================
    // 26. LIMITAR VALORES
    // ========================================================

    /**
     * Limita un valor entre un minimo
     * y un maximo.
     */
    public static int limitar(
            int valor,
            int minimo,
            int maximo) {

        if (minimo > maximo) {

            throw new IllegalArgumentException(
                    "El minimo no puede superar "
                            + "el maximo"
            );
        }

        return Math.max(
                minimo,
                Math.min(valor, maximo)
        );
    }

    /**
     * Limita un valor decimal.
     */
    public static double limitar(
            double valor,
            double minimo,
            double maximo) {

        if (!Double.isFinite(valor)
                || !Double.isFinite(minimo)
                || !Double.isFinite(maximo)) {

            throw new IllegalArgumentException(
                    "Los valores deben ser finitos"
            );
        }

        if (minimo > maximo) {

            throw new IllegalArgumentException(
                    "El minimo no puede superar "
                            + "el maximo"
            );
        }

        return Math.max(
                minimo,
                Math.min(valor, maximo)
        );
    }

    // ========================================================
    // 27. CENTRAR RECTANGULOS
    // ========================================================

    /**
     * Calcula un rectangulo centrado
     * dentro de otro.
     *
     * Util para componentes dibujados
     * mediante Java2D.
     */
    public static Rectangle centrar(
            Rectangle contenedor,
            int ancho,
            int alto) {

        if (contenedor == null) {

            throw new IllegalArgumentException(
                    "El contenedor no puede ser null"
            );
        }

        validarNoNegativo(ancho, "El ancho");
        validarNoNegativo(alto, "El alto");

        int x = contenedor.x
                + (contenedor.width - ancho) / 2;

        int y = contenedor.y
                + (contenedor.height - alto) / 2;

        return new Rectangle(
                x,
                y,
                ancho,
                alto
        );
    }

    // ========================================================
    // 28. CALCULAR AREA DE CONTENIDO
    // ========================================================

    /**
     * Calcula el ancho util horizontal
     * del contenido principal.
     *
     * Se descuenta la barra lateral
     * y el padding horizontal.
     */
    public static int anchoContenido(
            int anchoVentana,
            boolean sidebarColapsada) {

        validarNoNegativo(
                anchoVentana,
                "El ancho de ventana"
        );

        int anchoBarra =
                sidebarColapsada
                        ? SIDEBAR_ANCHO_COLAPSADO
                        : SIDEBAR_ANCHO;

        return Math.max(
                0,
                anchoVentana
                        - anchoBarra
                        - CONTENIDO_PADDING_HORIZONTAL * 2
        );
    }

    /**
     * Calcula el area util vertical.
     */
    public static int altoContenido(
            int altoVentana) {

        validarNoNegativo(
                altoVentana,
                "El alto de ventana"
        );

        return Math.max(
                0,
                altoVentana
                        - CONTENIDO_PADDING_VERTICAL * 2
        );
    }

    // ========================================================
    // 29. OBTENER PADDING ADAPTABLE
    // ========================================================

    /**
     * Devuelve el padding horizontal
     * adecuado para el ancho disponible.
     */
    public static int paddingAdaptable(
            int anchoDisponible) {

        ModoDiseno modo =
                obtenerModo(anchoDisponible);

        return switch (modo) {

            case ESTRECHO ->
                    CONTENIDO_PADDING_MINIMO;

            case COMPACTO ->
                    CONTENIDO_PADDING_COMPACTO;

            case NORMAL, AMPLIO ->
                    CONTENIDO_PADDING_HORIZONTAL;
        };
    }

    /**
     * Genera Insets adaptables.
     */
    public static Insets insetsAdaptables(
            int anchoDisponible) {

        int padding =
                paddingAdaptable(anchoDisponible);

        return insets(
                padding,
                padding,
                padding,
                padding
        );
    }

    // ========================================================
    // 30. VALIDACIONES
    // ========================================================

    private static void validarNoNegativo(
            int valor,
            String nombre) {

        if (valor < 0) {

            throw new IllegalArgumentException(
                    nombre + " no puede ser negativo"
            );
        }
    }

    // ========================================================
    // 31. DESCRIPCION DEL SISTEMA
    // ========================================================

    /**
     * Genera un resumen de proporciones
     * para documentacion del proyecto.
     */
    public static String generarInforme() {

        return """
                ========================================
                MONEDERO VIRTUAL
                PRIVATE LEDGER
                ========================================

                SISTEMA DE ESPACIADO
                INSTITUTIONAL NOIR

                ESCALA BASE:
                2, 4, 8, 12, 16, 20, 24, 32, 40

                VENTANA:
                Referencia: 1440 x 900
                Inicial:    1360 x 820
                Minima:     1024 x 640

                NAVEGACION:
                Sidebar: 248
                Compacta: 216
                Colapsada: 72

                CONTENIDO:
                Padding horizontal: 40
                Padding vertical:   36

                RETICULA:
                12 columnas
                Separacion: 24

                BOTONES:
                Normal: 44
                Grande: 52

                CAMPOS:
                Normal: 48

                TABLAS:
                Cabecera: 48
                Filas: 52

                RADIOS:
                Pequeno: 4
                Medio: 6
                Grande: 8

                ANIMACIONES:
                Rapida: 160 ms
                Normal: 220 ms
                Lenta: 280 ms

                ========================================
                """;
    }
}
