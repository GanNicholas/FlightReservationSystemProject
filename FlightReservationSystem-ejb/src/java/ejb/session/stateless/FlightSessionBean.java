/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.AirportEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightSchedulePlanEntity;
import java.util.List;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.AircraftConfigurationNotExistException;
import javax.persistence.Query;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightExistsException;
import util.exception.FlightRecordIsEmptyException;
import util.exception.FlightRouteDoesNotExistException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightSessionBean implements FlightSessionBeanRemote, FlightSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public FlightEntity createFlightWithoutReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig) throws FlightExistsException, FlightRouteDoesNotExistException, AircraftConfigurationNotExistException {
        try {
            FlightEntity flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            if (flight != null) {
                throw new FlightExistsException("Flight with same flight number exists!");
            }
        } catch (NoResultException ex) {

        }

        FlightRouteEntity fr = em.find(FlightRouteEntity.class, flightRoute.getFlightRouteId());
        AircraftConfigurationEntity acc = em.find(AircraftConfigurationEntity.class, aircraftConfig.getAircraftConfigId());

        if (fr == null) {
            throw new FlightRouteDoesNotExistException("Flight route does not exist!");
        }

        if (acc == null) {
            throw new AircraftConfigurationNotExistException("Aircraft Config does not exist!");
        }

        FlightEntity newFlight = new FlightEntity(flightNumber, fr, acc);
        em.persist(newFlight);
        em.flush();

//        //linking two flights that are actually O-D and D-O, so link them to be return flights
//        AirportEntity origin = flightRoute.getOriginLocation();
//        AirportEntity destination = flightRoute.getDestinationLocation();
//        try {
//            FlightRouteEntity returnRoute = (FlightRouteEntity) em.createNamedQuery("findFlightRoute").setParameter("origin", destination).setParameter("destination", origin).getSingleResult();
//            FlightEntity returnFlight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightRoute").setParameter("flightRoute", returnRoute).getSingleResult();
//            newFlight.setReturnFlight(returnFlight);
//            returnFlight.setReturnFlight(newFlight);
//        } catch (NoResultException ex) {
//
//        }
        return newFlight;
    }

    @Override
    public FlightEntity createFlightWithReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, String mainFlightNumber) throws FlightExistsException, FlightRouteDoesNotExistException, AircraftConfigurationNotExistException, FlightDoesNotExistException {
        FlightEntity mainFlight = null;
        try {
            FlightEntity flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            throw new FlightExistsException("Flight with same flight number exists!");
        } catch (NoResultException ex) {

        }

        FlightRouteEntity fr = em.find(FlightRouteEntity.class, flightRoute.getFlightRouteId());
        AircraftConfigurationEntity acc = em.find(AircraftConfigurationEntity.class, aircraftConfig.getAircraftConfigId());

        if (fr == null) {
            throw new FlightRouteDoesNotExistException("Flight route does not exist!");
        }

        if (acc == null) {
            throw new AircraftConfigurationNotExistException("Aircraft Config does not exist!");
        }

        try {
            mainFlight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", mainFlightNumber).getSingleResult();
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight does not exist!");
        }

        FlightEntity newFlight = new FlightEntity(flightNumber, fr, acc);
        em.persist(newFlight);
        em.flush();
        mainFlight.setReturnFlight(newFlight);
        newFlight.setReturnFlight(mainFlight);
        newFlight.setIsMainRoute(false);

        return newFlight;
    }

    @Override
    public List<FlightEntity> viewAllFlights() throws FlightRecordIsEmptyException {
        List<FlightEntity> listOfFlightEntity = em.createQuery("SELECT c FROM FlightEntity c WHERE c.isDeleted = FALSE AND c.isMainRoute = TRUE ORDER BY c.flightNumber ASC").getResultList();
        if (listOfFlightEntity.isEmpty()) {
            throw new FlightRecordIsEmptyException("No flight record exists!");
        }

        List<FlightEntity> listOfAllFlights = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < listOfFlightEntity.size(); i++) {
            if (listOfFlightEntity.get(i).getReturnFlight() != null && !listOfAllFlights.contains(listOfFlightEntity.get(i).getReturnFlight())) {

                listOfAllFlights.add(counter, listOfFlightEntity.get(i));
                listOfAllFlights.add(counter + 1, listOfFlightEntity.get(i).getReturnFlight());
                counter += 2;
            } else if (!listOfAllFlights.contains(listOfFlightEntity.get(i))) {
                listOfAllFlights.add(counter, listOfFlightEntity.get(i));
                counter++;
            }
        }

        return listOfAllFlights;
    }

    @Override
    public FlightEntity viewFlightDetails(String flightNumber) throws FlightDoesNotExistException {
        try {
            FlightEntity flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            flight.getAircraftConfig().getCabinClasses().size();
            flight.getFlightRoute();

            if (flight.getReturnFlight() != null) {
                flight.getReturnFlight().getAircraftConfig().getCabinClasses().size();
                flight.getReturnFlight().getFlightRoute();
            }

            return flight;

        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with this flight number : " + flightNumber + " does not exist!");
        }

    }

    @Override
    public void updateFlight(FlightEntity flight) throws FlightDoesNotExistException {
        String flightNumber = flight.getFlightNumber();
        try {
            FlightEntity oldFlight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight does not exist!");
        }
        em.merge(flight);
        em.flush();

//        FlightEntity updatedFlight = em.find(FlightEntity.class, flight.getFlightId());
//        if(updatedFlight == null){
//            throw new FlightDoesNotExistException("Flight record does not exist!");
//        }
//        //POTENTIAL ERROR
//        updatedFlight.setAircraftConfig(flight.getAircraftConfig());
//        flight.setAircraftConfig(null);
//        
//        updatedFlight.setFlightRoute(flight.getFlightRoute());
//        updatedFlight.setIsDeleted(flight.isIsDeleted());
//        
//        List<FlightSchedulePlanEntity> listOfFSP = flight.getListOfFlightSchedulePlan();
//        for(FlightSchedulePlanEntity plan : listOfFSP){
//            updatedFlight.getListOfFlightSchedulePlan().add(plan);
//            plan.setFlightEntity(updatedFlight);
//            //check if return FSP is using same flight number
//            plan.getReturnFlightSchedulePlan().setFlightEntity(updatedFlight);
//        }
    }

    @Override
    public boolean deleteFlight(String flightNumber) throws FlightDoesNotExistException {
        try {
            FlightEntity flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            List<FlightSchedulePlanEntity> listOfFlightSchedulePlan = em.createNamedQuery("queryFSPwithFlightNumber").setParameter("flightNum", flightNumber).getResultList();
            
            if (listOfFlightSchedulePlan.isEmpty()) {
                flight.setReturnFlight(null);
                flight.setFlightRoute(null);
                flight.setAircraftConfig(null);
                em.remove(flight);
                return true;
            } else {
                flight.setIsDeleted(true);
                return true;
            }
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with this flight number : " + flightNumber + " does not exist!");
        }
    }

    public List<FlightEntity> listOfFlightRecords(String tripType, String departureAirport, String destinationAirport, String departureDate, String returnDate, String passenger) {
        Query query = em.createQuery("SELECT f FROM FlightEntity f WHERE f.listOfFlightSchedulePlan.listOfFlightSchedule.departureDateTime BETWEEN :=departureDate AND :=endDate").setParameter("departureDate", departureDate).setParameter("endDate", returnDate);
        List<FlightEntity> listOfFlightRecord = query.getResultList();
        return listOfFlightRecord;
    }
}
