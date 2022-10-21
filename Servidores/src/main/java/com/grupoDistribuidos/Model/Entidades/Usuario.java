package com.grupoDistribuidos.Model.Entidades;

public class Usuario {
    private String username;
    private String pasword;
    public Usuario(String username, String pasword) {
        this.username = username;
        this.pasword = pasword;
    }
    public Usuario() {
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPasword(String pasword) {
        this.pasword = pasword;
    }
    public String getUsername() {
        return username;
    }
    public String getPasword() {
        return pasword;
    }
}
