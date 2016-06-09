/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Convenio;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class ConvenioJpaController extends GenericJpaController<Convenio> implements Serializable {
    public ConvenioJpaController() {
        setClassRef(Convenio.class);
    }

    public List<Convenio> listConvenio() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select c from Convenio c");
            List<Convenio> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    
    public void create(Convenio convenio) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            if (convenio.getInstitucion() != null) {
                if (convenio.getInstitucion().getId() == null || convenio.getInstitucion().getId() < 0) {
                    convenio.getInstitucion().setId(null);
                    em.persist(convenio.getInstitucion());
                }
            }
            em.persist(convenio);
            
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
            if (convenio.getInstitucion() != null) {
                if (convenio.getInstitucion().getId() == null || convenio.getInstitucion().getId() < 0) {
                    convenio.getInstitucion().setId(null);
                    em.persist(convenio.getInstitucion());
                }
            }            
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
