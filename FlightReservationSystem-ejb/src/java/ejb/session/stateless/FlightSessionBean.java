/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import java.util.List;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.FlightExistsException;
import util.exception.FlightRecordIsEmptyException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightSessionBean implements FlightSessionBeanRemote, FlightSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public FlightEntity createFlightWithoutReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig) throws FlightExistsException {
        try {
            FlightEntity flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            if (flight != null) {
                throw new FlightExistsException("Flight with same flight number exists!");
            }
        } catch (NoResultException ex) {

        }

        FlightEntity newFlight = new FlightEntity(flightNumber, flightRoute, aircraftConfig);
        em.persist(newFlight);
        em.flush();
        return newFlight;
    }

    @Override
    public FlightEntity createFlightWithReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, FlightEntity returnFlight) throws FlightExistsException {
        try {
            FlightEntity flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            if (flight != null) {
                throw new FlightExistsException("Flight with same flight number exists!");
            }
        } catch (NoResultException ex) {

        }

        FlightEntity newFlight = new FlightEntity(flightNumber, flightRoute, aircraftConfig);
        newFlight.setReturnFlight(returnFlight);
        em.persist(newFlight);
        em.flush();
        return newFlight;
    }

    public List<FlightEntity> viewAllFlights() throws FlightRecordIsEmptyException {
        List<FlightEntity> listOfFlightEntity = em.createQuery("SELECT c FROM FlightEntity c ORDER BY c.flightNumber").getResultList();
        if (listOfFlightEntity.size() == 0) {
            throw new FlightRecordIsEmptyException("No flight record exists!");
        }

        List<FlightEntity> listOfAllFlights = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < listOfFlightEntity.size(); i++) {
            if (listOfFlightEntity.get(i).getReturnFlight() != null) {
                listOfAllFlights.add(counter, listOfAllFlights.get(i));
                listOfAllFlights.add(counter + 1, listOfAllFlights.get(i).getReturnFlight());
                counter += 2;
            } else {
                listOfAllFlights.add(counter, listOfAllFlights.get(i));
                counter++;
            }
        }

        return listOfAllFlights;
    }
}
