package padron;

import padron.datos.RepositorioDistelec;
import padron.datos.RepositorioPadron;
import padron.presentacion.http.ServidorHTTP;
import padron.presentacion.tcp.ServidorTCP;

/**
 * Punto de entrada de la aplicación Padrón Electoral.
 *
 * Inicia ServidorHTTP en el puerto 8080 (hilo separado) y
 * ServidorTCP en el puerto 5000 (hilo principal).
 *
 * Archivos de datos esperados en el directorio de trabajo:
 *   datos/PADRON.txt
 *   datos/distelec.txt
 */
public class Main {

    private static final String RUTA_PADRON   = "datos/PADRON.txt";
    private static final String RUTA_DISTELEC = "datos/distelec.txt";

    private static final int PUERTO_HTTP = 8080;
    private static final int PUERTO_TCP  = 5000;

    public static void main(String[] args) {
        System.out.println("=== Padrón Electoral ===");

        // Carga de datos compartidos
        System.out.println("Cargando datos desde: " + RUTA_DISTELEC);
        RepositorioDistelec distelec = new RepositorioDistelec(RUTA_DISTELEC);

        System.out.println("Cargando datos desde: " + RUTA_PADRON);
        RepositorioPadron padron = new RepositorioPadron(RUTA_PADRON, distelec);

        // ServidorHTTP arranca en un hilo separado (daemon)
        ServidorHTTP servidorHttp = new ServidorHTTP(PUERTO_HTTP, padron);
        Thread hiloHttp = new Thread(servidorHttp, "hilo-http");
        hiloHttp.setDaemon(true);
        hiloHttp.start();
        System.out.println("ServidorHTTP escuchando en puerto " + PUERTO_HTTP);

        // ServidorTCP corre en el hilo principal
        System.out.println("ServidorTCP escuchando en puerto " + PUERTO_TCP);
        ServidorTCP servidorTcp = new ServidorTCP(PUERTO_TCP, padron);
        servidorTcp.iniciar();
    }
}
