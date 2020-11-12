/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.FlightReservationSessionBeanLocal;
import entity.FlightEntity;
import entity.FlightReservationEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.IndividualFlightReservationEntity;
import entity.PartnerEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.AccessFromWrongPortalException;
import util.exception.CustomerHasNoReservationException;
import util.exception.CustomerLoginInvalid;
import util.exception.FlightReservationDoesNotExistException;

/**
 *
 * @author nickg
 */
@WebService(serviceName = "PartnerReservationSystem")
@Stateless
public class PartnerReservationSystem {

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @EJB
    private FlightReservationSessionBeanLocal flightReservationSessionBean;

    @WebMethod(operationName = "retrieveListOfReservation")
    public List<FlightReservationEntity> retrieveListOfReservation(@WebParam(name = "custId") Long custId) throws CustomerHasNoReservationException {
        List<FlightReservationEntity> listOfFlightReservation = flightReservationSessionBean.retrieveListOfUnmanagedReservation(custId);
        for (FlightReservationEntity fr : listOfFlightReservation) {
            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                indivFr.setFlightReservation(null);
            }
        }
        return listOfFlightReservation;
    }

    @WebMethod(operationName = "loginPartner")
    public PartnerEntity loginPartner(String loginId, String loginPw) throws CustomerLoginInvalid, AccessFromWrongPortalException {
        PartnerEntity partner = (PartnerEntity) customerSessionBean.customerLoginUnmanaged(loginId, loginPw);

        return partner;
    }

    @WebMethod(operationName = "retrieveIndividualFlightReservation")
    public FlightReservationEntity retrieveIndividualFlightReservation(Long frId) throws FlightReservationDoesNotExistException {
        FlightReservationEntity fr = flightReservationSessionBean.getIndividualFlightReservationUnmanaged(frId);

        for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
            indivFr.setFlightReservation(null);
            //flightscheudle - flight || flight route - flight route || flight - flight || flightschedule - flightscheduleplan || 
            FlightScheduleEntity fs = indivFr.getFlightSchedule();
            FlightSchedulePlanEntity fsp = fs.getFlightSchedulePlan();
            fsp.getListOfFlightSchedule().clear(); // clear all associations to list of FS
            fsp.setReturnFlightSchedulePlan(null);

            FlightEntity flight = fsp.getFlightEntity();
            flight.setReturnFlight(null);
            flight.getListOfFlightSchedulePlan().clear(); // clear all associations to list of FSP

            FlightRouteEntity flightRoute = flight.getFlightRoute();
            flightRoute.setReturnRoute(null);

        }

        return fr;

    }

}
