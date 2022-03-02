package com.example.blooddonationapp.ModelClasses;

public class Patient
{
    private String userName;
    private String userPhone;
    private String patientName;


    private String age;
    private String bloodGrp;
    private String requiredUnits;
    private String location;
    private String pdfUri;
    private String additionalDetails;
    private String isValid="false";



    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getValid() {
        return isValid;
    }

    public void setValid(String isValid) {
        this.isValid = isValid;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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


    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }

    public String getRequiredUnits() {
        return requiredUnits;
    }

    public void setRequiredUnits(String requiredUnits) {
        this.requiredUnits = requiredUnits;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPdfUri() {
        return pdfUri;
    }

    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }
}
