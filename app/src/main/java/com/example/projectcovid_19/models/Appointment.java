package com.example.projectcovid_19.models;

public class Appointment {

    private String email;
    private int idCenter;
    private String dose1, dose2;

    public Appointment(){

    }

    public Appointment(String email, String dose1, String dose2){
        this.email = email;
        this.dose1 = dose1;
        this.dose2 = dose2;
    }

    public String getEmail() {
        return email;
    }

    public String getDose1() {
        return dose1;
    }

    public String getDose2() {
        return dose2;
    }
}
