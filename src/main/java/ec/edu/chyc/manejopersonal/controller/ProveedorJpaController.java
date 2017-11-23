/*
 * To change this license header, choose License Headers 
 * in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.AcuerdoConfidencial;
import ec.edu.chyc.manejopersonal.entity.Objeto;
import ec.edu.chyc.manejopersonal.entity.Proveedor;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class ProveedorJpaController
        extends GenericJpaController<Proveedor>
        implements Serializable {
    
    public ProveedorJpaController() {
        setClassRef(Proveedor.class);
    }

    public List<Proveedor> listProveedores() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct a from Proveedor a");
            List<Proveedor> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void create(Proveedor obj) throws Exception {
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
    
    public Proveedor findObjeto(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select a from Proveedor a  where a.id = :id", Proveedor.class);
            q.setParameter("id", id);
            Proveedor proveedor = (Proveedor) q.getSingleResult();
            return proveedor;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void edit(Proveedor prov) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(prov);
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
