/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.util;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/**
 *
 * @author marcelocaj
 */
public class ServerUtils {

    /**
     * *
     * Tamaño de buffer, utilizado para descarga o subida de archivos
     */
    public static final int BUFFER_SIZE = 6124;

    /**
     * *
     *
     * @return Path donde se almacenan los artículos
     */
    public static Path getPathArticulos() {
        return getDocRootPath().resolve("articulos");
    }

    /**
     * *
     * Devuelve el path para archivos temporales
     *
     * @return Path de archivos temporales
     */
    public static Path getPathTemp() {
        return getDocRootPath().resolve("tmp");
    }

    /**
     * *
     *
     * @return Ruta del docroot de glassfish
     */
    public static Path getDocRootPath() {
        return Paths.get("").toAbsolutePath().getParent().resolve("docroot").normalize();
    }

    /**
     * *
     *
     * @return Ruta en formato String del docroot de flassfish
     */
    public static String getDocRoot() {
        return getDocRootPath().normalize().toString();
    }

    /**
     * *
     * Devuelve un UUID generado y codificado en Base64
     *
     * @return Uuid codificado en Base64
     */
    public static String generateB64Uuid() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer uuidBytes = ByteBuffer.wrap(new byte[16]);
        uuidBytes.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        String asB64 = Base64.getEncoder().encodeToString(uuidBytes.array());
        return asB64;
    }

    /***
     * Muestra el tamaño de un archivo en una forma amigable (B, KB, MB, GB, TB, PB, EB.
     * Obtenido y modificado para quitar los formatos con "i" (KiB, MiB, KiB, etc): 
     *  https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     * @param bytes Tamaño en bytes.
     * @return Texto indicando el tamaño en formato amigable.
     */
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String toFileSystemSafeName(String name) {
        return toFileSystemSafeName(name, true, 250);
    }
    /***
     * Reemplaza caracteres inválidos de nombre de archivo a un nombre válido
     * Obtenido y modificado para que sea una función independiente e incluir acentos como válidos y reemplazar el espacio por _:
     *  http://grepcode.com/file/repository.springsource.com/org.apache.activemq/com.springsource.org.apache.kahadb/5.3.0/org/apache/kahadb/util/IOHelper.java    
     * @param name Nombre del archivo a convertir.
     * @param dirSeparators Si es true, reemplaza también los separadores de path ("/" ó "\"), si es false se mantienen.
     * @param maxFileLength Máxima cantidad de caracteres, si el nombre generado es mayor a esta cantidad, será truncado.
     * @return Nombre convertido.
     */
    public static String toFileSystemSafeName(String name, boolean dirSeparators, int maxFileLength) {
        int size = name.length();
        StringBuilder rc = new StringBuilder(size * 2);
        for (int i = 0; i < size; i++) {
            char c = name.charAt(i);
            boolean valid = c >= 'a' && c <= 'z';
            valid = valid || (c >= 'A' && c <= 'Z');
            valid = valid || (c >= '0' && c <= '9');
            valid = valid || (c == '_') || (c == '-') || (c == '.') || (c == '#')
                    || (dirSeparators && ((c == '/') || (c == '\\')));

            int indexAccent = "áéíóúÁÉÍÓÚñÑüÜ".indexOf(c);
            
            if (valid) {
                rc.append(c);
            } else if (indexAccent >= 0) {
                String replacement = "aeiouAEOIOUnNuU";
                rc.append(replacement.charAt(indexAccent));
            } else if (c == ' ') {
                rc.append("_");
            } else {
                // Encode the character using hex notation
                rc.append('#');
                rc.append(Integer.toHexString(c));
            }
        }
        String result = rc.toString();
        if (result.length() > maxFileLength) {
            result = result.substring(result.length() - maxFileLength, result.length());
        }
        return result;
    }
    public static String convertirNombreArchivo(String nombreOriginal) {
        return toFileSystemSafeName(nombreOriginal, true, 255);
    }
}
