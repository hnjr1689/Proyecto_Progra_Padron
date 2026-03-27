package padron.util;

import padron.dto.RespuestaPadron;
import padron.entidades.Direccion;
import padron.entidades.Persona;

/**
 * Clase que convierte una RespuestaPadron
 * a formato JSON o XML segun lo que pida el cliente.
 * @author Fabian
 */
public class Serializador {

    /**
     * Convierte la respuesta a formato JSON.
     * Si fue exitosa devuelve los datos de la persona,
     * si no devuelve el error con su codigo.
     * @param respuesta objeto RespuestaPadron a convertir
     * @return String con el JSON generado
     */
    public static String toJson(RespuestaPadron respuesta) {
        if (respuesta.esExitosa()) {
            Persona p = respuesta.getPersona();
            Direccion d = p.getDireccion();
            return String.format("""
                {
                  "cedula": "%s",
                  "nombre": "%s",
                  "primerApellido": "%s",
                  "segundoApellido": "%s",
                  "provincia": "%s",
                  "canton": "%s",
                  "distrito": "%s"
                }""",
                p.getCedula(),
                p.getNombre(),
                p.getPrimerApellido(),
                p.getSegundoApellido(),
                d != null ? d.getProvincia() : "",
                d != null ? d.getCanton()    : "",
                d != null ? d.getDistrito()  : ""
            );
        } else {
            return String.format("""
                {
                  "error": "%s",
                  "codigo": %d
                }""",
                respuesta.getError(),
                respuesta.getCodigoHttp()
            );
        }
    }

    /**
     * Convierte la respuesta a formato XML.
     * Si fue exitosa devuelve los datos de la persona,
     * si no devuelve el error con su codigo.
     * @param respuesta objeto RespuestaPadron a convertir
     * @return String con el XML generado
     */
    public static String toXml(RespuestaPadron respuesta) {
        if (respuesta.esExitosa()) {
            Persona p = respuesta.getPersona();
            Direccion d = p.getDireccion();
            return String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <persona>
                  <cedula>%s</cedula>
                  <nombre>%s</nombre>
                  <primerApellido>%s</primerApellido>
                  <segundoApellido>%s</segundoApellido>
                  <provincia>%s</provincia>
                  <canton>%s</canton>
                  <distrito>%s</distrito>
                </persona>""",
                p.getCedula(),
                p.getNombre(),
                p.getPrimerApellido(),
                p.getSegundoApellido(),
                d != null ? d.getProvincia() : "",
                d != null ? d.getCanton()    : "",
                d != null ? d.getDistrito()  : ""
            );
        } else {
            return String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <error>
                  <mensaje>%s</mensaje>
                  <codigo>%d</codigo>
                </error>""",
                respuesta.getError(),
                respuesta.getCodigoHttp()
            );
        }
    }
}

                
