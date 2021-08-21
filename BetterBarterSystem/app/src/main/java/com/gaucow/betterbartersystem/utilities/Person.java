package com.gaucow.betterbartersystem.utilities;

public class Person {
    private String selling;
    private String wants;
    public Person(String selling, String wants) {
        this.selling = selling;
        this.wants = wants;
    }

    public String getSelling() {
        return selling;
    }

    public String getWants() {
        return wants;
    }
}
