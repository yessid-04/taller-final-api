package edu.poo.vetcare.repository;

import java.util.List;

/**
 * Implementación reutilizable del CRUD sobre un recurso REST.
 * Cada repositorio concreto solo indica su ruta y su modelo.
 *
 * @param <T> tipo del modelo que administra el repositorio
 */
public abstract class ApiCrudRepository<T> implements CrudRepository<T> {

    /** Cliente HTTP compartido. */
    protected final ApiClient apiClient;

    /** Ruta del recurso, por ejemplo /duenos. */
    protected final String recurso;

    /** Clase del modelo, necesaria para convertir el JSON. */
    protected final Class<T> tipo;

    /**
     * Crea un repositorio para un recurso de la API.
     *
     * @param apiClient cliente HTTP compartido
     * @param recurso   ruta del recurso, por ejemplo /duenos
     * @param tipo      clase del modelo
     */
    protected ApiCrudRepository(ApiClient apiClient, String recurso, Class<T> tipo) {
        this.apiClient = apiClient;
        this.recurso = recurso;
        this.tipo = tipo;
    }

    @Override
    public List<T> listar() {
        return apiClient.obtenerLista(recurso, tipo);
    }

    @Override
    public T buscarPorId(int id) {
        return apiClient.obtener(recurso + "/" + id, tipo);
    }

    @Override
    public T crear(T entidad) {
        return apiClient.crear(recurso, entidad, tipo);
    }

    @Override
    public T actualizar(int id, T entidad) {
        return apiClient.actualizar(recurso + "/" + id, entidad, tipo);
    }

    @Override
    public void eliminar(int id) {
        apiClient.eliminar(recurso + "/" + id);
    }
}