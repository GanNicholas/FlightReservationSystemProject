/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.CurrentlyLoggedInException;
import util.exception.CurrentlyLoggedOutException;
import util.exception.EmployeeDoesNotExistException;
import util.exception.WrongPasswordException;

/**
 *
 * @author nickg
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    public EmployeeSessionBean() {
    }

    @Override
    public boolean employeeLogin(String userId, String userPw) throws EmployeeDoesNotExistException, WrongPasswordException, CurrentlyLoggedInException {
        try {
            EmployeeEntity employee = (EmployeeEntity) em.createNamedQuery("retrieveUsingLogin").setParameter("login", userId).getSingleResult();
            if (employee.getLoginPw().equals(userPw)) {
                return true;
            } else {
                throw new WrongPasswordException("Wrong password has been entered!");
            }
        } catch (NoResultException ex) {
            throw new EmployeeDoesNotExistException("Employee does not exist!");
        }
    }

    @Override
    public boolean employeeLogOut(String userId) throws EmployeeDoesNotExistException, CurrentlyLoggedOutException {
        try {
            EmployeeEntity employee = (EmployeeEntity) em.createNamedQuery("retrieveUsingLogin").setParameter("login", userId).getSingleResult();
            return true;
        } catch (NoResultException ex) {
            throw new EmployeeDoesNotExistException("Employee does not exist!");
        }
    }

}
