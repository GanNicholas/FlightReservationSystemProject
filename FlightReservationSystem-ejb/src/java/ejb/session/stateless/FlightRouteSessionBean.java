/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.FlightRouteEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
@Stateless
public class FlightRouteSessionBean implements FlightRouteSessionBeanRemote, FlightRouteSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    public Long createFlightRoute(FlightRouteEntity frEntity) throws FlightRouteODPairExistException {
        try {
            boolean frExistInDB = checkFlightRouteOD(frEntity);
            if (!frExistInDB) {
                em.persist(frEntity);
                em.flush();
            }

        } catch (FlightRouteODPairExistException ex) {
            throw new FlightRouteODPairExistException("Flight route O-D already exist");
        }

        return frEntity.getFlightRouteId();
    }

    public boolean checkFlightRouteOD(FlightRouteEntity frEntity) throws FlightRouteODPairExistException {
        try {
            Query query = em.createQuery("SELECT f FROM FlightRouteEntity a WHERE a.originLocation.iataAirportCode=:original AND a.destinationLocation.iataAirportCode=:destination").setParameter("original", frEntity.getOriginLocation().getIataAirportCode()).setParameter("destination", frEntity.getDestinationLocation().getIataAirportCode());
            FlightRouteEntity tempFrEntity = (FlightRouteEntity) query.getSingleResult();
            throw new FlightRouteODPairExistException("Flight route O-D already exist");
        } catch (NoResultException ex) {
            return false;
        }

    }

    public List<FlightRouteEntity> viewListOfFlightRoute() {

        Query query = em.createQuery("SELECT f FROM FlightRouteEntity f");
        List<FlightRouteEntity> listOfFlightRoute = query.getResultList();
        listOfFlightRoute.size();
        return listOfFlightRoute;
    }

}
