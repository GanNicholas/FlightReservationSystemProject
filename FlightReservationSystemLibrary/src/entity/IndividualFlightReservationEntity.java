/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nickg
 */
@Entity
public class IndividualFlightReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long indivFlightResId;

    @NotNull
    @OneToOne(optional = false, cascade = {CascadeType.DETACH})
    private FlightScheduleEntity flightSchedule;

    @OneToMany(cascade = {CascadeType.DETACH})
    private List<PassengerEntity> listOfPassengers;

    @OneToOne(optional = false, cascade = {CascadeType.DETACH})
    @JoinColumn(nullable = false)
    private CustomerEntity customerInfo;

    @OneToMany(cascade = {CascadeType.DETACH})
    //@JoinColumn(nullable = false)
    private List<SeatEntity> listOfSeats;

    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, scale = 2)
    private BigDecimal amount;

    @ManyToOne(optional = false, cascade = {CascadeType.DETACH})
    @JoinColumn(nullable = false)
    private FlightReservationEntity flightReservation;
    
   

    public IndividualFlightReservationEntity() {
        this.listOfPassengers = new ArrayList<>();
        this.listOfSeats = new ArrayList<>();
        
    }

    public IndividualFlightReservationEntity(FlightScheduleEntity flightSchedule, CustomerEntity customerInfo, BigDecimal amount, FlightReservationEntity flightReservation) {
        this();
        this.flightSchedule = flightSchedule;
        this.customerInfo = customerInfo;
        this.amount = amount;
        this.flightReservation = flightReservation;
    }

    public Long getIndivFlightResId() {
        return indivFlightResId;
    }

    public void setIndivFlightResId(Long indivFlightResId) {
        this.indivFlightResId = indivFlightResId;
    }

    public List<PassengerEntity> getListOfPassenger() {
        return listOfPassengers;
    }

    public void setListOfPassenger(List<PassengerEntity> listOfPassenger) {
        this.listOfPassengers = listOfPassenger;
    }

    public CustomerEntity getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerEntity customerInfo) {
        this.customerInfo = customerInfo;
    }

    public List<SeatEntity> getListOfSeats() {
        return listOfSeats;
    }

    public void setListOfSeats(List<SeatEntity> listOfSeats) {
        this.listOfSeats = listOfSeats;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (indivFlightResId != null ? indivFlightResId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the indivFlightResId fields are not set
        if (!(object instanceof IndividualFlightReservationEntity)) {
            return false;
        }
        IndividualFlightReservationEntity other = (IndividualFlightReservationEntity) object;
        if ((this.indivFlightResId == null && other.indivFlightResId != null) || (this.indivFlightResId != null && !this.indivFlightResId.equals(other.indivFlightResId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.IndividualFlightReservationEntity[ id=" + indivFlightResId + " ]";
    }


    public FlightScheduleEntity getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightScheduleEntity flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    public FlightReservationEntity getFlightReservation() {
        return flightReservation;
    }

    public void setFlightReservation(FlightReservationEntity flightReservation) {
        this.flightReservation = flightReservation;
    }

}
