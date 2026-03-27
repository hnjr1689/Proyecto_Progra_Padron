package padron.dto;

import padron.entidades.Persona;

/**
 * DTO que encapsula el resultado de una consulta al padrón.
 */
public class RespuestaPadron {

    private final Persona persona;
    private final String  error;
    private final int     codigoHttp;

    private RespuestaPadron(Persona persona, String error, int codigoHttp) {
        this.persona    = persona;
        this.error      = error;
        this.codigoHttp = codigoHttp;
    }

    public static RespuestaPadron exitosa(Persona persona) {
        return new RespuestaPadron(persona, null, 200);
    }

    public static RespuestaPadron noEncontrada(String cedula) {
        return new RespuestaPadron(null,
            "Persona no encontrada para la cédula: " + cedula, 404);
    }

    public static RespuestaPadron solicitudInvalida(String detalle) {
        return new RespuestaPadron(null, detalle, 400);
    }

    public static RespuestaPadron metodoNoPermitido() {
        return new RespuestaPadron(null,
            "Método no permitido. Solo se admite GET.", 405);
    }

    public static RespuestaPadron errorInterno(String detalle) {
        return new RespuestaPadron(null, "Error interno: " + detalle, 500);
    }

    public Persona getPersona()    { return persona; }
    public String  getError()      { return error; }
    public int     getCodigoHttp() { return codigoHttp; }
    public boolean esExitosa()     { return persona != null; }

    @Override
    public String toString() {
        return "RespuestaPadron{codigo=" + codigoHttp +
            (esExitosa() ? ", persona=" + persona
                         : ", error='" + error + "'") + "}";
    }
}
