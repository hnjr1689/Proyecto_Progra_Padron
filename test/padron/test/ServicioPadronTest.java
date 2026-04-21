package padron.test;

import padron.datos.RepositorioPadron;
import padron.dto.RespuestaPadron;
import padron.dto.SolicitudPadron;
import padron.dto.FormatoSalida;
import padron.entidades.Direccion;
import padron.entidades.Persona;
import padron.logica.ServicioPadron;

/**
 * Pruebas para ServicioPadron.
 * Usa un repositorio simulado para no depender de PADRON.txt.
 */
public class ServicioPadronTest {

    /**
     * Repositorio simulado que devuelve datos fijos sin leer archivos.
     */
    private static class RepositorioSimulado extends RepositorioPadron {

        RepositorioSimulado() {
            super("RUTA_INEXISTENTE", null); // no lee nada en el constructor
        }

        @Override
        public Persona buscarPorCedula(String cedula) {
            if ("101053316".equals(cedula))
                return new Persona("101053316", "LUCILA", "PORRAS", "AGUERO",
                    new Direccion("SAN JOSE", "CENTRAL", "HOSPITAL"));
            return null;
        }
    }

    private static final ServicioPadron SERVICIO =
        new ServicioPadron(new RepositorioSimulado());

    public static void runAll() {
        System.out.println("\n--- ServicioPadronTest ---");

        atender_solicitudNula_retornaError400();
        atender_cedulaVacia_retornaError400();
        atender_cedulaSoloLetras_retornaError400();
        atender_cedulaDemesiadoCorta_retornaError400();
        atender_cedulaDemasiadoLarga_retornaError400();
        atender_cedulaNoEncontrada_retorna404();
        atender_cedulaValida_retornaPersona();
        atender_cedulaNormalizadaConGuiones_encuentraPersona();
    }

    private static void atender_solicitudNula_retornaError400() {
        RespuestaPadron r = SERVICIO.atender(null);
        TestRunner.assertEquals("solicitud null → código 400", 400, r.getCodigoHttp());
        TestRunner.assertTrue("solicitud null → no exitosa", !r.esExitosa());
    }

    private static void atender_cedulaVacia_retornaError400() {
        RespuestaPadron r = SERVICIO.atender(new SolicitudPadron("", FormatoSalida.JSON));
        TestRunner.assertEquals("cédula vacía → código 400", 400, r.getCodigoHttp());
    }

    private static void atender_cedulaSoloLetras_retornaError400() {
        RespuestaPadron r = SERVICIO.atender(new SolicitudPadron("ABC", FormatoSalida.JSON));
        TestRunner.assertEquals("cédula solo letras → código 400", 400, r.getCodigoHttp());
    }

    private static void atender_cedulaDemesiadoCorta_retornaError400() {
        RespuestaPadron r = SERVICIO.atender(new SolicitudPadron("12345678", FormatoSalida.JSON));
        TestRunner.assertEquals("cédula 8 dígitos → código 400", 400, r.getCodigoHttp());
    }

    private static void atender_cedulaDemasiadoLarga_retornaError400() {
        RespuestaPadron r = SERVICIO.atender(new SolicitudPadron("12345678901", FormatoSalida.JSON));
        TestRunner.assertEquals("cédula 11 dígitos → código 400", 400, r.getCodigoHttp());
    }

    private static void atender_cedulaNoEncontrada_retorna404() {
        RespuestaPadron r = SERVICIO.atender(new SolicitudPadron("000000000", FormatoSalida.JSON));
        TestRunner.assertEquals("cédula no encontrada → código 404", 404, r.getCodigoHttp());
        TestRunner.assertTrue("cédula no encontrada → no exitosa", !r.esExitosa());
    }

    private static void atender_cedulaValida_retornaPersona() {
        RespuestaPadron r = SERVICIO.atender(new SolicitudPadron("101053316", FormatoSalida.JSON));
        TestRunner.assertEquals("cédula válida → código 200", 200, r.getCodigoHttp());
        TestRunner.assertTrue("cédula válida → es exitosa", r.esExitosa());
        TestRunner.assertEquals("cédula válida → nombre correcto",
            "LUCILA", r.getPersona().getNombre());
    }

    private static void atender_cedulaNormalizadaConGuiones_encuentraPersona() {
        // El servicio quita guiones antes de buscar
        RespuestaPadron r = SERVICIO.atender(
            new SolicitudPadron("1-0105-3316", FormatoSalida.JSON));
        TestRunner.assertEquals("cédula con guiones → código 200", 200, r.getCodigoHttp());
        TestRunner.assertTrue("cédula con guiones → es exitosa", r.esExitosa());
    }
}
