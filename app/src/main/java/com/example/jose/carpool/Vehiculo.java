package com.example.jose.carpool;

/**
 * Created by Johnny on 27/08/2017.
 */

public class Vehiculo {
    private String idVehiculo;
    private String placa;
    private String modelo;
    private String marca;
    private String color;
    private String nasientos;

    public Vehiculo(String placa, String modelo, String marca, String color, String nasientos){
        this.placa=placa;
        this.marca=marca;
        this.modelo=modelo;
        this.color=color;
        this.nasientos=nasientos;
    }

    public Vehiculo(String idVehiculo, String placa, String modelo, String marca, String color, String nasientos){
        this.setIdVehiculo(idVehiculo);
        this.placa=placa;
        this.marca=marca;
        this.modelo=modelo;
        this.color=color;
        this.nasientos=nasientos;
    }


    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNasientos() {
        return nasientos;
    }

    public void setNasientos(String nasientos) {
        this.nasientos = nasientos;
    }

    public String getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(String idVehiculo) {
        this.idVehiculo = idVehiculo;
    }
}
