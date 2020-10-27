/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Entity
public class CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

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

    @Min(value = 8, message = "Phone number is minimum 8 numbers long, with country prefix")
    @Max(value = 18, message = "Phone number is 18 numbers long, with country prefix")
    @Column(nullable = false, length = 8, unique = true)
    private String phoneNumber;

    @NotEmpty(message = "Address cannot be empty!")
    @Size(min = 15, max = 85, message = "Address has to be minimum 15 characters")
    @Column(nullable = false, length = 85)
    private String address;

    @NotBlank(message = "Login ID cannot contain spaces or be empty!")
    @Size(min = 6, max = 16, message = "Login ID has to be minimum 6 characters and maximum 25")
    @Column(nullable = false, length = 16, unique = true)
    private String loginId;

    @NotBlank(message = "Login Password cannot contain spaces or be empty!")
    @Size(min = 8, max = 16, message = "Login Password has to be minimum 8 characters and maximum 25")
    @Column(nullable = false, length = 16)
    private String loginPw;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @NotNull
    @OneToMany()
    @JoinColumn(nullable = false)
    private List<FlightReservationEntity> listOfFlightReservation;

    public CustomerEntity() {
        listOfFlightReservation = new ArrayList<>();
    }

    public CustomerEntity(String firstName, String lastName, String email, String phoneNumber, String address, String loginId, String loginPw, UserRole userRole) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.userRole = userRole;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginPw() {
        return loginPw;
    }

    public void setLoginPw(String loginPw) {
        this.loginPw = loginPw;
    }

    public List<FlightReservationEntity> getListOfFlightReservation() {
        return listOfFlightReservation;
    }

    public void setListOfFlightReservation(List<FlightReservationEntity> listOfFlightReservation) {
        this.listOfFlightReservation = listOfFlightReservation;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (customerId != null ? customerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the customerId fields are not set
        if (!(object instanceof CustomerEntity)) {
            return false;
        }
        CustomerEntity other = (CustomerEntity) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CustomerEntity[ id=" + customerId + " ]";
    }

}
