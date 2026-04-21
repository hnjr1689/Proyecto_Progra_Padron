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
 * Maneja la conexion de un cliente TCP en su propio hilo.
 * Lee lineas del cliente, procesa cada solicitud y responde.
 * Si el cliente manda BYE se cierra la conexion.
 * @author Adrian
 */
public class ManejadorCliente implements Runnable {

    private final Socket socket;
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

                // Si el cliente manda BYE cerramos la conexion
                if (linea.equalsIgnoreCase("BYE")) {
                    salida.println("ADIOS");
                    break;
                }

                // Procesamos la linea y mandamos la respuesta
                salida.println(procesar(linea));
            }
        } catch (IOException e) {
            System.err.println("Error con cliente TCP: " + e.getMessage());
        } finally {
            // Cerramos el socket al terminar
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    /*
     * Procesa una linea del protocolo TCP y retorna la respuesta
     * compactada en una sola linea para que los clientes puedan
     * leerla con un unico readLine().
     */
    private String procesar(String linea) {
        // Validacion estructural del protocolo ANTES de intentar parsear.
        // El formato obligatorio es exactamente tres campos separados por '|'.
        String[] partes = linea.split("\\|", -1);
        if (partes.length != 3) {
            return compactar(Serializador.serializar(
                RespuestaPadron.solicitudInvalida(
                    "Formato TCP invalido. Se esperaba: GET|cedula|JSON o GET|cedula|XML"),
                FormatoSalida.JSON));
        }
        if (!"GET".equalsIgnoreCase(partes[0].trim())) {
            return compactar(Serializador.serializar(
                RespuestaPadron.solicitudInvalida(
                    "Operacion no soportada: '" + partes[0].trim()
                    + "'. Solo se admite GET."),
                FormatoSalida.JSON));
        }

        try {
            SolicitudPadron solicitud = SolicitudPadron.parsear(linea);
            RespuestaPadron respuesta = servicio.atender(solicitud);
            return compactar(Serializador.serializar(respuesta, solicitud.getFormato()));
        } catch (IllegalArgumentException e) {
            return compactar(Serializador.serializar(
                RespuestaPadron.solicitudInvalida(e.getMessage()),
                detectarFormato(linea)));
        } catch (Exception e) {
            return compactar(Serializador.serializar(
                RespuestaPadron.errorInterno(e.getMessage()),
                FormatoSalida.JSON));
        }
    }

    /**
     * Convierte la respuesta a una sola linea eliminando saltos de linea
     * y espacios redundantes. Necesario para el protocolo TCP donde cada
     * respuesta debe ocupar exactamente una linea (terminada en \n).
     */
    private static String compactar(String texto) {
        return texto.replaceAll("[ \t]*\\r?\\n[ \t]*", " ").trim();
    }

    /**
     * Intenta detectar el formato pedido aunque la linea sea invalida.
     * Si contiene XML retorna XML, sino retorna JSON por defecto.
     */
    private FormatoSalida detectarFormato(String linea) {
        if (linea != null && linea.toUpperCase().contains("XML"))
            return FormatoSalida.XML;
        return FormatoSalida.JSON;
    }
}


