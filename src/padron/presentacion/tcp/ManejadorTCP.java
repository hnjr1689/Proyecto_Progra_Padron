package padron.presentacion.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import padron.dto.FormatoSalida;
import padron.dto.RespuestaPadron;
import padron.dto.SolicitudPadron;
import padron.logica.ServicioPadron;
import padron.util.Serializador;

/**
 * Maneja la sesión de un cliente TCP en su propio hilo.
 * Lee mensajes línea por línea hasta recibir BYE o que el cliente cierre.
 */
class ManejadorTCP implements Runnable {

    private final Socket        socket;
    private final ServicioPadron servicio;

    ManejadorTCP(Socket socket, ServicioPadron servicio) {
        this.socket   = socket;
        this.servicio = servicio;
    }

    @Override
    public void run() {
        try (
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter salida = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)
        ) {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                linea = linea.trim();

                if (linea.equalsIgnoreCase("BYE")) {
                    salida.println("BYE");
                    break;
                }

                salida.println(procesar(linea));
            }
        } catch (IOException e) {
            System.err.println("Error con cliente TCP [" +
                socket.getInetAddress() + "]: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private String procesar(String linea) {
        try {
            SolicitudPadron solicitud = SolicitudPadron.parsear(linea);
            RespuestaPadron respuesta = servicio.atender(solicitud.getCedula());
            return Serializador.serializar(respuesta, solicitud.getFormato());
        } catch (IllegalArgumentException e) {
            return Serializador.toJson(RespuestaPadron.solicitudInvalida(e.getMessage()));
        } catch (Exception e) {
            return Serializador.toJson(RespuestaPadron.errorInterno(e.getMessage()));
        }
    }
}
