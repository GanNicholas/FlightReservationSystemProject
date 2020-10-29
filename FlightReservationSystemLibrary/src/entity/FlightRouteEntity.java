/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author sohqi
 */
@Entity
@NamedQuery(name = "findFlightRoute", query = "SELECT a FROM FlightRouteEntity a WHERE a.originLocation =:origin AND a.destinationLocation =:destination")
public class FlightRouteEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightRouteId;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private AirportEntity originLocation;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private AirportEntity destinationLocation;

    private boolean isDeleted;

    private FlightRouteEntity returnRoute;

    public FlightRouteEntity() {

    }

    public FlightRouteEntity(AirportEntity originLocation, AirportEntity destinationLocation, FlightRouteEntity returnRoute) {
        this.originLocation = originLocation;
        this.destinationLocation = destinationLocation;
        this.returnRoute = returnRoute;
    }

    public Long getFlightRouteId() {
        return flightRouteId;
    }

    public void setFlightRouteId(Long flightRouteId) {
        this.flightRouteId = flightRouteId;
    }

    public AirportEntity getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(AirportEntity originLocation) {
        this.originLocation = originLocation;
    }

    public AirportEntity getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(AirportEntity destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public FlightRouteEntity getReturnRoute() {
        return returnRoute;
    }

    public void setReturnRoute(FlightRouteEntity returnRoute) {
        this.returnRoute = returnRoute;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getFlightRouteId() != null ? getFlightRouteId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightRouteId fields are not set
        if (!(object instanceof FlightRouteEntity)) {
            return false;
        }
        FlightRouteEntity other = (FlightRouteEntity) object;
        if ((this.getFlightRouteId() == null && other.getFlightRouteId() != null) || (this.getFlightRouteId() != null && !this.flightRouteId.equals(other.flightRouteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightRouteEntity[ id=" + getFlightRouteId() + " ]";
    }

}
