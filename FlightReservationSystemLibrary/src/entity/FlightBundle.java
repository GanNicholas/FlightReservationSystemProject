/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import util.enumeration.CabinClassType;

/**
 *
 * @author sohqi
 */
public class FlightBundle implements  Serializable{

    private FlightScheduleEntity departOne;
    private FlightScheduleEntity departTwo;
    private FlightScheduleEntity departThree;

    private FlightScheduleEntity returnOne;
    private FlightScheduleEntity returnTwo;
    private FlightScheduleEntity returnThree;

    private CabinClassType departOneCabinClassType;
    private CabinClassType departTwoCabinClassType;
    private CabinClassType departThreeCabinClassType;

    private CabinClassType returnOneCabinClassType;
    private CabinClassType returnTwoCabinClassType;
    private CabinClassType returnThreeCabinClassType;

    private FareEntity departOneFare;
    private FareEntity departTwoFare;
    private FareEntity departThreeFare;
    
    private FareEntity returnOneFare;
    private FareEntity returnTwoFare;
    private FareEntity returnThreeFare;
    
    
    public FlightBundle() {

    }

    public FlightBundle(FlightScheduleEntity fsOne, FlightScheduleEntity fsTwo, FlightScheduleEntity fsThree, CabinClassType departOneCabinClassType, CabinClassType departTwoCabinClassType, CabinClassType departThreeCabinClassType, CabinClassType returnOneCabinClassType, CabinClassType returnTwoCabinClassType, CabinClassType returnThreeCabinClassType) {
        this.departOne = fsOne;
        this.departTwo = fsTwo;
        this.departThree = fsThree;
        this.departOneCabinClassType = departOneCabinClassType;
        this.departTwoCabinClassType = departTwoCabinClassType;
        this.departThreeCabinClassType = departThreeCabinClassType;
        this.returnOneCabinClassType = returnOneCabinClassType;
        this.returnTwoCabinClassType = returnTwoCabinClassType;
        this.returnThreeCabinClassType = returnThreeCabinClassType;

    }

    public FlightBundle(FlightScheduleEntity departOne, FlightScheduleEntity departTwo, FlightScheduleEntity departThree) {
        this.departOne = departOne;
        this.departTwo = departTwo;
        this.departThree = departThree;
    }

    public FareEntity getDepartOneFare() {
        return departOneFare;
    }

    public void setDepartOneFare(FareEntity departOneFare) {
        this.departOneFare = departOneFare;
    }

    public FareEntity getDepartTwoFare() {
        return departTwoFare;
    }

    public void setDepartTwoFare(FareEntity departTwoFare) {
        this.departTwoFare = departTwoFare;
    }

    public FareEntity getDepartThreeFare() {
        return departThreeFare;
    }

    public void setDepartThreeFare(FareEntity departThreeFare) {
        this.departThreeFare = departThreeFare;
    }

    public FareEntity getReturnOneFare() {
        return returnOneFare;
    }

    public void setReturnOneFare(FareEntity returnOneFare) {
        this.returnOneFare = returnOneFare;
    }

    public FareEntity getReturnTwoFare() {
        return returnTwoFare;
    }

    public void setReturnTwoFare(FareEntity returnTwoFare) {
        this.returnTwoFare = returnTwoFare;
    }

    public FareEntity getReturnThreeFare() {
        return returnThreeFare;
    }

    public void setReturnThreeFare(FareEntity returnThreeFare) {
        this.returnThreeFare = returnThreeFare;
    }
    
    

    public CabinClassType getDepartOneCabinClassType() {
        return departOneCabinClassType;
    }

    public void setDepartOneCabinClassType(CabinClassType departOneCabinClassType) {
        this.departOneCabinClassType = departOneCabinClassType;
    }

    public CabinClassType getDepartTwoCabinClassType() {
        return departTwoCabinClassType;
    }

    public void setDepartTwoCabinClassType(CabinClassType departTwoCabinClassType) {
        this.departTwoCabinClassType = departTwoCabinClassType;
    }

    public CabinClassType getDepartThreeCabinClassType() {
        return departThreeCabinClassType;
    }

    public void setDepartThreeCabinClassType(CabinClassType departThreeCabinClassType) {
        this.departThreeCabinClassType = departThreeCabinClassType;
    }

    public CabinClassType getReturnOneCabinClassType() {
        return returnOneCabinClassType;
    }

    public void setReturnOneCabinClassType(CabinClassType returnOneCabinClassType) {
        this.returnOneCabinClassType = returnOneCabinClassType;
    }

    public CabinClassType getReturnTwoCabinClassType() {
        return returnTwoCabinClassType;
    }

    public void setReturnTwoCabinClassType(CabinClassType returnTwoCabinClassType) {
        this.returnTwoCabinClassType = returnTwoCabinClassType;
    }

    public CabinClassType getReturnThreeCabinClassType() {
        return returnThreeCabinClassType;
    }

    public void setReturnThreeCabinClassType(CabinClassType returnThreeCabinClassType) {
        this.returnThreeCabinClassType = returnThreeCabinClassType;
    }

    public FlightScheduleEntity getReturnOne() {
        return returnOne;
    }

    public void setReturnOne(FlightScheduleEntity returnOne) {
        this.returnOne = returnOne;
    }

    public FlightScheduleEntity getReturnTwo() {
        return returnTwo;
    }

    public void setReturnTwo(FlightScheduleEntity returnTwo) {
        this.returnTwo = returnTwo;
    }

    public FlightScheduleEntity getReturnThree() {
        return returnThree;
    }

    public void setReturnThree(FlightScheduleEntity returnThree) {
        this.returnThree = returnThree;
    }

    public FlightScheduleEntity getDepartOne() {
        return departOne;
    }

    public void setDepartOne(FlightScheduleEntity departOne) {
        this.departOne = departOne;
    }

    public FlightScheduleEntity getDepartTwo() {
        return departTwo;
    }

    public void setDepartTwo(FlightScheduleEntity departTwo) {
        this.departTwo = departTwo;
    }

    public FlightScheduleEntity getDepartThree() {
        return departThree;
    }

    public void setDepartThree(FlightScheduleEntity departThree) {
        this.departThree = departThree;
    }

}
