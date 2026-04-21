package cliente;

import cliente.presentacion.VentanaPrincipal;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada del cliente GUI del Padrón Electoral.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
