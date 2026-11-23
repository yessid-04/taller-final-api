package edu.poo.vetcare.controller;

import edu.poo.vetcare.model.Cita;
import edu.poo.vetcare.model.Dueno;
import edu.poo.vetcare.model.Mascota;
import edu.poo.vetcare.repository.ApiException;
import edu.poo.vetcare.repository.CitaRepository;
import edu.poo.vetcare.repository.DuenoRepository;
import edu.poo.vetcare.repository.MascotaRepository;
import edu.poo.vetcare.view.ConsolaView;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Coordina las acciones del usuario, los repositorios y la vista.
 * No conoce HTTP ni JSON: solo usa los repositorios.
 */
public class VetCareController {

    private final DuenoRepository duenoRepository;
    private final MascotaRepository mascotaRepository;
    private final CitaRepository citaRepository;
    private final ConsolaView vista;

    /**
     * Crea el controlador recibiendo sus dependencias por el constructor.
     *
     * @param duenoRepository   acceso a los dueños
     * @param mascotaRepository acceso a las mascotas
     * @param citaRepository    acceso a las citas
     * @param vista             vista de consola
     */
    public VetCareController(DuenoRepository duenoRepository,
                             MascotaRepository mascotaRepository,
                             CitaRepository citaRepository,
                             ConsolaView vista) {
        this.duenoRepository = duenoRepository;
        this.mascotaRepository = mascotaRepository;
        this.citaRepository = citaRepository;
        this.vista = vista;
    }

    /** Ejecuta el menú principal hasta que el usuario decida salir. */
    public void iniciar() {
        int opcion;
        do {
            opcion = vista.leerMenuPrincipal();
            switch (opcion) {
                case 1 -> gestionar(vista::leerMenuDuenos, this::ejecutarDuenos);
                case 2 -> gestionar(vista::leerMenuMascotas, this::ejecutarMascotas);
                case 3 -> gestionar(vista::leerMenuCitas, this::ejecutarCitas);
                case 0 -> vista.mostrarMensaje("Hasta pronto.");
                default -> vista.mostrarError("Opción no válida.");
            }
        } while (opcion != 0);
    }

    /**
     * Repite un submenú hasta que el usuario elija volver (0). Cualquier error
     * de la API se muestra como mensaje y el submenú continúa.
     *
     * @param leerMenu qué submenú mostrar y leer
     * @param ejecutar qué hacer con la opción elegida
     */
    private void gestionar(IntSupplier leerMenu, IntConsumer ejecutar) {
        int opcion;
        do {
            opcion = leerMenu.getAsInt();
            try {
                if (opcion != 0) {
                    ejecutar.accept(opcion);
                }
            } catch (ApiException e) {
                vista.mostrarError(e.getMessage());
            }
        } while (opcion != 0);
    }

    // ---------- Dueños ----------

    private void ejecutarDuenos(int opcion) {
        switch (opcion) {
            case 1 -> registrarDueno();
            case 2 -> vista.mostrarDuenos(duenoRepository.listar());
            case 3 -> consultarDueno();
            case 4 -> reemplazarDueno();
            case 5 -> eliminarDueno();
            case 6 -> vista.mostrarMascotas(duenoRepository.listarMascotas(vista.leerId("dueño")));
            default -> vista.mostrarError("Opción no válida.");
        }
    }

    private void registrarDueno() {
        Dueno creado = duenoRepository.crear(vista.leerDueno());
        vista.mostrarMensaje("Dueño registrado: " + creado);
    }

    private void consultarDueno() {
        Dueno dueno = duenoRepository.buscarPorId(vista.leerId("dueño"));
        vista.mostrarMensaje("Dueño: " + dueno);
    }

    private void reemplazarDueno() {
        int id = vista.leerId("dueño");
        Dueno actualizado = duenoRepository.actualizar(id, vista.leerDueno());
        vista.mostrarMensaje("Dueño actualizado: " + actualizado);
    }

    private void eliminarDueno() {
        duenoRepository.eliminar(vista.leerId("dueño"));
        vista.mostrarMensaje("Dueño eliminado.");
    }

    // ---------- Mascotas ----------

    private void ejecutarMascotas(int opcion) {
        switch (opcion) {
            case 1 -> registrarMascota();
            case 2 -> vista.mostrarMascotas(mascotaRepository.listar());
            case 3 -> consultarMascota();
            case 4 -> reemplazarMascota();
            case 5 -> eliminarMascota();
            default -> vista.mostrarError("Opción no válida.");
        }
    }

    private void registrarMascota() {
        Mascota creada = mascotaRepository.crear(vista.leerMascota());
        vista.mostrarMensaje("Mascota registrada: " + creada);
    }

    private void consultarMascota() {
        Mascota mascota = mascotaRepository.buscarPorId(vista.leerId("mascota"));
        vista.mostrarMensaje("Mascota: " + mascota);
    }

    private void reemplazarMascota() {
        int id = vista.leerId("mascota");
        Mascota actualizada = mascotaRepository.actualizar(id, vista.leerMascota());
        vista.mostrarMensaje("Mascota actualizada: " + actualizada);
    }

    private void eliminarMascota() {
        mascotaRepository.eliminar(vista.leerId("mascota"));
        vista.mostrarMensaje("Mascota eliminada.");
    }

    // ---------- Citas ----------

    private void ejecutarCitas(int opcion) {
        switch (opcion) {
            case 1 -> registrarCita();
            case 2 -> vista.mostrarCitas(citaRepository.listar());
            case 3 -> consultarCita();
            case 4 -> reemplazarCita();
            case 5 -> eliminarCita();
            case 6 -> vista.mostrarCitas(citaRepository.listarPorEstado(vista.leerEstado()));
            default -> vista.mostrarError("Opción no válida.");
        }
    }

    private void registrarCita() {
        Cita creada = citaRepository.crear(vista.leerCita());
        vista.mostrarMensaje("Cita registrada: " + creada);
    }

    private void consultarCita() {
        Cita cita = citaRepository.buscarPorId(vista.leerId("cita"));
        vista.mostrarMensaje("Cita: " + cita);
    }

    private void reemplazarCita() {
        int id = vista.leerId("cita");
        Cita nueva = vista.leerCita();
        nueva.setEstado(vista.leerEstado());
        Cita actualizada = citaRepository.actualizar(id, nueva);
        vista.mostrarMensaje("Cita actualizada: " + actualizada);
    }

    private void eliminarCita() {
        citaRepository.eliminar(vista.leerId("cita"));
        vista.mostrarMensaje("Cita eliminada.");
    }
}