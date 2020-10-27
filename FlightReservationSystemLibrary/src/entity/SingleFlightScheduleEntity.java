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

/**
 *
 * @author sohqi
 */
@Entity
public class SingleFlightScheduleEntity extends FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long flightSchedulePlanId;
    private String flightNumber;
    private List<FlightScheduleEntity> listOfFlightSchedule;
    private FlightSchedulePlanEntity returnFlightSchedulePlan;
    private List<FareEntity> listOfFare;
    private boolean isDeleted;

    public SingleFlightScheduleEntity() {
        super();
        listOfFlightSchedule = new ArrayList<FlightScheduleEntity>();
        listOfFare = new ArrayList<FareEntity>();
    }

    public SingleFlightScheduleEntity(String flightNumber, List<FlightScheduleEntity> listOfFlightSchedule, FlightSchedulePlanEntity flightSchedulePlan, List<FareEntity> listOfFare, boolean isDeleted) {
        this.flightNumber = flightNumber;
        this.listOfFlightSchedule = listOfFlightSchedule;
        this.returnFlightSchedulePlan = flightSchedulePlan;
        this.listOfFare = listOfFare;
        this.isDeleted = isDeleted;
    }

    @Override
    public Long getFlightSchedulePlanId() {
        return flightSchedulePlanId;
    }
    
    @Override
    public String getFlightNumber() {
        return flightNumber;
    }

    @Override
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    @Override
    public List<FlightScheduleEntity> getListOfFlightSchedule() {
        return listOfFlightSchedule;
    }

    @Override
    public void setListOfFlightSchedule(List<FlightScheduleEntity> listOfFlightSchedule) {
        this.listOfFlightSchedule = listOfFlightSchedule;
    }


    @Override
    public List<FareEntity> getListOfFare() {
        return listOfFare;
    }

    @Override
    public void setListOfFare(List<FareEntity> listOfFare) {
        this.listOfFare = listOfFare;
    }

    @Override
    public FlightSchedulePlanEntity getReturnFlightSchedulePlan() {
        return returnFlightSchedulePlan;
    }

    @Override
    public void setReturnFlightSchedulePlan(FlightSchedulePlanEntity returnFlightSchedulePlan) {
        this.returnFlightSchedulePlan = returnFlightSchedulePlan;
    }

    @Override
    public boolean isIsDeleted() {
        return isDeleted;
    }

    @Override
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
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
        if (!(object instanceof SingleFlightScheduleEntity)) {
            return false;
        }
        SingleFlightScheduleEntity other = (SingleFlightScheduleEntity) object;
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
