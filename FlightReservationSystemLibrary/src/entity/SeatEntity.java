/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.CabinClassType;

/**
 *
 * @author sohqi
 */
@Entity
public class SeatEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @NotEmpty(message = "Seat Number cannot be empty!")
    @Size(min = 1, max = 6, message = "Seat number cannot exceed 6 characters long!")
    @Column(nullable = false, length = 6)
    private String seatNumber;

    private boolean reserved;

    @OneToOne(cascade = {CascadeType.DETACH})
    private PassengerEntity passenger;

    @OneToOne(cascade = {CascadeType.DETACH})
    private FareEntity fare;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CabinClassType cabinType;

    public SeatEntity() {
    }

    //initial state when seat is free
    public SeatEntity(String seatNumber, CabinClassType cabinType) {
        this.seatNumber = seatNumber;
        this.reserved = false;
        this.passenger = null;
        this.fare = null;
        this.cabinType = cabinType;

    }

    //when seat is reserved
    public SeatEntity(String seatNumber, boolean reserved, PassengerEntity passenger, FareEntity fare) {
        this.seatNumber = seatNumber;
        this.reserved = reserved;
        this.passenger = passenger;
        this.fare = fare;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (seatId != null ? seatId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the seatId fields are not set
        if (!(object instanceof SeatEntity)) {
            return false;
        }
        SeatEntity other = (SeatEntity) object;
        if ((this.seatId == null && other.seatId != null) || (this.seatId != null && !this.seatId.equals(other.seatId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Seat[ id=" + seatId + " ]";
    }

    public CabinClassType getCabinType() {
        return cabinType;
    }

    public void setCabinType(CabinClassType cabinType) {
        this.cabinType = cabinType;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public PassengerEntity getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerEntity passenger) {
        this.passenger = passenger;
    }

    public FareEntity getFare() {
        return fare;
    }

    public void setFare(FareEntity fare) {
        this.fare = fare;
    }

}
