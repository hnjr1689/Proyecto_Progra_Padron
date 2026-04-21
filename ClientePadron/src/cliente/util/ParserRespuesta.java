package cliente.util;

import cliente.dto.RespuestaPadron;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsea respuestas JSON o XML del servidor y las convierte a RespuestaPadron.
 * No usa librerías externas; extrae campos mediante expresiones regulares.
 */
public class ParserRespuesta {

    private ParserRespuesta() {}

    /**
     * Parsea la respuesta cruda del servidor al DTO.
     *
     * @param crudo  texto JSON o XML recibido del servidor
     * @param esXml  true si el formato es XML, false si es JSON
     * @return RespuestaPadron con los datos parseados
     */
    public static RespuestaPadron parsear(String crudo, boolean esXml) {
        if (crudo == null || crudo.isBlank())
            return RespuestaPadron.error("Respuesta vacía del servidor.", 0);
        try {
            return esXml ? parsearXml(crudo) : parsearJson(crudo);
        } catch (Exception e) {
            return RespuestaPadron.error("Error al parsear respuesta: " + e.getMessage(), 0);
        }
    }

    // ── JSON ──────────────────────────────────────────────────────────────

    private static RespuestaPadron parsearJson(String json) {
        // Determinar si es error o éxito por la presencia del campo "error"
        if (json.contains("\"error\"")) {
            String mensaje = extraerJsonString(json, "error");
            int    codigo  = extraerJsonInt(json, "codigo");
            return RespuestaPadron.error(
                mensaje.isEmpty() ? "Error del servidor." : mensaje,
                codigo);
        }

        String cedula          = extraerJsonString(json, "cedula");
        String nombre          = extraerJsonString(json, "nombre");
        String primerApellido  = extraerJsonString(json, "primerApellido");
        String segundoApellido = extraerJsonString(json, "segundoApellido");
        String provincia       = extraerJsonString(json, "provincia");
        String canton          = extraerJsonString(json, "canton");
        String distrito        = extraerJsonString(json, "distrito");

        return RespuestaPadron.exitosa(cedula, nombre, primerApellido,
            segundoApellido, "", provincia, canton, distrito);
    }

    private static String extraerJsonString(String json, String campo) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(campo)
            + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            // Desescapar secuencias básicas de JSON
            return m.group(1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n",  "\n")
                .replace("\\r",  "\r");
        }
        return "";
    }

    private static int extraerJsonInt(String json, String campo) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(campo) + "\"\\s*:\\s*([0-9]+)");
        Matcher m = p.matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    // ── XML ───────────────────────────────────────────────────────────────

    private static RespuestaPadron parsearXml(String xml) {
        if (xml.contains("<error>")) {
            String mensaje = extraerXmlTag(xml, "mensaje");
            int    codigo  = parsearInt(extraerXmlTag(xml, "codigo"));
            return RespuestaPadron.error(
                mensaje.isEmpty() ? "Error del servidor." : mensaje,
                codigo);
        }

        String cedula          = extraerXmlTag(xml, "cedula");
        String nombre          = extraerXmlTag(xml, "nombre");
        String primerApellido  = extraerXmlTag(xml, "primerApellido");
        String segundoApellido = extraerXmlTag(xml, "segundoApellido");
        String provincia       = extraerXmlTag(xml, "provincia");
        String canton          = extraerXmlTag(xml, "canton");
        String distrito        = extraerXmlTag(xml, "distrito");

        return RespuestaPadron.exitosa(cedula, nombre, primerApellido,
            segundoApellido, "", provincia, canton, distrito);
    }

    private static String extraerXmlTag(String xml, String etiqueta) {
        Pattern p = Pattern.compile(
            "<" + Pattern.quote(etiqueta) + ">([^<]*)</" + Pattern.quote(etiqueta) + ">");
        Matcher m = p.matcher(xml);
        if (m.find()) {
            return desescaparXml(m.group(1));
        }
        return "";
    }

    private static String desescaparXml(String s) {
        return s.replace("&amp;",  "&")
                .replace("&lt;",   "<")
                .replace("&gt;",   ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
    }

    private static int parsearInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
