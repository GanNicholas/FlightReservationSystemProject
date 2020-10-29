/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 *
 * @author sohqi
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "queryFSPwithFlightNumber", query = "SELECT c FROM FlightSchedulePlanEntity c WHERE c.flightNumber = :flightNum")
public abstract class FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long flightSchedulePlanId;

    @NotEmpty(message = "Flight number cannot be empty!")
    @Size(min = 1, max = 16, message = "Flight number should not exceed 16 characters!")
    @Column(nullable = false, length = 8)
    private String flightNumber;

    @OneToMany(mappedBy = "flightSchedulePlan")
    @JoinColumn(nullable = false)
    private List<FlightScheduleEntity> listOfFlightSchedule;

    @OneToOne
    private FlightSchedulePlanEntity returnFlightSchedulePlan;

    @OneToMany
    @JoinColumn(nullable = false)
    private List<FareEntity> listOfFare;

    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(nullable = false)
    private FlightEntity flightEntity;

    public FlightSchedulePlanEntity() {
        this.listOfFlightSchedule = new ArrayList<FlightScheduleEntity>();
        this.listOfFare = new ArrayList<FareEntity>();
        this.returnFlightSchedulePlan = null;
        this.flightEntity = null;
    }

    public FlightSchedulePlanEntity(String flightNumber, boolean isDeleted, FlightEntity flightEntity) {
        this.flightNumber = flightNumber;
        this.isDeleted = isDeleted;
        this.flightEntity = flightEntity;
        this.listOfFlightSchedule = new ArrayList<FlightScheduleEntity>();
        this.listOfFare = new ArrayList<FareEntity>();
        this.returnFlightSchedulePlan = null;
    }

    public FlightSchedulePlanEntity(String flightNumber, List<FlightScheduleEntity> listOfFlightSchedule, FlightSchedulePlanEntity flightSchedulePlan, List<FareEntity> listOfFare, boolean isDeleted, FlightEntity flightEntity) {
        this.flightNumber = flightNumber;
        this.listOfFlightSchedule = listOfFlightSchedule;
        this.returnFlightSchedulePlan = flightSchedulePlan;
        this.listOfFare = listOfFare;
        this.isDeleted = isDeleted;
        this.flightEntity = flightEntity;
    }

    public Long getFlightSchedulePlanId() {
        return flightSchedulePlanId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public List<FlightScheduleEntity> getListOfFlightSchedule() {
        return listOfFlightSchedule;
    }

    public void setListOfFlightSchedule(List<FlightScheduleEntity> listOfFlightSchedule) {
        this.listOfFlightSchedule = listOfFlightSchedule;
    }

    public FlightSchedulePlanEntity getReturnFlightSchedulePlan() {
        return returnFlightSchedulePlan;
    }

    public void setReturnFlightSchedulePlan(FlightSchedulePlanEntity returnFlightSchedulePlan) {
        this.returnFlightSchedulePlan = returnFlightSchedulePlan;
    }

    public List<FareEntity> getListOfFare() {
        return listOfFare;
    }

    public void setListOfFare(List<FareEntity> listOfFare) {
        this.listOfFare = listOfFare;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public FlightEntity getFlightEntity() {
        return flightEntity;
    }

    public void setFlightEntity(FlightEntity flightEntity) {
        this.flightEntity = flightEntity;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getFlightSchedulePlanId() != null ? getFlightSchedulePlanId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightSchedulePlanId fields are not set
        if (!(object instanceof FlightSchedulePlanEntity)) {
            return false;
        }
        FlightSchedulePlanEntity other = (FlightSchedulePlanEntity) object;
        if ((this.getFlightSchedulePlanId() == null && other.getFlightSchedulePlanId() != null) || (this.getFlightSchedulePlanId() != null && !this.flightSchedulePlanId.equals(other.flightSchedulePlanId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedulePlan[ id=" + getFlightSchedulePlanId() + " ]";
    }

}
