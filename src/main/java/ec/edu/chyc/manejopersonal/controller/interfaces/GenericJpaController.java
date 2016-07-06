/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller.interfaces;

import ec.edu.chyc.manejopersonal.controller.util.EntityManagerUtil;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * *
 *
 * @param <T> Tipo de clase que manejará el controlador
 */
public class GenericJpaController<T> implements Serializable {

    /**
     * *
     * La clase que manera el controlador.
     */
    private Class<?> classRef = null;

    public GenericJpaController() {

    }

    public EntityManager getEntityManager() {
        if (classRef == null || classRef.equals(GenericJpaController.class)) {
            System.err.println("*****************************************************");
            System.err.println("ERROR: No se ha especificado una clase de referencia.");
            System.err.println("*****************************************************");

        }

        return EntityManagerUtil.get().createEntityManager();
    }

    public int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<T> rt = cq.from(classRef);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            Long result = (Long) q.getSingleResult();

            em.getTransaction().commit();

            int resultInt = result.intValue();
            return resultInt;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public T findEntity(Object id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            T result = (T) em.find(classRef, id);
            em.getTransaction().commit();
            return result;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    protected List<T> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(classRef));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            List list = q.getResultList();
            em.getTransaction().commit();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public Class<?> getClassRef() {
        return classRef;
    }

    public void setClassRef(Class<?> classRef) {
        this.classRef = classRef;
    }
}
