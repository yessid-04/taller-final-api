package edu.poo.vetcare.view;

import edu.poo.vetcare.model.Cita;
import edu.poo.vetcare.model.Dueno;
import edu.poo.vetcare.model.Mascota;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

/**
 * Vista de consola: solo muestra información y lee datos del usuario.
 * Es la única clase que usa System.out y Scanner.
 */
public class ConsolaView {

    private final Scanner scanner;

    /** Crea la vista leyendo con la codificación de la consola (tildes y ñ). */
    public ConsolaView() {
        String codificacion = System.getProperty("stdin.encoding", Charset.defaultCharset().name());
        this.scanner = new Scanner(System.in, codificacion);
    }

    /**
     * Muestra el menú principal y lee la opción elegida.
     *
     * @return 1 dueños, 2 mascotas, 3 citas o 0 para salir
     */
    public int leerMenuPrincipal() {
        System.out.println("""

                ===== VETCARE =====
                1. Dueños
                2. Mascotas
                3. Citas
                0. Salir""");
        return leerEntero("Opción: ");
    }

    /**
     * Muestra el submenú de dueños, en orden CRUD, y lee la opción elegida.
     *
     * @return número de la opción elegida; 0 para volver
     */
    public int leerMenuDuenos() {
        System.out.println("""

                --- DUEÑOS ---
                1.  Registrar dueño
                2.  Listar dueños
                3.  Consultar un dueño por id
                4.  Reemplazar un dueño
                5.  Eliminar un dueño
                6.     Ver las mascotas de un dueño
                0. Volver""");
        return leerEntero("Opción: ");
    }

    /**
     * Muestra el submenú de mascotas, en orden CRUD, y lee la opción elegida.
     *
     * @return número de la opción elegida; 0 para volver
     */
    public int leerMenuMascotas() {
        System.out.println("""

                --- MASCOTAS ---
                1. [C] Registrar mascota
                2. [R] Listar mascotas
                3. [R] Consultar una mascota por id
                4. [U] Reemplazar una mascota
                5. [D] Eliminar una mascota
                0. Volver""");
        return leerEntero("Opción: ");
    }

    /**
     * Muestra el submenú de citas, en orden CRUD, y lee la opción elegida.
     *
     * @return número de la opción elegida; 0 para volver
     */
    public int leerMenuCitas() {
        System.out.println("""

                --- CITAS ---
                1. [C] Registrar cita
                2. [R] Listar citas
                3. [R] Consultar una cita por id
                4. [U] Reemplazar una cita
                5. [D] Eliminar una cita
                6.     Filtrar citas por estado
                0. Volver""");
        return leerEntero("Opción: ");
    }

    /**
     * Pide los datos de un dueño.
     *
     * @return dueño con los datos ingresados, sin id
     */
    public Dueno leerDueno() {
        String nombre = leerTexto("Nombre: ");
        String documento = leerTexto("Documento: ");
        String telefono = leerTexto("Teléfono: ");
        String email = leerTexto("Email: ");
        return new Dueno(nombre, documento, telefono, email);
    }

    /**
     * Pide los datos de una mascota.
     *
     * @return mascota con los datos ingresados, sin id
     */
    public Mascota leerMascota() {
        String nombre = leerTexto("Nombre: ");
        String especie = leerTexto("Especie: ");
        String raza = leerTexto("Raza: ");
        int edad = leerEntero("Edad en años (redondeada): ");
        double peso = leerDecimal("Peso en kg: ");
        int duenoId = leerEntero("Id del dueño: ");
        return new Mascota(nombre, especie, raza, edad, peso, duenoId);
    }

    /**
     * Pide los datos de una cita. El estado queda como pendiente.
     *
     * @return cita con los datos ingresados, sin id
     */
    public Cita leerCita() {
        String fecha = leerTexto("Fecha (AAAA-MM-DD): ");
        String motivo = leerTexto("Motivo: ");
        String veterinario = leerTexto("Veterinario: ");
        double costo = leerDecimal("Costo: ");
        int mascotaId = leerEntero("Id de la mascota: ");
        return new Cita(fecha, motivo, veterinario, costo, mascotaId);
    }

    /**
     * Pide el estado de una cita.
     *
     * @return texto ingresado (pendiente, atendida o cancelada)
     */
    public String leerEstado() {
        return leerTexto("Estado (pendiente, atendida o cancelada): ").toLowerCase();
    }

    /**
     * Pide el identificador de un registro.
     *
     * @param entidad nombre de lo que se busca, por ejemplo "dueño"
     * @return identificador ingresado
     */
    public int leerId(String entidad) {
        return leerEntero("Id del " + entidad + ": ");
    }

    /**
     * Muestra una lista de dueños.
     *
     * @param duenos dueños a mostrar
     */
    public void mostrarDuenos(List<Dueno> duenos) {
        mostrarLista(duenos, "No hay dueños registrados.");
    }

    /**
     * Muestra una lista de mascotas.
     *
     * @param mascotas mascotas a mostrar
     */
    public void mostrarMascotas(List<Mascota> mascotas) {
        mostrarLista(mascotas, "No hay mascotas registradas.");
    }

    /**
     * Muestra una lista de citas.
     *
     * @param citas citas a mostrar
     */
    public void mostrarCitas(List<Cita> citas) {
        mostrarLista(citas, "No hay citas registradas.");
    }

    /**
     * Muestra un mensaje informativo.
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Muestra un mensaje de error, sin trazas de excepción.
     *
     * @param mensaje descripción del error
     */
    public void mostrarError(String mensaje) {
        System.out.println("ERROR: " + mensaje);
    }

    private void mostrarLista(List<?> elementos, String mensajeVacio) {
        if (elementos.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }
        elementos.forEach(System.out::println);
    }

    private String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        return scanner.nextLine().trim();
    }

    private int leerEntero(String etiqueta) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(etiqueta));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero.");
            }
        }
    }

    private double leerDecimal(String etiqueta) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(etiqueta).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número (puede usar decimales).");
            }
        }
    }
}