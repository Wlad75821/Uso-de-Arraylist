package parqueadero;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Punto de entrada de la aplicacion de parqueadero. */
public class Main {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        Path carpetaDatos = Path.of("data");
        try (Scanner sc = new Scanner(System.in)) {
            Caja caja = new Caja(carpetaDatos.resolve("caja.txt"));
            Parqueadero parqueadero = new Parqueadero(carpetaDatos.resolve("vehiculos.txt"), caja);
            int opcion;
            do {
                mostrarMenu();
                opcion = leerEntero(sc, "Seleccione una opcion: ");
                try {
                    switch (opcion) {
                        case 1 -> ingresarVehiculo(sc, parqueadero);
                        case 2 -> listarVehiculos(parqueadero);
                        case 3 -> registrarSalida(sc, parqueadero);
                        case 4 -> mostrarCaja(caja);
                        case 0 -> System.out.println("Gracias por utilizar el sistema.");
                        default -> System.out.println("Opcion no valida.");
                    }
                } catch (IOException e) {
                    System.out.println("No se pudo acceder a los archivos: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Dato incorrecto: " + e.getMessage());
                }
                System.out.println();
            } while (opcion != 0);
        } catch (IOException e) {
            System.out.println("No se pudo iniciar el sistema: " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println("==================================");
        System.out.println("       SISTEMA DE PARQUEADERO");
        System.out.println("==================================");
        System.out.println("1. Registrar ingreso de vehiculo");
        System.out.println("2. Ver vehiculos estacionados");
        System.out.println("3. Registrar salida y cobrar");
        System.out.println("4. Consultar caja");
        System.out.println("0. Salir");
    }

    private static void ingresarVehiculo(Scanner sc, Parqueadero p) throws IOException {
        System.out.print("Placa: ");
        String placa = sc.nextLine().trim();
        System.out.print("Marca del vehiculo: ");
        String marca = sc.nextLine().trim();
        if (p.registrarIngreso(placa, marca))
            System.out.println("Ingreso registrado correctamente.");
        else
            System.out.println("Ese vehiculo ya se encuentra estacionado.");
    }

    private static void listarVehiculos(Parqueadero p) {
        List<Vehiculo> lista = p.listarVehiculos();
        if (lista.isEmpty()) {
            System.out.println("No hay vehiculos estacionados.");
            return;
        }
        System.out.println("--- VEHICULOS ESTACIONADOS ---");
        for (Vehiculo v : lista) {
            System.out.printf("Placa: %s | Marca: %s | Ingreso: %s%n",
                    v.getPlaca(), v.getMarca(), v.getFechaHoraIngreso().format(FECHA));
        }
    }

    private static void registrarSalida(Scanner sc, Parqueadero p) throws IOException {
        System.out.print("Placa del vehiculo que sale: ");
        String placa = sc.nextLine().trim();
        Optional<Parqueadero.ResumenSalida> salida = p.registrarSalida(placa);
        if (salida.isEmpty()) {
            System.out.println("No se encontro un vehiculo estacionado con esa placa.");
            return;
        }
        Parqueadero.ResumenSalida r = salida.get();
        System.out.println("--- COMPROBANTE DE SALIDA ---");
        System.out.println("Placa: " + r.getVehiculo().getPlaca());
        System.out.println("Marca: " + r.getVehiculo().getMarca());
        System.out.println("Ingreso: " + r.getVehiculo().getFechaHoraIngreso().format(FECHA));
        System.out.println("Salida: " + r.getSalida().format(FECHA));
        System.out.println("Tiempo transcurrido: " + r.getMinutos() + " minuto(s)");
        System.out.println("Horas o fracciones cobradas: " + r.getHorasCobradas());
        System.out.printf("TOTAL A PAGAR: $%.2f%n", r.getValor());
    }

    private static void mostrarCaja(Caja caja) throws IOException {
        System.out.println("--- RESUMEN DE CAJA ---");
        System.out.println("Cantidad de cobros: " + caja.contarCobros());
        System.out.printf("Recaudacion acumulada: $%.2f%n", caja.obtenerRecaudacionTotal());
        System.out.println("Archivo de registro: " + caja.getArchivo().toAbsolutePath());
    }

    private static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.nextLine().trim();
            try { return Integer.parseInt(entrada); }
            catch (NumberFormatException e) { System.out.println("Ingrese un numero valido."); }
        }
    }
}
