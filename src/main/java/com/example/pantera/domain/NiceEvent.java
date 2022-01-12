package com.example.pantera.domain;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Timer;

public class NiceEvent extends Entity<Long> {
    private String nameEvent;
    private String createdBy;
    private String dateTime;
    private String hours;

    public NiceEvent(String nameEvent, String createdBy, String dateTime, String hours){
        this.nameEvent = nameEvent;
        this.createdBy = createdBy;
        this.dateTime = dateTime;
        this.hours = hours;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public String getHours() {
        return hours;
    }

    public void setNameEvent(String nameEvent) {
        this.nameEvent = nameEvent;
    }

    public void setHours(String hours) { this.hours = hours;}

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}