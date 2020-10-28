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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
    protected Long flightSchedulePlanId;

    @NotEmpty(message = "Flight number cannot be empty!")
    @Size(min = 1, max = 16, message = "Flight number should not exceed 16 characters!")
    @Column(nullable = false, length = 8)
    protected String flightNumber;

    @OneToMany(mappedBy = "flightSchedulePlan")
    @JoinColumn(nullable = false)
    protected List<FlightScheduleEntity> listOfFlightSchedule;

    protected FlightSchedulePlanEntity returnFlightSchedulePlan;

    @OneToMany
    @JoinColumn(nullable = false)
    protected List<FareEntity> listOfFare;
    

    protected boolean isDeleted;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected FlightEntity flightEntity;

    public FlightSchedulePlanEntity() {
        listOfFlightSchedule = new ArrayList<FlightScheduleEntity>();
        listOfFare = new ArrayList<FareEntity>();
        returnFlightSchedulePlan = null;
        flightEntity = null;
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
