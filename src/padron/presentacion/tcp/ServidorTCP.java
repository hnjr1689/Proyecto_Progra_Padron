package padron.presentacion.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import padron.logica.ServicioPadron;

/**
 * Servidor TCP concurrente para consultas al padron electoral.
 * Protocolo: GET|cedula|JSON o GET|cedula|XML
 */
public class ServidorTCP implements Runnable {

    private static final int MAX_HILOS = 10;

    private final int            puerto;
    private final ServicioPadron servicio;

    public ServidorTCP(int puerto, ServicioPadron servicio) {
        this.puerto   = puerto;
        this.servicio = servicio;
    }

    public void iniciar() { run(); }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_HILOS);
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor TCP escuchando en puerto " + puerto);
            while (!Thread.currentThread().isInterrupted()) {
                Socket cliente = servidor.accept();
                pool.execute(new ManejadorCliente(cliente, servicio));
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar servidor TCP: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
