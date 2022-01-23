package de.nx74205.idcharge.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LocalChargeData implements Serializable {

    private Integer chargeId;

    private LocalDateTime timeStamp;

    private Long mileage;

    private Long distance;

    private Double chargedKwPaid;

    private Double price;

    private Integer targetSoc;

    private String chargeTyp;

    private Double bcConsumption;

    private int viewPosition;

    public LocalChargeData() {

    }

    public LocalChargeData(LocalDateTime timeStamp, long mileage, double chargedKwPaid, double price,
                           int targetSoc, String chargeTyp, double bcConsumption) {
        this.timeStamp = timeStamp;
        this.mileage = mileage;
        this.chargedKwPaid = chargedKwPaid;
        this.price = price;
        this.targetSoc = targetSoc;
        this.chargeTyp = chargeTyp;
        this.bcConsumption = bcConsumption;
    }

    public Integer getChargeId() {
        return chargeId;
    }

    public void setChargeId(Integer chargeId) {
        this.chargeId = chargeId;
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
}
