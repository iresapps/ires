package com.example.ires.firebase;

public enum Incidents {
    fire(1),
    crimes(2),
    medical_emergencies(3);

    public final int value;

    Incidents(final int value) {
        this.value = value;
    }
}
