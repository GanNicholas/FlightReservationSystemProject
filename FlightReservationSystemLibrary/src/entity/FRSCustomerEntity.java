/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Entity
public class FRSCustomerEntity extends CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "First Name cannot be empty!")
    @Size(min = 2, max = 80, message = "First name needs to be between 2 to 80 characters")
    @Column(nullable = false, length = 80)
    private String firstName;

    @NotEmpty(message = "Last Name cannot be empty!")
    @Size(min = 2, max = 80, message = "Last name needs to be between 2 to 80 characters")
    @Column(nullable = false, length = 80)
    private String lastName;

    @NotEmpty(message = "Email cannot be empty!")
    @Size(min = 15, max = 320, message = "Email address has to be between 15 and 320 characters inclusive @domain.com")
    @Column(nullable = false, length = 320)
    private String email;

    @NotEmpty(message = "Mobile phone cannot be empty!")
    @Size(min = 8, max = 18, message = "Phone number is 8 to 18 numbers long, with country prefix")
    @Column(nullable = false, length = 8, unique = true)
    private String phoneNumber;

    @NotEmpty(message = "Address cannot be empty!")
    @Size(min = 10, max = 85, message = "Address has to be minimum 10 characters")
    @Column(nullable = false, length = 85)
    private String address;

    public FRSCustomerEntity() {
        super();
    }

    public FRSCustomerEntity(String loginId, String loginPw, UserRole userRole, String firstName, String lastName, String email, String phoneNumber, String address) {
        super(loginId, loginPw, userRole);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.customerId != null ? this.customerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FRSCustomerEntity)) {
            return false;
        }
        FRSCustomerEntity other = (FRSCustomerEntity) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OwnCustomerEntity[ id=" + this.customerId + " ]";
    }

}
