/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Entity
public class PartnerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    @NotEmpty(message = "First Name cannot be empty!")
    @Size(min = 2, max = 80, message = "Name needs to be between 2 to 80 characters")
    @Column(nullable = false, length = 80)
    private String name;

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
    @OneToMany
    @JoinColumn(nullable = false)
    private List<FlightReservationEntity> listOfFlightReservation;
    
    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public UserRole getUserRole() {
        return userRole;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerId != null ? partnerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerId fields are not set
        if (!(object instanceof PartnerEntity)) {
            return false;
        }
        PartnerEntity other = (PartnerEntity) object;
        if ((this.partnerId == null && other.partnerId != null) || (this.partnerId != null && !this.partnerId.equals(other.partnerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PartnerEntity[ id=" + partnerId + " ]";
    }
}
