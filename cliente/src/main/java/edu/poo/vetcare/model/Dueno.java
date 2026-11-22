package edu.poo.vetcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Representa a un dueño de mascotas tal como lo expone la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dueno {

    private Integer id;
    private String nombre;
    private String documento;
    private String telefono;
    private String email;

    /** Constructor vacío requerido por Jackson. */
    public Dueno() {
    }

    /**
     * Crea un dueño que aún no está registrado en la API.
     *
     * @param nombre    nombre completo del dueño
     * @param documento número de documento de identidad
     * @param telefono  teléfono de contacto
     * @param email     correo electrónico
     */
    public Dueno(String nombre, String documento, String telefono, String email) {
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "[%d] %s - doc %s - tel %s - %s"
                .formatted(id, nombre, documento, telefono, email);
    }
}