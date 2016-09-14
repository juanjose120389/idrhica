/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Convenio;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Hibernate;


public class ConvenioJpaController extends GenericJpaController<Convenio> implements Serializable {
    public ConvenioJpaController() {
        setClassRef(Convenio.class);
    }

    public Convenio findConvenio(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct c from Convenio c left join fetch c.proyectosCollection where c.id=:id");
            q.setParameter("id", id);
            Convenio convenio = (Convenio)q.getSingleResult();
            
            for (Proyecto proyecto : convenio.getProyectosCollection()) {
                for (Articulo articulo : proyecto.getArticulosCollection()) {
                    if (!convenio.getListaArticulos().contains(articulo)) {
                        convenio.getListaArticulos().add(articulo);
                    }
                    
                }
                for (Tesis tesis : proyecto.getTesisCollection()) {
                    if (!convenio.getListaTesis().contains(tesis)) {
                        convenio.getListaTesis().add(tesis);
                    }
                    Hibernate.initialize(tesis.getAutoresCollection());
                }
                
                //Hibernate.initialize(proyecto.getArticulosCollection());
                //Hibernate.initialize(proyecto.getTesisCollection());
            }
            
            return convenio;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    
    public List<Convenio> listConvenio() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct c from Convenio c left join fetch c.proyectosCollection order by c.fechaFin desc, c.titulo asc");
            List<Convenio> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    
    public void create(Convenio obj) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            if (obj.getInstitucion() != null) {
                if (obj.getInstitucion().getId() == null || obj.getInstitucion().getId() < 0) {
                    obj.getInstitucion().setId(null);
                    em.persist(obj.getInstitucion());
                }
            }
            
            em.persist(obj);
            

            //guardar la relación proyecto - convenio
            for (Proyecto proy : obj.getProyectosCollection()) {
                Proyecto proyAttached = em.find(Proyecto.class, proy.getId());
                proyAttached.getConveniosCollection().add(obj);
            }
            
            if (!obj.getArchivoConvenio().isEmpty()) {
                //si se subió el archivo, copiar del directorio de temporales al original destino, después eliminar el archivo temporal
                Path origen = ServerUtils.getPathTemp().resolve(obj.getArchivoConvenio());
                Path destino = ServerUtils.getPathConvenios().resolve(obj.getArchivoConvenio());

                Files.move(origen, destino, REPLACE_EXISTING);
            }            
            
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Convenio convenio) throws Exception {
        EntityManager em = null;
        try {            
            
            em = getEntityManager();
            em.getTransaction().begin();
            
            Convenio convenioAntiguo = em.find(Convenio.class, convenio.getId());
            
            if (convenio.getInstitucion() != null) {
                if (convenio.getInstitucion().getId() == null || convenio.getInstitucion().getId() < 0) {
                    convenio.getInstitucion().setId(null);
                    em.persist(convenio.getInstitucion());
                }
            }
            
            Set<Proyecto> listaProyectos = convenio.getProyectosCollection();
            Iterator<Proyecto> iterProyectosAnteriores = convenioAntiguo.getProyectosCollection().iterator();
            while (iterProyectosAnteriores.hasNext()) {
                //quitar los proyectos que ya no estan en el convenio editado
                Proyecto proy = iterProyectosAnteriores.next();
                if (!listaProyectos.contains(proy)) {
                    iterProyectosAnteriores.remove();
                    proy.getConveniosCollection().remove(convenio);
                }
            }
            for (Proyecto proy : listaProyectos) {
                //agregar proyectos que han sido agregados en el convenio editado
                if (!convenioAntiguo.getProyectosCollection().contains(proy)){
                    Proyecto proyExistenteAttached = em.find(Proyecto.class, proy.getId());
                    proyExistenteAttached.getConveniosCollection().add(convenio);
                }
            }
            
            
            String archivoAntiguo = convenioAntiguo.getArchivoConvenio();
            String archivoNuevo = convenio.getArchivoConvenio();
            
            ServerUtils.procesarAntiguoNuevoArchivo(archivoAntiguo, archivoNuevo, ServerUtils.getPathConvenios());
            
            em.merge(convenio);
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
