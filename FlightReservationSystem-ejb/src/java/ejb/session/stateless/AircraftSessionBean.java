/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.CabinClassConfigurationEntity;
import entity.SeatEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CabinClassExceedMaxCapacity;

/**
 *
 * @author sohqi
 */
@Stateless
public class AircraftSessionBean implements AircraftSessionBeanRemote, AircraftSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    public Long createAircraftConfiguration(AircraftConfigurationEntity aircraftConfigurationEntity, AircraftTypeEntity aircarAircraftTypeEntity, List<CabinClassConfigurationEntity> listOfCabinClassConfig) {
        int totalCabinPeople = 0;
        em.persist(aircarAircraftTypeEntity);
        em.flush();
        aircraftConfigurationEntity.setAircraftType(aircarAircraftTypeEntity);
        em.persist(aircraftConfigurationEntity);
        em.flush();
        //check all the cabin class configuration greater than the aircraftconfiguration max seating capacity
        for (CabinClassConfigurationEntity cabin : listOfCabinClassConfig) {
            String[] getSeatingPerRow = cabin.getSeatingConfig().split("-");
            int numPplPerRow = 0;
            for (int i = 0; i < getSeatingPerRow.length; i++) {
                numPplPerRow += Integer.parseInt(getSeatingPerRow[i]);
            }
            int maxPerCabinType = numPplPerRow * cabin.getNumRows();
            cabin.setAvailableSeats(maxPerCabinType);
            cabin.setBalancedSeats(maxPerCabinType);
            cabin.setReservedSeats(0);
            
            em.persist(cabin);
            
        }

        // aircraftConfigurationEntity.setCabinClasses(listOfCabinClassConfig);
        //aircraftConfigurationEntity.setCabinClasses(listOfCabinClassConfig);
        //generate seat into the database
        SeatEntity s = null;
        String cabinCol = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        /*for (CabinClassConfigurationEntity cabin : aircraftConfigurationEntity.getCabinClasses()) {

            String[] getSeatingPerRow = cabin.getSeatingConfig().split("-");
            int numPplPerRow = 0;
            for (int i = 0; i < getSeatingPerRow.length; i++) {
                numPplPerRow += Integer.parseInt(getSeatingPerRow[i]);
            }
            for (int noOfRow = 0; noOfRow < cabin.getNumRows(); noOfRow++) {
                for (int col = 0; col < numPplPerRow; col++) {
                    s = new SeatEntity();
                    s.setSeatNumber((noOfRow + 1) + "" + cabinCol.charAt(col));
                    s.setReserved(false);
                    s.setCabinType(cabin.getCabinclassType());
                    em.persist(s);
                    em.flush();
                }
            }

        }*/
        return aircraftConfigurationEntity.getAircraftConfigId();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<AircraftConfigurationEntity> viewAircraftConfiguration() {
        Query query = em.createQuery("SELECT a from AircraftConfigurationEntity a order by a.aircraftType asc, a.aircraftName asc");
        List<AircraftConfigurationEntity> aircraftConfiguration = query.getResultList();

        return aircraftConfiguration;
    }
}
