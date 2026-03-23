package padron.entidades;

/**
 * Entidad que representa a una persona inscrita en el padrón electoral.
 */
public class Persona {

    private String cedula;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private Direccion direccion;

    public Persona() {}

    public Persona(String cedula, String nombre, String primerApellido,
                   String segundoApellido, Direccion direccion) {
        this.cedula          = cedula;
        this.nombre          = nombre;
        this.primerApellido  = primerApellido;
        this.segundoApellido = segundoApellido;
        this.direccion       = direccion;
    }

    public String getCedula()            { return cedula; }
    public void   setCedula(String v)    { this.cedula = v; }

    public String getNombre()            { return nombre; }
    public void   setNombre(String v)    { this.nombre = v; }

    public String getPrimerApellido()           { return primerApellido; }
    public void   setPrimerApellido(String v)   { this.primerApellido = v; }

    public String getSegundoApellido()          { return segundoApellido; }
    public void   setSegundoApellido(String v)  { this.segundoApellido = v; }

    public Direccion getDireccion()             { return direccion; }
    public void      setDireccion(Direccion v)  { this.direccion = v; }

    /** Nombre completo en formato "Nombre PrimerApellido SegundoApellido". */
    public String getNombreCompleto() {
        return nombre + " " + primerApellido + " " + segundoApellido;
    }

    @Override
    public String toString() {
        return "Persona{cedula='" + cedula + "', nombre='" + getNombreCompleto() + "'}";
    }
}
