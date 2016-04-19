/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.PersonaTitulo;
import ec.edu.chyc.manejopersonal.entity.Titulo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class PersonaJpaController extends GenericJpaController<Persona> implements Serializable {
    public PersonaJpaController() {
        setClassRef(Persona.class);
    }

    public List<Persona> listPersonas() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select p from Persona p");
            List<Persona> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    
    public Persona getPersona(Persona persona) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select p from Persona p where p.id=:id");
            q.setParameter("id", persona.getId());
            List<Persona> list = q.getResultList();
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            } else {
                return null;
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }    
    
    public void create(Persona persona/*, Contratado contratado, Tesista tesista, Pasante pasante*/) throws Exception {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(persona);
/*            
            if (contratado != null) {
                contratado.setId(persona.getId());
                em.persist(contratado);
            }
            if (tesista != null) {
                tesista.setId(persona.getId());
                em.persist(tesista);
            }
            if (pasante != null) {
                pasante.setId(persona.getId());
                em.persist(pasante);
            }
  */          
            if (persona.getPersonaTitulosCollection() != null) {
                for (PersonaTitulo personaTitulo : persona.getPersonaTitulosCollection()) {
                    personaTitulo.setPersona(persona);
                    Titulo titulo = personaTitulo.getTitulo();
                    if (titulo.getId() < 0 || titulo.getId() == null) {
                        titulo.setId(null);
                        em.persist(titulo);
                    }

                    if (personaTitulo.getUniversidad().getId() < 0 || personaTitulo.getUniversidad().getId() == null) {
                        personaTitulo.getUniversidad().setId(null);                        
                        em.persist(personaTitulo.getUniversidad());
                    }
                    
                    if (personaTitulo.getId() == null || personaTitulo.getId() < 0) {
                        personaTitulo.setId(null);
                        em.persist(personaTitulo);
                    }                    
                    
                }
            }

            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Persona persona) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(persona);
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
