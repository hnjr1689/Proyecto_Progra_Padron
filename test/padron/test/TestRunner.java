package padron.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejecutor de pruebas automatizadas sin dependencias externas.
 * Corre todos los grupos de test y muestra resultados por consola.
 */
public class TestRunner {

    private static int total   = 0;
    private static int pasados = 0;
    private static int fallidos = 0;

    private static final List<String> fallos = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== SUITE DE PRUEBAS AUTOMATIZADAS — Padrón Electoral ===\n");

        SolicitudPadronTest.runAll();
        SerializadorTest.runAll();
        ServicioPadronTest.runAll();
        RepositorioDistelecTest.runAll();

        System.out.println("\n========================================");
        System.out.printf("  Total: %d  |  Pasados: %d  |  Fallidos: %d%n",
            total, pasados, fallidos);
        System.out.println("========================================");

        if (fallidos > 0) {
            System.out.println("\nPruebas fallidas:");
            fallos.forEach(f -> System.out.println("  - " + f));
            System.exit(1);
        } else {
            System.out.println("\nTodas las pruebas pasaron correctamente.");
        }
    }

    // ── Métodos de aserción ──────────────────────────────────────────────

    public static void assertTrue(String nombre, boolean condicion) {
        total++;
        if (condicion) {
            pasados++;
            System.out.println("  [PASS] " + nombre);
        } else {
            fallidos++;
            fallos.add(nombre);
            System.out.println("  [FAIL] " + nombre);
        }
    }

    public static void assertEquals(String nombre, Object esperado, Object actual) {
        boolean ok = esperado == null ? actual == null : esperado.equals(actual);
        total++;
        if (ok) {
            pasados++;
            System.out.println("  [PASS] " + nombre);
        } else {
            fallidos++;
            fallos.add(nombre + " (esperado='" + esperado + "' actual='" + actual + "')");
            System.out.println("  [FAIL] " + nombre
                + " | esperado: '" + esperado + "' | actual: '" + actual + "'");
        }
    }

    public static void assertContains(String nombre, String texto, String fragmento) {
        boolean ok = texto != null && texto.contains(fragmento);
        total++;
        if (ok) {
            pasados++;
            System.out.println("  [PASS] " + nombre);
        } else {
            fallidos++;
            fallos.add(nombre + " ('" + fragmento + "' no encontrado en respuesta)");
            System.out.println("  [FAIL] " + nombre + " | buscando: '" + fragmento + "'");
        }
    }

    public static void assertThrows(String nombre, Runnable accion,
                                    Class<? extends Exception> tipoEsperado) {
        total++;
        try {
            accion.run();
            fallidos++;
            fallos.add(nombre + " (no lanzó " + tipoEsperado.getSimpleName() + ")");
            System.out.println("  [FAIL] " + nombre
                + " | debía lanzar " + tipoEsperado.getSimpleName());
        } catch (Exception e) {
            if (tipoEsperado.isInstance(e)) {
                pasados++;
                System.out.println("  [PASS] " + nombre);
            } else {
                fallidos++;
                fallos.add(nombre + " (lanzó " + e.getClass().getSimpleName()
                    + " en lugar de " + tipoEsperado.getSimpleName() + ")");
                System.out.println("  [FAIL] " + nombre
                    + " | lanzó: " + e.getClass().getSimpleName()
                    + " en lugar de " + tipoEsperado.getSimpleName());
            }
        }
    }
}
