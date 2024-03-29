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
    @Future
    private GregorianCalendar arrivalDateTime;

    @Max(value = 1800, message = "Maximum flight duration is 1800 minutes (30 hours)!")
    @Positive
    private Integer flightDuration;

    @ManyToOne(optional = true, cascade = {CascadeType.DETACH})
    @JoinColumn(nullable = false)
    private FlightSchedulePlanEntity flightSchedulePlan;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    private List<SeatEntity> seatingPlan;

    public FlightScheduleEntity() {
        seatingPlan = new ArrayList<SeatEntity>();
    }

    public FlightScheduleEntity(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity flightSchedulePlan, GregorianCalendar arrivalDateTime) {
        this();
        this.departureDateTime = departureDateTime;
        this.flightDuration = flightDuration;
        this.flightSchedulePlan = flightSchedulePlan;
        this.arrivalDateTime = arrivalDateTime;

    }

    public FlightScheduleEntity(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity flightSchedulePlan, List<SeatEntity> seatingPlan, GregorianCalendar arrivalDateTime) {
        this.departureDateTime = departureDateTime;
        this.flightDuration = flightDuration;
        this.flightSchedulePlan = flightSchedulePlan;
        this.seatingPlan = seatingPlan;
        this.arrivalDateTime = arrivalDateTime;
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

    public GregorianCalendar getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(GregorianCalendar arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
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
