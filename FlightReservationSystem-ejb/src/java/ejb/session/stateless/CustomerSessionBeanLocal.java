/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import javax.ejb.Local;
import util.exception.CustomerExistException;
import util.exception.CustomerLoginInvalid;

/**
 *
 * @author sohqi
 */
@Local
public interface CustomerSessionBeanLocal {

    public Long registerCustomer(CustomerEntity c) throws CustomerExistException;
    public CustomerEntity customerLogin(String username, String password) throws CustomerLoginInvalid;
}
