package com.geraldcode.findapp.Model;

public class Objeto {

    String idObjeto, uidUsuario, correoUsuario, fechaHoraActual, titulo, descripcion, categoria, lugarPerdida, telefonoContacto, fechaPerdida, estado, imagenObjeto;

    public Objeto(){
    }

    public Objeto(String idObjeto, String uidUsuario, String correoUsuario, String fechaHoraActual, String titulo, String descripcion, String categoria, String lugarPerdida, String telefonoContacto, String fechaPerdida, String estado, String imagenObjeto) {
        this.idObjeto = idObjeto;
        this.uidUsuario = uidUsuario;
        this.correoUsuario = correoUsuario;
        this.fechaHoraActual = fechaHoraActual;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.lugarPerdida = lugarPerdida;
        this.telefonoContacto = telefonoContacto;
        this.fechaPerdida = fechaPerdida;
        this.estado = estado;
        this.imagenObjeto = imagenObjeto;
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getFechaHoraActual() {
        return fechaHoraActual;
    }

    public void setFechaHoraActual(String fechaHoraActual) {
        this.fechaHoraActual = fechaHoraActual;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getLugarPerdida() {
        return lugarPerdida;
    }

    public void setLugarPerdida(String lugarPerdida) {
        this.lugarPerdida = lugarPerdida;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getFechaPerdida() {
        return fechaPerdida;
    }

    public void setFechaPerdida(String fechaPerdida) {
        this.fechaPerdida = fechaPerdida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagenObjeto() {
        return imagenObjeto;
    }

    public void setImagenObjeto(String imagenObjeto) {
        this.imagenObjeto = imagenObjeto;
    }
}
