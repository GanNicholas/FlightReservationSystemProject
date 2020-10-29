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
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nickg
 */
@Entity
public class FlightReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightReservationId;

    @NotNull
    @Column(nullable = false, length = 3, unique = true)
    private String originIATACode;

    @NotNull
    @Column(nullable = false, length = 3, unique = true)
    private String destinationIATACode;

    @OneToMany(mappedBy = "flightReservation")
    private List<IndividualFlightReservationEntity> listOfIndividualFlightRes;
    
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CustomerEntity customer;

    public FlightReservationEntity() {
        listOfIndividualFlightRes = new ArrayList<>();
    }

    public FlightReservationEntity(String originIATACode, String destinationIATACode, BigDecimal totalAmount, CustomerEntity customer) {
        this();
        this.originIATACode = originIATACode;
        this.destinationIATACode = destinationIATACode;
        this.totalAmount = totalAmount;
        this.customer = customer;
    }

    public Long getFlightReservationId() {
        return flightReservationId;
    }

    public void setFlightReservationId(Long flightReservationId) {
        this.flightReservationId = flightReservationId;
    }

    public String getOriginIATACode() {
        return originIATACode;
    }

    public void setOriginIATACode(String originIATACode) {
        this.originIATACode = originIATACode;
    }

    public String getDestinationIATACode() {
        return destinationIATACode;
    }

    public void setDestinationIATACode(String destinationIATACode) {
        this.destinationIATACode = destinationIATACode;
    }

    public List<IndividualFlightReservationEntity> getListOfIndividualFlightRes() {
        return listOfIndividualFlightRes;
    }

    public void setListOfIndividualFlightRes(List<IndividualFlightReservationEntity> listOfIndividualFlightRes) {
        this.listOfIndividualFlightRes = listOfIndividualFlightRes;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightReservationId != null ? flightReservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightReservationId fields are not set
        if (!(object instanceof FlightReservationEntity)) {
            return false;
        }
        FlightReservationEntity other = (FlightReservationEntity) object;
        if ((this.flightReservationId == null && other.flightReservationId != null) || (this.flightReservationId != null && !this.flightReservationId.equals(other.flightReservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightReservationEntity[ id=" + flightReservationId + " ]";
    }

}
