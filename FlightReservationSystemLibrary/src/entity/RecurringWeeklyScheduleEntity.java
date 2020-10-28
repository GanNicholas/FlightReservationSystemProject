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
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

/**
 *
 * @author sohqi
 */
@Entity
public class RecurringWeeklyScheduleEntity extends FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Future
    private GregorianCalendar endDate;

    public RecurringWeeklyScheduleEntity() {
        super();
    }

    public RecurringWeeklyScheduleEntity(String flightNumber, List<FlightScheduleEntity> listOfFlightSchedule, FlightSchedulePlanEntity returnFlightSchedulePlan, List<FareEntity> listOfFare, boolean isDeleted, FlightEntity flightEntity, GregorianCalendar endDate) {
        super(flightNumber, listOfFlightSchedule, returnFlightSchedulePlan, listOfFare, isDeleted, flightEntity);
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
        if (!(object instanceof RecurringWeeklyScheduleEntity)) {
            return false;
        }
        RecurringWeeklyScheduleEntity other = (RecurringWeeklyScheduleEntity) object;
        if ((this.getFlightSchedulePlanId() == null && other.getFlightSchedulePlanId() != null) || (this.getFlightSchedulePlanId() != null && !this.flightSchedulePlanId.equals(other.getFlightSchedulePlanId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedulePlan[ id=" + getFlightSchedulePlanId() + " ]";
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

}
