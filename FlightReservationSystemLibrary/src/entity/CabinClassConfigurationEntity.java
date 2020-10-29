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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import util.enumeration.CabinClassType;

/**
 *
 * @author nickg
 */
@Entity
public class CabinClassConfigurationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cabinClassConfigId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CabinClassType cabinclassType;

    @Positive
    @Min(value = 1, message = "Minimum number of aisles is 1")
    @Max(value = 2, message = "Maximum number of aisles is 2")
    @Column(nullable = false)
    private Integer numAisles;

    @Positive
    @Min(value = 1, message = "Minimum number of rows is 1")
    @Max(value = 200, message = "Maximum number of rows is 200")
    @Column(nullable = false)
    private Integer numRows;

    // @Positive
    //@Min(value = 0, message = "Minimum number of available seats is 0")
    @Max(value = 1000, message = "Maximum number of available seats is 1000")
    @Column(nullable = false)
    private Integer availableSeats;

    //@Positive
    //@Min(value = 0, message = "Minimum number of reserved seats is 0")
    @Max(value = 1000, message = "Maximum number of reserved seats is 1000")
    @Column(nullable = false)
    private Integer reservedSeats;

    // @Positive
    //@Min(value = 0, message = "Minimum number of balanced seats is 0")
    @Max(value = 1000, message = "Maximum number of balanced seats is 1000")
    @Column(nullable = false)
    private Integer balancedSeats;

    @NotBlank(message = "Seating configuration cannot be empty")
    @Size(min = 3, max = 5, message = "Seating configuration should be between 3 to 5 characters, inclusive of '-'")
    @Column(nullable = false)
    private String seatingConfig;

    @OneToMany
    private List<FareEntity> fares;

    public CabinClassConfigurationEntity() {
        this.fares = new ArrayList<>();
    }

    public CabinClassConfigurationEntity(CabinClassType cabinclassType, Integer numAisles, Integer numRows, Integer availableSeats, Integer reservedSeats, Integer balancedSeats, String seatingConfig) {
        this();
        this.cabinclassType = cabinclassType;
        this.numAisles = numAisles;
        this.numRows = numRows;
        this.availableSeats = availableSeats;
        this.reservedSeats = reservedSeats;
        this.balancedSeats = balancedSeats;
        this.seatingConfig = seatingConfig;
    }

    public Long getCabinClassConfigId() {
        return cabinClassConfigId;
    }

    public void setCabinClassConfigId(Long cabinClassConfigId) {
        this.cabinClassConfigId = cabinClassConfigId;
    }

    public CabinClassType getCabinclassType() {
        return cabinclassType;
    }

    public void setCabinclassType(CabinClassType cabinclassType) {
        this.cabinclassType = cabinclassType;
    }

    public Integer getNumAisles() {
        return numAisles;
    }

    public void setNumAisles(Integer numAisles) {
        this.numAisles = numAisles;
    }

    public Integer getNumRows() {
        return numRows;
    }

    public void setNumRows(Integer numRows) {
        this.numRows = numRows;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(Integer reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public Integer getBalancedSeats() {
        return balancedSeats;
    }

    public void setBalancedSeats(Integer balancedSeats) {
        this.balancedSeats = balancedSeats;
    }

    public String getSeatingConfig() {
        return seatingConfig;
    }

    public void setSeatingConfig(String seatingConfig) {
        this.seatingConfig = seatingConfig;
    }

    public List<FareEntity> getFares() {
        return fares;
    }

    public void setFares(List<FareEntity> fares) {
        this.fares = fares;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cabinClassConfigId != null ? cabinClassConfigId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the cabinClassConfigId fields are not set
        if (!(object instanceof CabinClassConfigurationEntity)) {
            return false;
        }
        CabinClassConfigurationEntity other = (CabinClassConfigurationEntity) object;
        if ((this.cabinClassConfigId == null && other.cabinClassConfigId != null) || (this.cabinClassConfigId != null && !this.cabinClassConfigId.equals(other.cabinClassConfigId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CabinClassConfigurationEntity[ id=" + cabinClassConfigId + " ] + [ cabinclassType=" + cabinclassType + " ]  + [ numAisles=" + numAisles + " ]"
                + "+ [ numRows=" + numRows + " ]+ [ availableSeats=" + availableSeats + " ]+ [ reservedSeats=" + reservedSeats + " ]+ [ balancedSeats=" + balancedSeats + " ]+ [ seatingConfig=" + seatingConfig + " ]";
    }

}
