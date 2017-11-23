/*
 * To change this license header, choose License Headers 
 * in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Objeto;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class ObjetoJpaController
        extends GenericJpaController<Objeto>
        implements Serializable {
    
    public ObjetoJpaController() {
        setClassRef(Objeto.class);
    }
    
    public List<Objeto> listObjetos() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct a from Objeto a");
            List<Objeto> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void create(Objeto obj) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            if(obj.getProveedor()!=null 
                    && (obj.getProveedor().getId()==null
                    || obj.getProveedor().getId()<0)){
                obj.getProveedor().setId(null);
                em.persist(obj.getProveedor());
            }
            em.persist(obj);            
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public Objeto findObjeto(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select a from Objeto a  where a.id = :id", Objeto.class);
            q.setParameter("id", id);
            Objeto objeto = (Objeto) q.getSingleResult();
            return objeto;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void edit(Objeto obj) throws Exception {
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
