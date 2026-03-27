package padron.dto;

/**
 * DTO que encapsula los datos de una solicitud de consulta al padrón.
 * Protocolo TCP: GET|cedula|JSON  o  GET|cedula|XML
 */
public class SolicitudPadron {

    private final String        cedula;
    private final FormatoSalida formato;

    public SolicitudPadron(String cedula, FormatoSalida formato) {
        this.cedula  = cedula;
        this.formato = formato;
    }

    public static SolicitudPadron parsear(String linea) {
        if (linea == null || linea.isBlank())
            throw new IllegalArgumentException("La solicitud no puede estar vacía.");

        String[] partes = linea.trim().split("\\|");
        if (partes.length != 3)
            throw new IllegalArgumentException(
                "Formato inválido. Se esperaba GET|cedula|formato.");

        if (!"GET".equalsIgnoreCase(partes[0].trim()))
            throw new IllegalArgumentException(
                "Operación no soportada: '" + partes[0].trim() + "'. Solo GET.");

        String cedula = partes[1].trim();
        if (cedula.isEmpty())
            throw new IllegalArgumentException("La cédula no puede estar vacía.");

        FormatoSalida formato = FormatoSalida.desde(partes[2].trim());
        return new SolicitudPadron(cedula, formato);
    }

    public String        getCedula()  { return cedula; }
    public FormatoSalida getFormato() { return formato; }

    @Override
    public String toString() {
        return "SolicitudPadron{cedula='" + cedula + "', formato=" + formato + "}";
    }
}
