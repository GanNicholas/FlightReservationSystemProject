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

/**
 *
 * @author sohqi
 */
@Entity
public class AirportEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long airportId;
    private String airportName;
    private String iataAirPortCode;
    private String state;
    private String country;
    private Integer timeZoneHour;
    private Integer timeZoneMin;

    public AirportEntity() {
    }

    public AirportEntity(String airportName, String iataAirPortCode, String state, String country, Integer timeZoneHour, Integer timezoneMin) {
        this.airportName = airportName;
        this.iataAirPortCode = iataAirPortCode;
        this.state = state;
        this.country = country;
        this.timeZoneHour = timeZoneHour;
        this.timeZoneMin = timezoneMin;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getAirportId() != null ? getAirportId().hashCode() : 0);
        return hash;
    }

    public Long getAirportId() {
        return airportId;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public Integer getTimeZoneHour() {
        return timeZoneHour;
    }

    public void setTimeZoneHour(Integer timeZoneHour) {
        this.timeZoneHour = timeZoneHour;
    }

    public Integer getTimeZoneMin() {
        return timeZoneMin;
    }

    public void setTimeZoneMin(Integer timeZoneMin) {
        this.timeZoneMin = timeZoneMin;
    }

    public String getIataAirPortCode() {
        return iataAirPortCode;
    }

    public void setIataAirPortCode(String iataAirPortCode) {
        this.iataAirPortCode = iataAirPortCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

   

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the airportId fields are not set
        if (!(object instanceof AirportEntity)) {
            return false;
        }
        AirportEntity other = (AirportEntity) object;
        if ((this.getAirportId() == null && other.getAirportId() != null) || (this.getAirportId() != null && !this.airportId.equals(other.airportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AirportEntity[ id=" + getAirportId() + " ]";
    }
}
