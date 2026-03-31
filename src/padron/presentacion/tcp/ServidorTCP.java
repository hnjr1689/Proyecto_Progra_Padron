package padron.presentacion.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import padron.logica.ServicioPadron;

/**
 * Servidor TCP concurrente con pool de hilos.
 *
 * Protocolo de entrada:
 *   GET|cedula|JSON   — consulta, responde en JSON
 *   GET|cedula|XML    — consulta, responde en XML
 *   BYE               — cierra la conexión
 *
 * Cada cliente es atendido por un hilo del pool (tamaño fijo = 10).
 */
public class ServidorTCP implements Runnable {

    private static final int POOL_SIZE = 10;

    private final int           puerto;
    private final ServicioPadron servicio;

    public ServidorTCP(int puerto, ServicioPadron servicio) {
        this.puerto   = puerto;
        this.servicio = servicio;
    }

    /** Llamado desde el hilo principal en Main. */
    public void iniciar() {
        run();
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("ServidorTCP escuchando en puerto " + puerto);
            while (!Thread.currentThread().isInterrupted()) {
                Socket cliente = serverSocket.accept();
                pool.execute(new ManejadorTCP(cliente, servicio));
            }
        } catch (IOException e) {
            System.err.println("ServidorTCP detenido: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
