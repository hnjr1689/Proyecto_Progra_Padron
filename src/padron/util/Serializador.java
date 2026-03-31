package padron.util;

import padron.dto.FormatoSalida;
import padron.dto.RespuestaPadron;
import padron.entidades.Direccion;
import padron.entidades.Persona;

/**
 * Utilidad de serialización.
 * Convierte un RespuestaPadron a texto JSON o XML sin dependencias externas.
 */
public class Serializador {

    private Serializador() {}

    /**
     * Serializa la respuesta al formato solicitado.
     *
     * @param respuesta resultado de la consulta
     * @param formato   JSON o XML
     * @return texto listo para enviar al cliente
     */
    public static String serializar(RespuestaPadron respuesta, FormatoSalida formato) {
        return switch (formato) {
            case JSON -> toJson(respuesta);
            case XML  -> toXml(respuesta);
        };
    }

    // ---------------------------------------------------------------
    // JSON
    // ---------------------------------------------------------------

    public static String toJson(RespuestaPadron respuesta) {
        if (respuesta.esExitosa()) {
            Persona p = respuesta.getPersona();
            Direccion d = p.getDireccion();
            return "{\n"
                + "  \"cedula\": \""         + esc(p.getCedula())                        + "\",\n"
                + "  \"nombre\": \""          + esc(p.getNombre())                        + "\",\n"
                + "  \"primerApellido\": \""  + esc(p.getPrimerApellido())                + "\",\n"
                + "  \"segundoApellido\": \"" + esc(p.getSegundoApellido())               + "\",\n"
                + "  \"provincia\": \""       + esc(d != null ? d.getProvincia() : "")   + "\",\n"
                + "  \"canton\": \""          + esc(d != null ? d.getCanton()    : "")   + "\",\n"
                + "  \"distrito\": \""        + esc(d != null ? d.getDistrito()  : "")   + "\"\n"
                + "}";
        } else {
            return "{\n"
                + "  \"error\": \"" + esc(respuesta.getError()) + "\",\n"
                + "  \"codigo\": "  + respuesta.getCodigoHttp() + "\n"
                + "}";
        }
    }

    // ---------------------------------------------------------------
    // XML
    // ---------------------------------------------------------------

    public static String toXml(RespuestaPadron respuesta) {
        if (respuesta.esExitosa()) {
            Persona p = respuesta.getPersona();
            Direccion d = p.getDireccion();
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<persona>\n"
                + "  <cedula>"         + xml(p.getCedula())                        + "</cedula>\n"
                + "  <nombre>"          + xml(p.getNombre())                        + "</nombre>\n"
                + "  <primerApellido>"  + xml(p.getPrimerApellido())                + "</primerApellido>\n"
                + "  <segundoApellido>" + xml(p.getSegundoApellido())               + "</segundoApellido>\n"
                + "  <provincia>"       + xml(d != null ? d.getProvincia() : "")   + "</provincia>\n"
                + "  <canton>"          + xml(d != null ? d.getCanton()    : "")   + "</canton>\n"
                + "  <distrito>"        + xml(d != null ? d.getDistrito()  : "")   + "</distrito>\n"
                + "</persona>";
        } else {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<error>\n"
                + "  <mensaje>" + xml(respuesta.getError())              + "</mensaje>\n"
                + "  <codigo>"  + respuesta.getCodigoHttp()              + "</codigo>\n"
                + "</error>";
        }
    }

    // ---------------------------------------------------------------
    // Helpers de escape
    // ---------------------------------------------------------------

    /** Escapa caracteres especiales para valores JSON. */
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /** Escapa caracteres especiales para contenido XML. */
    private static String xml(String s) {
        if (s == null) return "";
        return s.replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&apos;");
    }
}
