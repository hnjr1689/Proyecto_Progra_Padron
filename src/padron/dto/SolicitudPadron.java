package padron.dto;

/**
 * DTO que encapsula los datos de una solicitud de consulta al padrón.
 * <p>
 * Protocolo TCP esperado: {@code GET|<cedula>|<formato>}
 * Ejemplo: {@code GET|123456789|JSON}
 */
public class SolicitudPadron {

    private final String       cedula;
    private final FormatoSalida formato;

    public SolicitudPadron(String cedula, FormatoSalida formato) {
        this.cedula  = cedula;
        this.formato = formato;
    }

    /**
     * Parsea una línea del protocolo TCP con el formato {@code GET|cedula|formato}.
     *
     * @param linea texto recibido del cliente
     * @return SolicitudPadron construida a partir de la línea
     * @throws IllegalArgumentException si la línea no cumple el protocolo
     */
    public static SolicitudPadron parsear(String linea) {
        if (linea == null || linea.isBlank()) {
            throw new IllegalArgumentException("La solicitud no puede estar vacía.");
        }

        String[] partes = linea.trim().split("\\|");

        if (partes.length != 3) {
            throw new IllegalArgumentException(
                "Formato inválido. Se esperaba GET|cedula|formato, se recibió: " + linea);
        }

        if (!"GET".equalsIgnoreCase(partes[0].trim())) {
            throw new IllegalArgumentException(
                "Operación no soportada: '" + partes[0].trim() + "'. Solo se admite GET.");
        }

        String cedula = partes[1].trim();
        if (cedula.isEmpty()) {
            throw new IllegalArgumentException("La cédula no puede estar vacía.");
        }

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
