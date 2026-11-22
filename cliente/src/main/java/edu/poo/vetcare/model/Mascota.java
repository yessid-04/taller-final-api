package edu.poo.vetcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a una mascota tal como la expone la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mascota {

    private Integer id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private double peso;

    @JsonProperty("dueno_id")
    private int duenoId;

    /** Constructor vacío requerido por Jackson. */
    public Mascota() {
    }

    /**
     * Crea una mascota que aún no está registrada en la API.
     *
     * @param nombre  nombre de la mascota
     * @param especie especie, por ejemplo Perro o Gato
     * @param raza    raza de la mascota
     * @param edad    edad en años (redondeada)
     * @param peso    peso en kilogramos
     * @param duenoId identificador del dueño
     */
    public Mascota(String nombre, String especie, String raza,
                   int edad, double peso, int duenoId) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.duenoId = duenoId;
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

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getDuenoId() {
        return duenoId;
    }

    public void setDuenoId(int duenoId) {
        this.duenoId = duenoId;
    }

    @Override
    public String toString() {
        return "[%d] %s (%s, %s) - %d años - %.1f kg - dueño %d"
                .formatted(id, nombre, especie, raza, edad, peso, duenoId);
    }
}