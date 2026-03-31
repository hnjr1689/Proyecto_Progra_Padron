package padron.logica;

import padron.datos.RepositorioPadron;
import padron.dto.RespuestaPadron;
import padron.entidades.Persona;

/**
 * Capa de lógica de negocio.
 * Orquesta la consulta entre los repositorios y devuelve un RespuestaPadron.
 * Ambos servidores (TCP y HTTP) deben usar este servicio — nunca los repositorios
 * directamente — para mantener la separación de capas.
 */
public class ServicioPadron {

    private final RepositorioPadron repositorio;

    public ServicioPadron(RepositorioPadron repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Atiende una consulta por cédula y retorna la respuesta correspondiente.
     *
     * @param cedula cédula recibida del cliente (puede tener guiones o espacios)
     * @return RespuestaPadron con la persona encontrada o el error apropiado
     */
    public RespuestaPadron atender(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            return RespuestaPadron.solicitudInvalida("La cédula no puede estar vacía.");
        }

        try {
            Persona persona = repositorio.buscarPorCedula(cedula);
            if (persona == null) {
                return RespuestaPadron.noEncontrada(cedula);
            }
            return RespuestaPadron.exitosa(persona);
        } catch (Exception e) {
            System.err.println("Error en ServicioPadron.atender: " + e.getMessage());
            return RespuestaPadron.errorInterno(e.getMessage());
        }
    }
}
