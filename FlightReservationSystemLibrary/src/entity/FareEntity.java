/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.CabinClassType;

/**
 *
 * @author sohqi
 */
@Entity
public class FareEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long FareId;
    
    @NotEmpty(message = "Fare basis code cannot be empty!")
    @Size(min = 3, max = 7, message = "Fare basis code has to be 3 to 7 characters long!")
    @Column(nullable = false, length = 7)
    private String fareBasisCode;
    
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, scale = 2)
    private BigDecimal fareAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CabinClassType cabinType;
    
    public FareEntity() {
    }

    public FareEntity(String fareBasisCode, BigDecimal fareAmount, CabinClassType cabinType) {
        this.fareBasisCode = fareBasisCode;
        this.fareAmount = fareAmount;
        this.cabinType = cabinType;
    }

    public Long getFareId() {
        return FareId;
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

    public CabinClassType getCabinType() {
        return cabinType;
    }

    public void setCabinType(CabinClassType cabinType) {
        this.cabinType = cabinType;
    }

}
