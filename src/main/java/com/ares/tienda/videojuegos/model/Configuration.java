package com.ares.tienda.videojuegos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Configuration {

    private boolean initialRecords;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public Configuration() {
    }

    public boolean isInitialRecords() {
        return initialRecords;
    }

    public void setInitialRecords(boolean initialRecords) {
        this.initialRecords = initialRecords;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}