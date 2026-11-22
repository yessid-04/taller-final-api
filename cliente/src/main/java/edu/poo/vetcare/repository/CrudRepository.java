package edu.poo.vetcare.repository;

import java.util.List;

/**
 * Operaciones básicas disponibles para cualquier recurso de la API.
 *
 * @param <T> tipo del modelo que administra el repositorio
 */
public interface CrudRepository<T> {

    /**
     * Lista todos los registros del recurso.
     *
     * @return lista de registros
     */
    List<T> listar();

    /**
     * Consulta un registro por su identificador.
     *
     * @param id identificador del registro
     * @return el registro encontrado
     */
    T buscarPorId(int id);

    /**
     * Registra un nuevo elemento.
     *
     * @param entidad datos del elemento a crear
     * @return el elemento creado, con su identificador
     */
    T crear(T entidad);

    /**
     * Reemplaza todos los datos de un registro existente.
     *
     * @param id      identificador del registro
     * @param entidad nuevos datos
     * @return el registro actualizado
     */
    T actualizar(int id, T entidad);

    /**
     * Elimina un registro.
     *
     * @param id identificador del registro
     */
    void eliminar(int id);
}