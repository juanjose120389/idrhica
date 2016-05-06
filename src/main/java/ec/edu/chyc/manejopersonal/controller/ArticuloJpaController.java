/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Financiamiento;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.PersonaArticulo;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Hibernate;

public class ArticuloJpaController extends GenericJpaController<Articulo> implements Serializable {

    public ArticuloJpaController() {
        setClassRef(Articulo.class);
    }

    public List<Articulo> listArticulos() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct a from Articulo a join fetch a.personasArticuloCollection");
            List<Articulo> list = q.getResultList();
            for (Articulo articulo : list) {
                Collection<Proyecto> listProyectos = articulo.getProyectosCollection();
                for (Proyecto proyecto : listProyectos) {
                    for (Financiamiento financiamiento : proyecto.getFinanciamientosCollection()) {
                        if (!articulo.getListaInstFinanciamientos().contains(financiamiento.getInstitucion())) {
                            articulo.getListaInstFinanciamientos().add(financiamiento.getInstitucion());
                        }
                    }
                }
            }
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void create(Articulo obj) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();

            em.persist(obj);

            Set<Proyecto> listaProyectos = obj.getProyectosCollection();
            for (Proyecto proyecto : listaProyectos) {
                Proyecto proyectoAttached = em.find(Proyecto.class, proyecto.getId());
                proyectoAttached.getArticulosCollection().add(obj);
            }
            
            /*for (Persona per : listaAutores) {
                //para cada una de los autores, agregar este artículo a su lista de artículos
                // no hace falta hacer un merge ya que lo detecta automáticamente
                Persona perAttached = em.find(Persona.class, per.getId());                
                perAttached.getArticulosCollection().add(obj);
            }*/
            if (!obj.getPersonasArticuloCollection().isEmpty()) {
                for (PersonaArticulo personaArticulo : obj.getPersonasArticuloCollection()) {
                    personaArticulo.setArticulo(obj);
                    Persona persona = personaArticulo.getPersona();

                    if (persona.getId() < 0 || persona.getId() == null) {
                        persona.setId(null);
                        em.persist(persona);
                    }

                    if (personaArticulo.getId() == null || personaArticulo.getId() < 0) {
                        personaArticulo.setId(null);
                        em.persist(personaArticulo);
                    }
                }
            }

            if (obj.getArchivoArticulo() != null && !obj.getArchivoArticulo().isEmpty()) {
                //si se subió el archivo, copiar del directorio de temporales al original de artículos, después eliminar el archivo temporal
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoArticulo());
                Path destino = ServerUtils.getPathArticulos().resolve(obj.getArchivoArticulo());

                Files.move(origen, destino, REPLACE_EXISTING);
                //FileUtils.moveFile(origen, destino);
            }

            //em.merge(obj);
            //em.persist(obj);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Articulo findArticulo(Long id) {
        return findArticulo(id, true, true);
    }

    public Articulo findArticulo(Long id, boolean incluirProyectos, boolean incluirAgradecimientos) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select a from Articulo a join fetch a.personasArticuloCollection where a.id = :id", Articulo.class);
            q.setParameter("id", id);
            Articulo articulo = (Articulo) q.getSingleResult();
            if (incluirProyectos) {
                Hibernate.initialize(articulo.getProyectosCollection());
            }
            if (incluirAgradecimientos) {
                Hibernate.initialize(articulo.getAgradecimientosCollection());
            }
            return articulo;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Articulo obj) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();

            Articulo articuloAntiguo = em.find(Articulo.class, obj.getId());

            
            Set<Proyecto> listaProyectos = obj.getProyectosCollection();
            Iterator<Proyecto> iterProyectoAnterior = articuloAntiguo.getProyectosCollection().iterator();
            while (iterProyectoAnterior.hasNext()) {
                //quitar los proyectos que ya no estan en el articulo editado
                Proyecto proy = iterProyectoAnterior.next();
                if (!listaProyectos.contains(proy)) {
                    iterProyectoAnterior.remove();
                    proy.getArticulosCollection().remove(articuloAntiguo);
                }
            }
            for (Proyecto proy : listaProyectos) {
                //agregar proyectos que han sido agregados en el articulo editado
                if (!articuloAntiguo.getProyectosCollection().contains(proy)){
                    Proyecto proyectoExistenteAsignado = em.find(Proyecto.class, proy.getId());
                    proyectoExistenteAsignado.getArticulosCollection().add(obj);
                }
            }
            
            Set<Institucion> listaAgradecimientos = obj.getAgradecimientosCollection();
            Iterator<Institucion> iterAgradecimientoAnterior = articuloAntiguo.getAgradecimientosCollection().iterator();
            while (iterAgradecimientoAnterior.hasNext()) {
                Institucion inst = iterAgradecimientoAnterior.next();
                if (!listaAgradecimientos.contains(inst)) {
                    iterAgradecimientoAnterior.remove();
                    inst.getArticulosCollection().remove(articuloAntiguo);
                }
            }
            for (Institucion inst : listaAgradecimientos) {
                if (!articuloAntiguo.getAgradecimientosCollection().contains(inst)) {
                    Institucion instExistenteAsignado = em.find(Institucion.class, inst.getId());
                    instExistenteAsignado.getArticulosCollection().add(obj);
                }
            }

            for (PersonaArticulo pa : articuloAntiguo.getPersonasArticuloCollection()) {
                //quitar PersonaArticulo que no existan en el nuevo articulo editado
                if (!obj.getPersonasArticuloCollection().contains(pa)) {
                    em.remove(pa);
                }
            }
            
            for (PersonaArticulo perArt : obj.getPersonasArticuloCollection()) {
                //si hay ids menores a uno, significa que son nuevos y deben ser puestos a null para que sean creados
                if (perArt.getId() != null && perArt.getId() < 0) {
                    perArt.setId(null);                    
                }
                if (perArt.getArticulo() == null) {
                    perArt.setArticulo(obj);
                }
                em.merge(perArt);
            }
            /*           
            Set<Persona> listaAutores = obj.getAutoresCollection();
            
            Iterator<Persona> iterPersonaAnterior = articuloAntiguo.getAutoresCollection().iterator();
            while (iterPersonaAnterior.hasNext()) {
                //para eliminar las personas que ya no están en el artículo editado actual
                Persona per = em.find(Persona.class,iterPersonaAnterior.next().getId());
                if (!listaAutores.contains(per)) {
                    iterPersonaAnterior.remove();
                    per.getArticulosCollection().remove(articuloAntiguo);
                    //em.merge(per);
                }
            }            
            
            for (Persona per : listaAutores) {
                //para agregar las personas que no están en el árticulo original sin editar
                if (!articuloAntiguo.getAutoresCollection().contains(per)) {
                    Persona autorExistenteAsignado = em.find(Persona.class, per.getId());
                    autorExistenteAsignado.getArticulosCollection().add(obj);                    
                }
            }
             */
            String archivoAntiguo = articuloAntiguo.getArchivoArticulo();
            String archivoNuevo = obj.getArchivoArticulo();

            //si no se ha cambiado el nombre del archivo, es porque el archivo subido no se ha modificado            
            boolean archivoSeMantiene = false;
            if (archivoAntiguo != null && !archivoAntiguo.isEmpty()
                    && archivoNuevo != null && !archivoNuevo.isEmpty()
                    && archivoAntiguo.equals(archivoNuevo)) {
                archivoSeMantiene = true;
            }

            if (archivoAntiguo != null && !archivoAntiguo.isEmpty() && !archivoSeMantiene) {
                //en caso de que existió un archivo almacenado anteriormente, se elimina, pero solo en caso de que se haya modificado el archivo original
                Path pathAntiguo = ServerUtils.getPathArticulos().resolve(archivoAntiguo);
                if (Files.exists(pathAntiguo)) {
                    Path pathNuevoDestino = ServerUtils.getPathTemp().resolve("eliminado_" + archivoAntiguo);
                    Files.move(pathAntiguo, pathNuevoDestino, REPLACE_EXISTING);
                }
            }

            if (archivoNuevo != null && !archivoNuevo.isEmpty() && !archivoSeMantiene) {
                //si se subió el archivo, copiar del directorio de temporales al original de artículos, después eliminar el archivo temporal
                // solo realizarlo si se modificó el archivo original
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoArticulo());
                Path destino = ServerUtils.getPathArticulos().resolve(obj.getArchivoArticulo());

                //FileUtils.copyFile(origen, destino);
                Files.move(origen, destino, REPLACE_EXISTING);
            }

            em.merge(obj);
            //em.persist(obj);

            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    public void destroy(Long id) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.remove(id);
            em.getTransaction().commit();

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
