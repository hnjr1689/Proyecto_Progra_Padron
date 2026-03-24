package padron.entidades;

/**
 * Entidad que representa la dirección electoral de una persona
 * (provincia, cantón y distrito).
 */
public class Direccion {

    private String provincia;
    private String canton;
    private String distrito;

    public Direccion() {}

    public Direccion(String provincia, String canton, String distrito) {
        this.provincia = provincia;
        this.canton    = canton;
        this.distrito  = distrito;
    }

    public String getProvincia()          { return provincia; }
    public void   setProvincia(String v)  { this.provincia = v; }

    public String getCanton()             { return canton; }
    public void   setCanton(String v)     { this.canton = v; }

    public String getDistrito()           { return distrito; }
    public void   setDistrito(String v)   { this.distrito = v; }

    /**
     * Retorna la dirección completa en un solo String.
     * @return provincia, canton y distrito concatenados.
     */
    public String getDireccionCompleta() {
        return provincia + ", " + canton + ", " + distrito;
    }

    @Override
    public String toString() {
        return provincia + ", " + canton + ", " + distrito;
    }
}
