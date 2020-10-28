/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @NotEmpty(message = "Airport name cannot be empty!")
    @Size(min = 1, max = 100, message = "Airport name should not exceed 100 characters!")
    @Column(nullable = false, length = 100)
    private String airportName;

    @NotNull
    @Column(nullable = false, length = 3, unique = true)
    private String iataAirPortCode;

    @Size(min = 0, max = 55, message = "Flight number should not exceed 100 characters!")
    private String state;

    @NotEmpty(message = "Country name cannot be empty!")
    @Size(min = 1, max = 57, message = "Country name should not exceed 57 characters!")
    @Column(nullable = false, length = 57)
    private String country;

    @Max(value = 28, message = "Maximum time zone is 28")
    private Integer timeZone;

    @Size(max = 28, message = "Country name should not exceed 28 characters!")
    private String city;

    public AirportEntity() {
    }

    public AirportEntity(String airportName, String iataAirPortCode, String state, String country, int timeZone, String city) {
        this.airportName = airportName;
        this.iataAirPortCode = iataAirPortCode;
        this.state = state;
        this.country = country;
        this.timeZone = timeZone;
        this.city = city;
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

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
