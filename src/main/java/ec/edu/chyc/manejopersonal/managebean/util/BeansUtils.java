/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.util;

import ec.edu.chyc.manejopersonal.managebean.GestorArticulo;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author marcelocaj
 */
public class BeansUtils {

    public static void ejecutarJS(String codigo) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute(codigo);
    }

    /***
     * Dado el archivo de fileUpload de primefaces (en el evento fileUpload), sube el archivo a un destino
     * @param origen Variable tipo UploadedFile obtenida del evento fileUpload del componente de primefaces, ahí se obtendrá el archivo
     * @param destino Archivo donde se guardará el archivo
     * @throws IOException Excepción ocurrida al crear, leer o escribir el archivo
     */
    public static void subirArchivoPrimefaces(UploadedFile origen, File destino) throws IOException {
        destino.createNewFile();

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            fileOutputStream = new FileOutputStream(destino);

            byte[] buffer = new byte[ServerUtils.BUFFER_SIZE];

            int bulk;
            inputStream = origen.getInputstream();

            while (true) {
                bulk = inputStream.read(buffer);
                if (bulk < 0) {
                    break;
                }
                fileOutputStream.write(buffer, 0, bulk);
                fileOutputStream.flush();
            }

            fileOutputStream.close();
            inputStream.close();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public static StreamedContent streamParaDescarga(Path direccionArchivoOrigen, String nombreArchivoDescarga) throws FileNotFoundException {
        String nombreArchivoGuardado = direccionArchivoOrigen.getFileName().toString();
        String extension = FilenameUtils.getExtension(nombreArchivoGuardado);

        String nuevoNombre = ServerUtils.convertirNombreArchivo(nombreArchivoDescarga, extension, 40);

        Path pathArchivo = direccionArchivoOrigen;
        InputStream stream = new BufferedInputStream(new FileInputStream(pathArchivo.toFile()));

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        StreamedContent streamParaDescarga = new DefaultStreamedContent(stream, externalContext.getMimeType(nuevoNombre), nuevoNombre);
        return streamParaDescarga;
    }
}
