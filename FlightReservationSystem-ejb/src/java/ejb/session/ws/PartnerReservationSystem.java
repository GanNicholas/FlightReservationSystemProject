/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.FlightReservationSessionBeanLocal;
import ejb.session.stateless.FlightRouteSessionBeanLocal;
import ejb.session.stateless.FlightScheduleSessionBeanLocal;
import entity.CustomerEntity;
import entity.FRSCustomerEntity;
import entity.FareEntity;
import entity.FlightBundle;
import entity.FlightEntity;
import entity.FlightReservationEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.IndividualFlightReservationEntity;
import entity.MultipleFlightScheduleEntity;
import entity.PartnerEntity;
import entity.PassengerEntity;
import entity.RecurringScheduleEntity;
import entity.RecurringWeeklyScheduleEntity;
import entity.SingleFlightScheduleEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.CabinClassType;
import util.exception.AccessFromWrongPortalException;
import util.exception.CustomerHasNoReservationException;
import util.exception.CustomerLoginInvalid;
import util.exception.FlightReservationDoesNotExistException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.IncorrectFormatException;

/**
 *
 * @author nickg
 */
@WebService(serviceName = "PartnerReservationSystem")
@Stateless
public class PartnerReservationSystem {

    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBean;

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private FlightReservationSessionBeanLocal flightReservationSessionBean;

    @WebMethod(operationName = "retrieveListOfReservation")
    public List<FlightReservationEntity> retrieveListOfReservation(@WebParam(name = "custId") Long custId) throws CustomerHasNoReservationException {
        List<FlightReservationEntity> listOfFlightReservation = flightReservationSessionBean.retrieveListOfUnmanagedReservation(custId);
        for (FlightReservationEntity fr : listOfFlightReservation) {
            fr.getCustomer().getListOfFlightReservation().clear();
            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                indivFr.setFlightReservation(null);
                indivFr.setCustomerInfo(null);
                indivFr.setFlightSchedule(null);
            }
        }
        return listOfFlightReservation;
    }

    @WebMethod(operationName = "loginPartner")
    public PartnerEntity loginPartner(String loginId, String loginPw) throws CustomerLoginInvalid, AccessFromWrongPortalException {
        PartnerEntity partner = (PartnerEntity) customerSessionBean.customerLoginUnmanaged(loginId, loginPw);
        List<FlightReservationEntity> listOfFlightRes = partner.getListOfFlightReservation();
        for (FlightReservationEntity fr : listOfFlightRes) {
            fr.setCustomer(null);
            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                indivFr.setFlightReservation(null);
                indivFr.setCustomerInfo(null);
                indivFr.setFlightSchedule(null);
            }
        }
        return partner;
    }

    @WebMethod(operationName = "retrieveIndividualFlightReservation")
    public FlightReservationEntity retrieveIndividualFlightReservation(Long frId) throws FlightReservationDoesNotExistException {
        FlightReservationEntity fr = flightReservationSessionBean.getIndividualFlightReservationUnmanaged(frId);
        fr.getCustomer().getListOfFlightReservation().clear();

        for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
            indivFr.setFlightReservation(null);
            indivFr.setCustomerInfo(null);
            //flightscheudle - flight || flight route - flight route || flight - flight || flightschedule - flightscheduleplan || 
            FlightScheduleEntity fs = indivFr.getFlightSchedule();
            FlightSchedulePlanEntity fsp = fs.getFlightSchedulePlan();
            if (fsp instanceof SingleFlightScheduleEntity) {
                fs.setFlightSchedulePlan((SingleFlightScheduleEntity) fsp);
            } else if (fsp instanceof MultipleFlightScheduleEntity) {
                fs.setFlightSchedulePlan((MultipleFlightScheduleEntity) fsp);
            } else if (fsp instanceof RecurringScheduleEntity) {
                fs.setFlightSchedulePlan((RecurringScheduleEntity) fsp);
            } else if (fsp instanceof RecurringWeeklyScheduleEntity) {
                fs.setFlightSchedulePlan((RecurringWeeklyScheduleEntity) fsp);
            }

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

    @WebMethod(operationName = "getDirectFlight")
    public List<FlightBundle> getDirectFlight(GregorianCalendar gStart, GregorianCalendar gEnd, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.getDirectFlightUnmanaged(gStart, gEnd, departureAirport, destinationAirport);
        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;

    }

    @WebMethod(operationName = "retrieveOD")
    public void retrieveOD(@WebParam(name = "oIataCode") String oIataCode) throws FlightRouteDoesNotExistException {
        flightRouteSessionBean.retrieveOD(oIataCode);
    }

    @WebMethod(operationName = "listOfConnectingFlightRecordsAftThreeDays")
    public List<FlightBundle> listOfConnectingFlightRecordsAftThreeDays(GregorianCalendar actual, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDaysUnmanaged(actual, departureAirport, destinationAirport);
        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;

    }

    @WebMethod(operationName = "listOfConnectingFlightRecordsLessThreeDays")
    public List<FlightBundle> listOfConnectingFlightRecordsLessThreeDays(GregorianCalendar actualDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDaysUnmanaged(actualDate, departureAirport, destinationAirport);

        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;
    }

    @WebMethod(operationName = "listOfConnectingFlightRecords")
    public List<FlightBundle> listOfConnectingFlightRecords(GregorianCalendar departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.listOfConnectingFlightRecordsUnmanaged(departureDate, departureAirport, destinationAirport);

        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;
    }

    @WebMethod(operationName = "convertCalendar")
    public GregorianCalendar convertCalendar(String dateTime) throws IncorrectFormatException {

        String[] information = dateTime.split("/");
        List<Integer> informationInteger = new ArrayList<>();
        for (String info : information) {
            informationInteger.add(Integer.parseInt(info));
        }

        if (informationInteger.size() != 3) {
            throw new IncorrectFormatException("Wrong date format!");
        }

        //NEED VALIDATE CALENDAR INPUT 
        GregorianCalendar newCalendar = new GregorianCalendar(informationInteger.get(2), (informationInteger.get(1) - 1), informationInteger.get(0), informationInteger.get(3), informationInteger.get(4));
        return newCalendar;

    }

    @WebMethod(operationName = "reserveFlightEJB")
    public void reserveFlightEJB(List<FlightReservationEntity> listOfFlightRes) {
        for (FlightReservationEntity fr : listOfFlightRes) {
            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                indivFr.setFlightReservation(fr);
            }
        }

        flightReservationSessionBean.reserveFlights(listOfFlightRes);
    }

    @WebMethod(operationName = "createFlightReservation")
    public FlightReservationEntity createFlightReservation(String originIATACode, String destinationIATACode, BigDecimal totalAmount, CustomerEntity customer) {
        FlightReservationEntity flightRes = new FlightReservationEntity(originIATACode, destinationIATACode, totalAmount, customer);
        return flightRes;
    }

    @WebMethod(operationName = "createIndivFlightRes")
    public IndividualFlightReservationEntity createIndivFlightRes(FlightScheduleEntity flightSchedule, CustomerEntity customerInfo, BigDecimal amount, FlightReservationEntity flightReservation) {
        IndividualFlightReservationEntity indivFr = new IndividualFlightReservationEntity(flightSchedule, customerInfo, amount, flightReservation);
        return indivFr;
    }

    @WebMethod(operationName = "createPassenger")
    public PassengerEntity createPassenger(String firstName, String lastName, String passportNumber) {
        PassengerEntity passenger = new PassengerEntity(firstName, lastName, passportNumber);
        return passenger;
    }

    @WebMethod(operationName = "createFare")
    public FareEntity createFare(String fareBasisCode, BigDecimal fareAmount, CabinClassType cabinType) {
        FareEntity newFare = new FareEntity(fareBasisCode, fareAmount, cabinType);
        return newFare;
    }

    @WebMethod(operationName = "convertCalendarExpiryDate")
    public GregorianCalendar convertCalendarExpiryDate(String dateTime) throws IncorrectFormatException {

        String[] information = dateTime.split("/");
        List<Integer> informationInteger = new ArrayList<>();
        for (String info : information) {
            informationInteger.add(Integer.parseInt(info));
        }

        if (informationInteger.size() < 2) {
            throw new IncorrectFormatException("Wrong date format!");
        }

        //NEED VALIDATE CALENDAR INPUT 
        GregorianCalendar newCalendar = new GregorianCalendar(informationInteger.get(2), (informationInteger.get(1) - 1), informationInteger.get(0), informationInteger.get(3), informationInteger.get(4));
        return newCalendar;

    }

//    @WebMethod(operationName = "loginCustomer")
//    public FRSCustomerEntity loginCustomer(String userId, String password) throws CustomerLoginInvalid {
//        FRSCustomerEntity customer = (FRSCustomerEntity) customerSessionBean.customerLoginUnmanaged(userId, password);
//        List<FlightReservationEntity> listOfFlightRes = customer.getListOfFlightReservation();
//        for (FlightReservationEntity fr : listOfFlightRes) {
//            fr.setCustomer(null);
//            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
//                indivFr.setFlightReservation(null);
//                indivFr.setCustomerInfo(null);
//                indivFr.setFlightSchedule(null);
////                indivFr.getFlightSchedule().getFlightSchedulePlan().getListOfFlightSchedule().clear();
//            }
//        }
//
//        System.out.println("Customer sending out to SOAP client");
//        return customer;
//    }

}
