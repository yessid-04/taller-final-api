package edu.poo.vetcare.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Centraliza las peticiones HTTP y la conversión entre JSON y objetos Java.
 * Es la única clase que conoce HTTP y JSON.
 */
public class ApiClient {

    private static final Duration TIEMPO_MAXIMO = Duration.ofSeconds(10);

    private final String urlBase;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    /**
     * Crea un cliente para la API indicada.
     *
     * @param urlBase dirección de la API, por ejemplo http://127.0.0.1:8000
     */
    public ApiClient(String urlBase) {
        this.urlBase = urlBase;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1) // Uvicorn no admite la mejora a HTTP/2
                .connectTimeout(TIEMPO_MAXIMO)
                .build();
        this.mapper = new ObjectMapper();
    }

    /** Ejecuta un GET y convierte la respuesta en una lista de objetos. */
    public <T> List<T> obtenerLista(String ruta, Class<T> tipo) {
        String json = enviar(peticion(ruta).GET().build());
        try {
            return mapper.readValue(json,
                    mapper.getTypeFactory().constructCollectionType(List.class, tipo));
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "La respuesta de la API no es válida: " + e.getOriginalMessage());
        }
    }

    /** Ejecuta un GET y convierte la respuesta en un objeto. */
    public <T> T obtener(String ruta, Class<T> tipo) {
        return convertir(enviar(peticion(ruta).GET().build()), tipo);
    }

    /** Ejecuta un POST con el objeto recibido como cuerpo JSON. */
    public <T> T crear(String ruta, T cuerpo, Class<T> tipo) {
        HttpRequest request = peticion(ruta)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(aJson(cuerpo)))
                .build();
        return convertir(enviar(request), tipo);
    }

    /** Ejecuta un PUT con el objeto recibido como cuerpo JSON. */
    public <T> T actualizar(String ruta, T cuerpo, Class<T> tipo) {
        HttpRequest request = peticion(ruta)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(aJson(cuerpo)))
                .build();
        return convertir(enviar(request), tipo);
    }

    /** Ejecuta un DELETE sobre la ruta indicada. */
    public void eliminar(String ruta) {
        enviar(peticion(ruta).DELETE().build());
    }

    private HttpRequest.Builder peticion(String ruta) {
        return HttpRequest.newBuilder()
                .uri(URI.create(urlBase + ruta))
                .timeout(TIEMPO_MAXIMO)
                .header("Accept", "application/json");
    }

    private String enviar(HttpRequest request) {
        try {
            HttpResponse<String> respuesta =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int codigo = respuesta.statusCode();
            if (codigo < 200 || codigo >= 300) {
                throw new ApiException(codigo, extraerDetalle(respuesta.body()));
            }
            return respuesta.body();
        } catch (IOException e) {
            throw new ApiException(0, "No fue posible conectarse con la API en " + urlBase);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "La petición fue interrumpida");
        }
    }

    /**
     * Obtiene el mensaje del campo detail. FastAPI lo envía como texto en los
     * errores propios y como arreglo en los errores de validación (422).
     */
    private String extraerDetalle(String cuerpo) {
        try {
            JsonNode detalle = mapper.readTree(cuerpo).path("detail");
            if (detalle.isTextual()) {
                return detalle.asText();
            }
            if (detalle.isArray() && !detalle.isEmpty()) {
                JsonNode error = detalle.get(0);
                JsonNode ubicacion = error.path("loc");
                String campo = ubicacion.isArray() && !ubicacion.isEmpty()
                        ? ubicacion.get(ubicacion.size() - 1).asText()
                        : "desconocido";
                return "Dato inválido en '" + campo + "': " + error.path("msg").asText();
            }
        } catch (JsonProcessingException e) {
            return "Respuesta de error no reconocida";
        }
        return "Error desconocido en la API";
    }

    private String aJson(Object objeto) {
        try {
            return mapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "No fue posible generar el JSON: " + e.getOriginalMessage());
        }
    }

    private <T> T convertir(String json, Class<T> tipo) {
        try {
            return mapper.readValue(json, tipo);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "La respuesta de la API no es válida: " + e.getOriginalMessage());
        }
    }
}