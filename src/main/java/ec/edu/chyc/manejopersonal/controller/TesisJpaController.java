/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Hibernate;


public class TesisJpaController extends GenericJpaController<Tesis> implements Serializable {
    public TesisJpaController() {
        setClassRef(Tesis.class);
    }

    public Tesis findTesis(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select t from Tesis t join fetch t.autoresCollection where t.id=:id");
            q.setParameter("id", id);
            Tesis tesis = (Tesis)q.getSingleResult();
            
            Hibernate.initialize(tesis.getProyectosCollection());
            Hibernate.initialize(tesis.getCodirectoresCollection());
            Hibernate.initialize(tesis.getTutoresCollection());
            Hibernate.initialize(tesis.getAutoresCollection());
            
            return tesis;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<Tesis> listTesis() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            //Query q = em.createQuery("select distinct t from Tesis t join fetch t.autoresCollection");
            Query q = em.createQuery("select distinct t from Tesis t join fetch t.autoresCollection  left join fetch t.codirectoresCollection left join fetch t.tutoresCollection left join fetch t.proyectosCollection");
            //Query q = em.createQuery("select  t from Persona t");
            //Query q = em.createQuery("select  t from Tesis t");
            List<Tesis> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    
    public void create(Tesis obj) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(obj);
            
            for (Persona perAutor : obj.getAutoresCollection()) {
                Persona perAutorAttached = em.find(Persona.class, perAutor.getId());
                perAutorAttached.getTesisCollection().add(obj);
            }
            
            for (Persona perCodirector : obj.getCodirectoresCollection()) {
                Persona perCodirectorAttached = em.find(Persona.class, perCodirector.getId());
                perCodirectorAttached.getTesisComoCodirectorCollection().add(obj);
            }
            
            for (Persona perTutor : obj.getTutoresCollection()) {
                Persona perTutorAttached = em.find(Persona.class, perTutor.getId());
                perTutorAttached.getTesisComoTutorCollection().add(obj);
            }            
            
            for (Proyecto proy : obj.getProyectosCollection()) {
                Proyecto proyAttached = em.find(Proyecto.class, proy.getId());
                proyAttached.getTesisCollection().add(obj);
            }
            
            if (!obj.getArchivoActaAprobacion().isEmpty()) {
                //si se subió el archivo, copiar del directorio de temporales al original destino, después eliminar el archivo temporal
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoActaAprobacion());
                Path destino = ServerUtils.getPathActasAprobacionTesis().resolve(obj.getArchivoActaAprobacion());

                Files.move(origen, destino, REPLACE_EXISTING);
            }
                        
            
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tesis tesis) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            Tesis tesisAntigua = em.find(Tesis.class,tesis.getId());
            
            Set<Proyecto> listaProyectos = tesis.getProyectosCollection();
            Iterator<Proyecto> iterProyectosAnteriores = tesisAntigua.getProyectosCollection().iterator();
            while (iterProyectosAnteriores.hasNext()) {
                //quitar los proyectos que ya no estan en la tesis editada
                Proyecto proy = iterProyectosAnteriores.next();
                if (!listaProyectos.contains(proy)) {
                    iterProyectosAnteriores.remove();
                    proy.getTesisCollection().remove(tesis);
                }
            }
            for (Proyecto proy : listaProyectos) {
                //agregar proyectos que han sido agregados en la tesis editada
                if (!tesisAntigua.getProyectosCollection().contains(proy)){
                    Proyecto proyExistenteAttached = em.find(Proyecto.class, proy.getId());
                    proyExistenteAttached.getTesisCollection().add(tesis);
                }
            }
            


            Set<Persona> listaAutores = tesis.getAutoresCollection();
            Iterator<Persona> iterAutoresAnteriores = tesisAntigua.getAutoresCollection().iterator();
            while (iterProyectosAnteriores.hasNext()) {
                //quitar los proyectos que ya no estan en la tesis editada
                Persona per = iterAutoresAnteriores.next();
                if (!listaAutores.contains(per)) {
                    iterAutoresAnteriores.remove();
                    per.getTesisCollection().remove(tesis);
                }
            }
            for (Persona per : listaAutores) {
                //agregar proyectos que han sido agregados en la tesis editada
                if (!tesisAntigua.getAutoresCollection().contains(per)){
                    Persona perExistenteAttached = em.find(Persona.class, per.getId());
                    perExistenteAttached.getTesisCollection().add(tesis);
                }
            }
            


            Set<Persona> listaCodirectores = tesis.getCodirectoresCollection();
            Iterator<Persona> iterCodirectoresAnteriores = tesisAntigua.getCodirectoresCollection().iterator();
            while (iterCodirectoresAnteriores.hasNext()) {
                //quitar los proyectos que ya no estan en la tesis editada
                Persona per = iterCodirectoresAnteriores.next();
                if (!listaCodirectores.contains(per)) {
                    iterCodirectoresAnteriores.remove();
                    per.getTesisComoCodirectorCollection().remove(tesis);
                }
            }
            for (Persona per : listaCodirectores) {
                //agregar proyectos que han sido agregados en la tesis editada
                if (!tesisAntigua.getCodirectoresCollection().contains(per)){
                    Persona perExistenteAttached = em.find(Persona.class, per.getId());
                    perExistenteAttached.getTesisComoCodirectorCollection().add(tesis);
                }
            }
            



            Set<Persona> listaTutores = tesis.getTutoresCollection();
            Iterator<Persona> iterTutoresAnteriores = tesisAntigua.getTutoresCollection().iterator();
            while (iterTutoresAnteriores.hasNext()) {
                //quitar los proyectos que ya no estan en la tesis editada
                Persona per = iterTutoresAnteriores.next();
                if (!listaTutores.contains(per)) {
                    iterTutoresAnteriores.remove();
                    per.getTesisComoTutorCollection().remove(tesis);
                }
            }
            for (Persona per : listaTutores) {
                //agregar proyectos que han sido agregados en la tesis editada
                if (!tesisAntigua.getTutoresCollection().contains(per)){
                    Persona perExistenteAttached = em.find(Persona.class, per.getId());
                    perExistenteAttached.getTesisComoTutorCollection().add(tesis);
                }
            }            

            
            String archivoAntiguo = tesisAntigua.getArchivoActaAprobacion();
            String archivoNuevo = tesis.getArchivoActaAprobacion();
            
            ServerUtils.procesarAntiguoNuevoArchivo(archivoAntiguo, archivoNuevo, ServerUtils.getPathActasAprobacionTesis());            
            
            em.merge(tesis);
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
