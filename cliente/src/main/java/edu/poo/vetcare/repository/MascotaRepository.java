package edu.poo.vetcare.repository;

import edu.poo.vetcare.model.Mascota;

/**
 * Acceso al recurso /mascotas de la API.
 */
public class MascotaRepository extends ApiCrudRepository<Mascota> {

    /**
     * Crea el repositorio de mascotas.
     *
     * @param apiClient cliente HTTP compartido
     */
    public MascotaRepository(ApiClient apiClient) {
        super(apiClient, "/mascotas", Mascota.class);
    }
}