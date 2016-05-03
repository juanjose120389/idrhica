/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.Tesis;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class TesisJpaController extends GenericJpaController<Tesis> implements Serializable {
    public TesisJpaController() {
        setClassRef(Tesis.class);
    }

    public List<Tesis> listTesis() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            //Query q = em.createQuery("select distinct t from Tesis t join fetch t.autoresCollection");
            Query q = em.createQuery("select distinct t from Tesis t join fetch t.autoresCollection");
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
            
            for (Persona t : obj.getAutoresCollection()) {
                Persona per = em.find(Persona.class, t.getId());
                per.getTesisCollection().add(obj);
                //tes.setTesis(obj);
                //em.merge(per);
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
