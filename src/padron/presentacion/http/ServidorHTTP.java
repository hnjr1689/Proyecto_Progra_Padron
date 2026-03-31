package padron.presentacion.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import padron.logica.ServicioPadron;

/**
 * Servidor HTTP basado en com.sun.net.httpserver.HttpServer.
 * Usa un ExecutorService de 10 hilos para atender peticiones concurrentes.
 *
 * Endpoints registrados:
 *   GET /padron/{cedula}?format=json|xml
 *   GET /padron?cedula={cedula}&format=json|xml
 */
public class ServidorHTTP implements Runnable {

    private static final int EXECUTOR_SIZE = 10;

    private final int           puerto;
    private final ServicioPadron servicio;

    public ServidorHTTP(int puerto, ServicioPadron servicio) {
        this.puerto   = puerto;
        this.servicio = servicio;
    }

    @Override
    public void run() {
        try {
            HttpServer servidor = HttpServer.create(
                new InetSocketAddress(puerto), 0);

            servidor.createContext("/padron", new ManejadorPadron(servicio));
            servidor.setExecutor(Executors.newFixedThreadPool(EXECUTOR_SIZE));
            servidor.start();

            System.out.println("ServidorHTTP escuchando en puerto " + puerto);
        } catch (IOException e) {
            System.err.println("Error iniciando ServidorHTTP: " + e.getMessage());
        }
    }
}
