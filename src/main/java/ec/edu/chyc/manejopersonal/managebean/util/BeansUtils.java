/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean.util;

import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.jbibtex.ParseException;
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
    
    public static String valorParam(String nombreParametro) {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        return params.get(nombreParametro);
    }
    
    /**
     * Devuelve true si el string contiene códigos latex
     * @param latexString La cadena de texto a comprobarse
     * @return true si contiene códigos latex, caso contrario false
     */
    public static boolean isLatexString(String latexString) {
        return (latexString.indexOf('\\') > -1 || latexString.indexOf('{') > -1);
    }
    
    /**
     * Transforma códigos latex a string en caso de existir, caso contrario devuelve la misma cadena
     * @param latexString Cadena de texto a transformar
     * @return La cadena con todos los códigos transformados a texto plano
     * @throws ParseException En caso de que no se pueda procesar algún código latex
     */
    public static String latexToString(String latexString) throws ParseException {
        if (isLatexString(latexString)) {
            // LaTeX string that needs to be translated to plain text string
            latexString = latexString.replace("–", "-");
            latexString = latexString.replace("−", "-");
            latexString = latexString.replace("≤", "<=");
            latexString = latexString.replace("≥", ">=");
         
            /*String transformed = "";
            for (int i = 0; i < latexString.length();i++) {
                if (latexString.charAt(i) > 255) {
                    transformed += "\\symbol{\""+Integer.toHexString((int)latexString.charAt(i)).toUpperCase()+"}";
                } else {
                    transformed += latexString.charAt(i);
                }
            }
            latexString = transformed;*/
            org.jbibtex.LaTeXParser latexParser = new org.jbibtex.LaTeXParser();

            List<org.jbibtex.LaTeXObject> latexObjects = latexParser.parse(latexString);

            org.jbibtex.LaTeXPrinter latexPrinter = new org.jbibtex.LaTeXPrinter();

            String plainTextString = latexPrinter.print(latexObjects); 
            return plainTextString;
        } else {
            return latexString;
            // Plain text string
        }
    }
    
    /**
     * Convierte org.jbibtex.Value a texto plano incluyendo una transformación de códigos latex a texto si es que tiene
     * @param value valor a comprobar, puede ser nulo
     * @return Si value es nulo, devuelve una cadena vacía, caso contrario si el valor de value contiene código latex, 
     * devuelve su transformación de texto plano.
     * @throws ParseException En caso de que no se pueda procesar algún código latex
     */
    public static String value2String(org.jbibtex.Value value) throws ParseException {
        if (value != null) {
            return latexToString(value.toUserString());
        } else {
            return "";
        }
    }
    
}
