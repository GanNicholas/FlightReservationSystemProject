/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 *
 * @author sohqi
 */
@Entity
@NamedQuery(name = "retrieveFlightUsingFlightNumber", query = "SELECT e FROM FlightEntity e WHERE e.flightNumber = :flightNum")
@NamedQuery(name = "retrieveFlightUsingFlightRoute", query = "SELECT e FROM FlightEntity e WHERE e.flightRoute = :flightRoute")
public class FlightEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    @NotEmpty(message = "Flight number cannot be empty!")
    @Size(min = 1, max = 16, message = "Flight number should not exceed 16 characters!")
    @Column(nullable = false, length = 16, unique = true)
    private String flightNumber;

    @ManyToOne
    @JoinColumn(nullable = false)
    private FlightRouteEntity flightRoute;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private AircraftConfigurationEntity aircraftConfig;

    private boolean isDeleted;

    @OneToMany(mappedBy = "flightEntity")
    private List<FlightSchedulePlanEntity> listOfFlightSchedulePlan;

    private FlightEntity returnFlight;

    public FlightEntity() {
        listOfFlightSchedulePlan = new ArrayList<FlightSchedulePlanEntity>();
        aircraftConfig = null;
        returnFlight = null;
    }

    public FlightEntity(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig) {
        this.flightNumber = flightNumber;
        this.flightRoute = flightRoute;
        this.aircraftConfig = aircraftConfig;
        this.returnFlight = null;
        this.listOfFlightSchedulePlan = new ArrayList<FlightSchedulePlanEntity>();
        this.isDeleted = false;
    }
    
    

    public FlightEntity(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, boolean isDeleted, List<FlightSchedulePlanEntity> listOfFlightSchedulePlan, FlightEntity returnFlight) {
        this.flightNumber = flightNumber;
        this.flightRoute = flightRoute;
        this.aircraftConfig = aircraftConfig;
        this.isDeleted = isDeleted;
        this.listOfFlightSchedulePlan = listOfFlightSchedulePlan;
        this.returnFlight = returnFlight;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightId != null ? flightId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightId fields are not set
        if (!(object instanceof FlightEntity)) {
            return false;
        }
        FlightEntity other = (FlightEntity) object;
        if ((this.flightId == null && other.flightId != null) || (this.flightId != null && !this.flightId.equals(other.flightId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightEntity[ id=" + flightId + " ]";
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public FlightRouteEntity getFlightRoute() {
        return flightRoute;
    }

    public void setFlightRoute(FlightRouteEntity flightRoute) {
        this.flightRoute = flightRoute;
    }

    public AircraftConfigurationEntity getAircraftConfig() {
        return aircraftConfig;
    }

    public void setAircraftConfig(AircraftConfigurationEntity aircraftConfig) {
        this.aircraftConfig = aircraftConfig;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public FlightEntity getReturnFlight() {
        return returnFlight;
    }

    public void setReturnFlight(FlightEntity returnFlight) {
        this.returnFlight = returnFlight;
    }

    public List<FlightSchedulePlanEntity> getListOfFlightSchedulePlan() {
        return listOfFlightSchedulePlan;
    }

    public void setListOfFlightSchedulePlan(List<FlightSchedulePlanEntity> listOfFlightSchedulePlan) {
        this.listOfFlightSchedulePlan = listOfFlightSchedulePlan;
    }

}
