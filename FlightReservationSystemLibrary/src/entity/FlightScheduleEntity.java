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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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

    @Future
    private GregorianCalendar departureDateTime;

    @Max(value = 480, message = "Maximum flight duration is 480 minutes (8 hours)!")
    @Positive
    private Integer flightDuration;

    @ManyToOne(optional = true)
    @JoinColumn(nullable = false)
    private FlightSchedulePlanEntity flightSchedulePlan;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<SeatEntity> seatingPlan;

    public FlightScheduleEntity() {
        seatingPlan = new ArrayList<SeatEntity>();
    }

    public FlightScheduleEntity(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity flightSchedulePlan) {
        this();
        this.departureDateTime = departureDateTime;
        this.flightDuration = flightDuration;
        this.flightSchedulePlan = flightSchedulePlan;
    }

    public FlightScheduleEntity(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity flightSchedulePlan, List<SeatEntity> seatingPlan) {
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

    public Integer getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(Integer flightDuration) {
        this.flightDuration = flightDuration;
    }

    public FlightSchedulePlanEntity getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    public void setFlightSchedulePlan(FlightSchedulePlanEntity flightSchedulePlan) {
        this.flightSchedulePlan = flightSchedulePlan;
    }

    public List<SeatEntity> getSeatingPlan() {
        return seatingPlan;
    }

    public void setSeatingPlan(List<SeatEntity> seatingPlan) {
        this.seatingPlan = seatingPlan;
    }

    public GregorianCalendar getDepartureDateTime() {
        return departureDateTime;
    }
    
    public GregorianCalendar getArrivalDateTime(){
        GregorianCalendar arrivalDateTime = (GregorianCalendar) departureDateTime.clone();
        arrivalDateTime.add(GregorianCalendar.MINUTE, flightDuration);
        return arrivalDateTime;
    }

    public void setDepartureDateTime(GregorianCalendar departureDateTime) {
        this.departureDateTime = departureDateTime;
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
