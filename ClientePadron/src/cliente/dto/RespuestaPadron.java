package cliente.dto;

/**
 * DTO que encapsula los datos parseados de la respuesta del servidor.
 */
public class RespuestaPadron {

    private final boolean exitosa;

    // Campos de una respuesta exitosa
    private final String cedula;
    private final String nombre;
    private final String primerApellido;
    private final String segundoApellido;
    private final String codElec;
    private final String provincia;
    private final String canton;
    private final String distrito;

    // Campos de una respuesta de error
    private final String mensajeError;
    private final int    codigoError;

    private RespuestaPadron(boolean exitosa,
                            String cedula, String nombre,
                            String primerApellido, String segundoApellido,
                            String codElec,
                            String provincia, String canton, String distrito,
                            String mensajeError, int codigoError) {
        this.exitosa         = exitosa;
        this.cedula          = cedula;
        this.nombre          = nombre;
        this.primerApellido  = primerApellido;
        this.segundoApellido = segundoApellido;
        this.codElec         = codElec;
        this.provincia       = provincia;
        this.canton          = canton;
        this.distrito        = distrito;
        this.mensajeError    = mensajeError;
        this.codigoError     = codigoError;
    }

    public static RespuestaPadron exitosa(String cedula, String nombre,
                                          String primerApellido, String segundoApellido,
                                          String codElec,
                                          String provincia, String canton, String distrito) {
        return new RespuestaPadron(true,
            cedula, nombre, primerApellido, segundoApellido,
            codElec, provincia, canton, distrito,
            null, 200);
    }

    public static RespuestaPadron error(String mensaje, int codigo) {
        return new RespuestaPadron(false,
            null, null, null, null, null, null, null, null,
            mensaje, codigo);
    }

    public boolean isExitosa()          { return exitosa; }
    public String  getCedula()          { return cedula; }
    public String  getNombre()          { return nombre; }
    public String  getPrimerApellido()  { return primerApellido; }
    public String  getSegundoApellido() { return segundoApellido; }
    public String  getCodElec()         { return codElec; }
    public String  getProvincia()       { return provincia; }
    public String  getCanton()          { return canton; }
    public String  getDistrito()        { return distrito; }
    public String  getMensajeError()    { return mensajeError; }
    public int     getCodigoError()     { return codigoError; }

    public String getNombreCompleto() {
        if (!exitosa) return "";
        return nombre + " " + primerApellido + " " + segundoApellido;
    }
}
