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
     * como es el formato costarricense, si no cumple retorna falso
     */
    private boolean esCedulaValida(String cedulaNorm) {
        return cedulaNorm.length() >= 9 && cedulaNorm.length() <= 10;
    }

    /**
     * Busca una persona por cedula en el archivo PADRON.txt.
     * Antes de buscar le quita a la cedula los guiones, espacios
     * y cualquier caracter que no sea numero para evitar errores
     *
     * @return la persona con su direccion incluida, o null si no se encuentra
     */
    public Persona buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank())
            return null;

        // Le quitamos guiones, espacios y letras que pueda traer la cedula
        String cedulaNorm = cedula.trim().replaceAll("[^0-9]", "").trim();
        if (cedulaNorm.isEmpty())
            return null;

        // Si la cedula no tiene entre 9 y 10 digitos no tiene sentido buscarla
        if (!esCedulaValida(cedulaNorm)) {
            System.err.println("Cedula invalida, debe tener entre 9 y 10 digitos: " + cedula);
            return null;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(rutaArchivo), "UTF-8"))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty())
                    continue;

                // PADRON.txt: cedula,codElec,_,fecha,_,nombre,apellido1,apellido2
                String[] partes = linea.split(",");
                if (partes.length < 8)
                    continue;

                // Normalizamos la cedula del archivo igual que la que nos mandaron
                String cedArchivo = partes[0].trim().replaceAll("[^0-9]", "");

                if (cedArchivo.equals(cedulaNorm)) {
                    String cedula2   = partes[0].trim();
                    String codElec   = partes[1].trim();
                    String nombre    = partes[5].trim();
                    String apellido1 = partes[6].trim();
                    String apellido2 = partes[7].trim();

                    // Buscamos la direccion en distelec usando el codigo electoral
                    Direccion dir = distelec.buscarPorCodigo(codElec);
                    return new Persona(cedula2, nombre, apellido1, apellido2, dir);
                }
            }

        } catch (IOException e) {
            System.err.println("Error leyendo PADRON.txt: " + e.getMessage());
        }

        // Si no encontro nada retorna null
        return null;
    }
}
