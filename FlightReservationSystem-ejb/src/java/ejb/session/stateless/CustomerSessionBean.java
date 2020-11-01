/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.CustomerEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerExistException;
import util.exception.CustomerLoginInvalid;

/**
 *
 * @author sohqi
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    public Long registerCustomer(CustomerEntity c) throws CustomerExistException {
        try {
            boolean isCustomerExist = isCustomerExist(c.getLoginId());
            em.persist(c);
            em.flush();
            return c.getCustomerId();

        } catch (CustomerExistException ex) {
            throw new CustomerExistException("Customer already exist");
        }

    }

    public boolean isCustomerExist(String login) throws CustomerExistException {
        try {
            Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.loginId =:login").setParameter("login", login);
            CustomerEntity c = (CustomerEntity) query.getSingleResult();
            throw new CustomerExistException("Customer already exist");
        } catch (NoResultException ex) {
            return false;
        }

    }

    public CustomerEntity customerLogin(String username, String password) throws CustomerLoginInvalid {
        try {
            Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.loginId =:login AND c.loginPw=:password").setParameter("login", username).setParameter("password", password);
            CustomerEntity customer = (CustomerEntity) query.getSingleResult();
            return customer;
        } catch (NoResultException ex) {
            throw new CustomerLoginInvalid("Invalid customer login");
        }
    }

}
