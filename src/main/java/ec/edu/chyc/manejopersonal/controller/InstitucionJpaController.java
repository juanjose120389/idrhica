/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Convenio;
import ec.edu.chyc.manejopersonal.entity.Financiamiento;
import ec.edu.chyc.manejopersonal.entity.Institucion;
import ec.edu.chyc.manejopersonal.entity.Pasantia;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Hibernate;


public class InstitucionJpaController extends GenericJpaController<Institucion> implements Serializable {
    public InstitucionJpaController() {
        setClassRef(Institucion.class);
    }

    public Institucion findInstitucion(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct i from Institucion i left join fetch i.financiamientosCollection where i.id=:id");
            q.setParameter("id", id);
            Institucion institucion = (Institucion)q.getSingleResult();
            
            for (Financiamiento financiamiento : institucion.getFinanciamientosCollection()) {
                Proyecto proyecto = financiamiento.getProyecto();

                for (Articulo articulo : proyecto.getArticulosCollection()) {
                    if (!institucion.getArticulosList().contains(articulo)) {
                        institucion.getArticulosList().add(articulo);
                    }
                }
                for (Tesis tesis : proyecto.getTesisCollection()) {
                    if (!institucion.getTesisList().contains(tesis)) {
                        Hibernate.initialize(tesis.getAutoresCollection());
                        institucion.getTesisList().add(tesis);
                    }
                }
                for (Pasantia pasantia : proyecto.getPasantiasCollection()) {
                    if (!institucion.getPasantiasList().contains(pasantia)) {
                        institucion.getPasantiasList().add(pasantia);
                    }
                }

            }
            if (!institucion.getConveniosCollection().isEmpty()) {
                for (Convenio convenio : institucion.getConveniosCollection()) {
                    Hibernate.initialize(convenio.getProyectosCollection());                    
                }
            }
            
            //Hibernate.initialize(institucion.getConveniosCollection());            
            return institucion;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<Institucion> listInstituciones() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select i from Institucion i order by i.nombre asc");
            List<Institucion> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    public void create(Institucion obj) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(obj);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Institucion obj) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(obj);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }
    
    public void destroy(Institucion obj) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.remove(obj);
            em.getTransaction().commit();
                    
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }    

}
