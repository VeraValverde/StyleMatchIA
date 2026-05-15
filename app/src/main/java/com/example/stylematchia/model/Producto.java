package com.example.stylematchia.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "productos")
public class Producto implements Serializable {

    @PrimaryKey
    @NonNull
    private String id = "";

    private String nombre = "";
    private String marca = "";
    private String descripcion = "";
    private double precio = 0.0;
    private String imagenUrl = "";
    private String enlaceTienda = "";

    public Producto() {
    }

    @NonNull
    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(@NonNull String id) {
        this.id = id == null ? "" : id;
    }

    public String getNombre() {
        return nombre == null ? "" : nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? "" : nombre;
    }

    public String getMarca() {
        return marca == null ? "" : marca;
    }

    public void setMarca(String marca) {
        this.marca = marca == null ? "" : marca;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl == null ? "" : imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl == null ? "" : imagenUrl;
    }

    public String getEnlaceTienda() {
        return enlaceTienda == null ? "" : enlaceTienda;
    }

    public void setEnlaceTienda(String enlaceTienda) {
        this.enlaceTienda = enlaceTienda == null ? "" : enlaceTienda;
    }

    public String getDescripcion() {
        return descripcion == null ? "" : descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion;
    }
}
