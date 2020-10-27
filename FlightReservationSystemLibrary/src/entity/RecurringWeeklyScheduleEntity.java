/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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
public class RecurringWeeklyScheduleEntity extends FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long flightSchedulePlanId;
    private String flightNumber;
    private List<FlightScheduleEntity> listOfFlightSchedule;
    private FlightSchedulePlanEntity returnFLightSchedulePlan;
    private List<FareEntity> listOfFare;
    private boolean isDeleted;
    private GregorianCalendar endDate;

    public RecurringWeeklyScheduleEntity() {
        super();
        listOfFlightSchedule = new ArrayList<FlightScheduleEntity>();
        listOfFare = new ArrayList<FareEntity>();

    }

    public RecurringWeeklyScheduleEntity(String flightNumber, List<FlightScheduleEntity> listOfFlightSchedule, FlightSchedulePlanEntity returnFLightSchedulePlan, List<FareEntity> listOfFare, boolean isDeleted, GregorianCalendar endDate) {
        this.flightNumber = flightNumber;
        this.listOfFlightSchedule = listOfFlightSchedule;
        this.returnFLightSchedulePlan = returnFLightSchedulePlan;
        this.listOfFare = listOfFare;
        this.isDeleted = isDeleted;
        this.endDate = endDate;
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
        if ((this.getFlightSchedulePlanId() == null && other.getFlightSchedulePlanId() != null) || (this.getFlightSchedulePlanId() != null && !this.flightSchedulePlanId.equals(other.getFlightSchedulePlanId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedulePlan[ id=" + getFlightSchedulePlanId() + " ]";
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

    public FlightSchedulePlanEntity getReturnFLightSchedulePlan() {
        return returnFLightSchedulePlan;
    }

    public void setReturnFLightSchedulePlan(FlightSchedulePlanEntity returnFLightSchedulePlan) {
        this.returnFLightSchedulePlan = returnFLightSchedulePlan;
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

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

}
