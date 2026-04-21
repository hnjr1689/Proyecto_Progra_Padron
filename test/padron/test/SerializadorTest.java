package padron.test;

import padron.dto.FormatoSalida;
import padron.dto.RespuestaPadron;
import padron.entidades.Direccion;
import padron.entidades.Persona;
import padron.util.Serializador;

/**
 * Pruebas para Serializador.
 * Verifica la generación correcta de JSON y XML, incluyendo escape de caracteres.
 */
public class SerializadorTest {

    private static final Persona PERSONA_OK = new Persona(
        "101053316", "LUCILA", "PORRAS", "AGUERO",
        new Direccion("SAN JOSE", "CENTRAL", "HOSPITAL"));

    private static final Persona PERSONA_SIN_DIR = new Persona(
        "200012345", "JUAN", "PEREZ", "RODRIGUEZ", null);

    public static void runAll() {
        System.out.println("\n--- SerializadorTest ---");

        json_respuestaExitosa_contienesCedula();
        json_respuestaExitosa_contieneDireccion();
        json_respuestaError_contieneError();
        json_respuestaError_contieneCodigo();
        json_personaSinDireccion_camposVacios();

        xml_respuestaExitosa_tieneEtiquetaPersona();
        xml_respuestaExitosa_contieneCedula();
        xml_respuestaError_tieneEtiquetaError();
        xml_respuestaExitosa_personaSinDireccion();

        json_escape_comillasEnNombre();
        xml_escape_ampersandEnNombre();

        serializar_delegaAJson();
        serializar_delegaAXml();
    }

    // ── JSON ────────────────────────────────────────────────────────────

    private static void json_respuestaExitosa_contienesCedula() {
        String json = Serializador.toJson(RespuestaPadron.exitosa(PERSONA_OK));
        TestRunner.assertContains("JSON éxito contiene cédula", json, "\"cedula\"");
        TestRunner.assertContains("JSON éxito valor cédula", json, "101053316");
    }

    private static void json_respuestaExitosa_contieneDireccion() {
        String json = Serializador.toJson(RespuestaPadron.exitosa(PERSONA_OK));
        TestRunner.assertContains("JSON éxito contiene provincia", json, "SAN JOSE");
        TestRunner.assertContains("JSON éxito contiene distrito", json, "HOSPITAL");
    }

    private static void json_respuestaError_contieneError() {
        String json = Serializador.toJson(RespuestaPadron.noEncontrada("000000000"));
        TestRunner.assertContains("JSON error contiene campo error", json, "\"error\"");
    }

    private static void json_respuestaError_contieneCodigo() {
        String json = Serializador.toJson(RespuestaPadron.solicitudInvalida("Cédula vacía."));
        TestRunner.assertContains("JSON error código 400", json, "400");
    }

    private static void json_personaSinDireccion_camposVacios() {
        String json = Serializador.toJson(RespuestaPadron.exitosa(PERSONA_SIN_DIR));
        TestRunner.assertContains("JSON sin dirección provincia vacía", json, "\"provincia\": \"\"");
    }

    // ── XML ─────────────────────────────────────────────────────────────

    private static void xml_respuestaExitosa_tieneEtiquetaPersona() {
        String xml = Serializador.toXml(RespuestaPadron.exitosa(PERSONA_OK));
        TestRunner.assertContains("XML éxito tiene <persona>", xml, "<persona>");
        TestRunner.assertContains("XML éxito tiene </persona>", xml, "</persona>");
    }

    private static void xml_respuestaExitosa_contieneCedula() {
        String xml = Serializador.toXml(RespuestaPadron.exitosa(PERSONA_OK));
        TestRunner.assertContains("XML éxito contiene cédula", xml, "<cedula>101053316</cedula>");
    }

    private static void xml_respuestaError_tieneEtiquetaError() {
        String xml = Serializador.toXml(RespuestaPadron.noEncontrada("000000000"));
        TestRunner.assertContains("XML error tiene <error>", xml, "<error>");
        TestRunner.assertContains("XML error tiene </error>", xml, "</error>");
    }

    private static void xml_respuestaExitosa_personaSinDireccion() {
        String xml = Serializador.toXml(RespuestaPadron.exitosa(PERSONA_SIN_DIR));
        TestRunner.assertContains("XML sin dirección provincia vacía", xml, "<provincia></provincia>");
    }

    // ── Escape de caracteres ─────────────────────────────────────────────

    private static void json_escape_comillasEnNombre() {
        Persona p = new Persona("111111111", "JO\"AN", "SMITH", "DOE",
            new Direccion("SAN JOSE", "CENTRAL", "HOSPITAL"));
        String json = Serializador.toJson(RespuestaPadron.exitosa(p));
        TestRunner.assertContains("JSON escapa comillas en nombre", json, "\\\"");
    }

    private static void xml_escape_ampersandEnNombre() {
        Persona p = new Persona("222222222", "JOSE&MARIA", "LOPEZ", "CRUZ",
            new Direccion("ALAJUELA", "CENTRAL", "CENTRAL"));
        String xml = Serializador.toXml(RespuestaPadron.exitosa(p));
        TestRunner.assertContains("XML escapa & en nombre", xml, "&amp;");
    }

    // ── Método genérico serializar() ─────────────────────────────────────

    private static void serializar_delegaAJson() {
        String out = Serializador.serializar(
            RespuestaPadron.exitosa(PERSONA_OK), FormatoSalida.JSON);
        TestRunner.assertContains("serializar() delega a JSON", out, "{");
    }

    private static void serializar_delegaAXml() {
        String out = Serializador.serializar(
            RespuestaPadron.exitosa(PERSONA_OK), FormatoSalida.XML);
        TestRunner.assertContains("serializar() delega a XML", out, "<?xml");
    }
}
