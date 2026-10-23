package com.ares.tienda.videojuegos.model;

import java.util.List;
import java.util.Map;

public class OrderSummary {

    private String fullName;
    private String address;
    private String province;
    private String cardHolder;
    private String cardType;
    private List<Map<String, Object>> cars;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public List<Map<String, Object>> getCars() {
        return cars;
    }

    public void setCars(List<Map<String, Object>> cars) {
        this.cars = cars;
    }
}
