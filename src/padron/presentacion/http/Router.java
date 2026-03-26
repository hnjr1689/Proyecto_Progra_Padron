package padron.presentacion.http;

/**
 * Clase auxiliar para manejar rutas del servidor HTTP.
 * Extrae la cedula de rutas tipo /padron/{cedula}
 * @author Dan
 */
public class Router {

    /*
     * Extrae la cedula de una ruta tipo /padron/123456789
     * Si la ruta no tiene el formato esperado retorna null
     */
    public static String extraerCedula(String ruta) {
        if (ruta == null) return null;
        String[] partes = ruta.split("/");
        if (partes.length >= 3) {
            return partes[2];
        }
        return null;
    }
}