package padron.presentacion.tcp;

import padron.dto.FormatoSalida;
import padron.dto.RespuestaPadron;
import padron.dto.SolicitudPadron;
import padron.logica.ServicioPadron;
import padron.util.Serializador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Maneja la conexión de un cliente TCP en su propio hilo.
 * Lee líneas del cliente, procesa cada solicitud y responde.
 */
public class ManejadorCliente implements Runnable {

    private final Socket         socket;
    private final ServicioPadron servicio;

    public ManejadorCliente(Socket socket, ServicioPadron servicio) {
        this.socket   = socket;
        this.servicio = servicio;
    }

    @Override
    public void run() {
        try (
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter salida = new PrintWriter(
                socket.getOutputStream(), true)
        ) {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                linea = linea.trim();

                if (linea.equalsIgnoreCase("BYE")) {
                    salida.println("ADIOS");
                    break;
                }

                salida.println(procesar(linea));
            }
        } catch (IOException e) {
            System.err.println("Error con cliente TCP: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private String procesar(String linea) {
        try {
            SolicitudPadron solicitud = SolicitudPadron.parsear(linea);
            RespuestaPadron respuesta = servicio.atender(solicitud);
            return Serializador.serializar(respuesta, solicitud.getFormato());
        } catch (IllegalArgumentException e) {
            return Serializador.serializar(
                RespuestaPadron.solicitudInvalida(e.getMessage()),
                detectarFormato(linea));
        } catch (Exception e) {
            return Serializador.serializar(
                RespuestaPadron.errorInterno(e.getMessage()),
                FormatoSalida.JSON);
        }
    }

    /**
     * Intenta detectar el formato pedido aunque la línea sea inválida.
     */
    private FormatoSalida detectarFormato(String linea) {
        if (linea != null && linea.toUpperCase().contains("XML"))
            return FormatoSalida.XML;
        return FormatoSalida.JSON;
    }
}
