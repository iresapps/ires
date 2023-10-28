package com.example.ires.firebase;

public enum Incidents {
    fire(0),
    crimes(1),
    medical_emergencies(2);

    public int value;

    Incidents(int value) {
        this.value = value;
    }
}
