/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long customerId;

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

    //@NotNull
    @OneToMany(mappedBy = "customer", cascade = {CascadeType.DETACH})
    private List<FlightReservationEntity> listOfFlightReservation;

    public CustomerEntity() {
        listOfFlightReservation = new ArrayList<>();
    }

    public CustomerEntity(String loginId, String loginPw, UserRole userRole) {
        this();
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
