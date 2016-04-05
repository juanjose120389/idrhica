/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Proyecto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class ProyectoJpaController extends GenericJpaController<Proyecto> implements Serializable {
    public ProyectoJpaController() {
        setClassRef(Proyecto.class);
    }

    public List<Proyecto> listProyecto() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select p from Proyecto p");
            List<Proyecto> list = q.getResultList();
            return list;
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
            em.persist(proyecto);
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
