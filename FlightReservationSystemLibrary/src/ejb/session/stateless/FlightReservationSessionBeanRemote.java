/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightReservationEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CustomerHasNoReservationException;
import util.exception.FlightReservationDoesNotExistException;

/**
 *
 * @author nickg
 */
@Remote
public interface FlightReservationSessionBeanRemote {

    public void reserveFlights(List<FlightReservationEntity> listOfFlightRes);

    public List<FlightReservationEntity> retrieveListOfReservation(Long custId) throws CustomerHasNoReservationException;

    public FlightReservationEntity getIndividualFlightReservation(Long frId) throws FlightReservationDoesNotExistException;
}
