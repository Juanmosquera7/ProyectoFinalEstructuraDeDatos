
package vista.tema;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.JTableHeader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;

/**
 * ============================================================
 *                     MONEDERO VIRTUAL
 * ============================================================
 *
 * SISTEMA VISUAL:
 * Institutional Noir / Private Ledger
 *
 * CLASE:
 * TemaAplicacion.java
 *
 * RESPONSABILIDADES:
 *
 * 1. Inicializar la apariencia general.
 * 2. Integrar Paleta, Tipografia y Espaciado.
 * 3. Configurar los valores globales de Swing.
 * 4. Establecer estilos visuales reutilizables.
 * 5. Proporcionar utilidades para ventanas.
 * 6. Mantener consistencia entre los paneles.
 *
 * DIRECCION ARTISTICA:
 *
 * - Minimalismo editorial.
 * - Superficies Obsidian y Graphite.
 * - Tipografia elegante y legible.
 * - Champagne como acento principal.
 * - Bordes finos y discretos.
 * - Ausencia de decoraciones innecesarias.
 *
 * IMPORTANTE:
 *
 * Esta clase configura controles Swing.
 *
 * Los componentes personalizados como:
 *
 * - BotonPremium
 * - Sidebar
 * - TarjetaEstadistica
 * - CampoMonetario
 * - TablaPremium
 *
 * implementaran sus propios detalles
 * mediante Java2D cuando sea necesario.
 *
 * No requiere dependencias externas.
 *
 * Compatible con Java 21.
 */
public final class TemaAplicacion {

    // ========================================================
    // 1. IDENTIDAD DEL SISTEMA
    // ========================================================

    public static final String NOMBRE_TEMA =
            "Institutional Noir";

    public static final String IDENTIDAD =
            "Monedero / Private Ledger";

    public static final String VERSION_TEMA =
            "1.0.0";

    // ========================================================
    // 2. ESTADO INTERNO
    // ========================================================

    private static boolean inicializado = false;

    private static String aparienciaActiva =
            "No inicializada";

    // ========================================================
    // 3. CONSTRUCTOR PRIVADO
    // ========================================================

    private TemaAplicacion() {

        throw new UnsupportedOperationException(
                "TemaAplicacion no puede instanciarse"
        );
    }

    // ========================================================
    // 4. INICIALIZACION GENERAL
    // ========================================================

    /**
     * Aplica el sistema visual global.
     *
     * Debe ejecutarse antes de construir
     * las ventanas de la aplicacion.
     *
     * Se recomienda llamarlo desde
     * SwingUtilities.invokeLater().
     */
    public static synchronized void inicializar() {

        if (inicializado) {
            return;
        }

        // Cargar las fuentes del proyecto.

        Tipografia.inicializar();

        // Instalar apariencia base.

        instalarLookAndFeel();

        // Aplicar todos los estilos.

        configurarColoresGenerales();

        configurarTipografiaGeneral();

        configurarBotones();

        configurarCamposTexto();

        configurarAreasTexto();

        configurarTablas();

        configurarListas();

        configurarCombos();

        configurarScroll();

        configurarMenus();

        configurarPestanas();

        configurarDialogos();

        configurarHerramientas();

        configurarSeparadores();

        configurarControlesAdicionales();

        inicializado = true;

        System.out.println(
                "[TEMA] " + NOMBRE_TEMA
                        + " inicializado correctamente."
        );

        System.out.println(
                "[TEMA] Look and Feel: "
                        + aparienciaActiva
        );

        // Diagnostico de fuentes reales del proyecto.
        // No modifica los estilos establecidos por cada panel.
        System.out.println(
                "[TEMA] Fuentes TTF cargadas: "
                        + Tipografia.cantidadArchivosCargados()
                        + "/9"
        );

        System.out.println(
                "[TEMA] Familias personalizadas: "
                        + Tipografia.todasPersonalizadas()
        );

        if (!Tipografia.todasPersonalizadas()
                || Tipografia.cantidadArchivosCargados() != 9) {

            System.err.println(
                    "[TEMA] ADVERTENCIA: algunas fuentes "
                            + "no se cargaron; revisa out/recursos/fuentes"
            );
        }

    }

    // ========================================================
    // 5. LOOK AND FEEL
    // ========================================================

    /**
     * Utilizamos el aspecto multiplataforma
     * de Java como base independiente.
     *
     * Los colores y las fuentes se
     * personalizan posteriormente.
     *
     * La implementacion no depende de
     * librerias externas.
     */
    private static void instalarLookAndFeel() {

        try {

            UIManager.setLookAndFeel(
                    UIManager
                            .getCrossPlatformLookAndFeelClassName()
            );

        } catch (Exception error) {

            System.err.println(
                    "[TEMA] No se pudo instalar "
                            + "el aspecto multiplataforma: "
                            + error.getMessage()
            );
        }

        aparienciaActiva =
                UIManager.getLookAndFeel().getName();
    }

    // ========================================================
    // 6. COLORES GENERALES
    // ========================================================

    private static void configurarColoresGenerales() {

        // Paneles.

        UIManager.put(
                "Panel.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "Panel.foreground",
                Paleta.TEXTO_PRINCIPAL
        );

        // Etiquetas.

        UIManager.put(
                "Label.foreground",
                Paleta.TEXTO_PRINCIPAL
        );

        UIManager.put(
                "Label.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "Label.disabledForeground",
                Paleta.TEXTO_DESHABILITADO
        );

        // Escritorio.

        UIManager.put(
                "Desktop.background",
                Paleta.FONDO_PRINCIPAL
        );

        // Ventanas internas.

        UIManager.put(
                "InternalFrame.activeTitleBackground",
                Paleta.GRAPHITE
        );

        UIManager.put(
                "InternalFrame.activeTitleForeground",
                Paleta.BONE
        );

        UIManager.put(
                "InternalFrame.inactiveTitleBackground",
                Paleta.ELEVATED
        );

        UIManager.put(
                "InternalFrame.inactiveTitleForeground",
                Paleta.ASH
        );

        // Selecciones generales.

        UIManager.put(
                "textHighlight",
                Paleta.ACENTO_SUAVE
        );

        UIManager.put(
                "textHighlightText",
                Paleta.BONE
        );

        UIManager.put(
                "control",
                Paleta.GRAPHITE
        );

        UIManager.put(
                "controlText",
                Paleta.BONE
        );

        UIManager.put(
                "controlHighlight",
                Paleta.ELEVATED
        );

        UIManager.put(
                "controlShadow",
                Paleta.DIVIDER
        );
    }

    // ========================================================
    // 7. TIPOGRAFIA GLOBAL
    // ========================================================

    private static void configurarTipografiaGeneral() {

        registrarFuenteUI(
                "defaultFont",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "Label.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "Button.font",
                Tipografia.boton()
        );

        registrarFuenteUI(
                "ToggleButton.font",
                Tipografia.boton()
        );

        registrarFuenteUI(
                "TextField.font",
                Tipografia.textoFormulario()
        );

        registrarFuenteUI(
                "FormattedTextField.font",
                Tipografia.textoFormulario()
        );

        registrarFuenteUI(
                "PasswordField.font",
                Tipografia.textoFormulario()
        );

        registrarFuenteUI(
                "TextArea.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "TextPane.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "EditorPane.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "Table.font",
                Tipografia.celdaTabla()
        );

        registrarFuenteUI(
                "TableHeader.font",
                Tipografia.encabezadoTabla()
        );

        registrarFuenteUI(
                "List.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "ComboBox.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "Menu.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "MenuItem.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "CheckBoxMenuItem.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "RadioButtonMenuItem.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "PopupMenu.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "TabbedPane.font",
                Tipografia.medium()
        );

        registrarFuenteUI(
                "ToolTip.font",
                Tipografia.caption()
        );

        registrarFuenteUI(
                "OptionPane.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "CheckBox.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "RadioButton.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "Spinner.font",
                Tipografia.cuerpo()
        );

        registrarFuenteUI(
                "ProgressBar.font",
                Tipografia.cuerpoPequeno()
        );

        registrarFuenteUI(
                "TitledBorder.font",
                Tipografia.subtitulo()
        );
    }

    /**
     * Registra una fuente como recurso UI.
     *
     * Esto permite que el Look and Feel
     * la utilice como valor predeterminado.
     */
    private static void registrarFuenteUI(
            String clave,
            Font fuente) {

        UIManager.put(
                clave,
                new FontUIResource(fuente)
        );
    }

    // ========================================================
    // 8. BOTONES
    // ========================================================

    private static void configurarBotones() {

        UIManager.put(
                "Button.background",
                Paleta.BOTON_SECUNDARIO
        );

        UIManager.put(
                "Button.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "Button.select",
                Paleta.BOTON_SECUNDARIO_HOVER
        );

        UIManager.put(
                "Button.disabledText",
                Paleta.TEXTO_DESHABILITADO
        );

        UIManager.put(
                "Button.focus",
                Paleta.ACENTO
        );

        UIManager.put(
                "Button.border",
                BorderFactory.createLineBorder(
                        Paleta.BORDE_SUAVE,
                        1
                )
        );

        UIManager.put(
                "Button.margin",
                new Insets(
                        Espaciado.BOTON_PADDING_VERTICAL,
                        Espaciado.BOTON_PADDING_HORIZONTAL,
                        Espaciado.BOTON_PADDING_VERTICAL,
                        Espaciado.BOTON_PADDING_HORIZONTAL
                )
        );

        UIManager.put(
                "Button.defaultButtonFollowsFocus",
                Boolean.FALSE
        );

        // Botones de alternancia.

        UIManager.put(
                "ToggleButton.background",
                Paleta.BOTON_SECUNDARIO
        );

        UIManager.put(
                "ToggleButton.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "ToggleButton.select",
                Paleta.ACENTO_SUAVE
        );

        // Botones de flecha y scroll.

        UIManager.put(
                "ArrowButton.background",
                Paleta.ELEVATED
        );

        UIManager.put(
                "ArrowButton.foreground",
                Paleta.ASH
        );
    }

    // ========================================================
    // 9. CAMPOS DE TEXTO
    // ========================================================

    private static void configurarCamposTexto() {

        String[] componentes = {
                "TextField",
                "FormattedTextField",
                "PasswordField"
        };

        for (String componente : componentes) {

            UIManager.put(
                    componente + ".background",
                    Paleta.INPUT_FONDO
            );

            UIManager.put(
                    componente + ".foreground",
                    Paleta.INPUT_TEXTO
            );

            UIManager.put(
                    componente + ".caretForeground",
                    Paleta.CHAMPAGNE
            );

            UIManager.put(
                    componente + ".selectionBackground",
                    Paleta.ACENTO_SUAVE
            );

            UIManager.put(
                    componente + ".selectionForeground",
                    Paleta.BONE
            );

            UIManager.put(
                    componente + ".inactiveForeground",
                    Paleta.TEXTO_DESHABILITADO
            );

            UIManager.put(
                    componente + ".inactiveBackground",
                    Paleta.INPUT_DESHABILITADO
            );

            UIManager.put(
                    componente + ".border",
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    Paleta.INPUT_BORDE,
                                    1
                            ),
                            BorderFactory.createEmptyBorder(
                                    Espaciado.INPUT_PADDING_VERTICAL,
                                    Espaciado.INPUT_PADDING_HORIZONTAL,
                                    Espaciado.INPUT_PADDING_VERTICAL,
                                    Espaciado.INPUT_PADDING_HORIZONTAL
                            )
                    )
            );
        }
    }

    // ========================================================
    // 10. AREAS DE TEXTO
    // ========================================================

    private static void configurarAreasTexto() {

        String[] componentes = {
                "TextArea",
                "TextPane",
                "EditorPane"
        };

        for (String componente : componentes) {

            UIManager.put(
                    componente + ".background",
                    Paleta.FONDO_CAMPO
            );

            UIManager.put(
                    componente + ".foreground",
                    Paleta.BONE
            );

            UIManager.put(
                    componente + ".caretForeground",
                    Paleta.CHAMPAGNE
            );

            UIManager.put(
                    componente + ".selectionBackground",
                    Paleta.ACENTO_SUAVE
            );

            UIManager.put(
                    componente + ".selectionForeground",
                    Paleta.BONE
            );

            UIManager.put(
                    componente + ".inactiveForeground",
                    Paleta.TEXTO_DESHABILITADO
            );
        }
    }

    // ========================================================
    // 11. TABLAS
    // ========================================================

    private static void configurarTablas() {

        UIManager.put(
                "Table.background",
                Paleta.TABLA_FILA
        );

        UIManager.put(
                "Table.foreground",
                Paleta.TABLA_TEXTO
        );

        UIManager.put(
                "Table.selectionBackground",
                Paleta.TABLA_SELECCION
        );

        UIManager.put(
                "Table.selectionForeground",
                Paleta.BONE
        );

        UIManager.put(
                "Table.gridColor",
                Paleta.TABLA_LINEAS
        );

        UIManager.put(
                "Table.focusCellBackground",
                Paleta.TABLA_SELECCION
        );

        UIManager.put(
                "Table.focusCellForeground",
                Paleta.BONE
        );

        UIManager.put(
                "TableHeader.background",
                Paleta.TABLA_CABECERA
        );

        UIManager.put(
                "TableHeader.foreground",
                Paleta.TABLA_TEXTO_SECUNDARIO
        );

        UIManager.put(
                "TableHeader.cellBorder",
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        Paleta.BORDE_SUAVE
                )
        );

        UIManager.put(
                "Table.ascendingSortIcon",
                null
        );

        UIManager.put(
                "Table.descendingSortIcon",
                null
        );
    }

    // ========================================================
    // 12. LISTAS
    // ========================================================

    private static void configurarListas() {

        UIManager.put(
                "List.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "List.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "List.selectionBackground",
                Paleta.ACENTO_SUAVE
        );

        UIManager.put(
                "List.selectionForeground",
                Paleta.BONE
        );

        UIManager.put(
                "List.focusCellHighlightBorder",
                BorderFactory.createLineBorder(
                        Paleta.ACENTO,
                        1
                )
        );
    }

    // ========================================================
    // 13. COMBO BOXES
    // ========================================================

    private static void configurarCombos() {

        UIManager.put(
                "ComboBox.background",
                Paleta.INPUT_FONDO
        );

        UIManager.put(
                "ComboBox.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "ComboBox.selectionBackground",
                Paleta.ACENTO_SUAVE
        );

        UIManager.put(
                "ComboBox.selectionForeground",
                Paleta.BONE
        );

        UIManager.put(
                "ComboBox.disabledBackground",
                Paleta.INPUT_DESHABILITADO
        );

        UIManager.put(
                "ComboBox.disabledForeground",
                Paleta.TEXTO_DESHABILITADO
        );

        UIManager.put(
                "ComboBox.buttonBackground",
                Paleta.ELEVATED
        );

        UIManager.put(
                "ComboBox.buttonForeground",
                Paleta.ASH
        );

        UIManager.put(
                "ComboBox.border",
                BorderFactory.createLineBorder(
                        Paleta.INPUT_BORDE,
                        1
                )
        );
    }

    // ========================================================
    // 14. BARRAS DE DESPLAZAMIENTO
    // ========================================================

    private static void configurarScroll() {

        UIManager.put(
                "ScrollPane.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "Viewport.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "ScrollPane.border",
                BorderFactory.createEmptyBorder()
        );

        UIManager.put(
                "ScrollBar.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "ScrollBar.foreground",
                Paleta.ASH
        );

        UIManager.put(
                "ScrollBar.thumb",
                Paleta.BORDE_FUERTE
        );

        UIManager.put(
                "ScrollBar.track",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "ScrollBar.thumbHighlight",
                Paleta.BORDE_FUERTE
        );

        UIManager.put(
                "ScrollBar.thumbDarkShadow",
                Paleta.BORDE_SUAVE
        );

        UIManager.put(
                "ScrollBar.width",
                12
        );
    }

    // ========================================================
    // 15. MENUS
    // ========================================================

    private static void configurarMenus() {

        UIManager.put(
                "MenuBar.background",
                Paleta.GRAPHITE
        );

        UIManager.put(
                "MenuBar.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "MenuBar.border",
                BorderFactory.createEmptyBorder()
        );

        UIManager.put(
                "Menu.background",
                Paleta.GRAPHITE
        );

        UIManager.put(
                "Menu.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "Menu.selectionBackground",
                Paleta.ACENTO_SUAVE
        );

        UIManager.put(
                "Menu.selectionForeground",
                Paleta.BONE
        );

        UIManager.put(
                "PopupMenu.background",
                Paleta.GRAPHITE
        );

        UIManager.put(
                "PopupMenu.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "PopupMenu.border",
                BorderFactory.createLineBorder(
                        Paleta.BORDE_SUAVE,
                        1
                )
        );

        String[] elementos = {
                "MenuItem",
                "CheckBoxMenuItem",
                "RadioButtonMenuItem"
        };

        for (String elemento : elementos) {

            UIManager.put(
                    elemento + ".background",
                    Paleta.GRAPHITE
            );

            UIManager.put(
                    elemento + ".foreground",
                    Paleta.BONE
            );

            UIManager.put(
                    elemento + ".selectionBackground",
                    Paleta.ACENTO_SUAVE
            );

            UIManager.put(
                    elemento + ".selectionForeground",
                    Paleta.BONE
            );

            UIManager.put(
                    elemento + ".disabledForeground",
                    Paleta.TEXTO_DESHABILITADO
            );
        }
    }

    // ========================================================
    // 16. PESTANAS
    // ========================================================

    private static void configurarPestanas() {

        UIManager.put(
                "TabbedPane.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "TabbedPane.foreground",
                Paleta.ASH
        );

        UIManager.put(
                "TabbedPane.selected",
                Paleta.ELEVATED
        );

        UIManager.put(
                "TabbedPane.focus",
                Paleta.ACENTO_SUAVE
        );

        UIManager.put(
                "TabbedPane.contentAreaColor",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "TabbedPane.tabAreaBackground",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "TabbedPane.contentBorderInsets",
                new Insets(0, 0, 0, 0)
        );

        UIManager.put(
                "TabbedPane.tabInsets",
                new Insets(12, 18, 12, 18)
        );
    }

    // ========================================================
    // 17. DIALOGOS
    // ========================================================

    private static void configurarDialogos() {

        UIManager.put(
                "OptionPane.background",
                Paleta.GRAPHITE
        );

        UIManager.put(
                "OptionPane.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "OptionPane.messageForeground",
                Paleta.BONE
        );

        UIManager.put(
                "OptionPane.buttonAreaBorder",
                BorderFactory.createEmptyBorder(
                        12, 12, 12, 12
                )
        );

        UIManager.put(
                "OptionPane.messageAreaBorder",
                BorderFactory.createEmptyBorder(
                        18, 18, 18, 18
                )
        );
    }

    // ========================================================
    // 18. TOOLTIPS
    // ========================================================

    private static void configurarHerramientas() {

        UIManager.put(
                "ToolTip.background",
                Paleta.ELEVATED
        );

        UIManager.put(
                "ToolTip.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "ToolTip.border",
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Paleta.BORDE_FUERTE,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                7, 10, 7, 10
                        )
                )
        );
    }

    // ========================================================
    // 19. SEPARADORES
    // ========================================================

    private static void configurarSeparadores() {

        UIManager.put(
                "Separator.foreground",
                Paleta.DIVIDER
        );

        UIManager.put(
                "Separator.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "PopupMenuSeparator.foreground",
                Paleta.DIVIDER
        );

        UIManager.put(
                "PopupMenuSeparator.background",
                Paleta.GRAPHITE
        );
    }

    // ========================================================
    // 20. CONTROLES ADICIONALES
    // ========================================================

    private static void configurarControlesAdicionales() {

        // Checkboxes.

        UIManager.put(
                "CheckBox.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "CheckBox.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "CheckBox.focus",
                Paleta.ACENTO
        );

        // Radio buttons.

        UIManager.put(
                "RadioButton.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "RadioButton.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "RadioButton.focus",
                Paleta.ACENTO
        );

        // Progress bars.

        UIManager.put(
                "ProgressBar.background",
                Paleta.ELEVATED
        );

        UIManager.put(
                "ProgressBar.foreground",
                Paleta.CHAMPAGNE
        );

        UIManager.put(
                "ProgressBar.selectionBackground",
                Paleta.OBSIDIAN
        );

        UIManager.put(
                "ProgressBar.selectionForeground",
                Paleta.BONE
        );

        // Sliders.

        UIManager.put(
                "Slider.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "Slider.foreground",
                Paleta.CHAMPAGNE
        );

        // Spinners.

        UIManager.put(
                "Spinner.background",
                Paleta.INPUT_FONDO
        );

        UIManager.put(
                "Spinner.foreground",
                Paleta.BONE
        );

        // Split panes.

        UIManager.put(
                "SplitPane.background",
                Paleta.FONDO_PRINCIPAL
        );

        UIManager.put(
                "SplitPane.dividerSize",
                1
        );

        UIManager.put(
                "SplitPaneDivider.draggingColor",
                Paleta.CHAMPAGNE
        );

        // Toolbars.

        UIManager.put(
                "ToolBar.background",
                Paleta.GRAPHITE
        );

        UIManager.put(
                "ToolBar.foreground",
                Paleta.BONE
        );

        UIManager.put(
                "ToolBar.border",
                BorderFactory.createEmptyBorder()
        );
    }

    // ========================================================
    // 21. CONFIGURAR VENTANA PRINCIPAL
    // ========================================================

    /**
     * Configura dimensiones, fondo y
     * comportamiento general de JFrame.
     *
     * No modifica el contenido de la ventana.
     */
    public static void configurarVentana(
            JFrame ventana) {

        validarComponente(ventana);

        inicializar();

        ventana.setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        ventana.getContentPane().setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        ventana.setMinimumSize(
                Espaciado.ventanaMinima()
        );

        ventana.setSize(
                Espaciado.ventanaInicial()
        );

        ventana.setLocationRelativeTo(null);

        ventana.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );
    }

    // ========================================================
    // 22. CONFIGURAR DIALOGO
    // ========================================================

    public static void configurarDialogo(
            JDialog dialogo) {

        validarComponente(dialogo);

        inicializar();

        dialogo.getContentPane().setBackground(
                Paleta.GRAPHITE
        );

        dialogo.setMinimumSize(
                new java.awt.Dimension(
                        Espaciado.DIALOGO_ANCHO_PEQUENO,
                        180
                )
        );

        dialogo.setLocationRelativeTo(
                dialogo.getOwner()
        );
    }

    // ========================================================
    // 23. ESTILOS SEMANTICOS DE PANELES
    // ========================================================

    /**
     * Panel principal:
     * superficie Obsidian.
     */
    public static void aplicarPanelPrincipal(
            JPanel panel) {

        validarComponente(panel);

        panel.setOpaque(true);

        panel.setBackground(
                Paleta.FONDO_PRINCIPAL
        );
    }

    /**
     * Panel secundario:
     * superficie Graphite.
     */
    public static void aplicarPanelSecundario(
            JPanel panel) {

        validarComponente(panel);

        panel.setOpaque(true);

        panel.setBackground(
                Paleta.FONDO_SECUNDARIO
        );
    }

    /**
     * Panel elevado:
     * superficie de tarjeta.
     */
    public static void aplicarPanelElevado(
            JPanel panel) {

        validarComponente(panel);

        panel.setOpaque(true);

        panel.setBackground(
                Paleta.FONDO_TARJETA
        );
    }

    /**
     * Panel con padding editorial.
     */
    public static void aplicarPanelContenido(
            JPanel panel) {

        aplicarPanelPrincipal(panel);

        panel.setBorder(
                Espaciado.bordeContenido()
        );
    }

    // ========================================================
    // 24. ESTILOS DE ETIQUETAS
    // ========================================================

    public static void aplicarTituloPagina(
            JLabel etiqueta) {

        validarComponente(etiqueta);

        etiqueta.setFont(
                Tipografia.tituloPagina()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_PRINCIPAL
        );

        etiqueta.setOpaque(false);
    }

    public static void aplicarTituloEditorial(
            JLabel etiqueta) {

        validarComponente(etiqueta);

        etiqueta.setFont(
                Tipografia.tituloEditorial()
        );

        etiqueta.setForeground(
                Paleta.BONE
        );

        etiqueta.setOpaque(false);
    }

    public static void aplicarTituloSeccion(
            JLabel etiqueta) {

        validarComponente(etiqueta);

        etiqueta.setFont(
                Tipografia.tituloSeccion()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_PRINCIPAL
        );

        etiqueta.setOpaque(false);
    }

    public static void aplicarTextoSecundario(
            JLabel etiqueta) {

        validarComponente(etiqueta);

        etiqueta.setFont(
                Tipografia.cuerpoPequeno()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_SECUNDARIO
        );

        etiqueta.setOpaque(false);
    }

    public static void aplicarEtiquetaPequena(
            JLabel etiqueta) {

        validarComponente(etiqueta);

        etiqueta.setFont(
                Tipografia.etiqueta()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_ETIQUETA
        );

        etiqueta.setOpaque(false);
    }

    public static void aplicarBalancePrincipal(
            JLabel etiqueta) {

        validarComponente(etiqueta);

        etiqueta.setFont(
                Tipografia.balancePrincipal()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_NUMERICO
        );

        etiqueta.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        etiqueta.setOpaque(false);
    }

    public static void aplicarMontoTabla(
            JLabel etiqueta) {

        validarComponente(etiqueta);

        etiqueta.setFont(
                Tipografia.montoTabla()
        );

        etiqueta.setForeground(
                Paleta.TEXTO_NUMERICO
        );

        etiqueta.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        etiqueta.setOpaque(false);
    }

    // ========================================================
    // 25. ESTILOS SEMANTICOS DE BOTONES
    // ========================================================

    /**
     * Estilo basico de accion principal.
     *
     * El componente BotonPremium aplicara
     * posteriormente los efectos propios
     * de hover y esquinas redondeadas.
     */
    public static void aplicarBotonPrimario(
            AbstractButton boton) {

        validarComponente(boton);

        boton.setFont(
                Tipografia.boton()
        );

        boton.setBackground(
                Paleta.BOTON_PRIMARIO
        );

        boton.setForeground(
                Paleta.BOTON_PRIMARIO_TEXTO
        );

        boton.setOpaque(true);

        boton.setFocusPainted(false);

        boton.setContentAreaFilled(true);

        boton.setBorderPainted(false);

        boton.setMargin(
                new Insets(
                        Espaciado.BOTON_PADDING_VERTICAL,
                        Espaciado.BOTON_PADDING_HORIZONTAL,
                        Espaciado.BOTON_PADDING_VERTICAL,
                        Espaciado.BOTON_PADDING_HORIZONTAL
                )
        );
    }

    /**
     * Estilo de accion secundaria.
     */
    public static void aplicarBotonSecundario(
            AbstractButton boton) {

        validarComponente(boton);

        boton.setFont(
                Tipografia.boton()
        );

        boton.setBackground(
                Paleta.BOTON_SECUNDARIO
        );

        boton.setForeground(
                Paleta.BOTON_SECUNDARIO_TEXTO
        );

        boton.setOpaque(true);

        boton.setContentAreaFilled(true);

        boton.setBorderPainted(true);

        boton.setBorder(
                BorderFactory.createLineBorder(
                        Paleta.BORDE_SUAVE,
                        1
                )
        );
    }

    // ========================================================
    // 26. CAMPOS DE FORMULARIO
    // ========================================================

    public static void aplicarCampoTexto(
            JTextField campo) {

        validarComponente(campo);

        campo.setFont(
                Tipografia.textoFormulario()
        );

        campo.setBackground(
                Paleta.INPUT_FONDO
        );

        campo.setForeground(
                Paleta.INPUT_TEXTO
        );

        campo.setCaretColor(
                Paleta.CHAMPAGNE
        );

        campo.setSelectionColor(
                Paleta.ACENTO_SUAVE
        );

        campo.setSelectedTextColor(
                Paleta.BONE
        );

        campo.setBorder(
                bordeCampo()
        );
    }

    /**
     * Area de texto de estilo editorial.
     */
    public static void aplicarAreaTexto(
            JTextArea area) {

        validarComponente(area);

        area.setFont(
                Tipografia.cuerpo()
        );

        area.setBackground(
                Paleta.FONDO_CAMPO
        );

        area.setForeground(
                Paleta.BONE
        );

        area.setCaretColor(
                Paleta.CHAMPAGNE
        );

        area.setSelectionColor(
                Paleta.ACENTO_SUAVE
        );

        area.setSelectedTextColor(
                Paleta.BONE
        );

        area.setLineWrap(true);

        area.setWrapStyleWord(true);

        area.setMargin(
                Espaciado.insetsCampo()
        );
    }

    // ========================================================
    // 27. TABLAS PREMIUM
    // ========================================================

    /**
     * Aplica la base visual a una JTable.
     *
     * Los renderizadores personalizados
     * se definiran en TablaPremium.
     */
    public static void aplicarTabla(
            JTable tabla) {

        validarComponente(tabla);

        tabla.setFont(
                Tipografia.celdaTabla()
        );

        tabla.setBackground(
                Paleta.TABLA_FILA
        );

        tabla.setForeground(
                Paleta.TABLA_TEXTO
        );

        tabla.setSelectionBackground(
                Paleta.TABLA_SELECCION
        );

        tabla.setSelectionForeground(
                Paleta.BONE
        );

        tabla.setGridColor(
                Paleta.TABLA_LINEAS
        );

        tabla.setShowVerticalLines(false);

        tabla.setShowHorizontalLines(true);

        tabla.setRowHeight(
                Espaciado.TABLA_FILA_ALTURA
        );

        tabla.setRowMargin(0);

        tabla.setIntercellSpacing(
                new java.awt.Dimension(
                        0,
                        1
                )
        );

        tabla.setFillsViewportHeight(true);

        tabla.setOpaque(true);

        // Configurar encabezado.

        JTableHeader encabezado =
                tabla.getTableHeader();

        if (encabezado != null) {

            encabezado.setFont(
                    Tipografia.encabezadoTabla()
            );

            encabezado.setBackground(
                    Paleta.TABLA_CABECERA
            );

            encabezado.setForeground(
                    Paleta.TABLA_TEXTO_SECUNDARIO
            );

            encabezado.setPreferredSize(
                    new java.awt.Dimension(
                            encabezado.getPreferredSize().width,
                            Espaciado.TABLA_CABECERA_ALTURA
                    )
            );

            encabezado.setReorderingAllowed(false);
        }
    }

    // ========================================================
    // 28. SCROLL PANES
    // ========================================================

    public static void aplicarScrollPane(
            JScrollPane scroll) {

        validarComponente(scroll);

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.setOpaque(true);

        scroll.setBackground(
                Paleta.FONDO_PRINCIPAL
        );

        JViewport viewport =
                scroll.getViewport();

        if (viewport != null) {

            viewport.setBackground(
                    Paleta.FONDO_PRINCIPAL
            );

            viewport.setOpaque(true);
        }

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        scroll.getHorizontalScrollBar()
                .setUnitIncrement(16);
    }

    // ========================================================
    // 29. BORDES DEL SISTEMA
    // ========================================================

    public static Border bordeSutil() {

        return BorderFactory.createLineBorder(
                Paleta.BORDE_SUAVE,
                1
        );
    }

    public static Border bordeNormal() {

        return BorderFactory.createLineBorder(
                Paleta.BORDE,
                1
        );
    }

    public static Border bordeFoco() {

        return BorderFactory.createLineBorder(
                Paleta.BORDE_FOCO,
                2
        );
    }

    public static Border bordeError() {

        return BorderFactory.createLineBorder(
                Paleta.BORDE_ERROR,
                1
        );
    }

    public static Border bordeCampo() {

        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        Paleta.INPUT_BORDE,
                        1
                ),
                BorderFactory.createEmptyBorder(
                        Espaciado.INPUT_PADDING_VERTICAL,
                        Espaciado.INPUT_PADDING_HORIZONTAL,
                        Espaciado.INPUT_PADDING_VERTICAL,
                        Espaciado.INPUT_PADDING_HORIZONTAL
                )
        );
    }

    public static Border bordeSeparadorInferior() {

        return BorderFactory.createMatteBorder(
                0,
                0,
                1,
                0,
                Paleta.DIVIDER
        );
    }

    // ========================================================
    // 30. REFRESCAR UNA VENTANA
    // ========================================================

    /**
     * Actualiza los delegados visuales
     * de una ventana existente.
     *
     * Util despues de cambiar valores
     * globales de UIManager.
     *
     * Se ejecuta en el hilo Swing.
     *
     * Advertencia:
     * Actualizar el Look and Feel puede
     * restablecer propiedades visuales
     * de componentes personalizados.
     */
    public static void actualizarVentana(
            Window ventana) {

        validarComponente(ventana);

        Runnable actualizar = () -> {

            SwingUtilities.updateComponentTreeUI(
                    ventana
            );

            ventana.invalidate();

            ventana.validate();

            ventana.repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {

            actualizar.run();

        } else {

            SwingUtilities.invokeLater(
                    actualizar
            );
        }
    }

    // ========================================================
    // 31. ESTADO DEL TEMA
    // ========================================================

    public static boolean estaInicializado() {

        return inicializado;
    }

    public static String getNombreTema() {

        return NOMBRE_TEMA;
    }

    public static String getAparienciaActiva() {

        return aparienciaActiva;
    }

    // ========================================================
    // 32. INFORME DEL TEMA
    // ========================================================

    public static String generarInforme() {

        inicializar();

        StringBuilder resultado =
                new StringBuilder();

        resultado.append(
                "========================================\n"
        );

        resultado.append(
                "MONEDERO VIRTUAL\n"
        );

        resultado.append(
                "SISTEMA DE DISENO\n"
        );

        resultado.append(
                "========================================\n\n"
        );

        resultado.append(
                "Identidad: "
        ).append(IDENTIDAD).append("\n");

        resultado.append(
                "Tema: "
        ).append(NOMBRE_TEMA).append("\n");

        resultado.append(
                "Version: "
        ).append(VERSION_TEMA).append("\n");

        resultado.append(
                "Look and Feel: "
        ).append(aparienciaActiva).append("\n\n");

        resultado.append(
                "COLORES\n"
        );

        resultado.append(
                "----------------------------------------\n"
        );

        resultado.append(
                "Obsidian: "
        ).append(
                Paleta.aHex(Paleta.OBSIDIAN)
        ).append("\n");

        resultado.append(
                "Graphite: "
        ).append(
                Paleta.aHex(Paleta.GRAPHITE)
        ).append("\n");

        resultado.append(
                "Bone: "
        ).append(
                Paleta.aHex(Paleta.BONE)
        ).append("\n");

        resultado.append(
                "Champagne: "
        ).append(
                Paleta.aHex(Paleta.CHAMPAGNE)
        ).append("\n\n");

        resultado.append(
                "TIPOGRAFIA\n"
        );

        resultado.append(
                "----------------------------------------\n"
        );

        resultado.append(
                "Inter personalizada: "
        ).append(
                Tipografia.tieneInterPersonalizada()
        ).append("\n");

        resultado.append(
                "IBM Plex Mono personalizada: "
        ).append(
                Tipografia.tieneMonoPersonalizada()
        ).append("\n");

        resultado.append(
                "Instrument Serif personalizada: "
        ).append(
                Tipografia.tieneEditorialPersonalizada()
        ).append("\n\n");

        resultado.append(
                "Archivos TTF cargados: "
        ).append(
                Tipografia.cantidadArchivosCargados()
        ).append("/9\n");

        resultado.append(
                "Todas las familias personalizadas: "
        ).append(
                Tipografia.todasPersonalizadas()
        ).append("\n\n");

        resultado.append(
                "ESPACIADO\n"
        );

        resultado.append(
                "----------------------------------------\n"
        );

        resultado.append(
                "Sidebar: "
        ).append(
                Espaciado.SIDEBAR_ANCHO
        ).append("\n");

        resultado.append(
                "Padding contenido: "
        ).append(
                Espaciado.CONTENIDO_PADDING_HORIZONTAL
        ).append("\n");

        resultado.append(
                "Columnas reticula: "
        ).append(
                Espaciado.GRID_COLUMNAS
        ).append("\n");

        resultado.append(
                "Separacion reticula: "
        ).append(
                Espaciado.GRID_SEPARACION_HORIZONTAL
        ).append("\n");

        return resultado.toString();
    }

    // ========================================================
    // 33. VALIDACIONES
    // ========================================================

    private static void validarComponente(
            Component componente) {

        if (componente == null) {

            throw new IllegalArgumentException(
                    "El componente no puede ser null"
            );
        }
    }
}

