package padron;

import padron.datos.RepositorioDistelec;
import padron.datos.RepositorioPadron;
import padron.logica.ServicioPadron;
import padron.presentacion.http.ServidorHTTP;
import padron.presentacion.tcp.ServidorTCP;

/**
 * Punto de entrada de la aplicacion Padron Electoral.
 * Inicia ServidorHTTP en el puerto 8080 (hilo separado daemon) y
 * ServidorTCP en el puerto 5000 (hilo principal, bloqueante).
 *
 * Archivos de datos esperados en el directorio de trabajo:
 *   datos/PADRON.txt    -- descargar del TSE manualmente (no incluido en repo)
 *   datos/distelec.txt  -- incluido en el repo
 */
public class Main {

    private static final String RUTA_PADRON   = "datos/PADRON.txt";
    private static final String RUTA_DISTELEC = "datos/distelec.txt";

    private static final int PUERTO_HTTP = 8080;
    private static final int PUERTO_TCP  = 5000;

    public static void main(String[] args) {
        System.out.println("=== Padron Electoral ===");

        System.out.println("Cargando distelec...");
        RepositorioDistelec distelec = new RepositorioDistelec(RUTA_DISTELEC);

        System.out.println("Repositorio de padron listo: " + RUTA_PADRON);
        RepositorioPadron padron = new RepositorioPadron(RUTA_PADRON, distelec);

        ServicioPadron servicio = new ServicioPadron(padron);

        ServidorHTTP servidorHttp = new ServidorHTTP(PUERTO_HTTP, servicio);
        Thread hiloHttp = new Thread(servidorHttp, "hilo-http");
        hiloHttp.setDaemon(true);
        hiloHttp.start();

        ServidorTCP servidorTcp = new ServidorTCP(PUERTO_TCP, servicio);
        servidorTcp.iniciar();
    }
}
