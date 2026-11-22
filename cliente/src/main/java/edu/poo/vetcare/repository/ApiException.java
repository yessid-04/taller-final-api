package edu.poo.vetcare.repository;

/**
 * Error ocurrido al comunicarse con la API.
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int codigoEstado;

    /**
     * Crea una excepción con el código HTTP recibido y un mensaje comprensible.
     *
     * @param codigoEstado código HTTP recibido, o 0 si no hubo respuesta
     * @param mensaje      descripción del error
     */
    public ApiException(int codigoEstado, String mensaje) {
        super(mensaje);
        this.codigoEstado = codigoEstado;
    }

    /**
     * Devuelve el código HTTP asociado al error.
     *
     * @return código de estado, o 0 si no hubo respuesta
     */
    public int getCodigoEstado() {
        return codigoEstado;
    }
}