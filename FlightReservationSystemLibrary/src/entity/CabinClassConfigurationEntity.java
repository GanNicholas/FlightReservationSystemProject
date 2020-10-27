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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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

    @Enumerated(EnumType.STRING)
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

    @Positive
    @Min(value = 1, message = "Minimum number of available seats is 1")
    @Max(value = 1000, message = "Maximum number of available seats is 1000")
    @Column(nullable = false)
    private Integer availableSeats;

    @Positive
    @Min(value = 1, message = "Minimum number of reserved seats is 1")
    @Max(value = 1000, message = "Maximum number of reserved seats is 1000")
    @Column(nullable = false)
    private Integer reservedSeats;

    @Positive
    @Min(value = 1, message = "Minimum number of balanced seats is 1")
    @Max(value = 1000, message = "Maximum number of balanced seats is 1000")
    @Column(nullable = false)
    private Integer balancedSeats;

    @NotBlank(message = "Seating configuration cannot be empty")
    @Size(min = 3, max = 4, message = "Seating configuration should be between 3 to 4 characters, inclusive of '-'")
    @Column(nullable = false)
    private String seatingConfig;

    @Positive
    @Min(value = 1, message = "Minimum number of seats in a column is 1")
    @Max(value = 1000, message = "Maximum number of seats in a column is 1000")
    @Column(nullable = false)
    private Integer numSeatsInAColumn;
    
    private List<FareEntity> fares;

    public Long getCabinClassConfigId() {
        return cabinClassConfigId;
    }

    public void setCabinClassConfigId(Long cabinClassConfigId) {
        this.cabinClassConfigId = cabinClassConfigId;
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
        return "entity.CabinClassConfigurationEntity[ id=" + cabinClassConfigId + " ]";
    }

}
