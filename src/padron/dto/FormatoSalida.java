package padron.dto;

/**
 * Formatos de respuesta soportados por el sistema.
 */
public enum FormatoSalida {
    JSON,
    XML;

    public static FormatoSalida desde(String token) {
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("El formato no puede estar vacío.");
        return switch (token.trim().toUpperCase()) {
            case "JSON" -> JSON;
            case "XML"  -> XML;
            default -> throw new IllegalArgumentException(
                "Formato desconocido: '" + token + "'. Use JSON o XML.");
        };
    }
}
