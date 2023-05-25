package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import race.system.MyDate;

public class MyDateDao {
    private EntityManager em;

    private boolean[] freeIDs;

    public MyDateDao(EntityManager em) {
        this.em = em;
        em.setFlushMode(FlushModeType.COMMIT);
        freeIDs = new boolean[2000];
        for (int i = 0; i < 2000; i++)
            freeIDs[i] = true;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<MyDate> getAllDates() throws HibernateException {
        try {
            em.getTransaction().begin();
            List<MyDate> myDates = em.createQuery("FROM Dates", MyDate.class)
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH").getResultList();
            em.clear();
            em.getTransaction().commit();
            return myDates;

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public void updateFreeID(List<MyDate> myDates) {
        for (MyDate myDate : myDates) {
            freeIDs[myDate.getDateId()] = false;
        }
    }
}
