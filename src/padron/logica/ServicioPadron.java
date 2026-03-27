package padron.logica;

import padron.datos.RepositorioPadron;
import padron.datos.RepositorioDistelec;
import padron.dto.RespuestaPadron;
import padron.entidades.Persona;

/**
 * Servicio principal de negocio del padron electoral.
 * Recibe una cedula, consulta los repositorios y retorna
 * una RespuestaPadron lista para serializar.
 * @author Fabian
 */
public class ServicioPadron {

    private final RepositorioPadron repositorioPadron;

    public ServicioPadron(RepositorioPadron repositorioPadron) {
        this.repositorioPadron = repositorioPadron;
    }

    /**
     * Atiende una consulta por cedula.
     * Valida que la cedula no este vacia y que tenga numeros validos.
     * @param cedula numero de cedula a buscar
     * @return RespuestaPadron con los datos o con el error correspondiente
     */
    public RespuestaPadron atender(String cedula) {

        // Validar que la cedula no venga vacia
        if (cedula == null || cedula.isBlank()) {
            return RespuestaPadron.solicitudInvalida(
                "La cedula no puede estar vacia.");
        }

        // Quitar guiones y espacios antes de buscar
        String cedulaNorm = cedula.trim().replaceAll("[^0-9]", "");
        if (cedulaNorm.isEmpty()) {
            return RespuestaPadron.solicitudInvalida(
                "La cedula no contiene numeros validos.");
        }

        // Buscar la persona en el padron
        Persona persona = repositorioPadron.buscarPorCedula(cedulaNorm);

        // Si no se encontro retornar error 404
        if (persona == null) {
            return RespuestaPadron.noEncontrada(cedula);
        }

        // Todo bien, retornar la persona encontrada
        return RespuestaPadron.exitosa(persona);
    }
}