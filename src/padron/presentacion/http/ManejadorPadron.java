package padron.presentacion.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import padron.dto.FormatoSalida;
import padron.dto.RespuestaPadron;
import padron.logica.ServicioPadron;
import padron.util.Serializador;

/**
 * Handler HTTP que atiende las consultas al padrón.
 *
 * Acepta dos formas de especificar la cédula:
 *   1. Path variable:  GET /padron/{cedula}?format=json|xml
 *   2. Query param:    GET /padron?cedula={cedula}&format=json|xml
 *
 * Solo se permite el método GET; cualquier otro retorna 405.
 */
class ManejadorPadron implements HttpHandler {

    private final ServicioPadron servicio;

    ManejadorPadron(ServicioPadron servicio) {
        this.servicio = servicio;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Solo GET está permitido
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            responder(exchange, RespuestaPadron.metodoNoPermitido(), FormatoSalida.JSON);
            return;
        }

        try {
            String path  = exchange.getRequestURI().getPath();   // /padron  o  /padron/12345
            String query = exchange.getRequestURI().getQuery();   // cedula=...&format=...

            Map<String, String> params = parseQuery(query);

            // Determinar cédula: primero path variable, luego query param
            String cedula = null;
            String[] segmentos = path.split("/");
            if (segmentos.length >= 3 && !segmentos[2].isBlank()) {
                cedula = segmentos[2];
            } else {
                cedula = params.get("cedula");
            }

            // Determinar formato (defecto JSON)
            FormatoSalida formato = resolverFormato(params.getOrDefault("format", "json"));

            RespuestaPadron respuesta;
            if (cedula == null || cedula.isBlank()) {
                respuesta = RespuestaPadron.solicitudInvalida(
                    "El parámetro 'cedula' es requerido.");
            } else {
                respuesta = servicio.atender(cedula);
            }

            responder(exchange, respuesta, formato);

        } catch (Exception e) {
            responder(exchange,
                RespuestaPadron.errorInterno(e.getMessage()), FormatoSalida.JSON);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void responder(HttpExchange exchange, RespuestaPadron respuesta,
                           FormatoSalida formato) throws IOException {
        String cuerpo      = Serializador.serializar(respuesta, formato);
        String contentType = (formato == FormatoSalida.JSON)
            ? "application/json; charset=UTF-8"
            : "application/xml; charset=UTF-8";

        byte[] bytes = cuerpo.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(respuesta.getCodigoHttp(), bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private FormatoSalida resolverFormato(String token) {
        try {
            return FormatoSalida.desde(token);
        } catch (IllegalArgumentException e) {
            return FormatoSalida.JSON;
        }
    }

    /** Parsea la query string en un mapa clave→valor (claves en minúsculas). */
    private Map<String, String> parseQuery(String query) {
        Map<String, String> mapa = new HashMap<>();
        if (query == null || query.isBlank()) return mapa;
        for (String par : query.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) {
                mapa.put(kv[0].trim().toLowerCase(), kv[1].trim());
            }
        }
        return mapa;
    }
}
