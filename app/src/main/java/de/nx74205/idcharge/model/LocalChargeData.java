package de.nx74205.idcharge.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LocalChargeData implements Serializable {

    private Integer chargeId;
    private String vin;
    private LocalDateTime timeStamp;
    private Long mileage;
    private Long distance;
    private Double chargedKwPaid;
    private Double price;
    private Integer targetSoc;
    private String chargeTyp;
    private Double bcConsumption;
    private int viewPosition;
    private Integer chargeDataId;
    private String recordStatus;
    public LocalChargeData() {

    }

    public Integer getChargeId() {
        return chargeId;
    }

    public void setChargeId(Integer chargeId) {
        this.chargeId = chargeId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public Long getMileage() {
        return mileage;
    }

    public Double getChargedKwPaid() {
        return chargedKwPaid;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getTargetSoc() {
        return targetSoc;
    }

    public String getChargeTyp() {
        return chargeTyp;
    }

    public Double getBcConsumption() {
        return bcConsumption;
    }

    public int getViewPosition() {
        return viewPosition;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }

    public void setChargedKwPaid(Double chargedKwPaid) {
        this.chargedKwPaid = chargedKwPaid;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setTargetSoc(Integer targetSoc) {
        this.targetSoc = targetSoc;
    }

    public void setChargeTyp(String chargeTyp) {
        this.chargeTyp = chargeTyp;
    }

    public void setBcConsumption(Double bcConsumption) {
        this.bcConsumption = bcConsumption;
    }

    public void setViewPosition(int viewPosition) {
        this.viewPosition = viewPosition;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Integer getChargeDataId() {
        return chargeDataId;
    }

    public void setChargeDataId(Integer chargeDataId) {
        this.chargeDataId = chargeDataId;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
}
