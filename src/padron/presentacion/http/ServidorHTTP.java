package padron.presentacion.http;

import padron.logica.ServicioPadron;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP para consultas al padrón electoral.
 * Puerto: 8080
 *
 * Endpoints soportados:
 *   GET /padron?cedula=XXX&format=json|xml
 *   GET /padron/{cedula}?format=json|xml
 */
public class ServidorHTTP {

    private static final int PUERTO = 8080;

    /**
     * Inicia el servidor HTTP con executor de hilos.
     */
    public static void iniciar(ServicioPadron servicio) {
        try {
            HttpServer server = HttpServer.create(
                new InetSocketAddress(PUERTO), 0);
            server.createContext("/padron", new ManejadorHTTP(servicio));
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            System.out.println("Servidor HTTP escuchando en puerto " + PUERTO);
        } catch (IOException e) {
            System.err.println("Error iniciando servidor HTTP: " + e.getMessage());
        }
    }
}