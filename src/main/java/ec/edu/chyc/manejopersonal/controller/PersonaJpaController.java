/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.controller;

import ec.edu.chyc.manejopersonal.controller.interfaces.GenericJpaController;
import ec.edu.chyc.manejopersonal.entity.Firma;
import java.io.Serializable;
import ec.edu.chyc.manejopersonal.entity.Persona;
import ec.edu.chyc.manejopersonal.entity.PersonaArticulo;
import ec.edu.chyc.manejopersonal.entity.PersonaFirma;
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

    public PersonaFirma findPersonaFirma(Long idPersona, Long idFirma){
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct pf from PersonaFirma pf where pf.persona.id=:idPersona and pf.firma.id=:idFirma");
            q.setParameter("idPersona", idPersona);
            q.setParameter("idFirma", idFirma);
            
            List<PersonaFirma> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public Firma findFirma(Long idFirma) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct f from Firma f left join fetch f.personasFirmaCollection where f.id=:id");
            q.setParameter("id", idFirma);
            
            List<Firma> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public Persona obtenerPersonaVacia() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct p from Persona p left join fetch p.personaFirmasCollection where p.id=1");            
            List<Persona> list = q.getResultList();
            if (list != null && list.size() > 0) {
                Persona persona = list.get(0);
                Hibernate.initialize(persona.getPersonaFirmasCollection());
                
                return persona;
            }
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    
    public PersonaFirma findPersonaFirma(Long idPersona, String firma){
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct pf from PersonaFirma pf where pf.persona.id=:idPersona and pf.firma.nombre=:firma");
            q.setParameter("idPersona", idPersona);
            q.setParameter("firma", firma);
            
            List<PersonaFirma> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    
    public List<Persona> listPersonas() throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct p from Persona p left join fetch p.personaFirmasCollection where p.identificacion <> ''");
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
    
    public List<Persona> findPersonaConFirma(List<String> firmasAutores) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct p from Persona p join fetch p.personaFirmasCollection pf where pf.firma.nombre in :firmas");
            q.setParameter("firmas", firmasAutores);
            List<Persona> list = q.getResultList();

            return list;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<Firma> findFirmas(List<String> firmasAutores) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("select distinct f from Firma f left join fetch f.personasFirmaCollection where f.nombre in :firmas");
            q.setParameter("firmas", firmasAutores);
            List<Firma> list = q.getResultList();
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

    public Persona findPersona(Long id, boolean incluirArticulos, boolean incluirContratos, boolean incluirTitulos, boolean incluirTesis, boolean incluirPasantias, boolean incluirFirmas) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            
            String incluir = "";
            ArrayList<String> listaFetchs = new ArrayList<>();
            
            /*int totalFetchs = 0;
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
            if (incluirFirmas) {
                listaFetchs.add(" left join fetch p.personaFirmasCollection ");
                totalFetchs++;                
            }
            
            
            if (listaFetchs.size() > 1) {
                incluir = listaFetchs.get(0);
            }*/
            
            TypedQuery<Persona> q = em.createQuery("select p from Persona p " + incluir + " where p.id=:id",Persona.class);            
            q.setParameter("id", id);
            Persona p = q.getSingleResult();
            
            if (incluirContratos) {
                Hibernate.initialize(p.getContratosCollection());
            }
            if (incluirArticulos) {
                //Hibernate.initialize(p.getPersonaArticulosCollection());
                //Hibernate.initialize(p.getPersonaArticulosCollection());
                p.getListaPersonaArticulos().clear();
                for (PersonaFirma perFirma : p.getPersonaFirmasCollection()) {                    
                    for (PersonaArticulo perArticulo : perFirma.getPersonasArticulosCollection()) {
                        p.getListaPersonaArticulos().add(perArticulo);
                    }
                }
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
            if (incluirFirmas) {
                Hibernate.initialize(p.getPersonaFirmasCollection());
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
