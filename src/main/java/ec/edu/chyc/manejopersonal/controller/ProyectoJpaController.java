/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Financiamiento;
import ec.edu.chyc.manejopersonal.entity.Lugar;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Hibernate;

public class ProyectoJpaController extends GenericJpaController<Proyecto> implements Serializable {

    public ProyectoJpaController() {
        setClassRef(Proyecto.class);
    }

    public List<Lugar> listLugares() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct l from Lugar l order by l.nombre asc");
            List<Lugar> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<Proyecto> listProyecto() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct p from Proyecto p left join fetch p.financiamientosCollection order by p.fechaFin desc, p.titulo asc");
            List<Proyecto> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Proyecto findProyecto(Long id, boolean cargarFinanciamientos, boolean cargarConvenios, boolean cargarArticulos, boolean cargarTesis, boolean cargarPasantias) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            Query q = em.createQuery("select p from Proyecto p left join fetch p.contratosCollection where p.id=:id");
            q.setParameter("id", id);
            Proyecto p = (Proyecto) q.getSingleResult();
            
            if (cargarConvenios || cargarArticulos) {
                Hibernate.initialize(p.getConveniosCollection());

                if (cargarArticulos) {
                    Hibernate.initialize(p.getArticulosCollection());
                }
            }
            if (cargarFinanciamientos) {
                Hibernate.initialize(p.getFinanciamientosCollection());
            }
            if (cargarTesis){
                Hibernate.initialize(p.getTesisCollection());
                for (Tesis tesis : p.getTesisCollection()) {
                    Hibernate.initialize(tesis.getAutoresCollection());
                }
            }
            if (cargarPasantias) {
                Hibernate.initialize(p.getPasantiasCollection());
            }
            return p;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void create(Proyecto proyecto) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            if (proyecto.getLugar() != null) {
                if (proyecto.getLugar().getId() == null || proyecto.getLugar().getId() < 0) {
                    proyecto.getLugar().setId(null);
                    em.persist(proyecto.getLugar());
                }
            }
            
            em.persist(proyecto);

            for (Financiamiento finan : proyecto.getFinanciamientosCollection()) {                
                finan.setProyecto(proyecto);

                if (finan.getInstitucion().getId() == null || finan.getInstitucion().getId() < 0) {
                    //si el codigo de la institucion es negativa o nula, es una institucion nueva
                    finan.getInstitucion().setId(null);
                    em.persist(finan.getInstitucion());
                }
                
                if (finan.getId() == null || finan.getId() < 0) {
                    finan.setId(null);
                    em.persist(finan);
                }
            }

            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proyecto proyecto) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            //quitar los elementos que ya no existen
            Proyecto proyectoAntiguo = em.find(Proyecto.class, proyecto.getId());
            for (Financiamiento finan : proyectoAntiguo.getFinanciamientosCollection()) {
                if (!proyecto.getFinanciamientosCollection().contains(finan)) {
                    em.remove(finan);
                }
            }

            //agregar los nuevos elementos
            for (Financiamiento finan : proyecto.getFinanciamientosCollection()) {
                finan.setProyecto(proyecto);
                if (finan.getId() == null || finan.getId() < 0) {
                    finan.setId(null);                    
                }
                if (finan.getInstitucion().getId() == null || finan.getInstitucion().getId() < 0) {
                    //si el codigo de la institucion es negativa o nula, es una institucion nueva
                    finan.getInstitucion().setId(null);
                    em.persist(finan.getInstitucion());
                }
                
                em.merge(finan);
            }
            
            if (proyecto.getLugar() != null) {
                if (proyecto.getLugar().getId() == null || proyecto.getLugar().getId() < 0) {
                    proyecto.getLugar().setId(null);                    
                    em.persist(proyecto.getLugar());
                }
            }
            
            
            em.merge(proyecto);
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
