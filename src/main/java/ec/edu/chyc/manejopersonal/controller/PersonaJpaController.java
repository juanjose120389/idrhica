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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.hibernate.Hibernate;


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
    
    public List<Persona> listPersonasConContrato() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select p from Persona p where p.contratosCollection is not empty");
            List<Persona> list = q.getResultList();
            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }        
    }
    public Persona findPersona(Long id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select p from Persona p left join fetch p.personaTitulosCollection where p.id=:id");
            q.setParameter("id", id);
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

    public Persona findPersona(Long id, boolean incluirArticulos, boolean incluirContratos, boolean incluirTitulos, boolean incluirTesis, boolean incluirPasantias) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            
            String incluir = "";
            ArrayList<String> listaFetchs = new ArrayList<>();
            
            int totalFetchs = 0;
            if (incluirContratos) {
                listaFetchs.add(" left join fetch p.contratosCollection ");
                totalFetchs++;
            }            
            if (incluirArticulos) {
                listaFetchs.add(" left join fetch p.personaArticulosCollection ");
                totalFetchs++;
            }            
            if (incluirTitulos) {
                listaFetchs.add(" left join fetch p.personaTitulosCollection ");
                totalFetchs++;
            }
            if (incluirTesis) {
                listaFetchs.add(" left join fetch p.tesisCollection ");
                totalFetchs++;                
            }
            if (incluirPasantias) {
                listaFetchs.add(" left join fetch p.tesisCollection ");
                totalFetchs++;                
            }
            
            if (listaFetchs.size() > 1) {
                incluir = listaFetchs.get(0);
            }
            
            TypedQuery<Persona> q = em.createQuery("select p from Persona p " + incluir + " where p.id=:id",Persona.class);            
            q.setParameter("id", id);
            Persona p = q.getSingleResult();
            
            if (incluirContratos) {
                Hibernate.initialize(p.getContratosCollection());
            }
            if (incluirArticulos) {
                Hibernate.initialize(p.getPersonaArticulosCollection());
            }
            if (incluirTitulos) {
                Hibernate.initialize(p.getPersonaTitulosCollection());
            }
            if (incluirTesis) {
                Hibernate.initialize(p.getTesisCollection());
            }
            if (incluirPasantias) {
                Hibernate.initialize(p.getPasantiasCollection());
            }
            
            return p;
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
            
            Persona personaAntigua = em.find(Persona.class, persona.getId());
            
            //quitar los personaTitulo que no existan en la nueva persona editada
            for (PersonaTitulo perTituloAntiguo : personaAntigua.getPersonaTitulosCollection()) {
                if (!persona.getPersonaTitulosCollection().contains(perTituloAntiguo)) {
                    em.remove(perTituloAntiguo);
                }
            }
            //poner en null los ids negativos para que se puedan crear
            for (PersonaTitulo perTitulo : persona.getPersonaTitulosCollection()) {
                if (perTitulo.getId() != null && perTitulo.getId() < 0) {
                    perTitulo.setId(null);
                }
                if (perTitulo.getPersona() == null) {
                    perTitulo.setPersona(persona);
                }
                em.merge(perTitulo);
            }
            
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
