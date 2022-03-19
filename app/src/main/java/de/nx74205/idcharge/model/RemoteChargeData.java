package de.nx74205.idcharge.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RemoteChargeData implements Serializable {
    private Integer id;
    private String vehicleVin;
    private ChargingStatus chargingStatus;
    private LocalDateTime timestamp;
    private LocalDateTime startOfCharge;
    private LocalDateTime endOfCharge;
    private Double quantityChargedKwh;
    private Double averageChargingPower;
    private Integer socStart;
    private Integer socEnd;
    private Integer mobileChargeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleVin() {
        return vehicleVin;
    }

    public void setVehicleVin(String vehicleVin) {
        this.vehicleVin = vehicleVin;
    }

    public ChargingStatus getChargingStatus() {
        return chargingStatus;
    }

    public void setChargingStatus(ChargingStatus chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getStartOfCharge() {
        return startOfCharge;
    }

    public void setStartOfCharge(LocalDateTime startOfCharge) {
        this.startOfCharge = startOfCharge;
    }

    public LocalDateTime getEndOfCharge() {
        return endOfCharge;
    }

    public void setEndOfCharge(LocalDateTime endOfCharge) {
        this.endOfCharge = endOfCharge;
    }

    public Double getQuantityChargedKwh() {
        return quantityChargedKwh;
    }

    public void setQuantityChargedKwh(Double quantityChargedKwh) {
        this.quantityChargedKwh = quantityChargedKwh;
    }

    public Double getAverageChargingPower() {
        return averageChargingPower;
    }

    public void setAverageChargingPower(Double averageChargingPower) {
        this.averageChargingPower = averageChargingPower;
    }

    public Integer getSocStart() {
        return socStart;
    }

    public void setSocStart(Integer socStart) {
        this.socStart = socStart;
    }

    public Integer getSocEnd() {
        return socEnd;
    }

    public void setSocEnd(Integer socEnd) {
        this.socEnd = socEnd;
    }

    public Integer getMobileChargeId() {
        return mobileChargeId;
    }

    public void setMobileChargeId(Integer mobileChargeId) {
        this.mobileChargeId = mobileChargeId;
    }
}
