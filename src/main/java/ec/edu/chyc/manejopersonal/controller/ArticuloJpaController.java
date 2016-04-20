/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Articulo;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.managebean.GestorArticulo;
import ec.edu.chyc.manejopersonal.util.ServerUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.io.FileUtils;


public class ArticuloJpaController extends GenericJpaController<Articulo> implements Serializable {
    public ArticuloJpaController() {
        setClassRef(Articulo.class);
    }

    public List<Articulo> listArticulos() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct a from Articulo a join fetch a.autoresCollection");
            List<Articulo> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    
    public void create(Articulo obj) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            //em.persist(obj);
            
            Set<Persona> listaAutores = obj.getAutoresCollection();            
            //ArrayList<Persona> listaAutoresAttached = new ArrayList<>();
            //obj.setAutoresCollection(new HashSet<Persona>());
            em.persist(obj);
            
            for (Persona per : listaAutores) {
                Persona perAttached = em.find(Persona.class, per.getId());                
                perAttached.getArticulosCollection().add(obj);
                //listaAutoresAttached.add(perAttached);
                //obj.getAutoresCollection().add(perAttached);
                //em.merge(perAttached);
                //em.merge(obj);
            }
            //obj.setAutoresCollection(listaAutoresAttached);
            //obj.setAutoresCollection(listaAutores);            
            
            if (obj.getArchivoArticulo() != null && !obj.getArchivoArticulo().isEmpty()) {
                //si se subió el archivo, copiar del directorio de temporales al original de artículos, después eliminar el archivo temporal
                File origen = ServerUtils.getPathTemp().resolve(obj.getArchivoArticulo()).toFile();
                File destino = ServerUtils.getPathArticulos().resolve(obj.getArchivoArticulo()).toFile();

                //FileUtils.copyFile(origen, destino);
                FileUtils.moveFile(origen, destino);
            }
            
            //em.merge(obj);
            //em.persist(obj);
            
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Articulo articulo) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(articulo);
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
