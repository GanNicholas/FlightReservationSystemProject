/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Entity
public class PartnerEntity extends CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Name cannot be empty!")
    @Size(min = 2, max = 80, message = "Name needs to be between 2 to 80 characters")
    @Column(nullable = false, length = 80)
    private String partnerName;

    public PartnerEntity() {
        super();
    }

    public PartnerEntity(String loginId, String loginPw, UserRole userRole, String partnerName) {
        super(loginId, loginPw, userRole);
        this.partnerName = partnerName;
    }

    public Long getPartnerId() {
        return this.getCustomerId();
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getCustomerId() != null ? this.getCustomerId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerId fields are not set
        if (!(object instanceof PartnerEntity)) {
            return false;
        }
        PartnerEntity other = (PartnerEntity) object;
        if ((this.getPartnerId() == null && other.getPartnerId() != null) || (this.getPartnerId() != null && !this.getPartnerId().equals(other.getPartnerId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PartnerEntity[ id=" + this.getCustomerId() + " ]";
    }
}
