package cliente.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Cliente HTTP que consume la API REST del servidor del Padrón Electoral.
 * Endpoint:  GET /padron?cedula={cedula}&format={json|xml}
 */
public class ClienteHTTP {

    private final String baseUrl;

    public ClienteHTTP(String baseUrl) {
        this.baseUrl = baseUrl; // ej. "http://localhost:8080"
    }

    /**
     * Consulta la cédula vía HTTP GET y retorna la respuesta cruda del servidor.
     *
     * @param cedula  cédula a consultar
     * @param formato "json" o "xml"
     * @return texto de la respuesta (JSON o XML)
     * @throws IOException si no hay conexión o el servidor responde con error de red
     */
    public String consultar(String cedula, String formato) throws IOException {
        String urlStr = baseUrl + "/padron?cedula="
            + cedularEscapada(cedula) + "&format=" + formato.toLowerCase();

        URL              url  = URI.create(urlStr).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(15000);

        int codigo = con.getResponseCode();
        // Para códigos 4xx/5xx el cuerpo viene en el error stream
        InputStream is = (codigo >= 400)
            ? con.getErrorStream()
            : con.getInputStream();

        if (is == null)
            throw new IOException("Sin respuesta del servidor (código " + codigo + ").");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, "UTF-8"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea).append('\n');
            }
        }
        return sb.toString().trim();
    }

    private static String cedularEscapada(String cedula) {
        // Solo se necesita escapar caracteres problemáticos en la URL
        return cedula == null ? "" : cedula.replace(" ", "%20");
    }
}
