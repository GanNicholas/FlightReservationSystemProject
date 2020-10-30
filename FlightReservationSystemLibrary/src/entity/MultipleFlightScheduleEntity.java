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
public class MultipleFlightScheduleEntity extends FlightSchedulePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public MultipleFlightScheduleEntity() {
        super();
    }

    public MultipleFlightScheduleEntity(String flightNumber, boolean isDeleted, FlightEntity flightEntity) {
        super(flightNumber, isDeleted, flightEntity);
    }

    public MultipleFlightScheduleEntity(String flightNumber, List<FlightScheduleEntity> listOfFlightSchedule, FlightSchedulePlanEntity returnFlightSchedulePlan, List<FareEntity> listOfFare, boolean isDeleted, FlightEntity flightEntity) {
        super(flightNumber, listOfFlightSchedule, returnFlightSchedulePlan, listOfFare, isDeleted, flightEntity);
    }

    @Override
    public String toString() {
        return "entity.FlightSchedulePlan[ id=" + flightSchedulePlanId + " ]";
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
        if (!(object instanceof MultipleFlightScheduleEntity)) {
            return false;
        }
        MultipleFlightScheduleEntity other = (MultipleFlightScheduleEntity) object;
        if ((this.flightSchedulePlanId == null && other.flightSchedulePlanId != null) || (this.flightSchedulePlanId != null && !this.flightSchedulePlanId.equals(other.flightSchedulePlanId))) {
            return false;
        }
        return true;
    }

}
