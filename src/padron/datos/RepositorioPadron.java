package padron.datos;

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
     * como es el formato costarricense.
     */
    private boolean esCedulaValida(String cedulaNorm) {
        return cedulaNorm.length() >= 9 && cedulaNorm.length() <= 10;
    }

}
