/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;


import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Entity;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 *
 * @author sohqi
 */
@Entity
public class RecurringScheduleEntity extends FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Future
    private GregorianCalendar endDate;
    @Positive
    @Min(value = 1, message = "Minimum for recurrent frequency is 1!")
    private Integer recurrentFreq;

    public RecurringScheduleEntity() {
        super();
        endDate = null;
    }
    
    public RecurringScheduleEntity(String flightNumber, boolean isDeleted, FlightEntity flightEntity, GregorianCalendar endDate, Integer recurrentFreq) {
        super(flightNumber, isDeleted, flightEntity);
        this.endDate = endDate;
        this.recurrentFreq = recurrentFreq;
    }

    public RecurringScheduleEntity(String flightNumber, List<FlightScheduleEntity> listOfFlightSchedule, FlightSchedulePlanEntity returnFlightSchedulePlan, List<FareEntity> listOfFare, boolean isDeleted, int recurrentFreq, GregorianCalendar endDate, FlightEntity flightEntity) {
        super(flightNumber, listOfFlightSchedule, returnFlightSchedulePlan, listOfFare, isDeleted, flightEntity);
        this.endDate = endDate;
        this.recurrentFreq = recurrentFreq;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightSchedulePlanId != null ? flightSchedulePlanId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightSchedulePlanId fields are not set
        if (!(object instanceof RecurringScheduleEntity)) {
            return false;
        }
        RecurringScheduleEntity other = (RecurringScheduleEntity) object;
        if ((this.flightSchedulePlanId == null && other.flightSchedulePlanId != null) || (this.flightSchedulePlanId != null && !this.equals(other.flightSchedulePlanId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RecurringScheduleEntity[ id=" + flightSchedulePlanId + " ]";
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public int getRecurrentFreq() {
        return recurrentFreq;
    }

    public void setRecurrentFreq(int recurrentFreq) {
        this.recurrentFreq = recurrentFreq;
    }

}
