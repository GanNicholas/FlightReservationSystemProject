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
    private FlightScheduleEntity flightSchedule;
    
    @OneToMany()
    @JoinColumn(nullable = false)
    private List<PassengerEntity> listOfPassenger;
    
    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private PartnerEntity partnerInfo;
    
    private List<SeatEntity> listOfSeats;
    
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, scale = 2 )
    private BigDecimal amount;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightReservationEntity flightReservation;

    public IndividualFlightReservationEntity() {
        this.listOfPassenger = new ArrayList<>();
        this.listOfSeats = new ArrayList<>();
    }

    public IndividualFlightReservationEntity(FlightScheduleEntity flightSchedule, PartnerEntity partnerInfo, BigDecimal amount, FlightReservationEntity flightReservation) {
        this();
        this.flightSchedule = flightSchedule;
        this.partnerInfo = partnerInfo;
        this.amount = amount;
        this.flightReservation = flightReservation;
    }

    public Long getIndivFlightResId() {
        return indivFlightResId;
    }

    public void setIndivFlightResId(Long indivFlightResId) {
        this.indivFlightResId = indivFlightResId;
    }

    public List<PassengerEntity> getlistOfPassenger() {
        return listOfPassenger;
    }

    public void setlistOfPassenger(List<PassengerEntity> listOfPassenger) {
        this.listOfPassenger = listOfPassenger;
    }

    public PartnerEntity getPartnerInfo() {
        return partnerInfo;
    }

    public void setPartnerInfo(PartnerEntity partnerInfo) {
        this.partnerInfo = partnerInfo;
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

}
