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
            List<MyDate> myDates = em.createQuery("FROM MyDate", MyDate.class)
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH").getResultList();
            em.clear();
            em.getTransaction().commit();
            return myDates;

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public MyDate findDate(int id) {
        em.getTransaction().begin();
        MyDate Date = em.find(MyDate.class, id);
        em.clear();
        em.getTransaction().commit();
        return Date;
    }

    public void saveDate(MyDate date) {
        try {
            em.getTransaction().begin();
            em.persist(date);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void updateDate(MyDate date) {
        try {
            em.getTransaction().begin();
            em.merge(date);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void deleteDate(MyDate date) {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM MyDate d WHERE dateID = :id", null).setParameter("id", date.getDateID())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearDate() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM MyDate").executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public int getFreeID() {
        int id = 0;
        em.getTransaction().begin();
        for (int i = 0; i < freeIDs.length; i++) {
            if (freeIDs[i]) {
                id = i;
                break;
            }
        }
        em.getTransaction().commit();
        return id;
    }

    public void addFreeID(int id) {
        freeIDs[id] = true;
    }

    public void updateFreeID(List<MyDate> myDates) {
        for (MyDate myDate : myDates) {
            freeIDs[myDate.getDateID()] = false;
        }
    }
}
