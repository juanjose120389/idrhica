/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Financiamiento;
import ec.edu.chyc.manejopersonal.entity.Firma;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.entity.Lugar;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.PersonaArticulo;
import ec.edu.chyc.manejopersonal.entity.PersonaFirma;
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
            Query q = em.createQuery("select distinct a from Articulo a join fetch a.personasArticuloCollection left join a.proyectosCollection p left join p.lugaresCollection order by a.anioPublicacion desc, a.nombre asc");
            List<Articulo> list = q.getResultList();
            for (Articulo articulo : list) {                
                Collection<Proyecto> listProyectos = articulo.getProyectosCollection();
                for (Proyecto proyecto : listProyectos) {
                    for (Financiamiento financiamiento : proyecto.getFinanciamientosCollection()) {
                        if (!articulo.getListaInstFinanciamientos().contains(financiamiento.getInstitucion())) {
                            articulo.getListaInstFinanciamientos().add(financiamiento.getInstitucion());
                        }
                    }
                    for (Lugar lugar : proyecto.getLugaresCollection()) {
                        if (!articulo.getListaLugares().contains(lugar)) {
                            articulo.getListaLugares().add(lugar);
                        }
                    }
                }
                for (PersonaArticulo personaArticulo : articulo.getPersonasArticuloCollection()) {
                    personaArticulo.setPersona(personaArticulo.getPersonaFirma().getPersona());
                    personaArticulo.setFirma(personaArticulo.getPersonaFirma().getFirma());
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
            
            if (!obj.getPersonasArticuloCollection().isEmpty()) {
                for (PersonaArticulo personaArticulo : obj.getPersonasArticuloCollection()) {
                    personaArticulo.setArticulo(obj);
                    
                    PersonaFirma personaFirma = personaArticulo.getPersonaFirma();
                    Persona persona = personaFirma.getPersona();
                    Firma firma = personaFirma.getFirma();
                    
                    if (firma.getId() == null || firma.getId() < 0) {
                        firma.setId(null);
                        em.persist(firma);
                    }
                    if (personaFirma.getId() == null || personaFirma.getId() < 0) {
                        personaFirma.setId(null);
                        em.persist(personaFirma);
                    }
                    /*
                    Persona persona = personaArticulo.getPersona();
                    if (persona.getId() < 0 || persona.getId() == null) {
                        persona.setId(null);
                        em.persist(persona);
                    }*/

                    if (personaArticulo.getId() == null || personaArticulo.getId() < 0) {
                        personaArticulo.setId(null);
                        em.persist(personaArticulo);
                    }
                }
            }

            if (obj.getArchivoArticulo() != null 
                    && !obj.getArchivoArticulo().isEmpty()) {
                //si se subió el archivo, mover del directorio de temporales al original de artículos
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoArticulo());
                String nuevoNombre = "id" + obj.getId() + "_" + obj.getArchivoArticulo();
                Path destino = ServerUtils.getPathArticulos().resolve(nuevoNombre);

                Files.move(origen, destino, REPLACE_EXISTING);
                //FileUtils.moveFile(origen, destino);
            }
            
            if (obj.getArchivoBibtex() != null && !obj.getArchivoBibtex().isEmpty()) {
                //si se subió el archivo, mover del directorio de temporales al original de artículos
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoBibtex());
                String nuevoNombre = "id" + obj.getId() + "_" + obj.getArchivoBibtex();
                Path destino = ServerUtils.getPathArticulos().resolve(nuevoNombre);

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
            Query q = em.createQuery("select a from Articulo a left join fetch a.personasArticuloCollection where a.id = :id", Articulo.class);
            q.setParameter("id", id);
            Articulo articulo = (Articulo) q.getSingleResult();
            if (incluirProyectos) {
                Hibernate.initialize(articulo.getProyectosCollection());
            }
            if (incluirAgradecimientos) {
                Hibernate.initialize(articulo.getAgradecimientosCollection());
            }
            for (PersonaArticulo perArticulo : articulo.getPersonasArticuloCollection()) {//cargar todas las firmas incluidas en el artículo
                Hibernate.initialize(perArticulo.getPersonaFirma());
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
                //agregar nuevas firmas que surjan en el artículo editado
                PersonaFirma personaFirma = perArt.getPersonaFirma();
                Firma firma = personaFirma.getFirma();
                
                if (firma.getId() == null || firma.getId() < 0) {
                    firma.setId(null);
                    em.persist(firma);
                }
                if (personaFirma.getId() == null || personaFirma.getId() < 0) {
                    personaFirma.setId(null);
                    em.persist(personaFirma);
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
            String archivoAntiguoArticulo = articuloAntiguo.getArchivoArticulo();
            String archivoNuevoArticulo = obj.getArchivoArticulo();
            ServerUtils.procesarAntiguoNuevoArchivo(archivoAntiguoArticulo, archivoNuevoArticulo, ServerUtils.getPathArticulos());
            
            String archivoAntiguoBibtex = articuloAntiguo.getArchivoBibtex();
            String archivoNuevoBibtex = obj.getArchivoBibtex();
            ServerUtils.procesarAntiguoNuevoArchivo(archivoAntiguoBibtex, archivoNuevoBibtex, ServerUtils.getPathArticulos());
            

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
