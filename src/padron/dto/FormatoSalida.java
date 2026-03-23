package padron.dto;

/**
 * Formato de respuesta solicitado por el cliente.
 */
public enum FormatoSalida {

    /** Respuesta en formato JSON. */
    JSON,

    /** Respuesta en texto plano. */
    TEXTO;

    /**
     * Parsea el token recibido por red (insensible a mayúsculas).
     *
     * @param token cadena recibida (p. ej. "JSON" o "TEXTO")
     * @return el enum correspondiente
     * @throws IllegalArgumentException si el token no es reconocido
     */
    public static FormatoSalida desde(String token) {
        return switch (token.toUpperCase()) {
            case "JSON"  -> JSON;
            case "TEXTO", "TEXT" -> TEXTO;
            default -> throw new IllegalArgumentException(
                "Formato desconocido: '" + token + "'. Use JSON o TEXTO.");
        };
    }
}
