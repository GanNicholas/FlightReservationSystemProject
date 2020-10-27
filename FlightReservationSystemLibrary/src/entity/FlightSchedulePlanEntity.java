/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author sohqi
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightSchedulePlanId;
    private String flightNumber;
    @OneToMany
    private List<FlightScheduleEntity> listOfFlightSchedule;
    private FlightSchedulePlanEntity returnFlightSchedulePlan;
    @OneToMany
    private List<Fare> listOfFare;
    private boolean isDeleted;
    @ManyToOne
    private FlightEntity flightEntity;
    public FlightSchedulePlanEntity() {
        listOfFare = new ArrayList<Fare>();
    }

    public FlightSchedulePlanEntity(String flightNumber, List<FlightScheduleEntity> listOfFlightSchedule, FlightSchedulePlanEntity flightSchedulePlan, List<Fare> listOfFare, boolean isDeleted) {
        this.flightNumber = flightNumber;
        this.listOfFlightSchedule = listOfFlightSchedule;
        this.returnFlightSchedulePlan = flightSchedulePlan;
        this.listOfFare = listOfFare;
        this.isDeleted = isDeleted;
    }
       
    
    public Long getFlightSchedulePlanId() {
        return flightSchedulePlanId;
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

    public List<Fare> getListOfFare() {
        return listOfFare;
    }

    public void setListOfFare(List<Fare> listOfFare) {
        this.listOfFare = listOfFare;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
}
