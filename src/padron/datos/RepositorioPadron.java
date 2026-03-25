package padron.datos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import padron.entidades.Direccion;
import padron.entidades.Persona;

/**
 * Esta clase lee PADRON.txt linea por linea y busca una persona por cedula
 * Mejora el resultado con la direccion obtenida de distelec
 */
public class RepositorioPadron {

    private final String rutaArchivo;
    private final RepositorioDistelec distelec;

    public RepositorioPadron(String rutaArchivo, RepositorioDistelec distelec) {
        this.rutaArchivo = rutaArchivo;
        this.distelec = distelec;
    }

    /*
     * Valida que la cedula ingresada por la persona tenga entre 9 y 10 digitos
     * como es el formato costarricense
     */
    private boolean esCedulaValida(String cedulaNorm) {
        return cedulaNorm.length() >= 9 && cedulaNorm.length() <= 10;
    }

    /**
     * Busca una persona por cedula.
     * Depura la cedula eliminando guiones y espacios existentes
     * 
     * @return a la persona con direccion incluida, o null si esta no se encuentra
     */
    public Persona buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank())
            return null;

        String cedulaNorm = cedula.replaceAll("[^0-9]", "");
        if (cedulaNorm.isEmpty())
            return null;

        if (!esCedulaValida(cedulaNorm)) {
            System.err.println("Cedula con formato invalido: " + cedula);
            return null;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(rutaArchivo), "UTF-8"))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty())
                    continue;

                String[] partes = linea.split("\\|");
                if (partes.length < 5)
                    continue;

                String cedArchivo = partes[0].trim().replaceAll("[^0-9]", "");

                if (cedArchivo.equals(cedulaNorm)) {
                    String cedula2 = partes[0].trim();
                    String nombre = partes[1].trim();
                    String apellido1 = partes[2].trim();
                    String apellido2 = partes[3].trim();
                    String codElec = partes[4].trim();

                    Direccion dir = distelec.buscarPorCodigo(codElec);
                    return new Persona(cedula2, nombre, apellido1, apellido2, dir);
                }
            }

        } catch (IOException e) {
            System.err.println("Error leyendo PADRON.txt: " + e.getMessage());
        }

        return null;
    }
}