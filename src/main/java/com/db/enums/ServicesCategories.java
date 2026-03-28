package com.db.enums;

import lombok.Getter;

@Getter
public enum ServicesCategories {
    ELECTRICIAN("Electrician"),
    PLUMBING("Plumbing"),
    CLEANING("Cleaning"),
    CAR_PENTER("Car Penter");

    private final String value;
    ServicesCategories(String value){
        this.value=value;
    }



}
