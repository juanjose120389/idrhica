package ec.edu.chyc.manejopersonal.controller.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EntityManagerUtil {

    private static final EntityManagerFactory emfInstance
            = Persistence.createEntityManagerFactory("manejopersonalPU");

    private EntityManagerUtil() {
    }

    public static EntityManagerFactory get() {
        return emfInstance;
    }
}
