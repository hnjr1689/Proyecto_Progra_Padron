package cliente.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cliente TCP que se conecta al servidor del Padrón Electoral.
 * Protocolo: GET|cedula|JSON  o  GET|cedula|XML
 * Cada respuesta llega en una sola línea (el servidor la compacta).
 */
public class ClienteTCP {

    private final String host;
    private final int    puerto;

    public ClienteTCP(String host, int puerto) {
        this.host   = host;
        this.puerto = puerto;
    }

    /**
     * Envía una consulta al servidor TCP y retorna la respuesta cruda.
     *
     * @param cedula  cédula a consultar
     * @param formato "JSON" o "XML"
     * @return texto de la respuesta (JSON o XML en una sola línea)
     * @throws IOException si no hay conexión o el servidor falla
     */
    public String consultar(String cedula, String formato) throws IOException {
        try (Socket socket = new Socket(host, puerto);
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(
                 new InputStreamReader(socket.getInputStream(), "UTF-8"))) {

            // Enviar solicitud
            salida.println("GET|" + cedula + "|" + formato);

            // El servidor responde en una sola línea
            String respuesta = entrada.readLine();
            if (respuesta == null)
                throw new IOException("El servidor cerró la conexión sin responder.");

            // Cerrar sesión cortésmente
            salida.println("BYE");

            return respuesta;
        }
    }
}
