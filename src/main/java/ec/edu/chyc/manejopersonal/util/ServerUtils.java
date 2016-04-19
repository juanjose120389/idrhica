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

    /***
     * Tamaño de buffer, utilizado para descarga o subida de archivos
     */
    public static final int BUFFER_SIZE = 6124;
    /***
     * 
     * @return Path donde se almacenan los artículos
     */
    public static Path getPathArticulos() {
        return getDocRootPath().resolve("articulos");
    }
    /***
     * Devuelve el path para archivos temporales
     * @return Path de archivos temporales
     */
    public static Path getPathTemp() {
        return getDocRootPath().resolve("tmp");
    }
    
    /***
     * 
     * @return Ruta del docroot de glassfish
     */
    public static Path getDocRootPath() {
        return Paths.get("").toAbsolutePath().getParent().resolve("docroot").normalize();
    }

    /***
     * 
     * @return Ruta en formato String del docroot de flassfish
     */
    public static String getDocRoot() {
        return getDocRootPath().normalize().toString();
    }

    /***
     * Devuelve un UUID generado y codificado en Base64
     * @return Uuid codificado en Base64
     */
    public static String generateB64Uuid() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer uuidBytes = ByteBuffer.wrap(new byte[16]);
        uuidBytes.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        String asB64 = Base64.getEncoder().encodeToString(uuidBytes.array());
        return asB64;
    }

    //modificado de https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    //muestra el formato de base 2 del tamaño de un archivo, pero sin las "i" (KiB, MiB, etc.)
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
