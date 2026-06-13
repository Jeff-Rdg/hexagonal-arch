package br.com.jrodrigues.hexagonal.application.core.domain;

public class Address {

    private String street;

    private String city;

    private String state;

    private String number;

    public Address(String street, String city, String state, String number) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.number = number;
    }

    public Address(){

    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
