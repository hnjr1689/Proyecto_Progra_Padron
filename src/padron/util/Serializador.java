package padron.util;

import padron.dto.FormatoSalida;
import padron.dto.RespuestaPadron;
import padron.entidades.Direccion;
import padron.entidades.Persona;

/**
 * Convierte una RespuestaPadron a JSON o XML
 * según el formato solicitado por el cliente.
 */
public class Serializador {

    /**
     * Serializa la respuesta al formato indicado.
     */
    public static String serializar(RespuestaPadron respuesta,
                                    FormatoSalida formato) {
        return switch (formato) {
            case JSON -> toJson(respuesta);
            case XML  -> toXml(respuesta);
        };
    }

    private static String toJson(RespuestaPadron r) {
        if (r.esExitosa()) {
            Persona   p = r.getPersona();
            Direccion d = p.getDireccion();
            return """
                {
                  "cedula": "%s",
                  "nombre": "%s",
                  "primerApellido": "%s",
                  "segundoApellido": "%s",
                  "provincia": "%s",
                  "canton": "%s",
                  "distrito": "%s"
                }""".formatted(
                    p.getCedula(),
                    p.getNombre(),
                    p.getPrimerApellido(),
                    p.getSegundoApellido(),
                    d != null ? d.getProvincia() : "",
                    d != null ? d.getCanton()    : "",
                    d != null ? d.getDistrito()  : "");
        } else {
            return """
                {
                  "error": "%s",
                  "codigo": %d
                }""".formatted(escaparJson(r.getError()), r.getCodigoHttp());
        }
    }

    private static String toXml(RespuestaPadron r) {
        if (r.esExitosa()) {
            Persona   p = r.getPersona();
            Direccion d = p.getDireccion();
            return """
                <?xml version="1.0" encoding="UTF-8"?>
                <persona>
                  <cedula>%s</cedula>
                  <nombre>%s</nombre>
                  <primerApellido>%s</primerApellido>
                  <segundoApellido>%s</segundoApellido>
                  <provincia>%s</provincia>
                  <canton>%s</canton>
                  <distrito>%s</distrito>
                </persona>""".formatted(
                    escaparXml(p.getCedula()),
                    escaparXml(p.getNombre()),
                    escaparXml(p.getPrimerApellido()),
                    escaparXml(p.getSegundoApellido()),
                    d != null ? escaparXml(d.getProvincia()) : "",
                    d != null ? escaparXml(d.getCanton())    : "",
                    d != null ? escaparXml(d.getDistrito())  : "");
        } else {
            return """
                <?xml version="1.0" encoding="UTF-8"?>
                <error>
                  <mensaje>%s</mensaje>
                  <codigo>%d</codigo>
                </error>""".formatted(escaparXml(r.getError()), r.getCodigoHttp());
        }
    }

    private static String escaparXml(String texto) {
        if (texto == null) return "";
        return texto.replace("&",  "&amp;")
                    .replace("<",  "&lt;")
                    .replace(">",  "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'",  "&apos;");
    }

    private static String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"");
    }
}

                
