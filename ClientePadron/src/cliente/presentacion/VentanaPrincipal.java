package cliente.presentacion;

import cliente.dto.RespuestaPadron;
import cliente.http.ClienteHTTP;
import cliente.tcp.ClienteTCP;
import cliente.util.ParserRespuesta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana principal de la aplicación cliente del Padrón Electoral.
 * Permite consultar cédulas vía TCP o HTTP, en formato JSON o XML,
 * y muestra los datos parseados junto con la respuesta cruda del servidor.
 */
public class VentanaPrincipal extends JFrame {

    // ── Configuración del servidor ─────────────────────────────────────
    private static final String HOST_TCP  = "localhost";
    private static final int    PUERTO_TCP = 5000;
    private static final String BASE_HTTP = "http://localhost:8080";

    // ── Colores de la interfaz ─────────────────────────────────────────
    private static final Color BG_OSCURO   = new Color(18, 18, 35);
    private static final Color BG_PANEL    = new Color(26, 30, 55);
    private static final Color BG_CAMPO    = new Color(35, 40, 68);
    private static final Color ACENTO      = new Color(99, 102, 241);   // índigo
    private static final Color ACENTO_HOV  = new Color(79,  82, 220);
    private static final Color COLOR_OK    = new Color(52, 211, 153);   // verde
    private static final Color COLOR_ERR   = new Color(252, 100, 100);  // rojo
    private static final Color TEXTO       = new Color(220, 224, 255);
    private static final Color TEXTO_DIM   = new Color(130, 140, 180);
    private static final Color BORDE       = new Color(60,  65, 100);

    private static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);
    private static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_NORMAL  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_TITULO  = new Font("Segoe UI", Font.BOLD,  18);

    // ── Componentes de entrada ─────────────────────────────────────────
    private JTextField  campoCedula;
    private JComboBox<String> comboFormato;
    private JComboBox<String> comboCanal;
    private JButton     btnConsultar;
    private JButton     btnLimpiar;

    // ── Componentes de salida (datos parseados) ────────────────────────
    private JTextField  campoNombre;
    private JTextField  campoCedulaOut;
    private JTextField  campoCodElec;
    private JTextField  campoProvincia;
    private JTextField  campoCanton;
    private JTextField  campoDistrito;

    // ── Respuesta cruda ────────────────────────────────────────────────
    private JTextArea   areaRespuesta;

    // ── Barra de estado ────────────────────────────────────────────────
    private JLabel      lblEstado;

    // ── Clientes ──────────────────────────────────────────────────────
    private final ClienteTCP  clienteTcp  = new ClienteTCP(HOST_TCP, PUERTO_TCP);
    private final ClienteHTTP clienteHttp = new ClienteHTTP(BASE_HTTP);

    // ══════════════════════════════════════════════════════════════════
    //  Constructor
    // ══════════════════════════════════════════════════════════════════

    public VentanaPrincipal() {
        configurarLookAndFeel();
        setTitle("Consulta Padrón Electoral");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 700);
        setMinimumSize(new Dimension(780, 620));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(BG_OSCURO);
        raiz.add(crearEncabezado(),  BorderLayout.NORTH);
        raiz.add(crearContenido(),   BorderLayout.CENTER);
        raiz.add(crearBarraEstado(), BorderLayout.SOUTH);
        setContentPane(raiz);

        campoCedula.addActionListener(e -> ejecutarConsulta());
    }

    // ══════════════════════════════════════════════════════════════════
    //  Construcción de la interfaz
    // ══════════════════════════════════════════════════════════════════

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titulo = new JLabel("CONSULTA PADRÓN ELECTORAL");
        titulo.setFont(FONT_TITULO);
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Sistema de consulta ciudadana — Costa Rica");
        subtitulo.setFont(FONT_NORMAL);
        subtitulo.setForeground(TEXTO_DIM);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);
        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearContenido() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_OSCURO);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        gbc.gridy = 0; panel.add(crearPanelEntrada(),  gbc);
        gbc.gridy = 1; panel.add(crearPanelDatos(),    gbc);
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill    = GridBagConstraints.BOTH;
        panel.add(crearPanelRespuesta(), gbc);
        return panel;
    }

    // ── Panel de entrada (cédula + controles) ─────────────────────────

    private JPanel crearPanelEntrada() {
        JPanel panel = crearPanelConBorde("Consulta");

        // Fila 1: cédula
        panel.add(etiqueta("Cédula:"));
        campoCedula = crearCampoTexto("Ingrese número de cédula (ej. 101053316)");
        panel.add(campoCedula);
        panel.add(new JLabel()); // spacer

        // Fila 2: formato + canal
        panel.add(etiqueta("Formato:"));
        comboFormato = crearCombo("JSON", "XML");
        panel.add(comboFormato);

        panel.add(etiqueta("  Canal:"));
        comboCanal = crearCombo("TCP", "HTTP");
        panel.add(comboCanal);

        // Fila 3: botones
        panel.add(new JLabel());
        btnConsultar = crearBoton("CONSULTAR", ACENTO, ACENTO_HOV);
        btnConsultar.addActionListener(e -> ejecutarConsulta());
        panel.add(btnConsultar);

        panel.add(new JLabel());
        btnLimpiar = crearBoton("LIMPIAR", new Color(60, 65, 100), BORDE);
        btnLimpiar.addActionListener(e -> limpiarTodo());
        panel.add(btnLimpiar);

        // Layout: etiqueta | campo | etiqueta | campo
        panel.setLayout(new GridBagLayout());
        reconstruirLayoutEntrada(panel);

        return panel;
    }

    private void reconstruirLayoutEntrada(JPanel panel) {
        panel.removeAll();
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5, 8, 5, 8);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        // Fila 0: cédula
        g.gridy = 0; g.gridx = 0; g.weightx = 0; panel.add(etiqueta("Cédula:"), g);
        g.gridx = 1; g.weightx = 1; g.gridwidth = 3;
        panel.add(campoCedula, g);
        g.gridwidth = 1;

        // Fila 1: formato | canal
        g.gridy = 1; g.gridx = 0; g.weightx = 0; panel.add(etiqueta("Formato:"), g);
        g.gridx = 1; g.weightx = 0.4; panel.add(comboFormato, g);
        g.gridx = 2; g.weightx = 0;   panel.add(etiqueta("Canal:"), g);
        g.gridx = 3; g.weightx = 0.6; panel.add(comboCanal, g);

        // Fila 2: botones
        g.gridy = 2; g.gridx = 1; g.weightx = 0.5; panel.add(btnConsultar, g);
        g.gridx = 3; g.weightx = 0.5; panel.add(btnLimpiar, g);
    }

    // ── Panel de datos parseados ───────────────────────────────────────

    private JPanel crearPanelDatos() {
        JPanel panel = crearPanelConBorde("Datos de la persona");
        panel.setLayout(new GridBagLayout());

        campoNombre     = crearCampoSalida();
        campoCedulaOut  = crearCampoSalida();
        campoCodElec    = crearCampoSalida();
        campoProvincia  = crearCampoSalida();
        campoCanton     = crearCampoSalida();
        campoDistrito   = crearCampoSalida();

        String[][] filas = {
            {"Nombre completo:", null},
            {"Cédula:", null},
            {"Código electoral:", null},
            {"Provincia:", null},
            {"Cantón:", null},
            {"Distrito:", null}
        };
        JTextField[] campos = {
            campoNombre, campoCedulaOut, campoCodElec,
            campoProvincia, campoCanton, campoDistrito
        };

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        // Distribuir en 2 columnas de pares (etiqueta | campo)
        for (int i = 0; i < campos.length; i++) {
            int col = (i % 2) * 2;
            int row = i / 2;

            g.gridy = row; g.gridx = col;     g.weightx = 0;
            panel.add(etiqueta(filas[i][0]), g);
            g.gridx = col + 1;                g.weightx = 1;
            panel.add(campos[i], g);
        }
        return panel;
    }

    // ── Panel de respuesta cruda ───────────────────────────────────────

    private JPanel crearPanelRespuesta() {
        JPanel panel = crearPanelConBorde("Respuesta del servidor (JSON / XML)");
        panel.setLayout(new BorderLayout(0, 0));

        areaRespuesta = new JTextArea();
        areaRespuesta.setEditable(false);
        areaRespuesta.setFont(FONT_MONO);
        areaRespuesta.setBackground(BG_CAMPO);
        areaRespuesta.setForeground(COLOR_OK);
        areaRespuesta.setCaretColor(TEXTO);
        areaRespuesta.setBorder(new EmptyBorder(8, 10, 8, 10));
        areaRespuesta.setLineWrap(true);
        areaRespuesta.setWrapStyleWord(false);

        JScrollPane scroll = new JScrollPane(areaRespuesta);
        scroll.setBackground(BG_CAMPO);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(BG_CAMPO);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(new EmptyBorder(6, 16, 6, 16));
        lblEstado = new JLabel("Listo.");
        lblEstado.setFont(FONT_NORMAL);
        lblEstado.setForeground(TEXTO_DIM);
        panel.add(lblEstado, BorderLayout.WEST);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    //  Lógica de consulta
    // ══════════════════════════════════════════════════════════════════

    private void ejecutarConsulta() {
        String cedula  = campoCedula.getText().trim();
        String formato = (String) comboFormato.getSelectedItem();
        String canal   = (String) comboCanal.getSelectedItem();

        if (cedula.isEmpty()) {
            mostrarError("Cédula vacía. Por favor ingrese un número de cédula.");
            return;
        }

        setEstado("Consultando " + canal + "...", TEXTO_DIM);
        btnConsultar.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                if ("TCP".equals(canal)) {
                    return clienteTcp.consultar(cedula, formato);
                } else {
                    return clienteHttp.consultar(cedula,
                        formato.toLowerCase());
                }
            }

            @Override
            protected void done() {
                btnConsultar.setEnabled(true);
                try {
                    String crudo = get();
                    mostrarRespuesta(crudo, "XML".equals(formato));
                } catch (Exception ex) {
                    String causa = ex.getCause() != null
                        ? ex.getCause().getMessage()
                        : ex.getMessage();
                    mostrarError("Error de conexión: " + causa);
                }
            }
        };
        worker.execute();
    }

    private void mostrarRespuesta(String crudo, boolean esXml) {
        // Mostrar respuesta cruda formateada
        String formateado = esXml ? formatearXml(crudo) : formatearJson(crudo);
        areaRespuesta.setText(formateado);
        areaRespuesta.setCaretPosition(0);

        // Parsear y mostrar datos
        RespuestaPadron r = ParserRespuesta.parsear(crudo, esXml);
        if (r.isExitosa()) {
            campoNombre.setText(r.getNombreCompleto());
            campoCedulaOut.setText(r.getCedula());
            campoCodElec.setText(r.getCodElec());
            campoProvincia.setText(r.getProvincia());
            campoCanton.setText(r.getCanton());
            campoDistrito.setText(r.getDistrito());
            setEstado("Consulta exitosa.", COLOR_OK);
            areaRespuesta.setForeground(COLOR_OK);
        } else {
            limpiarCamposDatos();
            String msg = r.getMensajeError();
            setEstado("Código " + r.getCodigoError() + ": " + msg, COLOR_ERR);
            areaRespuesta.setForeground(COLOR_ERR);
        }
    }

    private void mostrarError(String mensaje) {
        limpiarCamposDatos();
        areaRespuesta.setText("");
        setEstado(mensaje, COLOR_ERR);
    }

    private void limpiarTodo() {
        campoCedula.setText("");
        limpiarCamposDatos();
        areaRespuesta.setText("");
        areaRespuesta.setForeground(COLOR_OK);
        setEstado("Listo.", TEXTO_DIM);
        campoCedula.requestFocus();
    }

    private void limpiarCamposDatos() {
        campoNombre.setText("");
        campoCedulaOut.setText("");
        campoCodElec.setText("");
        campoProvincia.setText("");
        campoCanton.setText("");
        campoDistrito.setText("");
    }

    private void setEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
    }

    // ══════════════════════════════════════════════════════════════════
    //  Formateo para visualización
    // ══════════════════════════════════════════════════════════════════

    /** Indenta el JSON compacto recibido para mostrarlo de forma legible. */
    private static String formatearJson(String json) {
        StringBuilder sb  = new StringBuilder();
        int           ind = 0;
        boolean enString  = false;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\'))
                enString = !enString;
            if (!enString) {
                switch (c) {
                    case '{', '[' -> { sb.append(c); sb.append('\n');
                                       ind++;  indentar(sb, ind); }
                    case '}', ']' -> { sb.append('\n'); ind--;
                                       indentar(sb, ind); sb.append(c); }
                    case ','      -> { sb.append(c); sb.append('\n');
                                       indentar(sb, ind); }
                    case ':'      -> sb.append(": ");
                    default       -> sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static void indentar(StringBuilder sb, int nivel) {
        sb.append("  ".repeat(Math.max(0, nivel)));
    }

    /** Indenta el XML compacto para mostrarlo de forma legible. */
    private static String formatearXml(String xml) {
        // El XML del servidor ya viene multi-línea; solo lo limpiamos un poco
        return xml.replace(" <", "\n<").trim();
    }

    // ══════════════════════════════════════════════════════════════════
    //  Factories de componentes con el tema oscuro
    // ══════════════════════════════════════════════════════════════════

    private static JPanel crearPanelConBorde(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            new EmptyBorder(10, 14, 10, 14)));

        TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACENTO, 1), "  " + titulo + "  ");
        tb.setTitleColor(ACENTO);
        tb.setTitleFont(FONT_BOLD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            tb, new EmptyBorder(6, 8, 8, 8)));
        return panel;
    }

    private static JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TEXTO_DIM);
        return lbl;
    }

    private static JTextField crearCampoTexto(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(TEXTO_DIM);
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    g.drawString(placeholder, 10,
                        (getHeight() + g.getFontMetrics().getAscent()) / 2 - 2);
                }
            }
        };
        estilizarCampo(tf);
        return tf;
    }

    private static JTextField crearCampoSalida() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(BG_CAMPO);
        tf.setForeground(TEXTO);
        tf.setFont(FONT_MONO);
        tf.setCaretColor(TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE),
            new EmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private static void estilizarCampo(JTextField tf) {
        tf.setBackground(BG_CAMPO);
        tf.setForeground(TEXTO);
        tf.setFont(FONT_NORMAL);
        tf.setCaretColor(TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE),
            new EmptyBorder(6, 10, 6, 10)));
    }

    private static JComboBox<String> crearCombo(String... opciones) {
        JComboBox<String> cb = new JComboBox<>(opciones);
        cb.setBackground(BG_CAMPO);
        cb.setForeground(TEXTO);
        cb.setFont(FONT_NORMAL);
        cb.setBorder(BorderFactory.createLineBorder(BORDE));
        // Renderer personalizado para el tema oscuro
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACENTO : BG_CAMPO);
                setForeground(TEXTO);
                setFont(FONT_NORMAL);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        return cb;
    }

    private static JButton crearBoton(String texto, Color fondo, Color hover) {
        JButton btn = new JButton(texto) {
            private Color colorActual = fondo;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) {
                        colorActual = hover; repaint();
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        colorActual = fondo; repaint();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorActual);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(TEXTO);
                g2.setFont(FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════
    //  Look & Feel
    // ══════════════════════════════════════════════════════════════════

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",         BG_OSCURO);
        UIManager.put("OptionPane.background",     BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("ScrollBar.thumb",           BORDE);
        UIManager.put("ScrollBar.track",           BG_CAMPO);
        UIManager.put("ScrollBar.thumbHighlight",  ACENTO);
        UIManager.put("ComboBox.background",       BG_CAMPO);
        UIManager.put("ComboBox.foreground",       TEXTO);
        UIManager.put("ComboBox.selectionBackground", ACENTO);
        UIManager.put("ComboBox.selectionForeground", TEXTO);
        UIManager.put("PopupMenu.background",      BG_CAMPO);
    }
}
