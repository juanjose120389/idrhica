/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Tesista;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class TesistaJpaController extends GenericJpaController<Tesista> implements Serializable {
    public TesistaJpaController() {
        setClassRef(Tesista.class);
    }

    public List<Tesista> listTesista() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select p from Tesista p");
            List<Tesista> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    
    public void create(Tesista tesista) throws Exception {
        EntityManager em = null;
        

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(tesista);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tesista tesista) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(tesista);
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
