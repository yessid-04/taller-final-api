package edu.poo.vetcare.repository;

import edu.poo.vetcare.model.Dueno;
import edu.poo.vetcare.model.Mascota;
import java.util.List;

/**
 * Acceso al recurso /duenos de la API.
 */
public class DuenoRepository extends ApiCrudRepository<Dueno> {

    /**
     * Crea el repositorio de dueños.
     *
     * @param apiClient cliente HTTP compartido
     */
    public DuenoRepository(ApiClient apiClient) {
        super(apiClient, "/duenos", Dueno.class);
    }

    /**
     * Consulta las mascotas que pertenecen a un dueño.
     *
     * @param duenoId identificador del dueño
     * @return mascotas del dueño
     */
    public List<Mascota> listarMascotas(int duenoId) {
        return apiClient.obtenerLista(recurso + "/" + duenoId + "/mascotas", Mascota.class);
    }
}