package com.example.stylematchia.model;

public class Recomendacion {

    private String id;
    private String userId;
    private String userName;
    private String userEmail;
    private String marca;
    private String estilo;
    private String mensaje;
    private String estado;
    private long fecha;

    public Recomendacion() {
    }

    public Recomendacion(String id, String userId, String userName, String userEmail,
                         String marca, String estilo, String mensaje, String estado, long fecha) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.marca = marca;
        this.estilo = estilo;
        this.mensaje = mensaje;
        this.estado = estado;
        this.fecha = fecha;
    }

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId != null ? userId : "";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName != null ? userName : "";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail != null ? userEmail : "";
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMarca() {
        return marca != null ? marca : "";
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getEstilo() {
        return estilo != null ? estilo : "";
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public String getMensaje() {
        return mensaje != null ? mensaje : "";
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado != null ? estado : "pendiente";
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
