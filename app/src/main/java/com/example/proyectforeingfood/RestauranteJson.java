package com.example.proyectforeingfood;

import com.google.gson.annotations.SerializedName;

public class RestauranteJson {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("rol")
    private String rol;

    @SerializedName("photo")
    private String urlFoto;

    @SerializedName("latitud")
    private double latitud;

    @SerializedName("longitud")
    private double longitud;

    public RestauranteJson(String nombre, String rol, String urlFoto, double latitud, double longitud) {
        this.nombre = nombre;
        this.rol = rol;
        this.urlFoto = urlFoto;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
