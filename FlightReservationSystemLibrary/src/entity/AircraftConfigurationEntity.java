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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 *
 * @author nickg
 */
@Entity
@NamedQuery(name = "findAirCraftConfig", query = "SELECT a FROM AircraftConfigurationEntity a WHERE a.aircraftName =:name")
public class AircraftConfigurationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftConfigId;

    @Min(value = 1, message = "Minimum seating capacity is 1!")
    @Max(value = 1000, message = "Maximum seating capacity is 1000!")
    @Positive
    @Column(nullable = false)
    private Integer maxSeatingCapacity;

    @OneToOne(optional = false, cascade = {CascadeType.DETACH})
    private AircraftTypeEntity aircraftType;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    private List<CabinClassConfigurationEntity> cabinClasses;

    @NotEmpty(message = "Aircraft name cannot be empty!")
    @Size(max = 80, message = "Aircraft name should not exceed 80 characters!")
    @Column(nullable = false, length = 80)
    private String aircraftName;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    private List<SeatEntity> seatingPlan;

    public AircraftConfigurationEntity() {
        this.cabinClasses = new ArrayList<>();
        this.seatingPlan = new ArrayList<>();
    }

    public AircraftConfigurationEntity(Integer maxSeatingCapacity, AircraftTypeEntity aircraftType, String aircraftName) {
        this();
        this.maxSeatingCapacity = maxSeatingCapacity;
        this.aircraftType = aircraftType;
        this.aircraftName = aircraftName;

    }

    public Long getAircraftConfigId() {
        return aircraftConfigId;
    }

    public void setAircraftConfigId(Long aircraftConfigId) {
        this.aircraftConfigId = aircraftConfigId;
    }

    public Integer getMaxSeatingCapacity() {
        return maxSeatingCapacity;
    }

    public void setMaxSeatingCapacity(Integer maxSeatingCapacity) {
        this.maxSeatingCapacity = maxSeatingCapacity;
    }

    public AircraftTypeEntity getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftTypeEntity aircraftType) {
        this.aircraftType = aircraftType;
    }

    public List<CabinClassConfigurationEntity> getCabinClasses() {
        return cabinClasses;
    }

    public void setCabinClasses(List<CabinClassConfigurationEntity> cabinClasses) {
        this.cabinClasses = cabinClasses;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public List<SeatEntity> getSeatingPlan() {
        return seatingPlan;
    }

    public void setSeatingPlan(List<SeatEntity> seatingPlan) {
        this.seatingPlan = seatingPlan;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aircraftConfigId != null ? aircraftConfigId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the aircraftConfigId fields are not set
        if (!(object instanceof AircraftConfigurationEntity)) {
            return false;
        }
        AircraftConfigurationEntity other = (AircraftConfigurationEntity) object;
        if ((this.aircraftConfigId == null && other.aircraftConfigId != null) || (this.aircraftConfigId != null && !this.aircraftConfigId.equals(other.aircraftConfigId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AircraftConfigurationEntity[ id=" + aircraftConfigId + " ]";
    }

}
