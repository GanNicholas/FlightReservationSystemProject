/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.CabinClassConfigurationEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CabinClassExceedMaxCapacity;

/**
 *
 * @author sohqi
 */
@Remote
public interface AircraftSessionBeanRemote {

     public Long createAircraftConfiguration(AircraftConfigurationEntity aircraftConfigurationEntity, AircraftTypeEntity aircarAircraftTypeEntity, List<CabinClassConfigurationEntity> listOfCabinClassConfig);

    public List<AircraftConfigurationEntity> viewAircraftConfiguration();
}
