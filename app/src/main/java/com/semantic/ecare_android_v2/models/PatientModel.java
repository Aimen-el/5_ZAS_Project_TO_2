package com.semantic.ecare_android_v2.models;

/**
 * Created by ennajihihoussame on 22/10/2017.
 */

public class PatientModel {
    String name;
    String address;

    public PatientModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
