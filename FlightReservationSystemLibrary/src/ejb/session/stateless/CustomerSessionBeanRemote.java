/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import javax.ejb.Remote;
import util.exception.CustomerDoesNotExistException;
import util.exception.CustomerExistException;
import util.exception.CustomerLoginInvalid;

/**
 *
 * @author sohqi
 */
@Remote
public interface CustomerSessionBeanRemote {

    public Long registerCustomer(CustomerEntity c) throws CustomerExistException;

    public CustomerEntity customerLogin(String username, String password) throws CustomerLoginInvalid;
    
    public boolean isCustomerExist(String login) throws CustomerExistException;

    public CustomerEntity retrieveCustomerInfo(Long custId) throws CustomerDoesNotExistException;
}
