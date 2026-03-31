package padron;

import padron.datos.RepositorioDistelec;
import padron.datos.RepositorioPadron;
import padron.logica.ServicioPadron;
import padron.presentacion.http.ServidorHTTP;
import padron.presentacion.tcp.ServidorTCP;

/**
 * Punto de entrada de la aplicación Padrón Electoral.
 *
 * Inicia ServidorHTTP en el puerto 8080 (hilo separado daemon) y
 * ServidorTCP en el puerto 5000 (hilo principal, bloqueante).
 *
 * Archivos de datos esperados en el directorio de trabajo:
 *   datos/PADRON.txt    — padrón electoral (no incluido en el repo por su tamaño)
 *   datos/distelec.txt  — tabla de distritos electorales
 */
public class Main {

    private static final String RUTA_PADRON   = "datos/PADRON.txt";
    private static final String RUTA_DISTELEC = "datos/distelec.txt";

    private static final int PUERTO_HTTP = 8080;
    private static final int PUERTO_TCP  = 5000;

    public static void main(String[] args) {
        System.out.println("=== Padrón Electoral ===");

        // Capa de datos
        System.out.println("Cargando distelec...");
        RepositorioDistelec distelec = new RepositorioDistelec(RUTA_DISTELEC);

        System.out.println("Repositorio de padrón listo: " + RUTA_PADRON);
        RepositorioPadron padron = new RepositorioPadron(RUTA_PADRON, distelec);

        // Capa de lógica (única instancia compartida por ambos servidores)
        ServicioPadron servicio = new ServicioPadron(padron);

        // ServidorHTTP en hilo daemon
        ServidorHTTP servidorHttp = new ServidorHTTP(PUERTO_HTTP, servicio);
        Thread hiloHttp = new Thread(servidorHttp, "hilo-http");
        hiloHttp.setDaemon(true);
        hiloHttp.start();

        // ServidorTCP en hilo principal (bloqueante)
        ServidorTCP servidorTcp = new ServidorTCP(PUERTO_TCP, servicio);
        servidorTcp.iniciar();
    }
}
