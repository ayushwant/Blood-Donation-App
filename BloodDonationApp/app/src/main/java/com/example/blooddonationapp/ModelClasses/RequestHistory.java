package com.example.blooddonationapp.ModelClasses;

public class RequestHistory {

    private String userPhone;
    private String patientName;
    private String patientBloodGrp;
    private long requiredUnits;
    private String location;
    private String status;

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientBloodGrp() {
        return patientBloodGrp;
    }

    public void setPatientBloodGrp(String patientBloodGrp) {
        this.patientBloodGrp = patientBloodGrp;
    }

    public long getRequiredUnits() {
        return requiredUnits;
    }

    public void setRequiredUnits(long requiredUnits) {
        this.requiredUnits = requiredUnits;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
