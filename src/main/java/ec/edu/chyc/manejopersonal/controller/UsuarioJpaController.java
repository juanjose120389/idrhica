/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class UsuarioJpaController extends GenericJpaController<Usuario> implements Serializable {
    public UsuarioJpaController() {
        setClassRef(Usuario.class);
    }
    
    public void create(Usuario obj) throws Exception {
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
    
    public Usuario login(String username, String encPassword) {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            Query q = em.createQuery("select u from Usuario u where u.usuario=:usuario and u.password=:password");
            q.setParameter("usuario", username);
            q.setParameter("password", encPassword);
            
            List<Usuario> list = q.getResultList();
            Usuario usuario = null;
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            em.getTransaction().commit();
            return usuario;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario obj) throws Exception {
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
    
    public void destroy(Usuario obj) throws Exception {
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
