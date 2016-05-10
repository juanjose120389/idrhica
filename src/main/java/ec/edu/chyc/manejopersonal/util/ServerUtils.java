/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Base64;
import java.util.Random;
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
     *
     * @return Path donde se almacenan los contratos
     */
    public static Path getPathContratos() {
        return getDocRootPath().resolve("contratos");
    }
    
    /***
     * 
     * @return Path donde se almacenan las actas de aprobación de las tesis
     */
    public static Path getPathActasAprobacionTesis() {
        return getDocRootPath().resolve("actasAprobacionTesis");
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
    /***
     *
     * @return Ruta del docroot de glassfish
     */
    public static Path getDocRootPath() {
        return Paths.get("").toAbsolutePath().getParent().resolve("docroot").normalize();
    }

    /***
     *
     * @return Ruta en formato String del docroot de gflassfish
     */
    public static String getDocRoot() {
        return getDocRootPath().normalize().toString();
    }

    /***
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
     * Muestra el tamaño de un archivo en una forma amigable (B, KB, MB, GB, TB,
     * PB, EB. Obtenido y modificado para quitar los formatos con "i" (KiB, MiB,
     * KiB, etc):
     * https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     *
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

    /***
     * Reemplaza caracteres inválidos de nombre de archivo a un nombre válido
     * Obtenido y modificado para que sea una función independiente e incluir
     * acentos como válidos y reemplazar el espacio por _:
     * http://grepcode.com/file/repository.springsource.com/org.apache.activemq/com.springsource.org.apache.kahadb/5.3.0/org/apache/kahadb/util/IOHelper.java
     *
     * @param name Nombre del archivo a convertir.
     * @param dirSeparators Si es true, mantiene los separadores de path ("/" ó
     * "\"), si es false se reemplazan.
     * @param maxFileLength Máxima cantidad de caracteres, si el nombre generado
     * es mayor a esta cantidad, será truncado.
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

    /***
     * Convierte el nombre a un nombre de archivo válido (reemplazando caracteres inválidos por válidos)
     * @param nombreOriginal Nombre del archivo que se quiere transformar
     * @return Nombre de archivo transformado y válido
     */
    public static String convertirNombreArchivo(String nombreOriginal) {
        return toFileSystemSafeName(nombreOriginal, false, 255);
    }
    
    public static String convertirNombreArchivo(String nombreOriginal, String extension, int caracteres) {
        String nombre = nombreOriginal;
        if (nombre.length() - extension.length() - 1 > caracteres) {
            nombre = nombre.substring(0, caracteres);
        }
        nombre = ServerUtils.convertirNombreArchivo(nombre + "." + extension);
        return nombre;
    }
    
    /**
     * Mueve los archivos subidos de acuerdo al nombre del archivo que ha tenido antes y ahora. Si son diferentes,
     * entonces se mueve el anterior y se lo trata como "eliminado", posteriormente si el nombre del archivo nuevo
     * existe, se mueve del temporal al directorio destino. Si los dos nombres de archivos son iguales, no se realiza ninguna acción.
     * @param nombreArchivoAntiguo Nombre del archivo antiguo ya almacenado y que se debe encontrar en pathDestino, 
     * se lo tratará como eliminado (llevará el prefijo 'eliminado_' si este nombre es diferente al nombreArchivoNuevo.
     * @param nombreArchivoNuevo Nombre del archivo nuevo que se encuentra en el Path Temporal. Si el nombre no está vacío,
     * se moverá el archivo del directorio temporal al pathDestino.
     * @param pathDestino Ruta destino donde se almacenará el nombreArchivoNuevo.
     * @throws IOException En caso de que no se pueda mover ya sea el archivo antiguo o el archivo nuevo.
     */
    public static void procesarAntiguoNuevoArchivo(String nombreArchivoAntiguo, String nombreArchivoNuevo, Path pathDestino) throws IOException {

        //si no se ha cambiado el nombre del archivo, es porque el archivo subido no se ha modificado            
        boolean archivoSeMantiene = false;
        if (!nombreArchivoAntiguo.isEmpty() && nombreArchivoAntiguo.equals(nombreArchivoNuevo)) {
            archivoSeMantiene = true;
        }

        if (!nombreArchivoAntiguo.isEmpty() && !archivoSeMantiene) {
            //en caso de que existió un archivo almacenado anteriormente, se elimina, pero solo en caso de que se haya modificado el archivo original
            Path pathArchivoAntiguo = pathDestino.resolve(nombreArchivoAntiguo).normalize();
            if (Files.exists(pathArchivoAntiguo) && Files.isRegularFile(pathArchivoAntiguo)) {
                Path pathNuevoDestino = ServerUtils.getPathTemp().resolve("eliminado_" + nombreArchivoAntiguo);
                Files.move(pathArchivoAntiguo, pathNuevoDestino, REPLACE_EXISTING);
            }
        }

        if (!nombreArchivoNuevo.isEmpty() && !archivoSeMantiene) {
            //si se subió el nuevo archivo, copiar del directorio de temporales al original de destino, después eliminar el archivo temporal
            // solo realizarlo si se modificó el archivo original
            Path origen = ServerUtils.getPathTemp().resolve(nombreArchivoNuevo);
            Path destino = pathDestino.resolve(nombreArchivoNuevo).normalize();

            //FileUtils.copyFile(origen, destino);
            Files.move(origen, destino, REPLACE_EXISTING);
        }
    }
    
    public static String generarNombreValidoArchivo(String extension) {
        String nombreArchivo = ServerUtils.generateB64Uuid().replace("=", "") + ((new Random()).nextInt(8999)+1000) + "." + extension;
        return ServerUtils.convertirNombreArchivo(nombreArchivo);
    }
    
}
