package padron.logica;

import padron.datos.RepositorioPadron;
import padron.dto.RespuestaPadron;
import padron.dto.SolicitudPadron;
import padron.entidades.Persona;

/**
 * Servicio principal de negocio.
 * Orquesta la consulta al padron validando y normalizando la cedula.
 */
public class ServicioPadron {

    private final RepositorioPadron padron;

    public ServicioPadron(RepositorioPadron padron) {
        this.padron = padron;
    }

    public RespuestaPadron atender(SolicitudPadron solicitud) {
        try {
            if (solicitud == null)
                return RespuestaPadron.solicitudInvalida("Solicitud nula.");

            String cedula = solicitud.getCedula();
            if (cedula == null || cedula.isBlank())
                return RespuestaPadron.solicitudInvalida("Cedula vacia.");

            String cedulaNorm = cedula.replaceAll("[^0-9]", "");
            if (cedulaNorm.isEmpty())
                return RespuestaPadron.solicitudInvalida("Cedula invalida.");

            if (cedulaNorm.length() < 9 || cedulaNorm.length() > 10)
                return RespuestaPadron.solicitudInvalida(
                    "Cedula debe tener entre 9 y 10 digitos.");

            Persona persona = padron.buscarPorCedula(cedulaNorm);
            if (persona == null)
                return RespuestaPadron.noEncontrada(cedulaNorm);

            return RespuestaPadron.exitosa(persona);

        } catch (Exception e) {
            System.err.println("Error en ServicioPadron.atender: " + e.getMessage());
            return RespuestaPadron.errorInterno(e.getMessage());
        }
    }
}
