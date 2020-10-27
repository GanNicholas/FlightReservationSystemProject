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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author sohqi
 */
@Entity
public class FlightScheduleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long FlightScheduleId;
    private GregorianCalendar departureDateTime;
    private double flightDuration;
    @ManyToOne(optional = true)
    private FlightSchedulePlanEntity flightSchedulePlan;
    @OneToMany
    private List<List<SeatEntity>> seatingPlan;

    public FlightScheduleEntity() {
        seatingPlan = new ArrayList<List<SeatEntity>>();
    }

    public FlightScheduleEntity(GregorianCalendar departureDateTime, double flightDuration, FlightSchedulePlanEntity flightSchedulePlan, List<List<SeatEntity>> seatingPlan) {
        this.departureDateTime = departureDateTime;
        this.flightDuration = flightDuration;
        this.flightSchedulePlan = flightSchedulePlan;
        this.seatingPlan = seatingPlan;
    }

    public FlightScheduleEntity(GregorianCalendar departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public Long getFlightScheduleId() {
        return FlightScheduleId;
    }

    public double getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(double flightDuration) {
        this.flightDuration = flightDuration;
    }

    public FlightSchedulePlanEntity getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    public void setFlightSchedulePlan(FlightSchedulePlanEntity flightSchedulePlan) {
        this.flightSchedulePlan = flightSchedulePlan;
    }

    public List<List<SeatEntity>> getSeatingPlan() {
        return seatingPlan;
    }

    public void setSeatingPlan(List<List<SeatEntity>> seatingPlan) {
        this.seatingPlan = seatingPlan;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getFlightScheduleId() != null ? getFlightScheduleId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the FlightScheduleId fields are not set
        if (!(object instanceof FlightScheduleEntity)) {
            return false;
        }
        FlightScheduleEntity other = (FlightScheduleEntity) object;
        if ((this.getFlightScheduleId() == null && other.getFlightScheduleId() != null) || (this.getFlightScheduleId() != null && !this.FlightScheduleId.equals(other.FlightScheduleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedule[ id=" + getFlightScheduleId() + " ]";
    }

}