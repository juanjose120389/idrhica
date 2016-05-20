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
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
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
    
    private Firma findFirma(EntityManager em, String strFirma) {
        Query q = em.createQuery("select distinct f from Firma f where f.nombre=:nombre");
        q.setParameter("nombre", strFirma);

        List<Firma> list = q.getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;        
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
            for (PersonaTitulo personaTitulo : persona.getPersonaTitulosCollection()) {
                personaTitulo.setPersona(persona);
                Titulo titulo = personaTitulo.getTitulo();
                if (titulo.getId() == null || titulo.getId() < 0) {
                    titulo.setId(null);
                    em.persist(titulo);
                }

                if (personaTitulo.getUniversidad().getId() == null || personaTitulo.getUniversidad().getId() < 0) {
                    personaTitulo.getUniversidad().setId(null);
                    em.persist(personaTitulo.getUniversidad());
                }

                if (personaTitulo.getId() == null || personaTitulo.getId() < 0) {
                    personaTitulo.setId(null);
                    em.persist(personaTitulo);
                }

            }
            /*for (PersonaFirma personaFirma : persona.getPersonaFirmasCollection()) {
                personaFirma.setPersona(persona);
                Firma firma = personaFirma.getFirma();                
                if (firma.getId() == null || firma.getId() < 0) {
                    firma.setId(null);
                    em.persist(firma);
                }
                if (personaFirma.getId() == null || personaFirma.getId() < 0) {
                    personaFirma.setId(null);
                    em.persist(personaFirma);
                }
            }*/
            guardarPersonaFirma(em, persona);

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
            Hibernate.initialize(personaAntigua.getPersonaFirmasCollection());
            
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
            
            for (PersonaFirma perFirmaAntiguo : personaAntigua.getPersonaFirmasCollection()) {
                //revisar las firmas que estén en el antigua persona pero ya no en el nuevo editado,
                // por lo tanto si ya no están en el nuevo editado, hay que borrar las relaciones PersonaFirma
                // pero solo si no tiene PersonaArticulo relacionado (significaría que esa firma está actualmente 
                // siendo usada en un artículo, por lo tanto no de debe borrar)
                boolean firmaEnEditado = false;

                //primero revisar si la firma que existia antes de la persona, existe en el nuevo editado
                for (PersonaFirma perFirma : persona.getPersonaFirmasCollection()) {
                    if (StringUtils.equalsIgnoreCase(perFirmaAntiguo.getFirma().getNombre(), perFirma.getFirma().getNombre())) {
                        firmaEnEditado = true;
                        break;
                    }
                }
                if (!firmaEnEditado) {
                    //si es que la firma de la persona sin editar ya no existe en la persona editada, quitar la relación PersonaFirma
                    
                    //primero verificar que la firma no sea usada en ninguna PersonaArticulo
                    if (perFirmaAntiguo.getPersonasArticulosCollection().isEmpty()) {                            
                        Firma firmaBorrar = null;                        
                        if (perFirmaAntiguo.getFirma().getPersonasFirmaCollection().size() == 1) {
                            //si la firma a borrar esta asignada a solo una persona (que sería la persona actual, variable: persona),
                            // colocarla a la variable para borrarla
                            //Siempre las firmas van a estar asignadas al menos a una persona, caso contrario deben ser borradas
                            //Las firmas de personas desconocidas están ligadas a una persona: la persona con id=0
                            firmaBorrar = perFirmaAntiguo.getFirma();
                        }
                        //borrar la relación PersonaFirma
                        em.remove(perFirmaAntiguo);
                        if (firmaBorrar != null) {
                            //borrar la firma solo si estaba asignada a una sola persona
                            firmaBorrar.getPersonasFirmaCollection().clear();
                            em.remove(firmaBorrar);
                        }
                    }
                }
                /*
                if (!persona.getPersonaFirmasCollection().contains(perFirmaAntiguo)) {
                    if (perFirmaAntiguo.getPersonasArticulosCollection().isEmpty()) {
                        em.remove(em);
                    }
                }*/
            }
            guardarPersonaFirma(em, persona);
            
            em.merge(persona);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }
    
    /**
     * Guarda las nuevas firmas de la persona, integrando las relaciones PersonaFirma de acuerdo a si las
     * firmas existen o no en la base de datos. 
     * NO borra las firmas que ahora no estén y antes si estaban.
     * NO deben existir firmas repetidas en la lista que se va a guardar.
     * @param em EntityManager a usar, debe estar ya dentro de un EntityManager.begin();
     * @param persona Persona del que se almacenará las firmas, NO debe tener firmas repetidas
     */
    private void guardarPersonaFirma(EntityManager em, Persona persona) {
        for (PersonaFirma perFirma : persona.getPersonaFirmasCollection()) {
            //Firma firmaExistente = findFirma(em, perFirma.getFirma().getNombre());
            PersonaFirma perFirmaExistente = findPersonaFirma(persona.getId(), perFirma.getFirma().getNombre());
            Firma firmaExistente = findFirma(em, perFirma.getFirma().getNombre());

            if (firmaExistente != null) {
                if (perFirmaExistente != null) {
                    //significa que ya existe la firma en la db y también ya existe una relación entre la firma y la persona que se modifica
                    // por lo tanto no hay que realizar modificación alguna
                    //En caso de PersonaFirma tenga campos adicionales, aquí se los debería editar, mientras tanto sigue comentado el código
                    //perFirma.setId(perFirmaExistente.getId());
                    //em.merge(perfirma);
                } else {
                    //si la firma existe pero no hay relación entre la firma y la persona, entonces significa que la persona que se está editando
                    // va a tener una firma que es usado por varias personas, por lo tanto hay que agrega una nueva PersonaFirma indicando 
                    // la relación entre la persona que se está editanto y la firma que ya existe
                    perFirma.setId(null);
                    perFirma.setPersona(persona);
                    perFirma.setFirma(firmaExistente);
                    em.persist(perFirma);
                }
            } else {
                //si no existe la firma, se debe crear tanto la PersonaFirma como la Firma, independientemente de los ids que tengan
                perFirma.getFirma().setId(null);
                em.persist(perFirma.getFirma());

                perFirma.setId(null);
                em.persist(perFirma);
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
