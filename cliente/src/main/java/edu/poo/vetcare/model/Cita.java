package edu.poo.vetcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a una cita veterinaria tal como la expone la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cita {

    private Integer id;

    /** Fecha en formato AAAA-MM-DD, tal como la envía y recibe la API. */
    private String fecha;

    private String motivo;
    private String veterinario;
    private String estado = "pendiente";
    private double costo;

    @JsonProperty("mascota_id")
    private int mascotaId;

    /** Constructor vacío requerido por Jackson. */
    public Cita() {
    }

    /**
     * Crea una cita pendiente que aún no está registrada en la API.
     *
     * @param fecha       fecha en formato AAAA-MM-DD
     * @param motivo      motivo de la consulta
     * @param veterinario nombre del veterinario
     * @param costo       costo de la consulta
     * @param mascotaId   identificador de la mascota
     */
    public Cita(String fecha, String motivo, String veterinario,
                double costo, int mascotaId) {
        this.fecha = fecha;
        this.motivo = motivo;
        this.veterinario = veterinario;
        this.costo = costo;
        this.mascotaId = mascotaId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(int mascotaId) {
        this.mascotaId = mascotaId;
    }

    @Override
    public String toString() {
        return "[%d] %s - %s - %s - $%.0f - mascota %d - %s"
                .formatted(id, fecha, motivo, veterinario, costo, mascotaId, estado);
    }
}