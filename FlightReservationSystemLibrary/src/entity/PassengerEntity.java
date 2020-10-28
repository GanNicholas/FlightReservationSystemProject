/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 *
 * @author nickg
 */
@Entity
public class PassengerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passengerId;

    @NotEmpty(message = "First Name cannot be empty!")
    @Size(min = 2, max = 80, message = "First name needs to be between 2 to 80 characters")
    @Column(nullable = false, length = 80)
    private String firstName;

    @NotEmpty(message = "Last Name cannot be empty!")
    @Size(min = 2, max = 80, message = "Last name needs to be between 2 to 80 characters")
    @Column(nullable = false, length = 80)
    private String lastName;

    @NotEmpty(message = "Passport number cannot be empty!")
    @Size(min = 6, max = 20, message = "Passport number should be between 6 to 20 characters")
    @Column(nullable = false, length = 20, unique = true)
    private String passportNumber;

    public PassengerEntity() {
    }

    public PassengerEntity(String firstName, String lastName, String passportNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
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

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (passengerId != null ? passengerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the passengerId fields are not set
        if (!(object instanceof PassengerEntity)) {
            return false;
        }
        PassengerEntity other = (PassengerEntity) object;
        if ((this.passengerId == null && other.passengerId != null) || (this.passengerId != null && !this.passengerId.equals(other.passengerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PassengerEntity[ id=" + passengerId + " ]";
    }

}
