package edu.poo.vetcare;

import edu.poo.vetcare.controller.VetCareController;
import edu.poo.vetcare.repository.ApiClient;
import edu.poo.vetcare.repository.CitaRepository;
import edu.poo.vetcare.repository.DuenoRepository;
import edu.poo.vetcare.repository.MascotaRepository;
import edu.poo.vetcare.view.ConsolaView;

/**
 * Punto de entrada: crea los objetos y conecta sus dependencias.
 */
public class Main {

    private static final String URL_API = "http://127.0.0.1:8000";

    /**
     * Inicia el cliente de VetCare.
     *
     * @param args opcional: dirección de la API (por defecto http://127.0.0.1:8000)
     */
    public static void main(String[] args) {
        String url = args.length > 0 ? args[0] : URL_API;
        ApiClient apiClient = new ApiClient(url);
        VetCareController controlador = new VetCareController(
                new DuenoRepository(apiClient),
                new MascotaRepository(apiClient),
                new CitaRepository(apiClient),
                new ConsolaView());
        controlador.iniciar();
    }
}