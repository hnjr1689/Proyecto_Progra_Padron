package padron.dto;

import padron.entidades.Persona;

/**
 * DTO que encapsula el resultado de una consulta al padrón.
 * <p>
 * Si la consulta fue exitosa, {@code persona} contiene los datos y
 * {@code error} es {@code null}.<br>
 * Si hubo un error, {@code persona} es {@code null} y {@code error}
 * describe el problema.
 */
public class RespuestaPadron {

    private final Persona persona;
    private final String error;
    private final int codigoHttp;

    private RespuestaPadron(Persona persona, String error, int codigoHttp) {
        this.persona = persona;
        this.error = error;
        this.codigoHttp = codigoHttp;
    }

    // ---------------------------------------------------------------
    // Fábricas estáticas
    // ---------------------------------------------------------------

    /** Respuesta exitosa (HTTP 200). */
    public static RespuestaPadron exitosa(Persona persona) {
        return new RespuestaPadron(persona, null, 200);
    }

    /** Cédula no encontrada en el padrón (HTTP 404). */
    public static RespuestaPadron noEncontrada(String cedula) {
        return new RespuestaPadron(null,
                "Persona no encontrada para la cédula: " + cedula, 404);
    }

    /** Solicitud con formato o datos inválidos (HTTP 400). */
    public static RespuestaPadron solicitudInvalida(String detalle) {
        return new RespuestaPadron(null, detalle, 400);
    }

    /** Método HTTP no permitido (HTTP 405). */
    public static RespuestaPadron metodoNoPermitido() {
        return new RespuestaPadron(null,
                "Método no permitido. Solo se admite GET.", 405);
    }

    /** Error interno del servidor (HTTP 500). */
    public static RespuestaPadron errorInterno(String detalle) {
        return new RespuestaPadron(null, "Error interno: " + detalle, 500);
    }

    // ---------------------------------------------------------------
    // Accesores
    // ---------------------------------------------------------------

    public Persona getPersona() {
        return persona;
    }

    public String getError() {
        return error;
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }

    public boolean esExitosa() {
        return persona != null;
    }

    // ---------------------------------------------------------------
    // Serialización JSON mínima (sin dependencias externas)
    // ---------------------------------------------------------------

    /** Serializa la respuesta al formato JSON acordado. */
    public String toJson() {
        if (esExitosa()) {
            var p = persona;
            var d = p.getDireccion();
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
                    d != null ? d.getCanton() : "",
                    d != null ? d.getDistrito() : "");
        } else {
            return """
                    {
                      "error": "%s",
                      "codigo": %d
                    }""".formatted(error, codigoHttp);
        }
    }

    @Override
    public String toString() {
        return "RespuestaPadron{codigo=" + codigoHttp +
                (esExitosa() ? ", persona=" + persona : ", error='" + error + "'") + "}";
    }
}
