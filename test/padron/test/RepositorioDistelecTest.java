package padron.test;

import padron.datos.RepositorioDistelec;
import padron.entidades.Direccion;

/**
 * Pruebas para RepositorioDistelec.
 * Usa el archivo datos/distelec.txt incluido en el repositorio.
 */
public class RepositorioDistelecTest {

    private static final String RUTA_DISTELEC = "datos/distelec.txt";

    public static void runAll() {
        System.out.println("\n--- RepositorioDistelecTest ---");

        carga_archivoReal_registrosMayorQueCero();
        buscar_codigoExistente_retornaDireccion();
        buscar_provinciaCorrecta();
        buscar_codigoInexistente_retornaNull();
        buscar_codigoNulo_retornaNull();
        buscar_codigoVacio_retornaNull();
        buscar_segundoCodigoConocido();
        totalRegistros_mayorQueCero();
    }

    private static RepositorioDistelec crearRepo() {
        return new RepositorioDistelec(RUTA_DISTELEC);
    }

    private static void carga_archivoReal_registrosMayorQueCero() {
        RepositorioDistelec repo = crearRepo();
        TestRunner.assertTrue("distelec cargado tiene registros",
            repo.totalRegistros() > 0);
    }

    private static void buscar_codigoExistente_retornaDireccion() {
        // 101001 = SAN JOSE, CENTRAL, HOSPITAL (primera línea de distelec.txt)
        Direccion d = crearRepo().buscarPorCodigo("101001");
        TestRunner.assertTrue("código 101001 existe", d != null);
    }

    private static void buscar_provinciaCorrecta() {
        Direccion d = crearRepo().buscarPorCodigo("101001");
        TestRunner.assertEquals("código 101001 → provincia SAN JOSE",
            "SAN JOSE", d != null ? d.getProvincia().trim() : null);
    }

    private static void buscar_codigoInexistente_retornaNull() {
        Direccion d = crearRepo().buscarPorCodigo("999999");
        TestRunner.assertTrue("código 999999 no existe → null", d == null);
    }

    private static void buscar_codigoNulo_retornaNull() {
        Direccion d = crearRepo().buscarPorCodigo(null);
        TestRunner.assertTrue("buscar null → null", d == null);
    }

    private static void buscar_codigoVacio_retornaNull() {
        Direccion d = crearRepo().buscarPorCodigo("   ");
        TestRunner.assertTrue("buscar vacío → null", d == null);
    }

    private static void buscar_segundoCodigoConocido() {
        // 101002 = SAN JOSE, CENTRAL, ZAPOTE
        Direccion d = crearRepo().buscarPorCodigo("101002");
        TestRunner.assertTrue("código 101002 existe", d != null);
        TestRunner.assertEquals("código 101002 → cantón CENTRAL",
            "CENTRAL", d != null ? d.getCanton().trim() : null);
    }

    private static void totalRegistros_mayorQueCero() {
        TestRunner.assertTrue("totalRegistros() > 0",
            crearRepo().totalRegistros() > 0);
    }
}
