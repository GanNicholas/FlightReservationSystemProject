/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightReservationEntity;
import entity.IndividualFlightReservationEntity;
import entity.PassengerEntity;
import entity.SeatEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.CustomerHasNoReservationException;
import util.exception.FlightReservationDoesNotExistException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightReservationSessionBean implements FlightReservationSessionBeanRemote, FlightReservationSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void reserveFlights(List<FlightReservationEntity> listOfFlightRes) {
        for (FlightReservationEntity flightRes : listOfFlightRes) {
            for (IndividualFlightReservationEntity indivFlightRes : flightRes.getListOfIndividualFlightRes()) {
                em.merge(indivFlightRes.getFlightSchedule());

                for (SeatEntity seat : indivFlightRes.getListOfSeats()) {
                    em.persist(seat.getFare());
                    em.merge(seat);
                }

                for (PassengerEntity passenger : indivFlightRes.getListOfPassenger()) {
                    em.persist(passenger);
                }

                em.persist(indivFlightRes);
            }

            em.persist(flightRes);
        }

    }

    @Override
    public List<FlightReservationEntity> retrieveListOfReservation(Long custId) throws CustomerHasNoReservationException {
        List<FlightReservationEntity> listOfFlightReservation = em.createQuery("SELECT f FROM FlightReservationEntity f WHERE f.customer.customerId =:custId").setParameter("custId", custId).getResultList();

        if (listOfFlightReservation.size() == 0) {
            throw new CustomerHasNoReservationException("Customer does not have any reservation!");
        } else {

            for (FlightReservationEntity fr : listOfFlightReservation) {
                fr.getCustomer();
                fr.getListOfIndividualFlightRes().size();
            }

            return listOfFlightReservation;
        }

    }

    @Override
    public FlightReservationEntity getIndividualFlightReservation(Long frId) throws FlightReservationDoesNotExistException {
        FlightReservationEntity fr = em.find(FlightReservationEntity.class, frId);

        if (fr == null) {
            throw new FlightReservationDoesNotExistException("Flight Reservation does not exist!");
        } else {

            fr.getListOfIndividualFlightRes().size();
            fr.getCustomer();

            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                indivFr.getCustomerInfo();
                indivFr.getFlightReservation();
                indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation();
                indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();
                indivFr.getListOfPassenger().size();
                indivFr.getListOfSeats().size();
            }

            return fr;
        }
    }

    @Override
    public List<FlightReservationEntity> retrieveListOfUnmanagedReservation(Long custId) throws CustomerHasNoReservationException {
        List<FlightReservationEntity> listOfFlightReservation = em.createQuery("SELECT f FROM FlightReservationEntity f WHERE f.customer.customerId =:custId").setParameter("custId", custId).getResultList();

        if (listOfFlightReservation.size() == 0) {
            throw new CustomerHasNoReservationException("Customer does not have any reservation!");
        } else {

            for (FlightReservationEntity fr : listOfFlightReservation) {
                em.detach(fr);
                fr.getCustomer();
                em.detach(fr.getCustomer());
                fr.getListOfIndividualFlightRes().size();
                for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                    em.detach(indivFr);
                    em.detach(indivFr.getFlightSchedule());
                    for (PassengerEntity passenger : indivFr.getListOfPassenger()) {
                        em.detach(passenger);
                    }
                    for (SeatEntity seat : indivFr.getListOfSeats()) {
                        em.detach(seat);
                    }

                }

            }

            return listOfFlightReservation;
        }

    }

    @Override
    public FlightReservationEntity getIndividualFlightReservationUnmanaged(Long frId) throws FlightReservationDoesNotExistException {
        FlightReservationEntity fr = em.find(FlightReservationEntity.class, frId);

        if (fr == null) {
            throw new FlightReservationDoesNotExistException("Flight Reservation does not exist!");
        } else {

            fr.getListOfIndividualFlightRes().size();
            fr.getCustomer();

            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                indivFr.getCustomerInfo();
                indivFr.getFlightReservation();
                indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation();
                indivFr.getFlightSchedule();
                indivFr.getFlightSchedule().getFlightSchedulePlan();
                indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity();
                indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute();
                indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation();
                indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();
                indivFr.getListOfPassenger().size();
                indivFr.getListOfSeats().size();
            }
            em.detach(fr);
            return fr;
        }
    }

}
