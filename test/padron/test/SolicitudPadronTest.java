package padron.test;

import padron.dto.FormatoSalida;
import padron.dto.SolicitudPadron;

/**
 * Pruebas para SolicitudPadron.parsear().
 * Verifica el parsing del protocolo TCP y la detección de formatos.
 */
public class SolicitudPadronTest {

    public static void runAll() {
        System.out.println("\n--- SolicitudPadronTest ---");

        parseoValido_JSON();
        parseoValido_XML();
        parseoValido_cedulaConGuiones();
        parseo_soloDosCampos_lanzaExcepcion();
        parseo_cuatroCampos_lanzaExcepcion();
        parseo_lineaVacia_lanzaExcepcion();
        parseo_lineaNula_lanzaExcepcion();
        parseo_comandoNoGET_lanzaExcepcion();
        parseo_formatoDesconocido_lanzaExcepcion();
        parseo_cedulaVacia_lanzaExcepcion();
        parseo_soloGuiones_cedulaVacia();
    }

    private static void parseoValido_JSON() {
        SolicitudPadron s = SolicitudPadron.parsear("GET|101053316|JSON");
        TestRunner.assertEquals("parseo válido JSON – cédula",
            "101053316", s.getCedula());
        TestRunner.assertEquals("parseo válido JSON – formato",
            FormatoSalida.JSON, s.getFormato());
    }

    private static void parseoValido_XML() {
        SolicitudPadron s = SolicitudPadron.parsear("GET|200012345|XML");
        TestRunner.assertEquals("parseo válido XML – formato",
            FormatoSalida.XML, s.getFormato());
    }

    private static void parseoValido_cedulaConGuiones() {
        // El parseo acepta cédulas con guiones; la normalización ocurre en el servicio
        SolicitudPadron s = SolicitudPadron.parsear("GET|1-0105-3316|JSON");
        TestRunner.assertEquals("parseo cédula con guiones – cédula cruda",
            "1-0105-3316", s.getCedula());
    }

    private static void parseo_soloDosCampos_lanzaExcepcion() {
        TestRunner.assertThrows(
            "parseo 2 campos lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear("GET|101053316"),
            IllegalArgumentException.class);
    }

    private static void parseo_cuatroCampos_lanzaExcepcion() {
        TestRunner.assertThrows(
            "parseo 4 campos lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear("GET|101053316|JSON|EXTRA"),
            IllegalArgumentException.class);
    }

    private static void parseo_lineaVacia_lanzaExcepcion() {
        TestRunner.assertThrows(
            "parseo línea vacía lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear(""),
            IllegalArgumentException.class);
    }

    private static void parseo_lineaNula_lanzaExcepcion() {
        TestRunner.assertThrows(
            "parseo null lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear(null),
            IllegalArgumentException.class);
    }

    private static void parseo_comandoNoGET_lanzaExcepcion() {
        TestRunner.assertThrows(
            "parseo POST lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear("POST|101053316|JSON"),
            IllegalArgumentException.class);
    }

    private static void parseo_formatoDesconocido_lanzaExcepcion() {
        TestRunner.assertThrows(
            "parseo formato CSV lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear("GET|101053316|CSV"),
            IllegalArgumentException.class);
    }

    private static void parseo_cedulaVacia_lanzaExcepcion() {
        TestRunner.assertThrows(
            "parseo cédula vacía lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear("GET||JSON"),
            IllegalArgumentException.class);
    }

    private static void parseo_soloGuiones_cedulaVacia() {
        // "   " en cédula es tratado como vacío tras el trim
        TestRunner.assertThrows(
            "parseo cédula sólo espacios lanza IllegalArgumentException",
            () -> SolicitudPadron.parsear("GET|   |JSON"),
            IllegalArgumentException.class);
    }
}
