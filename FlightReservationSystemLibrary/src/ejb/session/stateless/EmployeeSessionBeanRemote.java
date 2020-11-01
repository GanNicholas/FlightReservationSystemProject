/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.Remote;
import util.enumeration.UserRole;
import util.exception.CurrentlyLoggedInException;
import util.exception.CurrentlyLoggedOutException;
import util.exception.EmployeeDoesNotExistException;
import util.exception.WrongPasswordException;

/**
 *
 * @author nickg
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public boolean employeeLogin(String userId, String userPw) throws EmployeeDoesNotExistException, WrongPasswordException, CurrentlyLoggedInException;

    public UserRole getEmployeeRole(String userId) throws EmployeeDoesNotExistException;

    public boolean employeeLogOut(String userId) throws EmployeeDoesNotExistException, CurrentlyLoggedOutException;

}
