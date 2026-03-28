package padron.presentacion.tcp;

import padron.logica.ServicioPadron;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor TCP concurrente para consultas al padrón.
 * Protocolo: GET|cedula|JSON  o  GET|cedula|XML
 * Puerto: 5000
 */
public class ServidorTCP {

    private static final int PUERTO    = 5000;
    private static final int MAX_HILOS = 10;

    /**
     * Inicia el servidor TCP con un pool de hilos.
     */
    public static void iniciar(ServicioPadron servicio) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_HILOS);
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor TCP escuchando en puerto " + PUERTO);
            while (true) {
                Socket cliente = servidor.accept();
                pool.execute(new ManejadorCliente(cliente, servicio));
            }
        } catch (IOException e) {
            System.err.println("Error en servidor TCP: " + e.getMessage());
        }
    }
}
