package padron.presentacion.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import padron.dto.FormatoSalida;
import padron.dto.RespuestaPadron;
import padron.dto.SolicitudPadron;
import padron.logica.ServicioPadron;
import padron.util.Serializador;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja las peticiones HTTP al endpoint /padron.
 * Soporta query params y path variable.
 */
public class ManejadorHTTP implements HttpHandler {

    private final ServicioPadron servicio;

    public ManejadorHTTP(ServicioPadron servicio) {
        this.servicio = servicio;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Solo se permite GET
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            responder(exchange, 405,
                Serializador.serializar(
                    RespuestaPadron.metodoNoPermitido(), FormatoSalida.JSON),
                "application/json");
            return;
        }

        try {
            URI    uri    = exchange.getRequestURI();
            String path   = uri.getPath();
            Map<String, String> params = parsearQuery(uri.getQuery());

            String cedula = null;
            String format = params.getOrDefault("format", "json");

            // GET /padron/{cedula}?format=...
            if (path.matches("/padron/.+")) {
                cedula = path.substring("/padron/".length());
            } else {
                // GET /padron?cedula=...&format=...
                cedula = params.get("cedula");
            }

            // Determinar formato, default JSON si viene inválido
            FormatoSalida formato;
            try {
                formato = FormatoSalida.desde(format);
            } catch (IllegalArgumentException e) {
                formato = FormatoSalida.JSON;
            }

            String contentType = formato == FormatoSalida.XML
                ? "application/xml" : "application/json";

            SolicitudPadron solicitud = new SolicitudPadron(cedula, formato);
            RespuestaPadron respuesta = servicio.atender(solicitud);
            String cuerpo = Serializador.serializar(respuesta, formato);

            responder(exchange, respuesta.getCodigoHttp(), cuerpo, contentType);

        } catch (Exception e) {
            responder(exchange, 500,
                Serializador.serializar(
                    RespuestaPadron.errorInterno(e.getMessage()),
                    FormatoSalida.JSON),
                "application/json");
        }
    }

    private void responder(HttpExchange ex, int codigo,
                           String cuerpo, String tipo) throws IOException {
        byte[] bytes = cuerpo.getBytes("UTF-8");
        ex.getResponseHeaders().set("Content-Type", tipo + "; charset=UTF-8");
        ex.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parsearQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;
        for (String par : query.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }
}
