package edu.poo.vetcare.repository;

import edu.poo.vetcare.model.Cita;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Acceso al recurso /citas de la API.
 */
public class CitaRepository extends ApiCrudRepository<Cita> {

    /**
     * Crea el repositorio de citas.
     *
     * @param apiClient cliente HTTP compartido
     */
    public CitaRepository(ApiClient apiClient) {
        super(apiClient, "/citas", Cita.class);
    }

    /**
     * Consulta las citas que tienen un estado determinado.
     *
     * @param estado pendiente, atendida o cancelada
     * @return citas con ese estado
     */
    public List<Cita> listarPorEstado(String estado) {
        String valor = URLEncoder.encode(estado, StandardCharsets.UTF_8);
        return apiClient.obtenerLista(recurso + "?estado=" + valor + "&" + LIMITE, Cita.class);
    }
}
