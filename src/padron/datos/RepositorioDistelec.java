package padron.datos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import padron.entidades.Direccion;

/**
 * Esta clase se encarga de cargar el archivo distelec.txt en memoria
 * usando un mapa para que las busquedas sean rapidas
 * Cada vez que se necesita una direccion se busca por su codigo electoral
 * 
 * @author Josue
 */
public class RepositorioDistelec {

    private final Map<String, Direccion> mapa = new HashMap<>();

    public RepositorioDistelec(String rutaArchivo) {
        cargarArchivo(rutaArchivo);
    }

    private void cargarArchivo(String ruta) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ruta), "UTF-8"))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty())
                    continue;

                String[] partes = linea.split("\\|");
                if (partes.length >= 4) {
                    String codElec = partes[0].trim();
                    String provincia = partes[1].trim();
                    String canton = partes[2].trim();
                    String distrito = partes[3].trim();
                    mapa.put(codElec, new Direccion(provincia, canton, distrito));
                }
            }
            System.out.println("Distelec cargado: " + mapa.size() + " registros.");

        } catch (IOException e) {
            System.err.println("Error cargando distelec.txt: " + e.getMessage());
        }
    }

    /**
     * Busca una direccion usando el codigo electoral como llave
     * Si el codigo no existe en el mapa retorna null
     * 
     * @param codElec codigo electoral a buscar
     * @return Direccion encontrada, o null si no existe
     */
    public Direccion buscarPorCodigo(String codElec) {
        if (codElec == null || codElec.isBlank())
            return null;
        return mapa.get(codElec.trim());
    }

    /**
     * Devuelve la cantidad total de direcciones cargadas en memoria
     * Util para verificar que el archivo se leyo correctamente
     * 
     * @return numero total de los registros cargados
     */
    public int totalRegistros() {
        return mapa.size();
    }
}