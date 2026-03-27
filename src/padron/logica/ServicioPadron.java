package padron.logica;

import padron.datos.RepositorioPadron;
import padron.dto.RespuestaPadron;
import padron.dto.SolicitudPadron;
import padron.entidades.Persona;

/**
 * Servicio principal de negocio.
 * Orquesta la consulta al padrón validando y normalizando la cédula.
 */
public class ServicioPadron {

    private final RepositorioPadron padron;

    public ServicioPadron(RepositorioPadron padron) {
        this.padron = padron;
    }
    
    /**
     * Atiende una solicitud de consulta al padrón.
     * Valida, normaliza la cédula y retorna la respuesta correspondiente.
     */
    public RespuestaPadron atender(SolicitudPadron solicitud) {
        try {
            if (solicitud == null)
                return RespuestaPadron.solicitudInvalida("Solicitud nula.");

            String cedula = solicitud.getCedula();
            if (cedula == null || cedula.isBlank())
                return RespuestaPadron.solicitudInvalida("Cédula vacía.");

            // Normalizar: quitar todo lo que no sea número
            String cedulaNorm = cedula.replaceAll("[^0-9]", "");
            if (cedulaNorm.isEmpty())
                return RespuestaPadron.solicitudInvalida("Cédula inválida.");

            if (cedulaNorm.length() < 9 || cedulaNorm.length() > 10)
                return RespuestaPadron.solicitudInvalida(
                    "Cédula debe tener entre 9 y 10 dígitos.");

            Persona persona = padron.buscarPorCedula(cedulaNorm);
            if (persona == null)
                return RespuestaPadron.noEncontrada(cedulaNorm);

            return RespuestaPadron.exitosa(persona);

        } catch (Exception e) {
            return RespuestaPadron.errorInterno(e.getMessage());
        }
    }
}


