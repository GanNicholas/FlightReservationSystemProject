/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author sohqi
 */
@Entity
public class FareEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long FareId;
    private String fareBasisCode;
    private BigDecimal fareAmount;

    public FareEntity() {
    }

    public FareEntity(String fareBasisCode, BigDecimal fareAmount) {
        this.fareBasisCode = fareBasisCode;
        this.fareAmount = fareAmount;
    }
    
    public Long getFareId() {
        return FareId;
    }

    public void setFareId(Long FareId) {
        this.FareId = FareId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getFareId() != null ? getFareId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the FareId fields are not set
        if (!(object instanceof FareEntity)) {
            return false;
        }
        FareEntity other = (FareEntity) object;
        if ((this.getFareId() == null && other.getFareId() != null) || (this.getFareId() != null && !this.FareId.equals(other.FareId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FareEntity[ id=" + getFareId() + " ]";
    }

    public String getFareBasisCode() {
        return fareBasisCode;
    }

    public void setFareBasisCode(String fareBasisCode) {
        this.fareBasisCode = fareBasisCode;
    }

    public BigDecimal getFareAmount() {
        return fareAmount;
    }

    public void setFareAmount(BigDecimal fareAmount) {
        this.fareAmount = fareAmount;
    }
    
}
